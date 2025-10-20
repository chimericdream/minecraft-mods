package com.chimericdream.artificialheart.mixin;

import com.chimericdream.artificialheart.block.ModBlocks;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EyeblossomBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public class ArtificialHeartShearsItemMixin {
    @Inject(method = "useOnBlock", at = @At("RETURN"), cancellable = true)
    private void artificialHeartUseOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (!(block instanceof EyeblossomBlock eyeblossom)) {
            return;
        }

        boolean isOpen = eyeblossom.state.isOpen();

        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getStack();
        if (player instanceof ServerPlayerEntity) {
            Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity) player, blockPos, itemStack);
        }

        BlockState clippedEyeblossomState = isOpen
            ? ModBlocks.CLIPPED_OPEN_EYEBLOSSOM_BLOCK.get().getDefaultState()
            : ModBlocks.CLIPPED_EYEBLOSSOM_BLOCK.get().getDefaultState();
        world.setBlockState(blockPos, clippedEyeblossomState);

        world.playSound(player, blockPos, SoundEvents.BLOCK_GROWING_PLANT_CROP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(context.getPlayer(), clippedEyeblossomState));

        if (player != null) {
            itemStack.damage(1, player, context.getHand());
        }

        cir.setReturnValue(ActionResult.SUCCESS);
    }
}
