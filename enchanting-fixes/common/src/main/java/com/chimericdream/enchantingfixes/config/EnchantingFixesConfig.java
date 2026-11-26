package com.chimericdream.enchantingfixes.config;

import com.chimericdream.enchantingfixes.ModInfo;
import com.chimericdream.enchantingfixes.enchantment.MaxLevelEntries;
import com.chimericdream.enchantingfixes.enchantment.MaxLevels;
import com.chimericdream.lib.config.YaclConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class EnchantingFixesConfig {
    public static final int PROTECTION_LIMIT_MIN = 80;
    public static final int PROTECTION_LIMIT_MAX = 100;

    /** Maximum level for each listed enchantment, by enchantment ID. These entries always apply. */
    @SerialEntry
    public Map<String, Integer> maxLevels = Defaults.MAX_LEVELS;
    /** If true, enchantments that are not in {@link #maxLevels} use {@link #defaultMaxLevel}. */
    @SerialEntry
    public boolean useDefaultMaxLevel = Defaults.USE_DEFAULT_MAX_LEVEL;
    @SerialEntry
    public int defaultMaxLevel = Defaults.DEFAULT_MAX_LEVEL;
    /** If true, {@link #defaultMaxLevel} also applies to enchantments with a vanilla maximum level of 1. */
    @SerialEntry
    public boolean defaultAppliesToSingleLevel = Defaults.DEFAULT_APPLIES_TO_SINGLE_LEVEL;
    /** The damage reduction that protection gets closer to above 20 points. Used only while the Scaling Enchantments pack is on. */
    @SerialEntry
    public int protectionLimitPercent = Defaults.PROTECTION_LIMIT_PERCENT;
    /** If true, the anvil has no "Too Expensive!" limit of 40 levels. */
    @SerialEntry
    public boolean removeAnvilLimit = Defaults.REMOVE_ANVIL_LIMIT;
    /** If true, enchantments that work correctly together are compatible. See {@code Incompatibilities}. */
    @SerialEntry
    public boolean removeIncompatibilities = Defaults.REMOVE_INCOMPATIBILITIES;

    public static final YaclConfig<EnchantingFixesConfig> CONFIG = YaclConfig.builder(EnchantingFixesConfig.class, ModInfo.MOD_ID)
        .screen((defaults, config, builder) -> builder
            .title(Component.translatable("text.config.enchantingfixes.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.enchantingfixes.section.maxLevels"))
                .group(ListOption.<String>createBuilder()
                    .name(Component.translatable("text.config.enchantingfixes.option.maxLevels"))
                    .description(OptionDescription.of(Component.translatable("text.config.enchantingfixes.option.maxLevels.desc")))
                    .binding(
                        MaxLevelEntries.toLines(Defaults.MAX_LEVELS),
                        () -> MaxLevelEntries.toLines(config.maxLevels),
                        lines -> config.maxLevels = MaxLevelEntries.fromLines(lines)
                    )
                    .controller(StringControllerBuilder::create)
                    .initial("minecraft:sharpness=10")
                    .collapsed(false)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.config.enchantingfixes.option.useDefaultMaxLevel"))
                    .description(OptionDescription.of(Component.translatable("text.config.enchantingfixes.option.useDefaultMaxLevel.desc")))
                    .binding(Defaults.USE_DEFAULT_MAX_LEVEL, () -> config.useDefaultMaxLevel, newVal -> config.useDefaultMaxLevel = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.enchantingfixes.option.defaultMaxLevel"))
                    .description(OptionDescription.of(Component.translatable("text.config.enchantingfixes.option.defaultMaxLevel.desc")))
                    .binding(Defaults.DEFAULT_MAX_LEVEL, () -> config.defaultMaxLevel, newVal -> config.defaultMaxLevel = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(MaxLevels.MIN_LEVEL, MaxLevels.MAX_LEVEL))
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.config.enchantingfixes.option.defaultAppliesToSingleLevel"))
                    .description(OptionDescription.of(Component.translatable("text.config.enchantingfixes.option.defaultAppliesToSingleLevel.desc")))
                    .binding(Defaults.DEFAULT_APPLIES_TO_SINGLE_LEVEL, () -> config.defaultAppliesToSingleLevel, newVal -> config.defaultAppliesToSingleLevel = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.enchantingfixes.section.anvil"))
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.config.enchantingfixes.option.removeAnvilLimit"))
                    .description(OptionDescription.of(Component.translatable("text.config.enchantingfixes.option.removeAnvilLimit.desc")))
                    .binding(Defaults.REMOVE_ANVIL_LIMIT, () -> config.removeAnvilLimit, newVal -> config.removeAnvilLimit = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.enchantingfixes.section.compatibility"))
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.config.enchantingfixes.option.removeIncompatibilities"))
                    .description(OptionDescription.of(Component.translatable("text.config.enchantingfixes.option.removeIncompatibilities.desc")))
                    .binding(Defaults.REMOVE_INCOMPATIBILITIES, () -> config.removeIncompatibilities, newVal -> config.removeIncompatibilities = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.enchantingfixes.section.scaling"))
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.enchantingfixes.option.protectionLimitPercent"))
                    .description(OptionDescription.of(Component.translatable("text.config.enchantingfixes.option.protectionLimitPercent.desc")))
                    .binding(Defaults.PROTECTION_LIMIT_PERCENT, () -> config.protectionLimitPercent, newVal -> config.protectionLimitPercent = newVal)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(PROTECTION_LIMIT_MIN, PROTECTION_LIMIT_MAX).step(1))
                    .build())
                .build())
        )
        .onLoad(EnchantingFixesConfig::clampValues)
        .build();

    /** The config file can contain values that the screen does not allow. */
    private static void clampValues(EnchantingFixesConfig config) {
        config.protectionLimitPercent = Math.clamp(config.protectionLimitPercent, PROTECTION_LIMIT_MIN, PROTECTION_LIMIT_MAX);
    }

    public static class Defaults {
        public static Map<String, Integer> MAX_LEVELS = MaxLevels.defaultConfigLevels();
        public static boolean USE_DEFAULT_MAX_LEVEL = false;
        public static int DEFAULT_MAX_LEVEL = 10;
        public static boolean DEFAULT_APPLIES_TO_SINGLE_LEVEL = false;
        public static int PROTECTION_LIMIT_PERCENT = 95;
        public static boolean REMOVE_ANVIL_LIMIT = true;
        public static boolean REMOVE_INCOMPATIBILITIES = false;
    }
}
