package com.chimericdream.stackitup.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.chimericdream.stackitup.util.ItemsHelper;

@Mixin(SolidBucketItem.class)
public class MixinPowderSnowBucketItem {

    @Redirect(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V"))
    private void stackableSnowBucket(Player instance, InteractionHand hand, ItemStack itemStack, UseOnContext context) {
        // >= 1 because it is decreased by 1 before our code execution
        if (ItemsHelper.isModified(context.getItemInHand()) && context.getItemInHand().getCount() >= 1) {
            ItemsHelper.insertNewItem(context.getPlayer(), new ItemStack(Items.BUCKET));
        } else {
            instance.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
        }
    }
}
