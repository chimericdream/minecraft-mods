package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.PolyCraftMod;
import com.chimericdream.polycraft.recipe.StationResolvers;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

/**
 * Campfires have no screen, so nobody chooses for them: they use the pack's default for a conflicting food if there is
 * one, and vanilla's pick otherwise. Both places a campfire looks up a recipe (when food goes on, for the cooking time,
 * and when it finishes, for the result) must agree, so both are wrapped the same way.
 */
@Mixin(CampfireBlockEntity.class)
public class PCCampfireBlockEntityMixin {
    private static final String CACHED_CHECK_LOOKUP =
        "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/server/level/ServerLevel;)Ljava/util/Optional;";

    @WrapOperation(method = "cookTick", at = @At(value = "INVOKE", target = CACHED_CHECK_LOOKUP))
    private static Optional<RecipeHolder<CampfireCookingRecipe>> polycraft$resolveWhenDone(
        RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> check,
        RecipeInput input,
        ServerLevel level,
        Operation<Optional<RecipeHolder<CampfireCookingRecipe>>> original
    ) {
        return resolve(level, (SingleRecipeInput) input, original.call(check, input, level));
    }

    @WrapOperation(
        method = "placeFood",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
        )
    )
    private Optional<RecipeHolder<CampfireCookingRecipe>> polycraft$resolveWhenPlaced(
        RecipeManager manager,
        RecipeType<CampfireCookingRecipe> type,
        RecipeInput input,
        Level level,
        Operation<Optional<RecipeHolder<CampfireCookingRecipe>>> original
    ) {
        Optional<RecipeHolder<CampfireCookingRecipe>> vanilla = original.call(manager, type, input, level);
        return level instanceof ServerLevel serverLevel ? resolve(serverLevel, (SingleRecipeInput) input, vanilla) : vanilla;
    }

    private static Optional<RecipeHolder<CampfireCookingRecipe>> resolve(
        ServerLevel level,
        SingleRecipeInput input,
        Optional<RecipeHolder<CampfireCookingRecipe>> vanilla
    ) {
        try {
            return StationResolvers.campfire(level, input, vanilla);
        } catch (RuntimeException e) {
            PolyCraftMod.LOGGER.error("PolyCraft couldn't pick a campfire recipe; using vanilla's", e);
            return vanilla;
        }
    }
}
