package com.chimericdream.lib.fabric.config;

import com.chimericdream.lib.config.YaclConfig;
import com.chimericdream.lib.config.YaclConfigScreens;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * chimeric-lib's own Mod Menu integration, registered as a {@code modmenu} entrypoint (see
 * {@code fabric.mod.json}) rather than something each consumer mod registers for itself.
 *
 * <p>{@link #getProvidedConfigScreenFactories()} provides a config screen for every mod in
 * {@link YaclConfigScreens}, computed fresh on every call (never cached) so a config registered after
 * this class is first touched still shows up. Mod Menu calls this only when it needs to build its mod
 * list/screen — always after every mod's own {@code main} entrypoint has finished — which is exactly
 * why {@link YaclConfig#init()} only registers into {@link YaclConfigScreens} instead of handing Mod
 * Menu a screen factory directly: Fabric does not order {@code main} entrypoints by {@code depends}, so
 * there's no point at mod-init time that's guaranteed to see every other mod's config already
 * registered. See {@link YaclConfig}'s class javadoc for the full rationale.
 *
 * <p>chimeric-lib deliberately does <b>not</b> depend on Mod Menu or YACL in {@code fabric.mod.json}'s
 * {@code depends} — Mod Menu stays a {@code recommends} (as it always was), and YACL is only ever
 * pulled in by mods that already depend on it themselves. The {@code config} package (this class
 * included) is opt-in: a mod that never calls {@link YaclConfig#builder} never needs Mod Menu or YACL
 * present at all, since {@link YaclConfigScreens#all()} would simply stay empty for it.
 */
@Environment(EnvType.CLIENT)
public final class YaclModMenuIntegration implements ModMenuApi {
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> factories = new LinkedHashMap<>();

        for (YaclConfig<?> config : YaclConfigScreens.all()) {
            factories.put(config.modId(), (ConfigScreenFactory<Screen>) config::screen);
        }

        return factories;
    }
}
