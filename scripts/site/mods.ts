import {existsSync, readFileSync} from 'node:fs';
import path from 'node:path';

import projectList from '../../project-list.json';
import {loadProperties} from '../util/shared.ts';
import type {ModInfo} from './types.ts';
import {git, REPO_ROOT} from './util.ts';

const UNRELEASED_HEADING = '### Unreleased changes';

// Mirrors scripts/mod-status.ts's changelog parsing.
const changelogStatus = (folder: string): {hasUnreleasedChanges: boolean; isNewMod: boolean} => {
    const file = path.join(REPO_ROOT, folder, 'CHANGELOG.md');
    if (!existsSync(file)) {
        return {hasUnreleasedChanges: false, isNewMod: false};
    }
    const contents = readFileSync(file, 'utf8');
    const idx = contents.indexOf(UNRELEASED_HEADING);
    if (idx === -1) {
        return {hasUnreleasedChanges: false, isNewMod: false};
    }
    const after = contents.slice(idx + UNRELEASED_HEADING.length);
    const next = after.match(/\n###\s/);
    const section = next ? after.slice(0, next.index) : after;
    return {hasUnreleasedChanges: section.trim().length > 0, isNewMod: !next};
};

const findIcon = (folder: string, modId: string): string | null => {
    const file = path.join(REPO_ROOT, folder, 'common', 'src', 'main', 'resources', 'assets', modId, 'icon.png');
    return existsSync(file) ? file : null;
};

const readme = (folder: string): string => {
    const file = path.join(REPO_ROOT, folder, 'README.md');
    return existsSync(file) ? readFileSync(file, 'utf8').replace(/\r\n/g, '\n') : '';
};

const readmeLoaders = (source: string): string[] =>
    [...new Set([...source.matchAll(/modloader-(Fabric|NeoForge)/g)].map(m => m[1]!))];

const readmeTagline = (source: string): string | null =>
    source.match(/^_(.+)_$/m)?.[1]?.trim() ?? null;

/** The Minecraft version the repo currently targets, e.g. `26.2`. */
export const minecraftVersion = (): string =>
    readFileSync(path.join(REPO_ROOT, 'gradle.properties'), 'utf8').match(/^minecraft_version\s*=\s*(\S+)/m)?.[1] ?? '';

/** How many mods list Opus (the in-game guide library) as an optional CurseForge dependency. */
export const opusOptionalCount = (mods: ModInfo[]): number =>
    mods.filter(m => /^curseforge_optional\s*=.*opus/m.test(readFileSync(path.join(REPO_ROOT, m.folder, 'gradle.properties'), 'utf8'))).length;

export const collectMods = async (): Promise<ModInfo[]> =>
    Promise.all((projectList as string[]).map(async (folder): Promise<ModInfo> => {
        const props = await loadProperties(folder);
        const latestTag = (await git('tag', '-l', `${folder}/*`, '--sort=-creatordate')).split('\n')[0] || null;
        const lastCommit = (await git('log', '-1', '--format=%cI', '--', folder)) || null;

        const source = readme(folder);
        return {
            folder,
            id: props.mod_id,
            name: props.mod_name,
            description: props.mod_description ?? '',
            version: props.mod_version,
            isPrerelease: props.mod_version.includes('-'),
            ...changelogStatus(folder),
            latestTag,
            lastCommit,
            iconFile: findIcon(folder, props.mod_id),
            loaders: readmeLoaders(source),
            tagline: readmeTagline(source),
            curseforgeId: (props as unknown as Record<string, string | undefined>).curseforge_project_id ?? null,
        };
    }));
