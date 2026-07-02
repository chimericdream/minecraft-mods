package com.chimericdream.minekea.client.render.block;

import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.HORIZONTAL_OFFSET;
import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.NUDGE;
import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.VERTICAL_MULTIPLIER;
import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.xLightFactor;
import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.yLightFactor;
import static com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer.zLightFactor;

public class GlassJarFluidRenderCommandQueue implements SubmitNodeCollector.CustomGeometryRenderer {
    private final int color;
    private final int light;
    private final double fillLevel;
    private final TextureAtlasSprite texture;

    public GlassJarFluidRenderCommandQueue(int color, TextureAtlasSprite texture, int light, double fillLevel) {
        this.color = color;
        this.fillLevel = fillLevel;
        this.light = light;
        this.texture = texture;
    }

    @Override
    public void render(PoseStack.Pose worldMatrix, VertexConsumer translucentBuffer) {
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
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, 0f, 1f - HORIZONTAL_OFFSET)
            .setColor(xColorR, xColorG, xColorB, 1f)
            .setUv(texture.getU0(), texture.getV0())
            .setLight(light)
            .setNormal(1f, 0f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, 0f, 0f + HORIZONTAL_OFFSET)
            .setColor(xColorR, xColorG, xColorB, 1f)
            .setUv(texture.getU1(), texture.getV0())
            .setLight(light)
            .setNormal(1f, 0f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, fluidTop, 0f + HORIZONTAL_OFFSET)
            .setColor(xColorR, xColorG, xColorB, 1f)
            .setUv(texture.getU1(), texture.getV1())
            .setLight(light)
            .setNormal(1f, 0f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, fluidTop, 1f - HORIZONTAL_OFFSET)
            .setColor(xColorR, xColorG, xColorB, 1f)
            .setUv(texture.getU1(), texture.getV1())
            .setLight(light)
            .setNormal(1f, 0f, 0f);

        // west (x-)
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, 0f, 0f + HORIZONTAL_OFFSET)
            .setColor(xColorR, xColorG, xColorB, 1f)
            .setUv(texture.getU0(), texture.getV0())
            .setLight(light)
            .setNormal(-1f, 0f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, 0f, 1f - HORIZONTAL_OFFSET)
            .setColor(xColorR, xColorG, xColorB, 1f)
            .setUv(texture.getU1(), texture.getV0())
            .setLight(light)
            .setNormal(-1f, 0f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, fluidTop, 1f - HORIZONTAL_OFFSET)
            .setColor(xColorR, xColorG, xColorB, 1f)
            .setUv(texture.getU1(), texture.getV1())
            .setLight(light)
            .setNormal(-1f, 0f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, fluidTop, 0f + HORIZONTAL_OFFSET)
            .setColor(xColorR, xColorG, xColorB, 1f)
            .setUv(texture.getU1(), texture.getV1())
            .setLight(light)
            .setNormal(-1f, 0f, 0f);

        // south (z+)
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, 0f, 1f - HORIZONTAL_OFFSET)
            .setColor(zColorR, zColorG, zColorB, 1f)
            .setUv(texture.getU0(), texture.getV0())
            .setLight(light)
            .setNormal(0f, 0f, 1f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, 0f, 1f - HORIZONTAL_OFFSET)
            .setColor(zColorR, zColorG, zColorB, 1f)
            .setUv(texture.getU1(), texture.getV0())
            .setLight(light)
            .setNormal(0f, 0f, 1f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, fluidTop, 1f - HORIZONTAL_OFFSET)
            .setColor(zColorR, zColorG, zColorB, 1f)
            .setUv(texture.getU1(), texture.getV1())
            .setLight(light)
            .setNormal(0f, 0f, 1f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, fluidTop, 1f - HORIZONTAL_OFFSET)
            .setColor(zColorR, zColorG, zColorB, 1f)
            .setUv(texture.getU1(), texture.getV1())
            .setLight(light)
            .setNormal(0f, 0f, 1f);

        // north (z-)
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, 0f, 0f + HORIZONTAL_OFFSET)
            .setColor(zColorR, zColorG, zColorB, 1f)
            .setUv(texture.getU0(), texture.getV0())
            .setLight(light)
            .setNormal(0f, 0f, -1f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, 0f, 0f + HORIZONTAL_OFFSET)
            .setColor(zColorR, zColorG, zColorB, 1f)
            .setUv(texture.getU1(), texture.getV0())
            .setLight(light)
            .setNormal(0f, 0f, -1f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, fluidTop, 0f + HORIZONTAL_OFFSET)
            .setColor(zColorR, zColorG, zColorB, 1f)
            .setUv(texture.getU1(), texture.getV1())
            .setLight(light)
            .setNormal(0f, 0f, -1f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, fluidTop, 0f + HORIZONTAL_OFFSET)
            .setColor(zColorR, zColorG, zColorB, 1f)
            .setUv(texture.getU1(), texture.getV1())
            .setLight(light)
            .setNormal(0f, 0f, -1f);

        // up (y+)
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, fluidTop - NUDGE, 1f - HORIZONTAL_OFFSET)
            .setColor(colorR, colorG, colorB, 1f)
            .setUv(texture.getU0(), texture.getV0())
            .setLight(light)
            .setNormal(0f, 1f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, fluidTop - NUDGE, 1f - HORIZONTAL_OFFSET)
            .setColor(colorR, colorG, colorB, 1f)
            .setUv(texture.getU1(), texture.getV0())
            .setLight(light)
            .setNormal(0f, 1f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, fluidTop - NUDGE, 0f + HORIZONTAL_OFFSET)
            .setColor(colorR, colorG, colorB, 1f)
            .setUv(texture.getU1(), texture.getV1())
            .setLight(light)
            .setNormal(0f, 1f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, fluidTop - NUDGE, 0f + HORIZONTAL_OFFSET)
            .setColor(colorR, colorG, colorB, 1f)
            .setUv(texture.getU0(), texture.getV1())
            .setLight(light)
            .setNormal(0f, 1f, 0f);

        // down (y-)
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, 0f + NUDGE, 0f + HORIZONTAL_OFFSET)
            .setColor(yColorR, yColorG, yColorB, 1f)
            .setUv(texture.getU0(), texture.getV0())
            .setLight(light)
            .setNormal(0f, -1f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, 0f + NUDGE, 0f + HORIZONTAL_OFFSET)
            .setColor(yColorR, yColorG, yColorB, 1f)
            .setUv(texture.getU1(), texture.getV0())
            .setLight(light)
            .setNormal(0f, -1f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 1f - HORIZONTAL_OFFSET, 0f + NUDGE, 1f - HORIZONTAL_OFFSET)
            .setColor(yColorR, yColorG, yColorB, 1f)
            .setUv(texture.getU1(), texture.getV1())
            .setLight(light)
            .setNormal(0f, -1f, 0f);
        translucentBuffer
            .addVertex(worldMatrix.pose(), 0f + HORIZONTAL_OFFSET, 0f + NUDGE, 1f - HORIZONTAL_OFFSET)
            .setColor(yColorR, yColorG, yColorB, 1f)
            .setUv(texture.getU0(), texture.getV1())
            .setLight(light)
            .setNormal(0f, -1f, 0f);
    }
}
