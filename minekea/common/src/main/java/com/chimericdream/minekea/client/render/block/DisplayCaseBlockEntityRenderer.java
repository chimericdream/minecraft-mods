package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.block.decorations.lighting.Lanterns;
import com.chimericdream.minekea.block.furniture.displaycases.DisplayCaseBlock;
import com.chimericdream.minekea.entity.block.furniture.DisplayCaseBlockEntity;
import com.chimericdream.minekea.item.containers.ContainerItems;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class DisplayCaseBlockEntityRenderer implements BlockEntityRenderer<DisplayCaseBlockEntity, DisplayCaseBlockEntityRenderState> {
    private final BlockEntityRendererProvider.Context context;

    public DisplayCaseBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public @NotNull DisplayCaseBlockEntityRenderState createRenderState() {
        return new DisplayCaseBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(DisplayCaseBlockEntity entity, DisplayCaseBlockEntityRenderState state, float tickProgress, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);
        BlockState blockState = entity.getBlockState();

        ItemStack stack = entity.getItem(0);
        ItemDisplayContext displayContext;

        if (stack.isEmpty()) {
            state.clear();
            displayContext = ItemDisplayContext.FIXED;
        } else {
            boolean isBlock = stack.getItem() instanceof BlockItem;

            state.setItem(stack);
            state.rotation = blockState.getValue(DisplayCaseBlock.ROTATION);
            state.isBlock = isBlock;
            state.distanceToCamera = cameraPos.distanceToSqr(entity.getBlockPos().getCenter());

            displayContext = isBlock ? ItemDisplayContext.GROUND : ItemDisplayContext.FIXED;

            if (stack.getCustomName() != null) {
                state.setCustomName(stack.getCustomName());
            }

            if (isBaggedItem(stack)) {
                stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(9001f), List.of(), List.of(), List.of()));
            }
        }

        this.context.itemModelResolver().appendItemLayers(state.displayItem, stack, displayContext, null, null, 0);
    }

    @Override
    public void submit(DisplayCaseBlockEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        matrices.pushPose();

        if (state.hasCustomName) {
            queue.submitNameTag(matrices, state.nameLabelPos, 0, state.customName, true, state.lightCoords, state.distanceToCamera, cameraState);
        }

        boolean isHead = isHeadItem(state.itemId);
        boolean isJar = isJarItem(state.itemId);
        boolean isLantern = isLanternItem(state.itemId);
        int rotation = state.rotation;

        if (state.isBlock) {
            Block block = BuiltInRegistries.BLOCK.getValue(state.itemId);
            double maxY = block.getShape(block.defaultBlockState(), null, null, null).max(Direction.Axis.Y);

            matrices.translate(0.5, 0.65 + Math.min((0.3 * Math.abs(maxY - 1.0)), 0.125), 0.5);

            if (!isHead) {
                matrices.mulPose(Axis.YP.rotationDegrees(rotation * 45));
            }
        } else {
            matrices.translate(0.5, 0.85, 0.5);
            matrices.mulPose(Axis.XP.rotationDegrees(90));
            matrices.mulPose(Axis.ZP.rotationDegrees(rotation * 45));
            matrices.scale(0.5f, 0.5f, 0.5f);
        }

        // Rotate some items so they face up instead of being half inside the case
        if (isHead || isJar || isLantern) {
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            matrices.mulPose(Axis.YP.rotationDegrees(180 - (rotation * 45)));
            matrices.mulPose(Axis.XN.rotationDegrees(90));
            matrices.translate(0, -0.0625, 0);
        }

        // A few things need to move up a tiny bit more
        if (isJar || isLantern) {
            matrices.translate(0, 0, 0.09375);
        }

        state.displayItem.submit(matrices, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        matrices.popPose();
    }

    private boolean isBaggedItem(ItemStack stack) {
        return stack.is(MinekeaItemTags.BAGGED_ITEMS);
    }

    private boolean isJarItem(Identifier id) {
        if (id == null) {
            return false;
        }

        return id.compareTo(BuiltInRegistries.ITEM.getKey(ContainerItems.GLASS_JAR_ITEM.get())) == 0;
    }

    private boolean isHeadItem(Identifier id) {
        if (id == null) {
            return false;
        }

        return
            id.compareTo(BuiltInRegistries.ITEM.getKey(Items.PLAYER_HEAD)) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.ZOMBIE_HEAD)) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.SKELETON_SKULL)) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.CREEPER_HEAD)) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.WITHER_SKELETON_SKULL)) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.PIGLIN_HEAD)) == 0;
    }

    private boolean isLanternItem(Identifier id) {
        if (id == null) {
            return false;
        }

        return
            id.compareTo(BuiltInRegistries.ITEM.getKey(Items.LANTERN)) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.SOUL_LANTERN)) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.COPPER_LANTERN.exposed())) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.COPPER_LANTERN.oxidized())) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.COPPER_LANTERN.unaffected())) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.COPPER_LANTERN.waxed())) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.COPPER_LANTERN.waxedExposed())) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.COPPER_LANTERN.waxedOxidized())) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.COPPER_LANTERN.waxedWeathered())) == 0
                || id.compareTo(BuiltInRegistries.ITEM.getKey(Items.COPPER_LANTERN.weathered())) == 0
                || Lanterns.BLOCKS.stream().anyMatch(block -> id.compareTo(block.getId()) == 0);
    }

//    @Override
//    public void render(DisplayCaseBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
//        BlockState state = entity.getCachedState();
//
//        if (entity.isEmpty()) {
//            return;
//        }
//
//        ItemStack stack = entity.getStack(0);
//
//        if (hasLabel(entity, stack)) {
//            renderLabelIfPresent(entity, stack.getName(), matrices, vertexConsumers, light, tickDelta);
//        }
//
//        int rotation = state.get(DisplayCaseBlock.ROTATION);
//
//        matrices.push();
//
//        Identifier id = Registries.ITEM.getId(stack.getItem());
//        boolean isBlock = isBlockItem(stack);
//
//        if (isBlock) {
//            Block block = Registries.BLOCK.get(id);
//            double maxY = block.getOutlineShape(block.getDefaultState(), entity.getWorld(), entity.getPos(), null).getMax(Direction.Axis.Y);
//
//            matrices.translate(0.5, 0.65 + Math.min((0.3 * Math.abs(maxY - 1.0)), 0.125), 0.5);
//
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation * 45));
//        } else {
//            matrices.translate(0.5, 0.85, 0.5);
//            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
//            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation * 45));
//            matrices.scale(0.5f, 0.5f, 0.5f);
//        }
//
//        // Rotate heads and jars so they face up instead of being half inside the case
//        if (isHeadItem(id) || isJarItem(stack)) {
//            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
//        }
//
//        // Jars need to move up a tiny bit more
//        if (isJarItem(stack)) {
//            matrices.translate(0, 0, 0.09375);
//        }
//
//        ModelTransformationMode mode = isBlock ? ModelTransformationMode.GROUND : ModelTransformationMode.FIXED;
//
//        if (isBaggedItem(stack)) {
//            stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(9001));
//        }
//
//        itemRenderer.renderItem(stack, mode, light, overlay, matrices, vertexConsumers, null, 0);
//
//        matrices.pop();
//    }
}
