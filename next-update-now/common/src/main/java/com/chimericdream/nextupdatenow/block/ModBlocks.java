package com.chimericdream.nextupdatenow.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Optional;
import java.util.function.Supplier;

import static com.chimericdream.nextupdatenow.NextUpdateNowMod.REGISTRY_HELPER;

public class ModBlocks {
    private static final Item.Properties COLORED_BLOCK_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.COLORED_BLOCKS);
    private static final Item.Properties BUILDING_BLOCK_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.BUILDING_BLOCKS);
    private static final Item.Properties NATURAL_BLOCK_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.NATURAL_BLOCKS);
    private static final Item.Properties REDSTONE_BLOCK_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.REDSTONE_BLOCKS);
    private static final Item.Properties FUNCTIONAL_BLOCK_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS);

    /**
     * Poplar has no unique door/trapdoor/button/pressure-plate/fence-gate/sign sounds in the vanilla
     * snapshot (only its leaves get bespoke sound events), so its WoodType/BlockSetType are built with
     * the same generic-wood defaults OAK/JUNGLE/DARK_OAK/MANGROVE use. WoodType.register/
     * BlockSetType.register are private (no mixin exists in this repo to open them), but nothing in
     * 26.2 depends on that registry: sign and hanging-sign geometry is now an ordinary baked block
     * model (see the copied poplar_sign_rot_0.json) rather than a WoodType-keyed texture atlas lookup,
     * and every other consumer (door/trapdoor/button/pressure-plate/fence-gate sounds) reads fields
     * directly off the instance we hand its constructor.
     */
    public static final BlockSetType POPLAR_BLOCK_SET_TYPE = new BlockSetType("poplar");
    public static final WoodType POPLAR_WOOD_TYPE = new WoodType("poplar", POPLAR_BLOCK_SET_TYPE);

    /**
     * There's still no natural world generation for poplar (no biome places it), but bonemealing the
     * sapling now grows a real tree: "red_poplar" is one of the three ConfiguredFeature variants ported
     * from the 26.3-snapshot-9 client (see com.chimericdream.nextupdatenow.worldgen), copied to 26.2's
     * data/minecraft/worldgen/configured_feature/ path. 26.2's TreeGrower only has room for one primary
     * "tree" pick (no weighted 3-way choice like the real 26.3 TreeGrower), so orange/yellow_poplar sit
     * unused here for now — easy to wire up if/when this mod gets real weighted tree selection.
     */
    public static final TreeGrower POPLAR_TREE_GROWER = new TreeGrower(
        "poplar",
        Optional.empty(),
        Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.withDefaultNamespace("red_poplar"))),
        Optional.empty()
    );

    public static void init() {
        registerConcreteAndWoolSlabsAndStairs();
        registerPoplarWoodSet();
    }

    private static void registerConcreteAndWoolSlabsAndStairs() {
        for (DyeColor color : DyeColor.values()) {
            String name = color.getName();

            Block concrete = BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(name + "_concrete"));
            String concreteSlabPath = name + "_concrete_slab";
            String concreteStairsPath = name + "_concrete_stairs";
            registerBlockWithItem(concreteSlabPath, () -> new SlabBlock(blockSettings(concrete, concreteSlabPath)), COLORED_BLOCK_SETTINGS);
            registerBlockWithItem(
                concreteStairsPath,
                () -> new ModStairsBlock(concrete.defaultBlockState(), blockSettings(concrete, concreteStairsPath)),
                COLORED_BLOCK_SETTINGS
            );

            Block wool = BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(name + "_wool"));
            String woolSlabPath = name + "_wool_slab";
            String woolStairsPath = name + "_wool_stairs";
            registerBlockWithItem(woolSlabPath, () -> new SlabBlock(blockSettings(wool, woolSlabPath)), COLORED_BLOCK_SETTINGS);
            registerBlockWithItem(
                woolStairsPath,
                () -> new ModStairsBlock(wool.defaultBlockState(), blockSettings(wool, woolStairsPath)),
                COLORED_BLOCK_SETTINGS
            );
        }
    }

    private static void registerPoplarWoodSet() {
        registerBlockWithItem("poplar_log", () -> new RotatedPillarBlock(blockSettings(Blocks.OAK_LOG, "poplar_log")), BUILDING_BLOCK_SETTINGS);
        registerBlockWithItem(
            "stripped_poplar_log",
            () -> new RotatedPillarBlock(blockSettings(Blocks.OAK_LOG, "stripped_poplar_log")),
            BUILDING_BLOCK_SETTINGS
        );
        registerBlockWithItem("poplar_wood", () -> new RotatedPillarBlock(blockSettings(Blocks.OAK_LOG, "poplar_wood")), BUILDING_BLOCK_SETTINGS);
        registerBlockWithItem(
            "stripped_poplar_wood",
            () -> new RotatedPillarBlock(blockSettings(Blocks.OAK_LOG, "stripped_poplar_wood")),
            BUILDING_BLOCK_SETTINGS
        );
        registerBlockWithItem("poplar_planks", () -> new Block(blockSettings(Blocks.OAK_PLANKS, "poplar_planks")), BUILDING_BLOCK_SETTINGS);
        registerBlockWithItem("poplar_slab", () -> new SlabBlock(blockSettings(Blocks.OAK_PLANKS, "poplar_slab")), BUILDING_BLOCK_SETTINGS);
        registerBlockWithItem(
            "poplar_stairs",
            () -> new ModStairsBlock(Blocks.OAK_PLANKS.defaultBlockState(), blockSettings(Blocks.OAK_PLANKS, "poplar_stairs")),
            BUILDING_BLOCK_SETTINGS
        );
        registerBlockWithItem("poplar_fence", () -> new FenceBlock(blockSettings(Blocks.OAK_PLANKS, "poplar_fence")), BUILDING_BLOCK_SETTINGS);

        registerBlockWithItem(
            "poplar_fence_gate",
            () -> new FenceGateBlock(POPLAR_WOOD_TYPE, blockSettings(Blocks.OAK_PLANKS, "poplar_fence_gate")),
            REDSTONE_BLOCK_SETTINGS
        );
        registerBlockWithItem(
            "poplar_door",
            () -> new ModDoorBlock(POPLAR_BLOCK_SET_TYPE, blockSettings(Blocks.OAK_PLANKS, "poplar_door")),
            REDSTONE_BLOCK_SETTINGS
        );
        registerBlockWithItem(
            "poplar_trapdoor",
            () -> new ModTrapDoorBlock(POPLAR_BLOCK_SET_TYPE, blockSettings(Blocks.OAK_PLANKS, "poplar_trapdoor")),
            REDSTONE_BLOCK_SETTINGS
        );
        registerBlockWithItem(
            "poplar_pressure_plate",
            () -> new ModPressurePlateBlock(POPLAR_BLOCK_SET_TYPE, blockSettings(Blocks.OAK_PLANKS, "poplar_pressure_plate")),
            REDSTONE_BLOCK_SETTINGS
        );
        registerBlockWithItem(
            "poplar_button",
            () -> new ModButtonBlock(POPLAR_BLOCK_SET_TYPE, 30, blockSettings(Blocks.OAK_PLANKS, "poplar_button")),
            REDSTONE_BLOCK_SETTINGS
        );

        registerBlockWithItem(
            "orange_poplar_leaves",
            () -> new ModLeavesBlock(0.1F, blockSettings(Blocks.OAK_LEAVES, "orange_poplar_leaves")),
            NATURAL_BLOCK_SETTINGS
        );
        registerBlockWithItem(
            "red_poplar_leaves",
            () -> new ModLeavesBlock(0.1F, blockSettings(Blocks.OAK_LEAVES, "red_poplar_leaves")),
            NATURAL_BLOCK_SETTINGS
        );
        registerBlockWithItem(
            "yellow_poplar_leaves",
            () -> new ModLeavesBlock(0.1F, blockSettings(Blocks.OAK_LEAVES, "yellow_poplar_leaves")),
            NATURAL_BLOCK_SETTINGS
        );

        registerBlockWithItem(
            "poplar_sapling",
            () -> new ModSaplingBlock(POPLAR_TREE_GROWER, blockSettings(Blocks.OAK_SAPLING, "poplar_sapling")),
            NATURAL_BLOCK_SETTINGS
        );

        RegistrySupplier<Block> standingSign = registerBlock(
            "poplar_sign",
            () -> new StandingSignBlock(POPLAR_WOOD_TYPE, blockSettings(Blocks.OAK_PLANKS, "poplar_sign"))
        );
        RegistrySupplier<Block> wallSign = registerBlock(
            "poplar_wall_sign",
            () -> new WallSignBlock(POPLAR_WOOD_TYPE, blockSettings(Blocks.OAK_PLANKS, "poplar_wall_sign"))
        );
        registerItem(
            "poplar_sign",
            () -> new SignItem(standingSign.get(), wallSign.get(), itemSettings(FUNCTIONAL_BLOCK_SETTINGS, "poplar_sign"))
        );

        RegistrySupplier<Block> hangingSign = registerBlock(
            "poplar_hanging_sign",
            () -> new CeilingHangingSignBlock(POPLAR_WOOD_TYPE, blockSettings(Blocks.OAK_PLANKS, "poplar_hanging_sign"))
        );
        RegistrySupplier<Block> wallHangingSign = registerBlock(
            "poplar_wall_hanging_sign",
            () -> new WallHangingSignBlock(POPLAR_WOOD_TYPE, blockSettings(Blocks.OAK_PLANKS, "poplar_wall_hanging_sign"))
        );
        registerItem(
            "poplar_hanging_sign",
            () -> new HangingSignItem(hangingSign.get(), wallHangingSign.get(), itemSettings(FUNCTIONAL_BLOCK_SETTINGS, "poplar_hanging_sign"))
        );
    }

    /**
     * All of this mod's blocks/items use vanilla's own "minecraft" namespace (these are previews of
     * upcoming vanilla content, not mod-namespaced content), so registration goes straight through
     * REGISTRY_HELPER's DeferredRegisters rather than through its registerBlock/registerWithItem
     * convenience methods: those methods collapse to {@code id.getPath()} on NeoForge (re-adding the
     * DeferredRegister's own "nextupdatenow" namespace), which is correct for every other mod in this
     * repo but would silently rename every block here. DeferredRegister.register(Identifier, Supplier)
     * itself honors whatever namespace is in the Identifier on both loaders.
     */
    private static RegistrySupplier<Block> registerBlock(String path, Supplier<Block> supplier) {
        return REGISTRY_HELPER.BLOCKS.register(Identifier.withDefaultNamespace(path), supplier);
    }

    private static RegistrySupplier<Block> registerBlockWithItem(String path, Supplier<Block> supplier, Item.Properties itemSettings) {
        RegistrySupplier<Block> block = registerBlock(path, supplier);
        registerItem(path, () -> new BlockItem(block.get(), itemSettings(itemSettings, path)));
        return block;
    }

    private static <T extends Item> RegistrySupplier<T> registerItem(String path, Supplier<T> supplier) {
        return REGISTRY_HELPER.ITEMS.register(Identifier.withDefaultNamespace(path), supplier);
    }

    private static Item.Properties itemSettings(Item.Properties base, String path) {
        return base.setId(ResourceKey.create(Registries.ITEM, Identifier.withDefaultNamespace(path)));
    }

    private static Properties blockSettings(Block template, String path) {
        return Properties.ofFullCopy(template).setId(ResourceKey.create(Registries.BLOCK, Identifier.withDefaultNamespace(path)));
    }
}
