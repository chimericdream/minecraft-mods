package com.chimericdream.archaeologytweaks;

import com.chimericdream.archaeologytweaks.block.ModBlocks;
import com.chimericdream.archaeologytweaks.registry.ModRegistries;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public final class ArchaeologyTweaksMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LoggerFactory.getLogger(ModInfo.MOD_ID);

    public static final ModBlocks BLOCKS;

    static {
        BLOCKS = new ModBlocks();
    }

//    @Override
//    public void onInitialize() {
//        LOGGER.info("[archtweaks] Registering blocks");
//        BLOCKS.register();
//
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> content.addAfter(
//            Items.SUSPICIOUS_GRAVEL,
//            ModBlocks.SUSPICIOUS_CLAY,
//            ModBlocks.SUSPICIOUS_DIRT,
//            ModBlocks.SUSPICIOUS_MUD,
//            ModBlocks.SUSPICIOUS_PACKED_MUD,
//            ModBlocks.SUSPICIOUS_RED_SAND,
//            ModBlocks.SUSPICIOUS_ROOTED_DIRT,
//            ModBlocks.SUSPICIOUS_SOUL_SAND,
//            ModBlocks.SUSPICIOUS_SOUL_SOIL
//        ));
//    }

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        ModRegistries.init();
    }
}
