package com.chimericdream.allhallowssteve.block;

import com.chimericdream.lib.dispenser.BlockTargetDispenseBehavior.Result;
import com.chimericdream.lib.dispenser.DispenserBehaviors;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Dispenser automation for the torch/shears interactions a player can already do by hand:
 * <ul>
 *     <li>Any of the four torches lights an unlit {@link DecoratedPumpkinBlock}. Aimed at a pumpkin
 *     that's already lit, the dispenser fails (click, torch stays put) rather than spitting the torch
 *     out, so a clock-driven dispenser doesn't pile torches up on the ground.</li>
 *     <li>Shears un-light a {@link LitDecoratedPumpkinBlock}, dropping its torch.</li>
 * </ul>
 * Every other target falls through to the item's existing dispenser behavior (plain drop for
 * torches; vanilla beehive/sheep shearing for shears) via chimeric-lib's {@link DispenserBehaviors}.
 */
public class ModDispenserBehaviors {
    public static void init() {
        DispenserBehaviors.wrap(List.of(Items.TORCH, Items.SOUL_TORCH, Items.COPPER_TORCH, Items.REDSTONE_TORCH), (level, target, state, stack, source) -> {
            if (state.getBlock() instanceof LitDecoratedPumpkinBlock) {
                return Result.FAIL;
            }

            if (!(state.getBlock() instanceof DecoratedPumpkinBlock)) {
                return Result.PASS;
            }

            Block litVariant = DecoratedPumpkinBlock.litVariantFor(stack);
            if (litVariant == null) {
                return Result.PASS;
            }

            DecoratedPumpkinBlock.light(level, target, state, litVariant, null);
            stack.shrink(1);

            return Result.SUCCESS;
        });

        DispenserBehaviors.wrap(Items.SHEARS, (level, target, state, stack, source) -> {
            if (!(state.getBlock() instanceof LitDecoratedPumpkinBlock)) {
                return Result.PASS;
            }

            LitDecoratedPumpkinBlock.extinguish(level, target, state, null);
            DispenserBehaviors.damageWithoutPlayer(level, stack);

            return Result.SUCCESS;
        });
    }
}
