package com.chimericdream.minekea.fabric.block.building.slabs;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.slabs.BookshelfSlabBlock;
import com.chimericdream.minekea.block.building.slabs.SlabBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureMap;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

import static com.chimericdream.minekea.fabric.block.furniture.BookshelfBlockDataGenerator.BOOKSHELF_MODEL;

public class BookshelfSlabBlockDataGenerator implements ChimericLibBlockDataGenerator {
    protected static final Model BOTTOM_BOOKSHELF_SLAB_MODEL = makeModel("block/building/slabs/bookshelves/bottom");
    protected static final Model TOP_BOOKSHELF_SLAB_MODEL = makeModel("block/building/slabs/bookshelves/top");

    private final BookshelfSlabBlock BLOCK;

    public BookshelfSlabBlockDataGenerator(Block block) {
        BLOCK = (BookshelfSlabBlock) block;
    }

    protected static Model makeModel(String path) {
        return new Model(
            Optional.of(Identifier.of(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.SHELF,
            MinekeaTextures.MATERIAL
        );
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureItemTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Item>, ProvidedTagBuilder<Item, Item>> getBuilder) {

    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .pattern("###")
            .input('#', Registries.BLOCK.get(BLOCK.BASE_BLOCK_ID))
            .criterion(RecipeGenerator.hasItem(Registries.BLOCK.get(BLOCK.BASE_BLOCK_ID)),
                generator.conditionsFromItem(Registries.BLOCK.get(BLOCK.BASE_BLOCK_ID)))
            .offerTo(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK, generator.slabDrops(BLOCK));
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Bookshelf Slab", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Identifier textureId = BLOCK.config.getTexture();

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0"))
            .put(MinekeaTextures.MATERIAL, textureId);

        Identifier coreModelId = blockStateModelGenerator.createSubModel(BLOCK, "", BOTTOM_BOOKSHELF_SLAB_MODEL, unused -> textures);
        Identifier topModelId = blockStateModelGenerator.createSubModel(BLOCK, "_top", TOP_BOOKSHELF_SLAB_MODEL, unused -> textures);
        Identifier doubleModelId = blockStateModelGenerator.createSubModel(BLOCK, "_double", BOOKSHELF_MODEL, unused -> textures);

        blockStateModelGenerator.blockStateCollector
            .accept(
                MultipartBlockStateSupplier.create(BLOCK)
                    .with(
                        When.create().set(SlabBlock.TYPE, SlabType.BOTTOM),
                        BlockStateVariant.create().put(VariantSettings.MODEL, coreModelId)
                    )
                    .with(
                        When.create().set(SlabBlock.TYPE, SlabType.TOP),
                        BlockStateVariant.create().put(VariantSettings.MODEL, topModelId)
                    )
                    .with(
                        When.create().set(SlabBlock.TYPE, SlabType.DOUBLE),
                        BlockStateVariant.create().put(VariantSettings.MODEL, doubleModelId)
                    )
            );
    }

    @Override
    public void configureItemModels(ItemModelGenerator itemModelGenerator) {

    }

    @Override
    public void generateTextures() {

    }
}
