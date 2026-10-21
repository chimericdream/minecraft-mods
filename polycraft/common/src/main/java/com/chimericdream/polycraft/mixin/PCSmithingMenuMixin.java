package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.recipe.ConflictService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

/** The smithing table's recipe lookup: PolyCraft may swap in the recipe the player chose and tells their client the choices. */
@Mixin(SmithingMenu.class)
public class PCSmithingMenuMixin {
    @WrapOperation(
        method = "createResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
        )
    )
    private Optional<RecipeHolder<SmithingRecipe>> polycraft$selectRecipe(
        RecipeManager manager,
        RecipeType<SmithingRecipe> type,
        RecipeInput input,
        Level level,
        Operation<Optional<RecipeHolder<SmithingRecipe>>> original
    ) {
        Optional<RecipeHolder<SmithingRecipe>> vanilla = original.call(manager, type, input, level);
        Player player = ((PCItemCombinerMenuAccessor) (Object) this).polycraft$player();
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return vanilla;
        }

        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        Optional<ConflictService.Selection<SmithingRecipe>> selection = ConflictService.select(
            serverLevel,
            type,
            (SmithingRecipeInput) input,
            vanilla.orElse(null),
            PolyCraftNetworking.choicesFor(serverPlayer, menu),
            null
        );

        PolyCraftNetworking.sync(serverPlayer, menu, selection.map(ConflictService.Selection::offer).orElse(null));
        return selection.map(ConflictService.Selection::chosen).or(() -> vanilla);
    }
}
