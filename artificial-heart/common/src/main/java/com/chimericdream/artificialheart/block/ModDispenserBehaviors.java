package com.chimericdream.artificialheart.block;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Mirrors vanilla's {@code Blocks.CARVED_PUMPKIN} dispenser behavior (registered inline in
 * {@code DispenseItemBehavior}'s bootstrap): a dispenser aimed at a valid golem base places the
 * pumpkin (completing the golem, via the normal {@code onPlace} -> {@code trySpawnGolem} path)
 * instead of falling back to equipping it onto a nearby mob.
 */
public class ModDispenserBehaviors {
    public static void init() {
        DispenserBlock.registerBehavior(ModBlocks.PALE_CARVED_PUMPKIN_BLOCK.get(), new OptionalDispenseItemBehavior() {
            protected ItemStack execute(final BlockSource source, final ItemStack dispensed) {
                Level level = source.level();
                BlockPos target = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                PaleCarvedPumpkinBlock pumpkinBlock = (PaleCarvedPumpkinBlock) ModBlocks.PALE_CARVED_PUMPKIN_BLOCK.get();

                if (level.isEmptyBlock(target) && pumpkinBlock.canSpawnGolem(level, target)) {
                    if (!level.isClientSide()) {
                        level.setBlock(target, pumpkinBlock.defaultBlockState(), 3);
                        level.gameEvent(null, GameEvent.BLOCK_PLACE, target);
                    }

                    dispensed.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(EquipmentDispenseItemBehavior.dispenseEquipment(source, dispensed));
                }

                return dispensed;
            }
        });
    }
}
