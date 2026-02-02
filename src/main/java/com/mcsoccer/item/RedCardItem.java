package com.mcsoccer.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class RedCardItem extends Item {

    private static final int COOLDOWN_TICKS = 60; // 3 seconds

    public RedCardItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            // Show red card (display message and sound)
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.5F, 1.5F);

            // Display ejection message
            player.displayClientMessage(
                    Component.literal("🔴 Red Card - Ejected!").withStyle(style -> style.withColor(0xDD1111)),
                    true // Action bar
            );

            player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
            player.swing(hand, true);
            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide() && !player.getCooldowns().isOnCooldown(stack)) {
            player.swing(hand);
        }

        return InteractionResult.PASS;
    }
}
