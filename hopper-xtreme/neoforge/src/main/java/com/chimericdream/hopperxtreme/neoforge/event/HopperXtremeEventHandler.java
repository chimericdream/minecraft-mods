package com.chimericdream.hopperxtreme.neoforge.event;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.component.HopperXtremeComponentTypes;
import com.chimericdream.hopperxtreme.component.HopperXtremeFilterModeComponent;
import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
import com.chimericdream.hopperxtreme.item.ModItems;
import com.chimericdream.lib.text.TextHelpers;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

import static com.chimericdream.hopperxtreme.block.GlazedHopperBlock.TOOLTIP_KEY;
import static com.chimericdream.hopperxtreme.item.HopperItemFilterItem.TOOLTIP_KEYS;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class HopperXtremeEventHandler {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        List<Text> tooltip = event.getToolTip();

        if (stack.isOf(ModBlocks.HONEY_GLAZED_HOPPER.get().asItem())) {
            tooltip.add(TextHelpers.getTooltip(TOOLTIP_KEY));
            return;
        }

        if (stack.isOf(ModItems.HOPPER_ITEM_FILTER_ITEM.get())) {
            HopperXtremeFilterModeComponent component = stack.getOrDefault(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), new HopperXtremeFilterModeComponent("include"));
            HopperItemFilterItem.FilterMode mode = HopperItemFilterItem.FilterMode.fromString(component.mode());

            tooltip.add(Text.translatable(TOOLTIP_KEYS.get(mode)));
            return;
        }
    }
}
