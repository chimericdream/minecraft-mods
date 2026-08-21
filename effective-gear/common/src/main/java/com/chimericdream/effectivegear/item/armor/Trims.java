package com.chimericdream.effectivegear.item.armor;

import com.chimericdream.effectivegear.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class Trims {
    public static final ResourceKey<TrimMaterial> ENDER_PEARL_TRIM_ID = ResourceKey.create(Registries.TRIM_MATERIAL, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "ender_pearl"));
}
