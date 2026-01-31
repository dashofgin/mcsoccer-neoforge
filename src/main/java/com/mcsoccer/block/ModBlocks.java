package com.mcsoccer.block;

import com.mcsoccer.MCSoccerMod;
import com.mcsoccer.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MCSoccerMod.MOD_ID);

    public static final DeferredBlock<Block> GOAL = BLOCKS.registerSimpleBlock("goal",
            BlockBehaviour.Properties.of()
                    .strength(1.5f)
                    .sound(SoundType.METAL)
                    .noOcclusion());

    // Block item registered via ModItems
    public static final DeferredItem<BlockItem> GOAL_BLOCK_ITEM = ModItems.ITEMS.registerSimpleBlockItem("goal", GOAL);
}
