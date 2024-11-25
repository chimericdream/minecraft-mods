package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.component.type.ContainerComponentBuilder;
import com.chimericdream.shulkerstuff.enchantment.ModEnchantments;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
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
@Mixin(ServerPlayerEntity.class)
public class ShulkerStuff$ServerPlayerEntityMixin {
    @Unique
    private int ss$previousSlot = -1;

    @Unique
    private ItemStack ss$previousMainHandStack = ItemStack.EMPTY;

    @Unique
    private ItemStack ss$previousOffHandStack = ItemStack.EMPTY;

    @Unique
    private boolean ss$hasOpenScreen = false;

    @Inject(method = "playerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;tick()V"))
    @Unique
    private void ss$playerTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        PlayerInventory inventory = player.getInventory();

        int currentSlot = inventory.selectedSlot;
        ItemStack currentMainStack = inventory.getMainHandStack();
        ItemStack currentOffStack = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);

        boolean didChangeSlots = currentSlot != this.ss$previousSlot;
        boolean didSwapHands = ItemStack.areEqual(this.ss$previousMainHandStack, currentOffStack) && ItemStack.areEqual(this.ss$previousOffHandStack, currentMainStack);
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
                ss$refillSlot(PlayerInventory.OFF_HAND_SLOT);
            }
        }

        this.ss$previousSlot = currentSlot;
        this.ss$previousMainHandStack = currentMainStack.copy();
        this.ss$previousOffHandStack = currentOffStack.copy();
    }

    @Inject(method = "openHandledScreen", at = @At("TAIL"))
    @Unique
    private void ss$openHandledScreen(CallbackInfoReturnable<OptionalInt> cir) {
        OptionalInt result = cir.getReturnValue();

        if (result.isPresent()) {
            this.ss$hasOpenScreen = true;
        }
    }

    @Inject(method = "closeHandledScreen", at = @At("TAIL"))
    @Unique
    private void ss$closeHandledScreen(CallbackInfo ci) {
        this.ss$hasOpenScreen = false;
    }

    @Unique
    private RegistryWrapper<Enchantment> ss$getEnchantmentRegistry() {
        return ((ServerPlayerEntity) (Object) this).getServerWorld()
            .getRegistryManager()
            .getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
    }

    @Unique
    private void ss$refillSlot(int slot) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        PlayerInventory inventory = player.getInventory();
        RegistryWrapper<Enchantment> registry = this.ss$getEnchantmentRegistry();

        for (ItemStack stack : inventory.main) {
            if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
                continue;
            }

            ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.ENCHANTMENTS);
            if (enchantments == null || enchantments.getLevel(registry.getOrThrow(ModEnchantments.REFILLING)) == 0) {
                continue;
            }

            ContainerComponent contents = stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
            ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

            ItemStack refillingStack = builder.getFirstMatchingStack(this.ss$previousMainHandStack);

            if (!refillingStack.isEmpty()) {
                inventory.setStack(slot, refillingStack);
                stack.set(DataComponentTypes.CONTAINER, builder.build());

                return;
            }
        }
    }
}
