package com.chimericdream.hopperxtreme.item;

import com.chimericdream.hopperxtreme.component.HopperXtremeComponentTypes;
import com.chimericdream.hopperxtreme.component.HopperXtremeCopiedFilterComponent;
import com.chimericdream.hopperxtreme.entity.AbstractXtremeHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Copying a Hopper Item Filter. A hopper's filter is a physical filter item, so copying one just means cloning
 * that item's data (its contents and its Include/Exclude mode) into another filter of the same tier.
 *
 * <p>Two routes share the helpers here: the crafting-grid {@code FilterCopyRecipe}, and the Hopper Wrench's
 * Copy/Paste mode, which copies from a placed hopper ({@link #copyOrPaste}).
 */
public final class FilterCopying {
    private FilterCopying() {
    }

    /** What a wrench click on a hopper did. {@link #NOT_APPLICABLE} means the target isn't a filter-capable hopper. */
    public enum Result {
        COPIED(true, "copy.success"),
        PASTED(true, "paste.success"),
        OVERWROTE(true, "paste.overwrote"),
        NO_FILTER(false, "copy.no_filter"),
        HAS_FILTER(false, "paste.has_filter"),
        WRONG_TIER(false, "paste.wrong_tier"),
        NO_BLANK(false, "paste.no_blank"),
        NOT_APPLICABLE(false, "");

        private final boolean success;
        private final String messageKey;

        Result(boolean success, String messageKey) {
            this.success = success;
            this.messageKey = messageKey;
        }

        public boolean isSuccess() {
            return success;
        }

        /** Translation key for the actionbar message; blank for {@link #NOT_APPLICABLE}. */
        public String messageKey() {
            return messageKey.isEmpty() ? "" : "item.hopperxtreme.tools.wrench.message." + messageKey;
        }
    }

    public static boolean isFilter(ItemStack stack) {
        return stack.getItem() instanceof HopperItemFilterItem;
    }

    /** A filter with nothing in it, whatever its Include/Exclude mode. */
    public static boolean isBlank(ItemStack stack) {
        if (!isFilter(stack)) {
            return false;
        }

        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);

        return contents == null || contents.nonEmptyItemCopyStream().findAny().isEmpty();
    }

    /** A filter with at least one item in it. */
    public static boolean isConfigured(ItemStack stack) {
        return isFilter(stack) && !isBlank(stack);
    }

    /** One-item identical copy of {@code filter}: same tier, contents, and mode. */
    public static ItemStack copyOf(ItemStack filter) {
        return filter.copyWithCount(1);
    }

    /**
     * The wrench's Copy/Paste click on a hopper. With nothing copied yet it copies the hopper's filter onto the
     * wrench; with a filter copied it pastes it. Server-side only.
     *
     * <ul>
     *   <li>Paste onto a hopper with no filter installs a copy and uses up a blank filter from the player's
     *       inventory (creative mode skips the cost).</li>
     *   <li>Paste onto a hopper that already has a filter refuses, unless the player is sneaking, which
     *       overwrites it. The tiers must match.</li>
     * </ul>
     */
    public static Result copyOrPaste(ItemStack wrench, Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof AbstractXtremeHopperBlockEntity hopper) || !hopper.withFilter) {
            return Result.NOT_APPLICABLE;
        }

        // The filter slot is the last slot, and getContainerSize() deliberately hides it.
        int filterSlot = hopper.getContainerSize();
        ItemStack installed = hopper.getItem(filterSlot);
        HopperXtremeCopiedFilterComponent copied = wrench.get(HopperXtremeComponentTypes.HOPPER_XTREME_COPIED_FILTER_COMPONENT.get());

        if (copied == null) {
            if (!isFilter(installed)) {
                return Result.NO_FILTER;
            }

            wrench.set(HopperXtremeComponentTypes.HOPPER_XTREME_COPIED_FILTER_COMPONENT.get(), new HopperXtremeCopiedFilterComponent(copyOf(installed)));

            return Result.COPIED;
        }

        ItemStack source = copied.filter();

        if (isFilter(installed)) {
            if (!player.isShiftKeyDown()) {
                return Result.HAS_FILTER;
            }

            if (!ItemStack.isSameItem(installed, source)) {
                return Result.WRONG_TIER;
            }

            install(hopper, filterSlot, source);

            return Result.OVERWROTE;
        }

        if (!player.hasInfiniteMaterials() && !takeBlankFilter(player, source)) {
            return Result.NO_BLANK;
        }

        install(hopper, filterSlot, source);

        return Result.PASTED;
    }

    private static void install(AbstractXtremeHopperBlockEntity hopper, int filterSlot, ItemStack source) {
        hopper.setItem(filterSlot, copyOf(source));
        hopper.setChanged();
    }

    /** Uses up one blank filter of {@code source}'s tier from the player's inventory. */
    private static boolean takeBlankFilter(Player player, ItemStack source) {
        NonNullList<ItemStack> items = player.getInventory().getNonEquipmentItems();

        for (ItemStack candidate : items) {
            if (ItemStack.isSameItem(candidate, source) && isBlank(candidate)) {
                candidate.shrink(1);

                return true;
            }
        }

        return false;
    }
}
