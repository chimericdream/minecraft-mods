package com.chimericdream.allhallowssteve.fabric.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.LitDecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.client.color.DecoratedPumpkinItemTintSource;
import com.chimericdream.allhallowssteve.client.render.DecoratedPumpkinItemRenderer;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import com.chimericdream.lib.fabric.blocks.model.ModelUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.TransmuteRecipeBuilder;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Optional;
import java.util.function.Function;

/**
 * A lit decorated pumpkin variant (see {@link LitDecoratedPumpkinBlock}). Shares
 * {@code DecoratedPumpkinBlockDataGenerator}'s block model/texture setup exactly (same base cube, same
 * {@code decorated_pumpkin_top}/{@code _side} textures, same tinted+special composite item model) — the
 * only difference is the item model's special layer carries this variant's overlay suffix, and it gets
 * a {@link TransmuteRecipeBuilder} recipe (plain decorated pumpkin + this variant's torch item) instead
 * of no recipe at all. {@code TransmuteRecipe} carries the input pumpkin's own components (dyed color,
 * carved stencils) onto the result, mirrored on the way back out by
 * {@link LitDecoratedPumpkinBlock#useItemOn}'s shears handling.
 */
public class LitDecoratedPumpkinBlockDataGenerator implements FabricBlockDataGenerator {
    private static final ModelTemplate TEMPLATE = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/template_decorated_pumpkin")),
        Optional.empty(),
        TextureSlot.TOP, TextureSlot.SIDE
    );

    protected final LitDecoratedPumpkinBlock block;

    public LitDecoratedPumpkinBlockDataGenerator(LitDecoratedPumpkinBlock block) {
        this.block = block;
    }

    private static Material texture(String suffix) {
        return new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/decorated_pumpkin/decorated_pumpkin_" + suffix));
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.MINEABLE_WITH_AXE)
            .setReplace(false)
            .add(block.builtInRegistryHolder().key());
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        Item item = block.asItem();

        LootPool.Builder pool = LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(generator.applyExplosionCondition(
                block,
                LootItem.lootTableItem(item)
                    .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                        .include(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get())
                        .include(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get()))
            ));

        generator.add(block, LootTable.lootTable().withPool(pool));
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Item pumpkinItem = ModBlocks.DECORATED_PUMPKIN.get().asItem();

        TransmuteRecipeBuilder.transmute(RecipeCategory.DECORATIONS, Ingredient.of(pumpkinItem), Ingredient.of(block.torchItem), block.asItem())
            .unlockedBy(RecipeProvider.getHasName(pumpkinItem), generator.has(pumpkinItem))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, block.displayName);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.TOP, texture("top"))
            .put(TextureSlot.SIDE, texture("side"));

        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(block, "", TEMPLATE, unused -> textures);

        ModelUtils.registerBlockWithHorizontalFacing(blockStateModelGenerator, LitDecoratedPumpkinBlock.FACING, block, modelId);

        blockStateModelGenerator.itemModelOutput.accept(
            block.asItem(),
            ItemModelUtils.composite(
                ItemModelUtils.tintedModel(modelId, new DecoratedPumpkinItemTintSource(DyedColorComponent.DEFAULT_COLOR)),
                ItemModelUtils.specialModel(modelId, new DecoratedPumpkinItemRenderer.Unbaked(block.overlaySuffix))
            )
        );
    }
}
