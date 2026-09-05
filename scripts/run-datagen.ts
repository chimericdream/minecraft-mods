import {$} from 'bun';

import {loadProperties, resolveSelectedProjects} from './util/shared.ts';

/**
 * Runs data generation for every selected project that declares it via `has_fabric_datagen` /
 * `has_neoforge_datagen` in its gradle.properties (see CLAUDE.md). Fabric Loom's datagen run config
 * is named `datagen`, producing task `runDatagen`; NeoForge's is named `data`, producing `runData` —
 * both per the `runs { ... }` blocks in each platform's build.gradle.
 */
export const runDatagen = async (projects: string[]): Promise<void> => {
    const taskPaths: string[] = [];

    for (const project of projects) {
        const properties = await loadProperties(project);
        const platforms = properties.enabled_platforms.split(',').map(platform => platform.trim()).filter(Boolean);

        if (properties.has_fabric_datagen && platforms.includes('fabric')) {
            taskPaths.push(`:${project}:fabric:runDatagen`);
        }

        if (properties.has_neoforge_datagen && platforms.includes('neoforge')) {
            taskPaths.push(`:${project}:neoforge:runData`);
        }
    }

    if (taskPaths.length === 0) {
        console.log('No selected projects declare data generation. Nothing to do.');
        return;
    }

    console.log(`Running data generation for: ${taskPaths.join(', ')}`);

    await $`./gradlew ${taskPaths}`;
};

if (import.meta.main) {
    await runDatagen(await resolveSelectedProjects());
}
