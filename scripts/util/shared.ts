import path from "node:path";

import projectList from '../../project-list.json';

export type ModProperties = {
    mod_id: string
    mod_name: string;
    mod_description: string;
    mod_version: string;
    maven_group: string;
    archives_name: string;
    enabled_platforms: string;
    has_patchouli_guide?: boolean;
    architectury_compat: string;
    chimericlib_compat: string;
    fabric_compat?: string;
    minecraft_compat: string;
};

export const getProjectFolder = (project: string) => path.join(__dirname, '..', '..', project);

export const loadProperties = async (project: string): Promise<ModProperties> => {
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

/**
 * Resolves the list of project folder names to operate on, based on a `--mods=<id,id,...>` (or
 * `--mods <id,id,...>`) CLI argument. Values are matched against each project's `mod_id` (from
 * gradle.properties) or its folder name, case-insensitively. With no `--mods` argument, returns
 * every project in project-list.json (i.e. a full build), preserving that file's ordering.
 */
export const resolveSelectedProjects = async (argv: string[] = process.argv): Promise<string[]> => {
    let modsValue: string | undefined;

    for (let i = 0; i < argv.length; i++) {
        const arg = argv[i];

        if (arg.startsWith('--mods=')) {
            modsValue = arg.slice('--mods='.length);
            break;
        }

        if (arg === '--mods') {
            modsValue = argv[i + 1];
            break;
        }
    }

    if (!modsValue) {
        return projectList;
    }

    const requestedIds = modsValue.split(',').map(id => id.trim().toLowerCase()).filter(Boolean);

    if (requestedIds.length === 0) {
        return projectList;
    }

    const aliasesByProject = new Map<string, string[]>();

    for (const project of projectList) {
        const properties = await loadProperties(project);
        aliasesByProject.set(project, [properties.mod_id.toLowerCase(), project.toLowerCase()]);
    }

    const selected: string[] = [];
    const unknown: string[] = [];

    for (const id of requestedIds) {
        const project = projectList.find(candidate => aliasesByProject.get(candidate)!.includes(id));

        if (!project) {
            unknown.push(id);
        } else if (!selected.includes(project)) {
            selected.push(project);
        }
    }

    if (unknown.length > 0) {
        const available = projectList
            .map(project => aliasesByProject.get(project)![0])
            .sort()
            .join(', ');

        throw new Error(`Unknown --mods value(s): ${unknown.join(', ')}. Available mod ids: ${available}`);
    }

    return selected;
};

export const isFullProjectSet = (projects: string[]): boolean =>
    projects.length === projectList.length && projects.every((project, i) => project === projectList[i]);
