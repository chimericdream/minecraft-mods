import {exists, readFile, rm, writeFile} from 'node:fs/promises';
import path from 'node:path';

import {getProjectFolder, loadProperties, resolveSelectedProjects} from "./util/shared.ts";

export const revertFabricModJson = async (projects: string[]): Promise<void> => {
    for (const project of projects) {
        const properties = await loadProperties(project);
        const projectFolder = getProjectFolder(project);

        const awFileName = `${properties.mod_id}.accesswidener`;

        const fileExists = await exists(path.join(projectFolder, 'common', 'src', 'main', 'resources', awFileName));

        if (fileExists) {
            const modJson = (await readFile(path.join(projectFolder, 'fabric', 'src', 'main', 'resources', 'fabric.mod.json'))).toString();
            const updatedJson = modJson.replace(/^( +)"accessWidener"[^\n]+\n/gm, '');

            await writeFile(path.join(projectFolder, 'fabric', 'src', 'main', 'resources', 'fabric.mod.json'), updatedJson);
            await rm(path.join(projectFolder, 'fabric', 'src', 'main', 'resources', awFileName), {force: true});
        }
    }
};

if (import.meta.main) {
    await revertFabricModJson(await resolveSelectedProjects());
}
