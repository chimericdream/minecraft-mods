package com.chimericdream.logallthethings.carpetlog.client;

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
 * Loads and caches the carpet-overlay geometry (see {@link CarpetFrameGeometry}) for each stair/slab
 * shape variant from {@code assets/logallthethings/models/block/}. Mirrors
 * {@code windowlog.client.WindowFrameGeometryCache} - see that class for the lazy-population/no-reload
 * caveat.
 */
public final class CarpetFrameGeometryCache {
    private static final Gson GSON = new Gson();
    private static final Map<String, Optional<CarpetFrameGeometry>> CACHE = new HashMap<>();

    private CarpetFrameGeometryCache() {
    }

    /**
     * @param modelName e.g. {@code "stairs_carpet"}, {@code "slab_top_carpet"} — resolved against
     *                  {@code assets/logallthethings/models/block/<modelName>.json}.
     */
    public static Optional<CarpetFrameGeometry> get(String modelName) {
        return CACHE.computeIfAbsent(modelName, CarpetFrameGeometryCache::load);
    }

    private static Optional<CarpetFrameGeometry> load(String modelName) {
        Identifier location = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "models/block/" + modelName + ".json");

        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(location);
            try (var reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                CarpetFrameGeometry.Json json = GSON.fromJson(reader, CarpetFrameGeometry.Json.class);
                return Optional.of(CarpetFrameGeometry.fromJson(json));
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
