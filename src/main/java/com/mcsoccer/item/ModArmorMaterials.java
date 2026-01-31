package com.mcsoccer.item;

import com.mcsoccer.MCSoccerMod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public class ModArmorMaterials {

    public static final ResourceKey<EquipmentAsset> JERSEY_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID,
                    ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, "jersey"));

    public static final ArmorMaterial JERSEY = new ArmorMaterial(
            5, // durability multiplier (like leather)
            Map.of(ArmorType.CHESTPLATE, 3), // defense: 3 for chestplate
            10, // enchantment value
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F, // toughness
            0.0F, // knockback resistance
            ItemTags.REPAIRS_LEATHER_ARMOR, // repaired with leather
            JERSEY_ASSET
    );
}
