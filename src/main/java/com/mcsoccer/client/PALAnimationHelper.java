package com.mcsoccer.client;

import com.mcsoccer.MCSoccerMod;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Direct PAL API calls - isolated so AnimationHandler can check for PAL availability
 * before loading this class.
 */
public class PALAnimationHelper {

    private static final ResourceLocation ANIM_LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, "soccer_animations");

    public static void registerAnimationLayer() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                ANIM_LAYER_ID,
                42,
                avatar -> new PlayerAnimationController(avatar, (controller, data, setter) -> com.zigythebird.playeranimcore.enums.PlayState.STOP)
        );
        MCSoccerMod.LOGGER.info("Registered PAL animation layer for MC Soccer");
    }

    public static void triggerAnimation(String animationName) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ResourceLocation animId = ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, animationName);

        IAnimation layer = PlayerAnimationAccess.getPlayerAnimationLayer(player, ANIM_LAYER_ID);
        if (layer instanceof PlayerAnimationController controller) {
            boolean success = controller.triggerAnimation(animId);
            if (success) {
                MCSoccerMod.LOGGER.debug("Triggered PAL animation: {}", animationName);
            } else {
                MCSoccerMod.LOGGER.warn("PAL animation not found: {}", animId);
            }
        } else {
            MCSoccerMod.LOGGER.debug("PAL animation layer not available for player");
        }
    }

    /**
     * Trigger animation on any player (not just local).
     * Used for network sync to show animations to all nearby players.
     */
    public static void triggerAnimationOnPlayer(Player player, String animationName) {
        ResourceLocation animId = ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, animationName);

        IAnimation layer = PlayerAnimationAccess.getPlayerAnimationLayer(player, ANIM_LAYER_ID);
        if (layer instanceof PlayerAnimationController controller) {
            boolean success = controller.triggerAnimation(animId);
            if (success) {
                MCSoccerMod.LOGGER.debug("Triggered PAL animation '{}' on player {}", animationName, player.getName().getString());
            } else {
                MCSoccerMod.LOGGER.warn("PAL animation not found: {}", animId);
            }
        } else {
            MCSoccerMod.LOGGER.debug("PAL animation layer not available for player {}", player.getName().getString());
        }
    }
}
