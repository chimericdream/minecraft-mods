package com.chimericdream.miniblockmerchants.fabric.client;

import com.chimericdream.lib.text.TextHelpers;
import com.chimericdream.miniblockmerchants.MiniblockMerchantsMod;
import com.chimericdream.miniblockmerchants.ModInfo;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

@Environment(EnvType.CLIENT)
public class MiniblockMerchantsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MiniblockMerchantsMod.LOGGER.info("Initializing client code");

        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if (!itemStack.getItem().getTranslationKey().startsWith(ModInfo.MOD_ID)) {
                return;
            }

            list.add(TextHelpers.getTooltip("tooltip.conversion_item.lore"));
        });
    }
}
