package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.block.decorations.lighting.Lanterns;
import com.chimericdream.minekea.block.furniture.displaycases.DisplayCaseBlock;
import com.chimericdream.minekea.entity.block.furniture.DisplayCaseBlockEntity;
import com.chimericdream.minekea.item.containers.ContainerItems;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class DisplayCaseBlockEntityRenderer implements BlockEntityRenderer<DisplayCaseBlockEntity, DisplayCaseBlockEntityRenderState> {
    private final BlockEntityRendererFactory.Context context;

    public DisplayCaseBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
    }

    @Override
    public DisplayCaseBlockEntityRenderState createRenderState() {
        return new DisplayCaseBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(DisplayCaseBlockEntity entity, DisplayCaseBlockEntityRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);
        BlockState blockState = entity.getCachedState();

        ItemStack stack = entity.getStack(0);
        ItemDisplayContext displayContext;

        if (stack.isEmpty()) {
            state.clear();
            displayContext = ItemDisplayContext.FIXED;
        } else {
            boolean isBlock = stack.getItem() instanceof BlockItem;

            state.setItem(stack);
            state.rotation = blockState.get(DisplayCaseBlock.ROTATION);
            state.isBlock = isBlock;
            state.distanceToCamera = cameraPos.squaredDistanceTo(entity.getPos().toCenterPos());

            displayContext = isBlock ? ItemDisplayContext.GROUND : ItemDisplayContext.FIXED;

            if (stack.getCustomName() != null) {
                state.setCustomName(stack.getCustomName());
            }

            if (isBaggedItem(stack)) {
                stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(9001f), List.of(), List.of(), List.of()));
            }
        }

        this.context.itemModelManager().update(state.displayItem, stack, displayContext, null, null, 0);
    }

    @Override
    public void render(DisplayCaseBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        matrices.push();

        if (state.hasCustomName) {
            queue.submitLabel(matrices, state.nameLabelPos, 0, state.customName, true, state.lightmapCoordinates, state.distanceToCamera, cameraState);
        }

        boolean isHead = isHeadItem(state.itemId);
        boolean isJar = isJarItem(state.itemId);
        boolean isLantern = isLanternItem(state.itemId);
        int rotation = state.rotation;

        if (state.isBlock) {
            Block block = Registries.BLOCK.get(state.itemId);
            double maxY = block.getOutlineShape(block.getDefaultState(), null, null, null).getMax(Direction.Axis.Y);

            matrices.translate(0.5, 0.65 + Math.min((0.3 * Math.abs(maxY - 1.0)), 0.125), 0.5);

            if (!isHead) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation * 45));
            }
        } else {
            matrices.translate(0.5, 0.85, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation * 45));
            matrices.scale(0.5f, 0.5f, 0.5f);
        }

        // Rotate some items so they face up instead of being half inside the case
        if (isHead || isJar || isLantern) {
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - (rotation * 45)));
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
            matrices.translate(0, -0.0625, 0);
        }

        // A few things need to move up a tiny bit more
        if (isJar || isLantern) {
            matrices.translate(0, 0, 0.09375);
        }

        state.displayItem.render(matrices, queue, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);

        matrices.pop();
    }

    private boolean isBaggedItem(ItemStack stack) {
        return stack.isIn(MinekeaItemTags.BAGGED_ITEMS);
    }

    private boolean isJarItem(Identifier id) {
        if (id == null) {
            return false;
        }

        return id.compareTo(Registries.ITEM.getId(ContainerItems.GLASS_JAR_ITEM.get())) == 0;
    }

    private boolean isHeadItem(Identifier id) {
        if (id == null) {
            return false;
        }

        return
            id.compareTo(Registries.ITEM.getId(Items.PLAYER_HEAD)) == 0
                || id.compareTo(Registries.ITEM.getId(Items.ZOMBIE_HEAD)) == 0
                || id.compareTo(Registries.ITEM.getId(Items.SKELETON_SKULL)) == 0
                || id.compareTo(Registries.ITEM.getId(Items.CREEPER_HEAD)) == 0
                || id.compareTo(Registries.ITEM.getId(Items.WITHER_SKELETON_SKULL)) == 0
                || id.compareTo(Registries.ITEM.getId(Items.PIGLIN_HEAD)) == 0;
    }

    private boolean isLanternItem(Identifier id) {
        if (id == null) {
            return false;
        }

        return
            id.compareTo(Registries.ITEM.getId(Items.LANTERN)) == 0
                || id.compareTo(Registries.ITEM.getId(Items.SOUL_LANTERN)) == 0
                || id.compareTo(Registries.ITEM.getId(Items.COPPER_LANTERNS.exposed())) == 0
                || id.compareTo(Registries.ITEM.getId(Items.COPPER_LANTERNS.oxidized())) == 0
                || id.compareTo(Registries.ITEM.getId(Items.COPPER_LANTERNS.unaffected())) == 0
                || id.compareTo(Registries.ITEM.getId(Items.COPPER_LANTERNS.waxed())) == 0
                || id.compareTo(Registries.ITEM.getId(Items.COPPER_LANTERNS.waxedExposed())) == 0
                || id.compareTo(Registries.ITEM.getId(Items.COPPER_LANTERNS.waxedOxidized())) == 0
                || id.compareTo(Registries.ITEM.getId(Items.COPPER_LANTERNS.waxedWeathered())) == 0
                || id.compareTo(Registries.ITEM.getId(Items.COPPER_LANTERNS.weathered())) == 0
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
