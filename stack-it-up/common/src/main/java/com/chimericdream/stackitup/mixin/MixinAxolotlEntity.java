package com.chimericdream.stackitup.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chimericdream.stackitup.util.ItemsHelper;

@Mixin(Axolotl.class)
public class MixinAxolotlEntity {
    @Inject(method = "usePlayerItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V"), cancellable = true)
    protected void reduceBucketsByOne(Player player, InteractionHand hand, ItemStack stack, CallbackInfo ci) {
        if (ItemsHelper.isModified(stack) && stack.getCount() > 1) {
            if (player.getAbilities().instabuild) {
                // this is not the vanilla behavior,
                // but I just has no idea how to decrease the stack count by 1 in creative mode -
                // it simply won't change for some reason that I haven't looked into
                // so, I decide to keep your inventory unchanged, lol.
                ;
            } else {
                ItemsHelper.insertNewItem(player, new ItemStack(Items.WATER_BUCKET));
                stack.shrink(1);
            }
            ci.cancel();
        }
    }
}
