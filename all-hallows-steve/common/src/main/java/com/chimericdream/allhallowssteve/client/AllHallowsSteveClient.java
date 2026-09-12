package com.chimericdream.allhallowssteve.client;

import com.chimericdream.allhallowssteve.block.DecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.LitDecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.client.color.DecoratedPumpkinBlockColors;
import com.chimericdream.allhallowssteve.client.render.DecoratedPumpkinBlockEntityRenderer;
import com.chimericdream.allhallowssteve.client.render.DecoratedPumpkinItemRenderer;
import com.chimericdream.allhallowssteve.client.screen.CarvingStationScreen;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.world.level.block.Block;

public class AllHallowsSteveClient {
    public static void onInitializeClient() {
        MenuScreens.register(ModBlocks.CARVING_STATION_SCREEN_HANDLER.get(), CarvingStationScreen::new);

        ColorHandlerRegistry.registerBlockColors(
            DecoratedPumpkinBlockColors.TINT_SOURCE,
            ModBlocks.DECORATED_PUMPKIN.get(),
            ModBlocks.LIT_DECORATED_PUMPKIN.get(),
            ModBlocks.LIT_DECORATED_PUMPKIN_BLUE.get(),
            ModBlocks.LIT_DECORATED_PUMPKIN_GREEN.get(),
            ModBlocks.LIT_DECORATED_PUMPKIN_RED.get()
        );

        // One BlockEntityType (and so one renderer registration) covers the unlit block and all four
        // lit variants — see ModBlocks.DECORATED_PUMPKIN_BLOCK_ENTITY's valid-block set.
        BlockEntityRendererRegistry.register(ModBlocks.DECORATED_PUMPKIN_BLOCK_ENTITY.get(), DecoratedPumpkinBlockEntityRenderer::new);

        SpecialModelRenderers.ID_MAPPER.put(DecoratedPumpkinBlock.BLOCK_ID, new DecoratedPumpkinItemRenderer.Unbaked("").type());
        for (RegistrySupplier<Block> lit : ModBlocks.LIT_DECORATED_PUMPKINS) {
            LitDecoratedPumpkinBlock litBlock = (LitDecoratedPumpkinBlock) lit.get();
            SpecialModelRenderers.ID_MAPPER.put(litBlock.BLOCK_ID, new DecoratedPumpkinItemRenderer.Unbaked(litBlock.overlaySuffix).type());
        }
    }
}
