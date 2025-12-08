package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;

import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.HORIZONTAL_OFFSET;
import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.NUDGE;
import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.VERTICAL_MULTIPLIER;
import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.xLightFactor;
import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.yLightFactor;
import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.zLightFactor;

public class GlassJarFluidRenderCommandQueue implements OrderedRenderCommandQueue.Custom {
    private final int color;
    private final int light;
    private final double fillLevel;
    private final Sprite texture;

    public GlassJarFluidRenderCommandQueue(int color, Sprite texture, int light, double fillLevel) {
        this.color = color;
        this.fillLevel = fillLevel;
        this.light = light;
        this.texture = texture;
    }

    @Override
    public void render(MatrixStack.Entry worldMatrix, VertexConsumer translucentBuffer) {
        float fluidTop = (((float) fillLevel / GlassJarBlockEntity.MAX_BUCKETS) * VERTICAL_MULTIPLIER) - NUDGE;

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
    }
}
