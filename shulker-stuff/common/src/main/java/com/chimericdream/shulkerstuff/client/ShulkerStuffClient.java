package com.chimericdream.shulkerstuff.client;

import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.block.ModBlocks;
import com.chimericdream.shulkerstuff.client.screen.DyeStationScreen;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffPlatedComponent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.ShulkerBoxBlock;

@Environment(EnvType.CLIENT)
public class ShulkerStuffClient {
    public static void onInitializeClient() {
        MenuScreens.register(ModBlocks.DYE_STATION_SCREEN_HANDLER.get(), DyeStationScreen::new);

        ClientTooltipEvent.ITEM.register((stack, lines, context, flag) -> {
            if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
                return;
            }

            ShulkerStuffPlatedComponent ssPlatedComponent = stack.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_PLATED_COMPONENT.get());
            if (ssPlatedComponent != null && ssPlatedComponent.value()) {
                MutableComponent text = Component.translatable(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "tooltip.upgrades.plated").toLanguageKey());
                lines.add(text.withStyle(ChatFormatting.DARK_GRAY));
            }
        });
    }
}
