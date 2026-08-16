import {copyFile, mkdir, rm} from 'node:fs/promises';
import path from 'node:path';

import {getProjectFolder, loadProperties, resolveSelectedProjects} from "./util/shared.ts";

const FABRIC_PACK_DIR = path.join(__dirname, '..', 'build', 'modpacks', 'fabric');
const NEOFORGE_PACK_DIR = path.join(__dirname, '..', 'build', 'modpacks', 'neoforge');

const prepareDirectories = async () => {
    await rm(path.join(__dirname, '..', 'build', 'modpacks'), {recursive: true, force: true});

    await mkdir(FABRIC_PACK_DIR, {recursive: true});
    await mkdir(NEOFORGE_PACK_DIR, {recursive: true});
};

export const createModpacks = async (projects: string[]): Promise<void> => {
    await prepareDirectories();

    for (const project of projects) {
        const properties = await loadProperties(project);
        const projectFolder = getProjectFolder(project);

        const fabricFileName = `${properties.archives_name}-fabric-${properties.minecraft_compat}-${properties.mod_version}.jar`;
        const neoForgeFileName = `${properties.archives_name}-neoforge-${properties.minecraft_compat}-${properties.mod_version}.jar`;

        const fabricFile = path.join(projectFolder, 'fabric', 'build', 'libs', fabricFileName);
        const neoforgeFile = path.join(projectFolder, 'neoforge', 'build', 'libs', neoForgeFileName);

        await copyFile(fabricFile, path.join(__dirname, '..', 'build', 'modpacks', 'fabric', fabricFileName));
        await copyFile(neoforgeFile, path.join(__dirname, '..', 'build', 'modpacks', 'neoforge', neoForgeFileName));
    }
};

if (import.meta.main) {
    await createModpacks(await resolveSelectedProjects());
}
