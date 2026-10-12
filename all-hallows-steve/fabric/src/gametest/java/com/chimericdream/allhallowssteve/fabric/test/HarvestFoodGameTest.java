package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.item.HarvestFoods;
import com.chimericdream.allhallowssteve.item.ModItems;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Coverage for the harvest foods: the crafting and cooking recipes, the food values, and that only the
 * spiced soul cakes apply (30 second) effects when eaten.
 */
@SuppressWarnings("unused")
public class HarvestFoodGameTest {
    private static ItemStack craft(GameTestHelper context, int width, int height, List<ItemStack> grid) {
        CraftingInput input = CraftingInput.of(width, height, grid);

        return context.getLevel().getServer().getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, input, context.getLevel())
            .map(holder -> holder.value().assemble(input))
            .orElse(ItemStack.EMPTY);
    }

    private static ItemStack craftShapeless(GameTestHelper context, Item... items) {
        return craft(context, items.length, 1, List.of(items).stream().map(ItemStack::new).toList());
    }

    private static <T extends Recipe<SingleRecipeInput>> ItemStack cook(GameTestHelper context, RecipeType<T> type, Item input) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(new ItemStack(input));

        return context.getLevel().getServer().getRecipeManager()
            .getRecipeFor(type, recipeInput, context.getLevel())
            .map(holder -> holder.value().assemble(recipeInput))
            .orElse(ItemStack.EMPTY);
    }

    /**
     * Eats the item as a cow and returns its status effects. Mock players have no network connection, so
     * applying an effect to one fails when it tries to send the update packet.
     */
    private static LivingEntity eat(GameTestHelper context, Item item) {
        Cow cow = context.spawn(EntityTypes.COW, new BlockPos(2, 2, 2));
        ItemStack stack = new ItemStack(item, 2);

        stack.finishUsingItem(context.getLevel(), cow);

        return cow;
    }

    private static Set<Holder<MobEffect>> effects(LivingEntity entity) {
        return entity.getActiveEffects().stream().map(MobEffectInstance::getEffect).collect(Collectors.toSet());
    }

    @GameTest
    public void soulCakeRecipesMakeTwoAndPickTheVariantFromTheSpice(GameTestHelper context) {
        Item[][] cases = {
            {ModItems.SOUL_CAKE.get()},
            {ModItems.SOUL_CAKE_CLOVES.get(), ModItems.CLOVES.get()},
            {ModItems.SOUL_CAKE_CINNAMON.get(), ModItems.CINNAMON.get()},
            {ModItems.SOUL_CAKE_NUTMEG.get(), ModItems.NUTMEG.get()},
            {ModItems.SOUL_CAKE_SPICED.get(), ModItems.MULLING_SPICES.get()},
        };

        for (Item[] row : cases) {
            ItemStack result = row.length > 1
                ? craftShapeless(context, Items.EGG, Items.WHEAT, Items.SUGAR, row[1])
                : craftShapeless(context, Items.EGG, Items.WHEAT, Items.SUGAR);

            context.assertValueEqual(result.getItem(), row[0], "soul cake variant for " + row[0]);
            context.assertValueEqual(result.getCount(), 2, "soul cakes per batch for " + row[0]);
        }

        context.succeed();
    }

    @GameTest
    public void twoLooseSpicesDoNotMakeASoulCake(GameTestHelper context) {
        ItemStack result = craftShapeless(context, Items.EGG, Items.WHEAT, Items.SUGAR, ModItems.CLOVES.get(), ModItems.NUTMEG.get());

        context.assertTrue(result.isEmpty(), "soul cake from two loose spices: " + result);
        context.succeed();
    }

    @GameTest
    public void soulCakeNeedsAllThreeBaseIngredients(GameTestHelper context) {
        context.assertTrue(craftShapeless(context, Items.WHEAT, Items.SUGAR).isEmpty(), "soul cake without an egg");
        context.assertTrue(craftShapeless(context, Items.EGG, Items.SUGAR, ModItems.CLOVES.get()).isEmpty(), "soul cake without wheat");
        context.succeed();
    }

    @GameTest
    public void pumpkinSoupTakesACarrotOrAPotato(GameTestHelper context) {
        for (Item vegetable : List.of(Items.CARROT, Items.POTATO)) {
            ItemStack result = craftShapeless(context, Items.PUMPKIN, Items.BOWL, vegetable);

            context.assertValueEqual(result.getItem(), ModItems.PUMPKIN_SOUP.get(), "pumpkin soup with " + vegetable);
        }

        context.assertTrue(craftShapeless(context, Items.PUMPKIN, Items.BOWL, Items.BEETROOT).isEmpty(), "pumpkin soup with a beetroot");
        context.succeed();
    }

    @GameTest
    public void applePieIsAppleSugarAppleOverThreeWheat(GameTestHelper context) {
        ItemStack apple = new ItemStack(Items.APPLE);
        ItemStack sugar = new ItemStack(Items.SUGAR);
        ItemStack wheat = new ItemStack(Items.WHEAT);

        ItemStack result = craft(context, 3, 2, List.of(apple, sugar, apple, wheat, wheat, wheat));
        context.assertValueEqual(result.getItem(), ModItems.APPLE_PIE.get(), "apple pie");

        ItemStack swapped = craft(context, 3, 2, List.of(wheat, wheat, wheat, apple, sugar, apple));
        context.assertTrue(swapped.isEmpty(), "apple pie with the rows swapped");
        context.succeed();
    }

    @GameTest
    public void spicedPumpkinPieIsPumpkinPieAndMullingSpices(GameTestHelper context) {
        ItemStack result = craftShapeless(context, Items.PUMPKIN_PIE, ModItems.MULLING_SPICES.get());

        context.assertValueEqual(result.getItem(), ModItems.SPICED_PUMPKIN_PIE.get(), "spiced pumpkin pie");
        context.assertTrue(craftShapeless(context, Items.PUMPKIN_PIE, ModItems.CLOVES.get()).isEmpty(), "spiced pumpkin pie from one spice");
        context.succeed();
    }

    @GameTest
    public void pumpkinSeedsRoastInTheFurnaceSmokerAndCampfire(GameTestHelper context) {
        for (RecipeType<? extends Recipe<SingleRecipeInput>> type : List.of(RecipeType.SMELTING, RecipeType.SMOKING, RecipeType.CAMPFIRE_COOKING)) {
            context.assertValueEqual(cook(context, type, Items.PUMPKIN_SEEDS).getItem(), ModItems.ROASTED_PUMPKIN_SEEDS.get(), "roasting in " + type);
        }

        context.succeed();
    }

    @GameTest
    public void everyFoodHasFoodValues(GameTestHelper context) {
        for (Item item : List.of(
            ModItems.SOUL_CAKE.get(), ModItems.SOUL_CAKE_CLOVES.get(), ModItems.SOUL_CAKE_CINNAMON.get(),
            ModItems.SOUL_CAKE_NUTMEG.get(), ModItems.SOUL_CAKE_SPICED.get(), ModItems.PUMPKIN_SOUP.get(),
            ModItems.ROASTED_PUMPKIN_SEEDS.get(), ModItems.APPLE_PIE.get(), ModItems.SPICED_PUMPKIN_PIE.get()
        )) {
            context.assertTrue(new ItemStack(item).has(DataComponents.FOOD), item + " has food values");
        }

        context.succeed();
    }

    @GameTest
    public void plainFoodsHaveNoEffects(GameTestHelper context) {
        for (Item item : List.of(
            ModItems.SOUL_CAKE.get(), ModItems.PUMPKIN_SOUP.get(), ModItems.ROASTED_PUMPKIN_SEEDS.get(),
            ModItems.APPLE_PIE.get(), ModItems.SPICED_PUMPKIN_PIE.get()
        )) {
            context.assertTrue(effects(eat(context, item)).isEmpty(), item + " applied an effect");
        }

        context.succeed();
    }

    @GameTest
    public void eachSpiceGivesItsOwnThirtySecondEffect(GameTestHelper context) {
        context.assertValueEqual(effects(eat(context, ModItems.SOUL_CAKE_CLOVES.get())), Set.of(MobEffects.ABSORPTION), "clove soul cake effects");
        context.assertValueEqual(effects(eat(context, ModItems.SOUL_CAKE_CINNAMON.get())), Set.of(MobEffects.LUCK), "cinnamon soul cake effects");
        context.assertValueEqual(effects(eat(context, ModItems.SOUL_CAKE_NUTMEG.get())), Set.of(MobEffects.NIGHT_VISION), "nutmeg soul cake effects");

        MobEffectInstance absorption = eat(context, ModItems.SOUL_CAKE_CLOVES.get()).getEffect(MobEffects.ABSORPTION);
        context.assertValueEqual(absorption.getDuration(), HarvestFoods.EFFECT_TICKS, "absorption duration");
        context.assertValueEqual(absorption.getAmplifier(), 0, "absorption level");
        context.succeed();
    }

    @GameTest
    public void sachetSoulCakeGivesAllThreeEffects(GameTestHelper context) {
        context.assertValueEqual(
            effects(eat(context, ModItems.SOUL_CAKE_SPICED.get())),
            Set.of(MobEffects.ABSORPTION, MobEffects.LUCK, MobEffects.NIGHT_VISION),
            "spiced soul cake effects"
        );
        context.succeed();
    }
}
