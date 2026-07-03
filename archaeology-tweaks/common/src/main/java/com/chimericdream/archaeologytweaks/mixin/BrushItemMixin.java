package com.chimericdream.archaeologytweaks.mixin;

import com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushItem.class)
abstract public class BrushItemMixin {
    @Shadow
    abstract public HitResult calculateHitResult(Player user);

    @Shadow
    abstract public int getUseDuration(ItemStack stack, LivingEntity user);

    @Inject(
        method = "onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V",
        at = @At(value = "HEAD")
    )
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci) {
        if (remainingUseTicks >= 0 && user instanceof Player playerEntity) {
            HitResult hitResult = this.calculateHitResult(playerEntity);
            if (hitResult instanceof BlockHitResult blockHitResult) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    int i = this.getUseDuration(stack, user) - remainingUseTicks + 1;
                    boolean bl = i % 10 == 5;
                    if (bl) {
                        BlockPos blockPos = blockHitResult.getBlockPos();
                        if (world instanceof ServerLevel serverWorld) {
                            BlockEntity blockEntity = world.getBlockEntity(blockPos);

                            if (blockEntity instanceof ATBrushableBlockEntity brushableBlockEntity) {
                                boolean finishedBrushing = brushableBlockEntity.brush(
                                    world.getGameTime(),
                                    serverWorld,
                                    playerEntity,
                                    blockHitResult.getDirection(),
                                    stack
                                );

                                if (finishedBrushing) {
                                    EquipmentSlot equipmentSlot = stack.equals(playerEntity.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                                    stack.hurtAndBreak(1, playerEntity, equipmentSlot);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
