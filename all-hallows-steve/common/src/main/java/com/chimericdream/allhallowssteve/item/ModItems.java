package com.chimericdream.allhallowssteve.item;

import com.chimericdream.allhallowssteve.ModInfo;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumables;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

public class ModItems {
    public static final List<RegistrySupplier<Item>> PUMPKIN_STENCIL_ITEMS = new ArrayList<>();

    public static final RegistrySupplier<Item> CINNAMON = registerSpice("cinnamon");
    public static final RegistrySupplier<Item> CLOVES = registerSpice("cloves");
    public static final RegistrySupplier<Item> NUTMEG = registerSpice("nutmeg");
    /** One of each spice, crafted together; this is what goes into a cider cauldron. */
    public static final RegistrySupplier<Item> MULLING_SPICES = registerSpice("mulling_spices");

    public static final RegistrySupplier<Item> BLANK_STENCIL = registerStencil("blank", "Blank", 64);
    public static final RegistrySupplier<Item> CRAFTER_STENCIL = registerStencil("crafter", "Crafter");
    public static final RegistrySupplier<Item> CREAKING_STENCIL = registerStencil("creaking", "Creaking");
    public static final RegistrySupplier<Item> CREAKING_HEART_STENCIL = registerStencil("creaking_heart", "Creaking Heart");
    public static final RegistrySupplier<Item> CREEPER_STENCIL = registerStencil("creeper", "Creeper");
    public static final RegistrySupplier<Item> DISPENSER_STENCIL = registerStencil("dispenser", "Dispenser");
    public static final RegistrySupplier<Item> DROPPER_STENCIL = registerStencil("dropper", "Dropper");
    public static final RegistrySupplier<Item> HEART_STENCIL = registerStencil("heart", "Heart");
    public static final RegistrySupplier<Item> JACK_O_LANTERN_STENCIL = registerStencil("jack_o_lantern", "Jack o'Lantern");
    public static final RegistrySupplier<Item> JIGSAW_STENCIL = registerStencil("jigsaw", "Jigsaw");
    public static final RegistrySupplier<Item> LODESTONE_STENCIL = registerStencil("lodestone", "Lodestone");
    public static final RegistrySupplier<Item> OBSERVER_STENCIL = registerStencil("observer", "Observer");
    public static final RegistrySupplier<Item> SPAWNER_STENCIL = registerStencil("spawner", "Spawner");
    public static final RegistrySupplier<Item> STRUCTURE_BLOCK_STENCIL = registerStencil("structure_data", "Structure Block");
    public static final RegistrySupplier<Item> WITHER_ROSE_STENCIL = registerStencil("wither_rose", "Wither Rose");

