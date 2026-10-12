package com.chimericdream.allhallowssteve.fabric.advancement;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.advancement.CiderEvent;
import com.chimericdream.allhallowssteve.advancement.ModTriggers;
import com.chimericdream.allhallowssteve.advancement.PumpkinEvent;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.ConsumeItemTrigger;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.RecipeCraftedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * The mod's advancement tab. The shape is documented in {@code ideas/progression/advancements.md}:
 * <pre>
 * It's Pumpkin Season
 * └─ Gourd Workshop
 *    ├─ Not Just Orange
 *    ├─ First Cut Is the Deepest
 *    │  ├─ Pumpkin Head
 *    │  │  ├─ Better Side
 *    │  │  └─ Hey! Who turned out the lights?   (hidden)
 *    │  └─ Rare Cut
 *    │     └─ A Face for Every Occasion         (hidden challenge, awards experience)
 *    └─ Lit Different
 * </pre>
 * The cider press, spice, and mulled drink branch hangs off the same root:
 * <pre>
 * It's Pumpkin Season
 * ├─ Pressing Matters
 * │  ├─ Fresh Pressed
 * │  │  ├─ Berry Good
 * │  │  └─ Three Flavors
 * │  └─ Waste Not
 * └─ Spice Up Your Life
 *    └─ Mulling It Over
 *       └─ Big Batch
 *          └─ Ladle It On
 *             └─ Hot Cider Weather
 *                └─ Season's Drinkings                (hidden challenge, awards experience)
 * </pre>
 * Everything but the three hidden ones is a plain task, and only the two challenges give experience.
 */
public class AllHallowsSteveAdvancements extends FabricAdvancementProvider {
    /** The experience awarded for collecting every stencil. */
    private static final int FULL_SET_EXPERIENCE = 100;

    /** The experience awarded for drinking every kind of mulled drink. */
    private static final int ALL_MULLED_EXPERIENCE = 100;

    private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("gui/advancements/backgrounds/husbandry");

    private record Entry(String id, String title, String description) {
        Identifier identifier() {
            return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, id);
        }

        String titleKey() {
            return "advancements." + ModInfo.MOD_ID + "." + id + ".title";
        }

