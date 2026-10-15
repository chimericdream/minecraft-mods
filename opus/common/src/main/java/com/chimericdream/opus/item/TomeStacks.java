package com.chimericdream.opus.item;

import com.chimericdream.opus.component.BookIdComponent;
import com.chimericdream.opus.component.OpusComponentTypes;
import com.chimericdream.opus.component.TomeContentsComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Builds and inspects the stacks a Book of Books turns into. A Book of Books holds whole item stacks (Opus guides
 * and anything in {@link OpusTags#BOOK_OF_BOOKS_STORABLE}); picking one swaps the stack for that item, which also
 * carries the whole set, so punching the air can swap it back.
 */
public final class TomeStacks {
    private TomeStacks() {
    }

    /** A Book of Books holding {@code contents}. */
    public static ItemStack tome(TomeContentsComponent contents) {
        ItemStack stack = new ItemStack(OpusItems.BOOK_OF_BOOKS.get());
        stack.set(OpusComponentTypes.TOME_CONTENTS.get(), contents.unselected());

        return stack;
    }

    /** A copy of {@code stack} with any Book of Books data removed: what a stored entry looks like. */
    public static ItemStack plain(ItemStack stack) {
        ItemStack copy = stack.copyWithCount(1);
        copy.remove(OpusComponentTypes.TOME_CONTENTS.get());

        return copy;
    }

    /** A plain Opus book for {@code bookId}, with no Book of Books data. */
    public static ItemStack book(String bookId) {
        ItemStack stack = new ItemStack(OpusItems.BOOK.get());
        stack.set(OpusComponentTypes.BOOK_ID.get(), new BookIdComponent(bookId));

        return stack;
    }

    /** The stored item for {@code contents.books().get(index)} that remembers the whole set. */
    public static ItemStack transformed(TomeContentsComponent contents, int index) {
        ItemStack stack = plain(contents.books().get(index));
        stack.set(OpusComponentTypes.TOME_CONTENTS.get(), contents.withSelected(index));

        return stack;
    }

    /** The guide id a book stack opens, falling back to the default guide the way the item itself does. */
    public static String bookId(ItemStack stack) {
        return stack.getOrDefault(OpusComponentTypes.BOOK_ID.get(), new BookIdComponent(OpusBookItem.DEFAULT_BOOK)).bookId();
    }

    /** True for an Opus guide, or an item in the {@code opus:book_of_books_storable} tag: what a Book of Books may hold. */
    public static boolean isStorable(ItemStack stack) {
        return stack.is(OpusItems.BOOK.get()) || stack.is(OpusTags.BOOK_OF_BOOKS_STORABLE);
    }

    /** The contents of a Book of Books, or of an item that was turned out of one; {@code null} for anything else. */
    public static TomeContentsComponent contentsOf(ItemStack stack) {
        return stack.get(OpusComponentTypes.TOME_CONTENTS.get());
    }

    /** True for a Book of Books or an item turned out of one: the things the combining recipe treats as the tome. */
    public static boolean isTome(ItemStack stack) {
        return stack.is(OpusItems.BOOK_OF_BOOKS.get()) || contentsOf(stack) != null;
    }

    /** True for a storable item that is not part of a Book of Books. */
    public static boolean isPlainBook(ItemStack stack) {
        return isStorable(stack) && stack.get(OpusComponentTypes.TOME_CONTENTS.get()) == null;
    }

    /** The contents of an item that was turned out of a Book of Books, or {@code null} if {@code stack} is anything else. */
    public static TomeContentsComponent transformedContents(ItemStack stack) {
        if (stack.is(OpusItems.BOOK_OF_BOOKS.get())) {
            return null;
        }

        TomeContentsComponent contents = stack.get(OpusComponentTypes.TOME_CONTENTS.get());

        return contents != null && contents.hasSelection() ? contents : null;
    }

    /** The Book of Books a turned-out book came from, or an empty stack if {@code stack} is not one. */
    public static ItemStack revert(ItemStack stack) {
        TomeContentsComponent contents = transformedContents(stack);

        return contents == null ? ItemStack.EMPTY : tome(contents);
    }
}
