package com.chimericdream.archaeologytweaks.mixin;

import com.chimericdream.archaeologytweaks.block.ModBlocks;
import com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {
    @Unique
    protected boolean at$canHideItems(Block target) {
        return
            target.equals(Blocks.CLAY)
                || target.equals(Blocks.DIRT)
                || target.equals(Blocks.GRAVEL)
                || target.equals(Blocks.MUD)
                || target.equals(Blocks.PACKED_MUD)
                || target.equals(Blocks.RED_SAND)
                || target.equals(Blocks.ROOTED_DIRT)
                || target.equals(Blocks.SAND)
                || target.equals(Blocks.SOUL_SAND)
                || target.equals(Blocks.SOUL_SOIL);
    }

    @Unique
    protected BlockState at$getHiddenState(Block target) {
        if (target.equals(Blocks.CLAY)) {
            return ModBlocks.SUSPICIOUS_CLAY.get().getDefaultState();
        }

        if (target.equals(Blocks.DIRT)) {
            return ModBlocks.SUSPICIOUS_DIRT.get().getDefaultState();
        }

        if (target.equals(Blocks.GRAVEL)) {
            return Blocks.SUSPICIOUS_GRAVEL.getDefaultState();
        }

        if (target.equals(Blocks.MUD)) {
            return ModBlocks.SUSPICIOUS_MUD.get().getDefaultState();
        }

        if (target.equals(Blocks.PACKED_MUD)) {
            return ModBlocks.SUSPICIOUS_PACKED_MUD.get().getDefaultState();
        }

        if (target.equals(Blocks.RED_SAND)) {
            return ModBlocks.SUSPICIOUS_RED_SAND.get().getDefaultState();
        }

        if (target.equals(Blocks.ROOTED_DIRT)) {
            return ModBlocks.SUSPICIOUS_ROOTED_DIRT.get().getDefaultState();
        }

        if (target.equals(Blocks.SAND)) {
            return Blocks.SUSPICIOUS_SAND.getDefaultState();
        }

        if (target.equals(Blocks.SOUL_SAND)) {
            return ModBlocks.SUSPICIOUS_SOUL_SAND.get().getDefaultState();
        }

        return ModBlocks.SUSPICIOUS_SOUL_SOIL.get().getDefaultState();
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    protected void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        Block target = state.getBlock();
        if (!at$canHideItems(target)) {
            return;
        }

        BlockState newState = at$getHiddenState(target);

        Item offhandItem = player.getOffHandStack().getItem();
        ItemStack mainHandStack = player.getMainHandStack();

        if (offhandItem.equals(Items.BRUSH) && mainHandStack != ItemStack.EMPTY) {
            world.setBlockState(pos, newState);

            if (newState.isOf(Blocks.SUSPICIOUS_SAND) || newState.isOf(Blocks.SUSPICIOUS_GRAVEL)) {
                BrushableBlockEntity be = (BrushableBlockEntity) world.getBlockEntity(pos);

                assert be != null;

                ItemStack itemToHide = mainHandStack.copyWithCount(1);
                mainHandStack.decrementUnlessCreative(1, player);

                be.item = itemToHide;
                be.lootTable = null;
                be.markDirty();

                world.addBlockEntity(be);
            } else {
                ATBrushableBlockEntity be = (ATBrushableBlockEntity) world.getBlockEntity(pos);

                assert be != null;

                ItemStack itemToHide = mainHandStack.copyWithCount(1);
                mainHandStack.decrementUnlessCreative(1, player);

                be.setItem(itemToHide);

                world.addBlockEntity(be);
            }

            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
