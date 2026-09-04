package com.chimericdream.stackitup.mixin;

import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chimericdream.stackitup.util.ItemsHelper;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerPlayNetworkHandler {
    @Shadow
    public ServerPlayer player;

    /**
     *
     * Set the size of the new signed books to the size of the old writable books
     * instead of only making one new signed book regardless of the stack size.
     * This prevents unexpected item deletion.
     *
     **/
    @ModifyVariable(method = "signBook", at = @At("STORE"), ordinal = 1)
    public ItemStack fixSignedBookCount(ItemStack itemStack2, FilteredText title, List<FilteredText> pages, int slotId) {
        ItemStack originalStack = player.getInventory().getItem(slotId);
        if (ItemsHelper.isModified(originalStack)) {
            itemStack2.setCount(originalStack.getCount());
        }
        return itemStack2;
    }

    /**
     *
     * Split the written book into several stacks if it is over its maximum
     * stack count. This will occur whenever the writable book stack count is
     * greater than the written book's maximum stack count.
     *
     **/
    @Inject(method = "signBook", at = @At("RETURN"))
    public void fixSignedBookOverCount(FilteredText title, List<FilteredText> pages, int slotId, CallbackInfo ci) {
        ItemStack itemStack2 = player.getInventory().getItem(slotId);
        if (ItemsHelper.isModified(itemStack2) && (itemStack2.getCount() > itemStack2.getMaxStackSize())) {
            ItemStack splitStack = itemStack2.copy();
            int count = itemStack2.getCount() % itemStack2.getMaxStackSize();
            splitStack.setCount(count);
            itemStack2.shrink(count);
            ItemsHelper.insertNewItem(player, splitStack);
            while (itemStack2.getCount() > itemStack2.getMaxStackSize()) {
                splitStack = itemStack2.copy();
                count = itemStack2.getMaxStackSize();
                splitStack.setCount(count);
                itemStack2.shrink(count);
                ItemsHelper.insertNewItem(player, splitStack);
            }
        }
    }
}
