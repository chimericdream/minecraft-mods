package com.chimericdream.opus.recipe;

import com.chimericdream.opus.ModInfo;
import com.chimericdream.opus.component.TomeContentsComponent;
import com.chimericdream.opus.item.TomeStacks;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds books to a Book of Books: one or more Books of Books (or books turned out of one) plus any number of plain
 * storable books (Opus guides or items in the {@code opus:book_of_books_storable} tag), and nothing else, make a Book of Books holding everything. Duplicates are kept.
 */
public class TomeCombineRecipe extends CustomRecipe {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ModInfo.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeSerializer<TomeCombineRecipe>> SERIALIZER = SERIALIZERS.register(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tome_combine"),
        () -> new RecipeSerializer<>(
            MapCodec.unit(TomeCombineRecipe::new),
            StreamCodec.<RegistryFriendlyByteBuf, TomeCombineRecipe>unit(new TomeCombineRecipe())
        )
    );

    public static void init() {
        SERIALIZERS.register();
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        int tomes = 0;
        int books = 0;

        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }

            if (TomeStacks.isTome(stack)) {
                tomes++;
            } else if (TomeStacks.isPlainBook(stack)) {
                books++;
            } else {
                return false;
            }
        }

        return tomes >= 1 && tomes + books >= 2;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input) {
        List<ItemStack> combined = new ArrayList<>();

        for (ItemStack stack : input.items()) {
            TomeContentsComponent contents = TomeStacks.contentsOf(stack);

            if (contents != null) {
                combined.addAll(contents.books());
            } else if (TomeStacks.isPlainBook(stack)) {
                combined.add(TomeStacks.plain(stack));
            }
        }

        return TomeStacks.tome(TomeContentsComponent.of(combined));
    }

    @Override
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER.get();
    }
}
