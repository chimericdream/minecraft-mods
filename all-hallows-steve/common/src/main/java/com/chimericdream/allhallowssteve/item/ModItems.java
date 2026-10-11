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

    public static final Identifier CIDER_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "cider");
    public static final Identifier POMACE_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pomace");

    /** A bottle of pressed apple juice: food only, no effects, and it gives the glass bottle back. */
    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Item> CIDER = REGISTRY_HELPER.registerItem(
        CIDER_ID,
        () -> new Item(
            new Item.Properties()
                .arch$tab(CreativeModeTabs.FOOD_AND_DRINKS)
                .setId(REGISTRY_HELPER.makeItemRegistryKey(CIDER_ID))
                .craftRemainder(Items.GLASS_BOTTLE)
                .food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build(), Consumables.DEFAULT_DRINK)
                .usingConvertsTo(Items.GLASS_BOTTLE)
                .stacksTo(16)
        )
    );

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
