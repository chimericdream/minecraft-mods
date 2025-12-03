package com.chimericdream.minekea.client.render.item;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderState;
import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;
import java.util.WeakHashMap;

/*
 * Adapted from the Armor Rack mod
 * https://github.com/ThePotatoArchivist/ArmorRack/blob/b8e1a6d496c6f11f621d0ef8714358a323ee0fde/src/client/java/archives/tater/armorrack/client/render/ArmorRackEntityCache.java
 */
public class GlassJarItemEntityCache {
    private GlassJarItemEntityCache() {
    }

    private static final Map<World, Map<ItemStackWrapper, GlassJarBlockEntityRenderState>> CACHE = new WeakHashMap<>();

    public static GlassJarBlockEntityRenderState getOrCreate(ItemStack itemStack, ClientWorld world) {
        return CACHE.computeIfAbsent(
            world,
            _unused -> new WeakHashMap<>()
        ).computeIfAbsent(
            new ItemStackWrapper(itemStack),
            (_unused) -> {
                GlassJarBlockEntity entity = GlassJarBlockEntity.fromItemStack(itemStack, world);
                GlassJarBlockEntityRenderer renderer = (GlassJarBlockEntityRenderer) MinecraftClient.getInstance().getBlockEntityRenderDispatcher().<GlassJarBlockEntity, GlassJarBlockEntityRenderState>get(entity);

                if (renderer == null) {
                    return null;
                }

                GlassJarBlockEntityRenderState state = renderer.createRenderState();
                renderer.updateRenderState(entity, state, 1f, Vec3d.ZERO, null);
                return state;
            }
        );
    }

    // @TODO: move to chimericlib
    public record ItemStackWrapper(ItemStack stack) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ItemStackWrapper that = (ItemStackWrapper) o;
            return ItemStack.areItemsAndComponentsEqual(stack, that.stack);
        }

        @Override
        public int hashCode() {
            return ItemStack.hashCode(stack);
        }
    }
}
