package com.chimericdream.logallthethings.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

import com.chimericdream.logallthethings.windowlog.WindowLogBlocks;
import com.chimericdream.logallthethings.windowlog.client.WindowedBlockEntityRenderer;

public final class LogAllTheThingsClient {
    private LogAllTheThingsClient() {
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(
            WindowLogBlocks.WINDOWED_BLOCK_ENTITY.get(),
            WindowedBlockEntityRenderer::new
        );
    }
}
