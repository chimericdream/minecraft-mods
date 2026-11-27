package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.AdvancedSilkTouch;
import com.chimericdream.enchantingfixes.enchantment.MaxLevelEntries;
import com.chimericdream.enchantingfixes.enchantment.MaxLevels;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaxLevelsTest extends BootstrapMinecraft {
    private static EnchantingFixesConfig config(Map<String, Integer> maxLevels, boolean useDefault, int defaultMax, boolean singleLevel) {
        EnchantingFixesConfig config = new EnchantingFixesConfig();
        config.maxLevels = maxLevels;
        config.useDefaultMaxLevel = useDefault;
        config.defaultMaxLevel = defaultMax;
        config.defaultAppliesToSingleLevel = singleLevel;
        return config;
    }

    @AfterEach
    void resetConfig() {
        EnchantingFixesConfig live = EnchantingFixesConfig.CONFIG.instance();
        live.maxLevels = EnchantingFixesConfig.Defaults.MAX_LEVELS;
        live.useDefaultMaxLevel = EnchantingFixesConfig.Defaults.USE_DEFAULT_MAX_LEVEL;
        live.defaultMaxLevel = EnchantingFixesConfig.Defaults.DEFAULT_MAX_LEVEL;
        live.defaultAppliesToSingleLevel = EnchantingFixesConfig.Defaults.DEFAULT_APPLIES_TO_SINGLE_LEVEL;
    }

    @Test
    void defaultsUseLevel10OrTheHardCap() {
        EnchantingFixesConfig config = new EnchantingFixesConfig();
        assertEquals(10, MaxLevels.resolve(5, "minecraft:sharpness", config, false));
        assertEquals(1, MaxLevels.resolve(1, "minecraft:silk_touch", config, false));
        assertEquals(7, MaxLevels.resolve(3, "minecraft:thorns", config, false));
        assertEquals(3, MaxLevels.resolve(3, "minecraft:depth_strider", config, false));
        assertEquals(7, MaxLevels.resolve(3, "minecraft:thorns", config, true));
        assertEquals(5, MaxLevels.resolve(5, "mymod:zap", config, false));
    }

    @Test
    void theDefaultListHasEachMultiLevelVanillaEnchantment() {
        // definition().maxLevel() is the level in the data. getMaxLevel() would apply the config.
        Set<String> registered = VanillaRegistries.createLookup().lookupOrThrow(Registries.ENCHANTMENT).listElements()
            .filter(holder -> holder.value().definition().maxLevel() > 1)
            .map(holder -> holder.key().identifier().toString())
            .collect(Collectors.toSet());

        assertEquals(registered, Set.copyOf(MaxLevels.VANILLA_MULTI_LEVEL_ENCHANTMENTS));
        Set<String> listed = new java.util.HashSet<>(EnchantingFixesConfig.Defaults.MAX_LEVELS.keySet());
        listed.remove(AdvancedSilkTouch.ID);
        assertEquals(registered, listed);
    }

    @Test
    void listedEnchantmentUsesItsLevel() {
        EnchantingFixesConfig config = config(Map.of("minecraft:sharpness", 8), false, 10, false);
        assertEquals(8, MaxLevels.resolve(5, "minecraft:sharpness", config, false));
        assertEquals(4, MaxLevels.resolve(4, "minecraft:protection", config, false));
    }

    @Test
    void listedSingleLevelEnchantmentIgnoresTheSingleLevelSetting() {
        EnchantingFixesConfig config = config(Map.of("minecraft:mending", 3), true, 10, false);
        assertEquals(3, MaxLevels.resolve(1, "minecraft:mending", config, false));
    }

    @Test
    void defaultMaxLevelAppliesToUnlistedEnchantments() {
        EnchantingFixesConfig config = config(Map.of("minecraft:sharpness", 8), true, 10, false);
        assertEquals(8, MaxLevels.resolve(5, "minecraft:sharpness", config, false));
        assertEquals(10, MaxLevels.resolve(4, "minecraft:protection", config, false));
        assertEquals(10, MaxLevels.resolve(4, null, config, false));
    }

    @Test
    void defaultMaxLevelCanLowerTheLevel() {
        EnchantingFixesConfig config = config(Map.of(), true, 2, false);
        assertEquals(2, MaxLevels.resolve(5, "minecraft:sharpness", config, false));
    }

    @Test
    void singleLevelSettingControlsTheDefault() {
        EnchantingFixesConfig keep = config(Map.of(), true, 10, false);
        EnchantingFixesConfig allow = config(Map.of(), true, 10, true);
        assertEquals(1, MaxLevels.resolve(1, "minecraft:silk_touch", keep, false));
        assertEquals(10, MaxLevels.resolve(1, "minecraft:silk_touch", allow, false));
    }

    @Test
    void levelsAreClamped() {
        assertEquals(255, MaxLevels.resolve(5, "minecraft:sharpness", config(Map.of("minecraft:sharpness", 1000), false, 10, false), false));
        assertEquals(1, MaxLevels.resolve(5, "minecraft:sharpness", config(Map.of(), true, 0, false), false));
    }

    @Test
    void hardCapsLimitTheConfiguredLevel() {
        EnchantingFixesConfig config = config(Map.of("minecraft:lure", 10), true, 50, true);
        assertEquals(5, MaxLevels.resolve(3, "minecraft:lure", config, false));
        assertEquals(3, MaxLevels.resolve(3, "minecraft:depth_strider", config, false));
        assertEquals(25, MaxLevels.resolve(1, "minecraft:aqua_affinity", config, false));
        assertEquals(50, MaxLevels.resolve(2, "minecraft:knockback", config(Map.of("minecraft:knockback", 100), false, 10, false), false));
        assertEquals(50, MaxLevels.resolve(5, "minecraft:sharpness", config, false));
    }

    @Test
    void hardCapsDoNotLowerALevelBelowTheDefinition() {
        EnchantingFixesConfig config = config(Map.of("minecraft:lure", 10), false, 10, false);
        assertEquals(8, MaxLevels.resolve(8, "minecraft:lure", config(Map.of(), false, 10, false), false));
        assertEquals(8, MaxLevels.resolve(8, "minecraft:lure", config, false));
    }

    @Test
    void hardCapsDoNotRaiseALowerLevel() {
        EnchantingFixesConfig config = config(Map.of("minecraft:lure", 2), false, 10, false);
        assertEquals(2, MaxLevels.resolve(3, "minecraft:lure", config, false));
    }

    @Test
    void scalingPackReplacesSomeHardCaps() {
        EnchantingFixesConfig config = config(Map.of(), true, 100, true);
        assertEquals(100, MaxLevels.resolve(3, "minecraft:lure", config, true));
        assertEquals(100, MaxLevels.resolve(4, "minecraft:protection", config, true));
        assertEquals(30, MaxLevels.resolve(3, "minecraft:quick_charge", config, true));
        assertEquals(3, MaxLevels.resolve(3, "minecraft:depth_strider", config, true));
        assertEquals(32, MaxLevels.resolve(5, "minecraft:efficiency", config, true));
    }

    @Test
    void linesParseAndSkipBadEntries() {
        Map<String, Integer> map = MaxLevelEntries.fromLines(List.of(
            "minecraft:sharpness=10",
            " efficiency = 7 ",
            "mymod:zap=300",
            "minecraft:protection=lots",
            "no level here",
            "=5",
            "Bad Id=3"
        ));
        assertEquals(Map.of("minecraft:sharpness", 10, "minecraft:efficiency", 7, "mymod:zap", 255), map);
        assertEquals(List.of("minecraft:efficiency=7", "minecraft:sharpness=10", "mymod:zap=255"), MaxLevelEntries.toLines(map));
    }

    @Test
    void mixinAppliesTheConfigToRegisteredEnchantments() {
        HolderGetter<Enchantment> enchantments = VanillaRegistries.createLookup().lookupOrThrow(Registries.ENCHANTMENT);
        Enchantment silkTouch = enchantments.getOrThrow(Enchantments.SILK_TOUCH).value();
        Enchantment sharpness = enchantments.getOrThrow(Enchantments.SHARPNESS).value();

        assertEquals(1, silkTouch.getMaxLevel());
        assertEquals(10, sharpness.getMaxLevel());

        EnchantingFixesConfig live = EnchantingFixesConfig.CONFIG.instance();
        Enchantment lure = enchantments.getOrThrow(Enchantments.LURE).value();

        live.maxLevels = Map.of("minecraft:silk_touch", 2);
        live.defaultAppliesToSingleLevel = false;
        live.useDefaultMaxLevel = true;
        live.defaultMaxLevel = 7;

        assertEquals(2, silkTouch.getMaxLevel());
        assertEquals(7, sharpness.getMaxLevel());
        assertEquals(5, lure.getMaxLevel());
    }
}
