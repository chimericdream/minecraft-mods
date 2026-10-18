package com.chimericdream.pannotiacompanion.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

import java.util.Map;
import java.util.Set;

import com.chimericdream.pannotiacompanion.ModInfo;

/**
 * Appends World of Pannotia's extra drops (mob heads, husk sand, dragon loot, extra shulker shells and a few
 * block tweaks) to vanilla loot tables. Every addition is a single new pool that nests one of our own
 * tables, so nothing is ever replaced and other mods' changes to the same tables survive.
 */
public final class PannotiaLootTables {
    private static final String VANILLA = "minecraft";

    /** Entity types whose vanilla loot table gets {@code pannotia/additions/<name>} appended. */
    private static final Set<String> ENTITY_TARGETS = Set.of(
        "allay", "armadillo", "axolotl", "bat", "bee", "blaze",
        "bogged", "breeze", "camel", "camel_husk", "cat", "cave_spider",
        "chicken", "cod", "copper_golem", "cow", "creaking", "creeper",
        "dolphin", "donkey", "drowned", "elder_guardian", "ender_dragon", "enderman",
        "endermite", "evoker", "fox", "frog", "ghast", "glow_squid",
        "goat", "guardian", "happy_ghast", "hoglin", "horse", "husk",
        "illusioner", "iron_golem", "llama", "magma_cube", "mooshroom", "mule",
        "nautilus", "ocelot", "panda", "parched", "parrot", "phantom",
        "pig", "piglin", "piglin_brute", "pillager", "polar_bear", "pufferfish",
        "rabbit", "ravager", "salmon", "sheep", "shulker", "silverfish",
        "skeleton", "skeleton_horse", "slime", "sniffer", "snow_golem", "spider",
        "squid", "stray", "strider", "sulfur_cube", "tadpole", "trader_llama",
        "tropical_fish", "turtle", "vex", "villager", "vindicator", "wandering_trader",
        "warden", "witch", "wither", "wolf", "zoglin", "zombie",
        "zombie_horse", "zombie_nautilus", "zombie_villager", "zombified_piglin"
    );

    /** Block loot tables that get {@code pannotia/blocks/<name>} appended. */
    private static final Set<String> BLOCK_TARGETS = Set.of(
        "jungle_leaves", "brown_mushroom_block", "red_mushroom_block"
    );

    private static final Map<String, String> PREFIXES = Map.of(
        "entities/", "pannotia/additions/",
        "blocks/", "pannotia/blocks/"
    );

    private PannotiaLootTables() {
    }

    /**
     * Returns the pool to append to {@code key}, or {@code null} when we do not touch that table.
     */
    public static LootPool.Builder additionFor(ResourceKey<LootTable> key) {
        Identifier id = key.identifier();
        if (!VANILLA.equals(id.getNamespace())) {
            return null;
        }

        String path = id.getPath();
        for (Map.Entry<String, String> prefix : PREFIXES.entrySet()) {
            if (!path.startsWith(prefix.getKey())) {
                continue;
            }

            String name = path.substring(prefix.getKey().length());
            Set<String> targets = prefix.getKey().equals("entities/") ? ENTITY_TARGETS : BLOCK_TARGETS;
            if (!targets.contains(name)) {
                return null;
            }

            return LootPool.lootPool().add(NestedLootTable.lootTableReference(
                ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, prefix.getValue() + name))
            ));
        }

        return null;
    }
}
