package com.chimericdream.logallthethings.windowlog.client;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import com.chimericdream.logallthethings.ModInfo;

/**
 * Loads and caches the glass-frame geometry (see {@link WindowPaneGeometry}) for each stair/slab
 * shape variant from {@code assets/logallthethings/models/block/}. Not every variant this looks up is
 * expected to exist yet (see {@code WindowedBlockEntityRenderer}'s dispatch table) — a missing file is
 * cached as {@link Optional#empty()} so callers can fall back gracefully instead of re-attempting (and
 * re-logging) the failed load every frame.
 *
 * <p>Lazily populated and never invalidated: these files ship with the mod jar and don't change at
 * runtime outside a resource pack reload, which this doesn't currently handle (a known limitation —
 * reloading resource packs mid-session would need this cache cleared via a reload listener).
 */
public final class WindowPaneGeometryCache {
    private static final Gson GSON = new Gson();
    private static final Map<String, Optional<WindowPaneGeometry>> CACHE = new HashMap<>();

    private WindowPaneGeometryCache() {
    }

    /**
     * @param modelName e.g. {@code "stairs"}, {@code "inner_stairs_mirrored_top"} — resolved against
     *                  {@code assets/logallthethings/models/block/<modelName>.json}.
     */
    public static Optional<WindowPaneGeometry> get(String modelName) {
        return CACHE.computeIfAbsent(modelName, WindowPaneGeometryCache::load);
    }

    private static Optional<WindowPaneGeometry> load(String modelName) {
        Identifier location = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "models/block/" + modelName + ".json");

        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(location);
            try (var reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                WindowPaneGeometry.Json json = GSON.fromJson(reader, WindowPaneGeometry.Json.class);
                return Optional.of(WindowPaneGeometry.fromJson(json));
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
