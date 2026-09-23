package com.chimericdream.chimericlib.test.config;

import com.chimericdream.lib.config.YaclConfig;
import com.chimericdream.lib.config.YaclConfigScreens;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.platform.YACLPlatform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Does not extend {@code BootstrapMinecraft}: {@link SampleConfig} has no Minecraft-typed fields, and
 * {@code ConfigClassHandler}/Gson serialization work fine against the plain int field below without a
 * bootstrapped registry — only the actual YACL-generated {@link net.minecraft.client.gui.screens.Screen}
 * needs a live client, which is exactly why {@link #screenWithoutABuilderThrows()} never constructs one.
 */
public class YaclConfigTest {
    public static class SampleConfig {
        @SerialEntry
        public int value = 10;
    }

    private final List<Path> filesToClean = new ArrayList<>();

    @AfterEach
    void cleanup() throws IOException {
        for (Path path : filesToClean) {
            Files.deleteIfExists(path);
        }
        filesToClean.clear();
    }

    private static String uniqueModId() {
        return "yacltest-" + UUID.randomUUID();
    }

    private Path trackFile(String fileName) {
        Path path = YACLPlatform.getConfigDir().resolve(fileName);
        filesToClean.add(path);
        return path;
    }

    @Test
    void defaultFileNameIsModIdJson5() {
        String modId = uniqueModId();
        Path expectedPath = trackFile(modId + ".json5");

        YaclConfig<SampleConfig> config = YaclConfig.builder(SampleConfig.class, modId).build();

        assertFalse(Files.exists(expectedPath), "config file should not exist before load()");
        config.load();
        assertTrue(Files.exists(expectedPath), "load() should create the default <modId>.json5 file");
    }

    @Test
    void fileNameOverridesTheDefault() {
        String modId = uniqueModId();
        String customFileName = modId + "-custom.json5";
        Path customPath = trackFile(customFileName);
        Path defaultPath = trackFile(modId + ".json5");

        YaclConfig<SampleConfig> config = YaclConfig.builder(SampleConfig.class, modId)
            .fileName(customFileName)
            .build();
        config.load();

        assertTrue(Files.exists(customPath), "load() should create the overridden file name");
        assertFalse(Files.exists(defaultPath), "the default <modId>.json5 name should not be used");
    }

    @Test
    void loadRoundTripsAMutatedValueThroughSave() {
        String modId = uniqueModId();
        String fileName = modId + ".json5";
        trackFile(fileName);

        YaclConfig<SampleConfig> first = YaclConfig.builder(SampleConfig.class, modId)
            .fileName(fileName)
            .build();
        first.load();
        first.instance().value = 42;
        first.save();

        // A fresh YaclConfig instance pointed at the same file should read back the saved value.
        YaclConfig<SampleConfig> second = YaclConfig.builder(SampleConfig.class, modId)
            .fileName(fileName)
            .build();
        second.load();

        assertEquals(42, second.instance().value);
    }

    @Test
    void onLoadHookReceivesTheLoadedInstance() {
        String modId = uniqueModId();
        trackFile(modId + ".json5");
        AtomicReference<SampleConfig> seenByHook = new AtomicReference<>();

        YaclConfig<SampleConfig> config = YaclConfig.builder(SampleConfig.class, modId)
            .onLoad(seenByHook::set)
            .build();
        config.load();

        assertSame(config.instance(), seenByHook.get());
    }

    @Test
    void initDoesNotRegisterAScreenWhenNoneWasSupplied() {
        String modId = uniqueModId();
        trackFile(modId + ".json5");

        YaclConfig<SampleConfig> config = YaclConfig.builder(SampleConfig.class, modId).build();
        config.init();

        assertFalse(config.hasScreen());
        assertTrue(YaclConfigScreens.forMod(modId).isEmpty());
    }

    @Test
    void initRegistersAScreenAndReinitDoesNotDuplicateIt() {
        String modId = uniqueModId();
        trackFile(modId + ".json5");

        YaclConfig<SampleConfig> config = YaclConfig.builder(SampleConfig.class, modId)
            .screen((defaults, cfg, builder) -> builder)
            .build();

        config.init();
        assertTrue(config.hasScreen());
        assertSame(config, YaclConfigScreens.forMod(modId).orElseThrow());

        // Re-init (e.g. a second call from a mod's own init(), or a dev-env hot reload) must not add
        // a second entry for the same mod id.
        config.init();
        long registeredForThisMod = YaclConfigScreens.all().stream()
            .filter(registered -> registered.modId().equals(modId))
            .count();
        assertEquals(1, registeredForThisMod);
    }

    @Test
    void screenWithoutABuilderThrows() {
        String modId = uniqueModId();
        trackFile(modId + ".json5");

        YaclConfig<SampleConfig> config = YaclConfig.builder(SampleConfig.class, modId).build();

        // Never actually builds a Screen here (that needs a live client) — hasScreen() is checked
        // before screenBuilder is ever touched, so passing null for the unused parent is safe.
        assertThrows(IllegalStateException.class, () -> config.screen(null));
    }
}
