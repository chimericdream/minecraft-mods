package com.chimericdream.allhallowssteve.component.type;

import com.chimericdream.allhallowssteve.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * The stencil (by {@link com.chimericdream.allhallowssteve.item.PumpkinStencilItem#stencil} id, or
 * empty for a bare face) carved into each of a decorated pumpkin's four horizontal faces, in the
 * block's default ({@code FACING == NORTH}) orientation. The whole set rotates with the block the same
 * way its base model does (see {@code DecoratedPumpkinBlockDataGenerator}'s horizontal-facing
 * dispatch), so "north" always means whichever face pointed north at the moment it was carved.
 */
public record PumpkinStencilsComponent(Optional<String> north, Optional<String> east, Optional<String> south, Optional<String> west) {
    public static final PumpkinStencilsComponent EMPTY = new PumpkinStencilsComponent(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public static final Codec<PumpkinStencilsComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("north").forGetter(PumpkinStencilsComponent::north),
        Codec.STRING.optionalFieldOf("east").forGetter(PumpkinStencilsComponent::east),
        Codec.STRING.optionalFieldOf("south").forGetter(PumpkinStencilsComponent::south),
        Codec.STRING.optionalFieldOf("west").forGetter(PumpkinStencilsComponent::west)
    ).apply(instance, PumpkinStencilsComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pumpkin_stencils");

    public boolean isEmpty() {
        return north.isEmpty() && east.isEmpty() && south.isEmpty() && west.isEmpty();
    }

    public Optional<String> get(Direction face) {
        return switch (face) {
            case NORTH -> north;
            case EAST -> east;
            case SOUTH -> south;
            case WEST -> west;
            default -> Optional.empty();
        };
    }

    public PumpkinStencilsComponent with(Direction face, String stencil) {
        Optional<String> value = Optional.of(stencil);

        return switch (face) {
            case NORTH -> new PumpkinStencilsComponent(value, east, south, west);
            case EAST -> new PumpkinStencilsComponent(north, value, south, west);
            case SOUTH -> new PumpkinStencilsComponent(north, east, value, west);
            case WEST -> new PumpkinStencilsComponent(north, east, south, value);
            default -> this;
        };
    }
}
