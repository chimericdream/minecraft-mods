package com.chimericdream.shulkerstuff.component.type;

import com.chimericdream.shulkerstuff.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public record ShulkerStuffDyedColorComponent(int lidColor, int baseColor) {
    public static final Codec<ShulkerStuffDyedColorComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.INT.optionalFieldOf("lidColor", 9922455).forGetter(ShulkerStuffDyedColorComponent::lidColor),
        Codec.INT.optionalFieldOf("baseColor", 9922455).forGetter(ShulkerStuffDyedColorComponent::baseColor)
    ).apply(builder, ShulkerStuffDyedColorComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "dyed_color");

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("lidColor", lidColor);
        nbt.putInt("baseColor", baseColor);
        return nbt;
    }
}
