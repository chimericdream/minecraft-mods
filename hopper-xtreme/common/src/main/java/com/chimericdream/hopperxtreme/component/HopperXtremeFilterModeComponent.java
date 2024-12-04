package com.chimericdream.hopperxtreme.component;

import com.chimericdream.hopperxtreme.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public record HopperXtremeFilterModeComponent(String mode) {
    public static final Codec<HopperXtremeFilterModeComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.STRING.fieldOf("mode").forGetter(HopperXtremeFilterModeComponent::mode)
    ).apply(builder, HopperXtremeFilterModeComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.of(ModInfo.MOD_ID, "filter_mode");

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("mode", mode);
        return nbt;
    }
}
