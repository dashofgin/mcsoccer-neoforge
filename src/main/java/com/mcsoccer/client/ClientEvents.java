package com.mcsoccer.client;

import com.mcsoccer.MCSoccerMod;
import com.mcsoccer.client.renderer.SoccerBallRenderer;
import com.mcsoccer.entity.ModEntities;
import com.mcsoccer.input.ModKeybindings;
import com.mcsoccer.network.KickAction;
import com.mcsoccer.network.KickActionPayload;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = MCSoccerMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        checkAndSend(ModKeybindings.LONG_PASS, KickAction.LONG_PASS);
        checkAndSend(ModKeybindings.SHORT_PASS, KickAction.SHORT_PASS);
        checkAndSend(ModKeybindings.CURVE_SHOT, KickAction.CURVE);
        checkAndSend(ModKeybindings.KNUCKLEBALL, KickAction.KNUCKLEBALL);
        checkAndSend(ModKeybindings.STANDING_TACKLE, KickAction.STANDING_TACKLE);
        checkAndSend(ModKeybindings.SLIDE_TACKLE, KickAction.SLIDE_TACKLE);
    }

    private static void checkAndSend(KeyMapping key, KickAction action) {
        while (key.consumeClick()) {
            ClientPacketDistributor.sendToServer(new KickActionPayload(action));
        }
    }

    // Called from MCSoccerMod via modEventBus.addListener
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SOCCER_BALL.get(), context -> new SoccerBallRenderer(context));
    }

    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.registerCategory(ModKeybindings.CATEGORY);
        event.register(ModKeybindings.LONG_PASS);
        event.register(ModKeybindings.SHORT_PASS);
        event.register(ModKeybindings.CURVE_SHOT);
        event.register(ModKeybindings.KNUCKLEBALL);
        event.register(ModKeybindings.STANDING_TACKLE);
        event.register(ModKeybindings.SLIDE_TACKLE);
    }
}
