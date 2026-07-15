package com.chimericdream.miniblockmerchants.registry.trades;

import com.chimericdream.miniblockmerchants.data.MiniblockTextures;
import com.chimericdream.miniblockmerchants.item.ModItems;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffers;

import static com.chimericdream.miniblockmerchants.util.DataUtil.makeOffer;
import static com.chimericdream.miniblockmerchants.util.DataUtil.makeOfferList;

public class RecyclerTrades {
    public static final MerchantOffers TRADES = makeOfferList(
        makeOffer(new ItemCost(ModItems.ANCIENT_SHELL_ITEM.get()), "Ancient Shell", MiniblockTextures.ANCIENT_SHELL.getFirst(), MiniblockTextures.ANCIENT_SHELL.getSecond()),
        makeOffer(new ItemCost(ModItems.BOOK_OF_RITUALS_ITEM.get()), "Book of Rituals", MiniblockTextures.BOOK_OF_RITUALS.getFirst(), MiniblockTextures.BOOK_OF_RITUALS.getSecond()),
        makeOffer(new ItemCost(ModItems.BUDDING_CACTUS_ITEM.get()), "Budding Cactus", MiniblockTextures.BUDDING_CACTUS.getFirst(), MiniblockTextures.BUDDING_CACTUS.getSecond()),
        makeOffer(new ItemCost(ModItems.CRYSTAL_PHIAL_ITEM.get()), "Crystal Phial", MiniblockTextures.CRYSTAL_PHIAL.getFirst(), MiniblockTextures.CRYSTAL_PHIAL.getSecond()),
        makeOffer(new ItemCost(ModItems.CULTIVATED_SAPLING_ITEM.get()), "Cultivated Sapling", MiniblockTextures.CULTIVATED_SAPLING.getFirst(), MiniblockTextures.CULTIVATED_SAPLING.getSecond()),
        makeOffer(new ItemCost(ModItems.DRENCHED_SCORE_SHEET_ITEM.get()), "Drenched Score Sheet", MiniblockTextures.DRENCHED_SCORE_SHEET.getFirst(), MiniblockTextures.DRENCHED_SCORE_SHEET.getSecond()),
        makeOffer(new ItemCost(ModItems.ENCHANTED_RED_DELICIOUS_ITEM.get()), "Enchanted Red Delicious", MiniblockTextures.ENCHANTED_RED_DELICIOUS.getFirst(), MiniblockTextures.ENCHANTED_RED_DELICIOUS.getSecond()),
        makeOffer(new ItemCost(ModItems.ENDLESS_BOOKSHELF_ITEM.get()), "Endless Bookshelf", MiniblockTextures.ENDLESS_BOOKSHELF.getFirst(), MiniblockTextures.ENDLESS_BOOKSHELF.getSecond()),
        makeOffer(new ItemCost(ModItems.FINE_THREAD_ITEM.get()), "Fine Thread", MiniblockTextures.FINE_THREAD.getFirst(), MiniblockTextures.FINE_THREAD.getSecond()),
        makeOffer(new ItemCost(ModItems.FORGOTTEN_SCRAP_METAL_ITEM.get()), "Forgotten Scrap Metal", MiniblockTextures.FORGOTTEN_SCRAP_METAL.getFirst(), MiniblockTextures.FORGOTTEN_SCRAP_METAL.getSecond()),
        makeOffer(new ItemCost(ModItems.FRAGRANT_FLOWER_ITEM.get()), "Fragrant Flower", MiniblockTextures.FRAGRANT_FLOWER.getFirst(), MiniblockTextures.FRAGRANT_FLOWER.getSecond()),
        makeOffer(new ItemCost(ModItems.GALILEAN_SPYGLASS_ITEM.get()), "Galilean Spyglass", MiniblockTextures.GALILEAN_SPYGLASS.getFirst(), MiniblockTextures.GALILEAN_SPYGLASS.getSecond()),
        makeOffer(new ItemCost(ModItems.MASTERCRAFTED_IRON_ITEM.get()), "Mastercrafted Iron", MiniblockTextures.MASTERCRAFTED_IRON.getFirst(), MiniblockTextures.MASTERCRAFTED_IRON.getSecond()),
        makeOffer(new ItemCost(ModItems.MIXOLOGY_STATION_ITEM.get()), "Mixology Station", MiniblockTextures.MIXOLOGY_STATION.getFirst(), MiniblockTextures.MIXOLOGY_STATION.getSecond()),
        makeOffer(new ItemCost(ModItems.OVERGROWN_CARROT_ITEM.get()), "Overgrown Carrot", MiniblockTextures.OVERGROWN_CARROT.getFirst(), MiniblockTextures.OVERGROWN_CARROT.getSecond()),
        makeOffer(new ItemCost(ModItems.PRISMATIC_HONEYCOMB_ITEM.get()), "Prismatic Honeycomb", MiniblockTextures.PRISMATIC_HONEYCOMB.getFirst(), MiniblockTextures.PRISMATIC_HONEYCOMB.getSecond()),
        makeOffer(new ItemCost(ModItems.PURE_GOLD_ITEM.get()), "24-Karat Gold", MiniblockTextures.PURE_GOLD.getFirst(), MiniblockTextures.PURE_GOLD.getSecond()),
        makeOffer(new ItemCost(ModItems.RADIATING_REDSTONE_ITEM.get()), "Radiating Redstone", MiniblockTextures.RADIATING_REDSTONE.getFirst(), MiniblockTextures.RADIATING_REDSTONE.getSecond()),
        makeOffer(new ItemCost(ModItems.ROTTING_RECYCLING_BIN_ITEM.get()), "Rotting Recycling Bin", MiniblockTextures.ROTTING_RECYCLING_BIN.getFirst(), MiniblockTextures.ROTTING_RECYCLING_BIN.getSecond()),
        makeOffer(new ItemCost(ModItems.SCULPTING_CLAY_ITEM.get()), "Sculpting Clay", MiniblockTextures.SCULPTING_CLAY.getFirst(), MiniblockTextures.SCULPTING_CLAY.getSecond()),
        makeOffer(new ItemCost(ModItems.SHIMMERING_WHEAT_ITEM.get()), "Shimmering Wheat", MiniblockTextures.SHIMMERING_WHEAT.getFirst(), MiniblockTextures.SHIMMERING_WHEAT.getSecond()),
        makeOffer(new ItemCost(ModItems.SOAKED_VILLAGER_PLUSHIE_ITEM.get()), "Soaked Villager Plushie", MiniblockTextures.SOAKED_VILLAGER_PLUSHIE.getFirst(), MiniblockTextures.SOAKED_VILLAGER_PLUSHIE.getSecond()),
        makeOffer(new ItemCost(ModItems.SPARKLING_BLAZE_POWDER_ITEM.get()), "Sparkling Blaze Powder", MiniblockTextures.SPARKLING_BLAZE_POWDER.getFirst(), MiniblockTextures.SPARKLING_BLAZE_POWDER.getSecond()),
        makeOffer(new ItemCost(ModItems.STABILIZED_EXPLOSION_ITEM.get()), "Stabilized Explosion", MiniblockTextures.STABILIZED_EXPLOSION.getFirst(), MiniblockTextures.STABILIZED_EXPLOSION.getSecond()),
        makeOffer(new ItemCost(ModItems.UNUSUALLY_DENSE_ROCK_ITEM.get()), "Unusually Dense Rock", MiniblockTextures.UNUSUALLY_DENSE_ROCK.getFirst(), MiniblockTextures.UNUSUALLY_DENSE_ROCK.getSecond()),
        makeOffer(new ItemCost(ModItems.WAGYU_BEEF_ITEM.get()), "Wagyu Beef", MiniblockTextures.WAGYU_BEEF.getFirst(), MiniblockTextures.WAGYU_BEEF.getSecond())
    );
}
