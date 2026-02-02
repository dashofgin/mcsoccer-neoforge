package com.mcsoccer.client;

import com.mcsoccer.MCSoccerMod;
import com.mcsoccer.network.KickAction;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

/**
 * Handles triggering player animations for soccer actions.
 * Requires Player Animation Library (PAL) by ZigyTheBird to be installed.
 */
public class AnimationHandler {

    private static boolean palAvailable = false;
    private static boolean checkedPal = false;

    private static boolean isPalAvailable() {
        if (!checkedPal) {
            try {
                Class.forName("com.zigythebird.playeranim.api.PlayerAnimationAccess");
                palAvailable = true;
                MCSoccerMod.LOGGER.info("Player Animation Library (PAL) detected - animations enabled");
            } catch (ClassNotFoundException e) {
                palAvailable = false;
                MCSoccerMod.LOGGER.info("Player Animation Library (PAL) not found - animations disabled");
            }
            checkedPal = true;
        }
        return palAvailable;
    }

    public static void triggerAnimation(KickAction action) {
        if (!isPalAvailable()) return;

        String animationName = switch (action) {
            case SHORT_PASS -> "kick_short_pass";
            case LONG_PASS -> "kick_long_pass";
            case CURVE -> "kick_curve_shot";
            case KNUCKLEBALL -> "knuckleball";
            case STANDING_TACKLE -> "standing_tackle";
            case SLIDE_TACKLE -> "slide_tackle";
        };

        triggerPALAnimation(animationName);
    }

    private static void triggerPALAnimation(String animationName) {
        try {
            PALAnimationHelper.triggerAnimation(animationName);
        } catch (Exception e) {
            MCSoccerMod.LOGGER.warn("Failed to trigger PAL animation '{}': {}", animationName, e.getMessage());
        }
    }

    /**
     * Register the animation layer for PAL. Called during client setup.
     */
    public static void registerAnimationLayer() {
        if (!isPalAvailable()) return;
        try {
            PALAnimationHelper.registerAnimationLayer();
        } catch (Exception e) {
            MCSoccerMod.LOGGER.warn("Failed to register PAL animation layer: {}", e.getMessage());
        }
    }
}