    public static final Identifier APPLE_CIDER_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "apple_cider");
    public static final Identifier MULLED_APPLE_CIDER_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "mulled_apple_cider");
    public static final Identifier POMACE_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pomace");

    /** A bottle of pressed apple juice: food only, no effects, and it gives the glass bottle back. */
    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Item> APPLE_CIDER = REGISTRY_HELPER.registerItem(
        APPLE_CIDER_ID,
        () -> new Item(
            new Item.Properties()
                .arch$tab(CreativeModeTabs.FOOD_AND_DRINKS)
                .setId(REGISTRY_HELPER.makeItemRegistryKey(APPLE_CIDER_ID))
                .craftRemainder(Items.GLASS_BOTTLE)
                .food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build(), Consumables.DEFAULT_DRINK)
                .usingConvertsTo(Items.GLASS_BOTTLE)
                .stacksTo(16)
        )
    );

    /** Pressed sweet berries. The liquid is tinted red (see {@code CiderItemDataGenerator}). */
    public static final RegistrySupplier<Item> SWEET_BERRY_JUICE = registerJuice("sweet_berry_juice");

    /** Pressed glow berries. The liquid is tinted gold (see {@code CiderItemDataGenerator}). */
    public static final RegistrySupplier<Item> GLOW_BERRY_JUICE = registerJuice("glow_berry_juice");

    /**
     * A hot bottle of mulled cider. Every mulled drink has the same food values, which live in {@link MulledJuice}.
     */
    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Item> MULLED_APPLE_CIDER = REGISTRY_HELPER.registerItem(
        MULLED_APPLE_CIDER_ID,
        () -> new Item(
            new Item.Properties()
                .arch$tab(CreativeModeTabs.FOOD_AND_DRINKS)
                .setId(REGISTRY_HELPER.makeItemRegistryKey(MULLED_APPLE_CIDER_ID))
                .craftRemainder(Items.GLASS_BOTTLE)
                .food(MulledJuice.food(), Consumables.DEFAULT_DRINK)
                .usingConvertsTo(Items.GLASS_BOTTLE)
                .stacksTo(16)
        )
    );

    public static final RegistrySupplier<Item> MULLED_SWEET_BERRY_JUICE = registerMulledJuice("mulled_sweet_berry_juice");
    public static final RegistrySupplier<Item> MULLED_GLOW_BERRY_JUICE = registerMulledJuice("mulled_glow_berry_juice");

    /** What is left after pressing: compost, fertilizer, pig food, and a little healing for livestock (see {@code PomaceFeeding}). */
    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Item> POMACE = REGISTRY_HELPER.registerItem(
        POMACE_ID,
        () -> new Item(
            new Item.Properties()
                .arch$tab(CreativeModeTabs.INGREDIENTS)
                .setId(REGISTRY_HELPER.makeItemRegistryKey(POMACE_ID))
        )
    );

    /** A bottle of pressed berry juice: the same food values and bottle handling as apple cider. */
    @SuppressWarnings("UnstableApiUsage")
    private static RegistrySupplier<Item> registerJuice(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, name);

        return REGISTRY_HELPER.registerItem(
            id,
            () -> new Item(
                new Item.Properties()
                    .arch$tab(CreativeModeTabs.FOOD_AND_DRINKS)
                    .setId(REGISTRY_HELPER.makeItemRegistryKey(id))
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build(), Consumables.DEFAULT_DRINK)
                    .usingConvertsTo(Items.GLASS_BOTTLE)
                    .stacksTo(16)
            )
        );
    }

    /** A hot bottle of mulled berry juice: the same food values and bottle handling as mulled apple cider. */
    @SuppressWarnings("UnstableApiUsage")
    private static RegistrySupplier<Item> registerMulledJuice(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, name);

        return REGISTRY_HELPER.registerItem(
            id,
            () -> new Item(
                new Item.Properties()
                    .arch$tab(CreativeModeTabs.FOOD_AND_DRINKS)
                    .setId(REGISTRY_HELPER.makeItemRegistryKey(id))
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .food(MulledJuice.food(), Consumables.DEFAULT_DRINK)
                    .usingConvertsTo(Items.GLASS_BOTTLE)
                    .stacksTo(16)
            )
        );
    }

    @SuppressWarnings("UnstableApiUsage")
    private static RegistrySupplier<Item> registerSpice(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, name);

        return REGISTRY_HELPER.registerItem(id, () -> new Item(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS).setId(REGISTRY_HELPER.makeItemRegistryKey(id))));
    }

    private static RegistrySupplier<Item> registerStencil(String stencil, String stencilName) {
        return registerStencil(stencil, stencilName, PumpkinStencilItem.DEFAULT_STACK_SIZE);
    }

    private static RegistrySupplier<Item> registerStencil(String stencil, String stencilName, int maxStackSize) {
        RegistrySupplier<Item> item = REGISTRY_HELPER.registerItem(PumpkinStencilItem.makeId(stencil), () -> new PumpkinStencilItem(stencil, stencilName, maxStackSize));

        PUMPKIN_STENCIL_ITEMS.add(item);

        return item;
    }

    public static void init() {
    }
}
