package com.chimericdream.lib.trims;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Writes a mod's override of vanilla's {@code assets/minecraft/atlases/armor_trims.json} and
 * {@code items.json}, appending the mod's own {@link TrimMaterialConfig} palette permutations to
 * vanilla's full texture list.
 *
 * <p>Neither Fabric nor vanilla datagen expose a provider for these files (vanilla's own
 * {@code AtlasProvider}/{@code ItemModelGenerators.TRIM_MATERIAL_MODELS} exist only inside Mojang's
 * internal asset-build entrypoint, and are hardcoded to vanilla's own materials regardless), so this
 * class hand-rolls the same boilerplate a mod would otherwise have to restate by hand in every atlas
 * override: the full vanilla texture list, plus one {@code permutations} entry per material.
 *
 * <p>Because a resource pack's {@code armor_trims.json}/{@code items.json} fully replaces vanilla's
 * (there is no cross-pack merge for atlas sources), each consuming mod must still restate the whole
 * vanilla list itself if it wants its own trim to render — this class centralizes that list in one
 * place instead of two full JSON files per mod.
 *
 * <p><b>Caveat:</b> {@link #VANILLA_TRIM_PATTERNS} and {@link #VANILLA_ITEM_TRIM_TEXTURES} have no
 * datagen-time source to read from (Fabric doesn't expose vanilla's list either) and must be updated
 * by hand if a future Minecraft version adds or renames trim patterns.
 */
public class ArmorTrimAtlasProvider implements DataProvider {
    private static final String PALETTE_KEY = "minecraft:trims/color_palettes/trim_palette";

    /** Mirrors {@code assets/minecraft/atlases/armor_trims.json}'s vanilla pattern list, MC 26.2. */
    private static final List<String> VANILLA_TRIM_PATTERNS = List.of(
        "sentry", "dune", "coast", "wild", "ward", "eye", "vex", "tide", "snout", "rib",
        "spire", "wayfinder", "shaper", "silence", "raiser", "host", "flow", "bolt"
    );

    /** Mirrors {@code assets/minecraft/atlases/items.json}'s vanilla texture list, MC 26.2. */
    private static final List<String> VANILLA_ITEM_TRIM_TEXTURES = List.of(
        "minecraft:trims/items/leggings_trim",
        "minecraft:trims/items/chestplate_trim",
        "minecraft:trims/items/helmet_trim",
        "minecraft:trims/items/boots_trim"
    );

    private final PackOutput.PathProvider pathProvider;
    private final List<TrimMaterialConfig> materials;

    public ArmorTrimAtlasProvider(PackOutput output, List<TrimMaterialConfig> materials) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
        this.materials = materials;
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput writer) {
        JsonObject armorTrims = buildAtlas(humanoidTrimTextures());
        JsonObject items = buildAtlas(VANILLA_ITEM_TRIM_TEXTURES);

        return CompletableFuture.allOf(
            DataProvider.saveStable(writer, armorTrims, pathProvider.file(Identifier.withDefaultNamespace("armor_trims"), "json")),
            DataProvider.saveStable(writer, items, pathProvider.file(Identifier.withDefaultNamespace("items"), "json"))
        );
    }

    private static List<String> humanoidTrimTextures() {
        return VANILLA_TRIM_PATTERNS.stream()
            .flatMap(pattern -> Stream.of(
                "minecraft:trims/entity/humanoid/" + pattern,
                "minecraft:trims/entity/humanoid_leggings/" + pattern
            ))
            .toList();
    }

    private JsonObject buildAtlas(List<String> vanillaTextures) {
        JsonArray textures = new JsonArray();
        vanillaTextures.forEach(textures::add);

        JsonObject permutations = new JsonObject();
        for (TrimMaterialConfig material : materials) {
            permutations.addProperty(material.assetName(), material.paletteTextureId());
        }

        JsonObject source = new JsonObject();
        source.addProperty("type", "minecraft:paletted_permutations");
        source.add("textures", textures);
        source.addProperty("palette_key", PALETTE_KEY);
        source.add("permutations", permutations);

        JsonArray sources = new JsonArray();
        sources.add(source);

        JsonObject root = new JsonObject();
        root.add("sources", sources);

        return root;
    }

    @Override
    public @NotNull String getName() {
        return "Armor Trim Atlases";
    }
}
