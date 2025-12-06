package com.chimericdream.minekea.client.render.item;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderState;
import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

/*
 * Adapted from the Armor Rack mod
 * https://github.com/ThePotatoArchivist/ArmorRack/blob/b8e1a6d496c6f11f621d0ef8714358a323ee0fde/src/client/java/archives/tater/armorrack/client/render/ArmorRackEntityCache.java
 */
public class GlassJarItemEntityCache {
    private GlassJarItemEntityCache() {
    }

    private static final Map<ItemStack, GlassJarBlockEntityRenderState> CACHE = new HashMap<>();

    public static GlassJarBlockEntityRenderState getOrCreate(ItemStack itemStack, ClientWorld world) {
        return CACHE.computeIfAbsent(
            itemStack,
            (stack) -> {
                GlassJarBlockEntity entity = GlassJarBlockEntity.fromItemStack(stack, world);
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
}
