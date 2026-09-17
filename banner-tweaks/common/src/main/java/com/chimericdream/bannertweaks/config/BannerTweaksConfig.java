package com.chimericdream.bannertweaks.config;

import com.chimericdream.bannertweaks.ModInfo;
import com.chimericdream.lib.config.YaclConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

public class BannerTweaksConfig {
    @SerialEntry
    public int maxBannerLayers = Defaults.MAX_BANNER_LAYERS;

    public static final YaclConfig<BannerTweaksConfig> CONFIG = YaclConfig.builder(BannerTweaksConfig.class, ModInfo.MOD_ID)
        .screen((defaults, config, builder) -> builder
            .title(Component.literal("Banner Tweaks Config"))
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Banner Tweaks Config"))
                .option(Option.<Integer>createBuilder()
                    .name(Component.literal("Max. banner layers"))
                    .description(OptionDescription.of(Component.literal("The maximum number of layers a banner can have. Default: 12")))
                    .binding(Defaults.MAX_BANNER_LAYERS, () -> config.maxBannerLayers, newVal -> config.maxBannerLayers = newVal)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(1, 32)
                        .step(1))
                    .build())
                .build())
        )
        .build();

    public static class Defaults {
        public static int MAX_BANNER_LAYERS = 12;
    }
}
