package com.chimericdream.shulkerstuff.component.type;

import com.chimericdream.shulkerstuff.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public record ShulkerStuffPlatedComponent(boolean value) {
    public static final Codec<ShulkerStuffPlatedComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.BOOL.optionalFieldOf("value", false).forGetter(ShulkerStuffPlatedComponent::value)
    ).apply(builder, ShulkerStuffPlatedComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.of(ModInfo.MOD_ID, "plated");

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("value", value);
        return nbt;
    }
}
