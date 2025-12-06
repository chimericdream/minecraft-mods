package com.chimericdream.minekea.block.building.storage;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.util.Identifier;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class BrownEggCrateBlock extends EggCrateBlock {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "storage/brown_egg_crate");

    public BrownEggCrateBlock() {
        super(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID));
    }
}
