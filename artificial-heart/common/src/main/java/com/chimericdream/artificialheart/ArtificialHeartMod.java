package com.chimericdream.artificialheart;

import com.chimericdream.artificialheart.block.ModBlocks;
import com.chimericdream.artificialheart.block.ModDispenserBehaviors;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class ArtificialHeartMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        ModBlocks.init();

        REGISTRY_HELPER.init();
    }

    /**
     * Runs logic that depends on registry objects actually being resolvable via {@code .get()}.
     * On NeoForge, DeferredRegister entries aren't available until RegisterEvent fires, which
     * happens after all mods finish construction - so this must run from a post-registration
     * lifecycle hook (e.g. FMLCommonSetupEvent), not from {@link #init()} itself. On Fabric,
     * registration is synchronous, so calling this immediately after {@link #init()} is safe.
     */
    public static void postInit() {
        ModDispenserBehaviors.init();
    }
}
