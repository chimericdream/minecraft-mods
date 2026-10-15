package com.chimericdream.opus.component;

import com.chimericdream.opus.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * The guides stored in a Book of Books, in the order they were added (duplicates are allowed).
 *
 * <p>Carried by the Book of Books itself ({@code selected} is {@link #NONE}) and by the plain {@code opus:book}
 * it turns into when a guide is picked, where {@code selected} is the index of that guide so the item can turn
 * back and know which guide to give up.
 *
 * @param books    book ids such as {@code opus:guide}
 * @param selected index into {@code books} of the guide currently held, or {@link #NONE}
 */
public record TomeContentsComponent(List<String> books, int selected) {
    public static final int NONE = -1;
    public static final TomeContentsComponent EMPTY = new TomeContentsComponent(List.of(), NONE);

    public static final Codec<TomeContentsComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.STRING.listOf().fieldOf("books").forGetter(TomeContentsComponent::books),
        Codec.INT.optionalFieldOf("selected", NONE).forGetter(TomeContentsComponent::selected)
    ).apply(builder, TomeContentsComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tome_contents");

    public TomeContentsComponent {
        books = List.copyOf(books);
    }

    public static TomeContentsComponent of(List<String> books) {
        return new TomeContentsComponent(books, NONE);
    }

    public boolean isEmpty() {
        return books.isEmpty();
    }

    /** True when {@code selected} points at a stored guide, i.e. this belongs to a Book of Books turned into one of its guides. */
    public boolean hasSelection() {
        return selected >= 0 && selected < books.size();
    }

    public TomeContentsComponent withSelected(int index) {
        return new TomeContentsComponent(books, index);
    }

    /** The same guides with nothing selected. */
    public TomeContentsComponent unselected() {
        return new TomeContentsComponent(books, NONE);
    }

    /** The guides without the one at {@code index}; nothing is selected in the result. */
    public TomeContentsComponent without(int index) {
        List<String> remaining = new ArrayList<>(books);
        remaining.remove(index);

        return of(remaining);
    }
}
