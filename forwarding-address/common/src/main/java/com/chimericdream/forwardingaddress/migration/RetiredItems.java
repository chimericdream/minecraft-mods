package com.chimericdream.forwardingaddress.migration;

import com.chimericdream.lib.text.TextHelpers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * What the player sees of a retired item: a tooltip saying it will convert, and no place in the
 * creative inventory. Loader code wires these into its own tooltip and creative-tab events.
 */
public final class RetiredItems {
    public static final String TOOLTIP_KEY = "item.forwardingaddress.retired.tooltip";

    private RetiredItems() {
    }

    /**
     * @return whether a migration with an installed target retires this item
     */
    public static boolean isRetired(ItemStack stack) {
        return MigrationRegistry.INSTANCE.forItem(stack.getItem()) != null;
    }

    public static void appendTooltip(ItemStack stack, List<Component> lines) {
        MigrationRegistry.ResolvedItem resolved = MigrationRegistry.INSTANCE.forItem(stack.getItem());
        if (resolved == null) {
            return;
        }

        lines.add(TextHelpers.formatTooltip(Component.translatable(TOOLTIP_KEY, resolved.to().getDefaultInstance().getHoverName())));
    }
}
