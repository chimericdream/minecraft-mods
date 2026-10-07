package com.chimericdream.opus;

import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.opus.command.OpusCommand;
import com.chimericdream.opus.component.OpusComponentTypes;
import com.chimericdream.opus.item.OpusItems;
import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class OpusMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        // Touch the holder classes so their static registrations run before REGISTRY_HELPER.init() flushes.
        OpusComponentTypes.init();
        OpusItems.init();

        CommandRegistrationEvent.EVENT.register((dispatcher, context, selection) -> OpusCommand.register(dispatcher));

        REGISTRY_HELPER.init();
    }
}
