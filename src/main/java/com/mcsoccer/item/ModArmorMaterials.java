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

    // Each jersey gets its own equipment asset for unique rendering
    public static final ResourceKey<EquipmentAsset> JERSEY_REAL_MADRID_ASSET = jerseyAsset("jersey_real_madrid");
    public static final ResourceKey<EquipmentAsset> JERSEY_BARCELONA_ASSET = jerseyAsset("jersey_barcelona");
    public static final ResourceKey<EquipmentAsset> JERSEY_BAYERN_ASSET = jerseyAsset("jersey_bayern");
    public static final ResourceKey<EquipmentAsset> JERSEY_PSG_ASSET = jerseyAsset("jersey_psg");
    public static final ResourceKey<EquipmentAsset> JERSEY_MAN_CITY_ASSET = jerseyAsset("jersey_man_city");
    public static final ResourceKey<EquipmentAsset> JERSEY_LIVERPOOL_ASSET = jerseyAsset("jersey_liverpool");
    public static final ResourceKey<EquipmentAsset> JERSEY_JUVENTUS_ASSET = jerseyAsset("jersey_juventus");
    public static final ResourceKey<EquipmentAsset> JERSEY_AC_MILAN_ASSET = jerseyAsset("jersey_ac_milan");
    public static final ResourceKey<EquipmentAsset> JERSEY_POLAND_ASSET = jerseyAsset("jersey_poland");
    public static final ResourceKey<EquipmentAsset> JERSEY_BRAZIL_ASSET = jerseyAsset("jersey_brazil");
    public static final ResourceKey<EquipmentAsset> JERSEY_GERMANY_ASSET = jerseyAsset("jersey_germany");
    public static final ResourceKey<EquipmentAsset> JERSEY_ARGENTINA_ASSET = jerseyAsset("jersey_argentina");
    public static final ResourceKey<EquipmentAsset> JERSEY_FRANCE_ASSET = jerseyAsset("jersey_france");
    public static final ResourceKey<EquipmentAsset> JERSEY_ENGLAND_ASSET = jerseyAsset("jersey_england");
    public static final ResourceKey<EquipmentAsset> JERSEY_SPAIN_ASSET = jerseyAsset("jersey_spain");
    public static final ResourceKey<EquipmentAsset> JERSEY_ITALY_ASSET = jerseyAsset("jersey_italy");
    public static final ArmorMaterial JERSEY_REAL_MADRID = jerseyMaterial(JERSEY_REAL_MADRID_ASSET);
    public static final ArmorMaterial JERSEY_BARCELONA = jerseyMaterial(JERSEY_BARCELONA_ASSET);
    public static final ArmorMaterial JERSEY_BAYERN = jerseyMaterial(JERSEY_BAYERN_ASSET);
    public static final ArmorMaterial JERSEY_PSG = jerseyMaterial(JERSEY_PSG_ASSET);
    public static final ArmorMaterial JERSEY_MAN_CITY = jerseyMaterial(JERSEY_MAN_CITY_ASSET);
    public static final ArmorMaterial JERSEY_LIVERPOOL = jerseyMaterial(JERSEY_LIVERPOOL_ASSET);
    public static final ArmorMaterial JERSEY_JUVENTUS = jerseyMaterial(JERSEY_JUVENTUS_ASSET);
    public static final ArmorMaterial JERSEY_AC_MILAN = jerseyMaterial(JERSEY_AC_MILAN_ASSET);
    public static final ArmorMaterial JERSEY_POLAND = jerseyMaterial(JERSEY_POLAND_ASSET);
    public static final ArmorMaterial JERSEY_BRAZIL = jerseyMaterial(JERSEY_BRAZIL_ASSET);
    public static final ArmorMaterial JERSEY_GERMANY = jerseyMaterial(JERSEY_GERMANY_ASSET);
    public static final ArmorMaterial JERSEY_ARGENTINA = jerseyMaterial(JERSEY_ARGENTINA_ASSET);
    public static final ArmorMaterial JERSEY_FRANCE = jerseyMaterial(JERSEY_FRANCE_ASSET);
    public static final ArmorMaterial JERSEY_ENGLAND = jerseyMaterial(JERSEY_ENGLAND_ASSET);
    public static final ArmorMaterial JERSEY_SPAIN = jerseyMaterial(JERSEY_SPAIN_ASSET);
    public static final ArmorMaterial JERSEY_ITALY = jerseyMaterial(JERSEY_ITALY_ASSET);

    private static ResourceKey<EquipmentAsset> jerseyAsset(String name) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID,
                ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, name));
    }

    private static ArmorMaterial jerseyMaterial(ResourceKey<EquipmentAsset> asset) {
        return new ArmorMaterial(
                5,
                Map.of(ArmorType.CHESTPLATE, 3),
                10,
                SoundEvents.ARMOR_EQUIP_LEATHER,
                0.0F,
                0.0F,
                ItemTags.REPAIRS_LEATHER_ARMOR,
                asset
        );
    }
}
