package com.chimericdream.minekea.client.render.item;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderState;
import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/*
 * Adapted from the Armor Rack mod
 * https://github.com/ThePotatoArchivist/ArmorRack/blob/b8e1a6d496c6f11f621d0ef8714358a323ee0fde/src/client/java/archives/tater/armorrack/client/render/ArmorRackEntityCache.java
 */
public class GlassJarItemEntityCache {
    private GlassJarItemEntityCache() {
    }

    private static final Map<ItemStack, GlassJarBlockEntityRenderState> CACHE = new HashMap<>();

    public static GlassJarBlockEntityRenderState getOrCreate(ItemStack itemStack, ClientLevel world) {
        return CACHE.computeIfAbsent(
            itemStack,
            (stack) -> {
                GlassJarBlockEntity entity = GlassJarBlockEntity.fromItemStack(stack, world);
                GlassJarBlockEntityRenderer renderer = (GlassJarBlockEntityRenderer) Minecraft.getInstance().getBlockEntityRenderDispatcher().<GlassJarBlockEntity, GlassJarBlockEntityRenderState>getRenderer(entity);

                if (renderer == null) {
                    return null;
                }

                GlassJarBlockEntityRenderState state = renderer.createRenderState();
                renderer.extractRenderState(entity, state, 1f, Vec3.ZERO, null);
                return state;
            }
        );
    }
}
