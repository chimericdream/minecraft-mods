import {copyFile, exists, readFile, writeFile} from 'node:fs/promises';
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
        const updatedJson = modJson.replace(/^( +)"depends"/gm, `$1"accessWidener": "${awFileName}",\n$1"depends"`);
        await writeFile(path.join(projectFolder, 'fabric', 'src', 'main', 'resources', 'fabric.mod.json'), updatedJson);

        await copyFile(
            path.join(projectFolder, 'common', 'src', 'main', 'resources', awFileName),
            path.join(projectFolder, 'fabric', 'src', 'main', 'resources', awFileName)
        );
    }
}
