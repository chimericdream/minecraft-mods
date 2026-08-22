package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.Trims;
import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeehiveBlock.class)
public class EG$BeehiveBlockMixin {
    @Redirect(
        method = "useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CampfireBlock;isSmokeyPos(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eg$treatFullHoneycombTrimAsSmoked(
        Level level,
        BlockPos pos,
        ItemStack itemStack,
        BlockState state,
        Level outerLevel,
        BlockPos outerPos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        return CampfireBlock.isSmokeyPos(level, pos) || TrimSetUtils.isWearingFullTrim(player, Trims.HONEYCOMB_TRIM_ID);
    }
}
