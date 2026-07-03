package com.chimericdream.houdiniblock.neoforge.event;

import com.chimericdream.houdiniblock.ModInfo;
import com.chimericdream.houdiniblock.items.HoudiniBlockItem;
import com.chimericdream.houdiniblock.items.ModItems;
import com.chimericdream.lib.text.TextHelpers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

import static com.chimericdream.houdiniblock.items.HoudiniBlockItem.DEFAULT_NBT;
import static com.chimericdream.houdiniblock.items.HoudiniBlockItem.TOOLTIP_KEYS;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class HoudiniBlockEventHandler {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (!stack.is(ModItems.HOUDINI_BLOCK_ITEM.get())) {
            return;
        }

        CompoundTag nbt = stack.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, DEFAULT_NBT).copyTag();
        HoudiniBlockItem.PlacementMode currentMode = HoudiniBlockItem.PlacementMode.getFromNbt(nbt);

        List<Component> tooltip = event.getToolTip();
        tooltip.add(TextHelpers.getTooltip(TOOLTIP_KEYS.get(currentMode)));
    }
}
