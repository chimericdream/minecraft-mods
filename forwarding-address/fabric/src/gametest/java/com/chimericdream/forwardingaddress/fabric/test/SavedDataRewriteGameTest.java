package com.chimericdream.forwardingaddress.fabric.test;

import com.chimericdream.forwardingaddress.fabric.test.fixture.FixtureBlock;
import com.chimericdream.forwardingaddress.fabric.test.fixture.FixtureContainerEntity;
import com.chimericdream.forwardingaddress.fabric.test.fixture.TestFixtures;
import com.chimericdream.forwardingaddress.migration.SavedDataRewrites;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Do retired IDs that are no longer registered survive a real chunk load?
 * <p>
 * Each scenario serializes a real chunk, swaps a registered fixture ID for one that is not registered,
 * files the result under a chunk that has never been loaded, and loads it through the game's own
 * storage. The tag keeps the current data version, which is exactly what a mod update (as opposed to a
 * Minecraft update) leaves behind.
 * <p>
 * The scenarios run one after another inside a single test: loading a chunk blocks the server thread
 * while it pumps other work, so separate tests interleave and cannot be told apart.
 */
@SuppressWarnings("unused")
public class SavedDataRewriteGameTest {
    private static final String GHOST_BLOCK = TestFixtures.MOD_ID + ":ghost_block";
    private static final String UNMIGRATED_GHOST = TestFixtures.MOD_ID + ":unmigrated_ghost";
    private static final String GHOST_ITEM = TestFixtures.MOD_ID + ":ghost_item";

    private static int shift = 20;

    private record Loaded(LevelChunk chunk, BlockPos shifted, long renames, long calls) {
    }

    private static void rename(Tag tag, String key, String from, String to) {
        if (tag instanceof CompoundTag compound) {
            for (String name : new ArrayList<>(compound.keySet())) {
                if (name.equals(key) && compound.getString(name).filter(from::equals).isPresent()) {
                    compound.putString(name, to);
                } else {
                    rename(compound.get(name), key, from, to);
                }
            }
        } else if (tag instanceof ListTag list) {
            for (Tag element : list) {
                rename(element, key, from, to);
            }
        }
    }

    /**
     * Saves the chunk around {@code source}, lets {@code tamper} edit the saved tag, files it under a chunk
     * further east, and loads that chunk from storage.
     */
    private static Loaded reload(GameTestHelper context, BlockPos source, Consumer<CompoundTag> tamper) {
        ServerLevel level = context.getLevel();
        ChunkPos from = ChunkPos.containing(source);
        // A whole region (32 chunks) apart each time. Filing several chunks into one region file back to back
        // made the later ones load as freshly generated chunks that never reached the hook; the cause is
        // not understood, so keep them apart rather than trust the shortcut.
        shift += 64;
        ChunkPos to = new ChunkPos(from.x() + shift, from.z());

        CompoundTag tag = SerializableChunkData.copyOf(level, level.getChunk(from.x(), from.z())).write();
        tamper.accept(tag);
        tag.putInt("xPos", to.x());
        tag.putInt("zPos", to.z());

        long renamesBefore = SavedDataRewrites.RENAMES.get();
        long callsBefore = SavedDataRewrites.CALLS.get();
        level.getChunkSource().chunkMap.write(to, tag).join();
        LevelChunk loaded = level.getChunk(to.x(), to.z());

        return new Loaded(
            loaded,
            source.offset((to.x() - from.x()) * 16, 0, 0),
            SavedDataRewrites.RENAMES.get() - renamesBefore,
            SavedDataRewrites.CALLS.get() - callsBefore
        );
    }

    @GameTest
    public void retiredIdsThatAreNoLongerRegisteredAreRenamedWhenAChunkLoads(GameTestHelper context) {
        List<String> problems = new ArrayList<>();
        BlockPos relative = new BlockPos(2, 2, 2);
        BlockPos source = context.absolutePos(relative);

        // 1. A block with state, whose id is gone.
        context.getLevel().setBlock(source, TestFixtures.OLD_BLOCK.get().defaultBlockState()
            .setValue(FixtureBlock.FACING, Direction.EAST)
            .setValue(FixtureBlock.WATERLOGGED, true), 3);
        Loaded block = reload(context, source, tag -> rename(tag, "Name", TestFixtures.id("old_block").toString(), GHOST_BLOCK));
        BlockState state = block.chunk().getBlockState(block.shifted());
        if (block.calls() < 1) {
            problems.add("block: the hook never saw the chunk, so the result means nothing");
        } else if (!state.is(TestFixtures.NEW_BLOCK.get())) {
            problems.add("block: expected the new block but loaded " + state + " (hook calls " + block.calls() + ", renames " + block.renames() + ")");
        } else if (state.getValue(FixtureBlock.FACING) != Direction.EAST || !state.getValue(FixtureBlock.WATERLOGGED)) {
            problems.add("block: state did not carry over: " + state);
        }

        // 2. Control: the same thing with an id that has no migration. Records what vanilla does.
        context.getLevel().setBlock(source, TestFixtures.OLD_BLOCK.get().defaultBlockState(), 3);
        Loaded control = reload(context, source, tag -> rename(tag, "Name", TestFixtures.id("old_block").toString(), UNMIGRATED_GHOST));
        BlockState lost = control.chunk().getBlockState(control.shifted());
        if (control.calls() < 1) {
            problems.add("control: the hook never saw the chunk, so the result means nothing");
        } else if (!lost.isAir() || control.renames() != 0) {
            problems.add("control: expected vanilla to drop the unknown block untouched, but got " + lost + " with " + control.renames() + " renames");
        }

        // 3. An item inside a container, whose id is gone. NEW_CHEST has no migration, so only the item is in play.
        context.getLevel().setBlock(source, TestFixtures.NEW_CHEST.get().defaultBlockState(), 3);
        if (context.getLevel().getBlockEntity(source) instanceof FixtureContainerEntity chest) {
            chest.getItems().set(0, new ItemStack(TestFixtures.OLD_ITEM.get(), 5));
        } else {
            problems.add("item: could not set up the fixture container");
        }
        Loaded item = reload(context, source, tag -> rename(tag, "id", TestFixtures.id("old_item").toString(), GHOST_ITEM));
        BlockEntity entity = item.chunk().getBlockEntity(item.shifted());
        if (item.calls() < 1) {
            problems.add("item: the hook never saw the chunk, so the result means nothing");
        } else if (!(entity instanceof FixtureContainerEntity loadedChest)) {
            problems.add("item: the container did not load; found " + entity);
        } else {
            ItemStack stack = loadedChest.getItems().get(0);
            if (!stack.is(TestFixtures.NEW_ITEM.get()) || stack.getCount() != 5) {
                problems.add("item: expected 5 of the new item but found " + stack + " (renames " + item.renames() + ")");
            }
        }

        if (!problems.isEmpty()) {
            context.fail(String.join("; ", problems));
        }

        context.succeed();
    }
}
