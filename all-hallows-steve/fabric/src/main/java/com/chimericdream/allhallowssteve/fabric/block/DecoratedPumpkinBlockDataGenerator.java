package com.chimericdream.allhallowssteve.fabric.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.DecoratedPumpkinBlock;
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
 * The decorated pumpkin's block model is a fully static full-block model — no vanilla
 * {@code ModelTemplates} entry bakes a tintindex onto a top/side cube, so this points a custom
 * {@link ModelTemplate} at the hand-authored {@code block/template_decorated_pumpkin} parent (same
 * elements/tintindex layout the hand-written model used to inline directly) instead, whose top/side
 * faces are tintindex-0 so {@link DecoratedPumpkinItemTintSource} (item) and
 * {@link com.chimericdream.allhallowssteve.client.color.DecoratedPumpkinBlockColors} (placed block)
 * can recolor it from the stored {@link DyedColorComponent}. {@link DecoratedPumpkinBlock#FACING} is
 * dispatched with chimeric-lib's {@code ModelUtils.registerBlockWithHorizontalFacing} (same rotation
 * dispatch as vanilla's furnace), which {@code DecoratedPumpkinStencilRenderer} mirrors at render time
 * so a carved stencil overlay lands on the same real-world face the rotated block model does.
 *
 * <p>The item model is a composite of that same tinted model plus a {@code minecraft:special} layer
 * for the stencil overlays ({@link DecoratedPumpkinItemRenderer}). {@link BlockModelGenerators} has no
 * public helper for a composite/special item model — only single-purpose ones like
 * {@code registerSimpleTintedItemModel} — so this reaches its private {@code itemModelOutput} field
 * directly (widened in {@code allhallowssteve.accesswidener}) rather than hand-authoring the JSON:
 * vanilla's own {@code ModelProvider$ItemInfoCollector.finalizeAndValidate} always overwrites any block
 * item datagen left unregistered with a plain fallback model, which would silently clobber a
 * hand-authored file at the same path on every datagen run.
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
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, "Decorated Pumpkin");
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.TOP, texture("top"))
            .put(TextureSlot.SIDE, texture("side"));

        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(block, "", TEMPLATE, unused -> textures);

        ModelUtils.registerBlockWithHorizontalFacing(blockStateModelGenerator, DecoratedPumpkinBlock.FACING, block, modelId);

        blockStateModelGenerator.itemModelOutput.accept(
            block.asItem(),
            ItemModelUtils.composite(
                ItemModelUtils.tintedModel(modelId, new DecoratedPumpkinItemTintSource(DyedColorComponent.DEFAULT_COLOR)),
                // "base" isn't just a fallback icon — SpecialModelWrapper also reads *this* model's own
                // baked display transforms (the standard block gui/ground/fixed/hand scale-down) as the
                // special layer's ModelRenderProperties. Pointing it at a bare texture (tried first)
                // resolves to the missing-model placeholder instead, whose display properties are
                // identity — the stencil decals rendered at full 1:1 block scale while the sibling
                // tinted-model layer correctly shrank to item size. Reusing the same block model here
                // gives both composite layers the identical scale.
                ItemModelUtils.specialModel(modelId, new DecoratedPumpkinItemRenderer.Unbaked(""))
            )
        );
    }
}
