package com.mcsoccer.item;

import com.mcsoccer.entity.SoccerBallEntity;
import com.mcsoccer.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
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

public class WhistleItem extends Item {

    private static final double FREEZE_RADIUS = 32.0;
    private static final double TELEPORT_RADIUS = 16.0;
    private static final int COOLDOWN_TICKS = 40; // 2 seconds

    public WhistleItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Vec3 playerPos = player.position();
            boolean isSneaking = player.isCrouching();

            // Find all balls within freeze radius
            AABB freezeBox = AABB.ofSize(playerPos, FREEZE_RADIUS * 2, FREEZE_RADIUS * 2, FREEZE_RADIUS * 2);
            List<SoccerBallEntity> balls = serverLevel.getEntitiesOfClass(
                    SoccerBallEntity.class, freezeBox,
                    b -> b.isAlive() && b.distanceToSqr(player) < FREEZE_RADIUS * FREEZE_RADIUS
            );

            if (isSneaking) {
                // Shift + use: Teleport nearest ball to player's feet or hand
                SoccerBallEntity nearest = null;
                double closestDist = TELEPORT_RADIUS * TELEPORT_RADIUS;

                for (SoccerBallEntity ball : balls) {
                    double dist = ball.distanceToSqr(player);
                    if (dist < closestDist) {
                        closestDist = dist;
                        nearest = ball;
                    }
                }

                if (nearest != null) {
                    // Teleport to player's feet (slightly in front)
                    Vec3 lookDir = player.getLookAngle();
                    Vec3 targetPos = playerPos.add(lookDir.x * 1.2, 0.5, lookDir.z * 1.2);
                    nearest.teleportTo(targetPos.x, targetPos.y, targetPos.z);
                    nearest.setDeltaMovement(Vec3.ZERO);

                    // Play whistle sound
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.WHISTLE.get(), SoundSource.PLAYERS, 1.5F, 1.0F);

                    player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
                    player.swing(hand, true);
                    return InteractionResult.SUCCESS;
                }
            } else {
                // Normal use: Freeze all nearby balls
                int frozenCount = 0;
                for (SoccerBallEntity ball : balls) {
                    ball.setDeltaMovement(Vec3.ZERO);
                    ball.setOnGround(true);
                    frozenCount++;
                }

                if (frozenCount > 0) {
                    // Play whistle sound
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.WHISTLE.get(), SoundSource.PLAYERS, 1.5F, 1.0F);

                    player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
                    player.swing(hand, true);
                    return InteractionResult.SUCCESS;
                } else {
                    // No balls found, still play sound but shorter cooldown
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.WHISTLE.get(), SoundSource.PLAYERS, 1.0F, 1.2F);
                    player.getCooldowns().addCooldown(stack, 20);
                    player.swing(hand, true);
                    return InteractionResult.FAIL;
                }
            }
        }

        if (level.isClientSide() && !player.getCooldowns().isOnCooldown(stack)) {
            player.swing(hand);
        }

        return InteractionResult.PASS;
    }
}
