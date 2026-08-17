package com.chimericdream.shulkerstuff.config;

import com.chimericdream.shulkerstuff.ModInfo;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ShulkerStuffConfig {
    @SerialEntry
    public int platedShulkerUpgradeChance = Defaults.PLATED_SHULKER_UPGRADE_CHANCE;

    public static ConfigClassHandler<ShulkerStuffConfig> HANDLER = ConfigClassHandler.createBuilder(ShulkerStuffConfig.class)
        .id(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(YACLPlatform.getConfigDir().resolve("shulkerstuff.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, ((defaults, config, builder) -> builder
            .title(Component.translatable("text.config.shulkerstuff.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.config.shulkerstuff.title"))
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("text.config.shulkerstuff.option.platedShulkerUpgradeChance"))
                    .description(OptionDescription.of(Component.literal("")))
                    .binding(Defaults.PLATED_SHULKER_UPGRADE_CHANCE, () -> config.platedShulkerUpgradeChance, newVal -> config.platedShulkerUpgradeChance = newVal)
                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                    .build())
                .build())
        )).generateScreen(parent);
    }

    public static class Defaults {
        public static int PLATED_SHULKER_UPGRADE_CHANCE = 12;
    }
}
