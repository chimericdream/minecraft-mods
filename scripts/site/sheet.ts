import {closeSync, copyFileSync, existsSync, mkdirSync, openSync, readdirSync, readSync, statSync} from 'node:fs';
import path from 'node:path';

import type {ModInfo} from './types.ts';
import {REPO_ROOT} from './util.ts';

/** One texture shown on a mod's contact sheet: where it was copied to, and a readable name for its alt text. */
export type Frame = {url: string; name: string};

// Masks, overlays and placeholder states are half a picture; they read as noise on the sheet.
const SKIP = /overlay|placeholder|unset|mask|empty|_inside|_bottom|_rim|_particle/i;

const pngSize = (file: string): {w: number; h: number} | null => {
    const fd = openSync(file, 'r');
    try {
        const head = Buffer.alloc(24);
        readSync(fd, head, 0, 24, 0);
        return head.toString('ascii', 12, 16) === 'IHDR' ? {w: head.readUInt32BE(16), h: head.readUInt32BE(20)} : null;
    } finally {
        closeSync(fd);
    }
};

/** Square textures under one folder, grouped by the directory they sit in. */
const texturesByDir = (root: string): Map<string, string[]> => {
    const out = new Map<string, string[]>();
    const walk = (dir: string) => {
        for (const name of readdirSync(dir).sort()) {
            const full = path.join(dir, name);
            if (statSync(full).isDirectory()) {
                walk(full);
            } else if (name.endsWith('.png') && !SKIP.test(name) && !existsSync(`${full}.mcmeta`)) {
                const size = pngSize(full);
                if (size && size.w === size.h && size.w <= 64) {
                    out.set(dir, [...(out.get(dir) ?? []), full]);
                }
            }
        }
    };
    if (existsSync(root)) walk(root);
    return out;
};

/** Take one texture from each folder in turn, so a sheet shows the range of a mod rather than sixteen dyes. */
const roundRobin = (groups: string[][], max: number): string[] => {
    const picked: string[] = [];
    for (let i = 0; picked.length < max && groups.some(g => i < g.length); i++) {
        for (const g of groups) {
            if (i < g.length && picked.length < max) picked.push(g[i]!);
        }
    }
    return picked;
};

const label = (file: string) => path.basename(file, '.png').replace(/_/g, ' ');

/**
 * Copy up to `max` of a mod's own item textures (block textures when it has no items) into the site and
 * return them as frames. Mods that only tweak vanilla have none, and get an empty list.
 */
export const collectFrames = (mod: ModInfo, outDir: string, max = 8): Frame[] => {
    const assets = path.join(REPO_ROOT, mod.folder, 'common', 'src', 'main', 'resources', 'assets', mod.id, 'textures');
    const items = [...texturesByDir(path.join(assets, 'item')).values()];
    const blocks = [...texturesByDir(path.join(assets, 'block')).values()];
    const files = roundRobin(items.length ? [...items, ...blocks] : blocks, max);
    return files.map((file, i) => {
        const url = `mods/${mod.folder}/_sheet/${i}-${path.basename(file)}`;
        mkdirSync(path.dirname(path.join(outDir, url)), {recursive: true});
        copyFileSync(file, path.join(outDir, url));
        return {url, name: label(file)};
    });
};
