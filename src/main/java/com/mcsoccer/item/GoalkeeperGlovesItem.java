package com.mcsoccer.item;

import com.mcsoccer.entity.SoccerBallEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GoalkeeperGlovesItem extends Item {

    private static final double SAVE_REACH = 3.75;
    private static final double SAVE_ANGLE_DEG = 95.0;
    private static final int SUCCESS_COOLDOWN = 30;
    private static final int MISS_COOLDOWN = 15;

    public GoalkeeperGlovesItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            // Lunge toward look direction
            Vec3 look = player.getLookAngle();
            player.push(look.x * 0.5, look.y * 0.5, look.z * 0.5);
            player.hurtMarked = true;

            // Find nearest ball in cone
            AABB box = player.getBoundingBox().inflate(SAVE_REACH + 1.0);
            List<SoccerBallEntity> balls = serverLevel.getEntitiesOfClass(
                    SoccerBallEntity.class, box,
                    b -> b.isAlive() && b.distanceToSqr(player) < SAVE_REACH * SAVE_REACH
            );

            SoccerBallEntity target = null;
            double closestDist = Double.MAX_VALUE;
            for (SoccerBallEntity ball : balls) {
                if (isBallInCone(player, ball)) {
                    double dist = player.distanceToSqr(ball);
                    if (dist < closestDist) {
                        closestDist = dist;
                        target = ball;
                    }
                }
            }

            if (target != null) {
                // Punch ball away
                target.setDeltaMovement(Vec3.ZERO);
                Vec3 punchDir = player.getLookAngle().normalize().scale(1.0).add(0, 0.3, 0);
                target.setDeltaMovement(punchDir);

                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F,
                        0.8F + player.getRandom().nextFloat() * 0.4F);
                serverLevel.sendParticles(ParticleTypes.CRIT,
                        target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ(),
                        10, 0.3, 0.3, 0.3, 0.1);

                player.getCooldowns().addCooldown(stack, SUCCESS_COOLDOWN);
                player.swing(hand, true);
                return InteractionResult.SUCCESS;
            } else {
                // Missed
                player.getCooldowns().addCooldown(stack, MISS_COOLDOWN);
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.7F,
                        1.5F + player.getRandom().nextFloat() * 0.3F);
                player.swing(hand, true);
                return InteractionResult.FAIL;
            }
        }

        if (level.isClientSide() && !player.getCooldowns().isOnCooldown(stack)) {
            player.swing(hand);
        }

        return InteractionResult.PASS;
    }

    private boolean isBallInCone(Player player, SoccerBallEntity ball) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 ballCenter = ball.position().add(0, ball.getBbHeight() / 2.0, 0);
        if (eyePos.distanceToSqr(ballCenter) > SAVE_REACH * SAVE_REACH) {
            return false;
        }
        Vec3 toBall = ballCenter.subtract(eyePos).normalize();
        Vec3 lookDir = player.getLookAngle().normalize();
        double dot = lookDir.dot(toBall);
        double minCos = Math.cos(Math.toRadians(SAVE_ANGLE_DEG / 2.0));
        return dot >= minCos;
    }
}
