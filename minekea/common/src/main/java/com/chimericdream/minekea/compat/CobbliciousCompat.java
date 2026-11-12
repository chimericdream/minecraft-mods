package com.chimericdream.minekea.compat;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.chimericdream.minekea.ModInfo.MOD_ID;

/**
 * Cobblicious now owns the basalt brick blocks, the warped nether brick blocks, and Warped Wart.
 * When Cobblicious is loaded, Minekea does not register its own copies of them.
 * <p>
 * Minekea blocks that are made from these blocks (beams, covers, vertical stairs and slabs,
 * compressed blocks, and the warped nether brick bookshelves) stay. They use the Cobblicious block
 * as their ingredient instead.
 * <p>
 * Recipes, loot tables, and tags are generated JSON, so the Fabric data generator marks them with
 * load conditions. See {@code CobbliciousDataCompat}.
 */
public final class CobbliciousCompat {
    public static final String COBBLICIOUS_ID = "cobblicious";

    /**
     * Minekea id to the Cobblicious id that replaces it. The Cobblicious id is the same for the
     * block and for its item.
     */
    private static final Map<Identifier, Identifier> REPLACEMENTS = new LinkedHashMap<>();

    private static Boolean active = null;

    static {
        for (String material : List.of(
            "basalt_bricks",
            "cracked_basalt_bricks",
            "crimson_basalt_bricks",
            "mossy_basalt_bricks",
            "warped_basalt_bricks",
            "warped_nether_bricks"
        )) {
            replace("building/general/" + material, material);
            replace("building/stairs/" + material, material + "_stairs");
            replace("building/slabs/" + material, material + "_slab");
            replace("building/walls/" + material, material + "_wall");
        }

        replace("building/general/chiseled_basalt_bricks", "chiseled_basalt_bricks");

        replace("crops/warped_wart", "warped_wart");
        replace("crops/warped_wart/block", "warped_wart");
    }

    private CobbliciousCompat() {
    }

    private static void replace(String minekeaPath, String cobbliciousPath) {
        REPLACEMENTS.put(
            Identifier.fromNamespaceAndPath(MOD_ID, minekeaPath),
            Identifier.fromNamespaceAndPath(COBBLICIOUS_ID, cobbliciousPath)
        );
    }

    /**
     * True when Minekea must skip the blocks that Cobblicious provides.
     * <p>
     * The data generator always sees {@code false}. It must generate the Minekea blocks, and the
     * generated data carries the load conditions that turn it off at run time.
     */
    public static boolean isActive() {
        if (active == null) {
            active = System.getProperty("fabric-api.datagen") == null && Platform.isModLoaded(COBBLICIOUS_ID);
        }

        return active;
    }

    /**
     * Every Minekea id that Cobblicious replaces, with the Cobblicious id that replaces it.
     */
    public static Map<Identifier, Identifier> replacements() {
        return Collections.unmodifiableMap(REPLACEMENTS);
    }

    /**
     * The Cobblicious id for a Minekea id, when {@link #isActive()}. Otherwise the same id.
     */
    public static Identifier resolve(Identifier minekeaId) {
        if (!isActive()) {
            return minekeaId;
        }

        return counterpart(minekeaId);
    }

    public static Identifier counterpart(Identifier minekeaId) {
        Identifier id = REPLACEMENTS.get(minekeaId);

        if (id == null) {
            throw new IllegalArgumentException("Cobblicious does not replace " + minekeaId);
        }

        return id;
    }

    /**
     * A lazy reference to the Cobblicious block that replaces a Minekea block. Call
     * {@code get()} only after Cobblicious has registered its blocks.
     */
    public static RegistrySupplier<Block> block(Identifier minekeaId) {
        return RegistrarManager.get(COBBLICIOUS_ID).get(Registries.BLOCK).delegate(counterpart(minekeaId));
    }

    public static RegistrySupplier<Item> item(Identifier minekeaId) {
        return RegistrarManager.get(COBBLICIOUS_ID).get(Registries.ITEM).delegate(counterpart(minekeaId));
    }
}
