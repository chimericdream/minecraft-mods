import {$} from 'bun';

import {isFullProjectSet, loadProperties} from './shared.ts';

/**
 * Runs a Gradle lifecycle task (`build` or `clean`) against a set of projects. When `projects` is
 * every project in project-list.json, this just runs the unqualified task (`./gradlew build`),
 * matching the previous full-build behavior. Otherwise it targets each selected mod's container
 * project, its `common` subproject, and each of its `enabled_platforms` subprojects directly, so
 * unselected mods are left untouched.
 */
export const runGradle = async (projects: string[], taskName: 'build' | 'clean'): Promise<void> => {
    if (isFullProjectSet(projects)) {
        await $`./gradlew ${taskName}`;
        return;
    }

    const taskPaths: string[] = [];

    for (const project of projects) {
        const properties = await loadProperties(project);
        const platforms = properties.enabled_platforms.split(',').map(platform => platform.trim()).filter(Boolean);

        taskPaths.push(`:${project}:${taskName}`);
        taskPaths.push(`:${project}:common:${taskName}`);

        for (const platform of platforms) {
            taskPaths.push(`:${project}:${platform}:${taskName}`);
        }
    }

    console.log(`Running './gradlew ${taskName}' for: ${projects.join(', ')}`);

    await $`./gradlew ${taskPaths}`;
};
