package com.chimericdream.allhallowssteve.mixin;

import com.chimericdream.allhallowssteve.block.DecoratedPumpkinBlock;
import com.chimericdream.allhallowssteve.block.JackOLanterns;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * {@code ServerPlayerGameMode#useItemOn} skips {@code BlockState#useItemOn} entirely whenever the
 * player is sneaking with a nonempty hand (its {@code suppressUsingBlock} check) and goes straight
 * to the held item's own placement logic instead. Since lighting a {@link DecoratedPumpkinBlock} is
 * deliberately shift-gated, {@code DecoratedPumpkinBlock}'s block-level interaction can never
 * actually run for this case — the torch item's own placement has to be intercepted here instead.
 * <p>
 * All four torch items ({@code Items.TORCH}/{@code SOUL_TORCH}/{@code COPPER_TORCH}/
 * {@code REDSTONE_TORCH}) share the same {@code StandingAndWallBlockItem} class (also used by coral
 * fans and mob heads), and that class doesn't override {@code useOn} — so the mixin targets
 * {@code BlockItem} itself and identifies torches by {@code getBlock()} instead.
 * <p>
 * The same shift-click also lights a vanilla carved pumpkin into a jack o'lantern (regular torch
 * only, see {@link JackOLanterns}). It's shift-gated for the same reason: a plain click keeps
 * vanilla's behavior of placing the torch on the pumpkin's face.
 */
@Mixin(BlockItem.class)
abstract public class AHS$TorchBlockItemMixin {
    @Shadow
    abstract public Block getBlock();

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void ahs$lightDecoratedPumpkin(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Block block = this.getBlock();
        if (block != Blocks.TORCH && block != Blocks.SOUL_TORCH && block != Blocks.COPPER_TORCH && block != Blocks.REDSTONE_TORCH) {
            return;
        }

        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof DecoratedPumpkinBlock) {
            cir.setReturnValue(DecoratedPumpkinBlock.tryLight(context.getItemInHand(), state, level, pos, player));
            return;
        }

        ItemStack itemStack = context.getItemInHand();
        if (!JackOLanterns.canLight(state, itemStack)) {
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            JackOLanterns.light(serverLevel, pos, state, player);

            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
