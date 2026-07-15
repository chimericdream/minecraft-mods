package com.chimericdream.minekea.mixin;

import com.chimericdream.minekea.fluid.ModFluids;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MilkBucketItemMixin implements DispensibleContainerItem {
    @Inject(method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
    public void mk$useOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = context.getItemInHand();
        Consumable consumableComponent = stack.get(DataComponents.CONSUMABLE);
        if (consumableComponent == null || !consumableComponent.equals(Consumables.MILK_BUCKET)) {
            return;
        }

        Player player = context.getPlayer();

        if (player == null || player.isShiftKeyDown()) {
            return;
        }

        Level world = player.level();

        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos offset = blockPos.relative(direction);

        if (world.mayInteract(player, blockPos) && player.mayUseItemAt(offset, direction, stack)) {
            if (this.emptyContents(player, world, offset, null)) {
                this.checkExtraContent(player, world, stack, offset);

                if (player instanceof ServerPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, offset, stack);
                }

                cir.setReturnValue(InteractionResult.SUCCESS);

                return;
            }
        }

        cir.setReturnValue(InteractionResult.FAIL);
    }

    @Override
    public boolean emptyContents(@Nullable LivingEntity livingEntity, Level world, BlockPos pos, @Nullable BlockHitResult hit) {
        if (!(livingEntity instanceof Player player)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        boolean canBucketPlace = state.canBeReplaced(ModFluids.MILK_FLUID.get());
        boolean preventBucketPlace = state.isAir()
            || canBucketPlace
            || block instanceof LiquidBlockContainer && ((LiquidBlockContainer) block).canPlaceLiquid(player, world, pos, state, ModFluids.MILK_FLUID.get());

        if (!preventBucketPlace) {
            return hit != null && this.emptyContents(player, world, hit.getBlockPos().relative(hit.getDirection()), null);
        } else if (world.dimensionType().attributes().contains(EnvironmentAttributes.WATER_EVAPORATES)) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();

            world.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.8F);

            for (int l = 0; l < 8; ++l) {
                world.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0, 0.0, 0.0);
            }

            return true;
        } else if (block instanceof LiquidBlockContainer) {
            ((LiquidBlockContainer) block).placeLiquid(world, pos, state, ModFluids.MILK_FLUID.get().getSource(false));

            this.mk$playEmptyingSound(player, world, pos);

            return true;
        } else {
            if (!world.isClientSide() && canBucketPlace) {
                world.destroyBlock(pos, true);
            }

            if (!world.setBlock(pos, ModFluids.MILK_FLUID.get().defaultFluidState().createLegacyBlock(), 11) && !state.getFluidState().isSource()) {
                return false;
            } else {
                this.mk$playEmptyingSound(player, world, pos);

                return true;
            }
        }
    }

    @Unique
    protected void mk$playEmptyingSound(@Nullable Player player, LevelAccessor world, BlockPos pos) {
        world.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        world.gameEvent(player, GameEvent.FLUID_PLACE, pos);
    }
}