        String descriptionKey() {
            return "advancements." + ModInfo.MOD_ID + "." + id + ".description";
        }
    }

    private static final Entry ROOT = new Entry("root", "It's Pumpkin Season", "Get your hands on a pumpkin.");
    private static final Entry GOURD_WORKSHOP = new Entry("gourd_workshop", "Gourd Workshop", "Craft a Pumpkin Carving Station.");
    private static final Entry NOT_JUST_ORANGE = new Entry("not_just_orange", "Not Just Orange", "Dye a pumpkin in the Pumpkin Carving Station.");
    private static final Entry FIRST_CUT = new Entry("first_cut", "First Cut Is the Deepest", "Carve a stencil into a pumpkin.");
    private static final Entry PUMPKIN_HEAD = new Entry("pumpkin_head", "Pumpkin Head", "Wear a carved decorated pumpkin.");
    private static final Entry LIGHTS_OUT = new Entry("lights_out", "Hey! Who turned out the lights?", "Put on a decorated pumpkin with no carving.");
    private static final Entry BETTER_SIDE = new Entry("better_side", "Better Side", "Turn a worn pumpkin to a different face.");
    private static final Entry RARE_CUT = new Entry("rare_cut", "Rare Cut", "Find a rare stencil: Heart, Jigsaw, Spawner, or Structure Block.");
    private static final Entry FULL_SET = new Entry("face_for_every_occasion", "A Face for Every Occasion", "Collect every carving stencil.");
    private static final Entry LIT_DIFFERENT = new Entry("lit_different", "Lit Different", "Light a decorated pumpkin with something other than a regular torch.");

    private static final Entry PRESSING_MATTERS = new Entry("pressing_matters", "Pressing Matters", "Craft a Cider Press.");
    private static final Entry FRESH_PRESSED = new Entry("fresh_pressed", "Fresh Pressed", "Get a bottle of apple cider or berry juice.");
    private static final Entry BERRY_GOOD = new Entry("berry_good", "Berry Good", "Get a bottle of sweet berry juice or glow berry juice.");
    private static final Entry THREE_FLAVORS = new Entry("three_flavors", "Three Flavors", "Drink apple cider, sweet berry juice, and glow berry juice.");
    private static final Entry WASTE_NOT = new Entry("waste_not", "Waste Not", "Feed pomace to a hurt animal to heal it.");
    private static final Entry SPICE_UP_YOUR_LIFE = new Entry("spice_up_your_life", "Spice Up Your Life", "Get your hands on cinnamon, cloves, or nutmeg.");
    private static final Entry MULLING_IT_OVER = new Entry("mulling_it_over", "Mulling It Over", "Craft mulling spices.");
    private static final Entry BIG_BATCH = new Entry("big_batch", "Big Batch", "Fill a cauldron with three bottles of apple cider or berry juice.");
    private static final Entry LADLE_IT_ON = new Entry("ladle_it_on", "Ladle It On", "Ladle a serving of a mulled drink out of a cauldron.");
    private static final Entry HOT_CIDER_WEATHER = new Entry("hot_cider_weather", "Hot Cider Weather", "Drink a mulled drink.");
    private static final Entry SEASONS_DRINKINGS = new Entry("seasons_drinkings", "Season's Drinkings", "Drink every kind of mulled drink.");

    private static final List<Entry> ALL = List.of(
        ROOT, GOURD_WORKSHOP, NOT_JUST_ORANGE, FIRST_CUT, PUMPKIN_HEAD, LIGHTS_OUT, BETTER_SIDE, RARE_CUT, FULL_SET, LIT_DIFFERENT,
        PRESSING_MATTERS, FRESH_PRESSED, BERRY_GOOD, THREE_FLAVORS, WASTE_NOT, SPICE_UP_YOUR_LIFE, MULLING_IT_OVER, BIG_BATCH,
        LADLE_IT_ON, HOT_CIDER_WEATHER, SEASONS_DRINKINGS
    );

    public AllHallowsSteveAdvancements(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    public static void configureTranslations(FabricLanguageProvider.TranslationBuilder translationBuilder) {
        for (Entry entry : ALL) {
            translationBuilder.add(entry.titleKey(), entry.title());
            translationBuilder.add(entry.descriptionKey(), entry.description());
        }
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        Item pumpkin = ModBlocks.DECORATED_PUMPKIN.get().asItem();

        AdvancementHolder root = builder(ROOT, Items.PUMPKIN, null, AdvancementType.TASK, false)
            .addCriterion("has_pumpkin", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PUMPKIN))
            .addCriterion("has_decorated_pumpkin", InventoryChangeTrigger.TriggerInstance.hasItems(pumpkin))
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(consumer, ROOT.identifier().toString());

        AdvancementHolder workshop = builder(GOURD_WORKSHOP, ModBlocks.CARVING_STATION.get().asItem(), root, AdvancementType.TASK, false)
            .addCriterion("crafted_station", RecipeCraftedTrigger.TriggerInstance.craftedItem(
                ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "carving_station"))
            ))
            .save(consumer, GOURD_WORKSHOP.identifier().toString());

        builder(NOT_JUST_ORANGE, Items.DYE.orange(), workshop, AdvancementType.TASK, false)
            .addCriterion("dyed", ModTriggers.criterion(PumpkinEvent.DYED))
            .save(consumer, NOT_JUST_ORANGE.identifier().toString());

        AdvancementHolder firstCut = builder(FIRST_CUT, ModItems.CREEPER_STENCIL.get(), workshop, AdvancementType.TASK, false)
            .addCriterion("carved", ModTriggers.criterion(PumpkinEvent.CARVED))
            .save(consumer, FIRST_CUT.identifier().toString());

        AdvancementHolder pumpkinHead = builder(PUMPKIN_HEAD, pumpkin, firstCut, AdvancementType.TASK, false)
            .addCriterion("worn_carved", ModTriggers.criterion(PumpkinEvent.WORN_CARVED))
            .save(consumer, PUMPKIN_HEAD.identifier().toString());

        builder(LIGHTS_OUT, Items.DYE.black(), pumpkinHead, AdvancementType.TASK, true)
            .addCriterion("worn_uncarved", ModTriggers.criterion(PumpkinEvent.WORN_UNCARVED))
            .save(consumer, LIGHTS_OUT.identifier().toString());

        builder(BETTER_SIDE, Items.COMPASS, pumpkinHead, AdvancementType.TASK, false)
            .addCriterion("turned", ModTriggers.criterion(PumpkinEvent.TURNED))
            .save(consumer, BETTER_SIDE.identifier().toString());

        AdvancementHolder rareCut = builder(RARE_CUT, ModItems.HEART_STENCIL.get(), firstCut, AdvancementType.TASK, false)
            .addCriterion("has_heart", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.HEART_STENCIL.get()))
            .addCriterion("has_jigsaw", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.JIGSAW_STENCIL.get()))
            .addCriterion("has_spawner", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SPAWNER_STENCIL.get()))
            .addCriterion("has_structure_block", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STRUCTURE_BLOCK_STENCIL.get()))
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(consumer, RARE_CUT.identifier().toString());

        Advancement.Builder fullSet = builder(FULL_SET, ModItems.JACK_O_LANTERN_STENCIL.get(), rareCut, AdvancementType.CHALLENGE, true)
            .rewards(AdvancementRewards.Builder.experience(FULL_SET_EXPERIENCE));
        ModItems.PUMPKIN_STENCIL_ITEMS.forEach(stencil -> fullSet.addCriterion(
            "has_" + stencil.getId().getPath().replace("/", "_"),
            InventoryChangeTrigger.TriggerInstance.hasItems(stencil.get())
        ));
        fullSet.save(consumer, FULL_SET.identifier().toString());

        builder(LIT_DIFFERENT, Items.SOUL_TORCH, workshop, AdvancementType.TASK, false)
            .addCriterion("lit_unusual", ModTriggers.criterion(PumpkinEvent.LIT_UNUSUAL))
            .save(consumer, LIT_DIFFERENT.identifier().toString());

        generateCiderAdvancements(registryLookup, consumer, root);
    }

    private static Identifier modId(String path) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path);
    }

    private static String path(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    /** Completes when any one of {@code items} is in the player's inventory. */
    private static Advancement.Builder hasAnyOf(Advancement.Builder builder, List<Item> items) {
        items.forEach(item -> builder.addCriterion("has_" + path(item), InventoryChangeTrigger.TriggerInstance.hasItems(item)));

        return builder.requirements(AdvancementRequirements.Strategy.OR);
    }

    /** Adds a "drank this" criterion for each of {@code items}; the caller picks AND (every one) or OR (any one). */
    private static Advancement.Builder drinks(Advancement.Builder builder, HolderGetter<Item> itemLookup, List<Item> items) {
        items.forEach(item -> builder.addCriterion("drank_" + path(item), ConsumeItemTrigger.TriggerInstance.usedItem(itemLookup, item)));

        return builder;
    }

    /** The cider press, spice, and mulled drink advancements, a second branch off the root. */
    private void generateCiderAdvancements(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer, AdvancementHolder root) {
        HolderGetter<Item> itemLookup = registryLookup.lookupOrThrow(Registries.ITEM);
        List<Item> plainDrinks = List.of(ModItems.APPLE_CIDER.get(), ModItems.SWEET_BERRY_JUICE.get(), ModItems.GLOW_BERRY_JUICE.get());
        List<Item> berryJuices = List.of(ModItems.SWEET_BERRY_JUICE.get(), ModItems.GLOW_BERRY_JUICE.get());
        List<Item> mulledDrinks = List.of(ModItems.MULLED_APPLE_CIDER.get(), ModItems.MULLED_SWEET_BERRY_JUICE.get(), ModItems.MULLED_GLOW_BERRY_JUICE.get());
        List<Item> spices = List.of(ModItems.CINNAMON.get(), ModItems.CLOVES.get(), ModItems.NUTMEG.get());

        AdvancementHolder pressingMatters = builder(PRESSING_MATTERS, ModBlocks.CIDER_PRESS.get().asItem(), root, AdvancementType.TASK, false)
            .addCriterion("crafted_press", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceKey.create(Registries.RECIPE, modId("cider_press"))))
            .save(consumer, PRESSING_MATTERS.identifier().toString());

        AdvancementHolder freshPressed = hasAnyOf(builder(FRESH_PRESSED, ModItems.APPLE_CIDER.get(), pressingMatters, AdvancementType.TASK, false), plainDrinks)
            .save(consumer, FRESH_PRESSED.identifier().toString());

        hasAnyOf(builder(BERRY_GOOD, ModItems.SWEET_BERRY_JUICE.get(), freshPressed, AdvancementType.TASK, false), berryJuices)
            .save(consumer, BERRY_GOOD.identifier().toString());

        drinks(builder(THREE_FLAVORS, ModItems.GLOW_BERRY_JUICE.get(), freshPressed, AdvancementType.TASK, false), itemLookup, plainDrinks)
            .save(consumer, THREE_FLAVORS.identifier().toString());

        builder(WASTE_NOT, ModItems.POMACE.get(), pressingMatters, AdvancementType.TASK, false)
            .addCriterion("healed", ModTriggers.pomaceHealedCriterion())
            .save(consumer, WASTE_NOT.identifier().toString());

        AdvancementHolder spiceUp = hasAnyOf(builder(SPICE_UP_YOUR_LIFE, ModItems.CINNAMON.get(), root, AdvancementType.TASK, false), spices)
            .save(consumer, SPICE_UP_YOUR_LIFE.identifier().toString());

        AdvancementHolder mullingItOver = builder(MULLING_IT_OVER, ModItems.MULLING_SPICES.get(), spiceUp, AdvancementType.TASK, false)
            .addCriterion("crafted_mulling_spices", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceKey.create(Registries.RECIPE, modId("mulling_spices"))))
            .save(consumer, MULLING_IT_OVER.identifier().toString());

        AdvancementHolder bigBatch = builder(BIG_BATCH, Items.CAULDRON, mullingItOver, AdvancementType.TASK, false)
            .addCriterion("filled", ModTriggers.criterion(CiderEvent.CAULDRON_FILLED))
            .save(consumer, BIG_BATCH.identifier().toString());

        AdvancementHolder ladleItOn = hasAnyOf(builder(LADLE_IT_ON, Items.GLASS_BOTTLE, bigBatch, AdvancementType.TASK, false), mulledDrinks)
            .save(consumer, LADLE_IT_ON.identifier().toString());

        AdvancementHolder hotCiderWeather = drinks(builder(HOT_CIDER_WEATHER, ModItems.MULLED_APPLE_CIDER.get(), ladleItOn, AdvancementType.TASK, false), itemLookup, mulledDrinks)
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(consumer, HOT_CIDER_WEATHER.identifier().toString());

        drinks(builder(SEASONS_DRINKINGS, ModItems.MULLED_SWEET_BERRY_JUICE.get(), hotCiderWeather, AdvancementType.CHALLENGE, true), itemLookup, mulledDrinks)
            .rewards(AdvancementRewards.Builder.experience(ALL_MULLED_EXPERIENCE))
            .save(consumer, SEASONS_DRINKINGS.identifier().toString());
    }

    /**
     * A new advancement builder with its display set. The root passes {@code null} for {@code parent} and
     * gets the tab background; everything else is a child of {@code parent}. Toasts and chat messages are
     * always on, so a hidden advancement announces itself the moment it is earned.
     */
    private static Advancement.Builder builder(Entry entry, ItemLike icon, AdvancementHolder parent, AdvancementType type, boolean hidden) {
        Advancement.Builder builder = Advancement.Builder.advancement();

        if (parent != null) {
            builder.parent(parent);
        }

        return builder.display(
            icon,
            Component.translatable(entry.titleKey()),
            Component.translatable(entry.descriptionKey()),
            parent == null ? BACKGROUND : null,
            type,
            true,
            true,
            hidden
        );
    }
}
