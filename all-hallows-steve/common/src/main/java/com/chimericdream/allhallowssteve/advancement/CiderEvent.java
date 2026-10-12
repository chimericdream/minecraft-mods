package com.chimericdream.allhallowssteve.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * The cider and juice things a player can do that an advancement cares about, and that vanilla has no
 * trigger for. All of them are fired through {@link CiderEventTrigger}.
 */
public enum CiderEvent implements StringRepresentable {
    /** Poured the third bottle into a juice cauldron, filling it. */
    CAULDRON_FILLED("cauldron_filled");

    public static final Codec<CiderEvent> CODEC = StringRepresentable.fromEnum(CiderEvent::values);

    private final String name;

    CiderEvent(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
