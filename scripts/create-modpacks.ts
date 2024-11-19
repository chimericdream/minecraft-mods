import {copyFile, mkdir, rm} from 'node:fs/promises';
import path from 'node:path';

type ModProperties = {
    mod_id: string
    mod_name: string;
    mod_description: string;
    mod_version: string;
    maven_group: string;
    archives_name: string;
    enabled_platforms: string;
};

const FABRIC_PACK_DIR = path.join(__dirname, '..', 'build', 'modpacks', 'fabric');
const NEOFORGE_PACK_DIR = path.join(__dirname, '..', 'build', 'modpacks', 'neoforge');

const prepareDirectories = async () => {
    await rm(path.join(__dirname, '..', 'build', 'modpacks'), {recursive: true, force: true});

    await mkdir(FABRIC_PACK_DIR, {recursive: true});
    await mkdir(NEOFORGE_PACK_DIR, {recursive: true});
};

const getProjectFolder = (project: string) => path.join(__dirname, '..', project);

const loadProperties = async (project: string): Promise<ModProperties> => {
    const file = Bun.file(path.join(getProjectFolder(project), 'gradle.properties'));
    const contents = await file.text();

    const lines = contents.split(/\r?\n/)
        .filter(line => line.trim().length > 0)
        .filter(line => !line.startsWith('#'));

    return lines.reduce((acc, line) => {
        const [key, value] = line.split('=').map(part => part.trim());
        return {
            ...acc,
            [key]: value
        };
    }, {} as ModProperties);
}

const projectList = [
    'archaeology-tweaks',
    'athenaeum',
    'banner-tweaks',
    'beacon-conduit-tweaks',
    'blacklight',
    'chimeric-lib',
    'cobblicious',
    'enchantment-numbers-fix',
    'flat-bedrock',
    'hang-from-slabs',
    'hopper-xtreme',
    'houdini-block',
    'jdcrafte',
    'minekea',
    'miniblock-merchants',
    'pannotia-companion',
    'playgrounds',
    'shulker-stuff',
    'sponj',
    'villager-tweaks'
];

const createModpacks = async () => {
    await prepareDirectories();

    for (const project of projectList) {
        const properties = await loadProperties(project);
        const projectFolder = getProjectFolder(project);

        const fabricFileName = `${properties.archives_name}-fabric-${properties.mod_version}.jar`;
        const neoForgeFileName = `${properties.archives_name}-neoforge-${properties.mod_version}.jar`;

        const fabricFile = path.join(projectFolder, 'fabric', 'build', 'libs', fabricFileName);
        const neoforgeFile = path.join(projectFolder, 'neoforge', 'build', 'libs', neoForgeFileName);

        await copyFile(fabricFile, path.join(__dirname, '..', 'build', 'modpacks', 'fabric', fabricFileName));
        await copyFile(neoforgeFile, path.join(__dirname, '..', 'build', 'modpacks', 'neoforge', neoForgeFileName));
    }
};

void createModpacks();
