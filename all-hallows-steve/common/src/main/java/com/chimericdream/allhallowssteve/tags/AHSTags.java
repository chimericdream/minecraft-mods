package com.chimericdream.allhallowssteve.tags;

import com.chimericdream.allhallowssteve.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AHSTags {
    /** Small flowers that smelt into cloves. */
    public static final TagKey<Item> CLOVE_FLOWERS = TagKey.create(Registries.ITEM, id("clove_flowers"));

    /** Logs and woods that can drop cinnamon when stripped with an axe. */
    public static final TagKey<Block> CINNAMON_STRIPPABLES = TagKey.create(Registries.BLOCK, id("cinnamon_strippables"));

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path);
    }
}
