package com.chimericdream.jdcrafte.client;

import com.chimericdream.jdcrafte.block.ModBlocks;
import com.chimericdream.jdcrafte.client.render.block.WeathervaneBlockEntityRenderer;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

public class JDCrafteClient {
    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(
            ModBlocks.WEATHERVANE_BLOCK_ENTITY.get(),
            WeathervaneBlockEntityRenderer::new
        );
    }
}
