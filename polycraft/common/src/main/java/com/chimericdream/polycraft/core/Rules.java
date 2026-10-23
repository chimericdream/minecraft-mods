package com.chimericdream.polycraft.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * A pack author's preferences, from {@code config/polycraft/rules.json} or a datapack:
 * <pre>
 * {
 *   "priority": ["minecraft:oak_planks", "othermod:planks_x4"],
 *   "priorityNamespaces": ["othermod"],
 *   "exclude": ["badmod:duplicate_recipe"],
 *   "groups": { "&lt;group key or recipe id&gt;": { "default": "mod:recipe_id" } }
 * }
 * </pre>
 * Bad entries are reported through the warning callback and skipped; a rules file never stops the game.
 */
public record Rules(List<String> priority, List<String> priorityNamespaces, Set<String> exclude, Map<String, String> groupDefaults) {
    public static final Rules EMPTY = new Rules(List.of(), List.of(), Set.of(), Map.of());

    private static final Pattern ID = Pattern.compile("[a-z0-9_.-]+:[a-z0-9_./-]+");
    private static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_.-]+");

    public static Rules parse(String json, Consumer<String> warn) {
        JsonObject root;
        try {
            JsonElement parsed = JsonParser.parseString(json);
            if (!parsed.isJsonObject()) {
                warn.accept("rules must be a JSON object; ignoring the file");
                return EMPTY;
            }

            root = parsed.getAsJsonObject();
        } catch (JsonParseException e) {
            warn.accept("rules are not valid JSON (" + e.getMessage() + "); ignoring the file");
            return EMPTY;
        }

        List<String> priority = new ArrayList<>();
        List<String> priorityNamespaces = new ArrayList<>();
        Set<String> exclude = new LinkedHashSet<>();
        Map<String, String> groups = new LinkedHashMap<>();

        for (String key : root.keySet()) {
            JsonElement value = root.get(key);
            switch (key) {
                case "priority" -> readIds(value, "priority", warn, priority::add);
                case "priorityNamespaces" -> readNamespaces(value, "priorityNamespaces", warn, priorityNamespaces::add);
                case "exclude" -> readIds(value, "exclude", warn, exclude::add);
                case "groups" -> readGroups(value, warn, groups);
                default -> warn.accept("unknown rules key '" + key + "'; ignoring it");
            }
        }

        return new Rules(List.copyOf(priority), List.copyOf(priorityNamespaces), Set.copyOf(exclude), Map.copyOf(groups));
    }

    /**
     * Combines two rule sets; this one takes precedence. Priorities are listed this one first, excludes are added
     * together, and a group default in this one beats the same key in {@code other}.
     */
    public Rules merge(Rules other) {
        List<String> mergedPriority = new ArrayList<>(priority);
        for (String id : other.priority) {
            if (!mergedPriority.contains(id)) {
                mergedPriority.add(id);
            }
        }

        List<String> mergedNamespaces = new ArrayList<>(priorityNamespaces);
        for (String namespace : other.priorityNamespaces) {
            if (!mergedNamespaces.contains(namespace)) {
                mergedNamespaces.add(namespace);
            }
        }

        Set<String> mergedExclude = new LinkedHashSet<>(exclude);
        mergedExclude.addAll(other.exclude);

        Map<String, String> mergedGroups = new LinkedHashMap<>(other.groupDefaults);
        mergedGroups.putAll(groupDefaults);

        return new Rules(List.copyOf(mergedPriority), List.copyOf(mergedNamespaces), Set.copyOf(mergedExclude), Map.copyOf(mergedGroups));
    }

    public boolean isExcluded(String recipeId) {
        return exclude.contains(recipeId);
    }

    /**
     * The recipes to prefer for one group, most preferred first: a default given for the group key, then a default
     * given under the id of any of its options, then the global priority list.
     */
    public List<String> packOrder(String groupKey, List<String> optionIds) {
        List<String> order = new ArrayList<>();

        String forGroup = groupDefaults.get(groupKey);
        if (forGroup != null) {
            order.add(forGroup);
        }

        for (String optionId : optionIds) {
            String forRecipe = groupDefaults.get(optionId);
            if (forRecipe != null && !order.contains(forRecipe)) {
                order.add(forRecipe);
            }
        }

        for (String id : priority) {
            if (!order.contains(id)) {
                order.add(id);
            }
        }

        for (String namespace : priorityNamespaces) {
            String prefix = namespace + ":";
            for (String optionId : optionIds) {
                if (optionId.startsWith(prefix) && !order.contains(optionId)) {
                    order.add(optionId);
                }
            }
        }

        return order;
    }

    private static void readNamespaces(JsonElement value, String name, Consumer<String> warn, Consumer<String> sink) {
        if (!value.isJsonArray()) {
            warn.accept("'" + name + "' must be a list of namespaces; ignoring it");
            return;
        }

        for (JsonElement entry : value.getAsJsonArray()) {
            if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString() && NAMESPACE.matcher(entry.getAsString()).matches()) {
                sink.accept(entry.getAsString());
            } else {
                warn.accept("'" + name + "' has an entry that is not a namespace: " + entry + "; skipping it");
            }
        }
    }

    private static void readIds(JsonElement value, String name, Consumer<String> warn, Consumer<String> sink) {
        if (!value.isJsonArray()) {
            warn.accept("'" + name + "' must be a list of recipe ids; ignoring it");
            return;
        }

        for (JsonElement entry : value.getAsJsonArray()) {
            if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString() && ID.matcher(entry.getAsString()).matches()) {
                sink.accept(entry.getAsString());
            } else {
                warn.accept("'" + name + "' has an entry that is not a recipe id: " + entry + "; skipping it");
            }
        }
    }

    private static void readGroups(JsonElement value, Consumer<String> warn, Map<String, String> sink) {
        if (!value.isJsonObject()) {
            warn.accept("'groups' must be an object; ignoring it");
            return;
        }

        for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
            JsonElement settings = entry.getValue();
            JsonElement def = settings.isJsonObject() ? settings.getAsJsonObject().get("default") : null;
            if (def != null && def.isJsonPrimitive() && def.getAsJsonPrimitive().isString() && ID.matcher(def.getAsString()).matches()) {
                sink.put(entry.getKey(), def.getAsString());
            } else {
                warn.accept("group '" + entry.getKey() + "' needs a \"default\" recipe id; skipping it");
            }
        }
    }
}
