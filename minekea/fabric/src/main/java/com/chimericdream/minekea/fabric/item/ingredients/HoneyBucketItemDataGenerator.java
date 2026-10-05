package com.chimericdream.minekea.fabric.item.ingredients;

import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import com.chimericdream.minekea.fluid.ModFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

public class HoneyBucketItemDataGenerator extends ChimericLibItemDataGenerator {
    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        // Honey bottles leave an empty glass bottle behind (their vanilla crafting remainder)
        generator.shapeless(RecipeCategory.MISC, ModFluids.HONEY_BUCKET.get(), 1)
            .requires(Items.HONEY_BOTTLE, 3)
            .requires(Items.BUCKET)
            .unlockedBy(RecipeProvider.getHasName(Items.HONEY_BOTTLE),
                generator.has(Items.HONEY_BOTTLE))
            .save(exporter);
    }
}
