package com.chimericdream.modstatus;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pure parsing of a mod folder. Kept free of caching and platform state so the rules can be read
 * (and changed) in one place.
 *
 * <p>These rules deliberately mirror {@code scripts/mod-status.ts} (`bun run status`) so the gutter
 * label and the CLI never disagree.
 */
final class ModStatusReader {
    private static final Logger LOG = Logger.getInstance(ModStatusReader.class);

    private static final String GRADLE_PROPERTIES = "gradle.properties";
    private static final String CHANGELOG = "CHANGELOG.md";
    private static final String UNRELEASED_HEADING = "### Unreleased changes";

    /** Matches the start of the heading that ends the "Unreleased changes" section. */
    private static final Pattern NEXT_HEADING = Pattern.compile("\n###\s");

    private ModStatusReader() {
    }

    /**
     * Reads the mod status for a directory, or returns {@code null} if it is not a mod folder.
     *
     * <p>A directory counts as a mod folder when it holds a {@code gradle.properties} declaring both
     * {@code mod_id} and {@code mod_version}. That is what separates the mod roots from the repo
     * root and from each mod's {@code neoforge/} subproject, which also has a
     * {@code gradle.properties} but only sets {@code loom.platform}.
     */
    static @Nullable ModStatus read(@NotNull VirtualFile directory) {
        VirtualFile propertiesFile = directory.findChild(GRADLE_PROPERTIES);

        if (propertiesFile == null || propertiesFile.isDirectory()) {
            return null;
        }

        String propertiesText = loadText(propertiesFile);

        if (propertiesText == null) {
            return null;
        }

        Properties properties = new Properties();

        try {
            properties.load(new StringReader(propertiesText));
        } catch (IOException e) {
            LOG.warn("Could not parse " + propertiesFile.getPath(), e);
            return null;
        }

        String modId = trimmedOrNull(properties.getProperty("mod_id"));
        String version = trimmedOrNull(properties.getProperty("mod_version"));

        if (modId == null || version == null) {
            return null;
        }

        return new ModStatus(modId, version, hasUnreleasedChanges(directory.findChild(CHANGELOG)));
    }

    /**
     * A mod "has unreleased changes" when the text between {@code ### Unreleased changes} and the
     * next {@code ### } heading (or EOF) is not blank. A freshly cut release leaves that section
     * empty until new work lands under it.
     */
    private static boolean hasUnreleasedChanges(@Nullable VirtualFile changelog) {
        if (changelog == null || changelog.isDirectory()) {
            return false;
        }

        String text = loadText(changelog);

        if (text == null) {
            return false;
        }

        int headingIndex = text.indexOf(UNRELEASED_HEADING);

        if (headingIndex == -1) {
            return false;
        }

        String afterHeading = text.substring(headingIndex + UNRELEASED_HEADING.length());
        Matcher nextHeading = NEXT_HEADING.matcher(afterHeading);
        String section = nextHeading.find() ? afterHeading.substring(0, nextHeading.start()) : afterHeading;

        return !section.isBlank();
    }

    private static @Nullable String loadText(@NotNull VirtualFile file) {
        try {
            return VfsUtilCore.loadText(file);
        } catch (IOException e) {
            LOG.warn("Could not read " + file.getPath(), e);
            return null;
        }
    }

    private static @Nullable String trimmedOrNull(@Nullable String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty() ? null : trimmed;
    }
}
