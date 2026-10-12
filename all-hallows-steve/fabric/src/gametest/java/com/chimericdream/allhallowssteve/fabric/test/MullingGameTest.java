package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.block.JuiceCauldronBlock;
import com.chimericdream.allhallowssteve.block.JuiceCauldronType;
import com.chimericdream.allhallowssteve.block.JuiceCauldrons;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.item.MulledJuice;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Coverage for the mulling flow: pouring cider into a cauldron, adding mulling spices once, simmering over a
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

    private static void addSpices(GameTestHelper context, Player player) {
        use(context, player, new ItemStack(ModItems.MULLING_SPICES.get()));
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
        return state(context).is(ModBlocks.APPLE_CIDER_CAULDRON.get());
    }

    private static boolean isMulled(GameTestHelper context) {
        return state(context).is(ModBlocks.MULLED_APPLE_CIDER_CAULDRON.get());
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
    public void mullingSpicesOnlyGoInAFullCauldronAndOnlyOnce(GameTestHelper context) {
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        ItemStack spices = new ItemStack(ModItems.MULLING_SPICES.get(), 2);
        use(context, player, spices);
        context.assertFalse(state(context).getValue(JuiceCauldrons.SPICED), "no spices in a part-full cauldron");
        context.assertValueEqual(spices.getCount(), 2, "spices kept");

        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));
        use(context, player, spices);
        context.assertTrue(state(context).getValue(JuiceCauldrons.SPICED), "spices added");
        context.assertValueEqual(spices.getCount(), 1, "one mulling spices used");

        use(context, player, spices);
        context.assertValueEqual(spices.getCount(), 1, "a second mulling spices is not accepted");

        context.setBlock(CAULDRON, state(context).setValue(JuiceCauldrons.SPICED, false));
        ItemStack cinnamon = new ItemStack(ModItems.CINNAMON.get());
        use(context, player, cinnamon);
        context.assertFalse(state(context).getValue(JuiceCauldrons.SPICED), "a loose spice is not accepted");
        context.assertValueEqual(cinnamon.getCount(), 1, "loose cinnamon kept");
        context.succeed();
    }

    @GameTest
    public void fullCiderCauldronOverALitCampfireMullsAfterFiveSteps(GameTestHelper context) {
        lightFire(context, true);
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        fillWithCider(context, player);
        addSpices(context, player);

        tick(context, JuiceCauldronBlock.MAX_SIMMER);
        context.assertTrue(isCider(context), "still cider before the last step");
        context.assertValueEqual(state(context).getValue(JuiceCauldronBlock.SIMMER), JuiceCauldronBlock.MAX_SIMMER, "simmer progress");

        tick(context, 1);
        context.assertTrue(isMulled(context), "mulled after the last step");
        context.assertTrue(level(context) == 3, "level kept");
        context.succeed();
    }

    @GameTest
    public void aCauldronWithoutSpicesNeverSimmersEvenOverAFire(GameTestHelper context) {
        lightFire(context, true);
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        fillWithCider(context, player);
        tick(context, 10);
        context.assertTrue(isCider(context) && state(context).getValue(JuiceCauldronBlock.SIMMER) == 0, "no simmer without spices");

        addSpices(context, player);
        tick(context, JuiceCauldronBlock.MAX_SIMMER + 1);
        context.assertTrue(isMulled(context), "adding the spices starts the simmer");
        context.succeed();
    }

    @GameTest
    public void aCauldronWithNoFireNeverSimmers(GameTestHelper context) {
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        fillWithCider(context, player);
        addSpices(context, player);
        tick(context, 10);

        context.assertTrue(isCider(context) && state(context).getValue(JuiceCauldronBlock.SIMMER) == 0, "no progress without heat");

        lightFire(context, false);
        tick(context, 10);
        context.assertTrue(isCider(context) && state(context).getValue(JuiceCauldronBlock.SIMMER) == 0, "no progress over an unlit campfire");
        context.succeed();
    }

    @GameTest
    public void theFireGoingOutResetsProgressButKeepsSpices(GameTestHelper context) {
        lightFire(context, true);
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        fillWithCider(context, player);
        addSpices(context, player);
        tick(context, 3);
        context.assertValueEqual(state(context).getValue(JuiceCauldronBlock.SIMMER), 3, "progress before the fire goes out");

        lightFire(context, false);
        tick(context, 1);
        context.assertValueEqual(state(context).getValue(JuiceCauldronBlock.SIMMER), 0, "progress reset");
        context.assertTrue(state(context).getValue(JuiceCauldrons.SPICED), "spices kept");

        lightFire(context, true);
        tick(context, JuiceCauldronBlock.MAX_SIMMER + 1);
        context.assertTrue(isMulled(context), "a relit fire starts the simmer over");
        context.succeed();
    }

    @GameTest
    public void ladlingMulledCiderGivesServingsAndEmptiesTheCauldron(GameTestHelper context) {
        context.setBlock(
            CAULDRON,
            ModBlocks.MULLED_APPLE_CIDER_CAULDRON.get().defaultBlockState()
                .setValue(LayeredCauldronBlock.LEVEL, 3)
        );
        Player player = player(context, ItemStack.EMPTY);

        for (int i = 0; i < 3; i++) {
            use(context, player, new ItemStack(Items.GLASS_BOTTLE));
        }

        context.assertBlockPresent(Blocks.CAULDRON, CAULDRON);
        context.assertValueEqual(player.getInventory().countItem(ModItems.MULLED_APPLE_CIDER.get()), 3, "servings taken");
        context.succeed();
    }

    @GameTest
    public void mulledCiderMatchesTheOldThreeSpiceMix(GameTestHelper context) {
        // 5 / 0.3 base, plus cinnamon (+2 hunger), cloves (+0.25 modifier), and nutmeg (+1 hunger, +0.1 modifier).
        FoodProperties oldFull = new FoodProperties.Builder().nutrition(8).saturationModifier(0.65F).build();
        FoodProperties food = MulledJuice.food();

        context.assertValueEqual(food.nutrition(), oldFull.nutrition(), "hunger");
        context.assertValueEqual(food.saturation(), oldFull.saturation(), "saturation");
        context.assertTrue(new ItemStack(ModItems.MULLED_APPLE_CIDER.get()).get(DataComponents.FOOD).nutrition() == oldFull.nutrition(), "the item itself has them");
        context.succeed();
    }

    private static void fullMullingFlow(GameTestHelper context, JuiceCauldronType type) {
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        lightFire(context, true);
        Player player = player(context, ItemStack.EMPTY);

        for (int i = 0; i < 3; i++) {
            use(context, player, new ItemStack(type.juice().get()));
        }

        context.assertTrue(state(context).is(type.cauldron().get()) && level(context) == 3, "three bottles give a full " + type.name() + " cauldron");
        addSpices(context, player);
        tick(context, JuiceCauldronBlock.MAX_SIMMER + 1);
        context.assertTrue(state(context).is(type.mulledCauldron().get()) && level(context) == 3, "the batch finishes mulled");

        for (int i = 0; i < 3; i++) {
            use(context, player, new ItemStack(Items.GLASS_BOTTLE));
        }

        context.assertBlockPresent(Blocks.CAULDRON, CAULDRON);
        context.assertValueEqual(player.getInventory().countItem(type.mulledJuice().get()), 3, "servings taken");
        context.assertValueEqual(player.getInventory().countItem(Items.GLASS_BOTTLE), 3, "empty bottles returned from filling");
        context.succeed();
    }

    @GameTest
    public void sweetBerryJuiceMullsLikeCider(GameTestHelper context) {
        fullMullingFlow(context, JuiceCauldronType.SWEET_BERRY_JUICE);
    }

    @GameTest
    public void glowBerryJuiceMullsLikeCider(GameTestHelper context) {
        fullMullingFlow(context, JuiceCauldronType.GLOW_BERRY_JUICE);
    }

    @GameTest
    public void differentJuicesDoNotMix(GameTestHelper context) {
        context.setBlock(CAULDRON, Blocks.CAULDRON.defaultBlockState());
        Player player = player(context, ItemStack.EMPTY);

        use(context, player, new ItemStack(ModItems.SWEET_BERRY_JUICE.get()));
        use(context, player, new ItemStack(ModItems.GLOW_BERRY_JUICE.get()));
        use(context, player, new ItemStack(ModItems.APPLE_CIDER.get()));

        context.assertTrue(state(context).is(ModBlocks.SWEET_BERRY_JUICE_CAULDRON.get()) && level(context) == 1, "only the first juice went in");
        context.assertValueEqual(player.getInventory().countItem(Items.GLASS_BOTTLE), 1, "only one bottle returned");
        context.succeed();
    }

    @GameTest
    public void berryJuicesAreDrinkableBottles(GameTestHelper context) {
        for (JuiceCauldronType type : JuiceCauldronType.ALL) {
            context.assertTrue(new ItemStack(type.juice().get()).get(DataComponents.FOOD) != null, type.name() + " is food");
            context.assertTrue(new ItemStack(type.mulledJuice().get()).get(DataComponents.FOOD).nutrition() == MulledJuice.NUTRITION, "mulled " + type.name() + " food values");
        }

        context.succeed();
    }
}
