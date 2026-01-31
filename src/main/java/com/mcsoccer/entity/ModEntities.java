package com.mcsoccer.entity;

import com.mcsoccer.MCSoccerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MCSoccerMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<SoccerBallEntity>> SOCCER_BALL =
            ENTITY_TYPES.register("soccer_ball", (id) ->
                    EntityType.Builder.<SoccerBallEntity>of(SoccerBallEntity::new, MobCategory.MISC)
                            .sized(0.35F, 0.35F)
                            .clientTrackingRange(64)
                            .updateInterval(2)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, id))
            );
}
