package com.chimericdream.minekea.client.render.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class ArmoireBlockEntityRenderState extends BlockEntityRenderState {
    public Direction facing;
    public final List<ArmorItemData> items = new ArrayList<>();
    public final List<ItemStackRenderState> displayItems = List.of(
        new ItemStackRenderState(),
        new ItemStackRenderState(),
        new ItemStackRenderState(),
        new ItemStackRenderState(),
        new ItemStackRenderState(),
        new ItemStackRenderState(),
        new ItemStackRenderState(),
        new ItemStackRenderState()
    );

    public final List<StandArmorData> standArmor = new ArrayList<>();

    public record ArmorItemData(
        double xOffset,
        double yOffset,
        double zOffset,
        ItemStack stack
    ) {

    }

    public record StandArmorData(
        double xOffset,
        double zOffset,
        ItemStack chestplate,
        ItemStack leggings
    ) {

    }
}
