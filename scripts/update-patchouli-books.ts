import {cp, exists, mkdir, rm, writeFile} from 'node:fs/promises';
import path from 'node:path';

import projectList from '../project-list.json';
import {getProjectFolder, loadProperties} from "./util/shared.ts";

const getAssetDir = (loader: string, projectFolder: string, modId: string): string => path.join(projectFolder, loader, 'src', 'main', 'resources', 'assets', modId);
const getDataDir = (loader: string, projectFolder: string, modId: string): string => path.join(projectFolder, loader, 'src', 'main', 'resources', 'data', modId);

for (const project of projectList) {
    const properties = await loadProperties(project);
    const projectFolder = getProjectFolder(project);

    if (!properties.has_patchouli_guide) {
        continue;
    }

    const fabricAssetDir = getAssetDir('fabric', projectFolder, properties.mod_id);
    const fabricDataDir = getDataDir('fabric', projectFolder, properties.mod_id);
    const neoforgeAssetDir = getAssetDir('neoforge', projectFolder, properties.mod_id);
    const neoforgeDataDir = getDataDir('neoforge', projectFolder, properties.mod_id);

    await rm(path.join(neoforgeAssetDir, 'patchouli_books'), {force: true, recursive: true});
    await rm(path.join(neoforgeDataDir, 'patchouli_books'), {force: true, recursive: true});

    await mkdir(path.join(neoforgeAssetDir, 'patchouli_books'), {recursive: true});
    await mkdir(path.join(neoforgeDataDir, 'patchouli_books'), {recursive: true});

    await cp(
        path.join(fabricAssetDir, 'patchouli_books'),
        path.join(neoforgeAssetDir, 'patchouli_books'),
        {recursive: true}
    );

    await cp(
        path.join(fabricDataDir, 'patchouli_books'),
        path.join(neoforgeDataDir, 'patchouli_books'),
        {recursive: true}
    );

    const hasGitignore = await exists(path.join(projectFolder, 'neoforge', '.gitignore'));

    if (!hasGitignore) {
        const gitignore = [
            `/src/main/resources/assets/${properties.mod_id}/patchouli_books/`,
            `/src/main/resources/data/${properties.mod_id}/patchouli_books/`,
        ];

        await writeFile(path.join(projectFolder, 'neoforge', '.gitignore'), gitignore.join('\n').concat('\n'));
    }
}
