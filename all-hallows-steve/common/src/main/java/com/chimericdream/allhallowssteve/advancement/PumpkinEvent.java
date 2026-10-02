package com.chimericdream.allhallowssteve.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * The things a player can do with a decorated pumpkin that an advancement cares about, and that vanilla
 * has no trigger for. All of them are fired through {@link PumpkinEventTrigger}.
 */
public enum PumpkinEvent implements StringRepresentable {
    /** Took a dyed pumpkin out of the Pumpkin Carving Station. */
    DYED("dyed"),
    /** Took a pumpkin out of the Pumpkin Carving Station with a new stencil carved into it. */
    CARVED("carved"),
    /** Is wearing a decorated pumpkin with a stencil on at least one face. */
    WORN_CARVED("worn_carved"),
    /** Is wearing a decorated pumpkin with all four faces bare. */
    WORN_UNCARVED("worn_uncarved"),
    /** Lit a decorated pumpkin with a soul, copper or redstone torch, or a candle. */
    LIT_UNUSUAL("lit_unusual"),
    /** Turned a worn pumpkin to another face. */
    TURNED("turned");

    public static final Codec<PumpkinEvent> CODEC = StringRepresentable.fromEnum(PumpkinEvent::values);

    private final String name;

    PumpkinEvent(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
