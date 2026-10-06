package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.ShulkerStuffMod;
import com.chimericdream.shulkerstuff.component.type.ContainerComponentBuilder;
import com.chimericdream.shulkerstuff.enchantment.ModEnchantments;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
abstract public class ShulkerStuff$PlayerInventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    @Shadow
    abstract public boolean add(int slot, ItemStack stack);

    @Unique
    private static final long SS$NOTICE_COOLDOWN_TICKS = 100L;

    @Unique
    private long ss$lastStackedVacuumNotice = Long.MIN_VALUE / 2;

    @Unique
    private Registry<Enchantment> ss$getEnchantmentRegistry(ServerPlayer player) {
        return player.level()
            .registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT);
    }

    @Unique
    private void ss$notifyStackedVacuum(ServerPlayer player) {
        long now = player.level().getGameTime();
        if (now - this.ss$lastStackedVacuumNotice < SS$NOTICE_COOLDOWN_TICKS && now >= this.ss$lastStackedVacuumNotice) {
            return;
        }

        this.ss$lastStackedVacuumNotice = now;
        player.sendSystemMessage(
            Component.translatable(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "message.vacuum_stacked").toLanguageKey()),
            true
        );
    }

    @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void ssPlayerInventory$add(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (stack.isEmpty() || !stack.getItem().canFitInsideContainerItems()) {
            return;
        }

        Registry<Enchantment> registry = ss$getEnchantmentRegistry(serverPlayer);

        ShulkerStuffMod.LOGGER.trace("Inserting {} of {}", stack.getCount(), stack.getDisplayName());

        int j = 0;
        for (ItemStack inventoryStack : this.items) {
            ShulkerStuffMod.LOGGER.trace("Checking slot {}", j++);
            if (!(inventoryStack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
                continue;
            }

            ItemEnchantments enchantments = inventoryStack.get(DataComponents.ENCHANTMENTS);
            if (enchantments == null) {
                continue;
            }

            ItemContainerContents contents = inventoryStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

            int vacuumLevel = enchantments.getLevel(registry.getOrThrow(ModEnchantments.VACUUM));
            int voidLevel = enchantments.getLevel(registry.getOrThrow(ModEnchantments.VOID));

            if (vacuumLevel > 0) {
                // Writing the container component to a stack of boxes would copy the contents to every box in it.
                if (inventoryStack.getCount() > 1) {
                    ss$notifyStackedVacuum(serverPlayer);
                    continue;
                }

                builder.addStackForVacuum(stack, vacuumLevel);
            } else if (voidLevel > 0) {
                builder.addStackForVoid(stack);
            }

            inventoryStack.set(DataComponents.CONTAINER, builder.build());

            if (stack.isEmpty()) {
                cir.setReturnValue(true);
                return;
            }
        }

        cir.setReturnValue(this.add(-1, stack));
    }
}
