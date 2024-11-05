package com.chimericdream.miniblockmerchants.item;

import com.chimericdream.miniblockmerchants.ModInfo;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import static com.chimericdream.miniblockmerchants.registry.ModRegistries.registerItem;

public class ModItems {
    public static final RegistrySupplier<Item> ANCIENT_SHELL_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "ancient_shell"),
        () -> new VillagerConversionItem("ancient_shell", "mm_oceanographer")
    );
    public static final RegistrySupplier<Item> BOOK_OF_RITUALS_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "book_of_rituals"),
        () -> new VillagerConversionItem("book_of_rituals", "mm_ritualist")
    );
    public static final RegistrySupplier<Item> BUDDING_CACTUS_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "budding_cactus"),
        () -> new VillagerConversionItem("budding_cactus", "mm_eremologist")
    );
    public static final RegistrySupplier<Item> CRYSTAL_PHIAL_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "crystal_phial"),
        () -> new VillagerConversionItem("crystal_phial", "mm_alchemist")
    );
    public static final RegistrySupplier<Item> CULTIVATED_SAPLING_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "cultivated_sapling"),
        () -> new VillagerConversionItem("cultivated_sapling", "mm_arboriculturist")
    );
    public static final RegistrySupplier<Item> DRENCHED_SCORE_SHEET_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "drenched_score_sheet"),
        () -> new VillagerConversionItem("drenched_score_sheet", "mm_gamemaster")
    );
    public static final RegistrySupplier<Item> ENCHANTED_RED_DELICIOUS_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "enchanted_red_delicious"),
        () -> new VillagerConversionItem("enchanted_red_delicious", "mm_pomologist")
    );
    public static final RegistrySupplier<Item> ENDLESS_BOOKSHELF_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "endless_bookshelf"),
        () -> new VillagerConversionItem("endless_bookshelf", "mm_furnisher")
    );
    public static final RegistrySupplier<Item> FINE_THREAD_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "fine_thread"),
        () -> new VillagerConversionItem("fine_thread", "mm_tailor")
    );
    public static final RegistrySupplier<Item> FORGOTTEN_SCRAP_METAL_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "forgotten_scrap_metal"),
        () -> new VillagerConversionItem("forgotten_scrap_metal", "mm_steampunker")
    );
    public static final RegistrySupplier<Item> FRAGRANT_FLOWER_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "fragrant_flower"),
        () -> new VillagerConversionItem("fragrant_flower", "mm_horticulturist")
    );
    public static final RegistrySupplier<Item> GALILEAN_SPYGLASS_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "galilean_spyglass"),
        () -> new VillagerConversionItem("galilean_spyglass", "mm_astronomer")
    );
    public static final RegistrySupplier<Item> MASTERCRAFTED_IRON_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "mastercrafted_iron"),
        () -> new VillagerConversionItem("mastercrafted_iron", "mm_blacksmith")
    );
    public static final RegistrySupplier<Item> MIXOLOGY_STATION_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "mixology_station"),
        () -> new VillagerConversionItem("mixology_station", "mm_bartender")
    );
    public static final RegistrySupplier<Item> OVERGROWN_CARROT_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "overgrown_carrot"),
        () -> new VillagerConversionItem("overgrown_carrot", "mm_olericulturist")
    );
    public static final RegistrySupplier<Item> PRISMATIC_HONEYCOMB_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "prismatic_honeycomb"),
        () -> new VillagerConversionItem("prismatic_honeycomb", "mm_beekeeper")
    );
    public static final RegistrySupplier<Item> PURE_GOLD_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "pure_gold"),
        () -> new VillagerConversionItem("pure_gold", "mm_mineralogist")
    );
    public static final RegistrySupplier<Item> RADIATING_REDSTONE_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "radiating_redstone"),
        () -> new VillagerConversionItem("radiating_redstone", "mm_engineer")
    );
    public static final RegistrySupplier<Item> ROTTING_RECYCLING_BIN_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "rotting_recycling_bin"),
        () -> new VillagerConversionItem("rotting_recycling_bin", "mm_recycler")
    );
    public static final RegistrySupplier<Item> SCULPTING_CLAY_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "sculpting_clay"),
        () -> new VillagerConversionItem("sculpting_clay", "mm_sculptor")
    );
    public static final RegistrySupplier<Item> SHIMMERING_WHEAT_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "shimmering_wheat"),
        () -> new VillagerConversionItem("shimmering_wheat", "mm_baker")
    );
    public static final RegistrySupplier<Item> SOAKED_VILLAGER_PLUSHIE_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "soaked_villager_plushie"),
        () -> new VillagerConversionItem("soaked_villager_plushie", "mm_plushie_maniac")
    );
    public static final RegistrySupplier<Item> SPARKLING_BLAZE_POWDER_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "sparkling_blaze_powder"),
        () -> new VillagerConversionItem("sparkling_blaze_powder", "mm_netherographer")
    );
    public static final RegistrySupplier<Item> STABILIZED_EXPLOSION_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "stabilized_explosion"),
        () -> new VillagerConversionItem("stabilized_explosion", "mm_griefer")
    );
    public static final RegistrySupplier<Item> UNUSUALLY_DENSE_ROCK_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "unusually_dense_rock"),
        () -> new VillagerConversionItem("unusually_dense_rock", "mm_petrologist")
    );
    public static final RegistrySupplier<Item> WAGYU_BEEF_ITEM = registerItem(
        Identifier.of(ModInfo.MOD_ID, "wagyu_beef"),
        () -> new VillagerConversionItem("wagyu_beef", "mm_chef")
    );

    public static void init() {
    }
}
