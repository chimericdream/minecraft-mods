package com.chimericdream.miniblockmerchants.registry.trades;

import com.chimericdream.miniblockmerchants.data.MiniblockTextures;
import com.chimericdream.miniblockmerchants.item.ModItems;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffers;

import static com.chimericdream.miniblockmerchants.util.DataUtil.makeOffer;
import static com.chimericdream.miniblockmerchants.util.DataUtil.makeOfferList;

public class RecyclerTrades {
    public static final MerchantOffers TRADES = makeOfferList(
        makeOffer(new ItemCost(ModItems.ANCIENT_SHELL_ITEM.get()), "Ancient Shell", MiniblockTextures.ANCIENT_SHELL.getA(), MiniblockTextures.ANCIENT_SHELL.getB()),
        makeOffer(new ItemCost(ModItems.BOOK_OF_RITUALS_ITEM.get()), "Book of Rituals", MiniblockTextures.BOOK_OF_RITUALS.getA(), MiniblockTextures.BOOK_OF_RITUALS.getB()),
        makeOffer(new ItemCost(ModItems.BUDDING_CACTUS_ITEM.get()), "Budding Cactus", MiniblockTextures.BUDDING_CACTUS.getA(), MiniblockTextures.BUDDING_CACTUS.getB()),
        makeOffer(new ItemCost(ModItems.CRYSTAL_PHIAL_ITEM.get()), "Crystal Phial", MiniblockTextures.CRYSTAL_PHIAL.getA(), MiniblockTextures.CRYSTAL_PHIAL.getB()),
        makeOffer(new ItemCost(ModItems.CULTIVATED_SAPLING_ITEM.get()), "Cultivated Sapling", MiniblockTextures.CULTIVATED_SAPLING.getA(), MiniblockTextures.CULTIVATED_SAPLING.getB()),
        makeOffer(new ItemCost(ModItems.DRENCHED_SCORE_SHEET_ITEM.get()), "Drenched Score Sheet", MiniblockTextures.DRENCHED_SCORE_SHEET.getA(), MiniblockTextures.DRENCHED_SCORE_SHEET.getB()),
        makeOffer(new ItemCost(ModItems.ENCHANTED_RED_DELICIOUS_ITEM.get()), "Enchanted Red Delicious", MiniblockTextures.ENCHANTED_RED_DELICIOUS.getA(), MiniblockTextures.ENCHANTED_RED_DELICIOUS.getB()),
        makeOffer(new ItemCost(ModItems.ENDLESS_BOOKSHELF_ITEM.get()), "Endless Bookshelf", MiniblockTextures.ENDLESS_BOOKSHELF.getA(), MiniblockTextures.ENDLESS_BOOKSHELF.getB()),
        makeOffer(new ItemCost(ModItems.FINE_THREAD_ITEM.get()), "Fine Thread", MiniblockTextures.FINE_THREAD.getA(), MiniblockTextures.FINE_THREAD.getB()),
        makeOffer(new ItemCost(ModItems.FORGOTTEN_SCRAP_METAL_ITEM.get()), "Forgotten Scrap Metal", MiniblockTextures.FORGOTTEN_SCRAP_METAL.getA(), MiniblockTextures.FORGOTTEN_SCRAP_METAL.getB()),
        makeOffer(new ItemCost(ModItems.FRAGRANT_FLOWER_ITEM.get()), "Fragrant Flower", MiniblockTextures.FRAGRANT_FLOWER.getA(), MiniblockTextures.FRAGRANT_FLOWER.getB()),
        makeOffer(new ItemCost(ModItems.GALILEAN_SPYGLASS_ITEM.get()), "Galilean Spyglass", MiniblockTextures.GALILEAN_SPYGLASS.getA(), MiniblockTextures.GALILEAN_SPYGLASS.getB()),
        makeOffer(new ItemCost(ModItems.MASTERCRAFTED_IRON_ITEM.get()), "Mastercrafted Iron", MiniblockTextures.MASTERCRAFTED_IRON.getA(), MiniblockTextures.MASTERCRAFTED_IRON.getB()),
        makeOffer(new ItemCost(ModItems.MIXOLOGY_STATION_ITEM.get()), "Mixology Station", MiniblockTextures.MIXOLOGY_STATION.getA(), MiniblockTextures.MIXOLOGY_STATION.getB()),
        makeOffer(new ItemCost(ModItems.OVERGROWN_CARROT_ITEM.get()), "Overgrown Carrot", MiniblockTextures.OVERGROWN_CARROT.getA(), MiniblockTextures.OVERGROWN_CARROT.getB()),
        makeOffer(new ItemCost(ModItems.PRISMATIC_HONEYCOMB_ITEM.get()), "Prismatic Honeycomb", MiniblockTextures.PRISMATIC_HONEYCOMB.getA(), MiniblockTextures.PRISMATIC_HONEYCOMB.getB()),
        makeOffer(new ItemCost(ModItems.PURE_GOLD_ITEM.get()), "24-Karat Gold", MiniblockTextures.PURE_GOLD.getA(), MiniblockTextures.PURE_GOLD.getB()),
        makeOffer(new ItemCost(ModItems.RADIATING_REDSTONE_ITEM.get()), "Radiating Redstone", MiniblockTextures.RADIATING_REDSTONE.getA(), MiniblockTextures.RADIATING_REDSTONE.getB()),
        makeOffer(new ItemCost(ModItems.ROTTING_RECYCLING_BIN_ITEM.get()), "Rotting Recycling Bin", MiniblockTextures.ROTTING_RECYCLING_BIN.getA(), MiniblockTextures.ROTTING_RECYCLING_BIN.getB()),
        makeOffer(new ItemCost(ModItems.SCULPTING_CLAY_ITEM.get()), "Sculpting Clay", MiniblockTextures.SCULPTING_CLAY.getA(), MiniblockTextures.SCULPTING_CLAY.getB()),
        makeOffer(new ItemCost(ModItems.SHIMMERING_WHEAT_ITEM.get()), "Shimmering Wheat", MiniblockTextures.SHIMMERING_WHEAT.getA(), MiniblockTextures.SHIMMERING_WHEAT.getB()),
        makeOffer(new ItemCost(ModItems.SOAKED_VILLAGER_PLUSHIE_ITEM.get()), "Soaked Villager Plushie", MiniblockTextures.SOAKED_VILLAGER_PLUSHIE.getA(), MiniblockTextures.SOAKED_VILLAGER_PLUSHIE.getB()),
        makeOffer(new ItemCost(ModItems.SPARKLING_BLAZE_POWDER_ITEM.get()), "Sparkling Blaze Powder", MiniblockTextures.SPARKLING_BLAZE_POWDER.getA(), MiniblockTextures.SPARKLING_BLAZE_POWDER.getB()),
        makeOffer(new ItemCost(ModItems.STABILIZED_EXPLOSION_ITEM.get()), "Stabilized Explosion", MiniblockTextures.STABILIZED_EXPLOSION.getA(), MiniblockTextures.STABILIZED_EXPLOSION.getB()),
        makeOffer(new ItemCost(ModItems.UNUSUALLY_DENSE_ROCK_ITEM.get()), "Unusually Dense Rock", MiniblockTextures.UNUSUALLY_DENSE_ROCK.getA(), MiniblockTextures.UNUSUALLY_DENSE_ROCK.getB()),
        makeOffer(new ItemCost(ModItems.WAGYU_BEEF_ITEM.get()), "Wagyu Beef", MiniblockTextures.WAGYU_BEEF.getA(), MiniblockTextures.WAGYU_BEEF.getB())
    );
}
