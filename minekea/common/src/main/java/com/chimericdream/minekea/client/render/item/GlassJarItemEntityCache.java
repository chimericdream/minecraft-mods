package com.chimericdream.minekea.client.render.item;

import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderState;
import com.chimericdream.minekea.client.render.block.GlassJarBlockEntityRenderer;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/*
 * Adapted from the Armor Rack mod
 * https://github.com/ThePotatoArchivist/ArmorRack/blob/b8e1a6d496c6f11f621d0ef8714358a323ee0fde/src/client/java/archives/tater/armorrack/client/render/ArmorRackEntityCache.java
 */
public class GlassJarItemEntityCache {
    private GlassJarItemEntityCache() {
    }

    private static final int MAX_ENTRIES = 256;

    /*
     * ItemStack has no value-based equals/hashCode, so keying this cache on the stack itself made it
     * identity-keyed: item stacks are recreated constantly (sync, GUI copies), so nearly every lookup
     * missed, inserted a new entry, and nothing was ever evicted - an unbounded per-frame leak.
     *
     * Key instead on the components that actually drive the jar's render state (the same ones
     * GlassJarBlockEntity.fromItemStack reads), all of which have value-based equals/hashCode, and bound
     * the map with LRU eviction so it can never grow without limit.
     */
    private record CacheKey(@Nullable CustomData customData,
                            @Nullable TypedEntityData<EntityType<?>> entityData,
                            @Nullable Component customName) {
        static CacheKey of(ItemStack stack) {
            return new CacheKey(
                stack.get(DataComponents.CUSTOM_DATA),
                stack.get(DataComponents.ENTITY_DATA),
                stack.get(DataComponents.CUSTOM_NAME)
            );
        }
    }

    private static final Map<CacheKey, GlassJarBlockEntityRenderState> CACHE =
        new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<CacheKey, GlassJarBlockEntityRenderState> eldest) {
                return size() > MAX_ENTRIES;
            }
        };

    public static GlassJarBlockEntityRenderState getOrCreate(ItemStack itemStack, ClientLevel world) {
        return CACHE.computeIfAbsent(
            CacheKey.of(itemStack),
            (key) -> {
                GlassJarBlockEntity entity = GlassJarBlockEntity.fromItemStack(itemStack, world);
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
