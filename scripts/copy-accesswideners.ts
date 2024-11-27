import {copyFile, exists} from 'node:fs/promises';
import path from 'node:path';

import projectList from '../project-list.json';
import {getProjectFolder, loadProperties} from "./util/shared.ts";

for (const project of projectList) {
    const properties = await loadProperties(project);
    const projectFolder = getProjectFolder(project);

    const awFileName = `${properties.mod_id}.accesswidener`;

    const fileExists = await exists(path.join(projectFolder, 'common', 'src', 'main', 'resources', awFileName));

    if (fileExists) {
        await copyFile(
            path.join(projectFolder, 'common', 'src', 'main', 'resources', awFileName),
            path.join(projectFolder, 'fabric', 'src', 'main', 'resources', awFileName)
        );
    }
}
