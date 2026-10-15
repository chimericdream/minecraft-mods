package com.chimericdream.hopperxtreme.fabric.test;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.component.HopperXtremeComponentTypes;
import com.chimericdream.hopperxtreme.component.HopperXtremeFilterModeComponent;
import com.chimericdream.hopperxtreme.entity.AbstractXtremeHopperBlockEntity;
import com.chimericdream.hopperxtreme.item.FilterCopying;
import com.chimericdream.hopperxtreme.item.FilterCopying.Result;
import com.chimericdream.hopperxtreme.item.ModItems;
import com.chimericdream.hopperxtreme.item.WrenchItem;
import com.chimericdream.hopperxtreme.recipe.FilterCopyRecipe;
import com.chimericdream.hopperxtreme.recipe.FilterUpgradeRecipe;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.GameType;

/**
 * Filter copy and paste: the {@link FilterCopyRecipe} crafting recipe, and the Hopper Wrench's Copy/Paste mode
 * ({@link FilterCopying#copyOrPaste}). A hopper's filter is the item in its filter slot (index 5 for the
 * six-slot hoppers used here), so "copying a hopper's filter" is cloning that item.
 */
@SuppressWarnings("unused")
public class FilterCopyTest {
    private static final int FILTER_SLOT = 5;
    private static final BlockPos HOPPER_POS = new BlockPos(1, 1, 1);

    private static Item standard() {
        return ModItems.HOPPER_ITEM_FILTER_ITEM.get();
    }

    private static Item diamond() {
        return ModItems.DIAMOND_HOPPER_ITEM_FILTER_ITEM.get();
    }

    private static ItemStack blank(Item tier) {
        return new ItemStack(tier);
    }

