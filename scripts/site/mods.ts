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

/** Mod ids worth naming to a player; loader, game and Java requirements are implied. */
const DEP_NAMES: Record<string, string> = {
    'fabric-api': 'Fabric API',
    architectury: 'Architectury API',
    chimericlib: 'ChimericLib',
    yet_another_config_lib_v3: 'YACL',
    opus: 'Opus',
    modmenu: 'Mod Menu',
};

const dependencies = (folder: string): {requires: string[]; recommends: string[]} => {
    const file = path.join(REPO_ROOT, folder, 'fabric', 'src', 'main', 'resources', 'fabric.mod.json');
    if (!existsSync(file)) return {requires: [], recommends: []};
    const json = JSON.parse(readFileSync(file, 'utf8')) as Record<string, Record<string, string> | undefined>;
    const names = (deps: Record<string, string> | undefined) => Object.keys(deps ?? {}).flatMap(id => DEP_NAMES[id] ? [DEP_NAMES[id]] : []);
    return {requires: names(json.depends), recommends: names({...json.recommends, ...json.suggests})};
};

export const collectMods = async (): Promise<ModInfo[]> =>
    Promise.all((projectList as string[]).map(async (folder): Promise<ModInfo> => {
        const props = await loadProperties(folder);
        const latestTag = (await git('tag', '-l', `${folder}/*`, '--sort=-creatordate')).split('\n')[0] || null;
        const lastCommit = (await git('log', '-1', '--format=%cI', '--', folder)) || null;

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
            mcCompat: props.minecraft_compat ?? '',
            ...dependencies(folder),
        };
    }));
