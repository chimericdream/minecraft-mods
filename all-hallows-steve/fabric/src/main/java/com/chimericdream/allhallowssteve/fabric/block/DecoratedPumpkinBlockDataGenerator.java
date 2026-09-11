package com.chimericdream.allhallowssteve.fabric.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.client.color.DecoratedPumpkinItemTintSource;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Optional;

/**
 * The decorated pumpkin has no blockstate properties - it's a single, fully static full-block model
 * whose top/side faces are tintindex-0 so {@link DecoratedPumpkinItemTintSource} (item) and
 * {@link com.chimericdream.allhallowssteve.client.color.DecoratedPumpkinBlockColors} (placed block)
 * can recolor it from the stored {@link DyedColorComponent}. No vanilla {@code ModelTemplates} entry
 * bakes a tintindex onto a top/side cube, so this points a custom {@link ModelTemplate} at the
 * hand-authored {@code block/template_decorated_pumpkin} parent (same elements/tintindex layout the
 * hand-written model used to inline directly) instead.
 */
public class DecoratedPumpkinBlockDataGenerator implements FabricBlockDataGenerator {
    private static final ModelTemplate TEMPLATE = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/template_decorated_pumpkin")),
        Optional.empty(),
        TextureSlot.TOP, TextureSlot.SIDE
    );

    protected final Block block;

    public DecoratedPumpkinBlockDataGenerator(Block block) {
        this.block = block;
    }

    private static Material texture(String suffix) {
        return new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/decorated_pumpkin/decorated_pumpkin_" + suffix));
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
                        .include(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get()))
            ));

        generator.add(block, LootTable.lootTable().withPool(pool));
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, "Decorated Pumpkin");
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.TOP, texture("top"))
            .put(TextureSlot.SIDE, texture("side"));

        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(block, "", TEMPLATE, unused -> textures);
        MultiVariant model = BlockModelGenerators.plainVariant(modelId);

        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, model));
        blockStateModelGenerator.registerSimpleTintedItemModel(block, modelId, new DecoratedPumpkinItemTintSource(DyedColorComponent.DEFAULT_COLOR));
    }
}
