import {existsSync, readFileSync} from 'node:fs';
import path from 'node:path';

import type {Doc, DocKey} from './docs.ts';
import {MARK_URL, modLinksList, shell, type ModLinks} from './layout.ts';
import type {Frame} from './sheet.ts';
import type {ModInfo} from './types.ts';
import {esc, relUrl, REPO_ROOT, REPO_URL} from './util.ts';

const initials = (name: string) =>
    name.split(/[\s-]+/).filter(Boolean).slice(0, 2).map(w => w[0]!.toUpperCase()).join('');

/** Site-relative location of a mod's copied icon. */
export const iconUrl = (mod: ModInfo) => `mods/${mod.folder}/icon.png`;

const iconHtml = (mod: ModInfo, from: string, size = 48) => mod.iconFile
    ? `<img class="icon" src="${esc(path.posix.relative(from, iconUrl(mod)))}" alt="" width="${size}" height="${size}">`
    : `<div class="icon ph" aria-hidden="true">${esc(initials(mod.name))}</div>`;

const badges = (mod: ModInfo) => [
    mod.isNewMod ? '<span class="tag new">new</span>' : '',
    mod.isPrerelease ? '<span class="tag beta">pre-release</span>' : '',
    mod.hasUnreleasedChanges ? '<span class="tag unrel" title="The changelog has unreleased changes">changes coming</span>' : '',
].join('');

/** A mod's frames on a contact sheet; a mod with no textures of its own shows its icon as one frame. */
const framesHtml = (mod: ModInfo, frames: Frame[], from: string) => {
    const list = frames.length
        ? frames.map(f => `<li class="frame"><img src="${esc(path.posix.relative(from, f.url))}" alt="${esc(f.name)}" width="16" height="16" loading="lazy"></li>`).join('')
        : `<li class="frame">${iconHtml(mod, from)}</li>`;
    return `<ul class="frames${frames.length <= 1 ? ' one' : ''}">${list}</ul>`;
};

export const curseforgeUrl = (mod: ModInfo) => mod.curseforgeId ? `https://www.curseforge.com/projects/${mod.curseforgeId}` : null;

/**
 * The four mods the home page opens with, chosen to show the range: the biggest, an upgrade to one vanilla
 * block, a handful of small tweaks, and the absurd one. The line under each is the site's own framing.
 */
const FEATURED: {folder: string; line: string}[] = [
    {folder: 'minekea', line: 'The big one: furniture, storage and building blocks. Some assembly required.'},
    {folder: 'effective-gear', line: 'Small tweaks and bonuses for armor, weapons and tools.'},
    {folder: 'hopper-xtreme', line: 'One vanilla block, taken seriously: faster hoppers that filter, and push items upward.'},
    {folder: 'camel-nostrils', line: 'This mod is weird. Really weird.'},
];

export type HomeStats = {guides: number; guidePages: number; ideas: number};

