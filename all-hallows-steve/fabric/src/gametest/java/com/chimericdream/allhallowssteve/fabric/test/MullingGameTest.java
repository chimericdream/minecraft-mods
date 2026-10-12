package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.block.CiderCauldronBlock;
import com.chimericdream.allhallowssteve.block.CiderCauldrons;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.item.MulledAppleCider;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Coverage for the mulling flow: pouring cider into a cauldron, adding each spice once, simmering over a
 * campfire (including the fire going out), and ladling mulled cider out with spice-dependent food values.
 * The simmer is driven by calling the cauldron's {@code tick} directly, since a real batch takes 30 seconds.
 */
@SuppressWarnings("unused")
public class MullingGameTest {
    private static final BlockPos CAULDRON = new BlockPos(2, 3, 2);
    private static final BlockPos FIRE = new BlockPos(2, 2, 2);

    /** A main-inventory slot where {@link #use} keeps what the cauldron hands back, so tests can count it. */
    private static final int STASH_SLOT = 20;

    private static Player player(GameTestHelper context, ItemStack held) {
        Player player = context.makeMockServerPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, held);

        return player;
    }

    private static void use(GameTestHelper context, Player player, ItemStack held) {
        player.setItemInHand(InteractionHand.MAIN_HAND, held);
        context.useBlock(CAULDRON, player);

        // A used-up stack is replaced in hand by what it turned into (a bottle, a serving); stash it so the
        // next call doesn't overwrite it.
        ItemStack result = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (result != held && !result.isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

            for (int slot = STASH_SLOT; slot < STASH_SLOT + 5; slot++) {
                ItemStack stash = player.getInventory().getItem(slot);

                if (stash.isEmpty()) {
                    player.getInventory().setItem(slot, result);

                    break;
                }

                if (ItemStack.isSameItemSameComponents(stash, result)) {
                    stash.grow(result.getCount());

                    break;
                }
            }
        }
    }

    private static void fillWithCider(GameTestHelper context, Player player) {
        for (int i = 0; i < 3; i++) {
            use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        }
    }

    private static BlockState state(GameTestHelper context) {
        return context.getBlockState(CAULDRON);
    }

    private static void tick(GameTestHelper context, int times) {
        for (int i = 0; i < times; i++) {
            BlockState state = state(context);
            state.tick(context.getLevel(), context.absolutePos(CAULDRON), context.getLevel().getRandom());
        }
    }

    private static void lightFire(GameTestHelper context, boolean lit) {
        context.setBlock(FIRE, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, lit));
    }

    private static boolean isCider(GameTestHelper context) {
        return state(context).is(ModBlocks.CIDER_CAULDRON.get());
    }

    private static boolean isMulled(GameTestHelper context) {
        return state(context).is(ModBlocks.MULLED_CIDER_CAULDRON.get());
    }

    private static int level(GameTestHelper context) {
        return state(context).getValue(LayeredCauldronBlock.LEVEL);
    }

    @GameTest
    public void ciderBottlesFillAnEmptyCauldronOneLevelEach(GameTestHelper context) {
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        context.assertTrue(isCider(context) && level(context) == 1, "one bottle gives level 1");

        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        context.assertTrue(isCider(context) && level(context) == 3, "three bottles give a full cauldron");

        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        context.assertTrue(level(context) == 3, "a fourth bottle is not accepted");

        context.assertValueEqual(player.getInventory().countItem(Items.GLASS_BOTTLE), 3, "empty bottles returned");
        context.succeed();
    }

    @GameTest
    public void emptyBottlesTakeCiderBackOut(GameTestHelper context) {
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        fillWithCider(context, player);
        use(context, player, new ItemStack(Items.GLASS_BOTTLE));
        context.assertTrue(isCider(context) && level(context) == 2, "one serving out leaves level 2");

        use(context, player, new ItemStack(Items.GLASS_BOTTLE));
        use(context, player, new ItemStack(Items.GLASS_BOTTLE));
        context.assertBlockPresent(Blocks.CAULDRON, CAULDRON);
        context.assertValueEqual(player.getInventory().countItem(ModItems.APPLE_CIDER.get()), 3, "cider bottles taken");
        context.succeed();
    }

    @GameTest
    public void spicesOnlyGoInAFullCauldronAndOncePerType(GameTestHelper context) {
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        ItemStack cinnamon = new ItemStack(ModItems.CINNAMON.get(), 2);
        use(context, player, cinnamon);
        context.assertFalse(state(context).getValue(CiderCauldrons.CINNAMON), "no spice in a part-full cauldron");
        context.assertValueEqual(cinnamon.getCount(), 2, "cinnamon kept");

        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        use(context, player, cinnamon);
        context.assertTrue(state(context).getValue(CiderCauldrons.CINNAMON), "cinnamon added");
        context.assertValueEqual(cinnamon.getCount(), 1, "one cinnamon used");

        use(context, player, cinnamon);
        context.assertValueEqual(cinnamon.getCount(), 1, "a second cinnamon is not accepted");

        use(context, player, new ItemStack(ModItems.CLOVES.get()));
        use(context, player, new ItemStack(ModItems.NUTMEG.get()));
        BlockState state = state(context);
        context.assertTrue(state.getValue(CiderCauldrons.CLOVES) && state.getValue(CiderCauldrons.NUTMEG), "cloves and nutmeg added");
        context.succeed();
    }

    @GameTest
    public void fullCiderCauldronOverALitCampfireMullsAfterFiveSteps(GameTestHelper context) {
        lightFire(context, true);
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        fillWithCider(context, player);
        use(context, player, new ItemStack(ModItems.CLOVES.get()));

        tick(context, CiderCauldronBlock.MAX_SIMMER);
        context.assertTrue(isCider(context), "still cider before the last step");
        context.assertValueEqual(state(context).getValue(CiderCauldronBlock.SIMMER), CiderCauldronBlock.MAX_SIMMER, "simmer progress");

        tick(context, 1);
        context.assertTrue(isMulled(context), "mulled after the last step");
        context.assertTrue(level(context) == 3, "level kept");
        context.assertTrue(state(context).getValue(CiderCauldrons.CLOVES), "spices kept");
        context.assertFalse(state(context).getValue(CiderCauldrons.CINNAMON), "unadded spices stay out");
        context.succeed();
    }

    @GameTest
    public void aCauldronWithNoFireNeverSimmers(GameTestHelper context) {
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        fillWithCider(context, player);
        tick(context, 10);

        context.assertTrue(isCider(context) && state(context).getValue(CiderCauldronBlock.SIMMER) == 0, "no progress without heat");

        lightFire(context, false);
        tick(context, 10);
        context.assertTrue(isCider(context) && state(context).getValue(CiderCauldronBlock.SIMMER) == 0, "no progress over an unlit campfire");
        context.succeed();
    }

    @GameTest
    public void theFireGoingOutResetsProgressButKeepsSpices(GameTestHelper context) {
        lightFire(context, true);
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        fillWithCider(context, player);
        use(context, player, new ItemStack(ModItems.NUTMEG.get()));
        tick(context, 3);
        context.assertValueEqual(state(context).getValue(CiderCauldronBlock.SIMMER), 3, "progress before the fire goes out");

        lightFire(context, false);
        tick(context, 1);
        context.assertValueEqual(state(context).getValue(CiderCauldronBlock.SIMMER), 0, "progress reset");
        context.assertTrue(state(context).getValue(CiderCauldrons.NUTMEG), "spices kept");

        lightFire(context, true);
        tick(context, CiderCauldronBlock.MAX_SIMMER + 1);
        context.assertTrue(isMulled(context), "a relit fire starts the simmer over");
        context.succeed();
    }

    @GameTest
    public void ladlingMulledCiderGivesServingsAndEmptiesTheCauldron(GameTestHelper context) {
        context.setBlock(
            CAULDRON,
            ModBlocks.MULLED_CIDER_CAULDRON.get().defaultBlockState()
                .setValue(LayeredCauldronBlock.LEVEL, 3)
                .setValue(CiderCauldrons.CINNAMON, true)
                .setValue(CiderCauldrons.CLOVES, true)
                .setValue(CiderCauldrons.NUTMEG, true)
        );
        Player player = player(context, ItemStack.EMPTY);

        for (int i = 0; i < 3; i++) {
            use(context, player, new ItemStack(Items.GLASS_BOTTLE));
        }

        context.assertBlockPresent(Blocks.CAULDRON, CAULDRON);
        context.assertValueEqual(player.getInventory().countItem(ModItems.MULLED_APPLE_CIDER.get()), 3, "servings taken");

        FoodProperties food = player.getInventory().getNonEquipmentItems().stream()
            .filter(stack -> stack.is(ModItems.MULLED_APPLE_CIDER.get()))
            .findFirst()
            .map(stack -> stack.get(DataComponents.FOOD))
            .orElse(null);
        FoodProperties full = MulledAppleCider.food(true, true, true);

        context.assertTrue(food != null && food.nutrition() == full.nutrition(), "serving carries the fully spiced food values");
        context.succeed();
    }

    /** The spice list inside the tooltip line, as text. Mod names show as their translation keys on the server. */
    private static String spices(ItemLore lore) {
        Object[] args = ((TranslatableContents) lore.lines().get(0).getContents()).getArgs();

        return ((Component) args[0]).getString().toLowerCase();
    }

    @GameTest
    public void servingsListTheirSpicesInAFixedOrder(GameTestHelper context) {
        context.assertTrue(MulledAppleCider.serving(false, false, false).getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines().isEmpty(), "no tooltip without spices");

        // The order comes from the booleans, not from the order the spices were added to the cauldron.
        String all = spices(MulledAppleCider.serving(true, true, true).get(DataComponents.LORE));
        context.assertTrue(all.indexOf("cinnamon") < all.indexOf("cloves") && all.indexOf("cloves") < all.indexOf("nutmeg"), "cinnamon, cloves, nutmeg: " + all);

        String two = spices(MulledAppleCider.serving(false, true, true).get(DataComponents.LORE));
        context.assertTrue(two.contains("cloves") && two.contains("nutmeg") && !two.contains("cinnamon"), "only the spices used: " + two);
        context.succeed();
    }

    @GameTest
    public void eachSpiceHasItsOwnMix(GameTestHelper context) {
        FoodProperties plain = MulledAppleCider.food(false, false, false);
        FoodProperties cinnamon = MulledAppleCider.food(true, false, false);
        FoodProperties cloves = MulledAppleCider.food(false, true, false);
        FoodProperties nutmeg = MulledAppleCider.food(false, false, true);

        context.assertTrue(cinnamon.nutrition() > nutmeg.nutrition() && nutmeg.nutrition() > plain.nutrition(), "cinnamon gives the most hunger");
        context.assertValueEqual(cloves.nutrition(), plain.nutrition(), "cloves leave hunger alone");
        context.assertTrue(cloves.saturation() > nutmeg.saturation() && nutmeg.saturation() > plain.saturation(), "cloves give the most saturation");
        context.succeed();
    }
}
