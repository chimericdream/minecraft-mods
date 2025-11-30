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
import com.chimericdream.minekea.item.tools.BlockPainterItem;
import com.chimericdream.minekea.item.tools.HammerItem;
import com.chimericdream.minekea.network.CyclePainterColorPayload;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

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

            RegistryEntry<Item> registryEntry = itemStack.getRegistryEntry();

            if (registryEntry.matchesKey(REGISTRY_HELPER.makeItemRegistryKey(FakeCakeBlock.BLOCK_ID))) {
                list.addAll(((FakeCakeBlock) DecorationBlocks.FAKE_CAKE.get()).getTooltip());
                return;
            }

            if (registryEntry.matches(key -> key.toString().contains("minekea:") && key.toString().contains("table"))) {
                list.addAll(((TableBlock) Tables.BLOCKS.getFirst().get()).getTooltip());
                return;
            }

            if (registryEntry.getIdAsString().contains("minekea:building/compressed")) {
                RegistrySupplier<Block> block = CompressedBlocks.COMPRESSED_BLOCKS_BY_ID.get(registryEntry.getIdAsString());
                if (block != null) {
                    list.addAll(((CompressedBlock) block.get()).getTooltip());
                }
                return;
            }
        });
    }

    private void initializeBlockRenderLayers() {
        DisplayCases.BLOCKS.forEach(block -> BlockRenderLayerMap.putBlock(block.get(), BlockRenderLayer.CUTOUT));
        StorageBlocks.DYE_BLOCKS.forEach(block -> BlockRenderLayerMap.putBlock(block.get(), BlockRenderLayer.TRANSLUCENT));
        StorageBlocks.BAGGED_BLOCKS.forEach(block -> BlockRenderLayerMap.putBlock(block.get(), BlockRenderLayer.CUTOUT));
        VotiveCandles.BLOCKS.forEach(block -> BlockRenderLayerMap.putBlock(block.get(), BlockRenderLayer.TRANSLUCENT));

//        BlockEntityRendererFactories.register(GLASS_JAR_BLOCK_ENTITY.get(), GlassJarBlockEntityRenderer::new);
//        BuiltinItemRendererRegistry.INSTANCE.register(GLASS_JAR_ITEM.get(), new GlassJarItemRenderer());

        BlockRenderLayerMap.putBlocks(
            BlockRenderLayer.TRANSLUCENT,
            StorageBlocks.SUGAR_CANE_BLOCK.get(),
            ContainerBlocks.GLASS_JAR.get()
        );

        BlockRenderLayerMap.putBlocks(
            BlockRenderLayer.CUTOUT,
            StorageBlocks.SET_OF_EGGS_BLOCK.get(),
            ModCrops.WARPED_WART_PLANT_BLOCK.get()
        );
    }

    private void initializeKeybindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (CYCLE_PAINTER_COLOR.wasPressed()) {
                ClientPlayNetworking.send(new CyclePainterColorPayload(true));
            }
        });
    }
}
