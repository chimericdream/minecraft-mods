package com.chimericdream.archaeologytweaks.mixin;

import com.chimericdream.archaeologytweaks.block.ModBlocks;
import com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
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
            return ModBlocks.SUSPICIOUS_CLAY.get().defaultBlockState();
        }

        if (target.equals(Blocks.DIRT)) {
            return ModBlocks.SUSPICIOUS_DIRT.get().defaultBlockState();
        }

        if (target.equals(Blocks.GRAVEL)) {
            return Blocks.SUSPICIOUS_GRAVEL.defaultBlockState();
        }

        if (target.equals(Blocks.MUD)) {
            return ModBlocks.SUSPICIOUS_MUD.get().defaultBlockState();
        }

        if (target.equals(Blocks.PACKED_MUD)) {
            return ModBlocks.SUSPICIOUS_PACKED_MUD.get().defaultBlockState();
        }

        if (target.equals(Blocks.RED_SAND)) {
            return ModBlocks.SUSPICIOUS_RED_SAND.get().defaultBlockState();
        }

        if (target.equals(Blocks.ROOTED_DIRT)) {
            return ModBlocks.SUSPICIOUS_ROOTED_DIRT.get().defaultBlockState();
        }

        if (target.equals(Blocks.SAND)) {
            return Blocks.SUSPICIOUS_SAND.defaultBlockState();
        }

        if (target.equals(Blocks.SOUL_SAND)) {
            return ModBlocks.SUSPICIOUS_SOUL_SAND.get().defaultBlockState();
        }

        return ModBlocks.SUSPICIOUS_SOUL_SOIL.get().defaultBlockState();
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    protected void onUse(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        Block target = state.getBlock();
        if (!at$canHideItems(target)) {
            return;
        }

        BlockState newState = at$getHiddenState(target);

        Item offhandItem = player.getOffhandItem().getItem();
        ItemStack mainHandStack = player.getMainHandItem();

        if (offhandItem.equals(Items.BRUSH) && mainHandStack != ItemStack.EMPTY) {
            world.setBlockAndUpdate(pos, newState);

            if (newState.is(Blocks.SUSPICIOUS_SAND) || newState.is(Blocks.SUSPICIOUS_GRAVEL)) {
                BrushableBlockEntity be = (BrushableBlockEntity) world.getBlockEntity(pos);

                assert be != null;

                ItemStack itemToHide = mainHandStack.copyWithCount(1);
                mainHandStack.consume(1, player);

                be.item = itemToHide;
                be.lootTable = null;
                be.setChanged();

                world.setBlockEntity(be);
            } else {
                ATBrushableBlockEntity be = (ATBrushableBlockEntity) world.getBlockEntity(pos);

                assert be != null;

                ItemStack itemToHide = mainHandStack.copyWithCount(1);
                mainHandStack.consume(1, player);

                be.setItem(itemToHide);

                world.setBlockEntity(be);
            }

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
