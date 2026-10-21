package com.chimericdream.polycraft.recipe;

import com.chimericdream.polycraft.core.ConflictDetector;
import com.chimericdream.polycraft.core.IdFilter;
import com.chimericdream.polycraft.core.RecipeInfo;
import com.chimericdream.polycraft.core.Signature;
import com.chimericdream.polycraft.core.SignatureBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * PolyCraft's view of the loaded recipes: every recipe grouped by type in vanilla's own order, plus a signature for
 * each one, computed the first time it is needed. A new index is built whenever the game loads a new
 * {@link RecipeManager}, which is what happens on every {@code /reload}.
 * <p>
 * Signatures cover the vanilla recipe shapes (shaped, shapeless, single-input cooking, smithing). Any other recipe
 * gets a unique signature, so it is never treated as identical to something else, but it can still take part in
 * overlapping-input conflicts, which work from the real input and need no signature.
 */
public final class RecipeIndex {
    private static volatile RecipeIndex current;

    private final RecipeManager manager;
    private final Map<RecipeType<?>, List<RecipeHolder<?>>> byType = new IdentityHashMap<>();
    private final Map<RecipeHolder<?>, Signature> signatures = new ConcurrentHashMap<>();

    private RecipeIndex(RecipeManager manager) {
        this.manager = manager;
        for (RecipeHolder<?> holder : manager.getRecipes()) {
            byType.computeIfAbsent(holder.value().getType(), type -> new ArrayList<>()).add(holder);
        }
    }

    /** The index for {@code manager}, built on first use and rebuilt when the manager changes. */
    public static RecipeIndex of(RecipeManager manager) {
        RecipeIndex index = current;
        if (index == null || index.manager != manager) {
            index = new RecipeIndex(manager);
            current = index;
        }

        return index;
    }

    /** Every recipe of {@code type}, in the order vanilla tries them. */
    public List<RecipeHolder<?>> recipes(RecipeType<?> type) {
        return byType.getOrDefault(type, List.of());
    }

    public Signature signature(RecipeHolder<?> holder) {
        return signatures.computeIfAbsent(holder, RecipeIndex::computeSignature);
    }

    public static String typeId(RecipeType<?> type) {
        var key = BuiltInRegistries.RECIPE_TYPE.getKey(type);
        return key == null ? "unknown:unknown" : key.toString();
    }

    public static String recipeId(RecipeHolder<?> holder) {
        return holder.id().identifier().toString();
    }

