package com.mcsoccer.client;

import com.mcsoccer.MCSoccerMod;
import com.mcsoccer.network.KickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

/**
 * Handles triggering player animations for soccer actions.
 * Requires Player Animation Library (PAL) to be installed.
 */
public class AnimationHandler {

    /**
     * Triggers the appropriate player animation based on the kick action.
     * Animation will only play if PAL is installed.
     *
     * @param action The kick action to animate
     */
    public static void triggerAnimation(KickAction action) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        // Get animation name based on action
        String animationName = switch (action) {
            case SHORT_PASS -> "kick_short_pass";
            case LONG_PASS -> "kick_long_pass";
            case CURVE -> "kick_curve_shot";
            case KNUCKLEBALL -> "knuckleball";
            case STANDING_TACKLE -> "standing_tackle";
            case SLIDE_TACKLE -> "slide_tackle";
        };

        // Trigger animation via PAL (if installed)
        triggerPALAnimation(player, animationName);
    }

    /**
     * Triggers a PAL animation. Uses reflection to avoid hard dependency on PAL.
     * If PAL is not installed, this silently does nothing.
     */
    private static void triggerPALAnimation(LocalPlayer player, String animationName) {
        try {
            // Use reflection to call PAL API if available
            // PlayerAnimationAccess.getPlayerAssociatedData(player)
            //     .get(ANIMATION_LAYER)
            //     .setAnimation(new ResourceLocation("mcsoccer", animationName));

            Class<?> palClass = Class.forName("dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess");
            Class<?> factoryClass = Class.forName("dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory");

            // Get the animation container for the player
            Object animContainer = palClass
                .getMethod("getPlayerAssociatedData", net.minecraft.world.entity.player.Player.class)
                .invoke(null, player);

            if (animContainer == null) {
                MCSoccerMod.LOGGER.debug("PAL animation container not found for player");
                return;
            }

            // Get animation layer
            Object factoryField = factoryClass.getField("ANIMATION_DATA_FACTORY").get(null);
            Object animLayer = animContainer.getClass()
                .getMethod("get", Object.class)
                .invoke(animContainer, factoryField);

            if (animLayer == null) {
                MCSoccerMod.LOGGER.debug("PAL animation layer not initialized");
                return;
            }

            // Trigger the animation
            ResourceLocation animLocation = ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, animationName);
            animLayer.getClass()
                .getMethod("setAnimation", ResourceLocation.class)
                .invoke(animLayer, animLocation);

            MCSoccerMod.LOGGER.debug("Triggered PAL animation: {}", animationName);

        } catch (ClassNotFoundException e) {
            // PAL not installed - silently ignore
            MCSoccerMod.LOGGER.debug("Player Animation Library not found - animations disabled");
        } catch (Exception e) {
            MCSoccerMod.LOGGER.warn("Failed to trigger PAL animation: {}", e.getMessage());
        }
    }
}
