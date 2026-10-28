package com.chimericdream.forwardingaddress.stub;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Turns a {@link StubSpec} into real block-state properties.
 */
public final class StubProperties {
    private StubProperties() {
    }

    /**
     * @return the properties, or {@code null} if the spec names a template block that doesn't exist
     */
    @Nullable
    public static List<Property<?>> build(StubSpec spec) {
        if (spec.template() != null) {
            Block template = BuiltInRegistries.BLOCK.getOptional(spec.template()).orElse(null);
            return template == null ? null : new ArrayList<>(template.defaultBlockState().getProperties());
        }

        List<Property<?>> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : spec.properties().entrySet()) {
            result.add(property(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    /**
     * Booleans and consecutive integers become the real property types, so they copy to the target
     * exactly as they would between ordinary blocks. Anything else keeps its value names.
     */
    public static Property<?> property(String name, List<String> values) {
        if (values.size() == 2 && values.contains("true") && values.contains("false")) {
            return BooleanProperty.create(name);
        }

        Integer min = consecutiveFrom(values);
        if (min != null) {
            return IntegerProperty.create(name, min, min + values.size() - 1);
        }

        return new StubProperty(name, values);
    }

    @Nullable
    private static Integer consecutiveFrom(List<String> values) {
        if (values.size() < 2) {
            return null;
        }

        try {
            int first = Integer.parseInt(values.getFirst());
            if (first < 0) {
                return null;
            }

            for (int i = 0; i < values.size(); i++) {
                if (Integer.parseInt(values.get(i)) != first + i) {
                    return null;
                }
            }

            return first;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
