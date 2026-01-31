package com.mcsoccer.sound;

import com.mcsoccer.MCSoccerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Custom sounds registry. Currently uses placeholder registrations.
 * To add real sounds:
 * 1. Place .ogg files in assets/mcsoccer/sounds/
 * 2. Create assets/mcsoccer/sounds.json referencing them
 * 3. Uncomment and use the sound events below
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, MCSoccerMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BALL_KICK =
            SOUND_EVENTS.register("ball_kick", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, "ball_kick")));

    public static final DeferredHolder<SoundEvent, SoundEvent> GOAL_SCORED =
            SOUND_EVENTS.register("goal_scored", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, "goal_scored")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WHISTLE =
            SOUND_EVENTS.register("whistle", () ->
                    SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, "whistle")));
}
