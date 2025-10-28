package com.chimericdream.houdiniblock.fabric.client;

import com.chimericdream.houdiniblock.items.HoudiniBlockItem;
import com.chimericdream.houdiniblock.items.ModItems;
import com.chimericdream.lib.text.TextHelpers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.nbt.NbtCompound;

import static com.chimericdream.houdiniblock.items.HoudiniBlockItem.DEFAULT_NBT;
import static com.chimericdream.houdiniblock.items.HoudiniBlockItem.TOOLTIP_KEYS;

public final class HoudiniBlockFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if (!itemStack.isOf(ModItems.HOUDINI_BLOCK_ITEM.get())) {
                return;
            }

            NbtCompound nbt = itemStack.getComponents().getOrDefault(DataComponentTypes.CUSTOM_DATA, DEFAULT_NBT).copyNbt();
            HoudiniBlockItem.PlacementMode currentMode = HoudiniBlockItem.PlacementMode.getFromNbt(nbt);

            list.add(TextHelpers.getTooltip(TOOLTIP_KEYS.get(currentMode)));
        });
    }
}
