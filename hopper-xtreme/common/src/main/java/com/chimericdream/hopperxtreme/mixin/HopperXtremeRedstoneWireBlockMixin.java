package com.chimericdream.hopperxtreme.mixin;

import com.chimericdream.hopperxtreme.tag.CommonTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RedStoneWireBlock.class)
public class HopperXtremeRedstoneWireBlockMixin {
	// BlockState#is(Block) is now the erased TypedInstance#is(T) default method, so the call this
	// redirects (state.is(Blocks.HOPPER)) is emitted as BlockState.is(Ljava/lang/Object;)Z.
	@Redirect(
		method = "canSurviveOn",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z")
	)
	private boolean hx$CanRunOnTop(BlockState state, Object block) {
		return state.is((Block) block) || state.is(CommonTags.HOPPERS);
	}
}