    private static ItemStack configured(Item tier, String mode, ItemStack... contents) {
        ItemStack filter = new ItemStack(tier);

        filter.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(contents)));
        filter.set(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), new HopperXtremeFilterModeComponent(mode));

        return filter;
    }

    private static void expect(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static CraftingInput grid(ItemStack... stacks) {
        return CraftingInput.of(stacks.length, 1, List.of(stacks));
    }

    private static ServerPlayer survivalPlayer(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);

        return player;
    }

    private static ItemStack newWrench(WrenchItem.Mode mode) {
        ItemStack wrench = new ItemStack(ModItems.WRENCH.get());
        WrenchItem.setMode(wrench, mode);

        return wrench;
    }

    private static AbstractXtremeHopperBlockEntity placeHopper(GameTestHelper context, ItemStack installedFilter) {
        context.setBlock(HOPPER_POS, ModBlocks.FILTERED_GOLDEN_HOPPER.get().defaultBlockState());

        AbstractXtremeHopperBlockEntity hopper = context.getBlockEntity(HOPPER_POS, AbstractXtremeHopperBlockEntity.class);
        hopper.setItem(FILTER_SLOT, installedFilter);

        return hopper;
    }

    private static Result click(GameTestHelper context, ItemStack wrench, ServerPlayer player) {
        return FilterCopying.copyOrPaste(wrench, context.getLevel(), context.absolutePos(HOPPER_POS), player);
    }

    // --- recipe --------------------------------------------------------------------------------

    @GameTest
    public void recipeCopiesConfiguredFilterAndKeepsTheOriginal(GameTestHelper context) {
        ItemStack source = configured(standard(), "exclude", new ItemStack(Items.DIAMOND), new ItemStack(Items.GOLD_INGOT));
        ItemStack blank = blank(standard());
        CraftingInput input = grid(source, blank);
        FilterCopyRecipe recipe = new FilterCopyRecipe();

        expect(recipe.matches(input, context.getLevel()), "Recipe should match a configured filter plus a blank of the same tier");

        ItemStack result = recipe.assemble(input);
        expect(ItemStack.isSameItemSameComponents(result, source), "The copy should have the same contents and mode as the source");
        expect(result.getCount() == 1, "The recipe should produce a single filter");

        NonNullList<ItemStack> remaining = recipe.getRemainingItems(input);
        expect(ItemStack.isSameItemSameComponents(remaining.get(0), source), "The configured filter should be handed back");
        expect(remaining.get(1).isEmpty(), "The blank filter should be used up");

        context.succeed();
    }

    @GameTest
    public void upgradeRecipeKeepsContentsAndMode(GameTestHelper context) {
        ItemStack source = configured(standard(), "exclude", new ItemStack(Items.DIAMOND), new ItemStack(Items.GOLD_INGOT));
        CraftingInput input = grid(source, new ItemStack(Items.DIAMOND));
        FilterUpgradeRecipe recipe = new FilterUpgradeRecipe();

        expect(recipe.matches(input, context.getLevel()), "Upgrade should match a standard filter plus a diamond");

        ItemStack result = recipe.assemble(input);
        expect(result.is(diamond()), "Upgrade should produce a diamond filter");
        expect(result.get(DataComponents.CONTAINER).equals(source.get(DataComponents.CONTAINER)), "Upgrade should keep the filter's items");
        expect(result.get(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get()).equals(source.get(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get())), "Upgrade should keep the filter's mode");

        expect(!recipe.matches(grid(configured(diamond(), "include", new ItemStack(Items.EMERALD)), new ItemStack(Items.DIAMOND)), context.getLevel()), "A diamond filter can't be upgraded again");

        context.succeed();
    }

    @GameTest
    public void recipeWorksInEitherSlotOrderAndForDiamondFilters(GameTestHelper context) {
        ItemStack source = configured(diamond(), "include", new ItemStack(Items.EMERALD));
        FilterCopyRecipe recipe = new FilterCopyRecipe();

        expect(recipe.matches(grid(blank(diamond()), source), context.getLevel()), "Slot order shouldn't matter");

        CraftingInput input = grid(blank(diamond()), source);
        expect(ItemStack.isSameItemSameComponents(recipe.assemble(input), source), "Diamond filters should copy too");
        expect(ItemStack.isSameItemSameComponents(recipe.getRemainingItems(input).get(1), source), "The source is returned from whichever slot it was in");

        context.succeed();
    }

    @GameTest
    public void recipeRejectsEverythingButOneConfiguredAndOneBlankOfTheSameTier(GameTestHelper context) {
        ItemStack diamondItems = new ItemStack(Items.DIAMOND);
        FilterCopyRecipe recipe = new FilterCopyRecipe();

        expect(!recipe.matches(grid(configured(standard(), "include", diamondItems), configured(standard(), "include", diamondItems)), context.getLevel()), "Two configured filters shouldn't match");
        expect(!recipe.matches(grid(blank(standard()), blank(standard())), context.getLevel()), "Two blank filters shouldn't match");
        expect(!recipe.matches(grid(configured(standard(), "include", diamondItems), blank(diamond())), context.getLevel()), "Mixed tiers shouldn't match");
        expect(!recipe.matches(grid(configured(standard(), "include", diamondItems)), context.getLevel()), "A lone filter shouldn't match");
        expect(!recipe.matches(grid(configured(standard(), "include", diamondItems), blank(standard()), new ItemStack(Items.STICK)), context.getLevel()), "Extra ingredients shouldn't match");
        expect(!recipe.matches(grid(configured(standard(), "include", diamondItems), new ItemStack(Items.PAPER)), context.getLevel()), "A non-filter shouldn't count as a blank");

        context.succeed();
    }

    // --- wrench --------------------------------------------------------------------------------

    @GameTest
    public void copyThenPasteInstallsAnIdenticalFilterAndUsesUpABlank(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        ItemStack source = configured(standard(), "exclude", new ItemStack(Items.DIAMOND));
        ItemStack wrench = newWrench(WrenchItem.Mode.COPY_PASTE);

        placeHopper(context, source);
        expect(click(context, wrench, player) == Result.COPIED, "A click with nothing copied should copy the hopper's filter");

        // Move to an empty hopper (same position, filter removed) and paste.
        AbstractXtremeHopperBlockEntity target = placeHopper(context, ItemStack.EMPTY);
        player.getInventory().setItem(0, blank(standard()));

        expect(click(context, wrench, player) == Result.PASTED, "A click with a filter copied should paste it");
        expect(ItemStack.isSameItemSameComponents(target.getItem(FILTER_SLOT), source), "The pasted filter should match the source");
        expect(player.getInventory().getItem(0).isEmpty(), "Pasting should use up the blank filter");

        context.succeed();
    }

    @GameTest
    public void pasteNeedsABlankFilterInSurvival(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        ItemStack wrench = newWrench(WrenchItem.Mode.COPY_PASTE);

        placeHopper(context, configured(standard(), "include", new ItemStack(Items.DIAMOND)));
        click(context, wrench, player);

        AbstractXtremeHopperBlockEntity target = placeHopper(context, ItemStack.EMPTY);

        expect(click(context, wrench, player) == Result.NO_BLANK, "Pasting with no blank filter should be refused");
        expect(target.getItem(FILTER_SLOT).isEmpty(), "A refused paste should leave the hopper alone");

        // A blank of the other tier doesn't count.
        player.getInventory().setItem(0, blank(diamond()));
        expect(click(context, wrench, player) == Result.NO_BLANK, "A blank of a different tier shouldn't be used");

        // A configured filter isn't blank either.
        player.getInventory().setItem(0, configured(standard(), "include", new ItemStack(Items.STONE)));
        expect(click(context, wrench, player) == Result.NO_BLANK, "A configured filter shouldn't be used up as a blank");

        context.succeed();
    }

    @GameTest
    public void creativePlayersPasteWithoutUsingABlank(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.CREATIVE);
        ItemStack source = configured(standard(), "include", new ItemStack(Items.DIAMOND));
        ItemStack wrench = newWrench(WrenchItem.Mode.COPY_PASTE);

        placeHopper(context, source);
        click(context, wrench, player);

        AbstractXtremeHopperBlockEntity target = placeHopper(context, ItemStack.EMPTY);

        expect(click(context, wrench, player) == Result.PASTED, "Creative players should paste for free");
        expect(ItemStack.isSameItemSameComponents(target.getItem(FILTER_SLOT), source), "The pasted filter should match the source");

        context.succeed();
    }

    @GameTest
    public void pasteOntoAFilteredHopperNeedsSneakAndMatchingTier(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        ItemStack source = configured(standard(), "include", new ItemStack(Items.DIAMOND));
        ItemStack existing = configured(standard(), "exclude", new ItemStack(Items.COBBLESTONE));
        ItemStack wrench = newWrench(WrenchItem.Mode.COPY_PASTE);

        placeHopper(context, source);
        click(context, wrench, player);

        AbstractXtremeHopperBlockEntity target = placeHopper(context, existing);

        expect(click(context, wrench, player) == Result.HAS_FILTER, "A plain click shouldn't overwrite an existing filter");
        expect(ItemStack.isSameItemSameComponents(target.getItem(FILTER_SLOT), existing), "A refused paste should leave the existing filter alone");

        player.setShiftKeyDown(true);
        expect(click(context, wrench, player) == Result.OVERWROTE, "A sneak-click should overwrite the existing filter");
        expect(ItemStack.isSameItemSameComponents(target.getItem(FILTER_SLOT), source), "The overwritten filter should match the source");

        // A different tier can't be overwritten, even when sneaking.
        ItemStack diamondFilter = configured(diamond(), "include", new ItemStack(Items.EMERALD));
        target = placeHopper(context, diamondFilter);

        expect(click(context, wrench, player) == Result.WRONG_TIER, "Overwriting a different tier should be refused");
        expect(ItemStack.isSameItemSameComponents(target.getItem(FILTER_SLOT), diamondFilter), "A refused overwrite should leave the filter alone");

        context.succeed();
    }

    @GameTest
    public void copyNeedsAFilterInTheHopper(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        ItemStack wrench = newWrench(WrenchItem.Mode.COPY_PASTE);

        placeHopper(context, ItemStack.EMPTY);

        expect(click(context, wrench, player) == Result.NO_FILTER, "There's nothing to copy from a hopper with no filter");
        expect(wrench.get(HopperXtremeComponentTypes.HOPPER_XTREME_COPIED_FILTER_COMPONENT.get()) == null, "Nothing should be remembered");

        context.succeed();
    }

    @GameTest
    public void switchingModesForgetsTheCopiedFilter(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        ItemStack wrench = newWrench(WrenchItem.Mode.COPY_PASTE);

        placeHopper(context, configured(standard(), "include", new ItemStack(Items.DIAMOND)));
        click(context, wrench, player);

        expect(wrench.get(HopperXtremeComponentTypes.HOPPER_XTREME_COPIED_FILTER_COMPONENT.get()) != null, "The copy should be remembered");

        WrenchItem.setMode(wrench, WrenchItem.getMode(wrench).next());
        expect(WrenchItem.getMode(wrench) == WrenchItem.Mode.ROTATE, "The mode should cycle back to Rotate");
        expect(wrench.get(HopperXtremeComponentTypes.HOPPER_XTREME_COPIED_FILTER_COMPONENT.get()) == null, "Switching modes should forget the copy");

        context.succeed();
    }

    @GameTest
    public void modeKeySwitchesTheHeldWrench(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        ItemStack wrench = newWrench(WrenchItem.Mode.ROTATE);
        player.setItemInHand(InteractionHand.MAIN_HAND, wrench);

        WrenchItem.cycleMode(player);
        expect(WrenchItem.getMode(wrench) == WrenchItem.Mode.COPY_PASTE, "The key should switch Rotate to Copy / Paste");

        WrenchItem.cycleMode(player);
        expect(WrenchItem.getMode(wrench) == WrenchItem.Mode.ROTATE, "The key should switch Copy / Paste back to Rotate");

        context.succeed();
    }

    @GameTest
    public void modeKeyUsesTheOffHandWrenchAndPrefersTheMainHand(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        ItemStack offHand = newWrench(WrenchItem.Mode.ROTATE);
        player.setItemInHand(InteractionHand.OFF_HAND, offHand);

        WrenchItem.cycleMode(player);
        expect(WrenchItem.getMode(offHand) == WrenchItem.Mode.COPY_PASTE, "With only an off-hand wrench, the key should switch that one");

        ItemStack mainHand = newWrench(WrenchItem.Mode.ROTATE);
        player.setItemInHand(InteractionHand.MAIN_HAND, mainHand);

        WrenchItem.cycleMode(player);
        expect(WrenchItem.getMode(mainHand) == WrenchItem.Mode.COPY_PASTE, "With two wrenches, the main-hand one should switch");
        expect(WrenchItem.getMode(offHand) == WrenchItem.Mode.COPY_PASTE, "The off-hand wrench should be left alone");

        context.succeed();
    }

    @GameTest
    public void modeKeyDoesNothingWithoutAWrench(GameTestHelper context) {
        ServerPlayer player = survivalPlayer(context);
        ItemStack stick = new ItemStack(Items.STICK);
        player.setItemInHand(InteractionHand.MAIN_HAND, stick);

        WrenchItem.cycleMode(player);

        expect(ItemStack.isSameItemSameComponents(player.getItemInHand(InteractionHand.MAIN_HAND), new ItemStack(Items.STICK)), "Other items should be left alone");

        context.succeed();
    }

    @GameTest
    public void aNewWrenchStartsInRotateMode(GameTestHelper context) {
        expect(WrenchItem.getMode(new ItemStack(ModItems.WRENCH.get())) == WrenchItem.Mode.ROTATE, "A wrench with no mode set should rotate");

        context.succeed();
    }
}
