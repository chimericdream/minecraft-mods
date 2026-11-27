package com.chimericdream.enchantingfixes.enchantment;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Calculates the maximum level of an enchantment from the config. */
public final class MaxLevels {
    /** The level range that the enchantment definition codec and {@code ItemEnchantments} accept. */
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 255;

    /**
     * Above these levels, the vanilla formula gives no more benefit, even on one item with no other
     * sources of the same effect. Each value comes from the 26.2 enchantment data and the code that uses
     * it. Check them again when Minecraft changes an enchantment.
     */
    public static final Map<String, Integer> HARD_CAPS = Map.ofEntries(
        // CombatRules limits the total protection points to 20. Protection gives 1 point a level.
        Map.entry("minecraft:protection", 20),
        // 2 points a level. The burning time and explosion knockback parts stop at level 7.
        Map.entry("minecraft:fire_protection", 10),
        Map.entry("minecraft:blast_protection", 10),
        Map.entry("minecraft:projectile_protection", 10),
        // 3 points a level: 21 at level 7.
        Map.entry("minecraft:feather_falling", 7),
        // water_movement_efficiency has a maximum of 1.0. Each level gives 1/3.
        Map.entry("minecraft:depth_strider", 3),
        // sneaking_speed is 0.3 + 0.15 a level, with a maximum of 1.0.
        Map.entry("minecraft:swift_sneak", 5),
        // The charge time is 1.25 s - 0.25 s a level, with a minimum of 0.
        Map.entry("minecraft:quick_charge", 5),
        // Armor blocks at most 0.8 of the damage. Breach removes 0.15 a level.
        Map.entry("minecraft:breach", 6),
        // Knockback has no limit: each level adds 0.5 blocks/tick. At level 50, the target flies about 200
        // blocks. Farther than that, it leaves the simulation distance and stops in mid-air.
        Map.entry("minecraft:knockback", 50),
        // The chance is 15% a level. The damage does not change with the level.
        Map.entry("minecraft:thorns", 7),
        // The radius is 3 + 1 a level, clamped to 16.
        Map.entry("minecraft:frost_walker", 14),
        // mining_efficiency is level squared + 1, with a maximum of 1024.
        Map.entry("minecraft:efficiency", 32),
        // submerged_mining_speed is 0.2 * (1 + 4 * level), with a maximum of 20.
        Map.entry("minecraft:aqua_affinity", 25),
        // Each luck point removes 1 weight from fish (85). At 85, all catches are treasure.
        Map.entry("minecraft:luck_of_the_sea", 85),
        // Each level removes 100 ticks from a 100-600 tick wait. FishingHook picks a new wait while the
        // wait is 0 or less, so at level 6 and higher no fish bites.
        Map.entry("minecraft:lure", 5),
        // Level XXX collects bedrock, the last block.
        Map.entry(AdvancedSilkTouch.ID, AdvancedSilkTouch.MAX_LEVEL)
    );

    /**
     * Caps that replace {@link #HARD_CAPS} while the Scaling Enchantments pack is on. In the pack, these
     * enchantments get closer to a limit and do not reach it, so most of them have no cap.
     */
    public static final Map<String, Integer> SCALING_HARD_CAPS = Map.ofEntries(
        // EFCombatRulesMixin lets protection points above 20 count.
        Map.entry("minecraft:protection", MAX_LEVEL),
        Map.entry("minecraft:fire_protection", MAX_LEVEL),
        Map.entry("minecraft:blast_protection", MAX_LEVEL),
        Map.entry("minecraft:projectile_protection", MAX_LEVEL),
        Map.entry("minecraft:feather_falling", MAX_LEVEL),
        // The charge time is 1.5 s / level. CrossbowItem rounds it down to ticks. Because of float rounding,
        // it is 0 ticks at level 30.
        Map.entry("minecraft:quick_charge", 30),
        Map.entry("minecraft:breach", MAX_LEVEL),
        Map.entry("minecraft:thorns", MAX_LEVEL),
        Map.entry("minecraft:lure", MAX_LEVEL)
    );

