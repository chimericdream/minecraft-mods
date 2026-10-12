package com.chimericdream.allhallowssteve.fabric.block;

import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

/**
 * The cider and mulled-cider cauldrons have no items of their own, so breaking one drops a plain cauldron.
 * Their blockstates and models are hand-authored in resources (like Minekea's honey cauldron) since they
 * reuse vanilla's cauldron templates with a different fluid texture.
 */
public class AppleCiderCauldronBlockDataGenerator implements FabricBlockDataGenerator {
    protected final Block block;
    private final String name;

    public AppleCiderCauldronBlockDataGenerator(Block block, String name) {
        this.block = block;
        this.name = name;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.MINEABLE_WITH_PICKAXE)
            .setReplace(false)
            .add(block.builtInRegistryHolder().key());
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropOther(block, Items.CAULDRON);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(block, name);
    }
}
