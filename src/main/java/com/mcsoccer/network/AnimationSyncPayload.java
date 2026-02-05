package com.mcsoccer.network;

import com.mcsoccer.MCSoccerMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Server-to-client payload to sync player animations to all nearby players.
 */
public record AnimationSyncPayload(int playerId, String animationName) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AnimationSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, "animation_sync"));

    public static final StreamCodec<ByteBuf, AnimationSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, AnimationSyncPayload::playerId,
                    ByteBufCodecs.STRING_UTF8, AnimationSyncPayload::animationName,
                    AnimationSyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
