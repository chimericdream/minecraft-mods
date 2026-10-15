package com.chimericdream.opus.item;

import com.chimericdream.opus.component.BookIdComponent;
import com.chimericdream.opus.component.OpusComponentTypes;
import com.chimericdream.opus.component.TomeContentsComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Builds and inspects the stacks a Book of Books turns into. A Book of Books holds guides as ids; picking one
 * swaps the stack for a real {@code opus:book} for that guide that also carries the whole set, so punching
 * the air can swap it back.
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

    /** A plain book for {@code bookId}, with no Book of Books data. */
    public static ItemStack book(String bookId) {
        ItemStack stack = new ItemStack(OpusItems.BOOK.get());
        stack.set(OpusComponentTypes.BOOK_ID.get(), new BookIdComponent(bookId));

        return stack;
    }

    /** The book for {@code contents.books().get(index)} that remembers the whole set. */
    public static ItemStack transformed(TomeContentsComponent contents, int index) {
        ItemStack stack = book(contents.books().get(index));
        stack.set(OpusComponentTypes.TOME_CONTENTS.get(), contents.withSelected(index));

        return stack;
    }

    /** The guide id a book stack opens, falling back to the default guide the way the item itself does. */
    public static String bookId(ItemStack stack) {
        return stack.getOrDefault(OpusComponentTypes.BOOK_ID.get(), new BookIdComponent(OpusBookItem.DEFAULT_BOOK)).bookId();
    }

    /** The contents of a Book of Books, or of a book that was turned out of one; {@code null} for anything else. */
    public static TomeContentsComponent contentsOf(ItemStack stack) {
        if (stack.is(OpusItems.BOOK_OF_BOOKS.get()) || stack.is(OpusItems.BOOK.get())) {
            return stack.get(OpusComponentTypes.TOME_CONTENTS.get());
        }

        return null;
    }

    /** True for a Book of Books or a book turned out of one: the things the combining recipe treats as the tome. */
    public static boolean isTome(ItemStack stack) {
        return stack.is(OpusItems.BOOK_OF_BOOKS.get()) || contentsOf(stack) != null;
    }

    /** True for an ordinary Opus book that is not part of a Book of Books. */
    public static boolean isPlainBook(ItemStack stack) {
        return stack.is(OpusItems.BOOK.get()) && stack.get(OpusComponentTypes.TOME_CONTENTS.get()) == null;
    }

    /** The contents of a book that was turned out of a Book of Books, or {@code null} if {@code stack} is anything else. */
    public static TomeContentsComponent transformedContents(ItemStack stack) {
        if (!stack.is(OpusItems.BOOK.get())) {
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
