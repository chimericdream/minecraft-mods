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
    @SerialEntry
    public boolean enableCrouchBridging = Defaults.ENABLE_CROUCH_BRIDGING;
    @SerialEntry
    public int crouchBridgeMaxGapBlocks = Defaults.CROUCH_BRIDGE_MAX_GAP_BLOCKS;
    @SerialEntry
    public int crouchBridgeLookDownThreshold = Defaults.CROUCH_BRIDGE_LOOK_DOWN_THRESHOLD;

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
                .option(Option.<Boolean>createBuilder()
                    .name(Component.translatable("text.config.sneakytweaks.option.enableCrouchBridging"))
                    .description(OptionDescription.of(Component.literal("")))
                    .binding(Defaults.ENABLE_CROUCH_BRIDGING, () -> config.enableCrouchBridging, newVal -> config.enableCrouchBridging = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.sneakytweaks.option.crouchBridgeMaxGapBlocks"))
                    .description(OptionDescription.of(Component.literal("")))
                    .binding(Defaults.CROUCH_BRIDGE_MAX_GAP_BLOCKS, () -> config.crouchBridgeMaxGapBlocks, newVal -> config.crouchBridgeMaxGapBlocks = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.sneakytweaks.option.crouchBridgeLookDownThreshold"))
                    .description(OptionDescription.of(Component.literal("")))
                    .binding(Defaults.CROUCH_BRIDGE_LOOK_DOWN_THRESHOLD, () -> config.crouchBridgeLookDownThreshold, newVal -> config.crouchBridgeLookDownThreshold = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1).max(89))
                    .build())
                .build())
        )).generateScreen(parent);
    }

    public static class Defaults {
        public static boolean ENABLE_CAMPFIRE_SNEAKING = true;
        public static int CAMPFIRE_GRACE_TICKS = 200;
        public static boolean ENABLE_CROUCH_BRIDGING = true;
        public static int CROUCH_BRIDGE_MAX_GAP_BLOCKS = 3;
        public static int CROUCH_BRIDGE_LOOK_DOWN_THRESHOLD = 30;
    }
}
