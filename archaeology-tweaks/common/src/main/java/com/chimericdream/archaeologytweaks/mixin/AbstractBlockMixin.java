package com.chimericdream.archaeologytweaks.mixin;

import com.chimericdream.archaeologytweaks.block.ModBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
    protected boolean canHideItems(Block target) {
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
    protected BlockState getHiddenState(Block target) {
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
        if (!canHideItems(target)) {
            return;
        }

        BlockState newState = getHiddenState(target);

        Item offhandItem = player.getOffHandStack().getItem();

        if (offhandItem.equals(Items.BRUSH) && player.getMainHandStack() != ItemStack.EMPTY) {
            world.setBlockState(pos, newState);
            NbtElement itemData = player.getMainHandStack().encode(world.getRegistryManager());

            BrushableBlockEntity be = (BrushableBlockEntity) world.getBlockEntity(pos);
            NbtCompound nbt = new NbtCompound();
            nbt.put("item", itemData);

            assert be != null;

            be.readNbt(nbt, world.getRegistryManager());
            world.addBlockEntity(be);
            if (!player.isCreative()) {
                player.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }

            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
