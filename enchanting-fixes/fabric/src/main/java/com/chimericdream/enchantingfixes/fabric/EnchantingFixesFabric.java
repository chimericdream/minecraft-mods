package com.chimericdream.enchantingfixes.fabric;

import com.chimericdream.enchantingfixes.EnchantingFixesMod;
import com.chimericdream.enchantingfixes.ModInfo;
import com.chimericdream.enchantingfixes.enchantment.AdvancedSilkTouch;
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

        registerPack(ScalingEnchantments.PACK_PATH, "pack.enchantingfixes.scaling_enchantments");
        registerPack(AdvancedSilkTouch.PACK_PATH, "pack.enchantingfixes.advanced_silk_touch");
    }

    /** NORMAL: the pack is off until the player turns it on. */
    private static void registerPack(String path, String nameKey) {
        // Fabric adds "resourcepacks/" to the path itself.
        String packName = path.substring("resourcepacks/".length());
        ResourceLoader.registerBuiltinPack(
            Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, packName),
            FabricLoader.getInstance().getModContainer(ModInfo.MOD_ID).orElseThrow(),
            Component.translatable(nameKey),
            PackActivationType.NORMAL
        );
    }
}
