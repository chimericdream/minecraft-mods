package com.chimericdream.allhallowssteve.client;

import com.chimericdream.allhallowssteve.wearable.PumpkinFaces;
import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The client's copy of every player's chosen pumpkin face, filled in by {@code PumpkinFacePayload}. Read
 * when drawing a pumpkin on a player's head (see {@code AHS$ItemModelResolverMixin}) and when building the
 * first-person vision overlay. A player with no entry is on the default face.
 */
public final class ClientPumpkinFaces {
    private static final Map<UUID, Direction> FACES = new HashMap<>();

    private ClientPumpkinFaces() {
    }

    public static void set(UUID player, Direction face) {
        if (face == PumpkinFaces.DEFAULT) {
            FACES.remove(player);
        } else {
            FACES.put(player, face);
        }
    }

    public static Direction get(UUID player) {
        return FACES.getOrDefault(player, PumpkinFaces.DEFAULT);
    }
}
