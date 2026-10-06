package com.chimericdream.opus.component;

import com.chimericdream.opus.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

/**
 * Which book an Opus book item opens, e.g. {@code opus:guide}. Pack authors set it with
 * {@code /give @s opus:book[opus:book_id="mymod:field_guide"]} or in a loot table / recipe result.
 *
 * <p>UNVERIFIED: never compiled. Mirrors hopper-xtreme's {@code HopperXtremeFilterModeComponent}.
 */
public record BookIdComponent(String bookId) {
    public static final Codec<BookIdComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Codec.STRING.fieldOf("book_id").forGetter(BookIdComponent::bookId)
    ).apply(builder, BookIdComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "book_id");
}
