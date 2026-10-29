package com.chimericdream.rulesforthee.rule;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * Tells code that has no player in hand which player a rule read is for.
 *
 * <p>Most rule reads happen in a method on the player, or with the player passed in. A few vanilla helpers
 * are static and never receive the player (the respawn point finder is one), so the caller, which does have
 * the player, opens a scope around the call and a hook inside the helper asks for the {@link #current}
 * subject. Scopes nest and are per thread; every scope is closed in a {@code try}/{@code finally} (or
 * try-with-resources), so one can't leak into later work.
 */
public final class SubjectScope implements AutoCloseable {
    private static final ThreadLocal<Deque<ServerPlayer>> STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private SubjectScope() {
    }

    public static SubjectScope enter(ServerPlayer subject) {
        STACK.get().push(subject);

        return new SubjectScope();
    }

    /** The player the innermost open scope is for, if any scope is open on this thread. */
    public static Optional<ServerPlayer> current() {
        return Optional.ofNullable(STACK.get().peek());
    }

    @Override
    public void close() {
        Deque<ServerPlayer> stack = STACK.get();
        stack.pop();
        if (stack.isEmpty()) {
            // Don't leave an empty deque behind on a long-lived server thread.
            STACK.remove();
        }
    }
}
