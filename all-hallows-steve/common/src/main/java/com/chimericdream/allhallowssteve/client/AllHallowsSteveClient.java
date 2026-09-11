package com.chimericdream.allhallowssteve.client;

import com.chimericdream.allhallowssteve.block.DecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.client.color.DecoratedPumpkinBlockColors;
import com.chimericdream.allhallowssteve.client.render.DecoratedPumpkinBlockEntityRenderer;
import com.chimericdream.allhallowssteve.client.render.DecoratedPumpkinItemRenderer;
import com.chimericdream.allhallowssteve.client.screen.CarvingStationScreen;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.special.SpecialModelRenderers;

public class AllHallowsSteveClient {
    public static void onInitializeClient() {
        MenuScreens.register(ModBlocks.CARVING_STATION_SCREEN_HANDLER.get(), CarvingStationScreen::new);

        ColorHandlerRegistry.registerBlockColors(DecoratedPumpkinBlockColors.TINT_SOURCE, ModBlocks.DECORATED_PUMPKIN.get());

        BlockEntityRendererRegistry.register(ModBlocks.DECORATED_PUMPKIN_BLOCK_ENTITY.get(), DecoratedPumpkinBlockEntityRenderer::new);
        SpecialModelRenderers.ID_MAPPER.put(DecoratedPumpkinBlock.BLOCK_ID, DecoratedPumpkinItemRenderer.Unbaked.CODEC);
    }
}
