package com.chimericdream.modstatus;

/**
 * The bits of a mod folder the Project View cares about: what {@code gradle.properties} says the
 * next/current version is, and whether {@code CHANGELOG.md} has anything sitting under
 * {@code ### Unreleased changes}.
 */
public record ModStatus(String modId, String version, boolean hasUnreleasedChanges) {
}
