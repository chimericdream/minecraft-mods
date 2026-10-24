import {copyFileSync, existsSync, mkdirSync, readdirSync, readFileSync, statSync} from 'node:fs';
import path from 'node:path';

import {displayName} from './recipes/names.ts';
import type {ModInfo} from './types.ts';
import {REPO_ROOT} from './util.ts';

/** A texture copied into the site, with its site-root-relative URL. */
export type Texture = {url: string; name: string};

// Parts of a block model rather than something a player would recognise on its own.
const PARTIAL = /(_top|_bottom|_inside|_rim|_overlay|_particle|_placeholder|_unset|_end|_inner|_outer|_handle)(_|\.|$)/;

const walkPng = (dir: string): string[] => !existsSync(dir) ? [] : readdirSync(dir).sort().flatMap(name => {
    const full = path.join(dir, name);
    return statSync(full).isDirectory() ? walkPng(full) : name.endsWith('.png') ? [full] : [];
});

/** Square, small (16-32px) PNGs only: item and block art, not GUI sheets or animation strips. */
const isIcon = (file: string): boolean => {
    const head = readFileSync(file).subarray(16, 24);
    if (head.length < 8) return false;
    const w = head.readUInt32BE(0);
    const h = head.readUInt32BE(4);
    return w === h && w >= 16 && w <= 32;
};

const sample = <T>(list: T[], n: number): T[] =>
    list.length <= n ? list : Array.from({length: n}, (_, i) => list[Math.floor((i * list.length) / n)]!);

/** Up to `limit` of a mod's item textures (then block textures), copied to `mods/<folder>/_tex/`. */
export const collectTextures = (mod: ModInfo, outDir: string, limit = 24): Texture[] => {
    const root = path.join(REPO_ROOT, mod.folder, 'common', 'src', 'main', 'resources', 'assets', mod.id, 'textures');
    const pick = (kind: string) => walkPng(path.join(root, kind)).filter(f => !PARTIAL.test(path.basename(f)) && isIcon(f));
    const items = sample(pick('item'), limit);
    const files = [...items, ...sample(pick('block'), limit - items.length)];
    return files.map(file => {
        const rel = path.relative(root, file).split(path.sep).join('/');
        const url = `mods/${mod.folder}/_tex/${rel}`;
        mkdirSync(path.dirname(path.join(outDir, url)), {recursive: true});
        copyFileSync(file, path.join(outDir, url));
        return {url, name: displayName(rel.replace(/\.png$/, ''))};
    });
};

/** Copies one picture from a mod's assets folder (e.g. a guide screenshot) and returns its site URL. */
export const copyModAsset = (mod: ModInfo, outDir: string, rel: string): string | null => {
    const file = path.join(REPO_ROOT, mod.folder, 'common', 'src', 'main', 'resources', 'assets', mod.id, rel);
    if (!existsSync(file)) return null;
    const url = `mods/${mod.folder}/_tex/${rel}`;
    mkdirSync(path.dirname(path.join(outDir, url)), {recursive: true});
    copyFileSync(file, path.join(outDir, url));
    return url;
};
