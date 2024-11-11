package com.chimericdream.villagertweaks.config;

import com.chimericdream.villagertweaks.ModInfo;
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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VillagerTweaksConfig {
    @SerialEntry
    public boolean enableMaxTradeOverride = Defaults.ENABLE_MAX_TRADE_OVERRIDE;
    @SerialEntry
    public int maxTradesOverrideAmount = Defaults.MAX_TRADES_OVERRIDE_AMOUNT;
    @SerialEntry
    public boolean enableDemandBonus = Defaults.ENABLE_DEMAND_BONUS;
    @SerialEntry
    public boolean enableGlobalReputation = Defaults.ENABLE_GLOBAL_REPUTATION;
    @SerialEntry
    public boolean enableBadReputation = Defaults.ENABLE_BAD_REPUTATION;

    @SerialEntry
    public boolean enableConversionTimeOverride = Defaults.ENABLE_CONVERSION_TIME_OVERRIDE;
    @SerialEntry
    public int conversionTime = Defaults.CONVERSION_TIME;
    @SerialEntry
    public boolean forceVillagerConversion = Defaults.FORCE_VILLAGER_CONVERSION;
    @SerialEntry
    public boolean displayConversionTime = Defaults.DISPLAY_CONVERSION_TIME;

    @SerialEntry
    public boolean enableEmeraldTemptation = Defaults.ENABLE_EMERALD_TEMPTATION;

    public static ConfigClassHandler<VillagerTweaksConfig> HANDLER = ConfigClassHandler.createBuilder(VillagerTweaksConfig.class)
        .id(Identifier.of(ModInfo.MOD_ID, "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(YACLPlatform.getConfigDir().resolve("villagertweaks.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    public static void load() {
        HANDLER.load();
    }

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, ((defaults, config, builder) -> builder
            .title(Text.translatable("text.config.title"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("text.config.section.trading"))
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("text.config.option.enableMaxTradeOverride"))
                    .description(OptionDescription.of(Text.translatable("text.config.option.enableMaxTradeOverride.desc")))
                    .binding(Defaults.ENABLE_MAX_TRADE_OVERRIDE, () -> config.enableMaxTradeOverride, newVal -> config.enableMaxTradeOverride = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.option.maxTradesOverrideAmount"))
                    .description(OptionDescription.of(Text.translatable("text.config.option.maxTradesOverrideAmount.desc")))
                    .binding(Defaults.MAX_TRADES_OVERRIDE_AMOUNT, () -> config.maxTradesOverrideAmount, newVal -> config.maxTradesOverrideAmount = newVal)
                    .controller(IntegerFieldControllerBuilder::create)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("text.config.option.enableDemandBonus"))
                    .description(OptionDescription.of(Text.translatable("text.config.option.enableDemandBonus.desc")))
                    .binding(Defaults.ENABLE_DEMAND_BONUS, () -> config.enableDemandBonus, newVal -> config.enableDemandBonus = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("text.config.option.enableGlobalReputation"))
                    .description(OptionDescription.of(Text.translatable("text.config.option.enableGlobalReputation.desc")))
                    .binding(Defaults.ENABLE_GLOBAL_REPUTATION, () -> config.enableGlobalReputation, newVal -> config.enableGlobalReputation = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("text.config.option.enableBadReputation"))
                    .description(OptionDescription.of(Text.translatable("text.config.option.enableBadReputation.desc")))
                    .binding(Defaults.ENABLE_BAD_REPUTATION, () -> config.enableBadReputation, newVal -> config.enableBadReputation = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("text.config.section.conversion"))
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("text.config.option.forceVillagerConversion"))
                    .description(OptionDescription.of(Text.translatable("text.config.option.forceVillagerConversion.desc")))
                    .binding(Defaults.FORCE_VILLAGER_CONVERSION, () -> config.forceVillagerConversion, newVal -> config.forceVillagerConversion = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("text.config.option.enableConversionTimeOverride"))
                    .description(OptionDescription.of(Text.translatable("text.config.option.enableConversionTimeOverride.desc")))
                    .binding(Defaults.ENABLE_CONVERSION_TIME_OVERRIDE, () -> config.enableConversionTimeOverride, newVal -> config.enableConversionTimeOverride = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Text.translatable("text.config.option.conversionTime"))
                    .description(OptionDescription.of(Text.translatable("text.config.option.conversionTime.desc")))
                    .binding(Defaults.CONVERSION_TIME, () -> config.conversionTime, newVal -> config.conversionTime = newVal)
                    .controller(IntegerFieldControllerBuilder::create)
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("text.config.option.displayConversionTime"))
                    .description(OptionDescription.of(Text.translatable("text.config.option.displayConversionTime.desc")))
                    .binding(Defaults.DISPLAY_CONVERSION_TIME, () -> config.displayConversionTime, newVal -> config.displayConversionTime = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("text.config.section.misc"))
                .option(Option.<Boolean>createBuilder()
                    .name(Text.translatable("text.config.option.enableEmeraldTemptation"))
                    .description(OptionDescription.of(Text.translatable("text.config.option.enableEmeraldTemptation.desc")))
                    .binding(Defaults.ENABLE_EMERALD_TEMPTATION, () -> config.enableEmeraldTemptation, newVal -> config.enableEmeraldTemptation = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
        )).generateScreen(parent);
    }

    public static class Defaults {
        public static boolean ENABLE_MAX_TRADE_OVERRIDE = false;
        public static int MAX_TRADES_OVERRIDE_AMOUNT = -1;
        public static boolean ENABLE_DEMAND_BONUS = true;
        public static boolean ENABLE_GLOBAL_REPUTATION = false;
        public static boolean ENABLE_BAD_REPUTATION = true;

        public static boolean FORCE_VILLAGER_CONVERSION = false;
        public static boolean ENABLE_CONVERSION_TIME_OVERRIDE = false;
        public static int CONVERSION_TIME = 3600;
        public static boolean DISPLAY_CONVERSION_TIME = false;

        public static boolean ENABLE_EMERALD_TEMPTATION = false;
    }
}
