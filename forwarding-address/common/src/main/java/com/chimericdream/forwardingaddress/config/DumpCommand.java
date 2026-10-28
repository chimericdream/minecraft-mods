package com.chimericdream.forwardingaddress.config;

import com.chimericdream.forwardingaddress.ForwardingAddressMod;
import com.chimericdream.forwardingaddress.ForwardingSettings;
import com.chimericdream.lib.commands.ChimericCommand;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.properties.Property;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * {@code /forwardingaddress dump <namespace>}: writes a starter migration file listing a mod's blocks
 * and their properties, so a pack maker has what a stub needs before removing the mod. Targets are left
 * blank and never guessed. The file goes to {@code dumps/}, a folder that is never loaded as migrations.
 */
public final class DumpCommand implements ChimericCommand {
    private static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_.-]+");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal("forwardingaddress")
            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(Commands.literal("dump")
                .then(Commands.argument("namespace", StringArgumentType.word())
                    .executes(command -> {
                        String namespace = StringArgumentType.getString(command, "namespace");
                        CommandSourceStack source = command.getSource();

                        if (!NAMESPACE.matcher(namespace).matches()) {
                            source.sendFailure(Component.literal("\"" + namespace + "\" is not a valid namespace."));
                            return 0;
                        }

                        JsonObject dump = dump(namespace);
                        int count = dump.getAsJsonArray("blocks").size();
                        if (count == 0) {
                            source.sendFailure(Component.literal("No blocks are registered under \"" + namespace + "\". Is the mod installed?"));
                            return 0;
                        }

                        Path file = ForwardingSettings.folder().resolve("dumps").resolve(namespace + ".json");
                        try {
                            Files.createDirectories(file.getParent());
                            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                                GSON.toJson(dump, writer);
                            }
                        } catch (IOException e) {
                            ForwardingAddressMod.LOGGER.error("Could not write {}", file, e);
                            source.sendFailure(Component.literal("Could not write " + file + ": " + e.getMessage()));
                            return 0;
                        }

                        source.sendSuccess(() -> Component.literal("Wrote " + count + " block(s) to " + file + ". Fill in each \"to\", then move the entries you want into the migrations folder."), true);
                        return count;
                    })));
    }

    /**
     * @return the starter file for a namespace: every block with its properties, and a blank target
     */
    public static JsonObject dump(String namespace) {
        JsonArray blocks = new JsonArray();
        BuiltInRegistries.BLOCK.entrySet().stream()
            .filter(entry -> entry.getKey().identifier().getNamespace().equals(namespace))
            .sorted((a, b) -> a.getKey().identifier().compareTo(b.getKey().identifier()))
            .forEach(entry -> blocks.add(entry(entry.getKey().identifier(), entry.getValue())));

        JsonObject root = new JsonObject();
        root.addProperty("_note", "Starter file from /forwardingaddress dump. Fill in each \"to\" and move entries you want into the migrations folder. Entries with a blank \"to\" are ignored.");
        root.add("blocks", blocks);
        return root;
    }

    private static JsonObject entry(Identifier id, Block block) {
        JsonObject entry = new JsonObject();
        entry.addProperty("from", id.toString());
        entry.addProperty("to", "");

        if (block instanceof EntityBlock) {
            entry.addProperty("unsupported", "This block has a block entity, which a stub cannot keep. Only migrate it while the mod is installed.");
            return entry;
        }

        JsonObject properties = new JsonObject();
        for (Property<?> property : block.defaultBlockState().getProperties()) {
            properties.add(property.getName(), values(property));
        }

        JsonObject stub = new JsonObject();
        stub.add("properties", properties);
        entry.add("stub", stub);
        return entry;
    }

    private static <T extends Comparable<T>> JsonArray values(Property<T> property) {
        JsonArray values = new JsonArray();
        for (T value : property.getPossibleValues()) {
            values.add(property.getName(value));
        }

        return values;
    }
}
