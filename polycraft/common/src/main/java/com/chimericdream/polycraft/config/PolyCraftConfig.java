package com.chimericdream.polycraft.config;

import com.chimericdream.lib.config.YaclConfig;
import com.chimericdream.polycraft.ModInfo;
import com.chimericdream.polycraft.core.ConfigLimits;
import com.chimericdream.polycraft.core.IdFilter;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class PolyCraftConfig {
    @SerialEntry
    public boolean detectOverlappingInputs = Defaults.DETECT_OVERLAPPING_INPUTS;
    @SerialEntry
    public int visibleOptions = Defaults.VISIBLE_OPTIONS;
    @SerialEntry
    public boolean rememberChoices = Defaults.REMEMBER_CHOICES;
    @SerialEntry
    public boolean showConflictBadge = Defaults.SHOW_CONFLICT_BADGE;
    @SerialEntry
    public List<String> enabledRecipeTypes = new ArrayList<>(Defaults.ENABLED_RECIPE_TYPES);
    @SerialEntry
    public List<String> disabledRecipeTypes = new ArrayList<>(Defaults.DISABLED_RECIPE_TYPES);
    @SerialEntry
    public boolean debugLogging = Defaults.DEBUG_LOGGING;

    private transient List<String> filteredEnabled = List.of();
    private transient List<String> filteredDisabled = List.of();
    private transient IdFilter recipeTypeFilter = IdFilter.ALLOW_ALL;

    public static final YaclConfig<PolyCraftConfig> CONFIG = YaclConfig.builder(PolyCraftConfig.class, ModInfo.MOD_ID)
        .screen((defaults, config, builder) -> builder
            .title(Component.translatable("polycraft.config.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("polycraft.config.section.detection"))
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("polycraft.config.option.detectOverlappingInputs"))
                    .description(OptionDescription.of(Component.translatable("polycraft.config.option.detectOverlappingInputs.desc")))
                    .binding(Defaults.DETECT_OVERLAPPING_INPUTS, () -> config.detectOverlappingInputs, newVal -> config.detectOverlappingInputs = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(ListOption.<String>createBuilder()
                    .name(Component.translatable("polycraft.config.option.enabledRecipeTypes"))
                    .description(OptionDescription.of(Component.translatable("polycraft.config.option.enabledRecipeTypes.desc")))
                    .binding(new ArrayList<>(Defaults.ENABLED_RECIPE_TYPES), () -> config.enabledRecipeTypes, newVal -> config.enabledRecipeTypes = new ArrayList<>(newVal))
                    .controller(StringControllerBuilder::create)
                    .initial("minecraft:*")
                    .build())
                .option(ListOption.<String>createBuilder()
                    .name(Component.translatable("polycraft.config.option.disabledRecipeTypes"))
                    .description(OptionDescription.of(Component.translatable("polycraft.config.option.disabledRecipeTypes.desc")))
                    .binding(new ArrayList<>(Defaults.DISABLED_RECIPE_TYPES), () -> config.disabledRecipeTypes, newVal -> config.disabledRecipeTypes = new ArrayList<>(newVal))
                    .controller(StringControllerBuilder::create)
                    .initial("othermod:some_type")
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("polycraft.config.section.interface"))
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("polycraft.config.option.visibleOptions"))
                    .description(OptionDescription.of(Component.translatable("polycraft.config.option.visibleOptions.desc")))
                    .binding(Defaults.VISIBLE_OPTIONS, () -> config.visibleOptions, newVal -> config.visibleOptions = newVal)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(ConfigLimits.MIN_VISIBLE_OPTIONS, ConfigLimits.MAX_VISIBLE_OPTIONS)
                        .step(1))
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("polycraft.config.option.showConflictBadge"))
                    .description(OptionDescription.of(Component.translatable("polycraft.config.option.showConflictBadge.desc")))
                    .binding(Defaults.SHOW_CONFLICT_BADGE, () -> config.showConflictBadge, newVal -> config.showConflictBadge = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("polycraft.config.section.misc"))
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("polycraft.config.option.rememberChoices"))
                    .description(OptionDescription.of(Component.translatable("polycraft.config.option.rememberChoices.desc")))
                    .binding(Defaults.REMEMBER_CHOICES, () -> config.rememberChoices, newVal -> config.rememberChoices = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("polycraft.config.option.debugLogging"))
                    .description(OptionDescription.of(Component.translatable("polycraft.config.option.debugLogging.desc")))
                    .binding(Defaults.DEBUG_LOGGING, () -> config.debugLogging, newVal -> config.debugLogging = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build()))
        .build();

    public static PolyCraftConfig get() {
        return CONFIG.instance();
    }

    /** {@link #visibleOptions} limited to the range the popup can lay out. */
    public int visibleOptionCount() {
        return ConfigLimits.visibleOptions(visibleOptions);
    }

    /** Which recipe types PolyCraft handles, from the allow and deny lists. Rebuilt only when the lists change. */
    public IdFilter recipeTypes() {
        if (!enabledRecipeTypes.equals(filteredEnabled) || !disabledRecipeTypes.equals(filteredDisabled)) {
            filteredEnabled = List.copyOf(enabledRecipeTypes);
            filteredDisabled = List.copyOf(disabledRecipeTypes);
            recipeTypeFilter = new IdFilter(filteredEnabled, filteredDisabled);
        }

        return recipeTypeFilter;
    }

    public static final class Defaults {
        public static final boolean DETECT_OVERLAPPING_INPUTS = false;
        public static final int VISIBLE_OPTIONS = ConfigLimits.DEFAULT_VISIBLE_OPTIONS;
        public static final boolean REMEMBER_CHOICES = true;
        public static final boolean SHOW_CONFLICT_BADGE = true;
        public static final List<String> ENABLED_RECIPE_TYPES = List.of();
        public static final List<String> DISABLED_RECIPE_TYPES = List.of();
        public static final boolean DEBUG_LOGGING = false;

        private Defaults() {
        }
    }
}
