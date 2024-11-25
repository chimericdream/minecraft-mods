package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.ShulkerStuffMod;
import com.chimericdream.shulkerstuff.component.type.ContainerComponentBuilder;
import com.chimericdream.shulkerstuff.enchantment.ModEnchantments;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerInventory.class)
abstract public class ShulkerStuff$PlayerInventoryMixin {
    @Shadow
    @Final
    public PlayerEntity player;

    @Shadow
    @Final
    private List<DefaultedList<ItemStack>> combinedInventory;

    @Shadow
    abstract public boolean insertStack(int slot, ItemStack stack);

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void ssPlayerInventory$insertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }

        if (stack.isEmpty() || !stack.getItem().canBeNested()) {
            return;
        }

        RegistryWrapper<Enchantment> registry = this.player.getWorld()
            .getRegistryManager()
            .getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        ShulkerStuffMod.LOGGER.trace("Inserting {} of {}", stack.getCount(), stack.getName());

        int i = 0;
        for (DefaultedList<ItemStack> inventory : this.combinedInventory) {
            ShulkerStuffMod.LOGGER.trace("Checking inventory of size {} and index {}", inventory.size(), i++);

            int j = 0;
            for (ItemStack inventoryStack : inventory) {
                ShulkerStuffMod.LOGGER.trace("Checking slot {}", j++);
                if (!(inventoryStack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
                    continue;
                }

                ItemEnchantmentsComponent enchantments = inventoryStack.get(DataComponentTypes.ENCHANTMENTS);
                if (enchantments == null) {
                    continue;
                }

                ContainerComponent contents = inventoryStack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
                ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

                int vacuumLevel = enchantments.getLevel(registry.getOrThrow(ModEnchantments.VACUUM));
                int voidLevel = enchantments.getLevel(registry.getOrThrow(ModEnchantments.VOID));

                if (vacuumLevel > 0) {
                    builder.addStackForVacuum(stack, vacuumLevel);
                } else if (voidLevel > 0) {
                    builder.addStackForVoid(stack);
                }

                inventoryStack.set(DataComponentTypes.CONTAINER, builder.build());

                if (stack.isEmpty()) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        cir.setReturnValue(this.insertStack(-1, stack));
    }
}
