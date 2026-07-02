package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.block.containers.GlassJarBlock;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

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

    private final BlockEntityRendererProvider.Context context;

    public GlassJarBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
//        entityRenderer = ctx.getEntityRenderDispatcher();
//        itemRenderer = ctx.getItemRenderer();
        this.context = ctx;
    }

    @Override
    public GlassJarBlockEntityRenderState createRenderState() {
        return new GlassJarBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(GlassJarBlockEntity entity, GlassJarBlockEntityRenderState state, float tickProgress, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);

        state.facing = entity.getBlockState().getValue(GlassJarBlock.FACING);

        state.hasFluid = entity.hasFluid();
        state.hasItem = entity.hasItem();
        state.hasMob = entity.hasMob();

        state.fillLevel = entity.getStoredStacks() + 1;

        state.storedFluid = entity.getStoredFluid();
        state.fluidAmountInBuckets = entity.getStoredBuckets();

        ItemStack storedStack = entity.getStoredItem();
        ItemStack stack = GlassJarBlock.getStackToRender(storedStack);
        this.context.itemModelResolver().appendItemLayers(state.displayItem, stack, ItemDisplayContext.FIXED, null, null, 0);

        TypedEntityData<EntityType<?>> entityData = entity.getStoredMobData();
        String entityId = entity.getStoredMobId();

        Entity mobEntity = null;
        if (entityData != null && entityId != null && entity.getLevel() != null) {
            CompoundTag nbt = entityData.copyTagWithoutId();
            nbt.putString("id", entityId);

            ListTag fakePos = new ListTag();
            fakePos.add(DoubleTag.valueOf(0d));
            fakePos.add(DoubleTag.valueOf(9001d));
            fakePos.add(DoubleTag.valueOf(0d));

            nbt.put("Pos", fakePos);

            nbt.remove("equipment");

            mobEntity = EntityType.loadEntityRecursive(nbt, entity.getLevel(), EntitySpawnReason.SPAWNER, Function.identity());
        }

        if (mobEntity != null) {
            state.mobId = entityId;
            state.mobDisplay = this.context.entityRenderer().extractEntity(mobEntity, 0f);
            state.mobDisplay.lightCoords = state.lightCoords;
            state.mobHeight = mobEntity.getBbHeight();
            state.mobWidth = mobEntity.getBbWidth();
        }
    }

    @Override
    public void render(GlassJarBlockEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (state.hasItem) {
            renderJar(state, matrices, queue);
        } else if (state.hasFluid) {
            Fluid storedFluid = state.storedFluid;

            int color = getFluidColor(storedFluid);
            TextureAtlasSprite fluidTexture = getFluidTexture(storedFluid);

            renderFluidJar(state, color, fluidTexture, matrices, queue);
        } else if (state.hasMob) {
            renderMobJar(state, matrices, queue, this.context.entityRenderer(), cameraState);
        }
    }

    public static void renderJar(GlassJarBlockEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue) {
        int fillLevel = state.fillLevel;
        float fY = (float) fillLevel / (GlassJarBlockEntity.MAX_ITEM_STACKS + 1);

        matrices.pushPose();

        matrices.translate(0.5, (fY * 0.25) + NUDGE, 0.5);
        matrices.scale(0.749f, fY, 0.749f);

        state.displayItem.submit(matrices, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        matrices.popPose();
    }

    public static void renderFluidJar(GlassJarBlockEntityRenderState state, int color, TextureAtlasSprite texture, PoseStack matrices, SubmitNodeCollector queue) {
        queue.submitCustomGeometry(
            matrices,
            RenderType.translucentMovingBlock(),
            new GlassJarFluidRenderCommandQueue(color, texture, state.lightCoords, state.fluidAmountInBuckets)
        );
    }

    public static void renderMobJar(GlassJarBlockEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, EntityRenderDispatcher entityRenderer, CameraRenderState cameraState) {
        if (!state.hasMob) {
            return;
        }

        matrices.pushPose();
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
            matrices.mulPose((new Quaternionf()).rotationY(3.1415927f));
        } else if (facing.equals(Direction.SOUTH)) {
            matrices.mulPose(new Quaternionf());
        } else if (facing.equals(Direction.WEST)) {
            matrices.mulPose((new Quaternionf()).rotationY(-1.5707964f));
        } else {
            matrices.mulPose((new Quaternionf()).rotationY(1.5707964f));
        }

        switch (state.mobId) {
            case "minecraft:allay":
                matrices.translate(0f, 0.125f, 0f);
                break;
            default:
                // no-op
        }

        try {
            entityRenderer.submit(state.mobDisplay, cameraState, 0f, 0f, 0f, matrices, queue);
        } catch (Exception e) {
            MinekeaMod.LOGGER.error("Error while rendering glass jar block entity!", e);
        }

        matrices.popPose();
    }

    abstract protected int getFluidColor(Fluid fluid);

    abstract protected TextureAtlasSprite getFluidTexture(Fluid fluid);
}
