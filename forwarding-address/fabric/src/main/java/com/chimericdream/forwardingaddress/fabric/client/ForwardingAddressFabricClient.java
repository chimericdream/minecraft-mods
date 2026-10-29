package com.chimericdream.forwardingaddress.fabric.client;

import com.chimericdream.forwardingaddress.migration.RetiredItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public final class ForwardingAddressFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> RetiredItems.appendTooltip(stack, lines));
    }
}
