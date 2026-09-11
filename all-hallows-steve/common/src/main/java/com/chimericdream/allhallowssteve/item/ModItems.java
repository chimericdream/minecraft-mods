package com.chimericdream.allhallowssteve.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

public class ModItems {
    public static final List<RegistrySupplier<Item>> PUMPKIN_STENCIL_ITEMS = new ArrayList<>();

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
