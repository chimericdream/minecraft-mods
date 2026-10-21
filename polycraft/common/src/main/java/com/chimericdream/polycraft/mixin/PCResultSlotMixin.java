package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.recipe.ConflictService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

/**
 * Taking the crafting result works out which items stay behind in the grid (buckets, bottles) by looking the recipe
 * up again, and vanilla's lookup there ignores any choice. Without this, the leftovers would come from the wrong recipe.
 */
@Mixin(ResultSlot.class)
public class PCResultSlotMixin {
    @Shadow
    @Final
    private Player player;

    @WrapOperation(
        method = "getRemainingItems",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
        )
    )
    private Optional<RecipeHolder<CraftingRecipe>> polycraft$selectRecipeForRemainders(
        RecipeManager manager,
        RecipeType<CraftingRecipe> type,
        RecipeInput input,
        Level level,
        Operation<Optional<RecipeHolder<CraftingRecipe>>> original
    ) {
        Optional<RecipeHolder<CraftingRecipe>> vanilla = original.call(manager, type, input, level);
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return vanilla;
        }

        return ConflictService
            .select(serverLevel, type, (CraftingInput) input, vanilla.orElse(null), PolyCraftNetworking.choicesFor(serverPlayer), null)
            .map(ConflictService.Selection::chosen)
            .or(() -> vanilla);
    }
}
