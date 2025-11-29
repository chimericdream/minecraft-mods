package com.chimericdream.minekea.mixin;

import com.chimericdream.minekea.fluid.ModFluids;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MilkBucketItemMixin implements FluidModificationItem {
    @Inject(method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    public void mk$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = context.getStack();
        ConsumableComponent consumableComponent = stack.get(DataComponentTypes.CONSUMABLE);
        if (consumableComponent == null || !consumableComponent.equals(ConsumableComponents.MILK_BUCKET)) {
            return;
        }

        PlayerEntity player = context.getPlayer();

        if (player == null || player.isSneaking()) {
            return;
        }

        World world = player.getEntityWorld();

        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockPos offset = blockPos.offset(direction);

        if (world.canEntityModifyAt(player, blockPos) && player.canPlaceOn(offset, direction, stack)) {
            if (this.placeFluid(player, world, offset, null)) {
                this.onEmptied(player, world, stack, offset);

                if (player instanceof ServerPlayerEntity) {
                    Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) player, offset, stack);
                }

                cir.setReturnValue(ActionResult.SUCCESS);

                return;
            }
        }

        cir.setReturnValue(ActionResult.FAIL);
    }

    @Override
    public boolean placeFluid(@Nullable LivingEntity livingEntity, World world, BlockPos pos, @Nullable BlockHitResult hit) {
        if (!(livingEntity instanceof PlayerEntity player)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        boolean canBucketPlace = state.canBucketPlace(ModFluids.MILK_FLUID.get());
        boolean preventBucketPlace = state.isAir()
            || canBucketPlace
            || block instanceof FluidFillable && ((FluidFillable) block).canFillWithFluid(player, world, pos, state, ModFluids.MILK_FLUID.get());

        if (!preventBucketPlace) {
            return hit != null && this.placeFluid(player, world, hit.getBlockPos().offset(hit.getSide()), null);
        } else if (world.getDimension().ultrawarm()) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();

            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

            for (int l = 0; l < 8; ++l) {
                world.addImportantParticleClient(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0, 0.0, 0.0);
            }

            return true;
        } else if (block instanceof FluidFillable) {
            ((FluidFillable) block).tryFillWithFluid(world, pos, state, ModFluids.MILK_FLUID.get().getStill(false));

            this.mk$playEmptyingSound(player, world, pos);

            return true;
        } else {
            if (!world.isClient() && canBucketPlace) {
                world.breakBlock(pos, true);
            }

            if (!world.setBlockState(pos, ModFluids.MILK_FLUID.get().getDefaultState().getBlockState(), 11) && !state.getFluidState().isStill()) {
                return false;
            } else {
                this.mk$playEmptyingSound(player, world, pos);

                return true;
            }
        }
    }

    @Unique
    protected void mk$playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(player, GameEvent.FLUID_PLACE, pos);
    }
}
