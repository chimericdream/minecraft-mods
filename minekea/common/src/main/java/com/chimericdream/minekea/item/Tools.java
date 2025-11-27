package com.chimericdream.minekea.item;

import com.chimericdream.minekea.item.tools.HammerItem;
import com.chimericdream.minekea.item.tools.WrenchItem;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.ItemTags;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class Tools implements ModThingGroup {
    public static final List<RegistrySupplier<Item>> ITEMS = new ArrayList<>();
    public static final List<RegistrySupplier<Item>> HAMMERS = new ArrayList<>();

    public static final RegistrySupplier<Item> STONE_HAMMER_ITEM = REGISTRY_HELPER.registerItem(HammerItem.makeId("stone"), () -> new HammerItem(ToolMaterial.STONE, 4, "Stone", null, ItemTags.STONE_TOOL_MATERIALS));
    public static final RegistrySupplier<Item> COPPER_HAMMER_ITEM = REGISTRY_HELPER.registerItem(HammerItem.makeId("copper"), () -> new HammerItem(ToolMaterial.COPPER, 4, "Copper", null, ItemTags.COPPER_TOOL_MATERIALS));
    public static final RegistrySupplier<Item> IRON_HAMMER_ITEM = REGISTRY_HELPER.registerItem(HammerItem.makeId("iron"), () -> new HammerItem(ToolMaterial.IRON, 5, "Iron", Items.IRON_INGOT, null));
    public static final RegistrySupplier<Item> GOLD_HAMMER_ITEM = REGISTRY_HELPER.registerItem(HammerItem.makeId("gold"), () -> new HammerItem(ToolMaterial.GOLD, 6, "Gold", Items.GOLD_INGOT, null));
    public static final RegistrySupplier<Item> DIAMOND_HAMMER_ITEM = REGISTRY_HELPER.registerItem(HammerItem.makeId("diamond"), () -> new HammerItem(ToolMaterial.DIAMOND, 7, "Diamond", Items.DIAMOND, null));
    public static final RegistrySupplier<Item> NETHERITE_HAMMER_ITEM = REGISTRY_HELPER.registerItem(HammerItem.makeId("netherite"), () -> new HammerItem(ToolMaterial.NETHERITE, 8, "Netherite", Items.NETHERITE_INGOT, null, new Item.Settings().fireproof()));

    //    public static final RegistrySupplier<Item> BLOCK_PAINTER_ITEM = REGISTRY_HELPER.registerItem(BlockPainterItem.ITEM_ID, BlockPainterItem::new);
    public static final RegistrySupplier<Item> WRENCH_ITEM = REGISTRY_HELPER.registerItem(WrenchItem.ITEM_ID, WrenchItem::new);

//    public static final RegistrySupplier<ScreenHandlerType<BlockPainterScreenHandler>> BLOCK_PAINTER_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(BlockPainterScreenHandler.SCREEN_ID, () -> new ScreenHandlerType<>(BlockPainterScreenHandler::new, FeatureSet.empty()));

    static {
        HAMMERS.add(STONE_HAMMER_ITEM);
        HAMMERS.add(COPPER_HAMMER_ITEM);
        HAMMERS.add(IRON_HAMMER_ITEM);
        HAMMERS.add(GOLD_HAMMER_ITEM);
        HAMMERS.add(DIAMOND_HAMMER_ITEM);
        HAMMERS.add(NETHERITE_HAMMER_ITEM);

        ITEMS.addAll(HAMMERS);
//        ITEMS.add(BLOCK_PAINTER_ITEM);
        ITEMS.add(WRENCH_ITEM);
    }
}
