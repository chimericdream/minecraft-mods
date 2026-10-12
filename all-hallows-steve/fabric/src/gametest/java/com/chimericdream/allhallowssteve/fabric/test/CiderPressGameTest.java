package com.chimericdream.allhallowssteve.fabric.test;

import com.chimericdream.allhallowssteve.block.CiderPressBlock;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.block.entity.CiderPressBlockEntity;
import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.item.PomaceFeeding;
import com.chimericdream.allhallowssteve.tag.ModTags;
import dev.architectury.event.EventResult;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.ComposterBlock;

/**
 * Coverage for the cider press: the timed pressing and what it makes, fuel charges, how fast it stops
 * without inputs or fuel, hopper-facing slot rules, and pomace (its tags, composting, and healing
 * injured livestock). The press GUI and model are covered by the manual steps in {@code TEST_PLAN.md}.
 */
@SuppressWarnings("unused")
public class CiderPressGameTest {
    private static final BlockPos TARGET = new BlockPos(2, 2, 2);

    /** Long enough for one full pressing plus the tick it starts on. */
    private static final int PRESS_TICKS = CiderPressBlockEntity.PRESS_TIME + 10;

    private static CiderPressBlockEntity placePress(GameTestHelper context) {
        context.setBlock(TARGET, ModBlocks.CIDER_PRESS.get().defaultBlockState().setValue(CiderPressBlock.FACING, Direction.SOUTH));

        return context.getBlockEntity(TARGET, CiderPressBlockEntity.class);
    }

    private static CiderPressBlockEntity loadedPress(GameTestHelper context, int apples, int bottles, ItemStack fuel) {
        return loadedPress(context, new ItemStack(Items.APPLE, apples), bottles, fuel);
    }

    private static CiderPressBlockEntity loadedPress(GameTestHelper context, ItemStack fruit, int bottles, ItemStack fuel) {
        CiderPressBlockEntity press = placePress(context);
        press.setItem(CiderPressBlockEntity.SLOT_FRUIT, fruit);
        press.setItem(CiderPressBlockEntity.SLOT_BOTTLE, new ItemStack(Items.GLASS_BOTTLE, bottles));
        press.setItem(CiderPressBlockEntity.SLOT_FUEL, fuel);

        return press;
    }

