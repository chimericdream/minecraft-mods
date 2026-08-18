package com.chimericdream.betterportallinking.config;

import com.chimericdream.betterportallinking.ModInfo;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BetterPortalLinkingConfig {
    @SerialEntry
    public boolean enableAddressLinking = Defaults.ENABLE_ADDRESS_LINKING;
    @SerialEntry
    public boolean logLinkingDecisions = Defaults.LOG_LINKING_DECISIONS;

    public static ConfigClassHandler<BetterPortalLinkingConfig> HANDLER = ConfigClassHandler.createBuilder(BetterPortalLinkingConfig.class)
        .id(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(YACLPlatform.getConfigDir().resolve("betterportallinking.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    public static void load() {
        HANDLER.load();
    }

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, ((defaults, config, builder) -> builder
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
        )).generateScreen(parent);
    }

    public static class Defaults {
        public static boolean ENABLE_ADDRESS_LINKING = true;
        public static boolean LOG_LINKING_DECISIONS = false;
    }
}
