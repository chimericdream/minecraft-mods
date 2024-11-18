package com.chimericdream.shulkerstuff.component.type;

import com.chimericdream.shulkerstuff.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public record ShulkerStuffDataComponent(int dyedColor) {
    public static final Codec<ShulkerStuffDataComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.INT.fieldOf("dyedColor").forGetter(ShulkerStuffDataComponent::dyedColor)
    ).apply(builder, ShulkerStuffDataComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.of(ModInfo.MOD_ID, "data");

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("dyedColor", dyedColor);
        return nbt;
    }
}
