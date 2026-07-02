package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.block.furniture.armoires.ArmoireBlock;
import com.chimericdream.minekea.entity.block.furniture.ArmoireBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ArmoireBlockEntityRenderer implements BlockEntityRenderer<ArmoireBlockEntity, ArmoireBlockEntityRenderState> {
    private final BlockEntityRendererProvider.Context context;

    public ArmoireBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public ArmoireBlockEntityRenderState createRenderState() {
        return new ArmoireBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(ArmoireBlockEntity entity, ArmoireBlockEntityRenderState state, float tickProgress, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);
        BlockState blockState = entity.getBlockState();

        List<ItemStack> items = entity.getItems();
        state.facing = blockState.getValue(ArmoireBlock.FACING);

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
            this.context.itemModelResolver().appendItemLayers(state.displayItems.get(lastIdx++), stack, ItemDisplayContext.GROUND, null, null, 0);
        }

        state.items.clear();
        state.items.addAll(stateItems);
    }

    @Override
    public void render(ArmoireBlockEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        Direction facing = state.facing;

        Quaternionf rotation;
        switch (facing) {
            case NORTH -> rotation = Axis.YP.rotationDegrees(0);
            case EAST -> rotation = Axis.YP.rotationDegrees(90);
            case SOUTH -> rotation = Axis.YP.rotationDegrees(180);
            case WEST -> rotation = Axis.YP.rotationDegrees(270);
            default -> throw new IllegalStateException("Armoires cannot face ".concat(facing.toString()).concat("."));
        }

        ItemStackRenderState renderState;
        ArmoireBlockEntityRenderState.ArmorItemData item;
        for (int i = 0; i < state.items.size(); i += 1) {
            item = state.items.get(i);
            renderState = state.displayItems.get(i);

            double xOffset = item.xOffset();
            double yOffset = item.yOffset();
            double zOffset = item.zOffset();

            matrices.pushPose();

            matrices.translate(xOffset, yOffset, zOffset);
            matrices.mulPose(rotation);
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.mulPose(Axis.YP.rotationDegrees(180));

            renderState.submit(matrices, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            matrices.popPose();
        }
    }
}
