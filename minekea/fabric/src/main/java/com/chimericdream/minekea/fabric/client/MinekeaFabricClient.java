package com.chimericdream.minekea.fabric.client;

import com.chimericdream.minekea.block.building.storage.StorageBlocks;
import com.chimericdream.minekea.block.decorations.candles.VotiveCandles;
import com.chimericdream.minekea.block.furniture.displaycases.DisplayCases;
import com.chimericdream.minekea.client.MinekeaClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;

public final class MinekeaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MinekeaClient.initializeClientRegistries();
        MinekeaClient.onInitializeClient();

        initializeBlockRenderLayers();
        initializeKeybindings();
    }

    private void initializeBlockRenderLayers() {
//        Armoires.BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), BlockRenderLayer.CUTOUT));
        DisplayCases.BLOCKS.forEach(block -> BlockRenderLayerMap.putBlock(block.get(), BlockRenderLayer.CUTOUT));
        StorageBlocks.DYE_BLOCKS.forEach(block -> BlockRenderLayerMap.putBlock(block.get(), BlockRenderLayer.TRANSLUCENT));
        StorageBlocks.BAGGED_BLOCKS.forEach(block -> BlockRenderLayerMap.putBlock(block.get(), BlockRenderLayer.CUTOUT));
//        Lanterns.BLOCKS.forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block.get(), BlockRenderLayer.CUTOUT));
        VotiveCandles.BLOCKS.forEach(block -> BlockRenderLayerMap.putBlock(block.get(), BlockRenderLayer.TRANSLUCENT));

//        BlockRenderLayerMap.INSTANCE.putBlocks(BlockRenderLayer.TRANSLUCENT, GLASS_JAR.get());

//        BlockEntityRendererFactories.register(GLASS_JAR_BLOCK_ENTITY.get(), GlassJarBlockEntityRenderer::new);
//        BuiltinItemRendererRegistry.INSTANCE.register(GLASS_JAR_ITEM.get(), new GlassJarItemRenderer());

        BlockRenderLayerMap.putBlocks(
            BlockRenderLayer.TRANSLUCENT,
            StorageBlocks.SUGAR_CANE_BLOCK.get()
        );

//        BlockRenderLayerMap.INSTANCE.putBlocks(
//            BlockRenderLayer.CUTOUT,
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