    @GameTest(maxTicks = 300)
    public void pressingMakesCiderAndPomace(GameTestHelper context) {
        CiderPressBlockEntity press = loadedPress(context, 3, 1, new ItemStack(Items.CHARCOAL));

        context.runAfterDelay(PRESS_TICKS, () -> {
            ItemStack cider = press.getItem(CiderPressBlockEntity.SLOT_CIDER);
            ItemStack pomace = press.getItem(CiderPressBlockEntity.SLOT_POMACE);
            context.assertTrue(cider.is(ModItems.APPLE_CIDER.get()) && cider.getCount() == 1, "one cider in the output, got " + cider);
            context.assertTrue(pomace.is(ModItems.POMACE.get()) && pomace.getCount() == 1, "one pomace in the output, got " + pomace);
            context.assertTrue(press.getItem(CiderPressBlockEntity.SLOT_FRUIT).isEmpty(), "apples are used up");
            context.assertTrue(press.getItem(CiderPressBlockEntity.SLOT_BOTTLE).isEmpty(), "bottle is used up");
            context.assertTrue(press.getItem(CiderPressBlockEntity.SLOT_FUEL).isEmpty(), "charcoal is used up");
            context.assertValueEqual(press.getFuel(), CiderPressBlockEntity.CHARCOAL_CHARGES - 1, "charges left after one pressing");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 300)
    public void sweetBerriesMakeSweetBerryJuiceAndPomace(GameTestHelper context) {
        CiderPressBlockEntity press = loadedPress(context, new ItemStack(Items.SWEET_BERRIES, 3), 1, new ItemStack(Items.CHARCOAL));

        context.runAfterDelay(PRESS_TICKS, () -> {
            ItemStack juice = press.getItem(CiderPressBlockEntity.SLOT_CIDER);
            ItemStack pomace = press.getItem(CiderPressBlockEntity.SLOT_POMACE);
            context.assertTrue(juice.is(ModItems.SWEET_BERRY_JUICE.get()) && juice.getCount() == 1, "one sweet berry juice in the output, got " + juice);
            context.assertTrue(pomace.is(ModItems.POMACE.get()) && pomace.getCount() == 1, "one pomace in the output, got " + pomace);
            context.assertTrue(press.getItem(CiderPressBlockEntity.SLOT_FRUIT).isEmpty(), "berries are used up");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 300)
    public void glowBerriesMakeGlowBerryJuiceAndPomace(GameTestHelper context) {
        CiderPressBlockEntity press = loadedPress(context, new ItemStack(Items.GLOW_BERRIES, 3), 1, new ItemStack(Items.CHARCOAL));

        context.runAfterDelay(PRESS_TICKS, () -> {
            ItemStack juice = press.getItem(CiderPressBlockEntity.SLOT_CIDER);
            ItemStack pomace = press.getItem(CiderPressBlockEntity.SLOT_POMACE);
            context.assertTrue(juice.is(ModItems.GLOW_BERRY_JUICE.get()) && juice.getCount() == 1, "one glow berry juice in the output, got " + juice);
            context.assertTrue(pomace.is(ModItems.POMACE.get()) && pomace.getCount() == 1, "one pomace in the output, got " + pomace);
            context.assertTrue(press.getItem(CiderPressBlockEntity.SLOT_FRUIT).isEmpty(), "berries are used up");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 60)
    public void aDifferentJuiceInTheOutputBlocksPressing(GameTestHelper context) {
        CiderPressBlockEntity press = loadedPress(context, new ItemStack(Items.SWEET_BERRIES, 3), 1, new ItemStack(Items.CHARCOAL));
        press.setItem(CiderPressBlockEntity.SLOT_CIDER, new ItemStack(ModItems.APPLE_CIDER.get()));

        context.runAfterDelay(30, () -> {
            context.assertValueEqual(press.getPressTime(), 0, "press time with another juice in the output");
            context.assertValueEqual(press.getItem(CiderPressBlockEntity.SLOT_FRUIT).getCount(), 3, "berries left");
            context.succeed();
        });
    }

    @GameTest
    public void berriesGoInTheFruitSlot(GameTestHelper context) {
        CiderPressBlockEntity press = placePress(context);

        context.assertTrue(press.canPlaceItemThroughFace(CiderPressBlockEntity.SLOT_FRUIT, new ItemStack(Items.SWEET_BERRIES), Direction.UP), "sweet berries go in");
        context.assertTrue(press.canPlaceItemThroughFace(CiderPressBlockEntity.SLOT_FRUIT, new ItemStack(Items.GLOW_BERRIES), Direction.UP), "glow berries go in");
        context.assertTrue(CiderPressBlockEntity.juiceFor(new ItemStack(Items.CARROT)) == null, "carrots make no juice");
        context.succeed();
    }

    @GameTest(maxTicks = 300)
    public void blazePowderGivesTwentyCharges(GameTestHelper context) {
        CiderPressBlockEntity press = loadedPress(context, 3, 1, new ItemStack(Items.BLAZE_POWDER));

        context.runAfterDelay(10, () -> {
            context.assertValueEqual(press.getFuel(), CiderPressBlockEntity.BLAZE_POWDER_CHARGES - 1, "charges while pressing");
            context.assertTrue(press.getPressTime() > 0, "press is running");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 60)
    public void needsThreeApples(GameTestHelper context) {
        CiderPressBlockEntity press = loadedPress(context, 2, 1, new ItemStack(Items.CHARCOAL));

        context.runAfterDelay(30, () -> {
            context.assertValueEqual(press.getPressTime(), 0, "press time with two apples");
            context.assertValueEqual(press.getItem(CiderPressBlockEntity.SLOT_FRUIT).getCount(), 2, "apples left");
            context.assertValueEqual(press.getItem(CiderPressBlockEntity.SLOT_FUEL).getCount(), 1, "fuel left untouched");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 60)
    public void needsABottle(GameTestHelper context) {
        CiderPressBlockEntity press = loadedPress(context, 3, 0, new ItemStack(Items.CHARCOAL));

        context.runAfterDelay(30, () -> {
            context.assertValueEqual(press.getPressTime(), 0, "press time with no bottle");
            context.assertValueEqual(press.getItem(CiderPressBlockEntity.SLOT_FRUIT).getCount(), 3, "apples left");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 60)
    public void needsFuel(GameTestHelper context) {
        CiderPressBlockEntity press = loadedPress(context, 3, 1, ItemStack.EMPTY);

        context.runAfterDelay(30, () -> {
            context.assertValueEqual(press.getPressTime(), 0, "press time with no fuel");
            context.assertValueEqual(press.getItem(CiderPressBlockEntity.SLOT_FRUIT).getCount(), 3, "apples left");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 60)
    public void fullOutputBlocksPressing(GameTestHelper context) {
        CiderPressBlockEntity press = loadedPress(context, 3, 1, new ItemStack(Items.CHARCOAL));
        press.setItem(CiderPressBlockEntity.SLOT_CIDER, new ItemStack(ModItems.APPLE_CIDER.get(), ModItems.APPLE_CIDER.get().getDefaultMaxStackSize()));

        context.runAfterDelay(30, () -> {
            context.assertValueEqual(press.getPressTime(), 0, "press time with a full cider slot");
            context.assertValueEqual(press.getItem(CiderPressBlockEntity.SLOT_FRUIT).getCount(), 3, "apples left");
            context.succeed();
        });
    }

    @GameTest(maxTicks = 60)
    public void pullingTheApplesCancelsThePressing(GameTestHelper context) {
        CiderPressBlockEntity press = loadedPress(context, 3, 1, new ItemStack(Items.CHARCOAL));

        context.runAfterDelay(10, () -> {
            context.assertTrue(press.getPressTime() > 0, "press is running");
            press.setItem(CiderPressBlockEntity.SLOT_FRUIT, ItemStack.EMPTY);
        });
        context.runAfterDelay(20, () -> {
            context.assertValueEqual(press.getPressTime(), 0, "press time after taking the apples out");
            context.assertTrue(press.getItem(CiderPressBlockEntity.SLOT_CIDER).isEmpty(), "no cider made");
            context.succeed();
        });
    }

    @GameTest
    public void hoppersFeedInputsAndDrainOutputs(GameTestHelper context) {
        CiderPressBlockEntity press = placePress(context);
        ItemStack apples = new ItemStack(Items.APPLE);
        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
        ItemStack cider = new ItemStack(ModItems.APPLE_CIDER.get());

        for (Direction side : new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST}) {
            context.assertTrue(press.canPlaceItemThroughFace(CiderPressBlockEntity.SLOT_FRUIT, apples, side), "apples go in from " + side);
            context.assertTrue(press.canPlaceItemThroughFace(CiderPressBlockEntity.SLOT_BOTTLE, bottle, side), "bottles go in from " + side);
            context.assertTrue(press.canPlaceItemThroughFace(CiderPressBlockEntity.SLOT_FUEL, new ItemStack(Items.COAL), side), "coal goes in from " + side);
        }

        context.assertFalse(press.canPlaceItemThroughFace(CiderPressBlockEntity.SLOT_CIDER, cider, Direction.UP), "cider can't be put in the output slot");
        context.assertFalse(press.canPlaceItemThroughFace(CiderPressBlockEntity.SLOT_FRUIT, new ItemStack(Items.CARROT), Direction.UP), "carrots aren't pressed");
        context.assertFalse(press.canPlaceItemThroughFace(CiderPressBlockEntity.SLOT_FUEL, new ItemStack(Items.STICK), Direction.UP), "sticks aren't fuel");

        context.assertTrue(press.canTakeItemThroughFace(CiderPressBlockEntity.SLOT_CIDER, cider, Direction.DOWN), "cider comes out the bottom");
        context.assertTrue(press.canTakeItemThroughFace(CiderPressBlockEntity.SLOT_POMACE, new ItemStack(ModItems.POMACE.get()), Direction.NORTH), "pomace comes out the side");
        context.assertFalse(press.canTakeItemThroughFace(CiderPressBlockEntity.SLOT_FRUIT, apples, Direction.DOWN), "apples don't come back out");
        context.assertFalse(press.canTakeItemThroughFace(CiderPressBlockEntity.SLOT_FUEL, new ItemStack(Items.COAL), Direction.NORTH), "fuel doesn't come back out");

        context.assertValueEqual(press.getSlotsForFace(Direction.DOWN).length, 2, "slots reachable from the bottom");
        context.succeed();
    }

    @GameTest
    public void fuelChargesMatchTheIdea(GameTestHelper context) {
        context.assertValueEqual(CiderPressBlockEntity.fuelCharges(new ItemStack(Items.CHARCOAL)), 8, "charcoal charges");
        context.assertValueEqual(CiderPressBlockEntity.fuelCharges(new ItemStack(Items.COAL)), 8, "coal charges");
        context.assertValueEqual(CiderPressBlockEntity.fuelCharges(new ItemStack(Items.BLAZE_POWDER)), 20, "blaze powder charges");
        context.assertValueEqual(CiderPressBlockEntity.fuelCharges(new ItemStack(Items.STICK)), 0, "sticks aren't fuel");
        context.succeed();
    }

    @GameTest
    public void pomaceIsPigFoodFertilizerAndCompostable(GameTestHelper context) {
        ItemStack pomace = new ItemStack(ModItems.POMACE.get());

        context.assertTrue(pomace.is(ItemTags.PIG_FOOD), "pomace is pig food");
        context.assertTrue(pomace.is(ModTags.FERTILIZERS), "pomace is a fertilizer");
        context.assertTrue(ComposterBlock.COMPOSTABLES.getFloat(ModItems.POMACE.get()) > 0.0F, "pomace is compostable");
        context.succeed();
    }

    private static Player playerHoldingPomace(GameTestHelper context, int count) {
        Player player = context.makeMockServerPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.POMACE.get(), count));

        return player;
    }

    @GameTest
    public void pomaceHealsAnInjuredPig(GameTestHelper context) {
        Pig pig = context.spawnWithNoFreeWill(EntityTypes.PIG, 2, 2, 2);
        pig.setHealth(4.0F);
        Player player = playerHoldingPomace(context, 2);

        EventResult result = PomaceFeeding.onInteract(player, pig, InteractionHand.MAIN_HAND);

        context.assertTrue(result.isTrue(), "feeding succeeds");
        context.assertValueEqual(pig.getHealth(), 4.0F + PomaceFeeding.HEAL_AMOUNT, "pig health");
        context.assertValueEqual(player.getMainHandItem().getCount(), 1, "pomace left in hand");
        context.succeed();
    }

    @GameTest
    public void pomaceHealsOtherLivestock(GameTestHelper context) {
        Sheep sheep = context.spawnWithNoFreeWill(EntityTypes.SHEEP, 2, 2, 2);
        sheep.setHealth(3.0F);
        Player player = playerHoldingPomace(context, 1);

        EventResult result = PomaceFeeding.onInteract(player, sheep, InteractionHand.MAIN_HAND);

        context.assertTrue(result.isTrue(), "feeding succeeds");
        context.assertValueEqual(sheep.getHealth(), 3.0F + PomaceFeeding.HEAL_AMOUNT, "sheep health");
        context.assertTrue(player.getMainHandItem().isEmpty(), "pomace is used up");
        context.succeed();
    }

    @GameTest
    public void healthyAnimalsAreLeftToVanilla(GameTestHelper context) {
        Pig pig = context.spawnWithNoFreeWill(EntityTypes.PIG, 2, 2, 2);
        Player player = playerHoldingPomace(context, 1);

        EventResult result = PomaceFeeding.onInteract(player, pig, InteractionHand.MAIN_HAND);

        context.assertTrue(result.isEmpty(), "a healthy pig is left to vanilla breeding");
        context.assertValueEqual(player.getMainHandItem().getCount(), 1, "pomace left in hand");
        context.succeed();
    }

    @GameTest
    public void creativePlayersKeepTheirPomace(GameTestHelper context) {
        Pig pig = context.spawnWithNoFreeWill(EntityTypes.PIG, 2, 2, 2);
        pig.setHealth(4.0F);
        Player player = context.makeMockServerPlayer(GameType.CREATIVE);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.POMACE.get(), 1));

        PomaceFeeding.onInteract(player, pig, InteractionHand.MAIN_HAND);

        context.assertValueEqual(pig.getHealth(), 4.0F + PomaceFeeding.HEAL_AMOUNT, "pig health");
        context.assertValueEqual(player.getMainHandItem().getCount(), 1, "pomace left in hand");
        context.succeed();
    }

    @GameTest
    public void otherAnimalsIgnoreIt(GameTestHelper context) {
        Wolf wolf = context.spawnWithNoFreeWill(EntityTypes.WOLF, 2, 2, 2);
        wolf.setHealth(4.0F);
        Player player = playerHoldingPomace(context, 1);

        EventResult result = PomaceFeeding.onInteract(player, wolf, InteractionHand.MAIN_HAND);

        context.assertTrue(result.isEmpty(), "wolves aren't livestock");
        context.assertValueEqual(wolf.getHealth(), 4.0F, "wolf health");
        context.succeed();
    }
}
