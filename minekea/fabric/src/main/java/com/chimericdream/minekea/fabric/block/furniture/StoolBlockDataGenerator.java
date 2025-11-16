//package com.chimericdream.minekea.fabric.block.furniture;
//
//import com.chimericdream.lib.util.Tool;
//import com.chimericdream.minekea.ModInfo;
//import com.chimericdream.minekea.block.furniture.seats.StoolBlock;
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
//
//import java.util.Optional;
//import java.util.function.Function;
//
//public class StoolBlockDataGenerator extends ChimericLibBlockDataGenerator {
//    private final StoolBlock BLOCK;
//
//    protected static final Model STOOL_MODEL = new Model(
//        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/seating/stool")),
//        Optional.empty(),
//        MinekeaTextures.LOG,
//        MinekeaTextures.PLANKS
//    );
//
//    public StoolBlockDataGenerator(Block block) {
//        this.BLOCK = (StoolBlock) block;
//    }
//
//    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
//        Block plankIngredient = BLOCK.config.getIngredient();
//        Block logIngredient = BLOCK.config.getIngredient("log");
//
//        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 2)
//            .pattern("PP")
//            .pattern("LL")
//            .input('P', plankIngredient)
//            .input('L', logIngredient)
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
//        translationBuilder.add(BLOCK, String.format("%s Stool", BLOCK.config.getMaterialName()));
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
//            .put(MinekeaTextures.LOG, Registries.BLOCK.getId(logIngredient).withPrefixedPath("block/"))
//            .put(MinekeaTextures.PLANKS, Registries.BLOCK.getId(plankIngredient).withPrefixedPath("block/"));
//
//        blockStateModelGenerator.registerSingleton(
//            BLOCK,
//            textures,
//            STOOL_MODEL
//        );
//    }
//}
