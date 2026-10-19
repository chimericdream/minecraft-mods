package com.chimericdream.jdcrafte.fabric.test;

import com.chimericdream.jdcrafte.block.FeedingTroughBlockEntity;
import com.chimericdream.jdcrafte.block.ModBlocks;
import com.chimericdream.jdcrafte.config.JDCrafteConfig;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * Coverage for the feeding trough's breeding behavior: same-species pairing, one pair per check, the
 * breeding-cooldown and baby exclusions, the crowding cap, the food-picks-the-animals rule, and the
 * config kill switch and clamps.
 *
 * <p>Most tests call {@link FeedingTroughBlockEntity#feedOnePair} directly with a hand-built
 * {@link JDCrafteConfig}, so they are deterministic and never touch the shared config instance. The
 * two tick tests exercise the real tick path and restore the shared config afterwards.
 */
@SuppressWarnings("unused")
public class FeedingTroughBreedingGameTest {
    private static final BlockPos TROUGH_POS = new BlockPos(4, 2, 4);

    private static FeedingTroughBlockEntity placeTrough(GameTestHelper context, ItemStack food) {
        // A floor so spawned animals stay put inside the 8x8x8 arena.
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                context.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
            }
        }

        context.setBlock(TROUGH_POS, ModBlocks.FEEDING_TROUGH.get());
        FeedingTroughBlockEntity trough = context.getBlockEntity(TROUGH_POS, FeedingTroughBlockEntity.class);
        trough.setItem(0, food);

        return trough;
    }

    private static <E extends Animal> E spawn(GameTestHelper context, EntityType<E> type, int x, int z) {
        E animal = context.spawnWithNoFreeWill(type, new BlockPos(x, 2, z));
        animal.setPersistenceRequired();

        return animal;
    }

    private static void feed(GameTestHelper context, FeedingTroughBlockEntity trough, JDCrafteConfig config) {
        trough.feedOnePair(context.getLevel(), context.absolutePos(TROUGH_POS), config);
    }

    private static void assertInLove(GameTestHelper context, Animal animal, boolean expected) {
        if (animal.isInLove() != expected) {
            context.fail("Expected " + animal.getType() + " isInLove=" + expected + " but was " + animal.isInLove());
        }
    }

    private static void assertStored(GameTestHelper context, FeedingTroughBlockEntity trough, int expected) {
        if (trough.getStoredCount() != expected) {
            context.fail("Expected " + expected + " stored food items but found " + trough.getStoredCount());
        }
    }

    @GameTest
    public void feedsAPairOfSameSpeciesAndCostsOneFoodEach(GameTestHelper context) {
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.WHEAT, 10));
        Animal a = spawn(context, EntityTypes.COW, 2, 2);
        Animal b = spawn(context, EntityTypes.COW, 5, 5);

        feed(context, trough, new JDCrafteConfig());

        assertInLove(context, a, true);
        assertInLove(context, b, true);
        assertStored(context, trough, 8);
        context.succeed();
    }

    @GameTest
    public void loneAnimalIsNeverFed(GameTestHelper context) {
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.WHEAT, 10));
        Animal cow = spawn(context, EntityTypes.COW, 2, 2);

        feed(context, trough, new JDCrafteConfig());

        assertInLove(context, cow, false);
        assertStored(context, trough, 10);
        context.succeed();
    }

    @GameTest
    public void differentSpeciesDoNotPairWithEachOther(GameTestHelper context) {
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.WHEAT, 10));
        Animal cow = spawn(context, EntityTypes.COW, 2, 2);
        Animal sheep = spawn(context, EntityTypes.SHEEP, 5, 5);

        feed(context, trough, new JDCrafteConfig());

        assertInLove(context, cow, false);
        assertInLove(context, sheep, false);
        assertStored(context, trough, 10);
        context.succeed();
    }

    @GameTest
    public void feedsOnlyOnePairPerCheck(GameTestHelper context) {
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.WHEAT, 10));
        List<Animal> cows = List.of(
            spawn(context, EntityTypes.COW, 1, 1),
            spawn(context, EntityTypes.COW, 1, 3),
            spawn(context, EntityTypes.COW, 6, 1),
            spawn(context, EntityTypes.COW, 6, 3)
        );

        feed(context, trough, new JDCrafteConfig());

        long inLove = cows.stream().filter(Animal::isInLove).count();
        if (inLove != 2) {
            context.fail("Expected exactly 2 cows in love but found " + inLove);
        }
        assertStored(context, trough, 8);
        context.succeed();
    }

    @GameTest
    public void animalOnBreedingCooldownIsSkipped(GameTestHelper context) {
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.WHEAT, 10));
        Animal ready = spawn(context, EntityTypes.COW, 2, 2);
        Animal cooling = spawn(context, EntityTypes.COW, 5, 5);
        cooling.setAge(6000); // positive age == breeding cooldown

        feed(context, trough, new JDCrafteConfig());

        assertInLove(context, ready, false);
        assertInLove(context, cooling, false);
        assertStored(context, trough, 10);
        context.succeed();
    }

    @GameTest
    public void babiesAreNeverFedButStillCountTowardTheCap(GameTestHelper context) {
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.WHEAT, 10));
        Animal adult = spawn(context, EntityTypes.COW, 2, 2);
        Animal baby = spawn(context, EntityTypes.COW, 5, 5);
        baby.setBaby(true);

        // Only one adult, so the baby must not be used to complete the pair.
        feed(context, trough, new JDCrafteConfig());
        assertInLove(context, adult, false);
        assertInLove(context, baby, false);
        assertStored(context, trough, 10);

        // Two adults plus the baby is three animals: a cap of 3 blocks feeding, which proves the baby counts.
        Animal secondAdult = spawn(context, EntityTypes.COW, 3, 5);
        JDCrafteConfig capped = new JDCrafteConfig();
        capped.troughAnimalCap = 3;
        feed(context, trough, capped);
        assertInLove(context, adult, false);
        assertInLove(context, secondAdult, false);
        assertStored(context, trough, 10);

        // With the default cap the two adults are fed and the baby still is not.
        feed(context, trough, new JDCrafteConfig());
        assertInLove(context, adult, true);
        assertInLove(context, secondAdult, true);
        assertInLove(context, baby, false);
        assertStored(context, trough, 8);
        context.succeed();
    }

    @GameTest
    public void crowdingCapCountsEverySupportedSpecies(GameTestHelper context) {
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.WHEAT_SEEDS, 10));
        Animal chickenA = spawn(context, EntityTypes.CHICKEN, 2, 2);
        Animal chickenB = spawn(context, EntityTypes.CHICKEN, 5, 5);
        // Pigs don't eat seeds but still count toward the cap.
        spawn(context, EntityTypes.PIG, 2, 5);
        spawn(context, EntityTypes.PIG, 5, 2);

        JDCrafteConfig capped = new JDCrafteConfig();
        capped.troughAnimalCap = 4;
        feed(context, trough, capped);

        assertInLove(context, chickenA, false);
        assertInLove(context, chickenB, false);
        assertStored(context, trough, 10);

        capped.troughAnimalCap = 5;
        feed(context, trough, capped);

        assertInLove(context, chickenA, true);
        assertInLove(context, chickenB, true);
        assertStored(context, trough, 8);
        context.succeed();
    }

    @GameTest
    public void storedFoodDecidesWhichSpeciesBreed(GameTestHelper context) {
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.CARROT, 10));
        Animal cowA = spawn(context, EntityTypes.COW, 1, 1);
        Animal cowB = spawn(context, EntityTypes.COW, 1, 6);
        Animal pigA = spawn(context, EntityTypes.PIG, 6, 1);
        Animal pigB = spawn(context, EntityTypes.PIG, 6, 6);

        feed(context, trough, new JDCrafteConfig());

        assertInLove(context, cowA, false);
        assertInLove(context, cowB, false);
        assertInLove(context, pigA, true);
        assertInLove(context, pigB, true);
        assertStored(context, trough, 8);
        context.succeed();
    }

    @GameTest
    public void goldenCarrotBreedsHorses(GameTestHelper context) {
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.GOLDEN_CARROT, 4));
        Animal a = spawn(context, EntityTypes.HORSE, 2, 2);
        Animal b = spawn(context, EntityTypes.HORSE, 5, 5);

        feed(context, trough, new JDCrafteConfig());

        assertInLove(context, a, true);
        assertInLove(context, b, true);
        assertStored(context, trough, 2);
        context.succeed();
    }

    @GameTest
    public void unsupportedSpeciesAreIgnored(GameTestHelper context) {
        // Goats are animals but not a supported species, so even a pair that could breed is left alone.
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.WHEAT, 10));
        Animal a = spawn(context, EntityTypes.GOAT, 2, 2);
        Animal b = spawn(context, EntityTypes.GOAT, 5, 5);

        feed(context, trough, new JDCrafteConfig());

        assertInLove(context, a, false);
        assertInLove(context, b, false);
        assertStored(context, trough, 10);
        context.succeed();
    }

    @GameTest
    public void animalsOutsideTheConfiguredRadiusAreIgnored(GameTestHelper context) {
        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.WHEAT, 10));
        Animal near = spawn(context, EntityTypes.COW, 4, 5);
        Animal far = spawn(context, EntityTypes.COW, 0, 0);

        JDCrafteConfig narrow = new JDCrafteConfig();
        narrow.troughHorizontalRadius = 2;
        feed(context, trough, narrow);

        assertInLove(context, near, false);
        assertInLove(context, far, false);
        assertStored(context, trough, 10);

        feed(context, trough, new JDCrafteConfig());

        assertInLove(context, near, true);
        assertInLove(context, far, true);
        context.succeed();
    }

    /**
     * Drives the real tick path. Kill-switch and enabled phases live in one test because they mutate the
     * shared config instance, and tests in a batch run concurrently: two tests flipping it would race.
     */
    @GameTest(maxTicks = 150)
    public void killSwitchStopsTickFeedingAndEnablingResumesIt(GameTestHelper context) {
        JDCrafteConfig shared = JDCrafteConfig.CONFIG.instance();
        boolean originalEnabled = shared.troughFeedingEnabled;
        int originalInterval = shared.troughIntervalSeconds;

        FeedingTroughBlockEntity trough = placeTrough(context, new ItemStack(Items.WHEAT, 10));
        Animal a = spawn(context, EntityTypes.COW, 2, 2);
        Animal b = spawn(context, EntityTypes.COW, 5, 5);

        shared.troughFeedingEnabled = false;
        shared.troughIntervalSeconds = 1;

        // Well past one 20-tick interval: a disabled trough must still have done nothing.
        context.runAfterDelay(45, () -> {
            try {
                assertInLove(context, a, false);
                assertInLove(context, b, false);
                assertStored(context, trough, 10);
                shared.troughFeedingEnabled = true;
            } catch (RuntimeException e) {
                shared.troughFeedingEnabled = originalEnabled;
                shared.troughIntervalSeconds = originalInterval;
                throw e;
            }
        });

        // The check is offset by position within each interval, so one full interval always covers it.
        context.runAfterDelay(100, () -> {
            try {
                assertInLove(context, a, true);
                assertInLove(context, b, true);
                assertStored(context, trough, 8);
            } finally {
                shared.troughFeedingEnabled = originalEnabled;
                shared.troughIntervalSeconds = originalInterval;
            }
            context.succeed();
        });
    }

    @GameTest
    public void configClampPullsValuesIntoRange(GameTestHelper context) {
        JDCrafteConfig config = new JDCrafteConfig();
        config.troughHorizontalRadius = 99;
        config.troughVerticalRadius = -3;
        config.troughIntervalSeconds = 0;
        config.troughAnimalCap = 1;
        config.clamp();

        if (config.troughHorizontalRadius != JDCrafteConfig.MAX_HORIZONTAL_RADIUS
            || config.troughVerticalRadius != JDCrafteConfig.MIN_VERTICAL_RADIUS
            || config.troughIntervalSeconds != JDCrafteConfig.MIN_INTERVAL_SECONDS
            || config.troughAnimalCap != JDCrafteConfig.MIN_ANIMAL_CAP) {
            context.fail("Config did not clamp: " + config.troughHorizontalRadius + "/" + config.troughVerticalRadius
                + "/" + config.troughIntervalSeconds + "/" + config.troughAnimalCap);
        }

        config.troughHorizontalRadius = 0;
        config.troughVerticalRadius = 50;
        config.clamp();

        if (config.troughHorizontalRadius != JDCrafteConfig.MIN_HORIZONTAL_RADIUS
            || config.troughVerticalRadius != JDCrafteConfig.MAX_VERTICAL_RADIUS) {
            context.fail("Config did not clamp the opposite bounds");
        }
        context.succeed();
    }
}
