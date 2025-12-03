package com.chimericdream.minekea.client.render.item;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderState;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Set;

public class GlassJarItemRenderer implements SpecialModelRenderer<GlassJarBlockEntityRenderState> {
    private static final CameraRenderState CAMERA_STATE = new CameraRenderState();

    @Override
    public void render(@Nullable GlassJarBlockEntityRenderState state, ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
//        if (data != null) {
//            data.light = light;
//        }

        matrices.push();
        matrices.translate(1f, 0f, 1f);
        matrices.scale(-1, 1, -1);
        Objects.requireNonNull(MinecraftClient.getInstance().getBlockEntityRenderDispatcher().getByRenderState(state)).render(state, matrices, queue, CAMERA_STATE);
        matrices.pop();
    }

    @Override
    public void collectVertices(Set<Vector3f> vertices) {
    }

    @Override
    public @Nullable GlassJarBlockEntityRenderState getData(ItemStack stack) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) {
            return null;
        }

        return GlassJarItemEntityCache.getOrCreate(stack, world);
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final GlassJarItemRenderer.Unbaked INSTANCE = new GlassJarItemRenderer.Unbaked();
        public static final MapCodec<Unbaked> CODEC;

        @Override
        public SpecialModelRenderer<?> bake(BakeContext context) {
            return new GlassJarItemRenderer();
        }

        public MapCodec<GlassJarItemRenderer.Unbaked> getCodec() {
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
