package com.chimericdream.minekea.fabric.client;

import com.chimericdream.minekea.block.building.compressed.CompressedBlock;
import com.chimericdream.minekea.block.building.compressed.CompressedBlocks;
import com.chimericdream.minekea.block.building.storage.StorageBlocks;
import com.chimericdream.minekea.block.containers.ContainerBlocks;
import com.chimericdream.minekea.block.decorations.DecorationBlocks;
import com.chimericdream.minekea.block.decorations.FakeCakeBlock;
import com.chimericdream.minekea.block.decorations.candles.VotiveCandles;
import com.chimericdream.minekea.block.furniture.displaycases.DisplayCases;
import com.chimericdream.minekea.block.furniture.tables.TableBlock;
import com.chimericdream.minekea.block.furniture.tables.Tables;
import com.chimericdream.minekea.client.MinekeaClient;
import com.chimericdream.minekea.crop.ModCrops;
import com.chimericdream.minekea.fabric.client.render.block.FabricGlassJarBlockEntityRenderer;
import com.chimericdream.minekea.item.tools.BlockPainterItem;
import com.chimericdream.minekea.item.tools.HammerItem;
import com.chimericdream.minekea.network.CyclePainterColorPayload;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//import net.fabricmc.fabric.api.client.rendering.v1.ChunkSectionLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;
import static com.chimericdream.minekea.client.Keybindings.CYCLE_PAINTER_COLOR;

//import com.chimericdream.minekea.fabric.client.render.block.GlassJarBlockEntityRenderer;
//import com.chimericdream.minekea.fabric.client.render.item.GlassJarItemRenderer;
//import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
//import static com.chimericdream.minekea.block.containers.ContainerBlocks.GLASS_JAR;
//import static com.chimericdream.minekea.block.containers.ContainerBlocks.GLASS_JAR_BLOCK_ENTITY;
//import static com.chimericdream.minekea.item.containers.ContainerItems.GLASS_JAR_ITEM;

public final class MinekeaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MinekeaClient.initializeClientRegistries();
        MinekeaClient.onInitializeClient();
        MinekeaClient.registerEntityRenderers();
        MinekeaClient.registerBlockEntityRenderers();

        initializeBlockRenderLayers();
        initializeKeybindings();
        initializeTooltips();
    }

    private void initializeTooltips() {
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if (itemStack.getItem() instanceof HammerItem hammer) {
                list.addAll(hammer.getTooltip());
                return;
            }

            if (itemStack.getItem() instanceof BlockPainterItem painter) {
                list.addAll(painter.getTooltip(itemStack));
                return;
            }

            Holder<Item> registryEntry = itemStack.typeHolder();

            if (registryEntry.is(REGISTRY_HELPER.makeItemRegistryKey(FakeCakeBlock.BLOCK_ID))) {
                list.addAll(((FakeCakeBlock) DecorationBlocks.FAKE_CAKE.get()).getTooltip());
                return;
            }

            if (registryEntry.is(key -> key.toString().contains("minekea:") && key.toString().contains("table"))) {
                list.addAll(((TableBlock) Tables.BLOCKS.getFirst().get()).getTooltip());
                return;
            }

            if (registryEntry.getRegisteredName().contains("minekea:building/compressed")) {
                RegistrySupplier<Block> block = CompressedBlocks.COMPRESSED_BLOCKS_BY_ID.get(registryEntry.getRegisteredName());
                if (block != null) {
                    list.addAll(((CompressedBlock) block.get()).getTooltip());
                }
                return;
            }
        });
    }

    private void initializeBlockRenderLayers() {
//        DisplayCases.BLOCKS.forEach(block -> ChunkSectionLayerMap.putBlock(block.get(), ChunkSectionLayer.CUTOUT));
//        StorageBlocks.DYE_BLOCKS.forEach(block -> ChunkSectionLayerMap.putBlock(block.get(), ChunkSectionLayer.TRANSLUCENT));
//        StorageBlocks.BAGGED_BLOCKS.forEach(block -> ChunkSectionLayerMap.putBlock(block.get(), ChunkSectionLayer.CUTOUT));
//        VotiveCandles.BLOCKS.forEach(block -> ChunkSectionLayerMap.putBlock(block.get(), ChunkSectionLayer.TRANSLUCENT));

        BlockEntityRendererRegistry.register(
            ContainerBlocks.GLASS_JAR_BLOCK_ENTITY.get(),
            FabricGlassJarBlockEntityRenderer::new
        );

//        BuiltinItemRendererRegistry.INSTANCE.register(GLASS_JAR_ITEM.get(), new GlassJarItemRenderer());

//        ChunkSectionLayerMap.putBlocks(
//            ChunkSectionLayer.TRANSLUCENT,
//            StorageBlocks.SUGAR_CANE_BLOCK.get(),
//            ContainerBlocks.GLASS_JAR.get()
//        );

//        ChunkSectionLayerMap.putBlocks(
//            ChunkSectionLayer.CUTOUT,
//            StorageBlocks.EGG_CRATE_BLOCK.get(),
//            ModCrops.WARPED_WART_PLANT_BLOCK.get()
//        );
    }

    private void initializeKeybindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (CYCLE_PAINTER_COLOR.isDown()) {
                ClientPlayNetworking.send(new CyclePainterColorPayload(true));
            }
        });
    }
}
