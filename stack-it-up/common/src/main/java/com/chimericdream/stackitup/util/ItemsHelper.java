package com.chimericdream.stackitup.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.chimericdream.stackitup.StackItUpMod.LOGGER;

public class ItemsHelper {
    public static final int ItemMaxCount = 99;
    private static ItemsHelper itemsHelper;

    private ItemsHelper() {
    }

    public static ItemsHelper getItemsHelper() {
        if (itemsHelper == null) {
            itemsHelper = new ItemsHelper();
        }
        return itemsHelper;
    }

    public void resetAll(boolean serverSide) {
        for (Map.Entry<ResourceKey<Item>, Item> itemEntry : getItemSet()) {
            Item item = itemEntry.getValue();
            ((IItemMaxCount) item).revert();
        }
        if (serverSide) LOGGER.info("[StackItUp] Reset all items");
    }

    public void resetItem(Item item) {
        ((IItemMaxCount) item).revert();
        LOGGER.info("[StackItUp] Reset " + BuiltInRegistries.ITEM.getKey(item).toString());
    }

    public void setCountByConfig(Set<Map.Entry<String, Integer>> configSet, boolean serverSide) {
        resetAll(serverSide);
        for (Map.Entry<String, Integer> entry : configSet) {
            Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(entry.getKey()));
            int size = Integer.min(entry.getValue(), ItemsHelper.ItemMaxCount);
            if (serverSide)
                LOGGER.info("[StackItUp] Set " + entry.getKey() + " to " + size);
            else
                LOGGER.info("[StackItUp] [Client] Set " + entry.getKey() + " to " + size);
            ((IItemMaxCount) item).setMaxCount(size);
        }
    }

    public int getDefaultCount(Item item) {
        return ((IItemMaxCount) item).getVanillaMaxCount();
    }

    public int getCurrentCount(Item item) {
        return item.getDefaultMaxStackSize();
    }

    public boolean isVanilla(Item item) {
        return getDefaultCount(item) == getCurrentCount(item);
    }

    public void setSingle(Item item, int count) {
        ((IItemMaxCount) item).setMaxCount(count);
        LOGGER.info("[StackItUp] Set " + BuiltInRegistries.ITEM.getKey(item).toString() + " to " + count);
    }

    public LinkedList<Item> getAllModifiedItems() {
        LinkedList<Item> list = new LinkedList<>();
        for (Map.Entry<ResourceKey<Item>, Item> itemEntry : getItemSet()) {
            Item item = itemEntry.getValue();
            if (getDefaultCount(item) != getCurrentCount(item) && !list.contains(item)) {
                list.add(item);
            }
        }
        return list;
    }

    public int setMatchedItems(int originalSize, int newSize, String type) {
        int counter = 0;
        switch (type) {
            case "vanilla":
                for (Map.Entry<ResourceKey<Item>, Item> itemEntry : getItemSet()) {
                    Item item = itemEntry.getValue();
                    if (isVanilla(item) && getCurrentCount(item) == originalSize) {
                        setSingle(item, newSize);
                        counter++;
                    }
                }
                break;
            case "modified":
                for (Map.Entry<ResourceKey<Item>, Item> itemEntry : getItemSet()) {
                    Item item = itemEntry.getValue();
                    if (!isVanilla(item) && getCurrentCount(item) == originalSize) {
                        setSingle(item, newSize);
                        counter++;
                    }
                }
                break;
            case "all":
                for (Map.Entry<ResourceKey<Item>, Item> itemEntry : getItemSet()) {
                    Item item = itemEntry.getValue();
                    if (getCurrentCount(item) == originalSize) {
                        setSingle(item, newSize);
                        counter++;
                    }
                }
                break;
        }

        return counter;
    }

    public LinkedHashMap<String, Integer> getNewConfigMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        for (Map.Entry<ResourceKey<Item>, Item> itemEntry : getItemSet()) {
            Item item = itemEntry.getValue();
            String id = BuiltInRegistries.ITEM.getKey(item).toString();
            if (getDefaultCount(item) != getCurrentCount(item) && !map.containsKey(id)) {
                map.put(id, item.getDefaultMaxStackSize());
            }
        }
        return map;
    }

    private Set<Map.Entry<ResourceKey<Item>, Item>> getItemSet() {
        return BuiltInRegistries.ITEM.entrySet();
    }

    public static boolean shulkerBoxHasItems(ItemStack stack) {
        DataComponentMap tag = stack.getComponents();

        if (tag == null || !tag.has(DataComponents.BLOCK_ENTITY_DATA))
            return false;

        CompoundTag bet = Objects.requireNonNull(tag.get(DataComponents.BLOCK_ENTITY_DATA)).copyTagWithoutId();
        return bet.getList("Items").map(list -> !list.isEmpty()).orElse(false);
    }

    public static void insertNewItem(Player player, InteractionHand hand, ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty()) {
            player.setItemInHand(hand, stack2);
        } else if (!player.getInventory().add(stack2)) {
            player.drop(stack2, false);
        }
    }

    public static void insertNewItem(Player player, ItemStack stack2) {
        if (!player.getInventory().add(stack2)) {
            player.drop(stack2, false);
        }
    }

    public static boolean isModified(ItemStack s) {
        if (s.isEmpty()) {
            return false;
        }
        Item i = s.getItem();
        return ((IItemMaxCount) i).getVanillaMaxCount() != i.getDefaultMaxStackSize();
    }
}
