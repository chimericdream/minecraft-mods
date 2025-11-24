package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.block.furniture.shelves.ShelfBlock;
import com.chimericdream.minekea.entity.block.furniture.ShelfBlockEntity;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;

/*
 * Much of the code in this file was adapted from similar code in the SimpleShelves mod by Pinaz993. They have my
 * gratitude for doing such a good job documenting their code! (also for making it open source!!)
 * Links:
 * https://github.com/Pinaz993/SimpleShelves/blob/master/src/main/java/net/pinaz993/simpleshelves/client/ShelfEntityRenderer.java
 * https://www.curseforge.com/minecraft/mc-mods/simple-shelves
 */
public class ShelfBlockEntityRenderer implements BlockEntityRenderer<ShelfBlockEntity, ShelfBlockEntityRenderState> {
    private final ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
    private final BlockEntityRendererFactory.Context context;

    public ShelfBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
    }

    @Override
    public ShelfBlockEntityRenderState createRenderState() {
        return new ShelfBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(ShelfBlockEntity entity, ShelfBlockEntityRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);
        BlockState blockState = entity.getCachedState();

        state.wallSide = blockState.get(ShelfBlock.WALL_SIDE);

        DefaultedList<ItemStack> items = entity.getItems();
        for (int i = 0; i < items.size(); i += 1) {
            ItemStack stack = items.get(i);
            state.setItem(stack, isBlockItem(stack), false, i);

            if (stack.isEmpty()) {
                state.displayItems[i].clear();
            } else if (isBaggedItem(stack)) {
                stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(9001f), List.of(), List.of(), List.of()));
            }

            this.context.itemModelManager().update(state.displayItems[i], stack, ItemDisplayContext.FIXED, null, null, 0);
        }
    }


    private boolean isBlockItem(ItemStack stack) {
        return stack.getItem() instanceof BlockItem;
    }

    private boolean isBaggedItem(ItemStack stack) {
        return stack.isIn(MinekeaItemTags.BAGGED_ITEMS);
    }

//    private boolean isJarItem(ItemStack stack) {
//        return stack.isOf(ContainerBlocks.GLASS_JAR.get().asItem());
//    }

    @Override
    public void render(ShelfBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        Quaternionf rotation;
        RotationAxis axis = RotationAxis.POSITIVE_Y;
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
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        Quaternionf rotation,
        int i
    ) {
        double x = 0.125 + (i * 0.25);

        ItemRenderState displayItem = state.displayItems[i];
        boolean isBlockItem = state.isBlockItem[i];
        boolean isJarItem = state.isJarItem[i];

        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);

        matrices.multiply(rotation);
        matrices.translate(-0.5, -0.5, -0.5);

        if (isJarItem) {
            matrices.translate(x, 0.785, 0.25);
            matrices.scale(0.9f, 0.9f, 0.9f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        } else if (isBlockItem) {
            matrices.translate(x, 0.65, 0.25);
            matrices.scale(0.375f, 0.375f, 0.375f);
        } else {
            matrices.translate(x, 0.7, 0.25);
            matrices.scale(0.25f, 0.25f, 0.25f);
        }

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        displayItem.render(matrices, queue, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);

        matrices.pop();
    }
}
