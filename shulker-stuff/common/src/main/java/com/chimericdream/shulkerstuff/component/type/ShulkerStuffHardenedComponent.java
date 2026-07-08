package com.chimericdream.shulkerstuff.component.type;

import com.chimericdream.shulkerstuff.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record ShulkerStuffHardenedComponent(boolean value) {
    public static final Codec<ShulkerStuffHardenedComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.BOOL.optionalFieldOf("value", false).forGetter(ShulkerStuffHardenedComponent::value)
    ).apply(builder, ShulkerStuffHardenedComponent::new));

    public static final ResourceLocation COMPONENT_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "hardened");

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("value", value);
        return nbt;
    }
}