    /**
     * The vanilla 26.2 enchantments with a maximum level above 1. Used for the default config. Single-level
     * enchantments are not in the list, so they stay at level 1 unless the player changes the config.
     */
    public static final List<String> VANILLA_MULTI_LEVEL_ENCHANTMENTS = List.of(
        "minecraft:bane_of_arthropods", "minecraft:blast_protection", "minecraft:breach", "minecraft:density",
        "minecraft:depth_strider", "minecraft:efficiency", "minecraft:feather_falling", "minecraft:fire_aspect",
        "minecraft:fire_protection", "minecraft:fortune", "minecraft:frost_walker", "minecraft:impaling",
        "minecraft:knockback", "minecraft:looting", "minecraft:loyalty", "minecraft:luck_of_the_sea",
        "minecraft:lunge", "minecraft:lure", "minecraft:piercing", "minecraft:power",
        "minecraft:projectile_protection", "minecraft:protection", "minecraft:punch", "minecraft:quick_charge",
        "minecraft:respiration", "minecraft:riptide", "minecraft:sharpness", "minecraft:smite",
        "minecraft:soul_speed", "minecraft:sweeping_edge", "minecraft:swift_sneak", "minecraft:thorns",
        "minecraft:unbreaking", "minecraft:wind_burst"
    );

    /** The default level in the config list: {@value #DEFAULT_CONFIG_LEVEL}, or the hard cap if it is lower. */
    public static final int DEFAULT_CONFIG_LEVEL = 10;

    private MaxLevels() {
    }

    /**
     * A config list with each multi-level vanilla enchantment at {@link #DEFAULT_CONFIG_LEVEL} or its hard cap,
     * whichever is lower, and Advanced Silk Touch at {@value AdvancedSilkTouch#MAX_LEVEL}.
     */
    public static Map<String, Integer> defaultConfigLevels() {
        Map<String, Integer> levels = new HashMap<>();
        for (String id : VANILLA_MULTI_LEVEL_ENCHANTMENTS) {
            levels.put(id, Math.min(DEFAULT_CONFIG_LEVEL, HARD_CAPS.getOrDefault(id, MAX_LEVEL)));
        }
        // Applies only while the Advanced Silk Touch data pack is on.
        levels.put(AdvancedSilkTouch.ID, AdvancedSilkTouch.MAX_LEVEL);

        return Map.copyOf(levels);
    }

    public static int clamp(int level) {
        return Mth.clamp(level, MIN_LEVEL, MAX_LEVEL);
    }

    /**
     * @param vanillaMaxLevel the maximum level from the enchantment definition
     * @param id              the enchantment ID, or null if it is not known
     * @param scaling         true if the Scaling Enchantments pack is on
     */
    public static int resolve(int vanillaMaxLevel, @Nullable String id, EnchantingFixesConfig config, boolean scaling) {
        return applyHardCap(vanillaMaxLevel, id, configured(vanillaMaxLevel, id, config), scaling);
    }

    private static int configured(int vanillaMaxLevel, @Nullable String id, EnchantingFixesConfig config) {
        if (id != null) {
            Integer configured = config.maxLevels.get(id);
            if (configured != null) {
                return clamp(configured);
            }
        }

        if (!config.useDefaultMaxLevel) {
            return vanillaMaxLevel;
        }

        if (vanillaMaxLevel == 1 && !config.defaultAppliesToSingleLevel) {
            return vanillaMaxLevel;
        }

        return clamp(config.defaultMaxLevel);
    }

    /**
     * The cap never goes below the level in the enchantment definition. A data pack that raises the
     * maximum level of a vanilla enchantment can also change its effects, so the cap does not apply.
     */
    private static int applyHardCap(int vanillaMaxLevel, @Nullable String id, int level, boolean scaling) {
        if (id == null) {
            return level;
        }

        Integer cap = scaling && SCALING_HARD_CAPS.containsKey(id) ? SCALING_HARD_CAPS.get(id) : HARD_CAPS.get(id);
        if (cap == null) {
            return level;
        }

        return Math.min(level, Math.max(cap, vanillaMaxLevel));
    }
}
