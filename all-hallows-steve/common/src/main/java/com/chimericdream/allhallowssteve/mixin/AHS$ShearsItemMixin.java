package com.chimericdream.allhallowssteve.mixin;

import com.chimericdream.allhallowssteve.block.JackOLanterns;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Shears on a vanilla jack o'lantern remove its torch (see {@link JackOLanterns#extinguish}).
 * <p>
 * Hooked on the item rather than the block because vanilla's {@code CarvedPumpkinBlock} has no
 * {@code useItemOn} of its own to extend. {@code ItemStack#useOn} awards the {@code ITEM_USED} stat
 * for a successful item interaction, so unlike {@code LitDecoratedPumpkinBlock#useItemOn} this
 * doesn't award it itself.
 */
@Mixin(ShearsItem.class)
abstract public class AHS$ShearsItemMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void ahs$extinguishJackOLantern(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!JackOLanterns.isLit(state)) {
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            Player player = context.getPlayer();
            JackOLanterns.extinguish(serverLevel, pos, state, player);

            ItemStack itemStack = context.getItemInHand();
            if (player != null) {
                itemStack.hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
            } else {
                itemStack.hurtAndBreak(1, serverLevel, null, item -> {
                });
            }
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
