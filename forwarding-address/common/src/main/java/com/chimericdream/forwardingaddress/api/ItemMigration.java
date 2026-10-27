package com.chimericdream.forwardingaddress.api;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Converts every stack of one item into another item.
 *
 * @param from   the retired item's ID; the item must stay registered
 * @param to     the replacement item's ID
 * @param mapper optional adjustment of the converted stack
 */
public record ItemMigration(Identifier from, Identifier to, @Nullable ItemMapper mapper) {
}
