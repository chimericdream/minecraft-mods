package com.chimericdream.polycraft.recipe;

import com.chimericdream.polycraft.core.CookMath;
import com.chimericdream.polycraft.network.PolyCraftNetworking;
import com.chimericdream.polycraft.network.StationViewers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Chooses the recipe a furnace, blast furnace or smoker cooks with. The order is the block's pin, then the choice of
 * whoever is using it, then the pack's defaults, then vanilla's pick. The choice is remembered on the block and only
 * made again when the input item changes, someone changes the selection or a pin, or the recipes reload.
 */
public final class FurnaceResolver {
    private FurnaceResolver() {
    }

    /**
     * What the player could choose between right now, and what would be used, without changing anything on the block.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional<ConflictService.Selection<?>> compute(
        ServerLevel level,
        AbstractFurnaceBlockEntity furnace,
        SingleRecipeInput input,
        RecipeHolder<?> vanillaPick
    ) {
        StationBlock station = (StationBlock) furnace;
        ServerPlayer acting = StationViewers.acting(level, furnace);
        Function<String, Optional<String>> choices = acting == null ? key -> Optional.empty() : PolyCraftNetworking.choicesFor(acting);
        Predicate<ItemStack> acceptable = result -> canTake(furnace, result);

        return (Optional) ConflictService.select(
            level,
            (RecipeType) station.polycraft$recipeType(),
            input,
            (RecipeHolder) vanillaPick,
            choices,
            station.polycraft$state().pins(),
            acceptable
        );
    }

    /**
     * Wraps vanilla's lookup for a furnace.
     *
     * @param inputChanged true when the item in the input slot just changed, so the choice must be made afresh and the
     *                     cook starts over; false for the per-tick lookup
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional<RecipeHolder<? extends AbstractCookingRecipe>> resolve(
        ServerLevel level,
        AbstractFurnaceBlockEntity furnace,
        SingleRecipeInput input,
        Optional<? extends RecipeHolder<?>> vanilla,
        boolean inputChanged
    ) {
        StationBlock station = (StationBlock) furnace;
        FurnaceState state = station.polycraft$state();

        if (vanilla.isEmpty()) {
            state.clearActive();
            return Optional.empty();
        }

        RecipeHolder<?> vanillaPick = vanilla.get();
        String inputKey = RecipeIndex.resultKey(input.item());
        String previous = !inputChanged && inputKey.equals(state.activeInput()) ? state.activeRecipe() : null;

        if (!inputChanged && !state.isStale() && previous != null) {
            RecipeHolder<?> cached = byId(level, previous);
            if (cached != null && cached.value().getType() == station.polycraft$recipeType()
                && ((Recipe) cached.value()).matches(input, level)) {
                return Optional.of((RecipeHolder<? extends AbstractCookingRecipe>) cached);
            }
        }

        RecipeHolder<?> chosen = compute(level, furnace, input, vanillaPick)
            .<RecipeHolder<?>>map(ConflictService.Selection::chosen)
            .orElse(vanillaPick);
        String chosenId = RecipeIndex.recipeId(chosen);
        state.setActive(inputKey, chosenId);

        if (!inputChanged) {
            // Changing recipe part-way through a cook keeps the time already spent, against the new recipe's total.
            boolean switched = previous != null ? !previous.equals(chosenId) : !chosen.equals(vanillaPick);
            if (switched) {
                int newTotal = ((AbstractCookingRecipe) chosen.value()).cookingTime();
                station.polycraft$setCooking(CookMath.switchedProgress(station.polycraft$cookingTimer(), newTotal), newTotal);
                furnace.setChanged();
            }
        }

        return Optional.of((RecipeHolder<? extends AbstractCookingRecipe>) chosen);
    }

    private static @Nullable RecipeHolder<?> byId(ServerLevel level, String recipeId) {
        Identifier id = Identifier.tryParse(recipeId);
        if (id == null) {
            return null;
        }

        return level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, id)).orElse(null);
    }

    /** Whether the furnace's output slot could take {@code result} when a cook finishes, by vanilla's own rule. */
    public static boolean canTake(AbstractFurnaceBlockEntity furnace, ItemStack result) {
        ItemStack output = furnace.getItem(2);
        return CookMath.canMergeIntoOutput(
            output.isEmpty(),
            ItemStack.isSameItemSameComponents(output, result),
            output.getCount(),
            result.getCount(),
            Math.min(furnace.getMaxStackSize(), result.getMaxStackSize())
        );
    }
}
