package com.chimericdream.minekea.fabric.data;

import com.chimericdream.minekea.compat.CobbliciousCompat;
import com.google.common.hash.HashCode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;

import static com.chimericdream.minekea.ModInfo.MOD_ID;

/**
 * Wraps a data provider so that the data it writes works with and without Cobblicious.
 * <p>
 * Minekea does not register the blocks that Cobblicious replaces when Cobblicious is loaded (see
 * {@link CobbliciousCompat}). The data generator always sees those blocks, because Cobblicious is
 * not loaded while it runs. This class changes the generated JSON after the provider writes it:
 * <ul>
 *   <li><b>Recipes</b> that make a replaced item, and loot tables that mention a replaced block
 *   or item, get load conditions. They load only when Cobblicious is <i>not</i> loaded. The file
 *   carries a condition for Fabric and a condition for NeoForge, and each loader ignores the
 *   other key.</li>
 *   <li><b>Other recipes and advancements</b> that use a replaced item as an ingredient or
 *   criterion use the {@code minekea:cobblicious_swap/...} item tag instead. The tag holds the
 *   Minekea item and the Cobblicious item, and one of them is always absent.</li>
 *   <li><b>Tags</b> mark every replaced entry as optional, so a missing block does not stop the
 *   whole tag from loading.</li>
 * </ul>
 * Client assets (block states, models, translations) are not changed. They are harmless when the
 * block does not exist.
 */
public final class CobbliciousDataCompat implements DataProvider {
    private static final String FABRIC_CONDITIONS_KEY = "fabric:load_conditions";
    private static final String NEOFORGE_CONDITIONS_KEY = "neoforge:conditions";

    private static final Pattern DATA_PATH = Pattern.compile(".*/data/[^/]+/(recipe|advancement|loot_table|tags)/(.+)\\.json$");

    /** The Minekea ids that Cobblicious replaces, as strings. */
    private static final Set<String> REPLACED = new HashSet<>();
    /** The replaced ids that are items, as strings. */
    private static final Set<String> REPLACED_ITEMS = new HashSet<>();

    /**
     * Recipes that make something else from a replaced item, but that Cobblicious has its own copy of.
     * They are paths under {@code data/minekea/recipe}, without the extension.
     */
    private static final Set<String> DUPLICATED_BY_COBBLICIOUS = Set.of("warped_wart_block");

    /**
     * Recipes that turn a compressed Minekea block back into a replaced block. They stay when Cobblicious is
     * loaded, but they give the Cobblicious block. They are paths under {@code data/minekea/recipe}, without the
     * extension.
     */
    private static final Set<String> UNPACK_RECIPES = new HashSet<>();

    private static final String COBBLICIOUS_RECIPE_SUFFIX = "_cobblicious";

    /** Top-level recipe fields that never hold an ingredient. */
    private static final Set<String> RECIPE_FIELDS_TO_KEEP = Set.of("result", "type", "category", "group", "show_notification");

    static {
        List.of(
            "basalt_bricks",
            "cracked_basalt_bricks",
            "crimson_basalt_bricks",
            "mossy_basalt_bricks",
            "warped_basalt_bricks",
            "warped_nether_bricks"
        ).forEach(material -> UNPACK_RECIPES.add("building/general/" + material + "_from_compressed"));

        CobbliciousCompat.replacements().keySet().forEach(id -> {
            REPLACED.add(id.toString());

            // The warped wart plant block is the only replaced id that has no item of its own.
            if (!id.getPath().equals("crops/warped_wart/block")) {
                REPLACED_ITEMS.add(id.toString());
            }
        });
    }

    private final DataProvider delegate;

    public CobbliciousDataCompat(DataProvider delegate) {
        this.delegate = delegate;
    }

