package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.spice.Spices;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * Coverage for the spices: the cinnamon drop from stripping (both {@link Spices#onStrip} directly, for
 * a deterministic chance, and through a real axe use, for the {@code AHS$AxeItemMixin} hook) and the
 * smelting and smoking recipes for cloves and nutmeg, and the mulling spices crafting recipe. The farmer trade is covered by the manual steps
 * in {@code TEST_PLAN.md}.
 */
@SuppressWarnings("unused")
public class SpiceGameTest {
    private static final BlockPos TARGET = new BlockPos(2, 2, 2);

    private static List<ItemEntity> drops(GameTestHelper context, Item item) {
        ServerLevel level = context.getLevel();

        return level.getEntitiesOfClass(ItemEntity.class, context.getBounds(), entity -> entity.getItem().is(item));
    }

    private static <T extends Recipe<SingleRecipeInput>> ItemStack cook(GameTestHelper context, RecipeType<T> type, Item input) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(new ItemStack(input));

        return context.getLevel().getServer().getRecipeManager()
            .getRecipeFor(type, recipeInput, context.getLevel())
            .map(holder -> holder.value().assemble(recipeInput))
            .orElse(ItemStack.EMPTY);
    }

    private static void assertCooks(GameTestHelper context, RecipeType<? extends Recipe<SingleRecipeInput>> type, Item input, Item output) {
        context.assertValueEqual(cook(context, type, input).getItem(), output, input + " in " + type);
    }

    @GameTest
    public void strippingCherryMangroveAndJungleCanDropCinnamon(GameTestHelper context) {
        for (Block log : List.of(Blocks.CHERRY_LOG, Blocks.CHERRY_WOOD, Blocks.MANGROVE_LOG, Blocks.MANGROVE_WOOD, Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD)) {
            context.setBlock(TARGET, log.defaultBlockState());
            context.assertTrue(Spices.onStrip(context.getLevel(), context.absolutePos(TARGET), log.defaultBlockState(), context.getLevel().getRandom(), 1f), "cinnamon from " + log);
        }

        context.assertValueEqual(drops(context, ModItems.CINNAMON.get()).size(), 6, "cinnamon dropped");
        context.succeed();
    }

    @GameTest
    public void otherLogsNeverDropCinnamon(GameTestHelper context) {
        for (Block log : List.of(Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.PALE_OAK_LOG, Blocks.CHERRY_PLANKS)) {
            context.assertFalse(Spices.onStrip(context.getLevel(), context.absolutePos(TARGET), log.defaultBlockState(), context.getLevel().getRandom(), 1f), "cinnamon from " + log);
        }

        context.assertValueEqual(drops(context, ModItems.CINNAMON.get()).size(), 0, "cinnamon dropped");
        context.succeed();
    }

    @GameTest
    public void zeroChanceNeverDrops(GameTestHelper context) {
        context.assertFalse(Spices.onStrip(context.getLevel(), context.absolutePos(TARGET), Blocks.JUNGLE_LOG.defaultBlockState(), context.getLevel().getRandom(), 0f), "cinnamon at 0% chance");
        context.succeed();
    }

    @GameTest
    public void axeStrippingJungleLogsEventuallyDropsCinnamon(GameTestHelper context) {
        Player player = context.makeMockServerPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));

        // 60 tries at 25% leaves a 0.75^60 (about 3e-8) chance of a false failure.
        for (int i = 0; i < 60; i++) {
            context.setBlock(TARGET, Blocks.JUNGLE_LOG.defaultBlockState());
            context.useBlock(TARGET, player);
            context.assertBlockPresent(Blocks.STRIPPED_JUNGLE_LOG, TARGET);
        }

        context.assertTrue(!drops(context, ModItems.CINNAMON.get()).isEmpty(), "at least one cinnamon from 60 strips");
        context.succeed();
    }

    @GameTest
    public void strippingOakNeverDropsCinnamon(GameTestHelper context) {
        Player player = context.makeMockServerPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));

        for (int i = 0; i < 60; i++) {
            context.setBlock(TARGET, Blocks.OAK_LOG.defaultBlockState());
            context.useBlock(TARGET, player);
        }

        context.assertValueEqual(drops(context, ModItems.CINNAMON.get()).size(), 0, "cinnamon dropped");
        context.succeed();
    }

    @GameTest
    public void smallFlowersCookIntoCloves(GameTestHelper context) {
        for (Item flower : List.of(Items.DANDELION, Items.POPPY, Items.AZURE_BLUET, Items.CORNFLOWER, Items.OXEYE_DAISY)) {
            assertCooks(context, RecipeType.SMELTING, flower, ModItems.CLOVES.get());
            assertCooks(context, RecipeType.SMOKING, flower, ModItems.CLOVES.get());
        }

        context.succeed();
    }

    @GameTest
    public void witherRoseDoesNotCookIntoCloves(GameTestHelper context) {
        ItemStack result = cook(context, RecipeType.SMELTING, Items.WITHER_ROSE);

        context.assertTrue(!result.is(ModItems.CLOVES.get()), "wither rose smelted into cloves");
        context.succeed();
    }

    @GameTest
    public void cocoaBeansCookIntoNutmeg(GameTestHelper context) {
        assertCooks(context, RecipeType.SMELTING, Items.COCOA_BEANS, ModItems.NUTMEG.get());
        assertCooks(context, RecipeType.SMOKING, Items.COCOA_BEANS, ModItems.NUTMEG.get());
        context.succeed();
    }

    @GameTest
    public void oneOfEachSpiceCraftsMullingSpices(GameTestHelper context) {
        CraftingInput input = CraftingInput.of(3, 1, List.of(
            new ItemStack(ModItems.NUTMEG.get()),
            new ItemStack(ModItems.CINNAMON.get()),
            new ItemStack(ModItems.CLOVES.get())
        ));
        ItemStack result = context.getLevel().getServer().getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, input, context.getLevel())
            .map(holder -> holder.value().assemble(input))
            .orElse(ItemStack.EMPTY);

        context.assertValueEqual(result.getItem(), ModItems.MULLING_SPICES.get(), "mulling spices from one of each");

        CraftingInput partial = CraftingInput.of(2, 1, List.of(new ItemStack(ModItems.NUTMEG.get()), new ItemStack(ModItems.CINNAMON.get())));
        boolean craftable = context.getLevel().getServer().getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, partial, context.getLevel())
            .map(holder -> holder.value().assemble(partial).is(ModItems.MULLING_SPICES.get()))
            .orElse(false);

        context.assertFalse(craftable, "mulling spices from only two spices");
        context.succeed();
    }
}
