package com.chimericdream.forwardingaddress.test.migration;

import com.chimericdream.forwardingaddress.api.BlockMigration;
import com.chimericdream.forwardingaddress.api.ItemMigration;
import com.chimericdream.forwardingaddress.migration.IdRewriter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdRewriterTest {
    private static final String OLD = "oldmod:trim";
    private static final String NEW = "newmod:trim";

    private static Identifier id(String value) {
        return Identifier.parse(value);
    }

    private static IdRewriter rewriter() {
        return new IdRewriter(java.util.Map.of(OLD, NEW), java.util.Map.of(OLD, NEW));
    }

    private static CompoundTag paletteEntry(String name) {
        CompoundTag entry = new CompoundTag();
        entry.putString("Name", name);
        CompoundTag properties = new CompoundTag();
        properties.putString("facing", "east");
        properties.putString("waterlogged", "true");
        entry.put("Properties", properties);
        return entry;
    }

    private static CompoundTag stack(String itemId, int count) {
        CompoundTag stack = new CompoundTag();
        stack.putString("id", itemId);
        stack.put("count", IntTag.valueOf(count));
        return stack;
    }

    private static CompoundTag chunk() {
        ListTag palette = new ListTag();
        palette.add(paletteEntry("minecraft:stone"));
        palette.add(paletteEntry(OLD));

        CompoundTag blockStates = new CompoundTag();
        blockStates.put("palette", palette);

        CompoundTag section = new CompoundTag();
        section.put("block_states", blockStates);

        ListTag biomePalette = new ListTag();
        biomePalette.add(StringTag.valueOf(OLD));
        CompoundTag biomes = new CompoundTag();
        biomes.put("palette", biomePalette);
        section.put("biomes", biomes);

        ListTag sections = new ListTag();
        sections.add(section);

        CompoundTag tick = new CompoundTag();
        tick.putString("i", OLD);
        ListTag ticks = new ListTag();
        ticks.add(tick);

        CompoundTag root = new CompoundTag();
        root.put("sections", sections);
        root.put("block_ticks", ticks);
        return root;
    }

    @Test
    void renamesPaletteEntriesAndKeepsTheirProperties() {
        CompoundTag root = chunk();

        int renamed = rewriter().rewrite(root);

        CompoundTag palette1 = root.getList("sections").orElseThrow().getCompound(0).orElseThrow()
            .getCompound("block_states").orElseThrow().getList("palette").orElseThrow().getCompound(1).orElseThrow();
        assertEquals(NEW, palette1.getString("Name").orElseThrow());
        assertEquals("east", palette1.getCompound("Properties").orElseThrow().getString("facing").orElseThrow());
        assertEquals("true", palette1.getCompound("Properties").orElseThrow().getString("waterlogged").orElseThrow());
        assertEquals(2, renamed, "the palette entry and the pending tick");
    }

    @Test
    void renamesPendingBlockTicks() {
        CompoundTag root = chunk();

        rewriter().rewrite(root);

        assertEquals(NEW, root.getList("block_ticks").orElseThrow().getCompound(0).orElseThrow().getString("i").orElseThrow());
    }

    @Test
    void leavesBystandersAlone() {
        CompoundTag root = chunk();

        rewriter().rewrite(root);

        CompoundTag section = root.getList("sections").orElseThrow().getCompound(0).orElseThrow();
        CompoundTag stone = section.getCompound("block_states").orElseThrow().getList("palette").orElseThrow().getCompound(0).orElseThrow();
        assertEquals("minecraft:stone", stone.getString("Name").orElseThrow());

        // A biome palette holds plain strings, never block IDs, so a matching string is not renamed.
        assertEquals(OLD, section.getCompound("biomes").orElseThrow().getList("palette").orElseThrow().getString(0).orElseThrow());
    }

    @Test
    void renamesItemStacksAtAnyDepthAndKeepsTheirCount() {
        // chest -> Items -> shulker box -> container component -> stack, like a bundle or shulker nesting
        CompoundTag inner = stack(OLD, 7);
        CompoundTag slot = new CompoundTag();
        slot.put("item", inner);
        slot.put("slot", IntTag.valueOf(3));
        ListTag container = new ListTag();
        container.add(slot);
        CompoundTag components = new CompoundTag();
        components.put("minecraft:container", container);

        CompoundTag shulker = stack("minecraft:shulker_box", 1);
        shulker.put("components", components);
        shulker.putString("note", OLD);

        ListTag items = new ListTag();
        items.add(shulker);
        CompoundTag blockEntity = new CompoundTag();
        blockEntity.put("Items", items);

        int renamed = rewriter().rewrite(blockEntity);

        assertEquals(1, renamed);
        assertEquals(NEW, inner.getString("id").orElseThrow());
        assertEquals(7, inner.getIntOr("count", 0));
        assertEquals(OLD, shulker.getString("note").orElseThrow(), "a string that is not an item id is not touched");
    }

    @Test
    void doesNotTreatAnIdWithoutACountAsAnItem() {
        // Entities and block entities also have an "id"; only a stack (id + count) is an item.
        CompoundTag entity = new CompoundTag();
        entity.putString("id", OLD);

        assertEquals(0, rewriter().rewrite(entity));
        assertEquals(OLD, entity.getString("id").orElseThrow());
    }

    @Test
    void anEmptyRewriterTouchesNothing() {
        CompoundTag root = chunk();
        CompoundTag copy = root.copy();

        assertEquals(0, IdRewriter.EMPTY.rewrite(root));
        assertEquals(copy, root);
    }

    @Test
    void appliesOnlyToRetiredIdsWhoseTargetExists() {
        Set<String> registeredBlocks = Set.of("newmod:trim", "oldmod:still_here", "newmod:other");
        IdRewriter rewriter = IdRewriter.from(
            List.of(
                new BlockMigration(id("oldmod:trim"), id("newmod:trim"), null, null, null, null),
                // still registered: stays on the live-conversion path
                new BlockMigration(id("oldmod:still_here"), id("newmod:other"), null, null, null, null),
                // target missing: renaming would only lose the original id
                new BlockMigration(id("oldmod:orphan"), id("missingmod:x"), null, null, null, null),
                // needs a mapper: rename alone can't do it
                new BlockMigration(id("oldmod:mapped"), id("newmod:other"), (from, to) -> to, null, null, null)
            ),
            List.of(new ItemMigration(id("oldmod:trim"), id("newmod:trim"), null)),
            blockId -> registeredBlocks.contains(blockId.toString()),
            itemId -> itemId.toString().equals("newmod:trim")
        );

        CompoundTag root = new CompoundTag();
        ListTag palette = new ListTag();
        for (String name : List.of("oldmod:trim", "oldmod:still_here", "oldmod:orphan", "oldmod:mapped")) {
            palette.add(paletteEntry(name));
        }
        root.put("palette", palette);

        assertEquals(1, rewriter.rewrite(root));

        List<String> names = palette.stream()
            .map(tag -> ((CompoundTag) tag).getString("Name").orElseThrow())
            .toList();
        assertEquals(List.of("newmod:trim", "oldmod:still_here", "oldmod:orphan", "oldmod:mapped"), names);
        assertTrue(!rewriter.isEmpty());
    }
}
