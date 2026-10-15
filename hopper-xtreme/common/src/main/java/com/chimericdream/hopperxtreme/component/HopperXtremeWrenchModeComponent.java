package com.chimericdream.hopperxtreme.component;

import com.chimericdream.hopperxtreme.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

/** Which job the Hopper Wrench is set to. A wrench without this component is in {@code rotate} mode. */
public record HopperXtremeWrenchModeComponent(String mode) {
    public static final Codec<HopperXtremeWrenchModeComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.STRING.fieldOf("mode").forGetter(HopperXtremeWrenchModeComponent::mode)
    ).apply(builder, HopperXtremeWrenchModeComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "wrench_mode");
}
