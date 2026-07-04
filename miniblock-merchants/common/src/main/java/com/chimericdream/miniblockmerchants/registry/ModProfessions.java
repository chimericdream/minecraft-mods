package com.chimericdream.miniblockmerchants.registry;

import com.chimericdream.miniblockmerchants.MiniblockMerchantsMod;
import com.chimericdream.miniblockmerchants.ModInfo;
import com.chimericdream.miniblockmerchants.registry.trades.*;
import com.google.common.collect.ImmutableSet;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffers;

import static com.chimericdream.miniblockmerchants.MiniblockMerchantsMod.REGISTRY_HELPER;

public class ModProfessions {
    public static final Map<String, RegistrySupplier<VillagerProfession>> PROFESSIONS = new HashMap<>();
    public static final Map<String, MerchantOffers> TRADES = new HashMap<>();

    public static final String ALCHEMIST_ID = "mm_alchemist";
    public static final RegistrySupplier<VillagerProfession> ALCHEMIST = register(ALCHEMIST_ID);

    public static final String ARBORICULTURIST_ID = "mm_arboriculturist";
    public static final RegistrySupplier<VillagerProfession> ARBORICULTURIST = register(ARBORICULTURIST_ID);

    public static final String ASTRONOMER_ID = "mm_astronomer";
    public static final RegistrySupplier<VillagerProfession> ASTRONOMER = register(ASTRONOMER_ID);

    public static final String BAKER_ID = "mm_baker";
    public static final RegistrySupplier<VillagerProfession> BAKER = register(BAKER_ID);

    public static final String BARTENDER_ID = "mm_bartender";
    public static final RegistrySupplier<VillagerProfession> BARTENDER = register(BARTENDER_ID);

    public static final String BEEKEEPER_ID = "mm_beekeeper";
    public static final RegistrySupplier<VillagerProfession> BEEKEEPER = register(BEEKEEPER_ID);

    public static final String BLACKSMITH_ID = "mm_blacksmith";
    public static final RegistrySupplier<VillagerProfession> BLACKSMITH = register(BLACKSMITH_ID);

    public static final String CHEF_ID = "mm_chef";
    public static final RegistrySupplier<VillagerProfession> CHEF = register(CHEF_ID);

    public static final String ENGINEER_ID = "mm_engineer";
    public static final RegistrySupplier<VillagerProfession> ENGINEER = register(ENGINEER_ID);

    public static final String EREMOLOGIST_ID = "mm_eremologist";
    public static final RegistrySupplier<VillagerProfession> EREMOLOGIST = register(EREMOLOGIST_ID);

    public static final String FURNISHER_ID = "mm_furnisher";
    public static final RegistrySupplier<VillagerProfession> FURNISHER = register(FURNISHER_ID);

    public static final String GAMEMASTER_ID = "mm_gamemaster";
    public static final RegistrySupplier<VillagerProfession> GAMEMASTER = register(GAMEMASTER_ID);

    public static final String GRIEFER_ID = "mm_griefer";
    public static final RegistrySupplier<VillagerProfession> GRIEFER = register(GRIEFER_ID);

    public static final String HORTICULTURIST_ID = "mm_horticulturist";
    public static final RegistrySupplier<VillagerProfession> HORTICULTURIST = register(HORTICULTURIST_ID);

    public static final String MINERALOGIST_ID = "mm_mineralogist";
    public static final RegistrySupplier<VillagerProfession> MINERALOGIST = register(MINERALOGIST_ID);

    public static final String NETHEROGRAPHER_ID = "mm_netherographer";
    public static final RegistrySupplier<VillagerProfession> NETHEROGRAPHER = register(NETHEROGRAPHER_ID);

    public static final String OCEANOGRAPHER_ID = "mm_oceanographer";
    public static final RegistrySupplier<VillagerProfession> OCEANOGRAPHER = register(OCEANOGRAPHER_ID);

    public static final String OLERICULTURIST_ID = "mm_olericulturist";
    public static final RegistrySupplier<VillagerProfession> OLERICULTURIST = register(OLERICULTURIST_ID);

    public static final String PETROLOGIST_ID = "mm_petrologist";
    public static final RegistrySupplier<VillagerProfession> PETROLOGIST = register(PETROLOGIST_ID);

    public static final String PLUSHIE_MANIAC_ID = "mm_plushie_maniac";
    public static final RegistrySupplier<VillagerProfession> PLUSHIE_MANIAC = register(PLUSHIE_MANIAC_ID);

    public static final String POMOLOGIST_ID = "mm_pomologist";
    public static final RegistrySupplier<VillagerProfession> POMOLOGIST = register(POMOLOGIST_ID);

    public static final String RECYCLER_ID = "mm_recycler";
    public static final RegistrySupplier<VillagerProfession> RECYCLER = register(RECYCLER_ID);

    public static final String RITUALIST_ID = "mm_ritualist";
    public static final RegistrySupplier<VillagerProfession> RITUALIST = register(RITUALIST_ID);

    public static final String SCULPTOR_ID = "mm_sculptor";
    public static final RegistrySupplier<VillagerProfession> SCULPTOR = register(SCULPTOR_ID);

    public static final String STEAMPUNKER_ID = "mm_steampunker";
    public static final RegistrySupplier<VillagerProfession> STEAMPUNKER = register(STEAMPUNKER_ID);

    public static final String TAILOR_ID = "mm_tailor";
    public static final RegistrySupplier<VillagerProfession> TAILOR = register(TAILOR_ID);

