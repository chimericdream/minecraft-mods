package com.chimericdream.hopperxtreme.item;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.client.screen.HopperItemFilterScreenHandler;
import com.chimericdream.hopperxtreme.component.HopperXtremeComponentTypes;
import com.chimericdream.hopperxtreme.component.HopperXtremeFilterModeComponent;
import com.chimericdream.lib.inventories.ImplementedInventory;
import java.util.List;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

public class HopperItemFilterItem extends Item {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "hopper_item_filter");

    public static final Map<FilterMode, String> TOOLTIP_KEYS = Map.of(
        FilterMode.INCLUDE, "item.hopperxtreme.hopper_item_filter.tooltip.include",
        FilterMode.EXCLUDE, "item.hopperxtreme.hopper_item_filter.tooltip.exclude"
    );

    @SuppressWarnings("UnstableApiUsage")
    public HopperItemFilterItem() {
        super(
            new Properties()
                .stacksTo(1)
                .arch$tab(CreativeModeTabs.REDSTONE_BLOCKS)
                .useItemDescriptionPrefix()
                .setId(REGISTRY_HELPER.makeItemRegistryKey(ITEM_ID))
        );
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);

        stack.set(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), new HopperXtremeFilterModeComponent("include"));

        return stack;
    }

    @Override
    public @NotNull InteractionResult use(Level world, Player player, InteractionHand hand) {
        // Everything below is server-authoritative; the client just reports the (unchanged) held item.
        // `world` is the player's level, so a single client-side check covers what used to be a nested
        // `player.level() != null && !player.level().isClientSide()` plus an inner `!world.isClientSide()`.
        if (!world.isClientSide()) {
            if (player.isShiftKeyDown()) {
                try {
                    ItemStack itemStack = player.getItemInHand(hand);
                    HopperXtremeFilterModeComponent component = itemStack.getOrDefault(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), new HopperXtremeFilterModeComponent("include"));

                    FilterMode currentMode = FilterMode.fromString(component.mode());
                    FilterMode newMode = currentMode.equals(FilterMode.EXCLUDE) ? FilterMode.INCLUDE : FilterMode.EXCLUDE;

                    HopperXtremeFilterModeComponent newComponent = new HopperXtremeFilterModeComponent(newMode.toString());
                    itemStack.set(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), newComponent);

                    player.sendOverlayMessage(Component.translatable(TOOLTIP_KEYS.get(newMode)));

                    return InteractionResult.SUCCESS.heldItemTransformedTo(player.getItemInHand(hand));
                } catch (IllegalArgumentException e) {
                    return InteractionResult.FAIL;
                }
            } else {
                openScreen(player, player.getItemInHand(hand));
            }
        }

        return InteractionResult.SUCCESS.heldItemTransformedTo(player.getItemInHand(hand));
    }

    public static void openScreen(Player player, ItemStack filter) {
        if (player.level() != null && !player.level().isClientSide()) {
            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable(filter.getItem().getDescriptionId());
                }

                @Override
                public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                    return new HopperItemFilterScreenHandler(syncId, inv, filter);
                }
            });
        }
    }

    public static class FilterInventory implements ImplementedInventory {
        public static final int INVENTORY_SIZE = 5;

        private final ItemStack filterStack;
        private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

        public FilterInventory(ItemStack stack) {
            filterStack = stack;

            ItemContainerContents inventory = filterStack.get(DataComponents.CONTAINER);

            if (inventory != null) {
                inventory.copyInto(items);
            }
        }

        private boolean hasAnyItems() {
            return items.stream().anyMatch((itemStack) -> !itemStack.isEmpty());
        }

        @Override
        public NonNullList<ItemStack> getItems() {
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
        public void setChanged() {
            filterStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
            if (this.hasAnyItems()) {
                filterStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of("2233000"), List.of()));
            } else {
                filterStack.remove(DataComponents.CUSTOM_MODEL_DATA);
            }
        }
    }

    public static boolean matchesFilter(ItemStack filter, ItemStack stack) {
        if (filter.isEmpty()) {
            return true;
        }

        ItemContainerContents component = filter.get(DataComponents.CONTAINER);
        HopperXtremeFilterModeComponent filterComponent = filter.get(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get());

        if (component == null) {
            return false;
        }

        FilterMode mode = filterComponent == null ? FilterMode.INCLUDE : FilterMode.fromString(filterComponent.mode());

        for (ItemStack filterItem : component.nonEmptyItemCopyStream().toList()) {
            if (ItemStack.isSameItem(filterItem, stack)) {
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
