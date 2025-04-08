package com.chimericdream.archaeologytweaks.mixin;

import com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushItem.class)
abstract public class BrushItemMixin {
    @Shadow
    abstract public HitResult getHitResult(PlayerEntity user);

    @Shadow
    abstract public int getMaxUseTime(ItemStack stack, LivingEntity user);

    @Inject(
        method = "usageTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;I)V",
        at = @At(value = "HEAD")
    )
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci) {
        if (remainingUseTicks >= 0 && user instanceof PlayerEntity playerEntity) {
            HitResult hitResult = this.getHitResult(playerEntity);
            if (hitResult instanceof BlockHitResult blockHitResult) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    int i = this.getMaxUseTime(stack, user) - remainingUseTicks + 1;
                    boolean bl = i % 10 == 5;
                    if (bl) {
                        BlockPos blockPos = blockHitResult.getBlockPos();
                        if (world instanceof ServerWorld serverWorld) {
                            BlockEntity blockEntity = world.getBlockEntity(blockPos);

                            if (blockEntity instanceof ATBrushableBlockEntity brushableBlockEntity) {
                                boolean finishedBrushing = brushableBlockEntity.brush(
                                    world.getTime(),
                                    serverWorld,
                                    playerEntity,
                                    blockHitResult.getSide(),
                                    stack
                                );

                                if (finishedBrushing) {
                                    EquipmentSlot equipmentSlot = stack.equals(playerEntity.getEquippedStack(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                                    stack.damage(1, playerEntity, equipmentSlot);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
