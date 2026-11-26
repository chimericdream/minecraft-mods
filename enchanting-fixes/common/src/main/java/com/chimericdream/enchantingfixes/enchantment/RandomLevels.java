package com.chimericdream.enchantingfixes.enchantment;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Locale;

/**
 * The maximum level of random enchantments: loot, villager trades, piglin bartering, the enchanting table, and
 * mob equipment. The anvil and {@code /enchant} always use the configured maximum level.
 */
public final class RandomLevels {
    /** In {@link Mode#BALANCED}, random levels go this many levels above the vanilla maximum. */
    public static final int BALANCED_BONUS = 2;

    public enum Mode implements NameableEnum {
        /** Random levels use the vanilla maximum level. */
        OFF,
        /** Random levels go up to {@link #BALANCED_BONUS} levels above the vanilla maximum level. */
        BALANCED,
        /** Random levels use the full configured maximum level. */
        INSANE;

        @Override
        public Component getDisplayName() {
            return Component.translatable("text.config.enchantingfixes.option.randomLevels." + name().toLowerCase(Locale.ROOT));
        }
    }

    private RandomLevels() {
    }

    /**
     * @param configuredMaxLevel the maximum level from the config (the value of {@code getMaxLevel})
     * @param vanillaMaxLevel    the maximum level from the enchantment definition
     */
    public static int cap(int configuredMaxLevel, int vanillaMaxLevel, Mode mode) {
        return switch (mode) {
            case OFF -> Math.min(configuredMaxLevel, vanillaMaxLevel);
            case BALANCED -> Math.min(configuredMaxLevel, vanillaMaxLevel + BALANCED_BONUS);
            case INSANE -> configuredMaxLevel;
        };
    }

    /** Only the server makes random enchantments, so this uses the local config. */
    public static int cap(Enchantment enchantment, int configuredMaxLevel) {
        Mode mode = EnchantingFixesConfig.CONFIG.instance().randomLevels;
        return cap(configuredMaxLevel, enchantment.definition().maxLevel(), mode);
    }
}
