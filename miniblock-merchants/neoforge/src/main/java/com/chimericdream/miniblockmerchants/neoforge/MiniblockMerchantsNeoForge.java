package com.chimericdream.miniblockmerchants.neoforge;

import com.chimericdream.miniblockmerchants.MiniblockMerchantsMod;
import com.chimericdream.miniblockmerchants.ModInfo;
import com.chimericdream.miniblockmerchants.config.MiniblockMerchantsConfig;
import com.chimericdream.miniblockmerchants.neoforge.registry.LootModifierRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

@Mod(ModInfo.MOD_ID)
public final class MiniblockMerchantsNeoForge {
    public MiniblockMerchantsNeoForge(@NotNull IEventBus bus) {
        MiniblockMerchantsMod.init();
        LootModifierRegistry.LOOT_MODIFIERS.register(bus);

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (client, parent) -> MiniblockMerchantsConfig.configScreen(parent)
        );
    }
}
