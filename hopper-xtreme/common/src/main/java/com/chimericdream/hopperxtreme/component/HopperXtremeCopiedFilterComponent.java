package com.chimericdream.hopperxtreme.component;

import com.chimericdream.hopperxtreme.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * The filter the wrench most recently copied from a hopper: a one-item snapshot of the filter stack, so its
 * contents, Include/Exclude mode, and tier all travel with it. Kept on the wrench so it survives relogs.
 */
public record HopperXtremeCopiedFilterComponent(ItemStack filter) {
    public static final Codec<HopperXtremeCopiedFilterComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ItemStack.CODEC.fieldOf("filter").forGetter(HopperXtremeCopiedFilterComponent::filter)
    ).apply(builder, HopperXtremeCopiedFilterComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "copied_filter");

    // ItemStack has no value equality of its own; components need it so identical wrenches still stack-compare equal.
    @Override
    public boolean equals(Object other) {
        return other instanceof HopperXtremeCopiedFilterComponent that && ItemStack.matches(this.filter, that.filter);
    }

    @Override
    public int hashCode() {
        return ItemStack.hashItemAndComponents(this.filter);
    }
}
