package com.chimericdream.camelnostrils.client;

import com.chimericdream.camelnostrils.block.ModBlocks;
import com.chimericdream.camelnostrils.block.UpsideDownChestBlock;
import com.chimericdream.camelnostrils.client.render.block.UpsideDownChestRenderer;
import com.chimericdream.camelnostrils.client.render.item.UpsideDownChestItemRenderer;
import com.chimericdream.camelnostrils.client.render.entity.ZombieCodRenderer;
import com.chimericdream.camelnostrils.client.render.entity.ZombieSalmonRenderer;
import com.chimericdream.camelnostrils.client.render.entity.ZombieTropicalFishRenderer;
import com.chimericdream.camelnostrils.entity.ModEntities;
import com.chimericdream.lib.entities.FallingUpwardBlockEntity;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.minecraft.client.renderer.special.SpecialModelRenderers;

public class CamelNostrilsClient {
    // NeoForge must call this during mod construction (see CamelNostrilsNeoForge's constructor) - any
    // lifecycle event is too late, since architectury's own RegisterRenderers listener fires on its bus
    // before ours.
    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(
            ModEntities.FALLING_UPWARD_BLOCK_ENTITY,
            FallingUpwardBlockEntity.Renderer::new
        );

        // The zombie fish animate exactly like their vanilla counterparts, so these renderers just
        // extend the vanilla ones to swap in the zombie fish's own textures instead of duplicating models.
        EntityRendererRegistry.register(ModEntities.ZOMBIE_SALMON, ZombieSalmonRenderer::new);
        EntityRendererRegistry.register(ModEntities.ZOMBIE_COD, ZombieCodRenderer::new);
        EntityRendererRegistry.register(ModEntities.ZOMBIE_TROPICAL_FISH, ZombieTropicalFishRenderer::new);
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(
            ModBlocks.UPSIDE_DOWN_CHEST_BLOCK_ENTITY.get(),
            UpsideDownChestRenderer::new
        );

        SpecialModelRenderers.ID_MAPPER.put(UpsideDownChestBlock.BLOCK_ID, UpsideDownChestItemRenderer.Unbaked.MAP_CODEC);
    }
}
