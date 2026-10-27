package com.chimericdream.forwardingaddress.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

/**
 * Converts every placed block of one type into another block.
 *
 * @param from           the retired block's ID; the block must stay registered
 * @param to             the replacement block's ID
 * @param propertyMapper optional adjustment of the converted block state
 * @param entityMapper   moves block-entity data when the two blocks use different block-entity types;
 *                       without it such a block is left alone
 * @param hiddenSlots    optional count of container slots past {@code getContainerSize()} that must
 *                       survive the swap (Hopper X-Treme's trailing filter slot)
 * @param itemMapper     optional adjustment for the block's item, which converts too
 */
public record BlockMigration(
    Identifier from,
    Identifier to,
    @Nullable PropertyMapper propertyMapper,
    @Nullable BlockEntityMapper entityMapper,
    @Nullable ToIntFunction<BlockEntity> hiddenSlots,
    @Nullable ItemMapper itemMapper
) {
}
