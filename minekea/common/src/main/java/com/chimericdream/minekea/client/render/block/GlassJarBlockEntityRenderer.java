package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.block.containers.GlassJarBlock;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

abstract public class GlassJarBlockEntityRenderer implements BlockEntityRenderer<GlassJarBlockEntity, GlassJarBlockEntityRenderState> {
    protected static final float yLightFactor = 0.5f;
    protected static final float zLightFactor = 0.8f;
    protected static final float xLightFactor = 0.6f;

    // Prevents z-fighting when the textures would otherwise be touching
    protected static final float NUDGE = 0.0001f;
    // Ensures that textures start at the appropriate distance from the block's edge
    protected static final float EDGE_OFFSET = 5f / 16f;
    protected static final float HORIZONTAL_OFFSET = NUDGE + EDGE_OFFSET;
    // Ensures that the total height of the contents doesn't go above the top
    protected static final float VERTICAL_MULTIPLIER = 9f / 16f;

    //    protected final EntityRenderDispatcher entityRenderer;
//    protected final ItemRenderer itemRenderer;
    private final BlockEntityRendererFactory.Context context;

    public GlassJarBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
//        entityRenderer = ctx.getEntityRenderDispatcher();
//        itemRenderer = ctx.getItemRenderer();
        this.context = ctx;
    }

    @Override
    public GlassJarBlockEntityRenderState createRenderState() {
        return new GlassJarBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(GlassJarBlockEntity entity, GlassJarBlockEntityRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);
        state.hasItem = entity.hasItem();
        state.fillLevel = entity.getStoredStacks() + 1;

        state.storedFluid = entity.getStoredFluid();
        state.fluidAmountInBuckets = entity.getStoredBuckets();
        state.hasFluid = entity.hasFluid();

        ItemStack storedStack = entity.getStoredItem();
        ItemStack stack = GlassJarBlock.getStackToRender(storedStack);
        this.context.itemModelManager().update(state.displayItem, stack, ItemDisplayContext.FIXED, null, null, 0);
    }

    @Override
    public void render(GlassJarBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        if (state.hasItem) {
            renderJar(state, matrices, queue);
        } else if (state.hasFluid) {
            Fluid storedFluid = state.storedFluid;

            int color = getFluidColor(storedFluid);
            Sprite fluidTexture = getFluidTexture(storedFluid);

            renderFluidJar(state, color, fluidTexture, matrices, queue);
        }
    }

    public static void renderJar(GlassJarBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue) {
        int fillLevel = state.fillLevel;
        float fY = (float) fillLevel / (GlassJarBlockEntity.MAX_ITEM_STACKS + 1);

        matrices.push();

        matrices.translate(0.5, (fY * 0.25) + NUDGE, 0.5);
        matrices.scale(0.749f, fY, 0.749f);

        state.displayItem.render(matrices, queue, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);

        matrices.pop();
    }

    public static void renderFluidJar(GlassJarBlockEntityRenderState state, int color, Sprite texture, MatrixStack matrices, OrderedRenderCommandQueue queue) {
        queue.submitCustom(
            matrices,
            RenderLayer.getTranslucentMovingBlock(),
            new GlassJarFluidRenderCommandQueue(color, texture, state.lightmapCoordinates, state.fluidAmountInBuckets)
        );
    }

//    public void render(GlassJarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
//        if (entity.hasMob()) {
//            renderMob(entity, matrices, vertexConsumers);
//        } else if (entity.hasFluid()) {
//            renderFluid(entity, matrices, vertexConsumers, light);
//        } else if (entity.hasItem()) {
//        if (entity.hasItem()) {
//            renderItem(entity, matrices, vertexConsumers);
//        }
//    }

//    protected void renderMob(GlassJarBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
//        TypedEntityData<EntityType<?>> mobData = entity.getStoredMobData();
//        if (mobData == null) {
//            return;
//        }
//
//        World world = MinecraftClient.getInstance().world;
//        if (world == null) {
//            return;
//        }
//
//        Entity mob = mobData.getType().create(world, SpawnReason.CONVERSION);
//        EntityType.loadFromEntityNbt(world, null, mob, mobData);
//
//        if (mob == null) {
//            return;
//        }
//
//        matrices.push();
//        matrices.translate(0.5f, 0f, 0.5f);
//        float f = 0.34375f;
//        float g = Math.max(mob.getWidth(), mob.getHeight());
//        if ((double) g > 1.0) {
//            f /= g;
//        }
//
//        int light = entity.getWorld() == null ? 15728880 : WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
//
//        matrices.translate(0.0F, 0.4F, 0.0F);
//        matrices.translate(0.0F, -0.3125F, 0.0F);
//        matrices.scale(f, f, f);
//
//        if (entity.getWorld() != null) {
//            Direction facing = entity.getCachedState().get(GlassJarBlock.FACING);
//            if (facing.equals(Direction.NORTH)) {
//                matrices.multiply((new Quaternionf()).rotationY(3.1415927f));
//            } else if (facing.equals(Direction.SOUTH)) {
//                matrices.multiply(new Quaternionf());
//            } else if (facing.equals(Direction.WEST)) {
//                matrices.multiply((new Quaternionf()).rotationY(-1.5707964f));
//            } else {
//                matrices.multiply((new Quaternionf()).rotationY(1.5707964f));
//            }
//        }
//
//        entityRenderer.render(mob, 0.0, 0.0, 0.0, 0.0f, 0.0f, matrices, vertexConsumers, light);
//        matrices.pop();
//    }

//    protected void renderItem(GlassJarBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
//        World world = entity.getWorld();
//        BlockPos pos = entity.getPos();
//
//        ItemStack storedStack = entity.getStack(0);
//        if (storedStack.isEmpty()) {
//            return;
//        }
//
//        ItemStack stack = GlassJarBlock.getStackToRender(storedStack);
//
//        if (stack.isEmpty()) {
//            return;
//        }
//
//        int fillLevel = entity.getStoredStacks() + 1;
//        float fY = (float) fillLevel / (GlassJarBlockEntity.MAX_ITEM_STACKS + 1);
//
//        matrices.push();
//
//        matrices.translate(0.5, (fY * 0.25) + NUDGE, 0.5);
//        matrices.scale(0.749f, fY, 0.749f);
//
//        int lightAbove = world == null ? 15728880 : WorldRenderer.getLightmapCoordinates(world, pos.up());
//        itemRenderer.renderItem(stack, ItemDisplayContext.FIXED, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, null, 0);
//
//        matrices.pop();
//    }

    abstract protected int getFluidColor(Fluid fluid);

    abstract protected Sprite getFluidTexture(Fluid fluid);
}
