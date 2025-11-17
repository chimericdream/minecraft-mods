//package com.chimericdream.minekea.fabric.block.furniture;
//
//import com.chimericdream.lib.util.Tool;
//import com.chimericdream.minekea.ModInfo;
//import com.chimericdream.minekea.block.furniture.shutters.ShutterBlock;
//import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
//import com.chimericdream.minekea.resource.MinekeaTextures;
//import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
//import net.minecraft.block.Block;
//import net.minecraft.client.data.BlockStateModelGenerator;
//import net.minecraft.client.data.Model;
//import net.minecraft.client.data.TextureMap;
//import net.minecraft.data.loottable.BlockLootTableGenerator;
//import net.minecraft.data.recipe.RecipeExporter;
//import net.minecraft.data.recipe.RecipeGenerator;
//import net.minecraft.data.tag.ProvidedTagBuilder;
//import net.minecraft.recipe.book.RecipeCategory;
//import net.minecraft.registry.Registries;
//import net.minecraft.registry.RegistryWrapper;
//import net.minecraft.registry.tag.TagKey;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.Direction;
//
//import java.util.Optional;
//import java.util.function.Function;
//
//public class ShutterBlockDataGenerator extends ChimericLibBlockDataGenerator {
//    protected static final Model CLOSED_MODEL = makeModel("block/furniture/shutters/closed");
//    protected static final Model OPEN_MODEL = makeModel("block/furniture/shutters/open");
//
//    protected final ShutterBlock BLOCK;
//
//    public ShutterBlockDataGenerator(Block block) {
//        BLOCK = (ShutterBlock) block;
//    }
//
//    protected static Model makeModel(String path) {
//        return new Model(
//            Optional.of(Identifier.of(ModInfo.MOD_ID, path)),
//            Optional.empty(),
//            MinekeaTextures.PANEL,
//            MinekeaTextures.FRAME
//        );
//    }
//
//    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
//        Block plankIngredient = BLOCK.config.getIngredient();
//        Block logIngredient = BLOCK.config.getIngredient("log");
//
//        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
//            .pattern("#X#")
//            .pattern("#X#")
//            .pattern("#X#")
//            .input('X', plankIngredient)
//            .input('#', logIngredient)
//            .criterion(RecipeGenerator.hasItem(plankIngredient),
//                generator.conditionsFromItem(plankIngredient))
//            .criterion(RecipeGenerator.hasItem(logIngredient),
//                generator.conditionsFromItem(logIngredient))
//            .offerTo(exporter);
//    }
//
//    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
//        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
//        getBuilder.apply(tool.getMineableTag())
//            .setReplace(false)
//            .add(BLOCK);
//    }
//
//    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
//        translationBuilder.add(BLOCK, String.format("%s Shutters", BLOCK.config.getMaterialName()));
//        translationBuilder.add(BLOCK.asItem(), String.format("%s Shutters", BLOCK.config.getMaterialName()));
//    }
//
//    public void configureBlockLootTables(BlockLootTableGenerator generator) {
//        generator.addDrop(BLOCK);
//    }
//
//    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//        Block plankIngredient = BLOCK.config.getIngredient();
//        Block logIngredient = BLOCK.config.getIngredient("log");
//
//        TextureMap textures = new TextureMap()
//            .put(MinekeaTextures.FRAME, Registries.BLOCK.getId(logIngredient).withPrefixedPath("block/"))
//            .put(MinekeaTextures.PANEL, Registries.BLOCK.getId(plankIngredient).withPrefixedPath("block/"));
//
//        Identifier closedModelId = blockStateModelGenerator.createSubModel(BLOCK, "", CLOSED_MODEL, unused -> textures);
//        Identifier openModelId = blockStateModelGenerator.createSubModel(BLOCK, "_open", OPEN_MODEL, unused -> textures);
//
//        blockStateModelGenerator.blockStateCollector
//            .accept(
//                MultipartBlockStateSupplier.create(BLOCK)
//                    .with(
//                        When.create()
//                            .set(ShutterBlock.WALL_SIDE, Direction.NORTH)
//                            .set(ShutterBlock.OPEN, false),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.Y, VariantSettings.Rotation.R180)
//                            .put(VariantSettings.MODEL, closedModelId)
//                    )
//                    .with(
//                        When.create()
//                            .set(ShutterBlock.WALL_SIDE, Direction.SOUTH)
//                            .set(ShutterBlock.OPEN, false),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.MODEL, closedModelId)
//                    )
//                    .with(
//                        When.create()
//                            .set(ShutterBlock.WALL_SIDE, Direction.EAST)
//                            .set(ShutterBlock.OPEN, false),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.Y, VariantSettings.Rotation.R270)
//                            .put(VariantSettings.MODEL, closedModelId)
//                    )
//                    .with(
//                        When.create()
//                            .set(ShutterBlock.WALL_SIDE, Direction.WEST)
//                            .set(ShutterBlock.OPEN, false),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.Y, VariantSettings.Rotation.R90)
//                            .put(VariantSettings.MODEL, closedModelId)
//                    )
//                    .with(
//                        When.create()
//                            .set(ShutterBlock.OPEN, true),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.MODEL, openModelId)
//                    )
//            );
//    }
//}
