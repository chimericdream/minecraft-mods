package com.chimericdream.forwardingaddress.config;

import com.chimericdream.forwardingaddress.api.PropertyMapper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds the {@link PropertyMapper} for a JSON migration's {@code renameProperties} and
 * {@code mapValues}. Properties that need neither are already copied by name.
 */
public final class PropertyRules {
    private PropertyRules() {
    }

    /**
     * @param renames old property name to new property name
     * @param values  new property name to a map of old value name to new value name
     * @return the mapper, or {@code null} if there is nothing to do
     */
    @Nullable
    public static PropertyMapper mapper(Map<String, String> renames, Map<String, Map<String, String>> values) {
        if (renames.isEmpty() && values.isEmpty()) {
            return null;
        }

        Map<String, String> reverse = new HashMap<>();
        renames.forEach((from, to) -> reverse.put(to, from));

        return (oldState, target) -> {
            BlockState result = target;
            for (Property<?> property : target.getProperties()) {
                String newName = property.getName();
                String oldName = reverse.getOrDefault(newName, newName);
                Property<?> source = find(oldState, oldName);
                if (source == null) {
                    continue;
                }

                String value = valueName(oldState, source);
                Map<String, String> valueMap = values.get(newName);
                String mapped = valueMap == null ? value : valueMap.getOrDefault(value, value);

                // Same name and same value: the ordinary copy already did this.
                if (!oldName.equals(newName) || !mapped.equals(value)) {
                    result = set(result, property, mapped);
                }
            }

            return result;
        };
    }

    private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    private static <T extends Comparable<T>> BlockState set(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(parsed -> state.setValue(property, parsed)).orElse(state);
    }

    @Nullable
    private static Property<?> find(BlockState state, String name) {
        for (Property<?> candidate : state.getProperties()) {
            if (candidate.getName().equals(name)) {
                return candidate;
            }
        }

        return null;
    }
}
