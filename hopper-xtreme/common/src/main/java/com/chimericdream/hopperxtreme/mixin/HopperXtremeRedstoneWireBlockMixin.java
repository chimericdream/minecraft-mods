package com.chimericdream.hopperxtreme.mixin;

import com.chimericdream.hopperxtreme.tag.CommonTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
public class HopperXtremeRedstoneWireBlockMixin {
	@Redirect(
		method = "canRunOnTop",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z")
	)
	private boolean hopperXtremeCanRunOnTop(BlockState state, Block block) {
		return state.is(block) || state.is(CommonTags.HOPPERS);
	}

//	@Inject(
//        method = "canRunOnTop(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
//        at = @At(value = "RETURN"),
//        cancellable = true
//    )
//    private void hopperXtremeCanRunOnTop(BlockView world, BlockPos pos, BlockState floor, CallbackInfoReturnable<Boolean> cir) {
//		if (floor.isIn(CommonTags.HOPPERS)) {
//			cir.setReturnValue(true);
//		}
//	}
}
