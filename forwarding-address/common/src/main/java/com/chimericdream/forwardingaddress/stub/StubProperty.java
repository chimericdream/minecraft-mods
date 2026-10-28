package com.chimericdream.forwardingaddress.stub;

import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Optional;

/**
 * A block-state property declared in JSON, whose values are just names. It stands in for whatever the
 * removed mod used (an enum, usually), and converts to the target's property of the same name by value
 * name.
 */
public final class StubProperty extends Property<String> {
    private final List<String> values;

    public StubProperty(String name, List<String> values) {
        super(name, String.class);
        this.values = List.copyOf(values);
    }

    @Override
    public List<String> getPossibleValues() {
        return values;
    }

    @Override
    public String getName(String value) {
        return value;
    }

    @Override
    public Optional<String> getValue(String name) {
        return values.contains(name) ? Optional.of(name) : Optional.empty();
    }

    @Override
    public int getInternalIndex(String value) {
        return values.indexOf(value);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof StubProperty stub && super.equals(other) && values.equals(stub.values);
    }

    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + values.hashCode();
    }
}
