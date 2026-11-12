package com.chimericdream.cobblicious.fabric.data;

import com.chimericdream.cobblicious.crop.ModCrops;
import com.chimericdream.cobblicious.crop.WarpedWartPlantBlock;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.model.ModelUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class WarpedWartDataGenerator implements FabricBlockDataGenerator {
    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Item item = ModCrops.WARPED_WART_ITEM.get();

        generator.shapeless(RecipeCategory.BUILDING_BLOCKS, item, 9)
            .requires(Blocks.WARPED_WART_BLOCK)
            .unlockedBy(RecipeProvider.getHasName(Blocks.WARPED_WART_BLOCK), generator.has(Blocks.WARPED_WART_BLOCK))
            .save(exporter);

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.WARPED_WART_BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .define('#', item)
            .unlockedBy(RecipeProvider.getHasName(item), generator.has(item))
            .save(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        HolderGetter<Enchantment> enchantments = registryLookup.lookupOrThrow(Registries.ENCHANTMENT);

        generator.add(
            ModCrops.WARPED_WART_PLANT_BLOCK.get(),
            block -> LootTable.lootTable()
                .pool(
                    generator.applyExplosionDecay(
                        block,
                        LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(
                                LootItem.lootTableItem(ModCrops.WARPED_WART_ITEM.get())
                                    .apply(
                                        SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))
                                            .when(
                                                LootItemBlockStatePropertyCondition
                                                    .hasBlockStateProperties(block)
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WarpedWartPlantBlock.AGE, 3))
                                            )
                                    )
                                    .apply(
                                        ApplyBonusCount.addUniformBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE))
                                            .when(
                                                LootItemBlockStatePropertyCondition
                                                    .hasBlockStateProperties(block)
                                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(WarpedWartPlantBlock.AGE, 3))
                                            )
                                    )
                            )
                    ).build()
                )
        );
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block crop = ModCrops.WARPED_WART_PLANT_BLOCK.get();

        ModelUtils.registerCrop(blockStateModelGenerator, crop, BlockStateProperties.AGE_3, 0, 1, 1, 2);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ModCrops.WARPED_WART_PLANT_BLOCK.get(), "Warped Wart");
        translationBuilder.add(ModCrops.WARPED_WART_ITEM.get(), "Warped Wart");
    }
}