    /** The item tag that holds a replaced item and the Cobblicious item that replaces it. */
    public static Identifier swapTag(Identifier minekeaId) {
        return Identifier.fromNamespaceAndPath(MOD_ID, "cobblicious_swap/" + minekeaId.getPath());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return delegate.run(new RewritingOutput(output));
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    private record RewritingOutput(CachedOutput output) implements CachedOutput {
        @Override
        public void writeIfNeeded(Path path, byte[] data, HashCode hash) throws IOException {
            Matcher matcher = DATA_PATH.matcher(path.toString().replace('\\', '/'));

            if (!matcher.matches()) {
                output.writeIfNeeded(path, data, hash);

                return;
            }

            JsonElement json = JsonParser.parseString(new String(data, StandardCharsets.UTF_8));

            String type = matcher.group(1);
            String id = matcher.group(2);

            if (!json.isJsonObject()) {
                output.writeIfNeeded(path, data, hash);

                return;
            }

            // Written before the original is changed, because the changes below are made in place.
            if (type.equals("recipe") && UNPACK_RECIPES.contains(id)) {
                String fileName = path.getFileName().toString();
                Path variantPath = path.resolveSibling(fileName.substring(0, fileName.length() - ".json".length()) + COBBLICIOUS_RECIPE_SUFFIX + ".json");

                DataProvider.saveStable(output, cobbliciousVariant(json.getAsJsonObject()), variantPath).join();
            }

            if (!rewrite(type, id, json.getAsJsonObject())) {
                output.writeIfNeeded(path, data, hash);

                return;
            }

            // Writes with the same key order and indentation as every other generated file.
            DataProvider.saveStable(output, json, path).join();
        }
    }

    /**
     * Changes the JSON when it needs a change.
     *
     * @return true when the JSON changed
     */
    private static boolean rewrite(String type, String path, JsonObject json) {
        return switch (type) {
            case "recipe" -> rewriteRecipe(path, json);
            case "advancement" -> rewriteAdvancement(json);
            case "loot_table" -> {
                if (!mentionsReplaced(json)) {
                    yield false;
                }

                addConditions(json, false);

                yield true;
            }
            case "tags" -> optionalizeTagEntries(json);
            default -> false;
        };
    }

    private static boolean rewriteAdvancement(JsonObject json) {
        boolean changed = false;

        // A criterion names the recipe it unlocks by id, and a recipe has the same id as the item it makes.
        // Only item predicates are item ids.
        JsonElement criteria = json.get("criteria");

        if (criteria != null) {
            changed = swapIds(criteria, "", true);
        }

        // The unpack recipes have a second recipe that works with Cobblicious. Unlock both.
        JsonElement rewards = json.get("rewards");

        if (rewards != null && rewards.isJsonObject() && rewards.getAsJsonObject().get("recipes") instanceof JsonArray recipes) {
            for (JsonElement recipe : List.copyOf(recipes.asList())) {
                String id = recipe.getAsString();

                if (id.startsWith(MOD_ID + ":") && UNPACK_RECIPES.contains(id.substring(MOD_ID.length() + 1))) {
                    recipes.add(id + COBBLICIOUS_RECIPE_SUFFIX);
                    changed = true;
                }
            }
        }

        return changed;
    }

    /**
     * The same recipe, with the Cobblicious item as the result, that loads only when Cobblicious is loaded.
     * Returns a copy: the original is changed next.
     */
    private static JsonObject cobbliciousVariant(JsonObject recipe) {
        JsonObject variant = recipe.deepCopy();
        JsonObject result = variant.getAsJsonObject("result");
        Identifier replaced = Identifier.parse(result.get("id").getAsString());

        result.addProperty("id", CobbliciousCompat.counterpart(replaced).toString());
        addConditions(variant, true);

        return variant;
    }

    private static boolean rewriteRecipe(String path, JsonObject json) {
        if (DUPLICATED_BY_COBBLICIOUS.contains(path) || resultIsReplaced(json)) {
            addConditions(json, false);

            return true;
        }

        boolean changed = false;

        for (Map.Entry<String, JsonElement> entry : List.copyOf(json.entrySet())) {
            if (RECIPE_FIELDS_TO_KEEP.contains(entry.getKey())) {
                continue;
            }

            changed |= rewriteField(json, entry.getKey());
        }

        return changed;
    }

    private static boolean resultIsReplaced(JsonObject recipe) {
        JsonElement result = recipe.get("result");

        if (result == null) {
            return false;
        }

        if (result.isJsonObject()) {
            result = result.getAsJsonObject().get("id");
        }

        return result != null && result.isJsonPrimitive() && REPLACED.contains(result.getAsString());
    }

    /** True when any string in the JSON is the id of a replaced block or item. */
    private static boolean mentionsReplaced(JsonElement json) {
        if (json.isJsonPrimitive()) {
            return json.getAsJsonPrimitive().isString() && REPLACED.contains(json.getAsString());
        }

        if (json.isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray()) {
                if (mentionsReplaced(element)) {
                    return true;
                }
            }
        }

        if (json.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                if (mentionsReplaced(entry.getValue())) {
                    return true;
                }
            }
        }

