package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.lib.inventories.InventoryUtils;
import com.chimericdream.shulkerstuff.ShulkerStuffMod;
import com.chimericdream.shulkerstuff.block.ModBlocks;
import com.chimericdream.shulkerstuff.client.screen.DeepShulkerBoxScreenHandler;
import com.chimericdream.shulkerstuff.enchantment.ModEnchantments;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.IntStream;

@Mixin(ShulkerBoxBlockEntity.class)
abstract public class ShulkerStuff$ShulkerBoxBlockEntityMixin extends ShulkerStuff$BlockEntityMixin {
    @Inject(method = "getAvailableSlots", at = @At("HEAD"), cancellable = true)
    private void ss$getAvailableSlots(Direction dir, CallbackInfoReturnable<int[]> cir) {
        ShulkerStuffMod.LOGGER.info("Getting available slots");

        RegistryWrapper<Enchantment> enchantmentRegistry = ss$getEnchantmentRegistry();

        if (enchantmentRegistry == null) {
            return;
        }

        ComponentMap components = ss$getComponents();
        ItemEnchantmentsComponent enchantments = components.get(DataComponentTypes.ENCHANTMENTS);
        if (enchantments == null || enchantments.getLevel(enchantmentRegistry.getOrThrow(ModEnchantments.DEEP_STORAGE)) == 0) {
            return;
        }

        cir.setReturnValue(IntStream.range(0, 27 + 9 * enchantments.getLevel(enchantmentRegistry.getOrThrow(ModEnchantments.DEEP_STORAGE))).toArray());
    }

    @Inject(method = "createScreenHandler", at = @At("HEAD"), cancellable = true)
    private void ss$createScreenHandler(int syncId, PlayerInventory playerInventory, CallbackInfoReturnable<ScreenHandler> cir) {
        ShulkerStuffMod.LOGGER.info("Creating screen handler");

        RegistryWrapper<Enchantment> enchantmentRegistry = ss$getEnchantmentRegistry();

        if (enchantmentRegistry == null) {
            return;
        }

        ComponentMap components = ss$getComponents();
        ItemEnchantmentsComponent enchantments = components.get(DataComponentTypes.ENCHANTMENTS);
        if (enchantments == null || enchantments.getLevel(enchantmentRegistry.getOrThrow(ModEnchantments.DEEP_STORAGE)) == 0) {
            return;
        }

        cir.setReturnValue(new DeepShulkerBoxScreenHandler(
            ModBlocks.DEEP_SHULKER_SCREEN_HANDLER.get(),
            syncId,
            playerInventory,
            new DoubleInventory((ShulkerBoxBlockEntity) (Object) this, InventoryUtils.convertListToInventory(this.extraInventory)),
            enchantments.getLevel(enchantmentRegistry.getOrThrow(ModEnchantments.DEEP_STORAGE))
        ));
    }
}
