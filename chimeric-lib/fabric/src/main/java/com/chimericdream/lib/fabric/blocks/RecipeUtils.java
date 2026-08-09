package com.chimericdream.lib.fabric.blocks;

import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;

public class RecipeUtils {
    /**
     * The {@code .unlockedBy(RecipeProvider.getHasName(x), generator.has(x))} idiom — unlock a
     * recipe by having the same item the recipe already needs as an ingredient.
     */
    public static <T extends RecipeBuilder> T unlockedByHas(T builder, RecipeProvider generator, ItemLike ingredient) {
        builder.unlockedBy(RecipeProvider.getHasName(ingredient), generator.has(ingredient));

        return builder;
    }
}
