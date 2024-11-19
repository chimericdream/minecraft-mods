package com.chimericdream.shulkerstuff.component.type;

import com.chimericdream.shulkerstuff.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public record ShulkerStuffDataComponent(int dyedColor, boolean plated, boolean hardened) {
    public static final Codec<ShulkerStuffDataComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.INT.optionalFieldOf("dyedColor", -1).forGetter(ShulkerStuffDataComponent::dyedColor),
        Codec.BOOL.optionalFieldOf("plated", false).forGetter(ShulkerStuffDataComponent::plated),
        Codec.BOOL.optionalFieldOf("hardened", false).forGetter(ShulkerStuffDataComponent::hardened)
    ).apply(builder, ShulkerStuffDataComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.of(ModInfo.MOD_ID, "data");

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("dyedColor", dyedColor);
        nbt.putBoolean("plated", plated);
        nbt.putBoolean("hardened", hardened);
        return nbt;
    }
}
