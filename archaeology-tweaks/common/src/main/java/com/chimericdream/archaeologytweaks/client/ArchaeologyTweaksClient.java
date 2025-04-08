package com.chimericdream.archaeologytweaks.client;

import com.chimericdream.archaeologytweaks.block.ModBlocks;
import com.chimericdream.archaeologytweaks.client.render.block.entity.ATBrushableBlockEntityRenderer;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ArchaeologyTweaksClient {
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
