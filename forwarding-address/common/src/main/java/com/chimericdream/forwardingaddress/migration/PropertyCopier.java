package com.chimericdream.forwardingaddress.migration;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Copies block-state properties between two blocks by name and type.
 */
public final class PropertyCopier {
    private PropertyCopier() {
    }

    /**
     * Copies every property of {@code from} that {@code to} also has, with a value {@code to} accepts.
     * A property the target doesn't have, or whose value the target doesn't allow, keeps the target's
     * default; a migration's property mapper handles anything that needs more than that.
     */
    public static BlockState copy(BlockState from, BlockState to) {
        BlockState result = to;
        for (Property<?> property : from.getProperties()) {
            result = copyProperty(from, result, property);
        }

        return result;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
        Property<?> match = findByName(to, property.getName());
        if (match == null || match.getValueClass() != property.getValueClass()) {
            return to;
        }

        @SuppressWarnings("unchecked")
        Property<T> typed = (Property<T>) match;
        T value = from.getValue(property);
        if (!typed.getPossibleValues().contains(value)) {
            return to;
        }

        return to.setValue(typed, value);
    }

    private static Property<?> findByName(BlockState state, String name) {
        for (Property<?> candidate : state.getProperties()) {
            if (candidate.getName().equals(name)) {
                return candidate;
            }
        }

        return null;
    }
}
