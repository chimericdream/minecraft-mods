package com.chimericdream.athenaeum;

import com.chimericdream.athenaeum.registries.AthenaeumRegistries;
import com.google.common.base.Predicates;
import dev.architectury.registry.ReloadListenerRegistry;
import java.io.InputStream;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class AthenaeumReloadListener implements ResourceManagerReloadListener {
    public static void register() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new AthenaeumReloadListener(), ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "athenaeum_book_resource_listener"));
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        AthenaeumRegistries.BOOKS.clear();

        Map<ResourceLocation, Resource> resources = manager.listResources("athenaeum_books", Predicates.alwaysTrue());
        resources.forEach((id, resource) -> {
            ResourceLocation bookId = ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                id.getPath()
                    .replace("athenaeum_books/", "")
                    .replace(".json", "")
            );

            try (InputStream stream = resource.open()) {
                AthenaeumRegistries.BOOKS.addFromInputStream(bookId, stream);
            } catch (Exception e) {
                AthenaeumMod.LOGGER.error("Error occurred while loading resource json" + id.toString(), e);
            }
        });
    }

    @Override
    public @NotNull String getName() {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "athenaeum_book_resource_listener").toString();
    }
}
