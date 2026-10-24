import path from 'node:path';

import type {ModInfo} from './types.ts';
import {esc} from './util.ts';

const initials = (name: string) =>
    name.split(/[\s-]+/).filter(Boolean).slice(0, 2).map(w => w[0]!.toUpperCase()).join('');

/** Stable pick of one of six brand-palette tile colors (see `.ph.c0`-`.ph.c5` in styles.ts). */
const swatch = (s: string) => [...s].reduce((h, c) => (h * 31 + c.charCodeAt(0)) % 6, 7);

/** Site-relative location of a mod's copied icon. */
export const iconUrl = (mod: ModInfo) => `mods/${mod.folder}/icon.png`;

/** The mod's pixel-art icon, or a generated initials tile in the brand palette when it has none. */
export const iconHtml = (mod: ModInfo, from: string, size: 'small' | 'big' = 'small') => {
    const big = size === 'big' ? ' big' : '';
    return mod.iconFile
        ? `<img class="icon${big}" src="${esc(path.posix.relative(from, iconUrl(mod)))}" alt="" width="48" height="48">`
        : `<div class="icon ph c${swatch(mod.id)}${big}" aria-hidden="true">${esc(initials(mod.name))}</div>`;
};
