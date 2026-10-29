package com.chimericdream.forwardingaddress.fabric.test;

import com.chimericdream.forwardingaddress.config.DumpCommand;
import com.chimericdream.forwardingaddress.fabric.test.fixture.FixtureBlock;
import com.chimericdream.forwardingaddress.fabric.test.fixture.TestFixtures;
import com.chimericdream.forwardingaddress.migration.BlockConverter;
import com.chimericdream.forwardingaddress.migration.MigrationEvents;
import com.chimericdream.forwardingaddress.migration.RetiredItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Stubs for blocks of a mod that isn't installed, declared in JSON by {@link TestFixtures}: they
 * register, hold their saved state, convert to the target, and drop their own item.
 */
@SuppressWarnings("unused")
public class StubGameTest {
    private static final BlockPos POS = new BlockPos(2, 2, 2);
    private static final double SEARCH_RANGE = 3.0;

    private static Block block(String id) {
        return BuiltInRegistries.BLOCK.getOptional(Identifier.parse(id)).orElse(null);
    }

    private static BlockState set(GameTestHelper context, BlockState state) {
        context.getLevel().setBlock(context.absolutePos(POS), state, 3);
        return context.getLevel().getBlockState(context.absolutePos(POS));
    }

    private static <T extends Comparable<T>> BlockState with(BlockState state, String property, String value) {
        for (Property<?> candidate : state.getProperties()) {
            if (candidate.getName().equals(property)) {
                return withValue(state, candidate, value);
            }
        }

        throw new IllegalArgumentException(state + " has no property " + property);
    }

    private static <T extends Comparable<T>> BlockState withValue(BlockState state, Property<T> property, String value) {
        return state.setValue(property, property.getValue(value).orElseThrow());
    }

    @GameTest
    public void aStubKeepsItsStateAndConvertsToTheTarget(GameTestHelper context) {
        Block stub = block("removedmod:stairs");
        if (stub == null) {
            context.fail("the stub for removedmod:stairs should be registered");
            return;
        }

        BlockState placed = set(context, with(with(stub.defaultBlockState(), "facing", "east"), "waterlogged", "true"));
        if (!placed.is(stub)) {
            context.fail("the stub should stay in the world until converted, but found " + placed);
        }

        BlockConverter.Result result = MigrationEvents.blocks().convert(context.getLevel(), context.absolutePos(POS));
        BlockState converted = context.getLevel().getBlockState(context.absolutePos(POS));
        if (result != BlockConverter.Result.CONVERTED || !converted.is(TestFixtures.NEW_BLOCK.get())) {
            context.fail("the stub should convert to the new block, but got " + result + " and " + converted);
        } else if (converted.getValue(FixtureBlock.FACING) != Direction.EAST || !converted.getValue(FixtureBlock.WATERLOGGED)) {
            context.fail("facing and waterlogging should carry over from the stub, but got " + converted);
        }

        context.succeed();
    }

    @GameTest
    public void aStubWithATemplateCopiesItsPropertiesAndConverts(GameTestHelper context) {
        Block stub = block("removedmod:oak_like");
        if (stub == null) {
            context.fail("the stub for removedmod:oak_like should be registered");
            return;
        }

        set(context, with(with(stub.defaultBlockState(), "facing", "west"), "half", "top"));
        MigrationEvents.blocks().convert(context.getLevel(), context.absolutePos(POS));

        BlockState converted = context.getLevel().getBlockState(context.absolutePos(POS));
        if (!converted.is(Blocks.BIRCH_STAIRS)
            || converted.getValue(StairBlock.FACING) != Direction.WEST
            || converted.getValue(StairBlock.HALF) != Half.TOP) {
            context.fail("the stub should convert to birch stairs keeping facing and half, but got " + converted);
        }

        context.succeed();
    }

