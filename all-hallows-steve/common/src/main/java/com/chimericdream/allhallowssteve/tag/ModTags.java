package com.chimericdream.allhallowssteve.tag;

import com.chimericdream.allhallowssteve.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ModTags {
    /** What the cider press accepts in its fruit slot. Just apples to start, but a tag so packs can add to it. */
    public static final TagKey<Item> CIDER_PRESS_PRESSABLE = item("cider_press_pressable");

    /** What the cider press accepts in its fuel slot. See {@code CiderPressBlockEntity#fuelCharges} for how many pressings each gives. */
    public static final TagKey<Item> CIDER_PRESS_FUEL = item("cider_press_fuel");

    /** Common-namespace fertilizer tag, so other mods treat pomace as one. */
    public static final TagKey<Item> FERTILIZERS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "fertilizers"));

    /** Livestock that heal a little when fed pomace. */
    public static final TagKey<EntityType<?>> POMACE_FEEDABLE = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pomace_feedable"));

    private static TagKey<Item> item(String name) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, name));
    }
}
