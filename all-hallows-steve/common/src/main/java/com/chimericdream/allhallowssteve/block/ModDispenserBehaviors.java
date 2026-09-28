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
 *     <li>The same two interactions for vanilla pumpkins (see {@link JackOLanterns}): a regular torch
 *     turns a carved pumpkin into a jack o'lantern, and shears turn it back. Any of the four torches
 *     fails against a jack o'lantern, same as against a lit decorated pumpkin; the other three torch
 *     types aimed at a carved pumpkin just drop as usual, since vanilla has no lit variant for them.</li>
 * </ul>
 * Every other target falls through to the item's existing dispenser behavior (plain drop for
 * torches; vanilla beehive/sheep shearing for shears) via chimeric-lib's {@link DispenserBehaviors}.
 */
public class ModDispenserBehaviors {
    public static void init() {
        DispenserBehaviors.wrap(List.of(Items.TORCH, Items.SOUL_TORCH, Items.COPPER_TORCH, Items.REDSTONE_TORCH), (level, target, state, stack, source) -> {
            if (state.getBlock() instanceof LitDecoratedPumpkinBlock || JackOLanterns.isLit(state)) {
                return Result.FAIL;
            }

            if (JackOLanterns.canLight(state, stack)) {
                JackOLanterns.light(level, target, state, null);
                stack.shrink(1);

                return Result.SUCCESS;
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
            if (JackOLanterns.isLit(state)) {
                JackOLanterns.extinguish(level, target, state, null);
            } else if (state.getBlock() instanceof LitDecoratedPumpkinBlock) {
                LitDecoratedPumpkinBlock.extinguish(level, target, state, null);
            } else {
                return Result.PASS;
            }

            DispenserBehaviors.damageWithoutPlayer(level, stack);

            return Result.SUCCESS;
        });
    }
}
