import {readFileSync} from 'node:fs';
import path from 'node:path';

import {REPO_ROOT} from './util.ts';

/** Pictures and footage the site uses, downloaded into `scripts/site/assets/` with their credits. */
export const ASSET_DIR = path.join(REPO_ROOT, 'scripts', 'site', 'assets');

export const PICTURE = {
    /** Alchemist's laboratory, Metropolitan Museum (CC0). Hero, large print. */
    lab: 'images/the-alchemist-s-laboratory-from-heinrich-khunrath.jpg',
    /** 'Distillatio', alchemist's laboratory, Wellcome Collection (CC BY 4.0). Hero crossfade, how it works. */
    distillatio: 'images/distillatio-scene-in-an-alchemist-laboratory.jpg',
    /** Chest, Metropolitan Museum (CC0). Hero, small print. */
    chest: 'images/chest-met-sf57-144-3s4.jpg',
    /** Cassone chest, Metropolitan Museum (CC0). Hopper X-Treme frame. */
    cassone: 'images/cassone-chest-pair-with-1975-1-1947.jpg',
    /** Mechanical band-saw engraving, Wellcome Collection (CC BY 4.0). Minekea frame. */
    bandsaw: 'images/woodwork-a-mechanical-band-saw-engraving-after.jpg',
    /** Blacksmith at the anvil, Wellcome Collection (CC BY 4.0). How it works. */
    anvil: 'images/blacksmith-at-the-anvil.jpg',
    /** Riplaeus' hieroglyphic scroll, Wellcome Collection (CC BY 4.0). Camel Nostrils frame. */
    scroll: 'images/rotulum-hieroglyphicum-g-riplaei-equitis-aurati.jpg',
    /** Ridge in mist, Rob Bye via picsum.photos (Unsplash licence). Footer. */
    mountain: 'images/mountain-ridge-in-mist.jpg',
} as const;

export const VIDEO = {
    /** Metal fabricator at work, Wikimedia Commons (CC0). Effective Gear frame, muted loop. */
    forge: 'video/gof-a-gweithiwr-metrel-ray-burrell-metal.webm',
    poster: 'video/gof-a-gweithiwr-metrel-ray-burrell-metal-poster.jpg',
} as const;

/** Site-relative location of a copied asset, from a page at `from` (a directory-style URL). */
export const assetUrl = (from: string, file: string): string => `${'../'.repeat(from.split('/').filter(Boolean).length)}assets/${file}`;

type Credit = {file: string; credit: string};

const readCredits = (dir: string): Credit[] => {
    const file = path.join(ASSET_DIR, dir, 'credits.json');
    return JSON.parse(readFileSync(file, 'utf8')) as Credit[];
};

/** Credit line for every picture and clip the site uses, in the order they appear in the footer. */
export const creditLines = (): string[] => {
    const fromFiles = [...readCredits('images'), ...readCredits('video')];
    const lines = fromFiles.map(c => c.credit);
    lines.push('"Grassy rocky mountain ridge emerging from thick grey mist" by Rob Bye, via picsum.photos, Unsplash licence');
    return lines;
};
