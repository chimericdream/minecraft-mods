package com.chimericdream.artificialheart.mixin;

import com.chimericdream.artificialheart.block.ArtificialCreakingHeartBlock;
import com.chimericdream.artificialheart.block.ModBlocks;
import com.chimericdream.lib.util.math.DirectionUtils;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class ArtificialHeartAxeItemMixin {
    @Inject(method = "useOnBlock", at = @At("RETURN"), cancellable = true)
    private void artificialHeartUseOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
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
    private void ah$stripResinFromArtificialFace(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getStack();
        if (player instanceof ServerPlayerEntity) {
            Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity) player, pos, itemStack);
        }

        Direction.Axis axis = state.get(ArtificialCreakingHeartBlock.AXIS);
        Direction face = DirectionUtils.getHitFace(axis, context.getSide());

        BooleanProperty faceProp = ArtificialCreakingHeartBlock.getFaceProp(face);
        if (!state.get(faceProp)) {
            return;
        }

        BlockState updatedState = state.with(faceProp, false);
        world.setBlockState(pos, updatedState);

        world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(context.getPlayer(), updatedState));

        if (player != null) {
            itemStack.damage(1, player, context.getHand());
        }

        cir.setReturnValue(ActionResult.SUCCESS);
    }

    @Unique
    private void ah$convertHeartToArtificial(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getStack();
        if (player instanceof ServerPlayerEntity) {
            Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity) player, blockPos, itemStack);
        }

        BlockState artificialHeartState = ModBlocks.ARTIFICIAL_CREAKING_HEART_BLOCK.get().getStateWithProperties(blockState);
        world.setBlockState(blockPos, artificialHeartState);

        world.playSound(player, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(context.getPlayer(), artificialHeartState));

        if (player != null) {
            itemStack.damage(1, player, context.getHand());
        }

        cir.setReturnValue(ActionResult.SUCCESS);
    }
}
