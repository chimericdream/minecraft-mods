package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.ShulkerStuffMod;
import com.chimericdream.shulkerstuff.component.type.ContainerComponentBuilder;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffHardenedComponent;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffPlatedComponent;
import com.chimericdream.shulkerstuff.tag.ShulkerStuffItemTags;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Item.class)
public class ShulkerStuff$ItemMixin {
    @Inject(method = "appendTooltip", at = @At("TAIL"))
    private void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo cir) {
        if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }

        ShulkerStuffHardenedComponent ssHardenedComponent = stack.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_HARDENED_COMPONENT.get());
        if (ssHardenedComponent != null && ssHardenedComponent.value()) {
            MutableText text = Text.translatable(Identifier.of(ModInfo.MOD_ID, "tooltip.upgrades.hardened").toTranslationKey());
            tooltip.add(text.formatted(Formatting.DARK_PURPLE));
        }

        ShulkerStuffPlatedComponent ssPlatedComponent = stack.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_PLATED_COMPONENT.get());
        if (ssPlatedComponent != null && ssPlatedComponent.value()) {
            MutableText text = Text.translatable(Identifier.of(ModInfo.MOD_ID, "tooltip.upgrades.plated").toTranslationKey());
            tooltip.add(text.formatted(Formatting.DARK_GRAY));
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = player.getStackInHand(hand);

        if (!player.isSneaking() || !(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }

        if (this.ssItem$dropFirstItem(stack, player)) {
            cir.setReturnValue(TypedActionResult.success(stack, world.isClient()));
        } else {
            cir.setReturnValue(TypedActionResult.fail(stack));
        }
    }

    @Inject(method = "onStackClicked", at = @At("HEAD"), cancellable = true)
    private void onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
                return;
            }

            if (clickType != ClickType.RIGHT) {
                cir.setReturnValue(false);

                return;
            }

            ContainerComponent contents = stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
            ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

            ItemStack itemStack = slot.getStack();
            if (itemStack.isEmpty()) {
                ItemStack itemStack2 = builder.removeFirst();
                if (!itemStack2.isEmpty()) {
                    ItemStack itemStack3 = slot.insertStack(itemStack2);
                    builder.addStack(itemStack3);
                    this.ssItem$playRemoveOneSound(player);
                }
            } else if (itemStack.getItem().canBeNested()) {
                int i = builder.addFromSlot(slot, player);
                if (i > 0) {
                    this.ssItem$playInsertSound(player);
                }
            }

            stack.set(DataComponentTypes.CONTAINER, builder.build());
            cir.setReturnValue(true);
        } catch (Exception e) {
            ShulkerStuffMod.LOGGER.error("An error occurred while processing a shulker box item click.", e);
        }
    }

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private void onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (!(stack.getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }

        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) {
            return;
        }

        ContainerComponent contents = stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
        ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

        if (otherStack.isEmpty()) {
            ItemStack itemStack = builder.removeFirst();
            if (itemStack != null) {
                this.ssItem$playRemoveOneSound(player);
                cursorStackReference.set(itemStack);
            }
        } else {
            int startingCount = otherStack.getCount();
            builder.addStack(otherStack);
            if (otherStack.getCount() != startingCount) {
                this.ssItem$playInsertSound(player);
            }
            cursorStackReference.set(otherStack);
        }

        stack.set(DataComponentTypes.CONTAINER, builder.build());
        cir.setReturnValue(true);
    }

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void isEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isIn(ShulkerStuffItemTags.SHULKER_BOX_ITEMS)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getEnchantability", at = @At("HEAD"), cancellable = true)
    private void getEnchantability(CallbackInfoReturnable<Integer> cir) {
        //noinspection ConstantValue
        if (!((Object) this instanceof BlockItem bi) || !(bi.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }

        cir.setReturnValue(20);
    }

    @Unique
    private void ssItem$playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ENTITY_SHULKER_HURT_CLOSED, 0.2F, 0.95F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    @Unique
    private void ssItem$playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 0.2F, 0.95F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    @Unique
    private boolean ssItem$dropFirstItem(ItemStack stack, PlayerEntity player) {
        ContainerComponent contents = stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
        ContainerComponentBuilder builder = new ContainerComponentBuilder(contents);

        ItemStack stack2 = builder.removeFirst();
        if (stack2.isEmpty()) {
            return false;
        }

        if (player instanceof ServerPlayerEntity) {
            player.dropItem(stack2, true);
        }

        this.ssItem$playRemoveOneSound(player);

        stack.set(DataComponentTypes.CONTAINER, builder.build());

        return true;
    }
}
