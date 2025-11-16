package com.chimericdream.minekea.fabric.client;

import com.chimericdream.minekea.client.MinekeaClient;
import net.fabricmc.api.ClientModInitializer;

public final class MinekeaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MinekeaClient.initializeClientRegistries();
        MinekeaClient.onInitializeClient();

        initializeBlockRenderLayers();
        initializeKeybindings();
    }

    private void initializeBlockRenderLayers() {
//        Armoires.BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderLayer.getCutout()));
//        DisplayCases.BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderLayer.getCutout()));
//        StorageBlocks.DYE_BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderLayer.getTranslucent()));
//        StorageBlocks.BAGGED_BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderLayer.getCutout()));
//        Lanterns.BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderLayer.getCutout()));
//        VotiveCandles.BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), RenderLayer.getTranslucent()));
//
//        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), GLASS_JAR.get());
//
//        BlockEntityRendererFactories.register(GLASS_JAR_BLOCK_ENTITY.get(), GlassJarBlockEntityRenderer::new);
//        BuiltinItemRendererRegistry.INSTANCE.register(GLASS_JAR_ITEM.get(), new GlassJarItemRenderer());
//
//        BlockRenderLayerMap.INSTANCE.putBlocks(
//            RenderLayer.getTranslucent(),
//            StorageBlocks.SUGAR_CANE_BLOCK.get()
//        );
//
//        BlockRenderLayerMap.INSTANCE.putBlocks(
//            RenderLayer.getCutout(),
//            StorageBlocks.SET_OF_EGGS_BLOCK.get(),
//            WARPED_WART_PLANT_BLOCK.get()
//        );
    }

    private void initializeKeybindings() {
//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            if (CYCLE_PAINTER_COLOR.wasPressed()) {
//                ClientPlayNetworking.send(new CyclePainterColorPayload(true));
//            }
//        });
    }
}
