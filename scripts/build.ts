import {resolveSelectedProjects} from './util/shared.ts';
import {clean} from './clean.ts';
import {updatePatchouliBooks} from './update-patchouli-books.ts';
import {buildGradle} from './build-gradle.ts';
import {createModpacks} from './create-modpacks.ts';

const projects = await resolveSelectedProjects();

console.log(`Building: ${projects.join(', ')}`);

await clean(projects);
await updatePatchouliBooks(projects);

await buildGradle(projects);
await createModpacks(projects);
