package com.mcsoccer;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.mcsoccer.block.ModBlockEntities;
import com.mcsoccer.block.ModBlocks;
import com.mcsoccer.entity.ModEntities;
import com.mcsoccer.item.ModItems;
import com.mcsoccer.sound.ModSounds;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(MCSoccerMod.MOD_ID)
public class MCSoccerMod {
    public static final String MOD_ID = "mcsoccer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MCSoccerMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("MC Soccer mod loaded!");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SOCCER_BALL);
            event.accept(ModBlocks.GOAL_BLOCK_ITEM);
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            // Club jerseys
            event.accept(ModItems.JERSEY_REAL_MADRID);
            event.accept(ModItems.JERSEY_BARCELONA);
            event.accept(ModItems.JERSEY_BAYERN);
            event.accept(ModItems.JERSEY_PSG);
            event.accept(ModItems.JERSEY_MAN_CITY);
            event.accept(ModItems.JERSEY_LIVERPOOL);
            event.accept(ModItems.JERSEY_JUVENTUS);
            event.accept(ModItems.JERSEY_AC_MILAN);

            // National team jerseys
            event.accept(ModItems.JERSEY_POLAND);
            event.accept(ModItems.JERSEY_BRAZIL);
            event.accept(ModItems.JERSEY_GERMANY);
            event.accept(ModItems.JERSEY_ARGENTINA);
            event.accept(ModItems.JERSEY_FRANCE);
            event.accept(ModItems.JERSEY_ENGLAND);
            event.accept(ModItems.JERSEY_SPAIN);
            event.accept(ModItems.JERSEY_ITALY);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("MC Soccer: Server starting!");
    }
}
