package com.mcsoccer.data;

import com.mcsoccer.MCSoccerMod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MCSoccerMod.MOD_ID);

    public static final Supplier<AttachmentType<PlayerSoccerData>> PLAYER_SOCCER_DATA =
            ATTACHMENT_TYPES.register("player_soccer_data",
                    () -> AttachmentType.builder(PlayerSoccerData::new).build());
}