    /** What {@code stack} is, without its count: the item plus a hash of its components. */
    public static String resultKey(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()) + "#" + Integer.toHexString(ItemStack.hashItemAndComponents(stack.copyWithCount(1)));
    }

    /** A recipe's description for one actual input. {@code result} is what the recipe makes from that input. */
    public RecipeInfo info(RecipeHolder<?> holder, ItemStack result) {
        return new RecipeInfo(recipeId(holder), signature(holder), resultKey(result), result.getCount());
    }

    /**
     * All identical-input conflicts among the loaded recipes of the allowed types, for the commands. Each recipe's
     * result comes from running it on an input built from its own ingredients; a recipe that can't be described
     * that way is left out, and so is any recipe the pack's rules exclude.
     */
    public List<ConflictDetector.Conflict> allConflicts(IdFilter allowedTypes, Predicate<String> excludedRecipe) {
        List<RecipeInfo> infos = new ArrayList<>();
        for (Map.Entry<RecipeType<?>, List<RecipeHolder<?>>> entry : byType.entrySet()) {
            if (!allowedTypes.test(typeId(entry.getKey()))) {
                continue;
            }

            for (RecipeHolder<?> holder : entry.getValue()) {
                if (excludedRecipe.test(recipeId(holder))) {
                    continue;
                }

                syntheticResult(holder).ifPresent(result -> infos.add(info(holder, result)));
            }
        }

        // The type map isn't ordered, so sort for a stable result: recipe ids are unique.
        infos.sort((a, b) -> a.id().compareTo(b.id()));
        return ConflictDetector.detectAll(infos);
    }

    /** The recipe's result for a made-up input that it accepts, or empty if it can't be worked out that way. */
    public static Optional<ItemStack> syntheticResult(RecipeHolder<?> holder) {
        try {
            Recipe<?> recipe = holder.value();
            ItemStack result;
            if (recipe instanceof ShapedRecipe shaped) {
                List<ItemStack> items = new ArrayList<>();
                for (Optional<Ingredient> slot : shaped.getIngredients()) {
                    items.add(slot.map(RecipeIndex::firstStack).orElse(ItemStack.EMPTY));
                }

                result = shaped.assemble(CraftingInput.of(shaped.getWidth(), shaped.getHeight(), items));
            } else if (recipe instanceof ShapelessRecipe shapeless) {
                List<ItemStack> items = shapeless.placementInfo().ingredients().stream().map(RecipeIndex::firstStack).toList();
                result = shapeless.assemble(CraftingInput.of(items.size(), 1, items));
            } else if (recipe instanceof SingleItemRecipe single) {
                result = single.assemble(new SingleRecipeInput(firstStack(single.input())));
            } else if (recipe instanceof SmithingRecipe smithing) {
                result = smithing.assemble(new SmithingRecipeInput(
                    smithing.templateIngredient().map(RecipeIndex::firstStack).orElse(ItemStack.EMPTY),
                    firstStack(smithing.baseIngredient()),
                    smithing.additionIngredient().map(RecipeIndex::firstStack).orElse(ItemStack.EMPTY)
                ));
            } else {
                return Optional.empty();
            }

            return result.isEmpty() ? Optional.empty() : Optional.of(result);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private static Signature computeSignature(RecipeHolder<?> holder) {
        Recipe<?> recipe = holder.value();
        String type = typeId(recipe.getType());
        String id = recipeId(holder);

        if (recipe instanceof ShapedRecipe shaped) {
            List<String> cells = new ArrayList<>();
            for (Optional<Ingredient> slot : shaped.getIngredients()) {
                cells.add(slotKey(slot));
            }

            return SignatureBuilder.shaped(type, id, shaped.getWidth(), shaped.getHeight(), cells);
        }

        if (recipe instanceof ShapelessRecipe shapeless) {
            List<String> keys = new ArrayList<>();
            for (Ingredient ingredient : shapeless.placementInfo().ingredients()) {
                keys.add(ingredientKey(ingredient));
            }

            return SignatureBuilder.shapeless(type, id, keys);
        }

        if (recipe instanceof SingleItemRecipe single) {
            return SignatureBuilder.ordered(type, id, singletonList(ingredientKey(single.input())));
        }

        if (recipe instanceof SmithingRecipe smithing) {
            List<String> slots = new ArrayList<>();
            slots.add(slotKey(smithing.templateIngredient()));
            slots.add(ingredientKey(smithing.baseIngredient()));
            slots.add(slotKey(smithing.additionIngredient()));
            return SignatureBuilder.ordered(type, id, slots);
        }

        return Signature.unique(type, id);
    }

    /** An optional slot's key: empty for no ingredient, {@code null} (unknown) if the ingredient can't be expressed. */
    private static String slotKey(Optional<Ingredient> slot) {
        return slot.isPresent() ? ingredientKey(slot.get()) : SignatureBuilder.EMPTY;
    }

    /** {@code null} when the ingredient can't be expressed as a set of items. */
    @SuppressWarnings("deprecation")
    private static String ingredientKey(Ingredient ingredient) {
        List<String> ids = new ArrayList<>();
        ingredient.items().forEach(holder -> ids.add(BuiltInRegistries.ITEM.getKey(holder.value()).toString()));
        return SignatureBuilder.ingredient(ids);
    }

    @SuppressWarnings("deprecation")
    private static ItemStack firstStack(Ingredient ingredient) {
        return ingredient.items().findFirst().map(Holder::value).map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    private static List<String> singletonList(String value) {
        List<String> list = new ArrayList<>(1);
        list.add(value);
        return list;
    }
}
