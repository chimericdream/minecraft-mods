package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.PolyCraftMod;
import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.recipe.ConflictService;
import com.chimericdream.polycraft.recipe.StationResolvers;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

/**
 * The preview slot on a Crafter's screen shows what the Crafter would make. It has to follow the same choice the Crafter
 * uses, so the viewer sees the real outcome, and it tells the viewer's client what the choices are.
 */
@Mixin(CrafterMenu.class)
public abstract class PCCrafterMenuMixin {
    @Shadow
    @Final
    private Player player;

    @Shadow
    @Final
    private CraftingContainer container;

    @WrapOperation(
        method = "refreshRecipeResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/CrafterBlock;getPotentialResults(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/crafting/CraftingInput;)Ljava/util/Optional;"
        )
    )
    private Optional<RecipeHolder<CraftingRecipe>> polycraft$selectRecipe(
        ServerLevel level,
        CraftingInput input,
        Operation<Optional<RecipeHolder<CraftingRecipe>>> original
    ) {
        Optional<RecipeHolder<CraftingRecipe>> vanilla = original.call(level, input);
        if (!(container instanceof CrafterBlockEntity crafter) || !(player instanceof ServerPlayer serverPlayer)) {
            return vanilla;
        }

        try {
            AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
            Optional<ConflictService.Selection<CraftingRecipe>> selection = StationResolvers.crafter(
                level, crafter, input, vanilla, PolyCraftNetworking.choicesFor(serverPlayer, menu)
            );
            PolyCraftNetworking.sync(serverPlayer, menu, selection.map(ConflictService.Selection::offer).orElse(null));
            return selection.map(ConflictService.Selection::chosen).or(() -> vanilla);
        } catch (RuntimeException e) {
            PolyCraftMod.LOGGER.error("PolyCraft couldn't pick a Crafter preview recipe; using vanilla's", e);
            return vanilla;
        }
    }
}
