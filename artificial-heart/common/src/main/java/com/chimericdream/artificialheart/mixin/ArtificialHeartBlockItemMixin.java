package com.chimericdream.artificialheart.mixin;

import com.chimericdream.artificialheart.block.ArtificialCreakingHeartBlock;
import com.chimericdream.lib.util.math.DirectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
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

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void ah$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        Block block = this.getBlock();
        //noinspection deprecation
        if (!block.getRegistryEntry().matches(Blocks.RESIN_CLUMP.getRegistryEntry())) {
            return;
        }

        PlayerEntity player = context.getPlayer();
        if (player == null || !player.isSneaking()) {
            return;
        }

        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        BlockState state = world.getBlockState(blockPos);

        if (state.getBlock() instanceof ArtificialCreakingHeartBlock) {
            BlockState updatedState = ah$getUpdatedState(state, context.getSide());

            if (updatedState == null) {
                info.setReturnValue(ActionResult.FAIL);
                return;
            }

            world.setBlockState(blockPos, updatedState);
            world.playSound(player, blockPos, SoundEvents.BLOCK_RESIN_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(context.getPlayer(), updatedState));

            info.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Unique
    private @Nullable BlockState ah$getUpdatedState(BlockState state, Direction hitSide) {
        Direction.Axis axis = state.get(ArtificialCreakingHeartBlock.AXIS);
        Direction face = DirectionUtils.getHitFace(axis, hitSide);

        BooleanProperty faceProp = ArtificialCreakingHeartBlock.getFaceProp(face);
        if (state.get(faceProp)) {
            return null;
        }

        return state.with(faceProp, true);
    }
}
