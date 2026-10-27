package com.chimericdream.forwardingaddress.migration;

import com.chimericdream.forwardingaddress.api.ItemMapper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * Converts item stacks of a retired item into the replacement item.
 */
public final class ItemConverter {
    private final MigrationRegistry registry;
    private final MigrationLog log;

    public ItemConverter(MigrationRegistry registry, MigrationLog log) {
        this.registry = registry;
        this.log = log;
    }

    /**
     * @return the converted stack, or {@code stack} itself when no migration applies or the conversion
     * can't be done exactly
     */
    public ItemStack convert(ItemStack stack) {
        if (stack.isEmpty()) {
            return stack;
        }

        MigrationRegistry.ResolvedItem resolved = registry.forItem(stack.getItem());
        if (resolved == null) {
            return stack;
        }

        ItemStack converted = new ItemStack(resolved.to(), stack.getCount());
        if (stack.getCount() > converted.getMaxStackSize()) {
            // Clamping would delete items and splitting needs somewhere to put the rest, so leave it.
            log.warn("stack-too-large:" + resolved.migration().from(), "Not converting {} x{} to {}: the new item stacks to {}", resolved.migration().from(), stack.getCount(), resolved.migration().to(), converted.getMaxStackSize());
            return stack;
        }

        converted.applyComponents(stack.getComponentsPatch());

        ItemMapper mapper = resolved.migration().mapper();
        if (mapper != null) {
            try {
                return mapper.map(stack, converted);
            } catch (RuntimeException e) {
                log.logger().error("The item mapper for {} failed; leaving the stack as it is", resolved.migration().from(), e);
                return stack;
            }
        }

        return converted;
    }

    /**
     * Converts every stack in the container in place.
     *
     * @return how many slots changed
     */
    public int convertContainer(Container container) {
        int changed = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack original = container.getItem(slot);
            ItemStack converted = convert(original);
            if (converted != original) {
                container.setItem(slot, converted);
                changed++;
            }
        }

        return changed;
    }
}
