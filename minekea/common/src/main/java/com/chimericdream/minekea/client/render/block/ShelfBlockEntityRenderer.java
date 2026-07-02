package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.block.furniture.shelves.ShelfBlock;
import com.chimericdream.minekea.entity.block.furniture.ShelfBlockEntity;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/*
 * Much of the code in this file was adapted from similar code in the SimpleShelves mod by Pinaz993. They have my
 * gratitude for doing such a good job documenting their code! (also for making it open source!!)
 * Links:
 * https://github.com/Pinaz993/SimpleShelves/blob/master/src/main/java/net/pinaz993/simpleshelves/client/ShelfEntityRenderer.java
 * https://www.curseforge.com/minecraft/mc-mods/simple-shelves
 */
public class ShelfBlockEntityRenderer implements BlockEntityRenderer<ShelfBlockEntity, ShelfBlockEntityRenderState> {
    private final ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
    private final BlockEntityRendererProvider.Context context;

    public ShelfBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public ShelfBlockEntityRenderState createRenderState() {
        return new ShelfBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(ShelfBlockEntity entity, ShelfBlockEntityRenderState state, float tickProgress, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);
        BlockState blockState = entity.getBlockState();

        state.wallSide = blockState.getValue(ShelfBlock.WALL_SIDE);

        NonNullList<ItemStack> items = entity.getItems();
        for (int i = 0; i < items.size(); i += 1) {
            ItemStack stack = items.get(i);
            state.setItem(stack, isBlockItem(stack), false, i);

            if (stack.isEmpty()) {
                state.displayItems[i].clear();
            } else if (isBaggedItem(stack)) {
                stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(9001f), List.of(), List.of(), List.of()));
            }

            this.context.itemModelResolver().appendItemLayers(state.displayItems[i], stack, ItemDisplayContext.FIXED, null, null, 0);
        }
    }


    private boolean isBlockItem(ItemStack stack) {
        return stack.getItem() instanceof BlockItem;
    }

    private boolean isBaggedItem(ItemStack stack) {
        return stack.is(MinekeaItemTags.BAGGED_ITEMS);
    }

//    private boolean isJarItem(ItemStack stack) {
//        return stack.isOf(ContainerBlocks.GLASS_JAR.get().asItem());
//    }

    @Override
    public void render(ShelfBlockEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        Quaternionf rotation;
        Axis axis = Axis.YP;
        switch (state.wallSide) {
            case NORTH -> rotation = axis.rotationDegrees(180);
            case EAST -> rotation = axis.rotationDegrees(90);
            case SOUTH -> rotation = axis.rotationDegrees(0);
            case WEST -> rotation = axis.rotationDegrees(270);
            default ->
                throw new IllegalStateException("Shelves cannot face ".concat(state.wallSide.toString()).concat("."));
        }

        for (int i = 0; i < state.displayItems.length; i += 1) {
            render(state, matrices, queue, rotation, i);
        }
    }

    private void render(
        ShelfBlockEntityRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        Quaternionf rotation,
        int i
    ) {
        double x = 0.125 + (i * 0.25);

        ItemStackRenderState displayItem = state.displayItems[i];
        boolean isBlockItem = state.isBlockItem[i];
        boolean isJarItem = state.isJarItem[i];

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);

        matrices.mulPose(rotation);
        matrices.translate(-0.5, -0.5, -0.5);

        if (isJarItem) {
            matrices.translate(x, 0.785, 0.25);
            matrices.scale(0.9f, 0.9f, 0.9f);
            matrices.mulPose(Axis.YP.rotationDegrees(90));
        } else if (isBlockItem) {
            matrices.translate(x, 0.65, 0.25);
            matrices.scale(0.375f, 0.375f, 0.375f);
        } else {
            matrices.translate(x, 0.7, 0.25);
            matrices.scale(0.25f, 0.25f, 0.25f);
        }

        matrices.mulPose(Axis.YP.rotationDegrees(180));

        displayItem.submit(matrices, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        matrices.popPose();
    }
}
