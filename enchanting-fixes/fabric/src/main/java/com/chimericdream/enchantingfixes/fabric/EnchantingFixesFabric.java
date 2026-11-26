package com.chimericdream.enchantingfixes.fabric;

import com.chimericdream.enchantingfixes.EnchantingFixesMod;
import com.chimericdream.enchantingfixes.ModInfo;
import com.chimericdream.enchantingfixes.enchantment.ScalingEnchantments;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class EnchantingFixesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EnchantingFixesMod.init();

        // Fabric adds "resourcepacks/" to the path itself.
        String packName = ScalingEnchantments.PACK_PATH.substring("resourcepacks/".length());
        ResourceLoader.registerBuiltinPack(
            Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, packName),
            FabricLoader.getInstance().getModContainer(ModInfo.MOD_ID).orElseThrow(),
            Component.translatable("pack.enchantingfixes.scaling_enchantments"),
            PackActivationType.NORMAL
        );
    }
}
