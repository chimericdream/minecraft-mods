package com.chimericdream.polycraft.rules;

import com.chimericdream.polycraft.ModInfo;
import com.chimericdream.polycraft.PolyCraftMod;
import com.chimericdream.polycraft.core.Rules;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Holds the pack author's rules: {@code config/polycraft/rules.json} plus every {@code data/<ns>/polycraft/rules/*.json}
 * from datapacks. The config file wins over datapacks, and datapacks apply in id order, so the result is the same on
 * every run. Datapack rules reload with {@code /reload}; the config file reloads with {@code /polycraft reload}.
 */
public final class PolyCraftRules {
    private static final String DATAPACK_DIR = "polycraft/rules";
    private static final String DEFAULT_FILE = """
        {
          "priority": [],
          "exclude": [],
          "groups": {}
        }
        """;

    private static volatile Rules configRules = Rules.EMPTY;
    private static volatile Rules datapackRules = Rules.EMPTY;
    private static volatile Rules merged = Rules.EMPTY;

    private PolyCraftRules() {
    }

    public static Rules current() {
        return merged;
    }

    public static Path configFile() {
        return Platform.getConfigFolder().resolve(ModInfo.MOD_ID).resolve("rules.json");
    }

    public static void init() {
        Identifier id = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "rules");
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new ResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(@NotNull ResourceManager manager) {
                reloadDatapackRules(manager);
            }

            @Override
            public @NotNull String getName() {
                return id.toString();
            }
        }, id);

        reloadConfigFile();
    }

    /** Reads {@code config/polycraft/rules.json}, creating a starter file the first time. */
    public static void reloadConfigFile() {
        Path file = configFile();
        try {
            if (Files.notExists(file)) {
                Files.createDirectories(file.getParent());
                Files.writeString(file, DEFAULT_FILE, StandardCharsets.UTF_8);
            }

            configRules = Rules.parse(Files.readString(file, StandardCharsets.UTF_8), message -> PolyCraftMod.LOGGER.warn("rules.json: {}", message));
        } catch (IOException e) {
            PolyCraftMod.LOGGER.warn("Couldn't read {}; using no config rules", file, e);
            configRules = Rules.EMPTY;
        }

        rebuild();
    }

    private static void reloadDatapackRules(ResourceManager manager) {
        Map<Identifier, Resource> found = new TreeMap<>(manager.listResources(DATAPACK_DIR, id -> id.getPath().endsWith(".json")));

        Rules combined = Rules.EMPTY;
        for (Map.Entry<Identifier, Resource> entry : found.entrySet()) {
            Identifier id = entry.getKey();
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                String text = reader.lines().collect(Collectors.joining("\n"));
                combined = combined.merge(Rules.parse(text, message -> PolyCraftMod.LOGGER.warn("{}: {}", id, message)));
            } catch (IOException | RuntimeException e) {
                PolyCraftMod.LOGGER.warn("Couldn't read {}; skipping it", id, e);
            }
        }

        datapackRules = combined;
        rebuild();
    }

    private static void rebuild() {
        merged = configRules.merge(datapackRules);
        PolyCraftMod.LOGGER.debug(
            "Rules: {} priority entries, {} excluded recipes, {} group defaults",
            merged.priority().size(), merged.exclude().size(), merged.groupDefaults().size()
        );
    }
}
