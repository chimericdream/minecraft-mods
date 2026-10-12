package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.item.ModItems;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Supplier;

/**
 * One drink that can be poured into a cauldron and mulled: its bottle, its mulled bottle, and its two cauldron
 * blocks. Everything is a supplier because the types are created before the registries are filled.
 */
public record JuiceCauldronType(
    String name,
    Supplier<Item> juice,
    Supplier<Item> mulledJuice,
    Supplier<Block> cauldron,
    Supplier<Block> mulledCauldron,
    CauldronInteraction.Dispatcher behaviors,
    CauldronInteraction.Dispatcher mulledBehaviors
) {
    public static final JuiceCauldronType APPLE_CIDER = create(
        "apple_cider",
        () -> ModItems.APPLE_CIDER.get(),
        () -> ModItems.MULLED_APPLE_CIDER.get(),
        () -> ModBlocks.APPLE_CIDER_CAULDRON.get(),
        () -> ModBlocks.MULLED_APPLE_CIDER_CAULDRON.get()
    );

    public static final JuiceCauldronType SWEET_BERRY_JUICE = create(
        "sweet_berry_juice",
        () -> ModItems.SWEET_BERRY_JUICE.get(),
        () -> ModItems.MULLED_SWEET_BERRY_JUICE.get(),
        () -> ModBlocks.SWEET_BERRY_JUICE_CAULDRON.get(),
        () -> ModBlocks.MULLED_SWEET_BERRY_JUICE_CAULDRON.get()
    );

    public static final JuiceCauldronType GLOW_BERRY_JUICE = create(
        "glow_berry_juice",
        () -> ModItems.GLOW_BERRY_JUICE.get(),
        () -> ModItems.MULLED_GLOW_BERRY_JUICE.get(),
        () -> ModBlocks.GLOW_BERRY_JUICE_CAULDRON.get(),
        () -> ModBlocks.MULLED_GLOW_BERRY_JUICE_CAULDRON.get()
    );

    public static final List<JuiceCauldronType> ALL = List.of(APPLE_CIDER, SWEET_BERRY_JUICE, GLOW_BERRY_JUICE);

    private static JuiceCauldronType create(String name, Supplier<Item> juice, Supplier<Item> mulledJuice, Supplier<Block> cauldron, Supplier<Block> mulledCauldron) {
        return new JuiceCauldronType(name, juice, mulledJuice, cauldron, mulledCauldron, new CauldronInteraction.Dispatcher(), new CauldronInteraction.Dispatcher());
    }

    public Identifier cauldronId() {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, this.name + "_cauldron");
    }

    public Identifier mulledCauldronId() {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "mulled_" + this.name + "_cauldron");
    }
}
