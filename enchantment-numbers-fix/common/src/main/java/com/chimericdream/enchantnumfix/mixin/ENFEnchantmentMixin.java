package com.chimericdream.enchantnumfix.mixin;

import com.chimericdream.enchantnumfix.RomanNumeralUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Renders enchantment levels as Roman numerals.
 *
 * <p>Vanilla {@code Enchantment.getFullname} builds the level suffix with
 * {@code Component.translatable("enchantment.level." + level)}. Rather than {@code @Overwrite} the
 * whole method (which would clash with any other mod that also touches {@code getFullname}), we
 * redirect only that single {@code Component.translatable(String)} call and substitute a literal
 * Roman-numeral component. Everything else — the description copy, curse/gray styling, and the
 * {@code level != 1 || maxLevel != 1} guard that decides whether a suffix is appended at all — is
 * left to vanilla, so this stays compatible with other enchantment-tooltip mods.
 */
@Mixin(Enchantment.class)
public abstract class ENFEnchantmentMixin {
    @Redirect(
        method = "getFullname",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    private static MutableComponent enchantnumfix$romanLevel(String translationKey, Holder<Enchantment> enchantment, int level) {
        return Component.literal(RomanNumeralUtil.toRoman(level));
    }
}
