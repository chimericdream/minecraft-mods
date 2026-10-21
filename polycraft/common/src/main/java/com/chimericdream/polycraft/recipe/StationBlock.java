package com.chimericdream.polycraft.recipe;

import net.minecraft.world.item.crafting.RecipeType;

/**
 * Added to furnace-type block entities by mixin. It gives PolyCraft's code a way to reach the state it keeps on the
 * block and the few private fields it needs, without a mixin accessor for each.
 */
public interface StationBlock {
    FurnaceState polycraft$state();

    RecipeType<?> polycraft$recipeType();

    /** Ticks the current cook has run, and the ticks it needs. */
    int polycraft$cookingTimer();

    int polycraft$cookingTotalTime();

    void polycraft$setCooking(int timer, int totalTime);
}
