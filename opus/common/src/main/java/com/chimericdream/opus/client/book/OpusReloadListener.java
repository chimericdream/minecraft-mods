package com.chimericdream.opus.client.book;

import com.chimericdream.opus.ModInfo;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

/**
 * Reloads {@link BookRepository} whenever client resources reload.
 *
 * <p>UNVERIFIED: never compiled. Structure copied from athenaeum's {@code AthenaeumReloadListener}, with
 * {@code PackType.CLIENT_RESOURCES} because books live under {@code assets/}.
 */
public class OpusReloadListener implements ResourceManagerReloadListener {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "book_resource_listener");

    public static void register() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new OpusReloadListener(), ID);
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        BookRepository.reload(manager);
    }

    @Override
    public @NotNull String getName() {
        return ID.toString();
    }
}
