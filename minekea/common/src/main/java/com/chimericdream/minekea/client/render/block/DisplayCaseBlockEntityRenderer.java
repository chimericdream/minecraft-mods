package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.block.furniture.displaycases.DisplayCaseBlock;
import com.chimericdream.minekea.entity.block.furniture.DisplayCaseBlockEntity;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderer;
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
    //    protected final BlockEntityRenderDispatcher dispatcher;
    private final ItemRenderer itemRenderer;
    private final TextRenderer textRenderer;
    private final BlockEntityRendererFactory.Context context;

    public DisplayCaseBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
//        this.dispatcher = ctx.getRenderDispatcher();
        this.itemRenderer = ctx.itemRenderer();
        this.textRenderer = ctx.textRenderer();
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
            state.setRotation(blockState.get(DisplayCaseBlock.ROTATION));
            state.setIsBlock(isBlock);

            displayContext = isBlock ? ItemDisplayContext.GROUND : ItemDisplayContext.FIXED;

            if (isBaggedItem(stack)) {
                stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(9001f), List.of(), List.of(), List.of()));
            }
        }

        this.context.itemModelManager().update(state.getDisplayItem(), stack, displayContext, null, null, 0);
    }

    @Override
    public void render(DisplayCaseBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        matrices.push();

//        if (hasLabel(entity, stack)) {
//            renderLabelIfPresent(entity, stack.getName(), matrices, vertexConsumers, light, tickDelta);
//        }

        boolean isHead = isHeadItem(state.getItem());
        int rotation = state.getRotation();

        if (state.isBlock()) {
            Block block = Registries.BLOCK.get(state.getItem());
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

        // Rotate heads and jars so they face up instead of being half inside the case
        if (isHead/* || isJarItem(stack)*/) {
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - (rotation * 45)));
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
            matrices.translate(0, -0.0625, 0);
        }

        // Jars need to move up a tiny bit more
//        if (isJarItem(stack)) {
//            matrices.translate(0, 0, 0.09375);
//        }

        state.getDisplayItem().render(matrices, queue, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);

        matrices.pop();
    }

    private boolean isBaggedItem(ItemStack stack) {
        return stack.isIn(MinekeaItemTags.BAGGED_ITEMS);
    }

//    private boolean isJarItem(ItemStack stack) {
//        return stack.isOf(ContainerBlocks.GLASS_JAR.get().asItem());
//    }

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
//
//    protected double getSquaredDistanceToCamera(DisplayCaseBlockEntity entity) {
//        return entity.getPos().getSquaredDistance(dispatcher.camera.getPos());
//    }
//
//    protected boolean hasLabel(DisplayCaseBlockEntity entity, ItemStack item) {
//        HitResult target = dispatcher.crosshairTarget;
//        BlockPos targetedPos = BlockPos.ofFloored(target.getPos());
//
//        if (entity.isEmpty()) {
//            return false;
//        }
//
//        if (MinecraftClient.isHudEnabled() && item.contains(DataComponentTypes.CUSTOM_NAME) && entity.getPos().isWithinDistance(targetedPos, 1.5)) {
//            double d = entity.getPos().getSquaredDistance(dispatcher.camera.getPos());
//            float f = 32.0F;
//            return d < (double) (f * f);
//        } else {
//            return false;
//        }
//    }
//
//    public TextRenderer getTextRenderer() {
//        return this.textRenderer;
//    }
//
//    protected void renderLabelIfPresent(DisplayCaseBlockEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta) {
//        double d = getSquaredDistanceToCamera(entity);
//
//        if (!(d > 4096.0)) {
//            float f = 1.5F;
//            int i = "deadmau5".equals(text.getString()) ? -10 : 0;
//            matrices.push();
//            matrices.translate(0.5, f, 0.5);
//            matrices.multiply(dispatcher.camera.getRotation());
//            matrices.scale(-0.025F, -0.025F, 0.025F);
//            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
//            float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
//            int j = (int) (g * 255.0F) << 24;
//            TextRenderer textRenderer = this.getTextRenderer();
//            float h = (float) (-textRenderer.getWidth(text) / 2);
//            textRenderer.draw(text, g, (float) i, 553648127, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, j, light);
//            textRenderer.draw(text, g, (float) i, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
//
//            matrices.pop();
//        }
//    }
}
