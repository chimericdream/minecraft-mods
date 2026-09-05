package com.chimericdream.effectivegear;

import com.chimericdream.effectivegear.block.EGBlocks;
import com.chimericdream.effectivegear.enchantment.ModEnchantments;
import com.chimericdream.effectivegear.network.ServerNetworking;
import com.chimericdream.effectivegear.util.PlayerAbilityState;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class EffectiveGearMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        EGBlocks.init();
        ModEnchantments.init();

        ServerNetworking.init();
        PlayerEvent.PLAYER_QUIT.register(PlayerAbilityState::remove);

        REGISTRY_HELPER.init();
    }
}
