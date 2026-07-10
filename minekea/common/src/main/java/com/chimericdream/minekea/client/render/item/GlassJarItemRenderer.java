package com.chimericdream.minekea.client.render.item;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.Objects;
import java.util.function.Consumer;

public class GlassJarItemRenderer implements SpecialModelRenderer<GlassJarBlockEntityRenderState> {
    private static final CameraRenderState CAMERA_STATE = new CameraRenderState();

    @Override
    public void submit(@Nullable GlassJarBlockEntityRenderState state, ItemDisplayContext displayContext, PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, boolean glint, int i) {
//        if (data != null) {
//            data.light = light;
//        }

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

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final GlassJarItemRenderer.Unbaked INSTANCE = new GlassJarItemRenderer.Unbaked();
        public static final MapCodec<com.chimericdream.minekea.client.render.item.GlassJarItemRenderer.Unbaked> CODEC;

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new GlassJarItemRenderer();
        }

        public MapCodec<GlassJarItemRenderer.Unbaked> type() {
            return CODEC;
        }

        static {
            CODEC = MapCodec.unit(INSTANCE);
        }
    }

//    public static <M extends BakedModel> void render(
//        ItemStack stack,
//        MatrixStack matrices,
//        VertexConsumerProvider vertexConsumers,
//        int light,
//        int overlay,
//        M model
//    ) {
//        BlockState defaultState = ContainerBlocks.GLASS_JAR.get().getDefaultState();
//        matrices.push();
//        matrices.translate(0.5, 0.5, 0.5);
//        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, false, matrices, vertexConsumers, light, overlay, model);
//        matrices.pop();
//
//        GlassJarBlockEntity entity = new GlassJarBlockEntity(BlockPos.ORIGIN, defaultState);
//
//        NbtComponent customData = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA);
//        if (customData != null && MinecraftClient.getInstance().world != null) {
//            NbtCompound data = customData.copyNbt();
//            if (!data.isEmpty()) {
//                entity.readNbt(data, MinecraftClient.getInstance().world.getRegistryManager());
//            }
//        }
//
//        NbtComponent entityData = stack.getComponents().get(DataComponentTypes.ENTITY_DATA);
//        if (entityData != null && MinecraftClient.getInstance().world != null) {
//            NbtCompound mobData = entityData.copyNbt();
//            if (!mobData.isEmpty()) {
//                entity.readMobNbt(mobData, MinecraftClient.getInstance().world.getRegistryManager());
//            }
//        }
//
//        MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(entity, matrices, vertexConsumers, light, overlay);
//    }
}
