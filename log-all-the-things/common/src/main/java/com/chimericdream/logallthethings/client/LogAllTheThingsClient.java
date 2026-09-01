package com.chimericdream.logallthethings.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

import com.chimericdream.logallthethings.carpetlog.CarpetLogBlocks;
import com.chimericdream.logallthethings.carpetlog.client.CarpetedBlockEntityRenderer;
import com.chimericdream.logallthethings.snowlog.SnowLogBlocks;
import com.chimericdream.logallthethings.snowlog.client.SnowedBlockEntityRenderer;
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
        BlockEntityRendererRegistry.register(
            CarpetLogBlocks.CARPETED_BLOCK_ENTITY.get(),
            CarpetedBlockEntityRenderer::new
        );
        BlockEntityRendererRegistry.register(
            SnowLogBlocks.SNOWED_BLOCK_ENTITY.get(),
            SnowedBlockEntityRenderer::new
        );
    }
}
