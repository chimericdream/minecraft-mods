package com.chimericdream.logallthethings.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

import com.chimericdream.logallthethings.windowlog.WindowLoggingBlocks;
import com.chimericdream.logallthethings.windowlog.client.WindowLoggedBlockEntityRenderer;

public final class LogAllTheThingsClient {
    private LogAllTheThingsClient() {
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(
            WindowLoggingBlocks.WINDOW_LOGGED_BLOCK_ENTITY.get(),
            WindowLoggedBlockEntityRenderer::new
        );
    }
}
