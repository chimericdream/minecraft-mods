package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.block.ModBlockEntityValidBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * See {@link ModBlockEntityValidBlocks} for why this is needed: vanilla's block entity types bake an
 * immutable valid-block set at class-init, so the upside-down beds (which reuse vanilla's
 * {@code BedBlockEntity}) would otherwise always fail validation.
 */
@Mixin(BlockEntityType.class)
public class CN$BlockEntityTypeMixin {
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void cn$allowExtraValidBlocks(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        BlockEntityType<?> self = (BlockEntityType<?>) (Object) this;
        if (ModBlockEntityValidBlocks.isExtraValid(self, state.getBlock())) {
            cir.setReturnValue(true);
        }
    }
}
