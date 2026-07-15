package com.chimericdream.artificialheart.mixin;

import com.chimericdream.artificialheart.block.ArtificialCreakingHeartBlock;
import com.chimericdream.artificialheart.block.ModBlocks;
import com.chimericdream.lib.util.math.DirectionUtils;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class ArtificialHeartAxeItemMixin {
    @Inject(method = "useOn", at = @At("RETURN"), cancellable = true)
    private void artificialHeartUseOnBlockMixin(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof CreakingHeartBlock) {
            ah$convertHeartToArtificial(context, cir);
            return;
        }

        if (block instanceof ArtificialCreakingHeartBlock) {
            ah$stripResinFromArtificialFace(context, cir);
        }
    }

    @Unique
    private void ah$stripResinFromArtificialFace(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);

        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, pos, itemStack);
        }

        Direction.Axis axis = state.getValue(ArtificialCreakingHeartBlock.AXIS);
        Direction face = DirectionUtils.getHitFace(axis, context.getClickedFace());

        BooleanProperty faceProp = ArtificialCreakingHeartBlock.getFaceProp(face);
        if (!state.getValue(faceProp)) {
            return;
        }

        BlockState updatedState = state.setValue(faceProp, false);
        world.setBlockAndUpdate(pos, updatedState);

        world.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
        world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(context.getPlayer(), updatedState));

        if (player != null) {
            itemStack.hurtAndBreak(1, player, context.getHand());
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Unique
    private void ah$convertHeartToArtificial(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = world.getBlockState(blockPos);

        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockPos, itemStack);
        }

        BlockState artificialHeartState = ModBlocks.ARTIFICIAL_CREAKING_HEART_BLOCK.get().withPropertiesOf(blockState);
        world.setBlockAndUpdate(blockPos, artificialHeartState);

        world.playSound(player, blockPos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
        world.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(context.getPlayer(), artificialHeartState));

        if (player != null) {
            itemStack.hurtAndBreak(1, player, context.getHand());
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
