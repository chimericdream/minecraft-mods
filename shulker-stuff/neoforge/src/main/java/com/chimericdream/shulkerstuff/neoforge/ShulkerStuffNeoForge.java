package com.chimericdream.shulkerstuff.neoforge;

import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.ShulkerStuffMod;
import com.chimericdream.shulkerstuff.config.ShulkerStuffConfig;
import com.chimericdream.shulkerstuff.loot.SSLootTableModifier;
import com.chimericdream.shulkerstuff.neoforge.registry.LootModifierRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

@Mod(ModInfo.MOD_ID)
public final class ShulkerStuffNeoForge {
    public static final SSLootTableModifier LOOT_TABLE_MODIFIER = new SSLootTableModifier();

    public ShulkerStuffNeoForge(@NotNull IEventBus bus) {
        ShulkerStuffMod.init();

        LootModifierRegistry.LOOT_MODIFIERS.register(bus);

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (client, parent) -> ShulkerStuffConfig.configScreen(parent)
        );
    }
}
