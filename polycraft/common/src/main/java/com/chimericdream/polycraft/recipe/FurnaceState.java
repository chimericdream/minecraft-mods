package com.chimericdream.polycraft.recipe;

import com.chimericdream.polycraft.core.PinMap;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * What PolyCraft remembers on one furnace-type block: its pins, and the recipe it is using for the item in its input
 * slot. Remembering the recipe in use is what stops a cook from changing recipe just because nobody is looking at the
 * furnace any more; it is only chosen again when the input item changes, someone changes the selection or a pin, or the
 * recipes reload.
 */
public final class FurnaceState {
    private static final Codec<Map<String, String>> PINS_CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING);

    private final PinMap pins = new PinMap();
    private @Nullable String activeInput;
    private @Nullable String activeRecipe;
    private boolean stale;

    public PinMap pins() {
        return pins;
    }

    /** The key of the input item the active recipe was chosen for. */
    public @Nullable String activeInput() {
        return activeInput;
    }

    public @Nullable String activeRecipe() {
        return activeRecipe;
    }

    public boolean isStale() {
        return stale;
    }

    public void setActive(String inputKey, String recipeId) {
        activeInput = inputKey;
        activeRecipe = recipeId;
        stale = false;
    }

    public void clearActive() {
        activeInput = null;
        activeRecipe = null;
        stale = false;
    }

    /** Makes the next lookup choose again, while remembering what was in use so a switch can be recognised. */
    public void invalidate() {
        stale = true;
    }

    public void save(ValueOutput output) {
        if (!pins.isEmpty()) {
            output.store("polycraft_pins", PINS_CODEC, pins.entries());
        }

        if (activeInput != null && activeRecipe != null) {
            output.putString("polycraft_active_input", activeInput);
            output.putString("polycraft_active_recipe", activeRecipe);
        }
    }

    public void load(ValueInput input) {
        pins.restore(input.read("polycraft_pins", PINS_CODEC).orElse(Map.of()));

        String savedInput = input.getStringOr("polycraft_active_input", "");
        String savedRecipe = input.getStringOr("polycraft_active_recipe", "");
        if (savedInput.isEmpty() || savedRecipe.isEmpty()) {
            clearActive();
        } else {
            setActive(savedInput, savedRecipe);
        }
    }
}
