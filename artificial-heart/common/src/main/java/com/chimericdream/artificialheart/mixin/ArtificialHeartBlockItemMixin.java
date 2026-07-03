package com.chimericdream.artificialheart.mixin;

import com.chimericdream.artificialheart.block.ArtificialCreakingHeartBlock;
import com.chimericdream.lib.util.math.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
abstract public class ArtificialHeartBlockItemMixin {
    @Shadow
    abstract public Block getBlock();

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void ah$useOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
        Block block = this.getBlock();
        //noinspection deprecation
        if (!block.builtInRegistryHolder().is(Blocks.RESIN_CLUMP.builtInRegistryHolder())) {
            return;
        }

        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return;
        }

        BlockPos blockPos = context.getClickedPos();
        Level world = context.getLevel();
        BlockState state = world.getBlockState(blockPos);

        if (state.getBlock() instanceof ArtificialCreakingHeartBlock) {
            BlockState updatedState = ah$getUpdatedState(state, context.getClickedFace());

            if (updatedState == null) {
                info.setReturnValue(InteractionResult.FAIL);
                return;
            }

            world.setBlockAndUpdate(blockPos, updatedState);
            world.playSound(player, blockPos, SoundEvents.RESIN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(context.getPlayer(), updatedState));

            info.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Unique
    private @Nullable BlockState ah$getUpdatedState(BlockState state, Direction hitSide) {
        Direction.Axis axis = state.getValue(ArtificialCreakingHeartBlock.AXIS);
        Direction face = DirectionUtils.getHitFace(axis, hitSide);

        BooleanProperty faceProp = ArtificialCreakingHeartBlock.getFaceProp(face);
        if (state.getValue(faceProp)) {
            return null;
        }

        return state.setValue(faceProp, true);
    }
}
