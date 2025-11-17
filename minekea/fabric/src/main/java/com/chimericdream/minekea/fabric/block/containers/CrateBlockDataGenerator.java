//package com.chimericdream.minekea.fabric.block.containers;
//
//import com.chimericdream.lib.util.Tool;
//import com.chimericdream.minekea.ModInfo;
//import com.chimericdream.minekea.block.containers.crates.CrateBlock;
//import com.chimericdream.minekea.client.screen.crate.CrateScreenHandler;
//import com.chimericdream.minekea.client.screen.crate.DoubleCrateScreenHandler;
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
//import net.minecraft.item.Item;
//import net.minecraft.recipe.book.RecipeCategory;
//import net.minecraft.registry.RegistryWrapper;
//import net.minecraft.registry.tag.TagKey;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.Direction;
//
//import java.util.Optional;
//import java.util.function.Function;
//
//public class CrateBlockDataGenerator extends ChimericLibBlockDataGenerator {
//    protected static final Model CRATE_MODEL = makeModel("block/containers/crate");
//    protected static final Model HALF_DOUBLE_CRATE_MODEL = makeModel("block/containers/double_crate_half");
//    protected static final Model RIGHT_HALF_DOUBLE_CRATE_MODEL = makeModel("block/containers/double_crate_half_right");
//
//    protected final CrateBlock BLOCK;
//
//    public CrateBlockDataGenerator(Block block) {
//        BLOCK = (CrateBlock) block;
//    }
//
//    protected static Model makeModel(String path) {
//        return new Model(
//            Optional.of(Identifier.of(ModInfo.MOD_ID, path)),
//            Optional.empty(),
//            MinekeaTextures.BRACE,
//            MinekeaTextures.MATERIAL
//        );
//    }
//
//    @Override
//    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
//        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
//        getBuilder.apply(tool.getMineableTag())
//            .setReplace(false)
//            .add(BLOCK);
//    }
//
//    @Override
//    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
//        Block ingredient1 = BLOCK.config.getIngredient();
//        TagKey<Item> ingredient2 = BLOCK.config.getTagIngredient();
//
//        generator.createShaped(RecipeCategory.MISC, BLOCK, 1)
//            .pattern("#X#")
//            .pattern("XXX")
//            .pattern("#X#")
//            .input('X', ingredient1)
//            .input('#', ingredient2)
//            .criterion(RecipeGenerator.hasItem(ingredient1),
//                generator.conditionsFromItem(ingredient1))
//            .criterion("has_log",
//                generator.conditionsFromTag(ingredient2))
//            .offerTo(exporter);
//    }
//
//    @Override
//    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
//        translationBuilder.add(BLOCK, String.format("%s Crate", BLOCK.config.getMaterialName()));
//        translationBuilder.add(BLOCK.asItem(), String.format("%s Crate", BLOCK.config.getMaterialName()));
//    }
//
//    @Override
//    public void configureBlockLootTables(BlockLootTableGenerator generator) {
//        generator.addDrop(BLOCK);
//    }
//
//    protected void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator, TextureMap textures) {
//        Identifier subModelId = blockStateModelGenerator.createSubModel(BLOCK, "", CRATE_MODEL, unused -> textures);
//        Identifier leftHalfModelId = blockStateModelGenerator.createSubModel(BLOCK, "_double_half", HALF_DOUBLE_CRATE_MODEL, unused -> textures);
//        Identifier rightHalfModelId = blockStateModelGenerator.createSubModel(BLOCK, "_double_half_right", RIGHT_HALF_DOUBLE_CRATE_MODEL, unused -> textures);
//
//        blockStateModelGenerator.blockStateCollector
//            .accept(
//                MultipartBlockStateSupplier.create(BLOCK)
//                    .with(
//                        When.create()
//                            .set(CrateBlock.AXIS, Direction.Axis.X),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.X, VariantSettings.Rotation.R90)
//                            .put(VariantSettings.Y, VariantSettings.Rotation.R90)
//                            .put(VariantSettings.MODEL, subModelId)
//                    )
//                    .with(
//                        When.create()
//                            .set(CrateBlock.AXIS, Direction.Axis.Z),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.X, VariantSettings.Rotation.R90)
//                            .put(VariantSettings.MODEL, subModelId)
//                    )
//                    .with(
//                        When.create()
//                            .set(CrateBlock.CONNECTED_NORTH, false)
//                            .set(CrateBlock.CONNECTED_SOUTH, false)
//                            .set(CrateBlock.CONNECTED_EAST, false)
//                            .set(CrateBlock.CONNECTED_WEST, false)
//                            .set(CrateBlock.AXIS, Direction.Axis.Y),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.MODEL, subModelId)
//                    )
//                    .with(
//                        When.create()
//                            .set(CrateBlock.CONNECTED_NORTH, false)
//                            .set(CrateBlock.CONNECTED_SOUTH, false)
//                            .set(CrateBlock.CONNECTED_EAST, false)
//                            .set(CrateBlock.CONNECTED_WEST, true)
//                            .set(CrateBlock.AXIS, Direction.Axis.Y),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.MODEL, leftHalfModelId)
//                    )
//                    .with(
//                        When.create()
//                            .set(CrateBlock.CONNECTED_NORTH, true)
//                            .set(CrateBlock.CONNECTED_SOUTH, false)
//                            .set(CrateBlock.CONNECTED_EAST, false)
//                            .set(CrateBlock.CONNECTED_WEST, false)
//                            .set(CrateBlock.AXIS, Direction.Axis.Y),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.Y, VariantSettings.Rotation.R90)
//                            .put(VariantSettings.MODEL, leftHalfModelId)
//                    )
//                    .with(
//                        When.create()
//                            .set(CrateBlock.CONNECTED_NORTH, false)
//                            .set(CrateBlock.CONNECTED_SOUTH, false)
//                            .set(CrateBlock.CONNECTED_EAST, true)
//                            .set(CrateBlock.CONNECTED_WEST, false)
//                            .set(CrateBlock.AXIS, Direction.Axis.Y),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.MODEL, rightHalfModelId)
//                    )
//                    .with(
//                        When.create()
//                            .set(CrateBlock.CONNECTED_NORTH, false)
//                            .set(CrateBlock.CONNECTED_SOUTH, true)
//                            .set(CrateBlock.CONNECTED_EAST, false)
//                            .set(CrateBlock.CONNECTED_WEST, false)
//                            .set(CrateBlock.AXIS, Direction.Axis.Y),
//                        BlockStateVariant.create()
//                            .put(VariantSettings.Y, VariantSettings.Rotation.R90)
//                            .put(VariantSettings.MODEL, rightHalfModelId)
//                    )
//            );
//    }
//
//    @Override
//    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//        TextureMap textures = new TextureMap()
//            .put(MinekeaTextures.BRACE, BLOCK.config.getTexture("brace"))
//            .put(MinekeaTextures.MATERIAL, BLOCK.config.getTexture());
//
//        this.configureBlockStateModels(blockStateModelGenerator, textures);
//    }
//
//    public static class SharedCrateDataGenerator extends ChimericLibBlockDataGenerator {
//        @Override
//        public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
//            translationBuilder.add(CrateScreenHandler.SCREEN_ID, "Crate");
//            translationBuilder.add(DoubleCrateScreenHandler.SCREEN_ID, "Large Crate");
//            translationBuilder.add(CrateScreenHandler.TRAPPED_SCREEN_ID, "Trapped Crate");
//            translationBuilder.add(DoubleCrateScreenHandler.TRAPPED_SCREEN_ID, "Trapped Large Crate");
//        }
//    }
//}
