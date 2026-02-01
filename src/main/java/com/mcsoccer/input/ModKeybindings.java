package com.mcsoccer.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.mcsoccer.MCSoccerMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class ModKeybindings {
    public static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, "mcsoccer"));

    public static final KeyMapping LONG_PASS = new KeyMapping(
            "key.mcsoccer.long_pass", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping SHORT_PASS = new KeyMapping(
            "key.mcsoccer.short_pass", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY);
    public static final KeyMapping CURVE_SHOT = new KeyMapping(
            "key.mcsoccer.curve_shot", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping KNUCKLEBALL = new KeyMapping(
            "key.mcsoccer.knuckleball", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping STANDING_TACKLE = new KeyMapping(
            "key.mcsoccer.standing_tackle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY);
    public static final KeyMapping SLIDE_TACKLE = new KeyMapping(
            "key.mcsoccer.slide_tackle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
}
