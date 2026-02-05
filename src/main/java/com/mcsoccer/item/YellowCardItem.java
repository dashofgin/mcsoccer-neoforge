package com.mcsoccer.item;

import net.minecraft.network.chat.Component;
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
import java.util.Optional;

public class YellowCardItem extends Item {

    private static final int COOLDOWN_TICKS = 60; // 3 seconds
    private static final double TARGET_RANGE = 16.0;
    private static final double BROADCAST_RANGE = 32.0;

    public YellowCardItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            // Find player the referee is looking at
            Optional<Player> targetOpt = findTargetPlayer(player, serverLevel);

            // Play sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.5F, 0.8F);

            String refereeName = player.getName().getString();

            if (targetOpt.isPresent()) {
                Player target = targetOpt.get();
                String targetName = target.getName().getString();

                // Message to broadcast
                Component message = Component.literal("⚠ ")
                        .append(Component.literal(refereeName).withStyle(s -> s.withColor(0xAAAAAA)))
                        .append(Component.literal(" gives YELLOW CARD to ").withStyle(s -> s.withColor(0xFFDD00)))
                        .append(Component.literal(targetName).withStyle(s -> s.withColor(0xFFFFFF)))
                        .append(Component.literal("!").withStyle(s -> s.withColor(0xFFDD00)));

                // Broadcast to all nearby players
                broadcastMessage(serverLevel, player.position(), message, BROADCAST_RANGE);

                // Also show on action bar for target
                target.displayClientMessage(
                        Component.literal("⚠ You received a YELLOW CARD!").withStyle(s -> s.withColor(0xFFDD00)),
                        true
                );
            } else {
                // No target - just show card in the air
                Component message = Component.literal("⚠ ")
                        .append(Component.literal(refereeName).withStyle(s -> s.withColor(0xAAAAAA)))
                        .append(Component.literal(" shows YELLOW CARD!").withStyle(s -> s.withColor(0xFFDD00)));

                broadcastMessage(serverLevel, player.position(), message, BROADCAST_RANGE);
            }

            player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
            player.swing(hand, true);
            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide() && !player.getCooldowns().isOnCooldown(stack)) {
            player.swing(hand);
        }

        return InteractionResult.PASS;
    }

    private Optional<Player> findTargetPlayer(Player referee, ServerLevel level) {
        Vec3 eyePos = referee.getEyePosition();
        Vec3 lookDir = referee.getLookAngle();
        Vec3 endPos = eyePos.add(lookDir.scale(TARGET_RANGE));

        // Get all players in range
        AABB searchBox = new AABB(eyePos, endPos).inflate(2.0);
        List<Player> players = level.getEntitiesOfClass(Player.class, searchBox, p -> p != referee);

        Player closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Player p : players) {
            // Check if player is roughly in the direction we're looking
            Vec3 toPlayer = p.position().add(0, p.getEyeHeight() / 2, 0).subtract(eyePos);
            double dist = toPlayer.length();

            if (dist > TARGET_RANGE) continue;

            // Normalize and check dot product (how aligned are we)
            Vec3 toPlayerNorm = toPlayer.normalize();
            double dot = lookDir.dot(toPlayerNorm);

            // Must be looking roughly at player (within ~30 degrees)
            if (dot > 0.85 && dist < closestDist) {
                closestDist = dist;
                closest = p;
            }
        }

        return Optional.ofNullable(closest);
    }

    private void broadcastMessage(ServerLevel level, Vec3 pos, Component message, double range) {
        AABB box = AABB.ofSize(pos, range * 2, range * 2, range * 2);
        List<Player> nearby = level.getEntitiesOfClass(Player.class, box,
                p -> p.distanceToSqr(pos.x, pos.y, pos.z) < range * range);

        for (Player p : nearby) {
            p.displayClientMessage(message, false);
        }
    }
}
