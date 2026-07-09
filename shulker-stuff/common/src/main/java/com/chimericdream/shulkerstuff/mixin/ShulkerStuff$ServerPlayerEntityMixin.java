package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.component.type.ContainerComponentBuilder;
import com.chimericdream.shulkerstuff.enchantment.ModEnchantments;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

/**
 * Many thanks and credit to the author of the Enchanted Shulkers mod, from whose code this is derived.
 *
 * @link <a href="https://modrinth.com/mod/enchantedshulkers">Modrinth page</a>
 */
@Mixin(ServerPlayer.class)
public class ShulkerStuff$ServerPlayerEntityMixin {
    @Unique
    private int ss$previousSlot = -1;

    @Unique
    private ItemStack ss$previousMainHandStack = ItemStack.EMPTY;

    @Unique
    private ItemStack ss$previousOffHandStack = ItemStack.EMPTY;

    @Unique
    private boolean ss$hasOpenScreen = false;

    @Inject(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;tick()V"))
    @Unique
    private void ss$playerTick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        Inventory inventory = player.getInventory();

        int currentSlot = inventory.getSelectedSlot();
        ItemStack currentMainStack = player.getMainHandItem();
        ItemStack currentOffStack = player.getOffhandItem();

        boolean didChangeSlots = currentSlot != this.ss$previousSlot;
        boolean didSwapHands = ItemStack.isSameItem(this.ss$previousMainHandStack, currentOffStack) && ItemStack.isSameItem(this.ss$previousOffHandStack, currentMainStack);
        boolean usedLastMainHandItem = !this.ss$previousMainHandStack.isEmpty() && currentMainStack.isEmpty() && !didSwapHands;
        boolean usedLastOffHandItem = !this.ss$previousOffHandStack.isEmpty() && currentOffStack.isEmpty() && !didSwapHands;

        boolean canRefill = !(
            this.ss$hasOpenScreen
                || player.isCreative()
                || didChangeSlots
                || didSwapHands
        );

        if (canRefill) {
            if (usedLastMainHandItem) {
                ss$refillSlot(currentSlot);
            } else if (usedLastOffHandItem) {
                ss$refillSlot(EquipmentSlot.OFFHAND.getId()); // might also be getIndex()... check this
            }
        }

        this.ss$previousSlot = currentSlot;
        this.ss$previousMainHandStack = currentMainStack.copy();
        this.ss$previousOffHandStack = currentOffStack.copy();
    }

    @Inject(method = "openMenu", at = @At("TAIL"))
    @Unique
    private void ss$openHandledScreen(CallbackInfoReturnable<OptionalInt> cir) {
        OptionalInt result = cir.getReturnValue();

        if (result.isPresent()) {
            this.ss$hasOpenScreen = true;
        }
    }

    @Inject(method = "closeContainer", at = @At("TAIL"))
    @Unique
    private void ss$closeHandledScreen(CallbackInfo ci) {
        this.ss$hasOpenScreen = false;
    }

    @Unique
    private Registry<Enchantment> ss$getEnchantmentRegistry() {
        return ((ServerPlayer) (Object) this).level()
            .registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT);
    }

    @Unique
    private void ss$refillSlot(int slot) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        Inventory inventory = player.getInventory();
        Registry<Enchantment> registry = this.ss$getEnchantmentRegistry();

        for (ItemStack stack : inventory.getNonEquipmentItems()) {
            if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
                continue;
            }

            ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
            if (enchantments == null || enchantments.getLevel(registry.getOrThrow(ModEnchantments.REFILLING)) == 0) {
                continue;
            }

            ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

            ItemStack refillingStack = builder.getFirstMatchingStack(this.ss$previousMainHandStack);

            if (!refillingStack.isEmpty()) {
                inventory.setItem(slot, refillingStack);
                stack.set(DataComponents.CONTAINER, builder.build());

                return;
            }
        }
    }
}
