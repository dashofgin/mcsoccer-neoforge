package com.mcsoccer.item;

import com.mcsoccer.MCSoccerMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MCSoccerMod.MOD_ID);

    // ==================== BALL ====================

    @SuppressWarnings("removal")
    public static final DeferredItem<Item> SOCCER_BALL = ITEMS.registerItem("soccer_ball",
            SoccerBallItem::new, new Item.Properties().stacksTo(16));

    // ==================== CLUB JERSEYS ====================

    public static final DeferredItem<Item> JERSEY_REAL_MADRID = registerJersey("jersey_real_madrid");
    public static final DeferredItem<Item> JERSEY_BARCELONA = registerJersey("jersey_barcelona");
    public static final DeferredItem<Item> JERSEY_BAYERN = registerJersey("jersey_bayern");
    public static final DeferredItem<Item> JERSEY_PSG = registerJersey("jersey_psg");
    public static final DeferredItem<Item> JERSEY_MAN_CITY = registerJersey("jersey_man_city");
    public static final DeferredItem<Item> JERSEY_LIVERPOOL = registerJersey("jersey_liverpool");
    public static final DeferredItem<Item> JERSEY_JUVENTUS = registerJersey("jersey_juventus");
    public static final DeferredItem<Item> JERSEY_AC_MILAN = registerJersey("jersey_ac_milan");

    // ==================== NATIONAL TEAM JERSEYS ====================

    public static final DeferredItem<Item> JERSEY_POLAND = registerJersey("jersey_poland");
    public static final DeferredItem<Item> JERSEY_BRAZIL = registerJersey("jersey_brazil");
    public static final DeferredItem<Item> JERSEY_GERMANY = registerJersey("jersey_germany");
    public static final DeferredItem<Item> JERSEY_ARGENTINA = registerJersey("jersey_argentina");
    public static final DeferredItem<Item> JERSEY_FRANCE = registerJersey("jersey_france");
    public static final DeferredItem<Item> JERSEY_ENGLAND = registerJersey("jersey_england");
    public static final DeferredItem<Item> JERSEY_SPAIN = registerJersey("jersey_spain");
    public static final DeferredItem<Item> JERSEY_ITALY = registerJersey("jersey_italy");

    private static DeferredItem<Item> registerJersey(String name) {
        DeferredItem<Item> item = ITEMS.registerItem(name,
                properties -> new Item(properties.humanoidArmor(ModArmorMaterials.JERSEY, ArmorType.CHESTPLATE)));
        return item;
    }
}
