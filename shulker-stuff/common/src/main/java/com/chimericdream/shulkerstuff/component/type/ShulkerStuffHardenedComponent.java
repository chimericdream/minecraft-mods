package com.chimericdream.shulkerstuff.component.type;

import com.chimericdream.shulkerstuff.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public record ShulkerStuffHardenedComponent(boolean value) {
    public static final Codec<ShulkerStuffHardenedComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.BOOL.optionalFieldOf("value", false).forGetter(ShulkerStuffHardenedComponent::value)
    ).apply(builder, ShulkerStuffHardenedComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.of(ModInfo.MOD_ID, "hardened");

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("value", value);
        return nbt;
    }
}
