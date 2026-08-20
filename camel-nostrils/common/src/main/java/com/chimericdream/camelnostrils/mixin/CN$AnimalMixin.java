package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class CN$AnimalMixin {
    private static final float GOLDEN_EGG_CHANCE = 0.15F;

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void cn$feedGoldenWheatSeeds(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Animal self = (Animal) (Object) this;

        if (!(self instanceof Chicken chicken)) {
            return;
        }

        ItemStack itemStack = player.getItemInHand(hand);

        if (!itemStack.is(ModItems.GOLDEN_WHEAT_SEEDS.get())) {
            return;
        }

        if (chicken.isBaby()) {
            if (!chicken.level().isClientSide()) {
                itemStack.consume(1, player);
                chicken.setAge(0);
            }

            cir.setReturnValue(InteractionResult.SUCCESS);
            cir.cancel();
        } else if (chicken.level() instanceof ServerLevel serverLevel && chicken.getRandom().nextFloat() < GOLDEN_EGG_CHANCE) {
            chicken.spawnAtLocation(serverLevel, new ItemStack(ModItems.GOLDEN_EGG.get()));
            chicken.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (chicken.getRandom().nextFloat() - chicken.getRandom().nextFloat()) * 0.2F + 1.0F);
            chicken.gameEvent(GameEvent.ENTITY_PLACE);
        }
    }
}
