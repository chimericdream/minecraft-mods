package com.chimericdream.hopperxtreme.recipe;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.item.FilterCopying;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Copies a Hopper Item Filter, like cloning a book: one configured filter plus one blank filter of the same tier,
 * and nothing else, make a second identical filter. The configured filter stays in the grid. A vanilla recipe
 * can't tell a blank filter from a configured one, so this has to be a custom recipe type.
 */
public class FilterCopyRecipe extends CustomRecipe {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ModInfo.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeSerializer<FilterCopyRecipe>> SERIALIZER = SERIALIZERS.register(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "filter_copy"),
        () -> new RecipeSerializer<>(
            MapCodec.unit(FilterCopyRecipe::new),
            StreamCodec.<RegistryFriendlyByteBuf, FilterCopyRecipe>unit(new FilterCopyRecipe())
        )
    );

    public static void init() {
        SERIALIZERS.register();
    }

    /** The configured filter, if the grid holds exactly one configured and one blank filter of the same tier. */
    private static ItemStack findSource(CraftingInput input) {
        ItemStack source = ItemStack.EMPTY;
        ItemStack blank = ItemStack.EMPTY;

        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }

            if (FilterCopying.isConfigured(stack) && source.isEmpty()) {
                source = stack;
            } else if (FilterCopying.isBlank(stack) && blank.isEmpty()) {
                blank = stack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        return !source.isEmpty() && !blank.isEmpty() && ItemStack.isSameItem(source, blank) ? source : ItemStack.EMPTY;
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        return !findSource(input).isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input) {
        return FilterCopying.copyOf(findSource(input));
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
        NonNullList<ItemStack> remaining = CraftingRecipe.defaultCraftingReminder(input);
        ItemStack source = findSource(input);

        // The configured filter is copied, not used up: hand it back so the player keeps the original.
        for (int i = 0; i < input.size(); i++) {
            if (input.getItem(i) == source) {
                remaining.set(i, FilterCopying.copyOf(source));
            }
        }

        return remaining;
    }

    @Override
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER.get();
    }
}
