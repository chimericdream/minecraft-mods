import path from "node:path";

export type ModProperties = {
    mod_id: string
    mod_name: string;
    mod_description: string;
    mod_version: string;
    maven_group: string;
    archives_name: string;
    enabled_platforms: string;
    has_patchouli_guide?: boolean;
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
