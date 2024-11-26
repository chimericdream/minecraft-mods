package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.ShulkerStuffMod;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffHardenedComponent;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffPlatedComponent;
import com.chimericdream.shulkerstuff.enchantment.ModEnchantments;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
abstract public class ShulkerStuff$BlockEntityMixin {
    protected DefaultedList<ItemStack> extraInventory = DefaultedList.ofSize(0);

    @Shadow
    private ComponentMap components;

    @Unique
    protected ComponentMap ss$getComponents() {
        return components;
    }

    @Inject(method = "readComponents(Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void ss$readExtraInventoryNbt(ItemStack stack, CallbackInfo ci) {
        if (!(((BlockEntity) (Object) this) instanceof ShulkerBoxBlockEntity)) {
            return;
        }

        ShulkerStuffMod.LOGGER.info("Reading extra inventory");

        RegistryWrapper<Enchantment> enchantmentRegistry = ss$getEnchantmentRegistry();

        if (enchantmentRegistry == null) {
            return;
        }

        ComponentMap components = stack.getComponents();
        ItemEnchantmentsComponent enchantments = components.get(DataComponentTypes.ENCHANTMENTS);

        if (enchantments == null || enchantments.getLevel(enchantmentRegistry.getOrThrow(ModEnchantments.DEEP_STORAGE)) == 0) {
            return;
        }

        extraInventory = DefaultedList.ofSize(9 * enchantments.getLevel(enchantmentRegistry.getOrThrow(ModEnchantments.DEEP_STORAGE)), ItemStack.EMPTY);

//        extraInventory = DefaultedList.ofSize(inventory.size() - 27, ItemStack.EMPTY);
//        for (int i = 0; i < extraInventory.size(); i++) {
//            extraInventory.set(i, inventory.get(i + 27));
//        }
    }

    @Inject(method = "toInitialChunkDataNbt", at = @At("TAIL"), cancellable = true)
    private void ss$toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup, CallbackInfoReturnable<NbtCompound> cir) {
        if (!(((BlockEntity) (Object) this) instanceof ShulkerBoxBlockEntity)) {
            return;
        }

        NbtCompound nbt = cir.getReturnValue();
        NbtCompound components = nbt.getCompound("components");

        ShulkerBoxBlockEntity self = (ShulkerBoxBlockEntity) (Object) this;

        ShulkerStuffDyedColorComponent ssDyedColorComponent = self.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get());
        if (ssDyedColorComponent != null) {
            components.put(ShulkerStuffDyedColorComponent.COMPONENT_ID.toString(), ssDyedColorComponent.toNbt());
        }

        ShulkerStuffHardenedComponent ssHardenedComponent = self.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_HARDENED_COMPONENT.get());
        if (ssHardenedComponent != null) {
            components.put(ShulkerStuffHardenedComponent.COMPONENT_ID.toString(), ssHardenedComponent.toNbt());
        }

        ShulkerStuffPlatedComponent ssPlatedComponent = self.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_PLATED_COMPONENT.get());
        if (ssPlatedComponent != null) {
            components.put(ShulkerStuffPlatedComponent.COMPONENT_ID.toString(), ssPlatedComponent.toNbt());
        }

        nbt.put("components", components);

        cir.setReturnValue(nbt);
    }

    @Unique
    protected @Nullable RegistryWrapper<Enchantment> ss$getEnchantmentRegistry() {
        World world = ((BlockEntity) (Object) this).getWorld();
        if (world == null) {
            return null;
        }

        return world.getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
    }
}
