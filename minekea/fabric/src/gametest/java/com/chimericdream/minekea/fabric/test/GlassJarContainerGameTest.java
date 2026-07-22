package com.chimericdream.minekea.fabric.test;

import com.chimericdream.minekea.block.containers.ContainerBlocks;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

/**
 * Regression coverage for the glass jar's {@code Container} contract (CODE-REVIEW-PLAN 1.1).
 *
 * <p>The jar implements {@code ImplementedInventory} but used to return {@code null} from
 * {@code getItems()}. Its own {@code tryInsert}/{@code removeStack} overrides avoided the list, but every
 * inherited default — {@code getContainerSize}, {@code getItem}, {@code setItem}, {@code removeItem},
 * {@code clearContent} — dereferenced it, so the instant a vanilla hopper (or any automation) found the jar
 * via {@code instanceof Container} it crashed the server with an NPE. The jar now backs a real single slot
 * and implements the container contract for real, cascading its compressed reserve stacks down into the
 * active slot so automation can drain the whole jar without losing items.
 *
 * <p>Redstone is used throughout because the jar only accepts items in the {@code GLASS_JAR_STORABLE} tag,
 * which gates hopper insertion via {@code canPlaceItem}.
 */
@SuppressWarnings("unused")
public class GlassJarContainerGameTest {
    private static final BlockPos JAR = new BlockPos(2, 2, 2);

    private static ItemStack redstone(int count) {
        return new ItemStack(Items.REDSTONE, count);
    }

    private GlassJarBlockEntity placeJar(GameTestHelper context, BlockPos pos) {
        context.setBlock(pos, ContainerBlocks.GLASS_JAR.get());
        return context.getBlockEntity(pos, GlassJarBlockEntity.class);
    }

    /**
     * Directly exercises the container methods that used to dereference the {@code null} item list. Before
     * the fix, {@code getContainerSize()} alone threw an NPE; this pins that the whole contract now works.
     */
    @GameTest
    public void containerApiDoesNotCrash(GameTestHelper context) {
        GlassJarBlockEntity jar = placeJar(context, JAR);

        context.assertTrue(jar.getContainerSize() == 1, "the jar should expose a single slot");
        context.assertTrue(jar.getItem(0).isEmpty(), "a fresh jar's slot should be empty");
        context.assertTrue(jar.isEmpty(), "a fresh jar should be empty");

        jar.setItem(0, redstone(10));
        context.assertTrue(jar.getItem(0).is(Items.REDSTONE) && jar.getItem(0).getCount() == 10,
            "setItem should populate the slot");
        context.assertTrue(!jar.isEmpty(), "a populated jar should not be empty");

        ItemStack removed = jar.removeItem(0, 4);
        context.assertTrue(removed.is(Items.REDSTONE) && removed.getCount() == 4, "removeItem should return 4");
        context.assertTrue(jar.getItem(0).getCount() == 6, "removeItem should leave 6 behind");

        ItemStack taken = jar.removeItemNoUpdate(0);
        context.assertTrue(taken.is(Items.REDSTONE) && taken.getCount() == 6, "removeItemNoUpdate should take the rest");
        context.assertTrue(jar.getItem(0).isEmpty(), "the slot should be empty after removeItemNoUpdate");

        jar.setItem(0, redstone(5));
        jar.clearContent();
        context.assertTrue(jar.getItem(0).isEmpty() && jar.isEmpty(), "clearContent should empty the jar");

        context.succeed();
    }

    /**
     * The jar compresses more than one stack of an item ({@code fullItemStacks}). Draining the active slot
     * via {@code removeItem} must cascade a reserve stack down so automation empties the whole jar, and — in
     * the narrow case where a hopper puts the last item back because its destination was full — the reserve
     * stack must not be silently deleted.
     */
    @GameTest
    public void reserveCascadeAndPutbackConserveItems(GameTestHelper context) {
        GlassJarBlockEntity jar = placeJar(context, JAR);

        // 64 + 64 compresses into one full active stack plus one reserve stack (128 total).
        jar.tryInsert(redstone(64));
        jar.tryInsert(redstone(64));
        context.assertTrue(jar.getItem(0).getCount() == 64 && jar.getStoredStacks() == 1,
            "two full stacks should compress into active=64 + reserve=1");

        // Jump the active slot straight to its last item (setItem never touches the reserve).
        jar.setItem(0, redstone(1));
        context.assertTrue(jar.getItem(0).getCount() == 1 && jar.getStoredStacks() == 1,
            "the reserve should survive setItem on the active slot");

        // The successful-extraction path: removing the last active item refills from the reserve.
        ItemStack extracted = jar.removeItem(0, 1);
        context.assertTrue(extracted.is(Items.REDSTONE) && extracted.getCount() == 1, "one item should come out");
        context.assertTrue(jar.getItem(0).getCount() == 64 && jar.getStoredStacks() == 0,
            "draining the active slot should cascade the reserve stack down");

        // Rebuild the active=1 + reserve=1 boundary to test the hopper putback.
        jar.clearContent();
        jar.tryInsert(redstone(64));
        jar.tryInsert(redstone(64));
        jar.setItem(0, redstone(1));

        // Mimic a vanilla hopper whose destination was full: removeItem(0, 1) then setItem(0, original).
        ItemStack original = jar.getItem(0).copy();
        jar.removeItem(0, 1);
        context.assertTrue(jar.getItem(0).getCount() == 64 && jar.getStoredStacks() == 0,
            "the cascade should refill the active slot before the putback");
        jar.setItem(0, original);
        context.assertTrue(jar.getItem(0).getCount() == 1 && jar.getStoredStacks() == 1,
            "the putback must return the borrowed reserve stack instead of deleting it");

        context.succeed();
    }

    /**
     * A vanilla hopper below the jar must pull its contents out without crashing — the concrete failure the
     * NPE caused in the field.
     */
    @GameTest(maxTicks = 120)
    public void vanillaHopperDrainsTheJar(GameTestHelper context) {
        BlockPos hopperPos = JAR.below();
        GlassJarBlockEntity jar = placeJar(context, JAR);
        jar.tryInsert(redstone(5));

        context.setBlock(hopperPos, Blocks.HOPPER); // default facing DOWN; pulls from the jar above it

        context.succeedWhen(() -> context.assertTrue(jar.isEmpty(), "the hopper should drain the jar empty"));
    }

    /**
     * A vanilla hopper above the jar must feed storable items into it without crashing, going through the
     * jar's {@code canPlaceItem}/{@code setItem} contract.
     */
    @GameTest(maxTicks = 120)
    public void vanillaHopperFeedsTheJar(GameTestHelper context) {
        BlockPos hopperPos = JAR.above();
        GlassJarBlockEntity jar = placeJar(context, JAR);

        context.setBlock(hopperPos, Blocks.HOPPER); // default facing DOWN, into the jar below it
        HopperBlockEntity hopper = context.getBlockEntity(hopperPos, HopperBlockEntity.class);
        hopper.setItem(0, redstone(3));

        context.succeedWhen(() -> context.assertTrue(
            jar.getItem(0).is(Items.REDSTONE) && jar.getItem(0).getCount() == 3,
            "the hopper should have fed all 3 redstone into the jar"));
    }
}
