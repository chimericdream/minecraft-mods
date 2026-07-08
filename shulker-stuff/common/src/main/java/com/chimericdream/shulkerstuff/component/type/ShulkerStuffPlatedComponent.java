package com.chimericdream.shulkerstuff.component.type;

import com.chimericdream.shulkerstuff.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record ShulkerStuffPlatedComponent(boolean value) {
    public static final Codec<ShulkerStuffPlatedComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.BOOL.optionalFieldOf("value", false).forGetter(ShulkerStuffPlatedComponent::value)
    ).apply(builder, ShulkerStuffPlatedComponent::new));

    public static final ResourceLocation COMPONENT_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "plated");

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("value", value);
        return nbt;
    }
}
