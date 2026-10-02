package com.chimericdream.allhallowssteve.config;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.lib.config.YaclConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

public class AllHallowsSteveConfig {
    @SerialEntry
    public int pumpkinOverlayOpacity = Defaults.PUMPKIN_OVERLAY_OPACITY;

    public static final YaclConfig<AllHallowsSteveConfig> CONFIG = YaclConfig.builder(AllHallowsSteveConfig.class, ModInfo.MOD_ID)
        .fileName("all-hallows-steve.json5")
        .screen((defaults, config, builder) -> builder
            .title(Component.translatable("text.config.allhallowssteve.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.allhallowssteve.title"))
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.allhallowssteve.option.pumpkinOverlayOpacity"))
                    .description(OptionDescription.of(Component.translatable("text.config.allhallowssteve.option.pumpkinOverlayOpacity.description")))
                    .binding(Defaults.PUMPKIN_OVERLAY_OPACITY, () -> config.pumpkinOverlayOpacity, newVal -> config.pumpkinOverlayOpacity = newVal)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(Defaults.MIN_PUMPKIN_OVERLAY_OPACITY, 100)
                        .step(5)
                        .formatValue(value -> Component.literal(value + "%")))
                    .build())
                .build())
        )
        .build();

    /** How opaque the worn-pumpkin vision overlay is, as a percentage, clamped to what the option allows. */
    public static int pumpkinOverlayOpacity() {
        int value = CONFIG.instance().pumpkinOverlayOpacity;

        return Math.max(Defaults.MIN_PUMPKIN_OVERLAY_OPACITY, Math.min(100, value));
    }

    public static class Defaults {
        public static final int PUMPKIN_OVERLAY_OPACITY = 100;
        public static final int MIN_PUMPKIN_OVERLAY_OPACITY = 10;
    }
}
