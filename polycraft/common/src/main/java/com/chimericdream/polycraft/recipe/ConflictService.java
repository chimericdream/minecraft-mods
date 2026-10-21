package com.chimericdream.polycraft.recipe;

import com.chimericdream.polycraft.PolyCraftMod;
import com.chimericdream.polycraft.config.PolyCraftConfig;
import com.chimericdream.polycraft.core.ChoiceResolver;
import com.chimericdream.polycraft.core.ConflictDetector;
import com.chimericdream.polycraft.core.PinMap;
import com.chimericdream.polycraft.core.RecipeInfo;
import com.chimericdream.polycraft.core.Rules;
import com.chimericdream.polycraft.rules.PolyCraftRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Where PolyCraft decides which recipe a station uses for the input it holds right now. It runs on the server only,
 * after vanilla has found its own pick, and either confirms that pick or replaces it. Anything unexpected falls back
 * to vanilla's pick, so a bug here can cost a player a choice but never a craft.
 */
public final class ConflictService {
    /** What the player can choose between at one station right now. */
    public record Offer(String groupKey, List<Option> options, int selectedIndex) {
        public Option selected() {
            return options.get(selectedIndex);
        }

        public Optional<Option> byId(String recipeId) {
            return options.stream().filter(option -> option.id().equals(recipeId)).findFirst();
        }
    }

    public record Option(RecipeHolder<?> holder, ItemStack result) {
        public String id() {
            return RecipeIndex.recipeId(holder);
        }
    }

    /**
     * {@code chosen} is the recipe the station should use. {@code offer} is present when the player has a real choice,
     * and drives the picker.
     */
    public record Selection<T extends Recipe<?>>(RecipeHolder<T> chosen, @Nullable Offer offer) {
    }

    private ConflictService() {
    }

    /**
     * @param vanillaPick  the recipe vanilla would use for this input, or null if it found none
     * @param playerChoice the acting player's remembered choice for a group key; use {@code key -> Optional.empty()}
     *                     when there is no acting player
     * @param pins         the station's pinned recipes, or null for stations without pins
     * @return empty when vanilla's pick should stand and there is nothing to offer
     */
    @SuppressWarnings("unchecked")
    public static <I extends RecipeInput, T extends Recipe<I>> Optional<Selection<T>> select(
        ServerLevel level,
        RecipeType<T> type,
        I input,
        @Nullable RecipeHolder<T> vanillaPick,
        Function<String, Optional<String>> playerChoice,
        @Nullable PinMap pins
    ) {
        try {
            PolyCraftConfig config = PolyCraftConfig.get();
            if (vanillaPick == null || input.isEmpty() || !config.recipeTypes().test(RecipeIndex.typeId(type))) {
                return Optional.empty();
            }

            RecipeIndex index = RecipeIndex.of(level.recipeAccess());
            List<RecipeHolder<?>> all = index.recipes(type);
            if (all.size() < 2) {
                return Optional.empty();
            }

            Rules rules = PolyCraftRules.current();
            List<Option> matches = new ArrayList<>();
            List<RecipeInfo> infos = new ArrayList<>();
            for (RecipeHolder<?> candidate : all) {
                RecipeHolder<T> holder = (RecipeHolder<T>) candidate;
                if (rules.isExcluded(RecipeIndex.recipeId(holder)) || !holder.value().matches(input, level)) {
                    continue;
                }

                ItemStack result = assembleSafely(holder, input);
                if (result.isEmpty() || !result.isItemEnabled(level.enabledFeatures())) {
                    continue;
                }

                matches.add(new Option(holder, result));
                infos.add(index.info(holder, result));
            }

            if (matches.isEmpty()) {
                return Optional.empty();
            }

            ConflictDetector.Mode mode = config.detectOverlappingInputs ? ConflictDetector.Mode.OVERLAPPING : ConflictDetector.Mode.IDENTICAL;
            Optional<ConflictDetector.Conflict> conflict = ConflictDetector.detect(infos, mode);

            if (conflict.isEmpty()) {
                // No choice to offer. Still skip an excluded recipe that vanilla would have picked.
                RecipeHolder<T> first = (RecipeHolder<T>) matches.getFirst().holder();
                return first.equals(vanillaPick) ? Optional.empty() : Optional.of(new Selection<>(first, null));
            }

            ConflictDetector.Conflict found = conflict.get();
            List<Option> options = new ArrayList<>();
            for (String id : found.optionIds()) {
                matches.stream().filter(option -> option.id().equals(id)).findFirst().ifPresent(options::add);
            }

            List<String> ids = options.stream().map(Option::id).toList();
            ChoiceResolver.Resolution resolution = ChoiceResolver.resolve(
                ids,
                pins == null ? Optional.empty() : pins.get(found.groupKey()),
                playerChoice.apply(found.groupKey()),
                rules.packOrder(found.groupKey(), ids)
            );

            // A recipe the player placed from the recipe book is vanilla's pick on purpose: respect it.
            String selectedId = ids.contains(RecipeIndex.recipeId(vanillaPick)) && resolution.source() == ChoiceResolver.Source.VANILLA
                ? RecipeIndex.recipeId(vanillaPick)
                : resolution.recipeId();

            Offer offer = new Offer(found.groupKey(), List.copyOf(options), ids.indexOf(selectedId));
            return Optional.of(new Selection<>((RecipeHolder<T>) offer.selected().holder(), offer));
        } catch (RuntimeException e) {
            PolyCraftMod.LOGGER.error("PolyCraft couldn't pick a recipe; using vanilla's", e);
            return Optional.empty();
        }
    }

    private static <I extends RecipeInput, T extends Recipe<I>> ItemStack assembleSafely(RecipeHolder<T> holder, I input) {
        try {
            return holder.value().assemble(input);
        } catch (RuntimeException e) {
            return ItemStack.EMPTY;
        }
    }
}
