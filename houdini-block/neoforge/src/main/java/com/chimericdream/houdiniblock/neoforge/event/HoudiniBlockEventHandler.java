package com.chimericdream.houdiniblock.neoforge.event;

import com.chimericdream.houdiniblock.ModInfo;
import com.chimericdream.houdiniblock.items.HoudiniBlockItem;
import com.chimericdream.houdiniblock.items.ModItems;
import com.chimericdream.lib.text.TextHelpers;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
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

        if (!stack.isOf(ModItems.HOUDINI_BLOCK_ITEM.get())) {
            return;
        }

        NbtCompound nbt = stack.getComponents().getOrDefault(DataComponentTypes.CUSTOM_DATA, DEFAULT_NBT).copyNbt();
        HoudiniBlockItem.PlacementMode currentMode = HoudiniBlockItem.PlacementMode.getFromNbt(nbt);

        List<Text> tooltip = event.getToolTip();
        tooltip.add(TextHelpers.getTooltip(TOOLTIP_KEYS.get(currentMode)));
    }
}
