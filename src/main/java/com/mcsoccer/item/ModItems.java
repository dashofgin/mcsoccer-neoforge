package com.mcsoccer.item;

import com.mcsoccer.MCSoccerMod;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MCSoccerMod.MOD_ID);

    // ==================== BALL ====================

    public static final DeferredItem<Item> SOCCER_BALL = ITEMS.registerSimpleItem("soccer_ball",
            new Item.Properties().stacksTo(1));

    // ==================== CLUB JERSEYS ====================

    public static final DeferredItem<Item> JERSEY_REAL_MADRID = ITEMS.registerSimpleItem("jersey_real_madrid",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_BARCELONA = ITEMS.registerSimpleItem("jersey_barcelona",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_BAYERN = ITEMS.registerSimpleItem("jersey_bayern",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_PSG = ITEMS.registerSimpleItem("jersey_psg",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_MAN_CITY = ITEMS.registerSimpleItem("jersey_man_city",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_LIVERPOOL = ITEMS.registerSimpleItem("jersey_liverpool",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_JUVENTUS = ITEMS.registerSimpleItem("jersey_juventus",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_AC_MILAN = ITEMS.registerSimpleItem("jersey_ac_milan",
            new Item.Properties().stacksTo(1));

    // ==================== NATIONAL TEAM JERSEYS ====================

    public static final DeferredItem<Item> JERSEY_POLAND = ITEMS.registerSimpleItem("jersey_poland",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_BRAZIL = ITEMS.registerSimpleItem("jersey_brazil",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_GERMANY = ITEMS.registerSimpleItem("jersey_germany",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_ARGENTINA = ITEMS.registerSimpleItem("jersey_argentina",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_FRANCE = ITEMS.registerSimpleItem("jersey_france",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_ENGLAND = ITEMS.registerSimpleItem("jersey_england",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_SPAIN = ITEMS.registerSimpleItem("jersey_spain",
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> JERSEY_ITALY = ITEMS.registerSimpleItem("jersey_italy",
            new Item.Properties().stacksTo(1));
}
