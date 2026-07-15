package com.chimericdream.minekea.fabric.crop;

import com.chimericdream.minekea.crop.ModCrops;
import com.chimericdream.minekea.crop.WarpedWartPlantBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class WarpedWartCropDataGenerator extends ChimericLibBlockDataGenerator {
    public static final Block BLOCK = ModCrops.WARPED_WART_PLANT_BLOCK.get();

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        HolderGetter<Enchantment> impl = registryLookup.lookupOrThrow(Registries.ENCHANTMENT);

        generator.add(
            BLOCK,
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
                                        ApplyBonusCount.addUniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE))
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

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, "Warped Wart");
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        CustomBlockStateModelSupplier.registerCrop(blockStateModelGenerator, BLOCK, BlockStateProperties.AGE_3, 0, 1, 1, 2);
    }
}
