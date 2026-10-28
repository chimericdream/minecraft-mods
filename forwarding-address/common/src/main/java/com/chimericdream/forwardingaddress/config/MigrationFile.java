package com.chimericdream.forwardingaddress.config;

import com.chimericdream.forwardingaddress.stub.StubSpec;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Reads one migration JSON file. Parsing never throws for bad content: a bad entry is dropped with a
 * message in {@link Parsed#problems()}, so one typo can't take the rest of a pack's migrations with it.
 *
 * <pre>{@code
 * {
 *   "blocks": [
 *     {
 *       "from": "oldmod:oak_stairs",
 *       "to": "newmod:oak_stairs",
 *       "stub": { "template": "minecraft:oak_stairs" },
 *       "renameProperties": { "dir": "facing" },
 *       "mapValues": { "half": { "upper": "top", "lower": "bottom" } }
 *     }
 *   ],
 *   "items": [ { "from": "oldmod:wrench", "to": "newmod:wrench" } ]
 * }
 * }</pre>
 *
 * <p>A stub is either {@code "template"} (a block that exists) or {@code "properties"}, a map of property
 * name to its value names. An entry with a blank or missing {@code to} is ignored, which is how the
 * starter files from {@code /forwardingaddress dump} stay harmless until they're filled in.
 */
public final class MigrationFile {
    private static final Pattern NAME = Pattern.compile("[a-z0-9_]+");

    private MigrationFile() {
    }

    public record BlockEntry(
        Identifier from,
        Identifier to,
        @Nullable StubSpec stub,
        Map<String, String> renameProperties,
        Map<String, Map<String, String>> mapValues
    ) {
    }

    public record ItemEntry(Identifier from, Identifier to) {
    }

    public record Parsed(List<BlockEntry> blocks, List<ItemEntry> items, List<String> problems) {
    }

    public static Parsed parse(String json) {
        List<BlockEntry> blocks = new ArrayList<>();
        List<ItemEntry> items = new ArrayList<>();
        List<String> problems = new ArrayList<>();

        JsonObject root;
        try {
            JsonElement parsed = JsonParser.parseString(json);
            if (!parsed.isJsonObject()) {
                problems.add("the file must contain a JSON object");
                return new Parsed(blocks, items, problems);
            }

            root = parsed.getAsJsonObject();
        } catch (RuntimeException e) {
            problems.add("not valid JSON: " + e.getMessage());
            return new Parsed(blocks, items, problems);
        }

        forEachEntry(root, "blocks", problems, (entry, where) -> {
            BlockEntry block = block(entry, where, problems);
            if (block != null) {
                blocks.add(block);
            }
        });
        forEachEntry(root, "items", problems, (entry, where) -> {
            Identifier from = id(entry, "from", where, problems);
            Identifier to = target(entry, where, problems);
            if (from != null && to != null) {
                items.add(new ItemEntry(from, to));
            }
        });

        return new Parsed(blocks, items, problems);
    }

    private interface EntryReader {
        void read(JsonObject entry, String where);
    }

    private static void forEachEntry(JsonObject root, String key, List<String> problems, EntryReader reader) {
        JsonElement element = root.get(key);
        if (element == null) {
            return;
        }

        if (!element.isJsonArray()) {
            problems.add("\"" + key + "\" must be a list");
            return;
        }

        JsonArray array = element.getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            String where = key + "[" + i + "]";
            if (array.get(i).isJsonObject()) {
                reader.read(array.get(i).getAsJsonObject(), where);
            } else {
                problems.add(where + " must be an object");
            }
        }
    }

    @Nullable
    private static BlockEntry block(JsonObject entry, String where, List<String> problems) {
        Identifier from = id(entry, "from", where, problems);
        Identifier to = target(entry, where, problems);
        if (from == null || to == null) {
            return null;
        }

        StubSpec stub = null;
        if (entry.has("stub")) {
            stub = stub(entry.get("stub"), where + ".stub", problems);
            if (stub == null) {
                return null;
            }
        }

        Map<String, String> renames = stringMap(entry, "renameProperties", where, problems);
        Map<String, Map<String, String>> values = new LinkedHashMap<>();
        if (entry.get("mapValues") instanceof JsonObject mapValues) {
            for (Map.Entry<String, JsonElement> property : mapValues.entrySet()) {
                values.put(property.getKey(), stringMap(mapValues, property.getKey(), where + ".mapValues", problems));
            }
        } else if (entry.has("mapValues")) {
            problems.add(where + ".mapValues must be an object");
        }

        return new BlockEntry(from, to, stub, renames, values);
    }

    @Nullable
    private static StubSpec stub(JsonElement element, String where, List<String> problems) {
        if (!(element instanceof JsonObject object)) {
            problems.add(where + " must be an object");
            return null;
        }

        boolean hasTemplate = object.has("template");
        boolean hasProperties = object.has("properties");
        if (hasTemplate == hasProperties) {
            problems.add(where + " needs exactly one of \"template\" or \"properties\"");
            return null;
        }

        if (hasTemplate) {
            Identifier template = id(object, "template", where, problems);
            return template == null ? null : new StubSpec(template, Map.of());
        }

        if (!(object.get("properties") instanceof JsonObject properties)) {
            problems.add(where + ".properties must be an object");
            return null;
        }

        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> property : properties.entrySet()) {
            if (!NAME.matcher(property.getKey()).matches()) {
                problems.add(where + ".properties: \"" + property.getKey() + "\" is not a valid property name (lowercase letters, digits and underscores)");
                return null;
            }

            if (!(property.getValue() instanceof JsonArray array) || array.isEmpty()) {
                problems.add(where + ".properties." + property.getKey() + " must be a non-empty list of value names");
                return null;
            }

            List<String> values = new ArrayList<>();
            for (JsonElement value : array) {
                String text = value.isJsonPrimitive() ? value.getAsString() : "";
                if (!NAME.matcher(text).matches() || values.contains(text)) {
                    problems.add(where + ".properties." + property.getKey() + ": \"" + text + "\" is not a valid, unique value name");
                    return null;
                }
                values.add(text);
            }

            result.put(property.getKey(), values);
        }

        return new StubSpec(null, result);
    }

    private static Map<String, String> stringMap(JsonObject parent, String key, String where, List<String> problems) {
        Map<String, String> result = new LinkedHashMap<>();
        if (!(parent.get(key) instanceof JsonObject object)) {
            if (parent.has(key)) {
                problems.add(where + "." + key + " must be an object");
            }
            return result;
        }

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (entry.getValue().isJsonPrimitive()) {
                result.put(entry.getKey(), entry.getValue().getAsString());
            } else {
                problems.add(where + "." + key + "." + entry.getKey() + " must be a string");
            }
        }

        return result;
    }

    @Nullable
    private static Identifier id(JsonObject entry, String key, String where, List<String> problems) {
        String text = entry.has(key) && entry.get(key).isJsonPrimitive() ? entry.get(key).getAsString() : "";
        Identifier id = text.isBlank() ? null : Identifier.tryParse(text);
        if (id == null) {
            problems.add(where + " has no valid \"" + key + "\" ID" + (text.isBlank() ? "" : " (\"" + text + "\")"));
        }

        return id;
    }

    // A blank or missing target is not an error to shout about: it is an unfinished starter entry.
    @Nullable
    private static Identifier target(JsonObject entry, String where, List<String> problems) {
        String text = entry.has("to") && entry.get("to").isJsonPrimitive() ? entry.get("to").getAsString() : "";
        if (text.isBlank()) {
            problems.add(where + " has no target (\"to\") yet, so it is ignored");
            return null;
        }

        return id(entry, "to", where, problems);
    }
}
