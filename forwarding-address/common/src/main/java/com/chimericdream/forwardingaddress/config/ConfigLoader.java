package com.chimericdream.forwardingaddress.config;

import com.chimericdream.forwardingaddress.ForwardingAddressMod;
import com.chimericdream.forwardingaddress.ForwardingSettings;
import com.chimericdream.forwardingaddress.api.Migrations;
import com.chimericdream.forwardingaddress.api.PropertyMapper;
import com.chimericdream.forwardingaddress.stub.StubBlock;
import com.chimericdream.forwardingaddress.stub.StubGuard;
import com.chimericdream.forwardingaddress.stub.StubProperties;
import com.chimericdream.forwardingaddress.stub.StubSpec;
import com.chimericdream.lib.registries.ModRegistryHelper;
import dev.architectury.platform.Platform;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Reads the migration JSON files in {@code config/forwardingaddress/migrations/}, registers the stubs
 * they declare, and declares their migrations through the same API mods use. Runs while the mod
 * initializes, because stubs are registry entries and the registries freeze right after.
 */
public final class ConfigLoader {
    private static final Logger LOGGER = ForwardingAddressMod.LOGGER;
    private static final Set<Identifier> STUBS = new HashSet<>();

    private ConfigLoader() {
    }

    public static Path folder() {
        return ForwardingSettings.folder().resolve("migrations");
    }

    /**
     * What one load did, for logging and tests.
     */
    public record Summary(int blocks, int items, int stubs, String stubHash) {
    }

    /**
     * Reads every {@code .json} file in the migrations folder, in name order, and applies it. A file
     * that can't be read or parsed is skipped with an error; it never stops the others.
     */
    public static Summary load(ModRegistryHelper helper) {
        Path folder = folder();
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            LOGGER.warn("Could not create {}", folder, e);
            return new Summary(0, 0, 0, hash(new TreeMap<>()));
        }

        List<Path> files;
        try (Stream<Path> listing = Files.list(folder)) {
            files = listing
                .filter(path -> path.getFileName().toString().endsWith(".json"))
                .sorted()
                .toList();
        } catch (IOException e) {
            LOGGER.error("Could not list {}", folder, e);
            return new Summary(0, 0, 0, hash(new TreeMap<>()));
        }

        Map<String, String> stubDeclarations = new TreeMap<>();
        int blocks = 0;
        int items = 0;
        int stubs = 0;
        for (Path file : files) {
            String text;
            try {
                text = Files.readString(file, StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOGGER.error("Could not read {}; skipping it", file, e);
                continue;
            }

            Summary summary = apply(MigrationFile.parse(text), file.getFileName().toString(), helper, Platform::isModLoaded, stubDeclarations);
            blocks += summary.blocks();
            items += summary.items();
            stubs += summary.stubs();
        }

        String hash = hash(stubDeclarations);
        if (!files.isEmpty()) {
            LOGGER.info("Loaded {} migration file(s): {} block, {} item, {} stub(s) [stub config {}]. Clients and servers must have identical stub config.",
                files.size(), blocks, items, stubs, hash);
        }

        return new Summary(blocks, items, stubs, hash);
    }

    /**
     * Applies one parsed file. Also the entry point GameTests use, with their own registry helper and
     * installed-mod check.
     *
     * @param source the name used in messages, normally the file name
     * @throws IllegalStateException if a stub's ID is already registered by something that isn't an
     *                               installed mod, since going on would crash the registry
     */
    public static Summary apply(MigrationFile.Parsed parsed, String source, ModRegistryHelper helper, Predicate<String> modInstalled) {
        return apply(parsed, source, helper, modInstalled, new TreeMap<>());
    }

    private static Summary apply(MigrationFile.Parsed parsed, String source, ModRegistryHelper helper, Predicate<String> modInstalled, Map<String, String> stubDeclarations) {
        for (String problem : parsed.problems()) {
            LOGGER.warn("{}: {}", source, problem);
        }

        int blocks = 0;
        int stubs = 0;
        for (MigrationFile.BlockEntry entry : parsed.blocks()) {
            if (entry.stub() != null && registerStub(entry, source, helper, modInstalled)) {
                stubs++;
                stubDeclarations.put(entry.from().toString(), describe(entry.stub()));
            }

            Migrations.BlockTarget migration = Migrations.block(entry.from()).to(entry.to());
            PropertyMapper mapper = PropertyRules.mapper(entry.renameProperties(), entry.mapValues());
            if (mapper != null) {
                migration.mapProperties(mapper);
            }
            migration.register();
            blocks++;
        }

        for (MigrationFile.ItemEntry entry : parsed.items()) {
            Migrations.item(entry.from()).to(entry.to()).register();
        }

        return new Summary(blocks, parsed.items().size(), stubs, "");
    }

    private static boolean registerStub(MigrationFile.BlockEntry entry, String source, ModRegistryHelper helper, Predicate<String> modInstalled) {
        Identifier id = entry.from();

        switch (StubGuard.check(id, modInstalled, known -> BuiltInRegistries.BLOCK.containsKey(known) || STUBS.contains(known))) {
            case REFUSED_VANILLA -> {
                LOGGER.error("{}: refusing to register a stub under {}; stubs under minecraft: are not allowed", source, id);
                return false;
            }
            case MOD_INSTALLED -> {
                LOGGER.info("{}: {} is installed, so the stub for {} is not needed", source, id.getNamespace(), id);
                return false;
            }
            case ALREADY_REGISTERED -> throw new IllegalStateException(
                "Cannot register a stub for " + id + " (declared in " + source + "): something is already registered under that ID. "
                    + "Remove the stub from " + source + ", or the other declaration of it."
            );
            case REGISTER -> {
                // fall through to registration below
            }
        }

        List<Property<?>> properties = StubProperties.build(entry.stub());
        if (properties == null) {
            LOGGER.error("{}: the template block {} for the stub {} does not exist; skipping the stub", source, entry.stub().template(), id);
            return false;
        }

        STUBS.add(id);
        helper.registerWithItem(
            id,
            () -> StubBlock.create(id, properties),
            new Item.Properties().component(DataComponents.ITEM_NAME, Component.translatable("item.forwardingaddress.stub", id.toString()))
        );

        return true;
    }

    private static String describe(StubSpec spec) {
        return spec.template() != null ? "template=" + spec.template() : "properties=" + spec.properties();
    }

    /**
     * A short fingerprint of the stub declarations, logged at startup so a client's and a server's logs
     * can be compared when a join fails over mismatched registries.
     */
    static String hash(Map<String, String> declarations) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            declarations.forEach((id, spec) -> digest.update((id + "=" + spec + "\n").getBytes(StandardCharsets.UTF_8)));
            return HexFormat.of().formatHex(digest.digest()).substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
