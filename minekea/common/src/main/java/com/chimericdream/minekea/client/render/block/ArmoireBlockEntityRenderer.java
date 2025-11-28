package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.block.furniture.armoires.ArmoireBlock;
import com.chimericdream.minekea.entity.block.furniture.ArmoireBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ArmoireBlockEntityRenderer implements BlockEntityRenderer<ArmoireBlockEntity, ArmoireBlockEntityRenderState> {
    private final BlockEntityRendererFactory.Context context;

    public ArmoireBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
    }

    @Override
    public ArmoireBlockEntityRenderState createRenderState() {
        return new ArmoireBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(ArmoireBlockEntity entity, ArmoireBlockEntityRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);
        BlockState blockState = entity.getCachedState();

        List<ItemStack> items = entity.getItems();
        state.facing = blockState.get(ArmoireBlock.FACING);

        List<ArmoireBlockEntityRenderState.ArmorItemData> stateItems = new ArrayList<>();

        ItemStack stack;
        int lastIdx = 0;
        for (int i = 0; i < items.size(); i += 1) {
            // Don't render chestplates or leggings; those are handled elsewhere
            if (i % 4 < 2) {
                continue;
            }

            stack = items.get(i);

            boolean isHelmet = i % 4 == 2;
            int armorStand = Math.floorDiv(i, 4);
            double bootOffset = (isHelmet ? 0 : 1) * 0.1875;

            double xOffset = switch (state.facing) {
                case NORTH -> 0.171875 + (0.21875 * armorStand);
                case SOUTH -> 0.828125 - (0.21875 * armorStand);
                case EAST -> 0.59375 - bootOffset;
                case WEST -> 0.40625 + bootOffset;
                default -> 0.0;
            };

            double yOffset = isHelmet ? 0.5 : 0.078125;

            double zOffset = switch (state.facing) {
                case NORTH -> 0.40625 + bootOffset;
                case SOUTH -> 0.59375 - bootOffset;
                case EAST -> 0.171875 + (0.21875 * armorStand);
                case WEST -> 0.828125 - (0.21875 * armorStand);
                default -> 0.0;
            };

            stateItems.add(new ArmoireBlockEntityRenderState.ArmorItemData(xOffset, yOffset, zOffset, stack));
            this.context.itemModelManager().update(state.displayItems.get(lastIdx++), stack, ItemDisplayContext.GROUND, null, null, 0);
        }

        state.items.clear();
        state.items.addAll(stateItems);
    }

    @Override
    public void render(ArmoireBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        Direction facing = state.facing;

        Quaternionf rotation;
        switch (facing) {
            case NORTH -> rotation = RotationAxis.POSITIVE_Y.rotationDegrees(0);
            case EAST -> rotation = RotationAxis.POSITIVE_Y.rotationDegrees(90);
            case SOUTH -> rotation = RotationAxis.POSITIVE_Y.rotationDegrees(180);
            case WEST -> rotation = RotationAxis.POSITIVE_Y.rotationDegrees(270);
            default -> throw new IllegalStateException("Armoires cannot face ".concat(facing.toString()).concat("."));
        }

        ItemRenderState renderState;
        ArmoireBlockEntityRenderState.ArmorItemData item;
        for (int i = 0; i < state.items.size(); i += 1) {
            item = state.items.get(i);
            renderState = state.displayItems.get(i);

            double xOffset = item.xOffset();
            double yOffset = item.yOffset();
            double zOffset = item.zOffset();

            matrices.push();

            matrices.translate(xOffset, yOffset, zOffset);
            matrices.multiply(rotation);
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

            renderState.render(matrices, queue, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);

            matrices.pop();
        }
    }
}
