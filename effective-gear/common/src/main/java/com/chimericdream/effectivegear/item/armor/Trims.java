package com.chimericdream.effectivegear.item.armor;

import com.chimericdream.effectivegear.ModInfo;
import com.chimericdream.lib.trims.TrimMaterialConfig;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class Trims {
    public static final TrimMaterialConfig ENDER_PEARL = new TrimMaterialConfig(
        ModInfo.MOD_ID, "ender_pearl", 0x258474, "trims/color_palettes/ender_pearl", "Ender Pearl Material"
    );

    /** Every custom trim material this mod defines. Add a future material here. */
    public static final List<TrimMaterialConfig> MATERIALS = List.of(ENDER_PEARL);

    public static final ResourceKey<TrimMaterial> ENDER_PEARL_TRIM_ID = ResourceKey.create(Registries.TRIM_MATERIAL, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, ENDER_PEARL.id()));
}
