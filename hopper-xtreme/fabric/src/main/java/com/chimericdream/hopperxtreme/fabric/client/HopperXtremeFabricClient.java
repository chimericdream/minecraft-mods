package com.chimericdream.hopperxtreme.fabric.client;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.client.HopperXtremeClient;
import com.chimericdream.hopperxtreme.component.HopperXtremeComponentTypes;
import com.chimericdream.hopperxtreme.component.HopperXtremeFilterModeComponent;
import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
import com.chimericdream.hopperxtreme.item.ModItems;
import com.chimericdream.lib.text.TextHelpers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;

import static com.chimericdream.hopperxtreme.block.GlazedHopperBlock.TOOLTIP_KEY;
import static com.chimericdream.hopperxtreme.item.HopperItemFilterItem.TOOLTIP_KEYS;

public final class HopperXtremeFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HopperXtremeClient.onInitializeClient();

        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if (itemStack.isOf(ModBlocks.HONEY_GLAZED_HOPPER.get().asItem())) {
                list.add(TextHelpers.getTooltip(TOOLTIP_KEY));
                return;
            }

            if (itemStack.isOf(ModItems.HOPPER_ITEM_FILTER_ITEM.get())) {
                HopperXtremeFilterModeComponent component = itemStack.getOrDefault(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), new HopperXtremeFilterModeComponent("include"));
                HopperItemFilterItem.FilterMode mode = HopperItemFilterItem.FilterMode.fromString(component.mode());

                list.add(Text.translatable(TOOLTIP_KEYS.get(mode)));
                return;
            }
        });
    }
}
