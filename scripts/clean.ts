import {resolveSelectedProjects} from './util/shared.ts';
import {runGradle} from './util/gradle.ts';

export const clean = (projects: string[]): Promise<void> => runGradle(projects, 'clean');

if (import.meta.main) {
    await clean(await resolveSelectedProjects());
}
