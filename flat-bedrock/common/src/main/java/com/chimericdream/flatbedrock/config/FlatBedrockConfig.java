package com.chimericdream.flatbedrock.config;

import com.chimericdream.flatbedrock.FlatBedrockMod;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FlatBedrockConfig {
    @SerialEntry
    public int overworldFloorThickness = Defaults.FLOOR_THICKNESS;
    @SerialEntry
    public String overworldReplacementBlock = Defaults.REPLACEMENT_BLOCK;

    @SerialEntry
    public int netherFloorThickness = Defaults.FLOOR_THICKNESS;
    @SerialEntry
    public String netherReplacementBlock = Defaults.REPLACEMENT_BLOCK;
    @SerialEntry
    public boolean netherNoRoof = Defaults.NETHER_NO_ROOF;
    @SerialEntry
    public int netherRoofThickness = Defaults.ROOF_THICKNESS;

    public static ConfigClassHandler<FlatBedrockConfig> HANDLER = ConfigClassHandler.createBuilder(FlatBedrockConfig.class)
        .id(Identifier.fromNamespaceAndPath(FlatBedrockMod.MOD_ID, "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(YACLPlatform.getConfigDir().resolve("flatbedrock.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    public static void load() {
        HANDLER.load();
    }

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, ((defaults, config, builder) -> builder
            .title(Component.literal("Flat Bedrock Config"))
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Overworld"))
                .option(Option.<Integer>createBuilder()
                    .name(Component.literal("Floor thickness"))
                    .description(OptionDescription.of(Component.literal("How many layers of bedrock to generate at the bottom of the Overworld. Set to 0 to remove it entirely. Default: 1")))
                    .binding(Defaults.FLOOR_THICKNESS, () -> config.overworldFloorThickness, newVal -> config.overworldFloorThickness = newVal)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 10)
                        .step(1))
                    .build())
                .option(Option.<String>createBuilder()
                    .name(Component.literal("Replacement block"))
                    .description(OptionDescription.of(Component.literal("The block ID to generate instead of bedrock in the Overworld. Default: minecraft:bedrock")))
                    .binding(Defaults.REPLACEMENT_BLOCK, () -> config.overworldReplacementBlock, newVal -> config.overworldReplacementBlock = newVal)
                    .controller(StringControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Nether"))
                .option(Option.<Integer>createBuilder()
                    .name(Component.literal("Floor thickness"))
                    .description(OptionDescription.of(Component.literal("How many layers of bedrock to generate at the bottom of the Nether. Set to 0 to remove it entirely. Default: 1")))
                    .binding(Defaults.FLOOR_THICKNESS, () -> config.netherFloorThickness, newVal -> config.netherFloorThickness = newVal)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 10)
                        .step(1))
                    .build())
                .option(Option.<Boolean>createBuilder()
                    .name(Component.literal("No roof"))
                    .description(OptionDescription.of(Component.literal("Removes the bedrock ceiling from the top of the Nether entirely.")))
                    .binding(Defaults.NETHER_NO_ROOF, () -> config.netherNoRoof, newVal -> config.netherNoRoof = newVal)
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Component.literal("Roof thickness"))
                    .description(OptionDescription.of(Component.literal("How many layers of bedrock to generate at the top of the Nether. Ignored while \"No roof\" is enabled. Default: 1")))
                    .binding(Defaults.ROOF_THICKNESS, () -> config.netherRoofThickness, newVal -> config.netherRoofThickness = newVal)
                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(1, 10)
                        .step(1))
                    .build())
                .option(Option.<String>createBuilder()
                    .name(Component.literal("Replacement block"))
                    .description(OptionDescription.of(Component.literal("The block ID to generate instead of bedrock in the Nether (both floor and roof). Default: minecraft:bedrock")))
                    .binding(Defaults.REPLACEMENT_BLOCK, () -> config.netherReplacementBlock, newVal -> config.netherReplacementBlock = newVal)
                    .controller(StringControllerBuilder::create)
                    .build())
                .build())
        )).generateScreen(parent);
    }

    /**
     * Overworld and Nether share the same "bedrock_floor" gradient name, so an unrecognized (e.g.
     * modded) dimension has no per-dimension setting of its own. Falling back to the Overworld
     * setting keeps this mod's pre-config behavior (always flatten) for anything it can't identify,
     * rather than silently doing nothing.
     */
    public static int resolveFloorThickness(ResourceKey<Level> dimension) {
        FlatBedrockConfig config = HANDLER.instance();
        return dimension == Level.NETHER ? config.netherFloorThickness : config.overworldFloorThickness;
    }

    /**
     * Only the Nether has a bedrock roof in vanilla, so roof settings aren't split per-dimension.
     */
    public static int resolveRoofThickness() {
        FlatBedrockConfig config = HANDLER.instance();
        return config.netherNoRoof ? 0 : config.netherRoofThickness;
    }

    public static BlockState resolveReplacementBlockState(ResourceKey<Level> dimension) {
        FlatBedrockConfig config = HANDLER.instance();
        String blockId = dimension == Level.NETHER ? config.netherReplacementBlock : config.overworldReplacementBlock;
        return resolveBlockState(blockId);
    }

    private static BlockState resolveBlockState(String blockId) {
        Identifier id = Identifier.tryParse(blockId);
        if (id == null) {
            return Blocks.BEDROCK.defaultBlockState();
        }

        return BuiltInRegistries.BLOCK.getOptional(id).map(Block::defaultBlockState).orElse(Blocks.BEDROCK.defaultBlockState());
    }

    public static class Defaults {
        public static int FLOOR_THICKNESS = 1;
        public static int ROOF_THICKNESS = 1;
        public static boolean NETHER_NO_ROOF = false;
        public static String REPLACEMENT_BLOCK = "minecraft:bedrock";
    }
}
