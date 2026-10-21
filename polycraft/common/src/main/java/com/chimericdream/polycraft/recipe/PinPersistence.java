package com.chimericdream.polycraft.recipe;

import com.chimericdream.polycraft.core.PinMap;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Map;

/** Saving and loading a block's pins, so every block that can be pinned stores them the same way. */
public final class PinPersistence {
    private static final String KEY = "polycraft_pins";
    private static final Codec<Map<String, String>> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING);

    private PinPersistence() {
    }

    public static void save(ValueOutput output, PinMap pins) {
        if (!pins.isEmpty()) {
            output.store(KEY, CODEC, pins.entries());
        }
    }

    public static void load(ValueInput input, PinMap pins) {
        pins.restore(input.read(KEY, CODEC).orElse(Map.of()));
    }
}
