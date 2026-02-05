package com.mcsoccer.network;

import com.mcsoccer.data.ModAttachments;
import com.mcsoccer.data.PlayerSoccerData;
import com.mcsoccer.entity.SoccerBallEntity;
import com.mcsoccer.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Comparator;
import java.util.List;

public class ModMessages {

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("mcsoccer");
        registrar.playToServer(
                KickActionPayload.TYPE,
                KickActionPayload.STREAM_CODEC,
                ModMessages::handleKickAction
        );
        registrar.playToClient(
                AnimationSyncPayload.TYPE,
                AnimationSyncPayload.STREAM_CODEC,
                ModMessages::handleAnimationSync
        );
    }

    private static void handleAnimationSync(AnimationSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // This runs on client - trigger animation on the target player
            Player localPlayer = context.player();
            if (localPlayer == null) return;

            Entity entity = localPlayer.level().getEntity(payload.playerId());
            if (entity instanceof Player targetPlayer) {
                // Call client-side animation handler
                com.mcsoccer.client.AnimationHandler.triggerAnimationOnPlayer(targetPlayer, payload.animationName());
            }
        });
    }

    private static void handleKickAction(KickActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            PlayerSoccerData data = player.getData(ModAttachments.PLAYER_SOCCER_DATA);
            if (!data.canTouchBall()) return;

            KickAction action = payload.action();

            // Broadcast animation to all nearby players
            broadcastAnimation(player, action);

            switch (action) {
                case LONG_PASS, SHORT_PASS, CURVE, KNUCKLEBALL -> handleShot(player, action);
                case STANDING_TACKLE -> handleStandingTackle(player);
                case SLIDE_TACKLE -> handleSlideTackle(player);
            }
        });
    }

    private static void broadcastAnimation(ServerPlayer player, KickAction action) {
        String animationName = switch (action) {
            case SHORT_PASS -> "kick_short_pass";
            case LONG_PASS -> "kick_long_pass";
            case CURVE -> "kick_curve_shot";
            case KNUCKLEBALL -> "knuckleball";
            case STANDING_TACKLE -> "standing_tackle";
            case SLIDE_TACKLE -> "slide_tackle";
        };

        AnimationSyncPayload payload = new AnimationSyncPayload(player.getId(), animationName);

        // Send to all players tracking this player (within render distance)
        if (player.level() instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, payload);
        }
    }

    private static void handleShot(ServerPlayer player, KickAction action) {
        SoccerBallEntity ball = findNearestBall(player, 4.0);
        if (ball == null) return;

        ball.applyKick(player, action);
        blockNearbyOpponents(player, ball, 2.0, 10);

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.BALL_KICK.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static void handleStandingTackle(ServerPlayer player) {
        SoccerBallEntity ball = findNearestBall(player, 4.0);
        if (ball == null) return;

        Vec3 look = player.getLookAngle();
        double speed = player.getDeltaMovement().horizontalDistance();
        double power = 0.7 + speed * 0.25;
        ball.setDeltaMovement(look.x * power, 0.3, look.z * power);
        ball.setLastKicker(player);

        // Knockback nearby opponents
        double yaw = Math.toRadians(player.getYRot());
        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);
        List<Player> nearby = player.level().getEntitiesOfClass(Player.class,
                player.getBoundingBox().inflate(1.5), p -> p != player);
        for (Player other : nearby) {
            Vec3 rel = other.position().subtract(player.position());
            double dot = rel.x * forwardX + rel.z * forwardZ;
            if (dot > 0) {
                other.push(forwardX * 0.4, 0.15, forwardZ * 0.4);
                other.hurtMarked = true;
            }
        }

        blockNearbyOpponents(player, ball, 2.0, 15);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static void handleSlideTackle(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        player.push(look.x * 0.8, 0.05, look.z * 0.8);
        player.hurtMarked = true;

        SoccerBallEntity ball = findNearestBall(player, 4.5);
        if (ball != null) {
            double speed = player.getDeltaMovement().horizontalDistance();
            double power = 0.9 + speed * 0.3;
            ball.setDeltaMovement(look.x * power, 0.15, look.z * power);
            ball.setLastKicker(player);
        }

        double yaw = Math.toRadians(player.getYRot());
        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);
        List<Player> nearby = player.level().getEntitiesOfClass(Player.class,
                player.getBoundingBox().inflate(2.0), p -> p != player);
        for (Player other : nearby) {
            Vec3 rel = other.position().subtract(player.position());
            double dot = rel.x * forwardX + rel.z * forwardZ;
            if (dot > 0 && player.distanceToSqr(other) < 4.0) {
                other.push(forwardX * 0.6, 0.2, forwardZ * 0.6);
                other.hurtMarked = true;
            }
        }

        blockNearbyOpponents(player, ball, 2.5, 20);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.7F, 0.8F);
    }

    private static SoccerBallEntity findNearestBall(Player player, double range) {
        Vec3 pos = player.position();
        List<SoccerBallEntity> balls = player.level().getEntitiesOfClass(
                SoccerBallEntity.class,
                new AABB(pos, pos).inflate(range),
                Entity::isAlive
        );
        return balls.stream()
                .min(Comparator.comparingDouble(b -> b.distanceToSqr(player)))
                .orElse(null);
    }

    private static void blockNearbyOpponents(Player kicker, SoccerBallEntity ball, double range, int ticks) {
        if (ball == null) return;
        Vec3 ballPos = ball.position();
        List<Player> nearby = kicker.level().getEntitiesOfClass(Player.class,
                new AABB(ballPos, ballPos).inflate(range), p -> p != kicker);
        for (Player other : nearby) {
            PlayerSoccerData data = other.getData(ModAttachments.PLAYER_SOCCER_DATA);
            data.blockTouch(ticks);
        }
    }
}
