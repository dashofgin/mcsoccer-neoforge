package com.mcsoccer.item;

import com.mcsoccer.MCSoccerMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MCSoccerMod.MOD_ID);

    // ==================== BALL ====================

    @SuppressWarnings("removal")
    public static final DeferredItem<Item> SOCCER_BALL = ITEMS.registerItem("soccer_ball",
            SoccerBallItem::new, new Item.Properties().stacksTo(16));

    // ==================== GOALKEEPER GLOVES ====================

    public static final DeferredItem<Item> GOALKEEPER_GLOVES = ITEMS.registerItem("goalkeeper_gloves",
            GoalkeeperGlovesItem::new);

    // ==================== CLUB JERSEYS ====================

    public static final DeferredItem<Item> JERSEY_REAL_MADRID = registerJersey("jersey_real_madrid", ModArmorMaterials.JERSEY_REAL_MADRID);
    public static final DeferredItem<Item> JERSEY_BARCELONA = registerJersey("jersey_barcelona", ModArmorMaterials.JERSEY_BARCELONA);
    public static final DeferredItem<Item> JERSEY_BAYERN = registerJersey("jersey_bayern", ModArmorMaterials.JERSEY_BAYERN);
    public static final DeferredItem<Item> JERSEY_PSG = registerJersey("jersey_psg", ModArmorMaterials.JERSEY_PSG);
    public static final DeferredItem<Item> JERSEY_MAN_CITY = registerJersey("jersey_man_city", ModArmorMaterials.JERSEY_MAN_CITY);
    public static final DeferredItem<Item> JERSEY_LIVERPOOL = registerJersey("jersey_liverpool", ModArmorMaterials.JERSEY_LIVERPOOL);
    public static final DeferredItem<Item> JERSEY_JUVENTUS = registerJersey("jersey_juventus", ModArmorMaterials.JERSEY_JUVENTUS);
    public static final DeferredItem<Item> JERSEY_AC_MILAN = registerJersey("jersey_ac_milan", ModArmorMaterials.JERSEY_AC_MILAN);

    // ==================== NATIONAL TEAM JERSEYS ====================

    public static final DeferredItem<Item> JERSEY_POLAND = registerJersey("jersey_poland", ModArmorMaterials.JERSEY_POLAND);
    public static final DeferredItem<Item> JERSEY_BRAZIL = registerJersey("jersey_brazil", ModArmorMaterials.JERSEY_BRAZIL);
    public static final DeferredItem<Item> JERSEY_GERMANY = registerJersey("jersey_germany", ModArmorMaterials.JERSEY_GERMANY);
    public static final DeferredItem<Item> JERSEY_ARGENTINA = registerJersey("jersey_argentina", ModArmorMaterials.JERSEY_ARGENTINA);
    public static final DeferredItem<Item> JERSEY_FRANCE = registerJersey("jersey_france", ModArmorMaterials.JERSEY_FRANCE);
    public static final DeferredItem<Item> JERSEY_ENGLAND = registerJersey("jersey_england", ModArmorMaterials.JERSEY_ENGLAND);
    public static final DeferredItem<Item> JERSEY_SPAIN = registerJersey("jersey_spain", ModArmorMaterials.JERSEY_SPAIN);
    public static final DeferredItem<Item> JERSEY_ITALY = registerJersey("jersey_italy", ModArmorMaterials.JERSEY_ITALY);
    private static DeferredItem<Item> registerJersey(String name, ArmorMaterial material) {
        return ITEMS.registerItem(name,
                properties -> new Item(properties.humanoidArmor(material, ArmorType.CHESTPLATE)));
    }
}
