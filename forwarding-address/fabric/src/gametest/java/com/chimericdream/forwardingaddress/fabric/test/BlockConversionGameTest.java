package com.chimericdream.forwardingaddress.fabric.test;

import com.chimericdream.forwardingaddress.fabric.test.fixture.FixtureBlock;
import com.chimericdream.forwardingaddress.fabric.test.fixture.FixtureContainerEntity;
import com.chimericdream.forwardingaddress.fabric.test.fixture.TestFixtures;
import com.chimericdream.forwardingaddress.migration.BlockConverter;
import com.chimericdream.forwardingaddress.migration.MigrationEvents;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Converts placed blocks directly through {@link BlockConverter}, so these cover the swap itself
 * (contents kept, nothing dropped, properties carried) apart from the queue that normally drives it.
 */
@SuppressWarnings("unused")
public class BlockConversionGameTest {
    private static final BlockPos POS = new BlockPos(2, 2, 2);
    private static final double SEARCH_RANGE = 3.0;

    private static BlockConverter.Result convert(GameTestHelper context, BlockPos relative) {
        return MigrationEvents.blocks().convert(context.getLevel(), context.absolutePos(relative));
    }

    private static FixtureContainerEntity entity(GameTestHelper context, BlockPos relative) {
        BlockEntity entity = context.getLevel().getBlockEntity(context.absolutePos(relative));
        if (!(entity instanceof FixtureContainerEntity fixture)) {
            throw context.assertionException("expected a fixture container at " + relative + " but found " + entity);
        }

        return fixture;
    }

    @GameTest
    public void aPlainBlockConvertsAndKeepsItsFacingAndWaterlogging(GameTestHelper context) {
        context.setBlock(POS, TestFixtures.OLD_BLOCK.get().defaultBlockState()
            .setValue(FixtureBlock.FACING, Direction.EAST)
            .setValue(FixtureBlock.WATERLOGGED, true));

        if (convert(context, POS) != BlockConverter.Result.CONVERTED) {
            context.fail("the old block should have converted");
        }

        BlockState state = context.getBlockState(POS);
        if (!state.is(TestFixtures.NEW_BLOCK.get())) {
            context.fail("expected the new block but found " + state);
        }
        if (state.getValue(FixtureBlock.FACING) != Direction.EAST || !state.getValue(FixtureBlock.WATERLOGGED)) {
            context.fail("facing and waterlogging should carry over, but the state is " + state);
        }

        context.succeed();
    }

    @GameTest
    public void aBlockWithNoMigrationIsLeftAlone(GameTestHelper context) {
        context.setBlock(POS, TestFixtures.NEW_BLOCK.get().defaultBlockState());

        if (convert(context, POS) != BlockConverter.Result.NOT_MIGRATED) {
            context.fail("a block with no migration should report NOT_MIGRATED");
        }

        context.assertBlockPresent(TestFixtures.NEW_BLOCK.get(), POS);
        context.succeed();
    }

    @GameTest
    public void aMigrationWhoseTargetIsNotInstalledIsSkipped(GameTestHelper context) {
        context.setBlock(POS, TestFixtures.ORPHAN_BLOCK.get().defaultBlockState());

        if (convert(context, POS) != BlockConverter.Result.NOT_MIGRATED) {
            context.fail("a migration with a missing target should not apply");
        }

        context.assertBlockPresent(TestFixtures.ORPHAN_BLOCK.get(), POS);
        context.succeed();
    }

