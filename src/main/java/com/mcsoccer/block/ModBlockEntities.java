package com.mcsoccer.block;

import com.mcsoccer.MCSoccerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MCSoccerMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GoalBlockEntity>> GOAL =
            BLOCK_ENTITY_TYPES.register("goal", () ->
                    new BlockEntityType<>(GoalBlockEntity::new, ModBlocks.GOAL.get())
            );
}