    @GameTest
    public void aJsonPropertyRenameApplies(GameTestHelper context) {
        Block stub = block("removedmod:renamed");
        if (stub == null) {
            context.fail("the stub for removedmod:renamed should be registered");
            return;
        }

        set(context, with(stub.defaultBlockState(), "dir", "east"));
        MigrationEvents.blocks().convert(context.getLevel(), context.absolutePos(POS));

        BlockState converted = context.getLevel().getBlockState(context.absolutePos(POS));
        if (!converted.is(TestFixtures.NEW_BLOCK.get()) || converted.getValue(FixtureBlock.FACING) != Direction.EAST) {
            context.fail("\"dir\" should be renamed to \"facing\" and keep its value, but got " + converted);
        }

        context.succeed();
    }

    @GameTest
    public void aBrokenStubDropsItsItemWhichConvertsToTheTargets(GameTestHelper context) {
        Block stub = block("removedmod:stairs");
        set(context, stub.defaultBlockState());

        context.getLevel().destroyBlock(context.absolutePos(POS), true);

        // The dropped stub item converts as soon as it enters the world, so what is left is the target's.
        context.assertItemEntityCountIs(stub.asItem(), POS, SEARCH_RANGE, 0);
        context.assertItemEntityCountIs(TestFixtures.NEW_BLOCK.get().asItem(), POS, SEARCH_RANGE, 1);
        context.succeed();
    }

    @GameTest
    public void aStubItemIsRetiredAndConverts(GameTestHelper context) {
        Item stubItem = block("removedmod:stairs").asItem();
        ItemStack stack = new ItemStack(stubItem, 3);

        if (!RetiredItems.isRetired(stack)) {
            context.fail("a stub's item should count as retired so it gets a tooltip and leaves the creative tabs");
        }

        ItemStack converted = MigrationEvents.items().convert(stack);
        if (!converted.is(TestFixtures.NEW_BLOCK.get().asItem()) || converted.getCount() != 3) {
            context.fail("the stub's item should convert with the block, but found " + converted);
        }

        context.succeed();
    }

    @GameTest
    public void aJsonItemMigrationApplies(GameTestHelper context) {
        // The old item isn't registered, so it can only be reached through the saved-data rewrite; here
        // it's enough that the declaration was accepted and did not register anything.
        if (BuiltInRegistries.ITEM.containsKey(Identifier.parse("removedmod:wrench"))) {
            context.fail("a JSON item migration must not register a stub item");
        }

        context.succeed();
    }

    @GameTest
    public void noStubIsRegisteredForAnInstalledModOrWithoutATarget(GameTestHelper context) {
        if (block("installedmod:real") != null) {
            context.fail("a stub under an installed mod's namespace must not be registered");
        }
        if (block("removedmod:unfinished") != null) {
            context.fail("an entry with a blank target must be ignored, stub included");
        }

        context.succeed();
    }

    @GameTest
    public void theDumpListsBlocksWithTheirPropertiesAndFlagsBlockEntities(GameTestHelper context) {
        JsonObject dump = DumpCommand.dump(TestFixtures.MOD_ID);
        JsonObject plain = entry(dump, TestFixtures.id("new_block").toString());
        JsonObject chest = entry(dump, TestFixtures.id("new_chest").toString());

        if (plain == null || chest == null) {
            context.fail("the dump should list both new_block and new_chest, but has " + dump);
            return;
        }

        if (!plain.get("to").getAsString().isEmpty()) {
            context.fail("targets must be left blank, but got " + plain.get("to"));
        }

        JsonObject properties = plain.getAsJsonObject("stub").getAsJsonObject("properties");
        if (!properties.has("facing") || properties.getAsJsonArray("facing").size() != 4 || !properties.has("waterlogged")) {
            context.fail("the dump should list facing (4 values) and waterlogged, but got " + properties);
        }

        if (!chest.has("unsupported") || chest.has("stub")) {
            context.fail("a block with a block entity should be flagged unsupported and get no stub, but got " + chest);
        }

        context.succeed();
    }

    private static JsonObject entry(JsonObject dump, String from) {
        JsonArray blocks = dump.getAsJsonArray("blocks");
        for (JsonElement element : blocks) {
            if (element.getAsJsonObject().get("from").getAsString().equals(from)) {
                return element.getAsJsonObject();
            }
        }

        return null;
    }
}
