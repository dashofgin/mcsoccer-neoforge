package com.mcsoccer.network;

import com.mcsoccer.MCSoccerMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record KickActionPayload(int actionOrdinal) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<KickActionPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, "kick_action"));

    public static final StreamCodec<ByteBuf, KickActionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, KickActionPayload::actionOrdinal,
                    KickActionPayload::new
            );

    public KickActionPayload(KickAction action) {
        this(action.ordinal());
    }

    public KickAction action() {
        KickAction[] values = KickAction.values();
        if (actionOrdinal >= 0 && actionOrdinal < values.length) {
            return values[actionOrdinal];
        }
        return KickAction.LONG_PASS;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
