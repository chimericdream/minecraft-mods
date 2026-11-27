package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.config.SyncedConfig;
import com.chimericdream.enchantingfixes.enchantment.AnvilUpgrades;
import com.chimericdream.enchantingfixes.enchantment.XpCosts;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Removes the "Too Expensive!" limit. {@code createResult} empties the result when the cost is 40 or more
 * and the player is not in creative mode. This mixin treats that check as if the player were in creative.
 *
 * <p>Also applies {@link AnvilUpgrades} while the linear upgrades setting is on, and {@link XpCosts} while the
 * fixed XP costs setting is on.
 *
 * <p>NeoForge moves the body of {@code createResult} to {@code createResultInternal}, so each injector targets
 * both methods. NeoForge's {@code createResult} calls none of the targets.
 */
@Mixin(AnvilMenu.class)
public abstract class EFAnvilMenuMixin {
    /**
     * The cost limit is the first creative check after the third constant 40 ({@code cost >= 40}). Before it,
     * vanilla has another creative check that lets incompatible enchantments combine. NeoForge changes that check,
     * so an ordinal of the call alone is different on each loader.
     *
     * <p>Vanilla sets the price to 40 when the first input is a stack of more than one item, so that the limit
     * stops the anvil from enchanting the whole stack. Stacks keep the limit.
     */
    @ModifyExpressionValue(
        method = {"createResult()V", "createResultInternal()V"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasInfiniteMaterials()Z", ordinal = 0),
        slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=40", ordinal = 2))
    )
    private boolean enchantingfixes$removeCostLimit(boolean original) {
        if (original || !SyncedConfig.effective().removeAnvilLimit) {
            return original;
        }

        return ((AnvilMenu) (Object) this).getSlot(0).getItem().getCount() <= 1;
    }

    /**
     * The first two {@code getOrDefault} calls read the repair cost of the two inputs for the price. Items from
     * before the setting can have a higher repair cost than the cap.
     */
    @ModifyExpressionValue(
        method = {"createResult()V", "createResultInternal()V"},
        at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0),
            @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1)
        }
    )
    private Object enchantingfixes$capInputRepairCost(Object original) {
        if (original instanceof Integer cost && SyncedConfig.effective().linearAnvilUpgrades) {
            return AnvilUpgrades.repairCost(cost);
        }

        return original;
    }

    @ModifyReturnValue(method = "calculateIncreasedRepairCost(I)I", at = @At("RETURN"))
    private static int enchantingfixes$capRepairCost(int original) {
        if (!SyncedConfig.effective().linearAnvilUpgrades) {
            return original;
        }

        return AnvilUpgrades.repairCost(original);
    }

    /**
     * Ordinal 0 is {@code Math.max(level, current)}: the level when the two inputs have different levels of an
     * enchantment. When the levels are the same, vanilla adds one level without this call.
     */
    @WrapOperation(
        method = {"createResult()V", "createResultInternal()V"},
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I", ordinal = 0)
    )
    private int enchantingfixes$linearUpgrade(int level, int current, Operation<Integer> original, @Local Holder<Enchantment> enchantment) {
        int higher = original.call(level, current);
        if (!SyncedConfig.effective().linearAnvilUpgrades) {
            return higher;
        }

        return AnvilUpgrades.combinedLevel(higher, Math.min(level, current), enchantment.value().definition().maxLevel());
    }

    /** The cost when the player takes the result. With the fixed XP costs setting, it takes XP points, not levels. */
    @WrapOperation(
        method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;giveExperienceLevels(I)V")
    )
    private void enchantingfixes$payInXpPoints(Player player, int amount, Operation<Void> original) {
        if (amount >= 0 || !SyncedConfig.effective().fixedXpCosts) {
            original.call(player, amount);
            return;
        }

        XpCosts.pay(player, -amount);
    }
}
