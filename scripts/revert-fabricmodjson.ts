import {exists, readFile, rm, writeFile} from 'node:fs/promises';
import path from 'node:path';

import projectList from '../project-list.json';
import {getProjectFolder, loadProperties} from "./util/shared.ts";

for (const project of projectList) {
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
