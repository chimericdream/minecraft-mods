package com.chimericdream.sneakytweaks.config;

import com.chimericdream.sneakytweaks.ModInfo;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SneakyTweaksConfig {
    @SerialEntry
    public boolean enableCampfireSneaking = Defaults.ENABLE_CAMPFIRE_SNEAKING;
    @SerialEntry
    public int campfireGraceTicks = Defaults.CAMPFIRE_GRACE_TICKS;

    public static ConfigClassHandler<SneakyTweaksConfig> HANDLER = ConfigClassHandler.createBuilder(SneakyTweaksConfig.class)
        .id(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(YACLPlatform.getConfigDir().resolve("sneaky-tweaks.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    public static void load() {
        HANDLER.load();
    }

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, ((defaults, config, builder) -> builder
            .title(Component.translatable("text.config.sneakytweaks.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.sneakytweaks.title"))
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.config.sneakytweaks.option.enableCampfireSneaking"))
                    .description(OptionDescription.of(Component.literal("")))
                    .binding(Defaults.ENABLE_CAMPFIRE_SNEAKING, () -> config.enableCampfireSneaking, newVal -> config.enableCampfireSneaking = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.sneakytweaks.option.campfireGraceTicks"))
                    .description(OptionDescription.of(Component.literal("")))
                    .binding(Defaults.CAMPFIRE_GRACE_TICKS, () -> config.campfireGraceTicks, newVal -> config.campfireGraceTicks = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(0))
                    .build())
                .build())
        )).generateScreen(parent);
    }

    public static class Defaults {
        public static boolean ENABLE_CAMPFIRE_SNEAKING = true;
        public static int CAMPFIRE_GRACE_TICKS = 200;
    }
}
