package com.chimericdream.allhallowssteve.item;

import net.minecraft.world.food.FoodProperties;

/** Food values for every mulled drink (cider and berry juices). The spices only gate the simmer; they never add effects. */
public final class MulledJuice {
    public static final int NUTRITION = 8;
    public static final float SATURATION_MODIFIER = 0.65F;

    private MulledJuice() {
    }

    public static FoodProperties food() {
        return new FoodProperties.Builder().nutrition(NUTRITION).saturationModifier(SATURATION_MODIFIER).build();
    }
}
