package com.chimericdream.hopperxtreme.recipe;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.component.HopperXtremeComponentTypes;
import com.chimericdream.hopperxtreme.component.HopperXtremeFilterModeComponent;
import com.chimericdream.hopperxtreme.item.ModItems;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Upgrades a Hopper Item Filter to a Diamond Hopper Item Filter (filter + diamond), keeping the filter's items and
 * its Include/Exclude mode. A vanilla shapeless recipe can't copy components from an ingredient, so this is a custom
 * recipe type. The standard filter's slots always fit in the diamond filter's, so nothing is lost.
 */
public class FilterUpgradeRecipe extends CustomRecipe {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ModInfo.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeSerializer<FilterUpgradeRecipe>> SERIALIZER = SERIALIZERS.register(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "filter_upgrade"),
        () -> new RecipeSerializer<>(
            MapCodec.unit(FilterUpgradeRecipe::new),
            StreamCodec.<RegistryFriendlyByteBuf, FilterUpgradeRecipe>unit(new FilterUpgradeRecipe())
        )
    );

    public static void init() {
        SERIALIZERS.register();
    }

    /** The standard filter, if the grid holds exactly one of those plus one diamond and nothing else. */
    private static ItemStack findSource(CraftingInput input) {
        ItemStack filter = ItemStack.EMPTY;
        boolean diamond = false;

        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ModItems.HOPPER_ITEM_FILTER_ITEM.get()) && filter.isEmpty()) {
                filter = stack;
            } else if (stack.is(Items.DIAMOND) && !diamond) {
                diamond = true;
            } else {
                return ItemStack.EMPTY;
            }
        }

        return diamond ? filter : ItemStack.EMPTY;
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        return !findSource(input).isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input) {
        ItemStack source = findSource(input);
        ItemStack result = ModItems.DIAMOND_HOPPER_ITEM_FILTER_ITEM.get().getDefaultInstance();

        ItemContainerContents contents = source.get(DataComponents.CONTAINER);
        if (contents != null) {
            result.set(DataComponents.CONTAINER, contents);
        }

        // Drives the "configured" item model; without it the upgraded filter renders as blank.
        CustomModelData modelData = source.get(DataComponents.CUSTOM_MODEL_DATA);
        if (modelData != null) {
            result.set(DataComponents.CUSTOM_MODEL_DATA, modelData);
        }

        HopperXtremeFilterModeComponent mode = source.get(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get());
        if (mode != null) {
            result.set(HopperXtremeComponentTypes.HOPPER_XTREME_FILTER_MODE_COMPONENT.get(), mode);
        }

        return result;
    }

    @Override
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER.get();
    }
}
