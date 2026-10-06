package com.chimericdream.opus.client.screen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Turns the id strings found in books into stacks to draw.
 *
 * <p>UNVERIFIED: never compiled. {@code Registry#getValue(Identifier)} is the 1.21.2+ name for the old
 * {@code get(ResourceLocation)}; confirm against 26.2. Tags ({@code #ns:name}) are not resolved yet - see the
 * TODO below for the sketch.
 */
final class ItemLookup {
    private ItemLookup() {
    }

    static ItemStack stack(String id, int count) {
        if (id == null || id.startsWith("#")) {
            // TODO(tags): resolve to the members of the item tag and let the caller cycle through them, e.g.
            //   BuiltInRegistries.ITEM.getTagOrEmpty(TagKey.create(Registries.ITEM, Identifier.parse(id.substring(1))))
            //       .map(holder -> new ItemStack(holder.value(), count))
            // For now show a barrier so the page still lays out and the gap is obvious.
            return new ItemStack(Items.BARRIER, count);
        }

        Identifier identifier = Identifier.tryParse(id);
        Item item = identifier == null ? Items.AIR : BuiltInRegistries.ITEM.getValue(identifier);
        return new ItemStack(item == Items.AIR ? Items.BARRIER : item, count);
    }
}