    @GameTest
    public void aContainerKeepsItsContentsAndDropsNothing(GameTestHelper context) {
        context.setBlock(POS, TestFixtures.OLD_CHEST.get().defaultBlockState());
        FixtureContainerEntity original = entity(context, POS);
        original.setItem(0, new ItemStack(Items.DIAMOND, 5));
        original.setItem(2, new ItemStack(Items.APPLE, 12));

        if (convert(context, POS) != BlockConverter.Result.CONVERTED) {
            context.fail("the old chest should have converted");
        }

        context.assertBlockPresent(TestFixtures.NEW_CHEST.get(), POS);
        FixtureContainerEntity converted = entity(context, POS);
        if (!ItemStack.matches(converted.getItem(0), new ItemStack(Items.DIAMOND, 5))
            || !converted.getItem(1).isEmpty()
            || !ItemStack.matches(converted.getItem(2), new ItemStack(Items.APPLE, 12))) {
            context.fail("the contents should be unchanged, but slots are " + converted.getItems());
        }

        context.assertItemEntityCountIs(Items.DIAMOND, POS, SEARCH_RANGE, 0);
        context.assertItemEntityCountIs(Items.APPLE, POS, SEARCH_RANGE, 0);
        context.succeed();
    }

    @GameTest
    public void aHiddenSlotSurvivesTheSwap(GameTestHelper context) {
        context.setBlock(POS, TestFixtures.OLD_HOPPER.get().defaultBlockState());
        FixtureContainerEntity original = entity(context, POS);
        original.setItem(1, new ItemStack(Items.IRON_INGOT, 3));
        // Past getContainerSize(), like Hopper X-Treme's filter slot.
        original.getItems().set(FixtureContainerEntity.VISIBLE, new ItemStack(Items.COMPASS));

        if (convert(context, POS) != BlockConverter.Result.CONVERTED) {
            context.fail("the old hopper should have converted");
        }

        context.assertBlockPresent(TestFixtures.NEW_HOPPER.get(), POS);
        FixtureContainerEntity converted = entity(context, POS);
        if (!converted.getItems().get(FixtureContainerEntity.VISIBLE).is(Items.COMPASS)) {
            context.fail("the hidden slot should survive, but slots are " + converted.getItems());
        }
        if (!converted.getItem(1).is(Items.IRON_INGOT)) {
            context.fail("the visible contents should survive, but slots are " + converted.getItems());
        }

        context.assertItemEntityCountIs(Items.COMPASS, POS, SEARCH_RANGE, 0);
        context.succeed();
    }

    @GameTest
    public void differentBlockEntityTypesConvertThroughTheMapper(GameTestHelper context) {
        context.setBlock(POS, TestFixtures.OLD_BOX.get().defaultBlockState().setValue(FixtureBlock.FACING, Direction.SOUTH));
        entity(context, POS).setItem(1, new ItemStack(Items.EMERALD, 7));

        if (convert(context, POS) != BlockConverter.Result.CONVERTED) {
            context.fail("the old box should have converted through its mapper");
        }

        context.assertBlockPresent(TestFixtures.NEW_CRATE.get(), POS);
        FixtureContainerEntity converted = entity(context, POS);
        if (!ItemStack.matches(converted.getItem(1), new ItemStack(Items.EMERALD, 7))) {
            context.fail("the mapper should move the contents across, but slots are " + converted.getItems());
        }
        if (context.getBlockState(POS).getValue(FixtureBlock.FACING) != Direction.SOUTH) {
            context.fail("facing should carry over");
        }

        context.assertItemEntityCountIs(Items.EMERALD, POS, SEARCH_RANGE, 0);
        context.succeed();
    }

    @GameTest
    public void aBlockEntityBlockWhoseTargetHasNoneIsLeftAlone(GameTestHelper context) {
        context.setBlock(POS, TestFixtures.OLD_EMPTYABLE.get().defaultBlockState());
        entity(context, POS).setItem(0, new ItemStack(Items.GOLD_INGOT, 9));

        if (convert(context, POS) != BlockConverter.Result.SKIPPED) {
            context.fail("converting would lose the block entity, so it should be skipped");
        }

        context.assertBlockPresent(TestFixtures.OLD_EMPTYABLE.get(), POS);
        if (!entity(context, POS).getItem(0).is(Items.GOLD_INGOT)) {
            context.fail("a skipped block must keep its contents");
        }

        context.assertItemEntityCountIs(Items.GOLD_INGOT, POS, SEARCH_RANGE, 0);
        context.succeed();
    }
}
