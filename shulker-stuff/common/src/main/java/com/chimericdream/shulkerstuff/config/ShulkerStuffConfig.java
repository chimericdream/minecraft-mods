package com.chimericdream.shulkerstuff.config;

import com.chimericdream.lib.config.YaclConfig;
import com.chimericdream.shulkerstuff.ModInfo;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;

public class ShulkerStuffConfig {
    @SerialEntry
    public int platedShulkerUpgradeChance = Defaults.PLATED_SHULKER_UPGRADE_CHANCE;

    public static final YaclConfig<ShulkerStuffConfig> CONFIG = YaclConfig.builder(ShulkerStuffConfig.class, ModInfo.MOD_ID)
        .screen((defaults, config, builder) -> builder
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
        )
        .build();

    public static class Defaults {
        public static int PLATED_SHULKER_UPGRADE_CHANCE = 12;
    }
}
