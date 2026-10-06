package com.chimericdream.opus.client.book;

import com.chimericdream.opus.core.book.BookSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Serves one language folder of one book out of the client resource manager, so pages can be overridden or added
 * by any resource pack: {@code assets/<namespace>/opus-books/<book>/<lang>/...}.
 *
 * <p>Compiles against 26.2; not yet run in game. {@code listResources(String, Predicate)} returning a map of {@code Resource}s
 * is taken from athenaeum's {@code AthenaeumReloadListener}.
 */
public final class ResourceBookSource implements BookSource {
    private final ResourceManager manager;
    private final String namespace;
    private final String root;

    /** @param root resource path of the language folder, e.g. {@code opus-books/guide/en_us} (no trailing slash) */
    public ResourceBookSource(ResourceManager manager, String namespace, String root) {
        this.manager = manager;
        this.namespace = namespace;
        this.root = root;
    }

    @Override
    public List<String> list() {
        String prefix = root + "/";

        return manager.listResources(root, id -> id.getNamespace().equals(namespace)).keySet().stream()
            .map(Identifier::getPath)
            .filter(path -> path.startsWith(prefix))
            .map(path -> path.substring(prefix.length()))
            .sorted()
            .toList();
    }

    @Override
    public Optional<String> readText(String path) {
        Optional<Resource> resource = manager.getResource(Identifier.fromNamespaceAndPath(namespace, root + "/" + path));
        if (resource.isEmpty()) {
            return Optional.empty();
        }

        try (InputStream stream = resource.get().open()) {
            return Optional.of(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
