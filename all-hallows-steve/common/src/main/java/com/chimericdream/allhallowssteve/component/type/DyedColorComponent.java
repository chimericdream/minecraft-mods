package com.chimericdream.allhallowssteve.component.type;

import com.chimericdream.allhallowssteve.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record DyedColorComponent(int color) {
    /** A natural pumpkin orange, used when a pumpkin has no stored dye color. */
    public static final int DEFAULT_COLOR = 14712858;

    public static final DyedColorComponent DEFAULT = new DyedColorComponent(DEFAULT_COLOR);

    public static final Codec<DyedColorComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.INT.optionalFieldOf("color", DEFAULT_COLOR).forGetter(DyedColorComponent::color)
    ).apply(builder, DyedColorComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "dyed_color");
}
