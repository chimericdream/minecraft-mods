package com.chimericdream.allhallowssteve.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

/** Food values for a serving of mulled cider. Spices only change the food value, never add effects. */
public final class MulledCider {
    public static final int BASE_NUTRITION = 5;
    public static final float BASE_SATURATION_MODIFIER = 0.3F;

    /** Cinnamon: more hunger points. */
    public static final int CINNAMON_NUTRITION = 2;
    /** Cloves: more saturation. */
    public static final float CLOVES_SATURATION_MODIFIER = 0.25F;
    /** Nutmeg: a smaller bump to both. */
    public static final int NUTMEG_NUTRITION = 1;
    public static final float NUTMEG_SATURATION_MODIFIER = 0.1F;

    public static final String SPICES_TOOLTIP_KEY = "item.allhallowssteve.mulled_cider.tooltip.spices";

    private MulledCider() {
    }

    public static FoodProperties food(boolean cinnamon, boolean cloves, boolean nutmeg) {
        int nutrition = BASE_NUTRITION;
        float modifier = BASE_SATURATION_MODIFIER;

        if (cinnamon) {
            nutrition += CINNAMON_NUTRITION;
        }

        if (cloves) {
            modifier += CLOVES_SATURATION_MODIFIER;
        }

        if (nutmeg) {
            nutrition += NUTMEG_NUTRITION;
            modifier += NUTMEG_SATURATION_MODIFIER;
        }

        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(modifier).build();
    }

    /** The spice names in a fixed order (cinnamon, cloves, nutmeg), however they were added; null when there are none. */
    public static Component spiceList(boolean cinnamon, boolean cloves, boolean nutmeg) {
        List<Item> spices = new ArrayList<>();

        if (cinnamon) {
            spices.add(ModItems.CINNAMON.get());
        }

        if (cloves) {
            spices.add(ModItems.CLOVES.get());
        }

        if (nutmeg) {
            spices.add(ModItems.NUTMEG.get());
        }

        if (spices.isEmpty()) {
            return null;
        }

        Component list = name(spices.get(0));

        for (int i = 1; i < spices.size(); i++) {
            list = Component.empty().append(list).append(", ").append(name(spices.get(i)));
        }

        return list;
    }

    private static Component name(Item item) {
        return item.getName(item.getDefaultInstance());
    }

    public static ItemStack serving(boolean cinnamon, boolean cloves, boolean nutmeg) {
        ItemStack stack = new ItemStack(ModItems.MULLED_CIDER.get());

        stack.set(DataComponents.FOOD, food(cinnamon, cloves, nutmeg));

        Component spices = spiceList(cinnamon, cloves, nutmeg);

        if (spices != null) {
            stack.set(DataComponents.LORE, new ItemLore(List.of(
                Component.translatable(SPICES_TOOLTIP_KEY, spices).withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false))
            )));
        }

        return stack;
    }
}
