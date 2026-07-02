package com.chimericdream.lib.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextHelpers {
    public static MutableComponent getTooltip(String tooltipId) {
        return formatTooltip(Component.translatable(tooltipId));
    }

    public static MutableComponent formatTooltip(MutableComponent tooltipText) {
        return tooltipText.withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC);
    }

    public static String getColorTranslationKey(String color) {
        return String.format("color.minecraft.%s", color);
    }
}
