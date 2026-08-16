import {resolveSelectedProjects} from './util/shared.ts';
import {runGradle} from './util/gradle.ts';

export const buildGradle = (projects: string[]): Promise<void> => runGradle(projects, 'build');

if (import.meta.main) {
    await buildGradle(await resolveSelectedProjects());
}
