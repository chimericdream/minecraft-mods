package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.block.containers.GlassJarBlock;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.function.Function;

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

        state.facing = entity.getCachedState().get(GlassJarBlock.FACING);

        state.hasFluid = entity.hasFluid();
        state.hasItem = entity.hasItem();
        state.hasMob = entity.hasMob();

        state.fillLevel = entity.getStoredStacks() + 1;

        state.storedFluid = entity.getStoredFluid();
        state.fluidAmountInBuckets = entity.getStoredBuckets();

        ItemStack storedStack = entity.getStoredItem();
        ItemStack stack = GlassJarBlock.getStackToRender(storedStack);
        this.context.itemModelManager().update(state.displayItem, stack, ItemDisplayContext.FIXED, null, null, 0);

        TypedEntityData<EntityType<?>> entityData = entity.getStoredMobData();
        String entityId = entity.getStoredMobId();

        Entity mobEntity = null;
        if (entityData != null && entityId != null && entity.getWorld() != null) {
            NbtCompound nbt = entityData.copyNbtWithoutId();
            nbt.putString("id", entityId);

            NbtList fakePos = new NbtList();
            fakePos.add(NbtDouble.of(0d));
            fakePos.add(NbtDouble.of(9001d));
            fakePos.add(NbtDouble.of(0d));

            nbt.put("Pos", fakePos);

            nbt.remove("equipment");

            mobEntity = EntityType.loadEntityWithPassengers(nbt, entity.getWorld(), SpawnReason.SPAWNER, Function.identity());
        }

        if (mobEntity != null) {
            state.mobId = entityId;
            state.mobDisplay = this.context.entityRenderDispatcher().getAndUpdateRenderState(mobEntity, 0f);
            state.mobDisplay.light = state.lightmapCoordinates;
            state.mobHeight = mobEntity.getHeight();
            state.mobWidth = mobEntity.getWidth();
        }
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
        } else if (state.hasMob) {
            renderMobJar(state, matrices, queue, this.context.entityRenderDispatcher(), cameraState);
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

    public static void renderMobJar(GlassJarBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, EntityRenderManager entityRenderer, CameraRenderState cameraState) {
        if (!state.hasMob) {
            return;
        }

        matrices.push();
        matrices.translate(0.5f, 0f, 0.5f);
        float f = 0.34375f;
        float g = Math.max(state.mobWidth, state.mobHeight);
        if ((double) g > 1.0) {
            f /= g;
        }

        matrices.translate(0f, 0.4f, 0f);
        matrices.translate(0f, -0.3125f, 0f);
        matrices.scale(f, f, f);

        Direction facing = state.facing;
        if (facing.equals(Direction.NORTH)) {
            matrices.multiply((new Quaternionf()).rotationY(3.1415927f));
        } else if (facing.equals(Direction.SOUTH)) {
            matrices.multiply(new Quaternionf());
        } else if (facing.equals(Direction.WEST)) {
            matrices.multiply((new Quaternionf()).rotationY(-1.5707964f));
        } else {
            matrices.multiply((new Quaternionf()).rotationY(1.5707964f));
        }

        switch (state.mobId) {
            case "minecraft:allay":
                matrices.translate(0f, 0.125f, 0f);
                break;
            default:
                // no-op
        }

        try {
            entityRenderer.render(state.mobDisplay, cameraState, 0f, 0f, 0f, matrices, queue);
        } catch (Exception e) {
            MinekeaMod.LOGGER.error("Error while rendering glass jar block entity!", e);
        }

        matrices.pop();
    }

    abstract protected int getFluidColor(Fluid fluid);

    abstract protected Sprite getFluidTexture(Fluid fluid);
}
