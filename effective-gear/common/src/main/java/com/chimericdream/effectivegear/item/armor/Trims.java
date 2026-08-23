package com.chimericdream.effectivegear.item.armor;

import com.chimericdream.effectivegear.ModInfo;
import com.chimericdream.lib.trims.TrimMaterialConfig;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class Trims {
    public static final TrimMaterialConfig BLAZE_POWDER = new TrimMaterialConfig(
        ModInfo.MOD_ID,
        "blaze_powder",
        0xE6A029, "trims/color_palettes/blaze_powder",
        "Blaze Powder Material"
    );
    public static final TrimMaterialConfig ECHO_SHARD = new TrimMaterialConfig(
        ModInfo.MOD_ID,
        "echo_shard",
        0x3E5C5C,
        "trims/color_palettes/echo_shard",
        "Echo Shard Material"
    );
    public static final TrimMaterialConfig ENCHANTED_GOLDEN_APPLE = new TrimMaterialConfig(
        ModInfo.MOD_ID,
        "enchanted_golden_apple",
        0xF6C13D,
        "trims/color_palettes/enchanted_golden_apple",
        "Enchanted Golden Apple Material"
    );
    public static final TrimMaterialConfig ENDER_PEARL = new TrimMaterialConfig(
        ModInfo.MOD_ID,
        "ender_pearl",
        0x258474,
        "trims/color_palettes/ender_pearl",
        "Ender Pearl Material"
    );
    public static final TrimMaterialConfig HONEYCOMB = new TrimMaterialConfig(
        ModInfo.MOD_ID,
        "honeycomb",
        0xD9A22C,
        "trims/color_palettes/honeycomb",
        "Honeycomb Material"
    );
    public static final TrimMaterialConfig NETHER_STAR = new TrimMaterialConfig(
        ModInfo.MOD_ID,
        "nether_star",
        0xFDF4B8,
        "trims/color_palettes/nether_star",
        "Nether Star Material"
    );
    public static final TrimMaterialConfig SLIMEBALL = new TrimMaterialConfig(
        ModInfo.MOD_ID,
        "slimeball",
        0x7CC93E,
        "trims/color_palettes/slimeball",
        "Slimeball Material"
    );
    public static final TrimMaterialConfig TURTLE_SCUTE = new TrimMaterialConfig(
        ModInfo.MOD_ID,
        "turtle_scute",
        0x6B8E4E,
        "trims/color_palettes/turtle_scute",
        "Turtle Scute Material"
    );

    /** Every custom trim material this mod defines. Add a future material here. */
    public static final List<TrimMaterialConfig> MATERIALS = List.of(
        BLAZE_POWDER,
        ECHO_SHARD,
        ENCHANTED_GOLDEN_APPLE,
        ENDER_PEARL,
        HONEYCOMB,
        NETHER_STAR,
        SLIMEBALL,
        TURTLE_SCUTE
    );

    public static final ResourceKey<TrimMaterial> BLAZE_POWDER_TRIM_ID = trimKey(BLAZE_POWDER);
    public static final ResourceKey<TrimMaterial> ECHO_SHARD_TRIM_ID = trimKey(ECHO_SHARD);
    public static final ResourceKey<TrimMaterial> ENCHANTED_GOLDEN_APPLE_TRIM_ID = trimKey(ENCHANTED_GOLDEN_APPLE);
    public static final ResourceKey<TrimMaterial> ENDER_PEARL_TRIM_ID = trimKey(ENDER_PEARL);
    public static final ResourceKey<TrimMaterial> HONEYCOMB_TRIM_ID = trimKey(HONEYCOMB);
    public static final ResourceKey<TrimMaterial> NETHER_STAR_TRIM_ID = trimKey(NETHER_STAR);
    public static final ResourceKey<TrimMaterial> SLIMEBALL_TRIM_ID = trimKey(SLIMEBALL);
    public static final ResourceKey<TrimMaterial> TURTLE_SCUTE_TRIM_ID = trimKey(TURTLE_SCUTE);

    private static ResourceKey<TrimMaterial> trimKey(TrimMaterialConfig config) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, Identifier.fromNamespaceAndPath(config.namespace(), config.id()));
    }
}
