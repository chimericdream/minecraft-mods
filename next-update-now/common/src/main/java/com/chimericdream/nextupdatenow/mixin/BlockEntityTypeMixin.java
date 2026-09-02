package com.chimericdream.nextupdatenow.mixin;

import com.chimericdream.nextupdatenow.block.ModBlockEntityValidBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * See {@link ModBlockEntityValidBlocks} for why this is needed: vanilla's block entity types bake an
 * immutable valid-block set at class-init, so blocks this mod registers under an existing type
 * (poplar's sign/hanging-sign blocks) would otherwise always fail validation.
 */
@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void nextupdatenow$allowExtraValidBlocks(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        BlockEntityType<?> self = (BlockEntityType<?>) (Object) this;
        if (ModBlockEntityValidBlocks.isExtraValid(self, state.getBlock())) {
            cir.setReturnValue(true);
        }
    }
}
