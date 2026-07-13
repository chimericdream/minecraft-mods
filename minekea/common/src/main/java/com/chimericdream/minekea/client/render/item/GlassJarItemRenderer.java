package com.chimericdream.minekea.client.render.item;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.Objects;
import java.util.function.Consumer;

public class GlassJarItemRenderer implements SpecialModelRenderer<GlassJarBlockEntityRenderState> {
    private static final CameraRenderState CAMERA_STATE = new CameraRenderState();

    @Override
    public void submit(@Nullable GlassJarBlockEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, boolean glint, int i) {
        if (state != null) {
            // The render state is cached once per ItemStack (see GlassJarItemEntityCache), so the
            // lightCoords baked in at extraction time reflect wherever the backing entity happened to
            // be in the world, not the item's actual current display context. Overwrite them with the
            // light the item renderer is actually asking us to draw with.
            state.lightCoords = light;
            state.mobDisplay.lightCoords = light;
        }

        matrices.pushPose();
        matrices.translate(1f, 0f, 1f);
        matrices.scale(-1, 1, -1);
        Objects.requireNonNull(Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(state)).submit(state, matrices, queue, CAMERA_STATE);
        matrices.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> vertices) {
    }

    @Override
    public @Nullable GlassJarBlockEntityRenderState extractArgument(ItemStack stack) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return null;
        }

        return GlassJarItemEntityCache.getOrCreate(stack, world);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<GlassJarBlockEntityRenderState> {
        public static final GlassJarItemRenderer.Unbaked INSTANCE = new GlassJarItemRenderer.Unbaked();
        public static final MapCodec<com.chimericdream.minekea.client.render.item.GlassJarItemRenderer.Unbaked> CODEC;

        @Override
        public SpecialModelRenderer<GlassJarBlockEntityRenderState> bake(BakingContext context) {
            return new GlassJarItemRenderer();
        }

        @Override
        public MapCodec<GlassJarItemRenderer.Unbaked> type() {
            return CODEC;
        }

        static {
            CODEC = MapCodec.unit(INSTANCE);
        }
    }
}
