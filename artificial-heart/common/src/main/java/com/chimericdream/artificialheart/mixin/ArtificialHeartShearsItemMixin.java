package com.chimericdream.artificialheart.mixin;

import com.chimericdream.artificialheart.block.ModBlocks;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EyeblossomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public class ArtificialHeartShearsItemMixin {
    @Inject(method = "useOn", at = @At("RETURN"), cancellable = true)
    private void artificialHeartUseOnBlockMixin(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (!(block instanceof EyeblossomBlock eyeblossom)) {
            return;
        }

        boolean isOpen = eyeblossom.type.emitSounds();

        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockPos, itemStack);
        }

        BlockState clippedEyeblossomState = isOpen
            ? ModBlocks.CLIPPED_OPEN_EYEBLOSSOM_BLOCK.get().defaultBlockState()
            : ModBlocks.CLIPPED_EYEBLOSSOM_BLOCK.get().defaultBlockState();
        world.setBlockAndUpdate(blockPos, clippedEyeblossomState);

        world.playSound(player, blockPos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
        world.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(context.getPlayer(), clippedEyeblossomState));

        if (player != null) {
            itemStack.hurtAndBreak(1, player, context.getHand());
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
