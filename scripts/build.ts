import {resolveSelectedProjects} from './util/shared.ts';
import {clean} from './clean.ts';
import {copyAccessWideners} from './copy-accesswideners.ts';
import {updatePatchouliBooks} from './update-patchouli-books.ts';
import {buildGradle} from './build-gradle.ts';
import {createModpacks} from './create-modpacks.ts';
import {revertFabricModJson} from './revert-fabricmodjson.ts';

const projects = await resolveSelectedProjects();

console.log(`Building: ${projects.join(', ')}`);

await clean(projects);
await copyAccessWideners(projects);
await updatePatchouliBooks(projects);

try {
    await buildGradle(projects);
    await createModpacks(projects);
} finally {
    // Always strip the transient fabric.mod.json/accesswidener edits made above, even if the
    // gradle build failed, so they never end up sitting in a commit (see CLAUDE.md).
    await revertFabricModJson(projects);
}
