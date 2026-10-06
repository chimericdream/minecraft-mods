package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.OpusMod;
import com.chimericdream.opus.core.widget.MobFit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Creates and caches the client-side mobs that {@code entity} widgets draw, one per entity id for the life of a book
 * screen. A mob that can't be previewed (not a living mob, unknown id, constructor failure, no world to create it in)
 * is remembered with a short reason; the widget then shows its labelled placeholder with that reason, and the
 * problem is logged once.
 */
final class MobPreviews {
    record Preview(LivingEntity entity, MobFit.Bounds bounds) {
    }

    private record Failure(String reason) {
    }

    private final Map<String, Object> cache = new HashMap<>();
    private final Set<String> logged = new HashSet<>();

    /** Collision box of the mob for the layout engine, or {@code null} when it can't be drawn live. */
    MobFit.Bounds bounds(String id) {
        Preview preview = preview(id);
        return preview == null ? null : preview.bounds();
    }

    Preview preview(String id) {
        return resolve(id) instanceof Preview preview ? preview : null;
    }

    /** Why {@code id} has no live preview, or {@code null} when it does. */
    String failure(String id) {
        return resolve(id) instanceof Failure failure ? failure.reason() : null;
    }

    private Object resolve(String id) {
        Object cached = cache.get(id);
        if (cached != null) {
            return cached;
        }

        Object created = create(id);
        // A missing world is temporary (the screen can open from the title screen in a future version), so retry later.
        if (!(created instanceof Failure failure && failure.reason().equals(NO_WORLD))) {
            cache.put(id, created);
        }
        if (created instanceof Failure failure && logged.add(id)) {
            OpusMod.LOGGER.warn("Opus: can't show a live preview of '{}': {}", id, failure.reason());
        }

        return created;
    }

    private static final String NO_WORLD = "needs a world to be loaded";

    private static int nextId = 1;

    private static Object create(String id) {
        Identifier identifier = id == null ? null : Identifier.tryParse(id);
        if (identifier == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(identifier)) {
            return new Failure("unknown entity");
        }

        var level = Minecraft.getInstance().level;
        if (level == null) {
            return new Failure(NO_WORLD);
        }

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(identifier);
        Entity entity;
        try {
            entity = type.create(level, EntitySpawnReason.LOAD);
        } catch (Throwable t) {
            return new Failure("could not be created (" + t.getClass().getSimpleName() + ")");
        }

        if (entity == null) {
            return new Failure("could not be created");
        }
        if (!(entity instanceof LivingEntity living)) {
            return new Failure("not a living mob");
        }

        // Render-state extraction reads the id, and 0 means "not assigned" (this entity never joins a level).
        living.setId(nextId++);

        return new Preview(living, new MobFit.Bounds(living.getBbWidth(), living.getBbHeight()));
    }
}
