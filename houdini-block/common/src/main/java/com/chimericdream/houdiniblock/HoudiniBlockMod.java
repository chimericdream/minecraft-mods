package com.chimericdream.houdiniblock;

import com.chimericdream.houdiniblock.registry.ModRegistries;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class HoudiniBlockMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        ModRegistries.init();
    }
}
