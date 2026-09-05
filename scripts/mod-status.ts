import path from 'node:path';

import {getProjectFolder, loadProperties, resolveSelectedProjects} from './util/shared.ts';

const UNRELEASED_HEADING = '### Unreleased changes';

/** Matches the start of the heading that ends the "Unreleased changes/Initial release" section. */
const NEXT_HEADING = /\n###\s/;

type ChangelogStatus = {
    /**
     * A mod "has unreleased changes" when its CHANGELOG.md's `### Unreleased changes` section (the
     * text between that heading and the next `### ` heading, or EOF) contains any non-whitespace
     * content. A freshly-cut release leaves that section empty until new work lands under it (see
     * CLAUDE.md's Versioning & releases section).
     */
    hasUnreleasedChanges: boolean;
    /**
     * A mod "is new" (never released) when there are no further `### ` headings after the initial
     * `### Unreleased changes` line.
     */
    isNewMod: boolean;
};

const NOT_FOUND: ChangelogStatus = {hasUnreleasedChanges: false, isNewMod: false};

/**
 * Mirrors `ModStatusReader`'s parsing rules (see
 * `tools/mod-status-plugin/src/main/java/com/chimericdream/modstatus/ModStatusReader.java`) so the
 * gutter label and this CLI never disagree.
 */
const readChangelogStatus = async (project: string): Promise<ChangelogStatus> => {
    const file = Bun.file(path.join(getProjectFolder(project), 'CHANGELOG.md'));

    if (!(await file.exists())) {
        return NOT_FOUND;
    }

    const contents = await file.text();
    const headingIndex = contents.indexOf(UNRELEASED_HEADING);

    if (headingIndex === -1) {
        return NOT_FOUND;
    }

    const afterHeading = contents.slice(headingIndex + UNRELEASED_HEADING.length);
    const nextHeadingMatch = afterHeading.match(NEXT_HEADING);
    const section = nextHeadingMatch ? afterHeading.slice(0, nextHeadingMatch.index) : afterHeading;

    return {
        hasUnreleasedChanges: section.trim().length > 0,
        isNewMod: !nextHeadingMatch,
    };
};

const parseFlags = (argv: string[]): {showVersions: boolean; showUnreleased: boolean; showNew: boolean} => {
    const showVersions = argv.includes('--versions');
    const showUnreleased = argv.includes('--unreleased');
    const showNew = argv.includes('--new');

    if (!showVersions && !showUnreleased && !showNew) {
        return {showVersions: true, showUnreleased: true, showNew: true};
    }

    return {showVersions, showUnreleased, showNew};
};

/**
 * Prints mod version numbers, which mods have unreleased changelog entries, and/or which mods have
 * never been released, per the `--versions` / `--unreleased` / `--new` flags (all three, if none is
 * given). Supports the same `--mods=<id,id,...>` / `--exclude=<id,id,...>` scoping as the
 * build/datagen scripts.
 */
export const printModStatus = async (projects: string[], argv: string[] = process.argv): Promise<void> => {
    const {showVersions, showUnreleased, showNew} = parseFlags(argv);

    if (showVersions) {
        const rows = await Promise.all(projects.map(async project => {
            const properties = await loadProperties(project);
            return {project, version: properties.mod_version};
        }));

        const width = Math.max(...rows.map(row => row.project.length));

        console.log('Mod versions:');
        for (const row of rows) {
            console.log(`  ${row.project.padEnd(width)}  ${row.version}`);
        }
    }

    if (showUnreleased || showNew) {
        const statusesByProject = new Map(await Promise.all(
            projects.map(async project => [project, await readChangelogStatus(project)] as const)
        ));

        if (showUnreleased) {
            if (showVersions) {
                console.log('');
            }

            const unreleased = projects.filter(project => statusesByProject.get(project)!.hasUnreleasedChanges);

            console.log('Mods with unreleased changes:');
            if (unreleased.length === 0) {
                console.log('  (none)');
            } else {
                for (const project of unreleased) {
                    console.log(`  ${project}`);
                }
            }
        }

        if (showNew) {
            if (showVersions || showUnreleased) {
                console.log('');
            }

            const neverReleased = projects.filter(project => statusesByProject.get(project)!.isNewMod);

            console.log('Mods never released:');
            if (neverReleased.length === 0) {
                console.log('  (none)');
            } else {
                for (const project of neverReleased) {
                    console.log(`  ${project}`);
                }
            }
        }
    }
};

if (import.meta.main) {
    await printModStatus(await resolveSelectedProjects());
}
