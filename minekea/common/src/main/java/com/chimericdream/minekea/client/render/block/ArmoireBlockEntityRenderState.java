package com.chimericdream.minekea.client.render.block;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class ArmoireBlockEntityRenderState extends BlockEntityRenderState {
    public Direction facing;
    public final List<ArmorItemData> items = new ArrayList<>();
    public final List<ItemRenderState> displayItems = List.of(
        new ItemRenderState(),
        new ItemRenderState(),
        new ItemRenderState(),
        new ItemRenderState(),
        new ItemRenderState(),
        new ItemRenderState(),
        new ItemRenderState(),
        new ItemRenderState()
    );

    public record ArmorItemData(
        double xOffset,
        double yOffset,
        double zOffset,
        ItemStack stack
    ) {

    }
}
