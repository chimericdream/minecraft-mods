package com.chimericdream.opus.client.item;

import net.minecraft.resources.Identifier;

/**
 * How one book's item should look. At most one of the two is set; {@code model} wins over {@code texture} when
 * {@code book.yml} supplies both, and both are already checked to exist in the loaded packs.
 *
 * @param texture sprite id in the items atlas, e.g. {@code pannotia:item/guide_book}
 * @param model model file id, e.g. {@code pannotia:item/guide_book} for {@code models/item/guide_book.json}
 */
public record BookLook(Identifier texture, Identifier model) {
}
