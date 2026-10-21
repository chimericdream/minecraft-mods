package com.chimericdream.polycraft.client;

import com.chimericdream.polycraft.network.ConflictOptionsPayload;
import org.jspecify.annotations.Nullable;

/** What the server last said the player can choose between. Only meaningful for the menu it names. */
public final class ClientConflictState {
    private static volatile ConflictOptionsPayload current;

    private ClientConflictState() {
    }

    public static void set(ConflictOptionsPayload payload) {
        current = payload;
    }

    /** The choices for the menu with {@code containerId}, or null when there are none. */
    public static @Nullable ConflictOptionsPayload forContainer(int containerId) {
        ConflictOptionsPayload payload = current;
        return payload != null && payload.containerId() == containerId && !payload.isEmpty() ? payload : null;
    }

    public static void clear() {
        current = null;
    }
}
