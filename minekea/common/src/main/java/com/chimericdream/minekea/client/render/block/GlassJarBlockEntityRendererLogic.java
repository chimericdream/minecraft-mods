package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.block.containers.GlassJarBlock;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.joml.Quaternionf;

import java.util.Optional;

public abstract class GlassJarBlockEntityRendererLogic {
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

    protected final EntityRenderDispatcher entityRenderer;
    protected final ItemRenderer itemRenderer;

    public GlassJarBlockEntityRendererLogic(BlockEntityRendererFactory.Context ctx) {
        entityRenderer = ctx.getEntityRenderDispatcher();
        itemRenderer = ctx.getItemRenderer();
    }

    public void render(GlassJarBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.hasMob()) {
            renderMob(entity, matrices, vertexConsumers);
        } else if (entity.hasFluid()) {
            renderFluid(entity, matrices, vertexConsumers, light);
        } else if (entity.hasItem()) {
            renderItem(entity, matrices, vertexConsumers);
        }
    }

    protected void renderMob(GlassJarBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        NbtCompound mobData = entity.getStoredMobData();
        if (mobData.isEmpty()) {
            return;
        }

        World world = MinecraftClient.getInstance().world;
        if (world == null) {
            return;
        }

        Optional<Entity> mob = EntityType.getEntityFromNbt(mobData, world);

        if (mob.isEmpty()) {
            return;
        }

        matrices.push();
        matrices.translate(0.5f, 0f, 0.5f);
        float f = 0.34375f;
        float g = Math.max(mob.get().getWidth(), mob.get().getHeight());
        if ((double) g > 1.0) {
            f /= g;
        }

        int light = entity.getWorld() == null ? 15728880 : WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());

        matrices.translate(0.0F, 0.4F, 0.0F);
        matrices.translate(0.0F, -0.3125F, 0.0F);
        matrices.scale(f, f, f);

        if (entity.getWorld() != null) {
            Direction facing = entity.getCachedState().get(GlassJarBlock.FACING);
            if (facing.equals(Direction.NORTH)) {
                matrices.multiply((new Quaternionf()).rotationY(3.1415927f));
            } else if (facing.equals(Direction.SOUTH)) {
                matrices.multiply(new Quaternionf());
            } else if (facing.equals(Direction.WEST)) {
                matrices.multiply((new Quaternionf()).rotationY(-1.5707964f));
            } else {
                matrices.multiply((new Quaternionf()).rotationY(1.5707964f));
            }
        }

        entityRenderer.render(mob.get(), 0.0, 0.0, 0.0, 0.0f, 0.0f, matrices, vertexConsumers, light);
        matrices.pop();
    }

    protected void renderFluidTexture(
        Sprite texture,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        float fluidTop,
        int color,
        int light
    ) {
        VertexConsumer translucentBuffer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());
        MatrixStack.Entry worldMatrix = matrices.peek();

        float colorR = (float) (color >> 16 & 255) / 255.0F;
        float colorG = (float) (color >> 8 & 255) / 255.0F;
        float colorB = (float) (color & 255) / 255.0F;
        float xColorR = colorR * xLightFactor;
        float xColorG = colorG * xLightFactor;
        float xColorB = colorB * xLightFactor;
        float yColorR = colorR * yLightFactor;
        float yColorG = colorG * yLightFactor;
        float yColorB = colorB * yLightFactor;
        float zColorR = colorR * zLightFactor;
        float zColorG = colorG * zLightFactor;
        float zColorB = colorB * zLightFactor;

        matrices.push();

        // east (x+)
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, 0f, 1f - HORIZONTAL_OFFSET)
            .color(xColorR, xColorG, xColorB, 1f)
            .texture(texture.getMinU(), texture.getMinV())
            .light(light)
            .normal(1f, 0f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, 0f, 0f + HORIZONTAL_OFFSET)
            .color(xColorR, xColorG, xColorB, 1f)
            .texture(texture.getMaxU(), texture.getMinV())
            .light(light)
            .normal(1f, 0f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, fluidTop, 0f + HORIZONTAL_OFFSET)
            .color(xColorR, xColorG, xColorB, 1f)
            .texture(texture.getMaxU(), texture.getMaxV())
            .light(light)
            .normal(1f, 0f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, fluidTop, 1f - HORIZONTAL_OFFSET)
            .color(xColorR, xColorG, xColorB, 1f)
            .texture(texture.getMaxU(), texture.getMaxV())
            .light(light)
            .normal(1f, 0f, 0f);

        // west (x-)
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, 0f, 0f + HORIZONTAL_OFFSET)
            .color(xColorR, xColorG, xColorB, 1f)
            .texture(texture.getMinU(), texture.getMinV())
            .light(light)
            .normal(-1f, 0f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, 0f, 1f - HORIZONTAL_OFFSET)
            .color(xColorR, xColorG, xColorB, 1f)
            .texture(texture.getMaxU(), texture.getMinV())
            .light(light)
            .normal(-1f, 0f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, fluidTop, 1f - HORIZONTAL_OFFSET)
            .color(xColorR, xColorG, xColorB, 1f)
            .texture(texture.getMaxU(), texture.getMaxV())
            .light(light)
            .normal(-1f, 0f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, fluidTop, 0f + HORIZONTAL_OFFSET)
            .color(xColorR, xColorG, xColorB, 1f)
            .texture(texture.getMaxU(), texture.getMaxV())
            .light(light)
            .normal(-1f, 0f, 0f);

        // south (z+)
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, 0f, 1f - HORIZONTAL_OFFSET)
            .color(zColorR, zColorG, zColorB, 1f)
            .texture(texture.getMinU(), texture.getMinV())
            .light(light)
            .normal(0f, 0f, 1f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, 0f, 1f - HORIZONTAL_OFFSET)
            .color(zColorR, zColorG, zColorB, 1f)
            .texture(texture.getMaxU(), texture.getMinV())
            .light(light)
            .normal(0f, 0f, 1f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, fluidTop, 1f - HORIZONTAL_OFFSET)
            .color(zColorR, zColorG, zColorB, 1f)
            .texture(texture.getMaxU(), texture.getMaxV())
            .light(light)
            .normal(0f, 0f, 1f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, fluidTop, 1f - HORIZONTAL_OFFSET)
            .color(zColorR, zColorG, zColorB, 1f)
            .texture(texture.getMaxU(), texture.getMaxV())
            .light(light)
            .normal(0f, 0f, 1f);

        // north (z-)
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, 0f, 0f + HORIZONTAL_OFFSET)
            .color(zColorR, zColorG, zColorB, 1f)
            .texture(texture.getMinU(), texture.getMinV())
            .light(light)
            .normal(0f, 0f, -1f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, 0f, 0f + HORIZONTAL_OFFSET)
            .color(zColorR, zColorG, zColorB, 1f)
            .texture(texture.getMaxU(), texture.getMinV())
            .light(light)
            .normal(0f, 0f, -1f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, fluidTop, 0f + HORIZONTAL_OFFSET)
            .color(zColorR, zColorG, zColorB, 1f)
            .texture(texture.getMaxU(), texture.getMaxV())
            .light(light)
            .normal(0f, 0f, -1f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, fluidTop, 0f + HORIZONTAL_OFFSET)
            .color(zColorR, zColorG, zColorB, 1f)
            .texture(texture.getMaxU(), texture.getMaxV())
            .light(light)
            .normal(0f, 0f, -1f);

        // up (y+)
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, fluidTop - NUDGE, 1f - HORIZONTAL_OFFSET)
            .color(colorR, colorG, colorB, 1f)
            .texture(texture.getMinU(), texture.getMinV())
            .light(light)
            .normal(0f, 1f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, fluidTop - NUDGE, 1f - HORIZONTAL_OFFSET)
            .color(colorR, colorG, colorB, 1f)
            .texture(texture.getMaxU(), texture.getMinV())
            .light(light)
            .normal(0f, 1f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, fluidTop - NUDGE, 0f + HORIZONTAL_OFFSET)
            .color(colorR, colorG, colorB, 1f)
            .texture(texture.getMaxU(), texture.getMaxV())
            .light(light)
            .normal(0f, 1f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, fluidTop - NUDGE, 0f + HORIZONTAL_OFFSET)
            .color(colorR, colorG, colorB, 1f)
            .texture(texture.getMinU(), texture.getMaxV())
            .light(light)
            .normal(0f, 1f, 0f);

        // down (y-)
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, 0f + NUDGE, 0f + HORIZONTAL_OFFSET)
            .color(yColorR, yColorG, yColorB, 1f)
            .texture(texture.getMinU(), texture.getMinV())
            .light(light)
            .normal(0f, -1f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, 0f + NUDGE, 0f + HORIZONTAL_OFFSET)
            .color(yColorR, yColorG, yColorB, 1f)
            .texture(texture.getMaxU(), texture.getMinV())
            .light(light)
            .normal(0f, -1f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 1f - HORIZONTAL_OFFSET, 0f + NUDGE, 1f - HORIZONTAL_OFFSET)
            .color(yColorR, yColorG, yColorB, 1f)
            .texture(texture.getMaxU(), texture.getMaxV())
            .light(light)
            .normal(0f, -1f, 0f);
        translucentBuffer
            .vertex(worldMatrix.getPositionMatrix(), 0f + HORIZONTAL_OFFSET, 0f + NUDGE, 1f - HORIZONTAL_OFFSET)
            .color(yColorR, yColorG, yColorB, 1f)
            .texture(texture.getMinU(), texture.getMaxV())
            .light(light)
            .normal(0f, -1f, 0f);

        matrices.pop();
    }

    protected void renderFluid(GlassJarBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Fluid storedFluid = entity.getStoredFluid();
        if (storedFluid.matchesType(Fluids.EMPTY)) {
            return;
        }

        int color = getFluidColor(storedFluid);
        Sprite fluidTexture = getFluidTexture(storedFluid);

        double fillLevel = entity.getStoredBuckets();

        float fluidTop = (((float) fillLevel / GlassJarBlockEntity.MAX_BUCKETS) * VERTICAL_MULTIPLIER) - NUDGE;

        renderFluidTexture(fluidTexture, matrices, vertexConsumers, fluidTop, color, light);
    }

    protected void renderItem(GlassJarBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        World world = entity.getWorld();
        BlockPos pos = entity.getPos();

        ItemStack storedStack = entity.getStack(0);
        if (storedStack.isEmpty()) {
            return;
        }

        ItemStack stack = GlassJarBlock.getStackToRender(storedStack);

        if (stack.isEmpty()) {
            return;
        }

        int fillLevel = entity.getStoredStacks() + 1;
        float fY = (float) fillLevel / (GlassJarBlockEntity.MAX_ITEM_STACKS + 1);

        matrices.push();

        matrices.translate(0.5, (fY * 0.25) + NUDGE, 0.5);
        matrices.scale(0.749f, fY, 0.749f);

        int lightAbove = world == null ? 15728880 : WorldRenderer.getLightmapCoordinates(world, pos.up());
        itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, null, 0);

        matrices.pop();
    }

    abstract protected int getFluidColor(Fluid fluid);

    abstract protected Sprite getFluidTexture(Fluid fluid);
}
