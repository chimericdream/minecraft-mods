package com.chimericdream.cobblicious.compat;

import com.chimericdream.forwardingaddress.api.Migrations;
import dev.architectury.platform.Platform;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.Identifier;

import static com.chimericdream.cobblicious.CobbliciousMod.LOGGER;
import static com.chimericdream.cobblicious.ModInfo.MOD_ID;

/**
 * Cobblicious took these blocks over from Minekea. This class tells Forwarding Address to convert the
 * old Minekea blocks and items in existing worlds to the Cobblicious ones.
 * <p>
 * Minekea does not register these blocks when Cobblicious is loaded, so each migration is a plain
 * rename. Both versions of a block have the same block state properties, so Forwarding Address copies
 * them by name.
 * <p>
 * This class references the Forwarding Address API. Load it only when {@link #isAvailable()} is true.
 */
public final class MinekeaMigrations {
    private static final String FORWARDING_ADDRESS_ID = "forwardingaddress";
    private static final String MINEKEA_ID = "minekea";

    private MinekeaMigrations() {
    }

    public static boolean isAvailable() {
        return Platform.isModLoaded(FORWARDING_ADDRESS_ID);
    }

    public static void register() {
        Map<Identifier, Identifier> blocks = new LinkedHashMap<>();

        for (String material : List.of(
            "basalt_bricks",
            "cracked_basalt_bricks",
            "crimson_basalt_bricks",
            "mossy_basalt_bricks",
            "warped_basalt_bricks",
            "warped_nether_bricks"
        )) {
            blocks.put(minekea("building/general/" + material), cobblicious(material));
            blocks.put(minekea("building/stairs/" + material), cobblicious(material + "_stairs"));
            blocks.put(minekea("building/slabs/" + material), cobblicious(material + "_slab"));
            blocks.put(minekea("building/walls/" + material), cobblicious(material + "_wall"));
        }

        blocks.put(minekea("building/general/chiseled_basalt_bricks"), cobblicious("chiseled_basalt_bricks"));

        // The Warped Wart block and item have different Minekea ids, and they share one Cobblicious id.
        blocks.put(minekea("crops/warped_wart/block"), cobblicious("warped_wart"));

        blocks.forEach((from, to) -> Migrations.block(from).to(to).register());

        // A block migration converts only the item with the same id as the block, so the wart item
        // needs its own migration.
        Migrations.item(minekea("crops/warped_wart")).to(cobblicious("warped_wart")).register();

        LOGGER.info("Registered {} Minekea migrations with Forwarding Address", blocks.size() + 1);
    }

    private static Identifier minekea(String path) {
        return Identifier.fromNamespaceAndPath(MINEKEA_ID, path);
    }

    private static Identifier cobblicious(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
