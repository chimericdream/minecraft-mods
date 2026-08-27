import path from 'node:path';

import {getProjectFolder, loadProperties, resolveSelectedProjects} from './util/shared.ts';

const UNRELEASED_HEADING = '### Unreleased changes';

/**
 * A mod "has unreleased changes" when its CHANGELOG.md's `### Unreleased changes` section (the
 * text between that heading and the next `### ` heading, or EOF) contains any non-whitespace
 * content. A freshly-cut release leaves that section empty until new work lands under it (see
 * CLAUDE.md's Versioning & releases section).
 */
const hasUnreleasedChanges = async (project: string): Promise<boolean> => {
    const file = Bun.file(path.join(getProjectFolder(project), 'CHANGELOG.md'));

    if (!(await file.exists())) {
        return false;
    }

    const contents = await file.text();
    const headingIndex = contents.indexOf(UNRELEASED_HEADING);

    if (headingIndex === -1) {
        return false;
    }

    const afterHeading = contents.slice(headingIndex + UNRELEASED_HEADING.length);
    const nextHeadingIndex = afterHeading.search(/\n###\s/);
    const section = nextHeadingIndex === -1 ? afterHeading : afterHeading.slice(0, nextHeadingIndex);

    return section.trim().length > 0;
};

const parseFlags = (argv: string[]): {showVersions: boolean; showUnreleased: boolean} => {
    const showVersions = argv.includes('--versions');
    const showUnreleased = argv.includes('--unreleased');

    if (!showVersions && !showUnreleased) {
        return {showVersions: true, showUnreleased: true};
    }

    return {showVersions, showUnreleased};
};

/**
 * Prints mod version numbers and/or which mods have unreleased changelog entries, per the
 * `--versions` / `--unreleased` flags (both, if neither is given). Supports the same
 * `--mods=<id,id,...>` / `--exclude=<id,id,...>` scoping as the build/datagen scripts.
 */
export const printModStatus = async (projects: string[], argv: string[] = process.argv): Promise<void> => {
    const {showVersions, showUnreleased} = parseFlags(argv);

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

    if (showUnreleased) {
        if (showVersions) {
            console.log('');
        }

        const unreleased: string[] = [];

        for (const project of projects) {
            if (await hasUnreleasedChanges(project)) {
                unreleased.push(project);
            }
        }

        console.log('Mods with unreleased changes:');
        if (unreleased.length === 0) {
            console.log('  (none)');
        } else {
            for (const project of unreleased) {
                console.log(`  ${project}`);
            }
        }
    }
};

if (import.meta.main) {
    await printModStatus(await resolveSelectedProjects());
}
