package com.chimericdream.opus.core.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A recipe widget block. The page can describe the recipe itself ("tier 1": no server data needed), name a recipe
 * by id, or both:
 *
 * <pre>
 * recipe: minecraft:hopper     # optional id; PLANNED, currently unused
 * type: crafting_shaped        # optional when 'pattern' is present
 * pattern: ["I I", "ICI", " I "]
 * key: { I: minecraft:iron_ingot, C: minecraft:chest }
 * result: minecraft:hopper
 * count: 1
 * </pre>
 *
 * <p>The planned lookup rule, not implemented yet: if an id is present, ask the server for that recipe and draw
 * what it returns; if the server does not have it, fall back to the inline definition. A block with only an id
 * parses to {@link Kind#BY_ID} and has nothing to fall back to. Until the lookup exists the id is kept but ignored,
 * so books can already include it.
 *
 * @param recipeId the optional recipe id, normalised to {@code namespace:path}; {@code null} when absent
 * @param grid     crafting: 9 cells (row-major 3x3, top-left aligned); smithing: template, base, addition;
 *                 otherwise a single input cell
 * @param result   {@code null} only for {@link Kind#BY_ID}
 */
public record RecipeSpec(
    Kind kind,
    String recipeId,
    List<Ingredient> grid,
    int patternWidth,
    int patternHeight,
    String result,
    int count,
    Integer cookingTime,
    Double experience
) {
    public enum Kind {
        CRAFTING_SHAPED,
        CRAFTING_SHAPELESS,
        SMELTING,
        BLASTING,
        SMOKING,
        CAMPFIRE_COOKING,
        STONECUTTING,
        SMITHING,
        BY_ID
    }

    /** One slot. Several alternatives are cycled by the renderer, as the recipe book does for tags. */
    public record Ingredient(List<String> alternatives) {
        public static final Ingredient EMPTY = new Ingredient(List.of());

        public boolean isEmpty() {
            return alternatives.isEmpty();
        }
    }

    public static RecipeSpec parse(Map<String, Object> props) throws SpecException {
        Object byId = props.containsKey("recipe") ? props.get("recipe") : props.get("id");
        boolean hasInline = props.containsKey("pattern") || props.containsKey("ingredients")
            || props.containsKey("ingredient") || props.containsKey("base");
        if (byId != null && !hasInline) {
            return new RecipeSpec(Kind.BY_ID, Ids.normalize(String.valueOf(byId)), List.of(), 0, 0, null, 1, null, null);
        }

        Kind kind = kindOf(props);
        String result = Ids.normalize(string(props, "result"));
        int count = intOf(props, "count", 1);
        if (count < 1 || count > 99) {
            throw new SpecException("'count' must be between 1 and 99");
        }

        RecipeSpec inline = switch (kind) {
            case CRAFTING_SHAPED -> shaped(props, result, count);
            case CRAFTING_SHAPELESS -> shapeless(props, result, count);
            case SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING -> new RecipeSpec(
                kind, null, List.of(ingredient(props.get("ingredient"), "ingredient")), 1, 1, result, count,
                props.containsKey("cooking_time") ? intOf(props, "cooking_time", 200) : null,
                props.get("experience") instanceof Number n ? n.doubleValue() : null
            );
            case STONECUTTING -> new RecipeSpec(
                kind, null, List.of(ingredient(props.get("ingredient"), "ingredient")), 1, 1, result, count, null, null
            );
            case SMITHING -> new RecipeSpec(
                kind, null,
                List.of(
                    ingredient(props.get("template"), "template"),
                    ingredient(props.get("base"), "base"),
                    ingredient(props.get("addition"), "addition")
                ),
                3, 1, result, count, null, null
            );
            case BY_ID -> throw new SpecException("recipe needs either 'recipe: <id>' or an inline definition");
        };

        return byId == null ? inline : inline.withRecipeId(Ids.normalize(String.valueOf(byId)));
    }

    /** The same recipe carrying the given id (used when a block has both an id and an inline definition). */
    public RecipeSpec withRecipeId(String id) {
        return new RecipeSpec(kind, id, grid, patternWidth, patternHeight, result, count, cookingTime, experience);
    }

    private static Kind kindOf(Map<String, Object> props) throws SpecException {
        Object type = props.get("type");
        if (type == null) {
            if (props.containsKey("pattern")) {
                return Kind.CRAFTING_SHAPED;
            }
            if (props.containsKey("ingredients")) {
                return Kind.CRAFTING_SHAPELESS;
            }
            throw new SpecException("recipe needs a 'type' (or a 'pattern' / 'ingredients' list)");
        }

        String name = String.valueOf(type).toLowerCase().replace("minecraft:", "");
        return switch (name) {
            case "crafting_shaped", "shaped" -> Kind.CRAFTING_SHAPED;
            case "crafting_shapeless", "shapeless" -> Kind.CRAFTING_SHAPELESS;
            case "smelting" -> Kind.SMELTING;
            case "blasting" -> Kind.BLASTING;
            case "smoking" -> Kind.SMOKING;
            case "campfire_cooking", "campfire" -> Kind.CAMPFIRE_COOKING;
            case "stonecutting" -> Kind.STONECUTTING;
            case "smithing", "smithing_transform" -> Kind.SMITHING;
            default -> throw new SpecException("unknown recipe type '" + type + "'");
        };
    }

    private static RecipeSpec shaped(Map<String, Object> props, String result, int count) throws SpecException {
        if (!(props.get("pattern") instanceof List<?> rows) || rows.isEmpty() || rows.size() > 3) {
            throw new SpecException("'pattern' must be a list of 1-3 rows");
        }
        if (!(props.get("key") instanceof Map<?, ?> rawKey)) {
            throw new SpecException("a shaped recipe needs a 'key' mapping each pattern character to an ingredient");
        }

        Map<Character, Ingredient> key = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : rawKey.entrySet()) {
            String k = String.valueOf(e.getKey());
            if (k.length() != 1 || k.equals(" ")) {
                throw new SpecException("key '" + k + "' must be a single non-space character");
            }
            key.put(k.charAt(0), ingredient(e.getValue(), "key '" + k + "'"));
        }

        int width = 0;
        for (Object row : rows) {
            width = Math.max(width, String.valueOf(row).length());
        }
        if (width < 1 || width > 3) {
            throw new SpecException("each pattern row must be 1-3 characters wide");
        }

        Ingredient[] cells = new Ingredient[9];
        java.util.Arrays.fill(cells, Ingredient.EMPTY);
        Set<Character> used = new HashSet<>();
        for (int r = 0; r < rows.size(); r++) {
            String row = String.valueOf(rows.get(r));
            for (int c = 0; c < row.length(); c++) {
                char ch = row.charAt(c);
                if (ch == ' ') {
                    continue;
                }
                Ingredient ingredient = key.get(ch);
                if (ingredient == null) {
                    throw new SpecException("pattern uses '" + ch + "' but 'key' does not define it");
                }
                used.add(ch);
                cells[r * 3 + c] = ingredient;
            }
        }
        for (Character defined : key.keySet()) {
            if (!used.contains(defined)) {
                throw new SpecException("key '" + defined + "' is defined but never used in 'pattern'");
            }
        }

        return new RecipeSpec(Kind.CRAFTING_SHAPED, null, List.of(cells), width, rows.size(), result, count, null, null);
    }

    private static RecipeSpec shapeless(Map<String, Object> props, String result, int count) throws SpecException {
        if (!(props.get("ingredients") instanceof List<?> list) || list.isEmpty() || list.size() > 9) {
            throw new SpecException("'ingredients' must be a list of 1-9 entries");
        }

        Ingredient[] cells = new Ingredient[9];
        java.util.Arrays.fill(cells, Ingredient.EMPTY);
        for (int i = 0; i < list.size(); i++) {
            cells[i] = ingredient(list.get(i), "ingredients[" + i + "]");
        }

        int width = Math.min(3, list.size());
        int height = (list.size() + 2) / 3;
        return new RecipeSpec(Kind.CRAFTING_SHAPELESS, null, List.of(cells), width, height, result, count, null, null);
    }

    private static Ingredient ingredient(Object value, String what) throws SpecException {
        if (value == null) {
            throw new SpecException("missing " + what);
        }

        List<String> alternatives = new ArrayList<>();
        if (value instanceof List<?> list) {
            for (Object o : list) {
                alternatives.add(Ids.normalize(String.valueOf(o)));
            }
        } else {
            alternatives.add(Ids.normalize(String.valueOf(value)));
        }

        if (alternatives.isEmpty()) {
            throw new SpecException(what + " has no ingredients");
        }

        return new Ingredient(List.copyOf(alternatives));
    }

    private static String string(Map<String, Object> props, String key) throws SpecException {
        Object value = props.get(key);
        if (value == null) {
            throw new SpecException("missing '" + key + "'");
        }

        return String.valueOf(value);
    }

    private static int intOf(Map<String, Object> props, String key, int fallback) throws SpecException {
        Object value = props.get(key);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }

        throw new SpecException("'" + key + "' must be a number");
    }
}
