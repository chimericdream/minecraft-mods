package com.chimericdream.betterportallinking.config;

import com.chimericdream.betterportallinking.ModInfo;
import com.chimericdream.lib.config.YaclConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

public class BetterPortalLinkingConfig {
    @SerialEntry
    public boolean enableAddressLinking = Defaults.ENABLE_ADDRESS_LINKING;
    @SerialEntry
    public boolean logLinkingDecisions = Defaults.LOG_LINKING_DECISIONS;

    public static final YaclConfig<BetterPortalLinkingConfig> CONFIG = YaclConfig.builder(BetterPortalLinkingConfig.class, ModInfo.MOD_ID)
        .screen((defaults, config, builder) -> builder
            .title(Component.translatable("text.config.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.section.general"))
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.config.option.enableAddressLinking"))
                    .description(OptionDescription.of(Component.translatable("text.config.option.enableAddressLinking.desc")))
                    .binding(Defaults.ENABLE_ADDRESS_LINKING, () -> config.enableAddressLinking, newVal -> config.enableAddressLinking = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.config.option.logLinkingDecisions"))
                    .description(OptionDescription.of(Component.translatable("text.config.option.logLinkingDecisions.desc")))
                    .binding(Defaults.LOG_LINKING_DECISIONS, () -> config.logLinkingDecisions, newVal -> config.logLinkingDecisions = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
        )
        .build();

    public static class Defaults {
        public static boolean ENABLE_ADDRESS_LINKING = true;
        public static boolean LOG_LINKING_DECISIONS = false;
    }
}
