package com.chimericdream.opus.client.item;

import com.chimericdream.opus.OpusMod;
import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.book.BookMeta;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads only the {@code texture} and {@code model} keys of every {@code book.yml}. Item models are baked during the
 * resource reload, in no guaranteed order against {@link com.chimericdream.opus.client.book.BookRepository}, so the
 * item model cannot wait for the repository and scans the packs itself.
 */
public final class BookLookScanner {
    private static final String TEXTURE_FOLDER = "item/";

    private BookLookScanner() {
    }

    public static Map<Identifier, BookLook> scan(ResourceManager manager) {
        Map<Identifier, BookLook> looks = new HashMap<>();

        manager.listResources("opus-books", id -> id.getPath().endsWith("/book.yml")).forEach((id, resource) -> {
            String[] parts = id.getPath().split("/");
            if (parts.length != 3) {
                return;
            }

            Identifier bookId = Identifier.fromNamespaceAndPath(id.getNamespace(), parts[1]);
            BookMeta meta = BookMeta.parse(readText(resource), parts[1], new Diagnostics());

            BookLook look = resolve(manager, meta.texture(), meta.model());
            if (look != null) {
                looks.put(bookId, look);
            }
        });

        return looks;
    }

    /** A missing or misplaced file is not an error here (the validator reports it); the book just keeps the default look. */
    static BookLook resolve(ResourceManager manager, String texture, String model) {
        Identifier modelId = model == null ? null : Identifier.tryParse(model.trim());
        if (modelId != null && manager.getResource(Identifier.fromNamespaceAndPath(modelId.getNamespace(), "models/" + modelId.getPath() + ".json")).isPresent()) {
            return new BookLook(null, modelId);
        }

        Identifier textureId = texture == null ? null : Identifier.tryParse(texture.trim());
        if (textureId != null && textureId.getPath().startsWith(TEXTURE_FOLDER)
            && manager.getResource(Identifier.fromNamespaceAndPath(textureId.getNamespace(), "textures/" + textureId.getPath() + ".png")).isPresent()) {
            return new BookLook(textureId, null);
        }

        return null;
    }

    private static String readText(Resource resource) {
        try (InputStream stream = resource.open()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            OpusMod.LOGGER.error("Could not read a book.yml", e);
            return "";
        }
    }
}
