package com.chimericdream.butwhatabout.client;

import com.chimericdream.butwhatabout.block.ModBlocks;
import com.chimericdream.butwhatabout.client.render.block.entity.ATBrushableBlockEntityRenderer;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

public class ButWhatAboutClient {
    public static void onInitializeClient() {
        registerEntityRenderers();
    }

    public static void registerEntityRenderers() {
        BlockEntityRendererRegistry.register(
            ModBlocks.BRUSHABLE_MOD_BLOCK_ENTITY.get(),
            ATBrushableBlockEntityRenderer::new
        );
    }
}
