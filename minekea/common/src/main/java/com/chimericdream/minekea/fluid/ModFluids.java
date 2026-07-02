package com.chimericdream.minekea.fluid;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.containers.HoneyCauldronBlock;
import com.chimericdream.minekea.block.containers.MilkCauldronBlock;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class ModFluids implements ModThingGroup {
    public static final RegistrySupplier<FlowingFluid> HONEY_FLUID = REGISTRY_HELPER.registerFluid(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "fluids/honey"), HoneyFluid::new);
    public static final RegistrySupplier<FlowingFluid> FLOWING_HONEY = REGISTRY_HELPER.registerFluid(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "fluids/honey/flowing"), HoneyFluid.Flowing::new);
    public static final RegistrySupplier<LiquidBlock> HONEY_SOURCE_BLOCK = REGISTRY_HELPER.registerBlock(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "fluids/honey/source"), HoneyFluid.Block::new);
    public static final RegistrySupplier<Item> HONEY_BUCKET = REGISTRY_HELPER.registerItem(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "containers/honey_bucket"), HoneyFluid.Bucket::new);
    public static final RegistrySupplier<Block> HONEY_CAULDRON = REGISTRY_HELPER.registerBlock(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "containers/cauldrons/honey"), HoneyCauldronBlock::new);

    public static final RegistrySupplier<FlowingFluid> MILK_FLUID = REGISTRY_HELPER.registerFluid(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "fluids/milk"), MilkFluid::new);
    public static final RegistrySupplier<FlowingFluid> FLOWING_MILK = REGISTRY_HELPER.registerFluid(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "fluids/milk/flowing"), MilkFluid.Flowing::new);
    public static final RegistrySupplier<LiquidBlock> MILK_SOURCE_BLOCK = REGISTRY_HELPER.registerBlock(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "fluids/milk/source"), MilkFluid.Block::new);
    public static final RegistrySupplier<Block> MILK_CAULDRON = REGISTRY_HELPER.registerBlock(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "containers/cauldrons/milk"), MilkCauldronBlock::new);

    public static void init() {
    }

    public static void postInit() {
    }
}