        return false;
    }

    /** Replaces replaced item ids with their swap tag in one top-level field. */
    private static boolean rewriteField(JsonObject json, String field) {
        JsonElement value = json.get(field);

        if (value == null) {
            return false;
        }

        if (isReplacedItem(value)) {
            json.add(field, swapTagReference(value));

            return true;
        }

        return swapIds(value, field, false);
    }

    private static boolean isReplacedItem(JsonElement json) {
        return json.isJsonPrimitive() && json.getAsJsonPrimitive().isString() && REPLACED_ITEMS.contains(json.getAsString());
    }

    private static JsonElement swapTagReference(JsonElement replacedItem) {
        return new JsonPrimitive("#" + swapTag(Identifier.parse(replacedItem.getAsString())));
    }

    /**
     * Replaces replaced item ids inside an array or an object with their swap tag, in place.
     *
     * @param parentKey    the key that holds {@code json}
     * @param itemsKeyOnly replace only the ids that are held by an {@code items} key
     * @return true when something was replaced
     */
    private static boolean swapIds(JsonElement json, String parentKey, boolean itemsKeyOnly) {
        boolean changed = false;

        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();

            for (int i = 0; i < array.size(); i += 1) {
                JsonElement element = array.get(i);

                if (isReplacedItem(element) && (!itemsKeyOnly || parentKey.equals("items"))) {
                    array.set(i, swapTagReference(element));
                    changed = true;
                } else {
                    changed |= swapIds(element, parentKey, itemsKeyOnly);
                }
            }
        }

        if (json.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                JsonElement element = entry.getValue();

                if (isReplacedItem(element) && (!itemsKeyOnly || entry.getKey().equals("items"))) {
                    entry.setValue(swapTagReference(element));
                    changed = true;
                } else {
                    changed |= swapIds(element, entry.getKey(), itemsKeyOnly);
                }
            }
        }

        return changed;
    }

    private static boolean optionalizeTagEntries(JsonObject json) {
        JsonElement values = json.get("values");

        if (values == null || !values.isJsonArray()) {
            return false;
        }

        boolean changed = false;
        JsonArray array = values.getAsJsonArray();

        for (int i = 0; i < array.size(); i += 1) {
            JsonElement entry = array.get(i);

            if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString() && REPLACED.contains(entry.getAsString())) {
                JsonObject optional = new JsonObject();
                optional.addProperty("id", entry.getAsString());
                optional.addProperty("required", false);

                array.set(i, optional);
                changed = true;
            }
        }

        return changed;
    }

    /**
     * @param whenLoaded true to load the file only when Cobblicious is loaded; false to load it only when
     *                   Cobblicious is not loaded
     */
    private static void addConditions(JsonObject json, boolean whenLoaded) {
        JsonObject fabricModLoaded = new JsonObject();
        fabricModLoaded.addProperty("condition", "fabric:all_mods_loaded");
        JsonArray modIds = new JsonArray();
        modIds.add(CobbliciousCompat.COBBLICIOUS_ID);
        fabricModLoaded.add("values", modIds);

        JsonArray fabricConditions = new JsonArray();

        if (whenLoaded) {
            fabricConditions.add(fabricModLoaded);
        } else {
            JsonObject fabricNot = new JsonObject();
            fabricNot.addProperty("condition", "fabric:not");
            fabricNot.add("value", fabricModLoaded);

            fabricConditions.add(fabricNot);
        }

        json.add(FABRIC_CONDITIONS_KEY, fabricConditions);

        JsonObject neoForgeModLoaded = new JsonObject();
        neoForgeModLoaded.addProperty("type", "neoforge:mod_loaded");
        neoForgeModLoaded.addProperty("modid", CobbliciousCompat.COBBLICIOUS_ID);

        JsonArray neoForgeConditions = new JsonArray();

        if (whenLoaded) {
            neoForgeConditions.add(neoForgeModLoaded);
        } else {
            JsonObject neoForgeNot = new JsonObject();
            neoForgeNot.addProperty("type", "neoforge:not");
            neoForgeNot.add("value", neoForgeModLoaded);

            neoForgeConditions.add(neoForgeNot);
        }

        json.add(NEOFORGE_CONDITIONS_KEY, neoForgeConditions);
    }
}