export const homePage = (mods: ModInfo[], links: Map<string, ModLinks>, frames: Map<string, Frame[]>, stats: HomeStats): string => {
    const byFolder = new Map(mods.map(m => [m.folder, m]));
    const featured = FEATURED.flatMap(f => byFolder.has(f.folder) ? [{...f, mod: byFolder.get(f.folder)!}] : []);
    const unreleased = mods.filter(m => m.hasUnreleasedChanges).length;

    const runs = featured.map(({mod, line}) => {
        const f = frames.get(mod.folder) ?? [];
        return `<li class="run${f.length <= 1 ? ' solo' : ''}"><a href="${esc(links.get(mod.folder)!.overview)}">
<div class="run-head"><h2>${esc(mod.name)}</h2><p>${esc(line)}</p></div>${framesHtml(mod, f, '')}</a></li>`;
    }).join('');

    const rows = mods.map(mod => {
        const l = links.get(mod.folder)!;
        const more = [['Guide', l.guide], ['Changelog', l.changelog], ['Ideas', l.ideas]]
            .filter(([, url]) => url).map(([label, url]) => `<a href="${esc(url!)}">${label}</a>`).join('');
        const search = `${mod.name} ${mod.folder} ${mod.description}`.toLowerCase();
        return `<li data-search="${esc(search)}"><a href="${esc(l.overview)}">${iconHtml(mod, '', 28)}<span class="nm">${esc(mod.name)}${badges(mod)}</span><span class="ver">${esc(mod.version)}</span><span class="desc">${esc(mod.description)}</span></a>
<div class="pv">${framesHtml(mod, frames.get(mod.folder) ?? [], '')}<h3>${esc(mod.name)}</h3><p>${esc(mod.description)}</p><p class="pv-links">${more}</p></div></li>`;
    }).join('');

    // The mark cut across a 6 x 4 board. It fills columns 1-4; the colour tiles sit on its black ground in
    // columns 0 and 5, so the picture stays whole when narrow screens drop those columns.
    const colour: Record<string, string> = {
        '0,0': `<div class="t c hd"><h2 id="ws-h">Inside the workshop</h2></div>`,
        '2,0': `<div class="t c"><p><b>${stats.guides}</b>in-game guides, ${stats.guidePages} pages, all readable here</p></div>`,
        '1,5': `<div class="t c"><p><b>${stats.ideas}</b>ideas on the roadmap, voted and ranked</p></div>`,
        '3,5': `<a class="t c act" href="ideas/"><span>Read the roadmap</span></a>`,
    };
    const tiles = Array.from({length: 24}, (_, i) => {
        const row = Math.floor(i / 6), col = i % 6;
        const tile = colour[`${row},${col}`];
        return tile ? tile.replace(/ class="t/, ` style="--d:${row + col}" class="t`)
            : `<div class="t${col === 0 || col === 5 ? ' edge' : ''}" style="--col:${col};--row:${row};--d:${row + col}" aria-hidden="true"><span></span></div>`;
    }).join('');

    return shell({
        title: 'chimericdream · Minecraft mods for Fabric and NeoForge',
        description: `${mods.length} Minecraft mods for Fabric and NeoForge: furniture and storage, building blocks, vanilla tweaks and a few strange ones.`,
        url: '',
        bare: true,
        body: `<section class="hero" aria-labelledby="top-h">
<h1 id="top-h">Mods from the dragon’s workshop</h1>
<div class="hero-copy"><p>${mods.length} Minecraft mods for Fabric and NeoForge: furniture and storage, building blocks, small fixes to vanilla, and a few that are just strange.</p>
<a class="btn" href="#catalogue">Find a mod</a></div>
<div class="hero-facts">
<p><b>Loaders.</b> Fabric and NeoForge, from one codebase built with Architectury.</p>
<p><b>Needs.</b> Most mods need Architectury API and ChimericLib. Opus is optional and adds the in-game guides.</p>
<p><b>Coming.</b> ${unreleased} mods have unreleased changes. <a href="changelog/">Read the changelog</a>.</p>
<p><b>Next.</b> ${stats.ideas} ideas are being weighed. <a href="ideas/">See the roadmap</a>.</p>
</div>
</section>
<ul class="sheet" aria-label="Four to start with">${runs}</ul>
<section class="cat reveal" id="catalogue" aria-labelledby="cat-h">
<div class="cat-head"><h2 id="cat-h">Every mod, A to Z</h2>
<div class="find" hidden><label for="find">Find a mod</label><input id="find" type="search" placeholder="Name, block or feature" autocomplete="off" spellcheck="false"><output for="find" aria-live="polite"></output></div></div>
<div class="cat-body"><ul class="index">${rows}</ul><p class="empty" hidden>Nothing matches. Try a block or a feature, like “hopper” or “books”.</p><div class="preview" aria-hidden="true"></div></div>
</section>
<section class="board-wrap" aria-labelledby="ws-h">
<div class="board" style="--mark:url(${MARK_URL})">${tiles}</div>
<div class="board-text reveal"><p>Every page here is built from the repository the mods live in: each README, changelog and in-game guide, and the ideas still being argued over. Guides are written once and read twice, in game through Opus and here with tables and crafting grids. Ideas go through a brainstorm, a shortlist and a vote before any code is written; the roadmap shows where each one stands.</p></div>
</section>`,
    });
};

export const overviewPage = (doc: Doc, body: string, links: ModLinks, frames: Frame[]): string => {
    const mod = doc.mod;
    const download = curseforgeUrl(mod);
    const actions = [
        download ? `<a class="btn" href="${esc(download)}">Download on CurseForge</a>` : '',
        !download && !links.guide ? `<a class="btn" href="${REPO_URL}/tree/main/${esc(mod.folder)}">Source on GitHub</a>` : '',
        links.guide ? `<a class="btn${download ? ' alt' : ''}" href="${esc(relUrl(doc.url, links.guide))}">Read the guide</a>` : '',
        links.changelog ? `<a class="btn alt" href="${esc(relUrl(doc.url, links.changelog))}">Changelog</a>` : '',
    ].join('');
    const loaders = mod.platforms.map(p => p === 'neoforge' ? 'NeoForge' : p === 'fabric' ? 'Fabric' : p).join(' and ');
    const meta = [
        `<b>${esc(mod.version)}</b>`,
        `${mod.minecraft ? `Minecraft ${esc(mod.minecraft)} mod` : 'Minecraft mod'}${loaders ? ` for ${esc(loaders)}` : ''}`,
    ].join(' · ');
    const sheetClass = frames.length > 1 ? (frames.length <= 4 ? 'few' : '') : frames.length || mod.iconFile ? 'solo' : 'solo initial';
    return shell({
        title: `${mod.name} · chimericdream`,
        description: mod.description,
        url: doc.url,
        side: modLinksList(links, doc.url),
        lead: `<header class="entry">
<div class="entry-text"><div class="entry-title">${frames.length ? iconHtml(mod, doc.url, 88) : ''}<h1>${esc(mod.name)}</h1></div>
<p class="lede">${esc(mod.description)}</p>
<p class="meta">${meta} ${badges(mod)}</p>
<div class="actions">${actions}</div></div>
<div class="entry-sheet ${sheetClass}">${framesHtml(mod, frames, doc.url)}</div>
</header>`,
        body: `<article>${body}</article><p class="edit-link"><a href="${REPO_URL}/blob/main/${esc(doc.key)}">View source on GitHub</a></p>`,
    });
};

/** README body without its leading `# Title`, which the overview header replaces. */
export const readmeBody = (source: string): string => source.replace(/^# .*\n+/, '');

type ChangelogSection = {heading: string; body: string};

const parseChangelog = (source: string): ChangelogSection[] =>
    source.replace(/\r\n/g, '\n').split(/^### /m).slice(1).map(chunk => {
        const nl = chunk.indexOf('\n');
        return {heading: chunk.slice(0, nl === -1 ? undefined : nl).trim(), body: nl === -1 ? '' : chunk.slice(nl + 1).trim()};
    });

/** Per-mod changelog pages are plain docs; this is the combined view of what is in progress and what shipped last. */
export const changelogPage = (mods: ModInfo[], render: (source: string, key: DocKey) => string): string => {
    const url = 'changelog/';
    const entries = mods.flatMap(mod => {
        const file = path.join(REPO_ROOT, mod.folder, 'CHANGELOG.md');
        if (!existsSync(file)) return [];
        const sections = parseChangelog(readFileSync(file, 'utf8'));
        const unreleased = sections.find(s => /^unreleased/i.test(s.heading));
        const latest = sections.find(s => !/^unreleased/i.test(s.heading));
        return [{mod, key: `${mod.folder}/CHANGELOG.md`, unreleased: unreleased?.body ? unreleased : undefined, latest}];
    });
    const link = (mod: ModInfo) => `${iconHtml(mod, url, 36)}<a href="${esc(relUrl(url, `mods/${mod.folder}/changelog`))}">${esc(mod.name)}</a>`;

    const upcoming = entries.filter(e => e.unreleased).map(e =>
        `<section class="feed-section"><h3 id="next-${esc(e.mod.folder)}">${link(e.mod)}</h3>${render(e.unreleased!.body, e.key)}</section>`).join('');
    const shipped = entries.filter(e => e.latest).map(e =>
        `<section class="feed-section"><h3 id="${esc(e.mod.folder)}">${link(e.mod)} <span class="ver">${esc(e.latest!.heading)}</span></h3>${render(e.latest!.body, e.key)}</section>`).join('');

    return shell({
        title: 'Changelog · chimericdream',
        url,
        body: `<article><h1>Changelog</h1>
<p>What is being worked on now, then the most recent release of every mod. Each mod's full history is on its own changelog page.</p>
<h2 id="in-development">In development</h2>${upcoming || '<p class="muted">Nothing unreleased right now.</p>'}
<h2 id="latest-releases">Latest releases</h2>${shipped}</article>`,
    });
};
