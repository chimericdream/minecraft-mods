package com.chimericdream.opus.component;

import com.chimericdream.opus.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The books stored in a Book of Books, in the order they were added (duplicates are allowed). Each is a full
 * single-item stack: an Opus guide, or any item in the {@code opus:book_of_books_storable} tag.
 *
 * <p>Carried by the Book of Books itself ({@code selected} is {@link #NONE}) and by the stored item it turns
 * into when one is picked, where {@code selected} is the index of that entry so the item can turn back and know
 * which entry to give up.
 *
 * @param books    the stored stacks, each of count 1 and without a {@code tome_contents} component of its own
 * @param selected index into {@code books} of the entry currently held, or {@link #NONE}
 */
public record TomeContentsComponent(List<ItemStack> books, int selected) {
    public static final int NONE = -1;
    public static final TomeContentsComponent EMPTY = new TomeContentsComponent(List.of(), NONE);

    public static final Codec<TomeContentsComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ItemStack.CODEC.listOf().fieldOf("books").forGetter(TomeContentsComponent::books),
        Codec.INT.optionalFieldOf("selected", NONE).forGetter(TomeContentsComponent::selected)
    ).apply(builder, TomeContentsComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tome_contents");

    public TomeContentsComponent {
        books = books.stream().map(TomeContentsComponent::single).toList();
    }

    private static ItemStack single(ItemStack stack) {
        return stack.copyWithCount(1);
    }

    // ItemStack has no value equality of its own.
    @Override
    public boolean equals(Object other) {
        return other instanceof TomeContentsComponent that
            && selected == that.selected
            && ItemStack.listMatches(books, that.books);
    }

    @Override
    public int hashCode() {
        return 31 * ItemStack.hashStackList(books) + selected;
    }

    public static TomeContentsComponent of(List<ItemStack> books) {
        return new TomeContentsComponent(books, NONE);
    }

    public boolean isEmpty() {
        return books.isEmpty();
    }

    /** True when {@code selected} points at a stored book, i.e. this belongs to a Book of Books turned into one of its books. */
    public boolean hasSelection() {
        return selected >= 0 && selected < books.size();
    }

    public TomeContentsComponent withSelected(int index) {
        return new TomeContentsComponent(books, index);
    }

    /** The same books with nothing selected. */
    public TomeContentsComponent unselected() {
        return new TomeContentsComponent(books, NONE);
    }

    /** The books without the one at {@code index}; nothing is selected in the result. */
    public TomeContentsComponent without(int index) {
        List<ItemStack> remaining = new ArrayList<>(books);
        remaining.remove(index);

        return of(remaining);
    }
}
