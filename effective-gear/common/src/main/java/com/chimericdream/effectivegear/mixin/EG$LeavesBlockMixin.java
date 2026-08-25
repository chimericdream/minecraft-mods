package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.enchantment.PreservingHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds a {@code preserved} blockstate property to every leaves block so the Preserving enchantment
 * (shears) has somewhere to carry "render with the default, non-biome-tinted color" from a mined item
 * through to its next placement, via the vanilla {@code BLOCK_STATE} item component.
 *
 * <p>{@code defaultBlockState()}/{@code registerDefaultState()} are declared on {@link Block}, not
 * {@link LeavesBlock} itself, so they can't be {@code @Shadow}ed here (Mixin only resolves shadow
 * members declared directly on the mixin's own target class) — call them via a plain {@link Block}
 * cast instead. {@code registerDefaultState} is widened to public for this in
 * {@code effectivegear.accesswidener}.
 */
@Mixin(LeavesBlock.class)
public abstract class EG$LeavesBlockMixin {
    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void eg$addPreservedProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(PreservingHelper.PRESERVED);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void eg$setDefaultPreserved(float leafParticleChance, Properties properties, CallbackInfo ci) {
        Block self = (Block) (Object) this;
        self.registerDefaultState(self.defaultBlockState().setValue(PreservingHelper.PRESERVED, false));
    }
}
