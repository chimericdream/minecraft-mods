package com.chimericdream.minekea.item;

import com.chimericdream.minekea.block.containers.GlassJarBlock;
import com.chimericdream.minekea.item.ingredients.WaxItem;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class WaxItems implements ModThingGroup {
    public static final Map<String, RegistrySupplier<Item>> WAX_ITEMS = new LinkedHashMap<>();

    public static final RegistrySupplier<Item> PLAIN_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("plain"), () -> new WaxItem("plain", Items.CANDLE));
    public static final RegistrySupplier<Item> WHITE_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("white"), () -> new WaxItem("white", Items.DYED_CANDLE.white()));
    public static final RegistrySupplier<Item> LIGHT_GRAY_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("light_gray"), () -> new WaxItem("light_gray", Items.DYED_CANDLE.lightGray()));
    public static final RegistrySupplier<Item> GRAY_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("gray"), () -> new WaxItem("gray", Items.DYED_CANDLE.gray()));
    public static final RegistrySupplier<Item> BLACK_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("black"), () -> new WaxItem("black", Items.DYED_CANDLE.black()));
    public static final RegistrySupplier<Item> BROWN_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("brown"), () -> new WaxItem("brown", Items.DYED_CANDLE.brown()));
    public static final RegistrySupplier<Item> RED_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("red"), () -> new WaxItem("red", Items.DYED_CANDLE.red()));
    public static final RegistrySupplier<Item> ORANGE_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("orange"), () -> new WaxItem("orange", Items.DYED_CANDLE.orange()));
    public static final RegistrySupplier<Item> YELLOW_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("yellow"), () -> new WaxItem("yellow", Items.DYED_CANDLE.yellow()));
    public static final RegistrySupplier<Item> LIME_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("lime"), () -> new WaxItem("lime", Items.DYED_CANDLE.lime()));
    public static final RegistrySupplier<Item> GREEN_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("green"), () -> new WaxItem("green", Items.DYED_CANDLE.green()));
    public static final RegistrySupplier<Item> CYAN_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("cyan"), () -> new WaxItem("cyan", Items.DYED_CANDLE.cyan()));
    public static final RegistrySupplier<Item> LIGHT_BLUE_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("light_blue"), () -> new WaxItem("light_blue", Items.DYED_CANDLE.lightBlue()));
    public static final RegistrySupplier<Item> BLUE_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("blue"), () -> new WaxItem("blue", Items.DYED_CANDLE.blue()));
    public static final RegistrySupplier<Item> PURPLE_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("purple"), () -> new WaxItem("purple", Items.DYED_CANDLE.purple()));
    public static final RegistrySupplier<Item> MAGENTA_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("magenta"), () -> new WaxItem("magenta", Items.DYED_CANDLE.magenta()));
    public static final RegistrySupplier<Item> PINK_WAX_ITEM = REGISTRY_HELPER.registerItem(WaxItem.makeId("pink"), () -> new WaxItem("pink", Items.DYED_CANDLE.pink()));

    static {
        WAX_ITEMS.put("plain", PLAIN_WAX_ITEM);
        WAX_ITEMS.put("white", WHITE_WAX_ITEM);
        WAX_ITEMS.put("light_gray", LIGHT_GRAY_WAX_ITEM);
        WAX_ITEMS.put("gray", GRAY_WAX_ITEM);
        WAX_ITEMS.put("black", BLACK_WAX_ITEM);
        WAX_ITEMS.put("brown", BROWN_WAX_ITEM);
        WAX_ITEMS.put("red", RED_WAX_ITEM);
        WAX_ITEMS.put("orange", ORANGE_WAX_ITEM);
        WAX_ITEMS.put("yellow", YELLOW_WAX_ITEM);
        WAX_ITEMS.put("lime", LIME_WAX_ITEM);
        WAX_ITEMS.put("green", GREEN_WAX_ITEM);
        WAX_ITEMS.put("cyan", CYAN_WAX_ITEM);
        WAX_ITEMS.put("light_blue", LIGHT_BLUE_WAX_ITEM);
        WAX_ITEMS.put("blue", BLUE_WAX_ITEM);
        WAX_ITEMS.put("purple", PURPLE_WAX_ITEM);
        WAX_ITEMS.put("magenta", MAGENTA_WAX_ITEM);
        WAX_ITEMS.put("pink", PINK_WAX_ITEM);
    }

    public static void postInit() {
        GlassJarBlock.ALLOWED_ITEMS.addAll(WAX_ITEMS.values().stream().map(Supplier::get).toList());
    }
}
