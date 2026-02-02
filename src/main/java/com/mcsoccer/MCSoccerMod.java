package com.mcsoccer;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.mcsoccer.block.ModBlockEntities;
import com.mcsoccer.block.ModBlocks;
import com.mcsoccer.command.ModCommands;
import com.mcsoccer.data.ModAttachments;
import com.mcsoccer.data.PlayerSoccerData;
import com.mcsoccer.entity.ModEntities;
import com.mcsoccer.item.ModItems;
import com.mcsoccer.network.ModMessages;
import com.mcsoccer.sound.ModSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(MCSoccerMod.MOD_ID)
public class MCSoccerMod {
    public static final String MOD_ID = "mcsoccer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SOCCER_TAB =
            CREATIVE_TABS.register("soccer_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mcsoccer.soccer_tab"))
                    .icon(() -> new ItemStack(ModItems.SOCCER_BALL.get()))
                    .displayItems((params, output) -> {
                        // Equipment
                        output.accept(ModItems.SOCCER_BALL.get());
                        output.accept(ModItems.GOALKEEPER_GLOVES.get());
                        output.accept(ModItems.WHISTLE.get());
                        output.accept(ModItems.YELLOW_CARD.get());
                        output.accept(ModItems.RED_CARD.get());
                        output.accept(ModBlocks.GOAL_BLOCK_ITEM.get());

                        // Club jerseys
                        output.accept(ModItems.JERSEY_REAL_MADRID.get());
                        output.accept(ModItems.JERSEY_BARCELONA.get());
                        output.accept(ModItems.JERSEY_BAYERN.get());
                        output.accept(ModItems.JERSEY_PSG.get());
                        output.accept(ModItems.JERSEY_MAN_CITY.get());
                        output.accept(ModItems.JERSEY_LIVERPOOL.get());
                        output.accept(ModItems.JERSEY_JUVENTUS.get());
                        output.accept(ModItems.JERSEY_AC_MILAN.get());

                        // National jerseys
                        output.accept(ModItems.JERSEY_POLAND.get());
                        output.accept(ModItems.JERSEY_BRAZIL.get());
                        output.accept(ModItems.JERSEY_GERMANY.get());
                        output.accept(ModItems.JERSEY_ARGENTINA.get());
                        output.accept(ModItems.JERSEY_FRANCE.get());
                        output.accept(ModItems.JERSEY_ENGLAND.get());
                        output.accept(ModItems.JERSEY_SPAIN.get());
                        output.accept(ModItems.JERSEY_ITALY.get());
                    })
                    .build());

    public MCSoccerMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(ModMessages::registerPayloads);

        if (FMLEnvironment.getDist().isClient()) {
            modEventBus.addListener(com.mcsoccer.client.ClientEvents::registerRenderers);
            modEventBus.addListener(com.mcsoccer.client.ClientEvents::registerKeys);
        }

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("MC Soccer mod loaded!");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("MC Soccer: Server starting!");
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            PlayerSoccerData data = player.getData(ModAttachments.PLAYER_SOCCER_DATA);
            data.tick();
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
