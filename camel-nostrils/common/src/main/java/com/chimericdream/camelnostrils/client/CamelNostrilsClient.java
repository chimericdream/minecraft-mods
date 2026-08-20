package com.chimericdream.camelnostrils.client;

import com.chimericdream.camelnostrils.block.ModBlocks;
import com.chimericdream.camelnostrils.block.UpsideDownChestBlock;
import com.chimericdream.camelnostrils.client.render.block.UpsideDownChestRenderer;
import com.chimericdream.camelnostrils.client.render.item.UpsideDownChestItemRenderer;
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
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(
            ModBlocks.UPSIDE_DOWN_CHEST_BLOCK_ENTITY.get(),
            UpsideDownChestRenderer::new
        );

        SpecialModelRenderers.ID_MAPPER.put(UpsideDownChestBlock.BLOCK_ID, UpsideDownChestItemRenderer.Unbaked.MAP_CODEC);
    }
}
