package com.chimericdream.hopperxtreme.item;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.client.screen.HopperItemFilterScreenHandler;
import com.chimericdream.hopperxtreme.component.HopperXtremeComponentTypes;
import com.chimericdream.hopperxtreme.component.HopperXtremeFilterModeComponent;
import com.chimericdream.lib.inventories.ImplementedInventory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class HopperItemFilterItem extends Item {
    public static final Identifier ITEM_ID = Identifier.of(ModInfo.MOD_ID, "hopper_item_filter");

    public static final Map<FilterMode, String> TOOLTIP_KEYS = Map.of(
        FilterMode.INCLUDE, "item.hopperxtreme.hopper_item_filter.tooltip.include",
        FilterMode.EXCLUDE, "item.hopperxtreme.hopper_item_filter.tooltip.exclude"
    );

    public HopperItemFilterItem() {
        super(new Settings().maxCount(1).arch$tab(ItemGroups.REDSTONE));
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);

        stack.set(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), new HopperXtremeFilterModeComponent("include"));

        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);

        HopperXtremeFilterModeComponent component = stack.getOrDefault(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), new HopperXtremeFilterModeComponent("include"));
        FilterMode mode = FilterMode.fromString(component.mode());

        tooltip.add(Text.translatable(TOOLTIP_KEYS.get(mode)));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (player.getWorld() != null && !player.getWorld().isClient) {
            if (player.isSneaking()) {
                try {
                    ItemStack itemStack = player.getStackInHand(hand);
                    HopperXtremeFilterModeComponent component = itemStack.getOrDefault(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), new HopperXtremeFilterModeComponent("include"));

                    FilterMode currentMode = FilterMode.fromString(component.mode());
                    FilterMode newMode = currentMode.equals(FilterMode.EXCLUDE) ? FilterMode.INCLUDE : FilterMode.EXCLUDE;

                    HopperXtremeFilterModeComponent newComponent = new HopperXtremeFilterModeComponent(newMode.toString());
                    itemStack.set(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), newComponent);

                    if (!world.isClient()) {
                        player.sendMessage(Text.translatable(TOOLTIP_KEYS.get(newMode)), true);
                    }

                    return TypedActionResult.success(player.getStackInHand(hand));
                } catch (IllegalArgumentException e) {
                    return TypedActionResult.fail(player.getStackInHand(hand));
                }
            } else {
                openScreen(player, player.getStackInHand(hand));
            }
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }

    public static void openScreen(PlayerEntity player, ItemStack filter) {
        if (player.getWorld() != null && !player.getWorld().isClient) {
            player.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return Text.translatable(filter.getItem().getTranslationKey());
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new HopperItemFilterScreenHandler(syncId, inv, filter);
                }
            });
        }
    }

    public static class FilterInventory implements ImplementedInventory {
        public static final int INVENTORY_SIZE = 5;

        private final ItemStack filterStack;
        private final DefaultedList<ItemStack> items = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);

        public FilterInventory(ItemStack stack) {
            filterStack = stack;

            ContainerComponent inventory = filterStack.get(DataComponentTypes.CONTAINER);

            if (inventory != null) {
                inventory.copyTo(items);
            }
        }

        private boolean hasAnyItems() {
            return items.stream().anyMatch((itemStack) -> !itemStack.isEmpty());
        }

        @Override
        public DefaultedList<ItemStack> getItems() {
            return items;
        }

        @Override
        public ItemStack tryInsert(ItemStack stack) {
            return stack;
        }

        @Override
        public ItemStack tryInsert(int slot, ItemStack stack) {
            return ImplementedInventory.super.tryInsert(slot, stack);
        }

        @Override
        public void markDirty() {
            filterStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(items));
            if (this.hasAnyItems()) {
                filterStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(2233000));
            } else {
                filterStack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
            }
        }
    }

    public static boolean matchesFilter(ItemStack filter, ItemStack stack) {
        if (filter.isEmpty()) {
            return true;
        }

        ContainerComponent component = filter.get(DataComponentTypes.CONTAINER);
        HopperXtremeFilterModeComponent filterComponent = filter.get(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get());

        if (component == null) {
            return false;
        }

        FilterMode mode = filterComponent == null ? FilterMode.INCLUDE : FilterMode.fromString(filterComponent.mode());

        for (ItemStack filterItem : component.iterateNonEmpty()) {
            if (ItemStack.areItemsEqual(filterItem, stack)) {
                return mode.equals(FilterMode.INCLUDE);
            }
        }

        return mode.equals(FilterMode.EXCLUDE);
    }

    public enum FilterMode {
        INCLUDE,
        EXCLUDE;

        public static FilterMode fromString(String mode) {
            if (mode != null && mode.equalsIgnoreCase("exclude")) {
                return EXCLUDE;
            }

            return INCLUDE;
        }

        public String toString() {
            return switch (this) {
                case INCLUDE -> "include";
                case EXCLUDE -> "exclude";
            };
        }
    }
}
