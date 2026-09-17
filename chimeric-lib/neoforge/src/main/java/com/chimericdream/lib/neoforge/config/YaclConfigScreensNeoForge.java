package com.chimericdream.lib.neoforge.config;

import com.chimericdream.lib.ChimericLib;
import com.chimericdream.lib.config.YaclConfig;
import com.chimericdream.lib.config.YaclConfigScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Supplier;

/**
 * chimeric-lib's own NeoForge config-screen registrar, run once for the whole game rather than
 * something each consumer mod wires up for itself.
 *
 * <p>{@link #onClientSetup(FMLClientSetupEvent)} fires only after every mod's constructor has
 * completed, so it is the first safe point to reach into another mod's {@code ModContainer} — exactly
 * why {@link YaclConfig#init()} (called from a mod's constructor, by way of its common {@code init()})
 * only registers into {@link YaclConfigScreens} instead of registering an extension point directly. See
 * {@link YaclConfig}'s class javadoc for the full rationale, including why the registry holds
 * {@code YaclConfig} objects rather than a {@code Screen}-typed lambda built in common code.
 *
 * <p>{@code FMLClientSetupEvent} is fired in parallel across mods and {@code ModContainer}'s extension
 * point map is not documented as thread-safe, so the actual registration work is deferred with
 * {@link FMLClientSetupEvent#enqueueWork(Runnable)}, which NeoForge runs single-threaded after every
 * mod's parallel client-setup work has finished.
 */
@EventBusSubscriber(modid = ChimericLib.MOD_ID, value = Dist.CLIENT)
public final class YaclConfigScreensNeoForge {
    private YaclConfigScreensNeoForge() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (YaclConfig<?> config : YaclConfigScreens.all()) {
                ModList.get().getModContainerById(config.modId()).ifPresentOrElse(
                    container -> {
                        Supplier<IConfigScreenFactory> factory = () -> (client, parent) -> config.screen(parent);
                        container.registerExtensionPoint(IConfigScreenFactory.class, factory);
                    },
                    () -> ChimericLib.LOGGER.warn(
                        "No NeoForge ModContainer found for mod id '{}'; its YACL config screen will not be registered.",
                        config.modId()
                    )
                );
            }
        });
    }
}
