package com.chimericdream.polycraft.recipe;

import com.chimericdream.polycraft.core.PinMap;

/**
 * A block entity that can have a recipe pinned to it: furnaces, blast furnaces, smokers and the Crafter. Added by
 * mixin.
 */
public interface PinHolder {
    PinMap polycraft$pins();

    /** Call after changing the pins, so the block is saved and chooses its recipe again. */
    void polycraft$pinsChanged();
}
