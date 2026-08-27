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
    has_fabric_datagen?: boolean;
    has_neoforge_datagen?: boolean;
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

const extractFlagValue = (argv: string[], flag: string): string | undefined => {
    for (let i = 0; i < argv.length; i++) {
        const arg = argv[i];

        if (arg.startsWith(`${flag}=`)) {
            return arg.slice(flag.length + 1);
        }

        if (arg === flag) {
            return argv[i + 1];
        }
    }

    return undefined;
};

const parseIds = (value: string | undefined): string[] =>
    (value ?? '').split(',').map(id => id.trim().toLowerCase()).filter(Boolean);

/**
 * Resolves the list of project folder names to operate on, based on `--mods=<id,id,...>` (or
 * `--mods <id,id,...>`) and `--exclude=<id,id,...>` (or `--exclude <id,id,...>`) CLI arguments.
 * Values are matched against each project's `mod_id` (from gradle.properties) or its folder name,
 * case-insensitively. With no `--mods` argument, the base set is every project in
 * project-list.json; `--exclude` then removes matching projects from that base set (whether it
 * came from `--mods` or the full list), preserving project-list.json's ordering.
 */
export const resolveSelectedProjects = async (argv: string[] = process.argv): Promise<string[]> => {
    const requestedIds = parseIds(extractFlagValue(argv, '--mods'));
    const excludedIds = parseIds(extractFlagValue(argv, '--exclude'));

    if (requestedIds.length === 0 && excludedIds.length === 0) {
        return projectList;
    }

    const aliasesByProject = new Map<string, string[]>();

    for (const project of projectList) {
        const properties = await loadProperties(project);
        aliasesByProject.set(project, [properties.mod_id.toLowerCase(), project.toLowerCase()]);
    }

    const resolveIds = (ids: string[], flagName: string): string[] => {
        const resolved: string[] = [];
        const unknown: string[] = [];

        for (const id of ids) {
            const project = projectList.find(candidate => aliasesByProject.get(candidate)!.includes(id));

            if (!project) {
                unknown.push(id);
            } else if (!resolved.includes(project)) {
                resolved.push(project);
            }
        }

        if (unknown.length > 0) {
            const available = projectList
                .map(project => aliasesByProject.get(project)![0])
                .sort()
                .join(', ');

            throw new Error(`Unknown ${flagName} value(s): ${unknown.join(', ')}. Available mod ids: ${available}`);
        }

        return resolved;
    };

    const base = requestedIds.length > 0 ? resolveIds(requestedIds, '--mods') : projectList;
    const excluded = new Set(resolveIds(excludedIds, '--exclude'));

    return base.filter(project => !excluded.has(project));
};

export const isFullProjectSet = (projects: string[]): boolean =>
    projects.length === projectList.length && projects.every((project, i) => project === projectList[i]);
