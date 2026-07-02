package com.chimericdream.enchantnumfix.mixin;

import com.chimericdream.enchantnumfix.RomanNumeralUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Enchantment.class)
public abstract class ENFEnchantmentMixin {
    /**
     * @author chimericdream
     * @reason Replace numbers with Roman numerals
     */
    @Overwrite
    public static Component getName(Holder<Enchantment> enchantment, int level) {
        MutableComponent mutableText = enchantment.value().description.copy();

        if (enchantment.is(EnchantmentTags.CURSE)) {
            ComponentUtils.mergeStyles(mutableText, Style.EMPTY.withColor(ChatFormatting.RED));
        } else {
            ComponentUtils.mergeStyles(mutableText, Style.EMPTY.withColor(ChatFormatting.GRAY));
        }

        if (level != 1 || enchantment.value().getMaxLevel() != 1) {
            mutableText.append(CommonComponents.SPACE).append(RomanNumeralUtil.toRoman(level));
        }

        return mutableText;
    }
}