    public static final List<RegistrySupplier<VillagerProfession>> PROFESSION_LIST = List.of(
            ALCHEMIST,
            ARBORICULTURIST,
            ASTRONOMER,
            BAKER,
            BARTENDER,
            BEEKEEPER,
            BLACKSMITH,
            CHEF,
            ENGINEER,
            EREMOLOGIST,
            FURNISHER,
            GAMEMASTER,
            GRIEFER,
            HORTICULTURIST,
            MINERALOGIST,
            NETHEROGRAPHER,
            OCEANOGRAPHER,
            OLERICULTURIST,
            PETROLOGIST,
            PLUSHIE_MANIAC,
            POMOLOGIST,
            RECYCLER,
            RITUALIST,
            SCULPTOR,
            STEAMPUNKER,
            TAILOR
    );

    public static void init() {
    }

    public static void populateTrades() {
        TRADES.put(makeId(ALCHEMIST_ID).toString(), AlchemistTrades.TRADES);
        TRADES.put(makeId(ARBORICULTURIST_ID).toString(), ArboriculturistTrades.TRADES);
        TRADES.put(makeId(ASTRONOMER_ID).toString(), AstronomerTrades.TRADES);
        TRADES.put(makeId(BAKER_ID).toString(), BakerTrades.TRADES);
        TRADES.put(makeId(BARTENDER_ID).toString(), BartenderTrades.TRADES);
        TRADES.put(makeId(BEEKEEPER_ID).toString(), BeekeeperTrades.TRADES);
        TRADES.put(makeId(BLACKSMITH_ID).toString(), BlacksmithTrades.TRADES);
        TRADES.put(makeId(CHEF_ID).toString(), ChefTrades.TRADES);
        TRADES.put(makeId(ENGINEER_ID).toString(), EngineerTrades.TRADES);
        TRADES.put(makeId(EREMOLOGIST_ID).toString(), EremologistTrades.TRADES);
        TRADES.put(makeId(FURNISHER_ID).toString(), FurnisherTrades.TRADES);
        TRADES.put(makeId(GAMEMASTER_ID).toString(), GamemasterTrades.TRADES);
        TRADES.put(makeId(GRIEFER_ID).toString(), GrieferTrades.TRADES);
        TRADES.put(makeId(HORTICULTURIST_ID).toString(), HorticulturistTrades.TRADES);
        TRADES.put(makeId(MINERALOGIST_ID).toString(), MineralogistTrades.TRADES);
        TRADES.put(makeId(NETHEROGRAPHER_ID).toString(), NetherographerTrades.TRADES);
        TRADES.put(makeId(OCEANOGRAPHER_ID).toString(), OceanographerTrades.TRADES);
        TRADES.put(makeId(OLERICULTURIST_ID).toString(), OlericulturistTrades.TRADES);
        TRADES.put(makeId(PETROLOGIST_ID).toString(), PetrologistTrades.TRADES);
        TRADES.put(makeId(PLUSHIE_MANIAC_ID).toString(), PlushieManiacTrades.TRADES);
        TRADES.put(makeId(POMOLOGIST_ID).toString(), PomologistTrades.TRADES);
        TRADES.put(makeId(RECYCLER_ID).toString(), RecyclerTrades.TRADES);
        TRADES.put(makeId(RITUALIST_ID).toString(), RitualistTrades.TRADES);
        TRADES.put(makeId(SCULPTOR_ID).toString(), SculptorTrades.TRADES);
        TRADES.put(makeId(STEAMPUNKER_ID).toString(), SteampunkerTrades.TRADES);
        TRADES.put(makeId(TAILOR_ID).toString(), TailorTrades.TRADES);
    }

    public static ResourceLocation makeId(String name) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, name);
    }

    public static ResourceKey<VillagerProfession> get(String name) {
        RegistrySupplier<VillagerProfession> supplier = PROFESSIONS.get(name);

        if (supplier == null) {
            return VillagerProfession.NONE;
        }

        return ResourceKey.create(Registries.VILLAGER_PROFESSION, makeId(name));
    }

    public static RegistrySupplier<VillagerProfession> register(String name) {
        ResourceLocation id = makeId(name);
        RegistrySupplier<VillagerProfession> prof = REGISTRY_HELPER.registerVillagerProfession(
                id,
                () -> new VillagerProfession(
                        Component.literal(id.toString()),
                        PoiType.NONE,
                        PoiType.NONE,
                        ImmutableSet.of(),
                        ImmutableSet.of(),
                        SoundEvents.VILLAGER_WORK_MASON
                )
        );

        PROFESSIONS.put(name, prof);

        return prof;
    }

    public static boolean isMiniblockMerchant(Holder<VillagerProfession> profession) {
        return profession.is(prof -> prof.location().getNamespace().equals(ModInfo.MOD_ID));
    }

    public static MerchantOffers getDefaultOffers() {
        MiniblockMerchantsMod.LOGGER.info("No trades found");
        return new MerchantOffers();
    }

    public static MerchantOffers getOffersForProfession(String id) {
        MiniblockMerchantsMod.LOGGER.info("Getting trades for {}", id);

        String profession = id.startsWith(ModInfo.MOD_ID) ? id : makeId(id).toString();

        if (TRADES.isEmpty()) {
            MiniblockMerchantsMod.LOGGER.info("Trades not yet populated");
            populateTrades();
        }

        return TRADES.getOrDefault(profession, getDefaultOffers());
    }
}
