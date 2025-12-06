package com.chimericdream.minekea.block.building.storage;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.util.Identifier;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class BlueEggCrateBlock extends EggCrateBlock {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "storage/blue_egg_crate");

    public BlueEggCrateBlock() {
        super(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID));
    }
}
