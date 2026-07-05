package com.chimericdream.hopperxtreme.fabric.block;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.item.ModItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class XtremeHopperRecipeGenerator extends RecipeProvider {
    private final HolderGetter<Item> itemLookup;

    public XtremeHopperRecipeGenerator(HolderLookup.Provider registries, RecipeOutput exporter) {
        super(registries, exporter);

        this.itemLookup = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void buildRecipes() {
        generateHopperRecipes();
        generateGlazedHopperRecipes();
        generateFilteredHopperRecipes();
        generateMultiHopperRecipes();
        generateHupperRecipes();
        generateMultiHupperRecipes();
    }

    private static String getCleanKey(Item item) {
        return item.getDescriptionId().replace(":", "/").replaceAll("block\\.[_a-zA-Z]+\\.", "");
    }

    private void generateHopperRecipes() {
        makeShapelessUpgradeRecipe(Items.HOPPER, Items.HONEYCOMB, ModBlocks.HONEYED_HOPPER.get().asItem(), "hoppers");
        makeShapelessUpgradeRecipe(Items.HOPPER, Items.COPPER_INGOT, ModBlocks.COPPER_HOPPER.get().asItem(), "hoppers");
        makeShapelessUpgradeRecipe(Items.HOPPER, Items.GOLD_INGOT, ModBlocks.GOLDEN_HOPPER.get().asItem(), "hoppers");
        makeShapelessUpgradeRecipe(ModBlocks.GOLDEN_HOPPER.get().asItem(), Items.DIAMOND, ModBlocks.DIAMOND_HOPPER.get().asItem(), "hoppers");
        makeSmithingRecipe(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModBlocks.DIAMOND_HOPPER.get().asItem(), Items.NETHERITE_INGOT, ModBlocks.NETHERITE_HOPPER.get().asItem(), "hoppers");
    }

    private void generateGlazedHopperRecipes() {
        makeShapedRecipe(ModBlocks.GLAZED_HOPPER.get().asItem(), "glazed_hoppers", List.of("IGI", "ICI", " I "), List.of(
            new Tuple<>('I', Items.IRON_INGOT),
            new Tuple<>('G', Items.GRAY_GLAZED_TERRACOTTA),
            new Tuple<>('C', Items.CHEST)
        ));
        makeShapelessUpgradeRecipe(Items.HOPPER, Items.GRAY_GLAZED_TERRACOTTA, 1, ModBlocks.GLAZED_HOPPER.get().asItem(), "glazed_hoppers", "_from_hopper");
        makeShapelessUpgradeRecipe(ModBlocks.GLAZED_HOPPER.get().asItem(), Items.HONEYCOMB, 1, ModBlocks.HONEY_GLAZED_HOPPER.get().asItem(), "glazed_hoppers", "_from_glazed");
        makeShapelessUpgradeRecipe(ModBlocks.GLAZED_HOPPER.get().asItem(), Items.GOLD_INGOT, 1, ModBlocks.GLAZED_GOLDEN_HOPPER.get().asItem(), "glazed_hoppers", "_from_glazed");
        makeShapelessUpgradeRecipe(ModBlocks.GLAZED_HOPPER.get().asItem(), Items.DIAMOND, 1, ModBlocks.GLAZED_DIAMOND_HOPPER.get().asItem(), "glazed_hoppers", "_from_glazed");
        makeSmithingRecipe(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModBlocks.GLAZED_DIAMOND_HOPPER.get().asItem(), Items.NETHERITE_INGOT, ModBlocks.GLAZED_NETHERITE_HOPPER.get().asItem(), "glazed_hoppers");

        makeShapelessUpgradeRecipe(ModBlocks.HONEYED_HOPPER.get().asItem(), Items.GRAY_GLAZED_TERRACOTTA, 1, ModBlocks.HONEY_GLAZED_HOPPER.get().asItem(), "glazed_hoppers", "_from_honeyed");
        makeShapelessUpgradeRecipe(ModBlocks.GOLDEN_HOPPER.get().asItem(), Items.GRAY_GLAZED_TERRACOTTA, 1, ModBlocks.GLAZED_GOLDEN_HOPPER.get().asItem(), "glazed_hoppers", "_from_golden");
        makeShapelessUpgradeRecipe(ModBlocks.DIAMOND_HOPPER.get().asItem(), Items.GRAY_GLAZED_TERRACOTTA, 1, ModBlocks.GLAZED_DIAMOND_HOPPER.get().asItem(), "glazed_hoppers", "_from_diamond");
        makeShapelessUpgradeRecipe(ModBlocks.NETHERITE_HOPPER.get().asItem(), Items.GRAY_GLAZED_TERRACOTTA, 1, ModBlocks.GLAZED_NETHERITE_HOPPER.get().asItem(), "glazed_hoppers", "_from_netherite");

        makeBiDirectionalConversionRecipe(ModBlocks.GLAZED_HOPPER.get().asItem(), 4, ModBlocks.GLAZED_MULTI_HOPPER.get().asItem(), 1, "glazed_multi_hoppers");
        makeBiDirectionalConversionRecipe(ModBlocks.GLAZED_GOLDEN_HOPPER.get().asItem(), 4, ModBlocks.GLAZED_GOLDEN_MULTI_HOPPER.get().asItem(), 1, "glazed_multi_hoppers");
        makeBiDirectionalConversionRecipe(ModBlocks.GLAZED_DIAMOND_HOPPER.get().asItem(), 4, ModBlocks.GLAZED_DIAMOND_MULTI_HOPPER.get().asItem(), 1, "glazed_multi_hoppers");
        makeBiDirectionalConversionRecipe(ModBlocks.GLAZED_NETHERITE_HOPPER.get().asItem(), 4, ModBlocks.GLAZED_NETHERITE_MULTI_HOPPER.get().asItem(), 1, "glazed_multi_hoppers");
        makeShapelessUpgradeRecipe(ModBlocks.GLAZED_MULTI_HOPPER.get().asItem(), Items.GOLD_INGOT, 4, ModBlocks.GLAZED_GOLDEN_MULTI_HOPPER.get().asItem(), "glazed_multi_hoppers");
        makeShapelessUpgradeRecipe(ModBlocks.GLAZED_GOLDEN_MULTI_HOPPER.get().asItem(), Items.DIAMOND, 4, ModBlocks.GLAZED_DIAMOND_MULTI_HOPPER.get().asItem(), "glazed_multi_hoppers");
    }

    private void generateFilteredHopperRecipes() {
        makeShapelessUpgradeRecipe(ModBlocks.GOLDEN_HOPPER.get().asItem(), ModItems.HOPPER_ITEM_FILTER_ITEM.get(), 1, ModBlocks.FILTERED_GOLDEN_HOPPER.get().asItem(), "filtered_hoppers", "_from_golden");
        makeShapelessUpgradeRecipe(ModBlocks.DIAMOND_HOPPER.get().asItem(), ModItems.HOPPER_ITEM_FILTER_ITEM.get(), 1, ModBlocks.FILTERED_DIAMOND_HOPPER.get().asItem(), "filtered_hoppers", "_from_diamond");
        makeShapelessUpgradeRecipe(ModBlocks.NETHERITE_HOPPER.get().asItem(), ModItems.HOPPER_ITEM_FILTER_ITEM.get(), 1, ModBlocks.FILTERED_NETHERITE_HOPPER.get().asItem(), "filtered_hoppers", "_from_netherite");

        makeShapelessUpgradeRecipe(ModBlocks.GLAZED_GOLDEN_HOPPER.get().asItem(), ModItems.HOPPER_ITEM_FILTER_ITEM.get(), 1, ModBlocks.FILTERED_GLAZED_GOLDEN_HOPPER.get().asItem(), "filtered_hoppers", "_from_glazed_golden");
        makeShapelessUpgradeRecipe(ModBlocks.GLAZED_DIAMOND_HOPPER.get().asItem(), ModItems.HOPPER_ITEM_FILTER_ITEM.get(), 1, ModBlocks.FILTERED_GLAZED_DIAMOND_HOPPER.get().asItem(), "filtered_hoppers", "_from_glazed_diamond");
        makeShapelessUpgradeRecipe(ModBlocks.GLAZED_NETHERITE_HOPPER.get().asItem(), ModItems.HOPPER_ITEM_FILTER_ITEM.get(), 1, ModBlocks.FILTERED_GLAZED_NETHERITE_HOPPER.get().asItem(), "filtered_hoppers", "_from_glazed_netherite");

        makeShapelessUpgradeRecipe(ModBlocks.FILTERED_GOLDEN_HOPPER.get().asItem(), Items.GRAY_GLAZED_TERRACOTTA, 1, ModBlocks.FILTERED_GLAZED_GOLDEN_HOPPER.get().asItem(), "filtered_hoppers", "_from_filtered_golden");
        makeShapelessUpgradeRecipe(ModBlocks.FILTERED_DIAMOND_HOPPER.get().asItem(), Items.GRAY_GLAZED_TERRACOTTA, 1, ModBlocks.FILTERED_GLAZED_DIAMOND_HOPPER.get().asItem(), "filtered_hoppers", "_from_filtered_diamond");
        makeShapelessUpgradeRecipe(ModBlocks.FILTERED_NETHERITE_HOPPER.get().asItem(), Items.GRAY_GLAZED_TERRACOTTA, 1, ModBlocks.FILTERED_GLAZED_NETHERITE_HOPPER.get().asItem(), "filtered_hoppers", "_from_filtered_netherite");

        makeShapelessUpgradeRecipe(ModBlocks.GOLDEN_HUPPER.get().asItem(), ModItems.HOPPER_ITEM_FILTER_ITEM.get(), 1, ModBlocks.FILTERED_GOLDEN_HUPPER.get().asItem(), "filtered_huppers", "_from_golden");
        makeShapelessUpgradeRecipe(ModBlocks.DIAMOND_HUPPER.get().asItem(), ModItems.HOPPER_ITEM_FILTER_ITEM.get(), 1, ModBlocks.FILTERED_DIAMOND_HUPPER.get().asItem(), "filtered_huppers", "_from_diamond");
        makeShapelessUpgradeRecipe(ModBlocks.NETHERITE_HUPPER.get().asItem(), ModItems.HOPPER_ITEM_FILTER_ITEM.get(), 1, ModBlocks.FILTERED_NETHERITE_HUPPER.get().asItem(), "filtered_huppers", "_from_netherite");

        makeBiDirectionalConversionRecipe(ModBlocks.FILTERED_GOLDEN_HOPPER.get().asItem(), ModBlocks.FILTERED_GOLDEN_HUPPER.get().asItem(), "filtered_huppers");
        makeBiDirectionalConversionRecipe(ModBlocks.FILTERED_DIAMOND_HOPPER.get().asItem(), ModBlocks.FILTERED_DIAMOND_HUPPER.get().asItem(), "filtered_huppers");
        makeBiDirectionalConversionRecipe(ModBlocks.FILTERED_NETHERITE_HOPPER.get().asItem(), ModBlocks.FILTERED_NETHERITE_HUPPER.get().asItem(), "filtered_huppers");

        makeBiDirectionalConversionRecipe(ModBlocks.FILTERED_GOLDEN_HOPPER.get().asItem(), 4, ModBlocks.FILTERED_GOLDEN_MULTI_HOPPER.get().asItem(), 1, "filtered_multi_hoppers");
        makeBiDirectionalConversionRecipe(ModBlocks.FILTERED_DIAMOND_HOPPER.get().asItem(), 4, ModBlocks.FILTERED_DIAMOND_MULTI_HOPPER.get().asItem(), 1, "filtered_multi_hoppers");
        makeBiDirectionalConversionRecipe(ModBlocks.FILTERED_NETHERITE_HOPPER.get().asItem(), 4, ModBlocks.FILTERED_NETHERITE_MULTI_HOPPER.get().asItem(), 1, "filtered_multi_hoppers");
    }

    private void generateMultiHopperRecipes() {
        makeBiDirectionalConversionRecipe(Items.HOPPER, 4, ModBlocks.MULTI_HOPPER.get().asItem(), 1, "multi_hoppers");
        makeBiDirectionalConversionRecipe(ModBlocks.GOLDEN_HOPPER.get().asItem(), 4, ModBlocks.GOLDEN_MULTI_HOPPER.get().asItem(), 1, "multi_hoppers");
        makeBiDirectionalConversionRecipe(ModBlocks.DIAMOND_HOPPER.get().asItem(), 4, ModBlocks.DIAMOND_MULTI_HOPPER.get().asItem(), 1, "multi_hoppers");
        makeBiDirectionalConversionRecipe(ModBlocks.NETHERITE_HOPPER.get().asItem(), 4, ModBlocks.NETHERITE_MULTI_HOPPER.get().asItem(), 1, "multi_hoppers");
        makeShapelessUpgradeRecipe(ModBlocks.MULTI_HOPPER.get().asItem(), Items.GOLD_INGOT, 4, ModBlocks.GOLDEN_MULTI_HOPPER.get().asItem(), "multi_hoppers");
        makeShapelessUpgradeRecipe(ModBlocks.GOLDEN_MULTI_HOPPER.get().asItem(), Items.DIAMOND, 4, ModBlocks.DIAMOND_MULTI_HOPPER.get().asItem(), "multi_hoppers");
    }

    private void generateHupperRecipes() {
        makeBiDirectionalConversionRecipe(Items.HOPPER, ModBlocks.HUPPER.get().asItem(), "huppers");
        makeBiDirectionalConversionRecipe(ModBlocks.HONEYED_HOPPER.get().asItem(), ModBlocks.HONEYED_HUPPER.get().asItem(), "huppers");
        makeBiDirectionalConversionRecipe(ModBlocks.COPPER_HOPPER.get().asItem(), ModBlocks.COPPER_HUPPER.get().asItem(), "huppers");
        makeBiDirectionalConversionRecipe(ModBlocks.GOLDEN_HOPPER.get().asItem(), ModBlocks.GOLDEN_HUPPER.get().asItem(), "huppers");
        makeBiDirectionalConversionRecipe(ModBlocks.DIAMOND_HOPPER.get().asItem(), ModBlocks.DIAMOND_HUPPER.get().asItem(), "huppers");
        makeBiDirectionalConversionRecipe(ModBlocks.NETHERITE_HOPPER.get().asItem(), ModBlocks.NETHERITE_HUPPER.get().asItem(), "huppers");
    }

    private void generateMultiHupperRecipes() {
        makeBiDirectionalConversionRecipe(ModBlocks.HUPPER.get().asItem(), 4, ModBlocks.MULTI_HUPPER.get().asItem(), 1, "multi_huppers");
        makeBiDirectionalConversionRecipe(ModBlocks.GOLDEN_HUPPER.get().asItem(), 4, ModBlocks.GOLDEN_MULTI_HUPPER.get().asItem(), 1, "multi_huppers");
        makeBiDirectionalConversionRecipe(ModBlocks.DIAMOND_HUPPER.get().asItem(), 4, ModBlocks.DIAMOND_MULTI_HUPPER.get().asItem(), 1, "multi_huppers");
        makeBiDirectionalConversionRecipe(ModBlocks.NETHERITE_HUPPER.get().asItem(), 4, ModBlocks.NETHERITE_MULTI_HUPPER.get().asItem(), 1, "multi_huppers");
        makeShapelessUpgradeRecipe(ModBlocks.MULTI_HUPPER.get().asItem(), Items.GOLD_INGOT, 4, ModBlocks.GOLDEN_MULTI_HUPPER.get().asItem(), "multi_huppers");
        makeShapelessUpgradeRecipe(ModBlocks.GOLDEN_MULTI_HUPPER.get().asItem(), Items.DIAMOND, 4, ModBlocks.DIAMOND_MULTI_HUPPER.get().asItem(), "multi_huppers");
    }

    private void makeShapedRecipe(Item output, String prefix, List<String> patterns, List<Tuple<Character, ItemLike>> inputs) {
        if (patterns.size() > 3) {
            throw new IllegalArgumentException("Too many patterns");
        }

        if (inputs.size() > 9) {
            throw new IllegalArgumentException("Too many inputs");
        }

        ShapedRecipeBuilder builder = ShapedRecipeBuilder
            .shaped(itemLookup, RecipeCategory.REDSTONE, output, 1);

        for (String pattern : patterns) {
            builder.pattern(pattern);
        }

        for (Tuple<Character, ItemLike> input : inputs) {
            builder.define(input.getA(), input.getB());
        }

        builder.unlockedBy(getHasName(output), has(output))
            .save(this.output, prefix + "/" + getCleanKey(output));
    }

    private void makeSmithingRecipe(Item template, Item base, Item addition, Item output, String prefix) {
        SmithingTransformRecipeBuilder
            .smithing(Ingredient.of(template), Ingredient.of(base), Ingredient.of(addition), RecipeCategory.REDSTONE, output)
            .unlocks(getHasName(template), has(template))
            .unlocks(getHasName(base), has(base))
            .unlocks(getHasName(addition), has(addition))
            .save(this.output, prefix + "/" + getCleanKey(output) + "_smithing");
    }

    private void makeShapelessUpgradeRecipe(Item base, Item ingredient, Item output, String prefix) {
        makeShapelessUpgradeRecipe(base, ingredient, 1, output, prefix);
    }

    private void makeShapelessUpgradeRecipe(Item base, Item ingredient, int qty, Item output, String prefix) {
        makeShapelessUpgradeRecipe(base, ingredient, qty, output, prefix, "");
    }

    private void makeShapelessUpgradeRecipe(Item base, Item ingredient, int qty, Item output, String prefix, String suffix) {
        ShapelessRecipeBuilder
            .shapeless(itemLookup, RecipeCategory.REDSTONE, output, 1)
            .requires(base)
            .requires(ingredient, qty)
            .unlockedBy(getHasName(base), has(base))
            .unlockedBy(getHasName(ingredient), has(ingredient))
            .save(this.output, prefix + "/" + getCleanKey(output) + suffix);
    }

    private void makeShapelessConversionRecipe(Item item1, Item item2, String prefix) {
        makeShapelessConversionRecipe(item1, 1, item2, 1, prefix);
    }

    private void makeBiDirectionalConversionRecipe(Item item1, Item item2, String prefix) {
        makeBiDirectionalConversionRecipe(item1, 1, item2, 1, prefix);
    }

    private void makeBiDirectionalConversionRecipe(Item item1, int count1, Item item2, int count2, String prefix) {
        makeShapelessConversionRecipe(item1, count1, item2, count2, prefix);
        makeShapelessConversionRecipe(item2, count2, item1, count1, prefix);
    }

    private void makeShapelessConversionRecipe(Item item1, int count1, Item item2, int count2, String prefix) {
        ShapelessRecipeBuilder
            .shapeless(itemLookup, RecipeCategory.REDSTONE, item2, count2)
            .requires(item1, count1)
            .unlockedBy(getHasName(item1), has(item1))
            .save(this.output, prefix + "/" + getCleanKey(item2) + "_from_" + getCleanKey(item1));
    }
}
