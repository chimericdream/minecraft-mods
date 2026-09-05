package com.chimericdream.sneakytweaks.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

import com.chimericdream.sneakytweaks.SneakyTweaksMod;
import com.chimericdream.sneakytweaks.ModInfo;
import com.chimericdream.sneakytweaks.campfire.CampfireGraceHolder;
import com.chimericdream.sneakytweaks.config.SneakyTweaksConfig;
import com.chimericdream.sneakytweaks.neoforge.campfire.CampfireGraceHolderImpl;

@Mod(ModInfo.MOD_ID)
public final class SneakyTweaksNeoForge {
    public SneakyTweaksNeoForge(@NotNull IEventBus bus) {
        SneakyTweaksMod.init();

        CampfireGraceHolder.setProvider(new CampfireGraceHolderImpl());
        CampfireGraceHolderImpl.ATTACHMENT_TYPES.register(bus);

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (client, parent) -> SneakyTweaksConfig.configScreen(parent)
        );
    }
}
