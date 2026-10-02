package com.chimericdream.allhallowssteve.fabric.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.CandleLitDecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
 * The candle variant (see {@link CandleLitDecoratedPumpkinBlock}). The block model is the same tinted
 * cube every decorated pumpkin uses (see {@code DecoratedPumpkinBlockDataGenerator}), rotated by
 * {@code FACING}; the candle count and lit state only change the overlay and the light, which the block
 * entity renderer handles, so every {@code CANDLES}/{@code LIT} combination points at the same model.
 * <p>
 * This block has no item, so there is no item model and no recipe, and the loot table drops a plain
 * decorated pumpkin with the dye color and stencils copied from the block entity. The candles
 * themselves are added to the drops in {@link CandleLitDecoratedPumpkinBlock#getDrops}, because the
 * loot-table system can't express "each candle in the color it was added with".
 */
public class CandleLitDecoratedPumpkinBlockDataGenerator implements FabricBlockDataGenerator {
    private static final ModelTemplate TEMPLATE = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/template_decorated_pumpkin")),
        Optional.empty(),
        TextureSlot.TOP, TextureSlot.SIDE
    );

    protected final CandleLitDecoratedPumpkinBlock block;

    public CandleLitDecoratedPumpkinBlockDataGenerator(CandleLitDecoratedPumpkinBlock block) {
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
        Item item = ModBlocks.DECORATED_PUMPKIN.get().asItem();

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
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(block, "Candle-lit Decorated Pumpkin");
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.TOP, texture("top"))
            .put(TextureSlot.SIDE, texture("side"));

        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(block, "", TEMPLATE, unused -> textures);
        MultiVariant model = BlockModelGenerators.plainVariant(modelId);

        blockStateModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(CandleLitDecoratedPumpkinBlock.FACING, CandleLitDecoratedPumpkinBlock.CANDLES, CandleLitDecoratedPumpkinBlock.LIT)
                    .generate((facing, candles, lit) -> rotated(model, facing)))
        );
    }

    private static MultiVariant rotated(MultiVariant model, Direction facing) {
        return switch (facing) {
            case EAST -> model.with(VariantMutator.Y_ROT.withValue(Quadrant.R90));
            case SOUTH -> model.with(VariantMutator.Y_ROT.withValue(Quadrant.R180));
            case WEST -> model.with(VariantMutator.Y_ROT.withValue(Quadrant.R270));
            default -> model;
        };
    }
}
