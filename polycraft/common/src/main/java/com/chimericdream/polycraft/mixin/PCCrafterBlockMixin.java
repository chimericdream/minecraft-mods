package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.PolyCraftMod;
import com.chimericdream.polycraft.recipe.ConflictService;
import com.chimericdream.polycraft.recipe.StationResolvers;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

/** When a Crafter crafts: the pin, then whoever has it open, then the pack default, then vanilla. */
@Mixin(CrafterBlock.class)
public class PCCrafterBlockMixin {
    @WrapOperation(
        method = "dispenseFrom",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/CrafterBlock;getPotentialResults(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/crafting/CraftingInput;)Ljava/util/Optional;"
        )
    )
    private Optional<RecipeHolder<CraftingRecipe>> polycraft$selectRecipe(
        ServerLevel level,
        CraftingInput input,
        Operation<Optional<RecipeHolder<CraftingRecipe>>> original,
        @Local CrafterBlockEntity crafter
    ) {
        Optional<RecipeHolder<CraftingRecipe>> vanilla = original.call(level, input);
        try {
            return StationResolvers.crafter(level, crafter, input, vanilla, StationResolvers.crafterChoices(level, crafter))
                .map(ConflictService.Selection::chosen)
                .or(() -> vanilla);
        } catch (RuntimeException e) {
            PolyCraftMod.LOGGER.error("PolyCraft couldn't pick a Crafter recipe; using vanilla's", e);
            return vanilla;
        }
    }
}
