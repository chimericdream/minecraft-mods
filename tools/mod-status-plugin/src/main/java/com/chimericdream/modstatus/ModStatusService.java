package com.chimericdream.modstatus;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches the parsed mod status per directory. The Project View asks for a node's decoration far more
 * often than the underlying files change, so without this every tree update would re-read
 * {@code gradle.properties} and {@code CHANGELOG.md} for every folder in the repo.
 *
 * <p>Entries are dropped by {@link ModStatusFileListener} when either file is touched.
 */
@Service(Service.Level.PROJECT)
public final class ModStatusService {
    /**
     * Stand-in for "this directory is not a mod folder". {@link ConcurrentHashMap} cannot hold null
     * values, and caching the negative answer matters: most directories in the tree are not mods.
     */
    private static final ModStatus NOT_A_MOD = new ModStatus("", "", false, false);

    private final Map<String, ModStatus> cache = new ConcurrentHashMap<>();

    /** Returns the mod status for a directory, or {@code null} if it is not a mod folder. */
    public @Nullable ModStatus getStatus(@NotNull VirtualFile directory) {
        if (!directory.isValid() || !directory.isDirectory()) {
            return null;
        }

        ModStatus status = cache.computeIfAbsent(directory.getPath(), path -> {
            ModStatus read = ModStatusReader.read(directory);
            return read == null ? NOT_A_MOD : read;
        });

        return status == NOT_A_MOD ? null : status;
    }

    /** Drops the cached status for a single directory path. */
    public void invalidate(@NotNull String directoryPath) {
        cache.remove(directoryPath);
    }

    /** Drops every cached status. */
    public void invalidateAll() {
        cache.clear();
    }
}
