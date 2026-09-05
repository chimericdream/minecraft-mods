package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.block.LivnaBlock;
import com.chimericdream.camelnostrils.tag.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class CN$AnvilMenuMixin {
    @Inject(method = "lambda$onTake$0", at = @At("HEAD"), cancellable = true)
    private static void cn$onTakeAccessExecute(final Player player, final Level level, final BlockPos pos, final CallbackInfo ci) {
        BlockState state = level.getBlockState(pos);
        if (!player.hasInfiniteMaterials() && state.is(ModTags.LIVNA_BLOCKS) && player.getRandom().nextFloat() < 0.12F) {
            BlockState newBlockState = LivnaBlock.damage(state);
            if (newBlockState == null) {
                level.removeBlock(pos, false);
                level.levelEvent(1029, pos, 0);
            } else {
                level.setBlock(pos, newBlockState, 2);
                level.levelEvent(1030, pos, 0);
            }
            ci.cancel();
        }
    }
}
