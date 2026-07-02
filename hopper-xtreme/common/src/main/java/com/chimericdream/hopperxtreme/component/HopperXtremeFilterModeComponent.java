package com.chimericdream.hopperxtreme.component;

import com.chimericdream.hopperxtreme.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record HopperXtremeFilterModeComponent(String mode) {
    public static final Codec<HopperXtremeFilterModeComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.STRING.fieldOf("mode").forGetter(HopperXtremeFilterModeComponent::mode)
    ).apply(builder, HopperXtremeFilterModeComponent::new));

    public static final ResourceLocation COMPONENT_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "filter_mode");

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("mode", mode);
        return nbt;
    }
}
