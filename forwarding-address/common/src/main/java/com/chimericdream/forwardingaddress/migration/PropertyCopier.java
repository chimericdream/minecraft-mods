package com.chimericdream.forwardingaddress.migration;

import com.chimericdream.forwardingaddress.stub.StubProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Copies block-state properties between two blocks by name and type. A stub's properties, which only know their values by name, match by value name instead.
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
        if (match == null) {
            return to;
        }

        if (match.getValueClass() != property.getValueClass()) {
            // A stub's properties only know their values by name, so match them to the target by name.
            return property instanceof StubProperty || match instanceof StubProperty
                ? copyByValueName(from, to, property, match)
                : to;
        }

        @SuppressWarnings("unchecked")
        Property<T> typed = (Property<T>) match;
        T value = from.getValue(property);
        if (!typed.getPossibleValues().contains(value)) {
            return to;
        }

        return to.setValue(typed, value);
    }

    private static <T extends Comparable<T>, U extends Comparable<U>> BlockState copyByValueName(BlockState from, BlockState to, Property<T> source, Property<U> match) {
        return match.getValue(source.getName(from.getValue(source)))
            .map(value -> to.setValue(match, value))
            .orElse(to);
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
