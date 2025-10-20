package com.chimericdream.artificialheart.fabric.client;

import com.chimericdream.artificialheart.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;

public final class ArtificialHeartFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlocks(
            BlockRenderLayer.CUTOUT,
            ModBlocks.CLIPPED_EYEBLOSSOM_BLOCK.get(),
            ModBlocks.CLIPPED_OPEN_EYEBLOSSOM_BLOCK.get(),
            ModBlocks.POTTED_CLIPPED_EYEBLOSSOM_BLOCK.get(),
            ModBlocks.POTTED_CLIPPED_OPEN_EYEBLOSSOM_BLOCK.get()
        );
    }
}
