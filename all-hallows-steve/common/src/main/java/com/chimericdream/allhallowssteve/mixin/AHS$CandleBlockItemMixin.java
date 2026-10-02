package com.chimericdream.allhallowssteve.mixin;

import com.chimericdream.allhallowssteve.block.CandleLitDecoratedPumpkinBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Shift-clicking a candle on a decorated pumpkin adds the candle to it (see
 * {@link CandleLitDecoratedPumpkinBlock}). Hooked on the item for the same reason as
 * {@code AHS$TorchBlockItemMixin}: a sneaking player with a nonempty hand skips the block's own
 * {@code useItemOn} entirely, so the block can never see this interaction. A plain click is left to
 * vanilla, which places the candle on the pumpkin's face.
 */
@Mixin(BlockItem.class)
abstract public class AHS$CandleBlockItemMixin {
    @Shadow
    abstract public Block getBlock();

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void ahs$addCandleToDecoratedPumpkin(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(this.getBlock() instanceof CandleBlock)) {
            return;
        }

        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        InteractionResult result = CandleLitDecoratedPumpkinBlock.tryAddCandle(context.getItemInHand(), state, level, pos, player);
        if (result != InteractionResult.PASS) {
            cir.setReturnValue(result);
        }
    }
}
