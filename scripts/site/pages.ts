import {existsSync, readFileSync} from 'node:fs';
import path from 'node:path';

import type {Doc, DocKey} from './docs.ts';
import {LOGO_URL, modLinksList, shell, type ModLinks} from './layout.ts';
import type {ModInfo} from './types.ts';
import {esc, relUrl, REPO_ROOT, REPO_URL} from './util.ts';

const initials = (name: string) =>
    name.split(/[\s-]+/).filter(Boolean).slice(0, 2).map(w => w[0]!.toUpperCase()).join('');

const hue = (s: string) => [...s].reduce((h, c) => (h * 31 + c.charCodeAt(0)) % 360, 7);

/** Site-relative location of a mod's copied icon. */
export const iconUrl = (mod: ModInfo) => `mods/${mod.folder}/icon.png`;

const iconHtml = (mod: ModInfo, from: string, size = 56) => mod.iconFile
    ? `<img class="icon" src="${esc(path.posix.relative(from, iconUrl(mod)))}" alt="" width="${size}" height="${size}" loading="lazy">`
    : `<div class="icon ph" style="--h:${hue(mod.id)}" aria-hidden="true">${esc(initials(mod.name))}</div>`;

const badges = (mod: ModInfo) => [
    mod.isNewMod ? '<span class="badge new">new</span>' : '',
    mod.isPrerelease ? '<span class="badge beta">pre-release</span>' : '',
    mod.hasUnreleasedChanges ? '<span class="badge unrel" title="The changelog has unreleased changes">unreleased changes</span>' : '',
].join('');

/**
 * Four mods chosen to show the range of what gets built: big and small, practical and silly.
 * The tagline is the author's own voice; everything else on a tile comes from the mod itself.
 */
const FEATURED: {folder: string; tagline: string; tone: 'violet' | 'crimson' | 'snow' | 'night'; wide?: boolean; flip?: boolean}[] = [
    {folder: 'minekea', tagline: 'Furniture, storage and building blocks for every room you can think of. Some assembly required.', tone: 'violet', wide: true},
    {folder: 'hopper-xtreme', tagline: 'Upgrade your hoppers.', tone: 'night'},
    {folder: 'effective-gear', tagline: 'Small quality-of-life tweaks for armor, weapons and tools.', tone: 'snow'},
    {folder: 'camel-nostrils', tagline: 'This mod is weird. Really weird.', tone: 'crimson', flip: true},
];

export type HomeStats = {guides: number; ideas: number; minecraft: string};

const findKey = (mod: ModInfo) => esc(`${mod.name} ${mod.description}`.toLowerCase());

export const homePage = (mods: ModInfo[], links: Map<string, ModLinks>, stats: HomeStats): string => {
    const featured = FEATURED.flatMap(f => {
        const mod = mods.find(m => m.folder === f.folder);
        return mod ? [{...f, mod}] : [];
    });
    const featuredFolders = new Set(featured.map(f => f.folder));

    const tiles = featured.map(({mod, tagline, tone, wide, flip}, i) => {
        const l = links.get(mod.folder)!;
        const art = mod.iconFile
            ? `<img src="${esc(iconUrl(mod))}" alt="" width="200" height="200">`
            : `<span class="icon ph" style="--h:${hue(mod.id)}" aria-hidden="true">${esc(initials(mod.name))}</span>`;
        return `<a class="feat ${tone}${wide ? ' wide' : ''}${flip ? ' flip' : ''}" href="${esc(l.overview)}" data-find="${findKey(mod)}" data-reveal style="--i:${i}">
<span class="feat-art">${art}</span>
<span class="feat-body"><h3>${esc(mod.name)}</h3><p>${esc(tagline)}</p><span class="ver">${esc(mod.version)}${badges(mod)}</span></span></a>`;
    }).join('\n');

    const rows = mods.filter(m => !featuredFolders.has(m.folder)).map((mod, i) => {
        const l = links.get(mod.folder)!;
        const status = mod.isNewMod ? 'new' : mod.isPrerelease ? 'pre-release' : '';
        return `<li data-find="${findKey(mod)}" data-reveal style="--i:${i % 3}"><a href="${esc(l.overview)}">${iconHtml(mod, '')}<span class="nm">${esc(mod.name)}</span>
<span class="vr">${esc(mod.version)}${status ? ` · ${status}` : ''}</span><span class="ds">${esc(mod.description)}</span></a></li>`;
    }).join('\n');

    return shell({
        title: 'chimericdream’s Minecraft mods',
        description: `${mods.length} Minecraft mods for Fabric and NeoForge, each with its guide, changelog and roadmap.`,
        url: '',
        bleed: true,
        body: `<div class="hero">
<div><h1 data-reveal>${mods.length} mods for Minecraft</h1>
<p class="lead" data-reveal style="--i:1">Furniture, storage, building blocks and a few things that should not exist, for Fabric and NeoForge.</p>
<p class="actions" data-reveal style="--i:2"><a class="btn" href="#catalogue">Browse all ${mods.length} mods</a><a class="btn quiet" href="ideas/">See what is coming</a></p></div>
<img class="crest" src="${LOGO_URL}" alt="A crimson dragon draped across twin snowy mountains under a crescent moon, the chimericdream mark" width="360" height="360">
</div>
<div class="row-scroll" role="group" aria-label="Four mods that show the range">
${tiles}
</div>
<section class="section" id="catalogue" aria-labelledby="cat-h">
<div class="section-head" data-reveal><h2 id="cat-h">The rest of the catalogue</h2>
<label class="find"><span class="sr-only">Find a mod</span><input id="find" type="search" placeholder="Find a mod" autocomplete="off"></label></div>
<ul class="sheet">
${rows}
</ul>
<p class="none" id="find-none">No mod matches that.</p>
</section>
<section class="section" aria-labelledby="about-h">
<div class="about">
<ul data-reveal><li><b>${mods.length}</b>mods</li><li><b>${stats.guides}</b>in-game guides</li><li><b>${stats.ideas}</b>ideas on the roadmap</li><li><b>${esc(stats.minecraft)}</b>current Minecraft version</li></ul>
<div data-reveal style="--i:1"><h2 class="sr-only" id="about-h">About this site</h2>
<p>Everything here is published straight from the repository as it is written: each mod’s README, changelog, in-game guide and feature ideas.</p>
<p>The roadmap lists the ideas under consideration across every mod. The changelog shows what has shipped and what is on its way.</p>
<p class="actions"><a class="btn" href="ideas/">Roadmap</a><a class="btn quiet" href="changelog/">Changelog</a><a class="btn quiet" href="${REPO_URL}">Source on GitHub</a></p></div>
</div>
</section>`,
    });
};

export const overviewPage = (doc: Doc, body: string, links: ModLinks): string => {
    const mod = doc.mod;
    const primary = links.guide
        ? `<a class="btn" href="${esc(relUrl(doc.url, links.guide))}">Read the in-game guide</a>`
        : links.changelog ? `<a class="btn" href="${esc(relUrl(doc.url, links.changelog))}">Read the changelog</a>` : '';
    const more = [
        links.guide && links.changelog ? `<a class="btn quiet" href="${esc(relUrl(doc.url, links.changelog))}">Changelog</a>` : '',
        links.ideas ? `<a class="btn quiet" href="${esc(relUrl(doc.url, links.ideas))}">Ideas</a>` : '',
        `<a class="btn quiet" href="${REPO_URL}/tree/main/${esc(mod.folder)}">Source</a>`,
    ].join('');
    const status = mod.isNewMod ? 'New' : mod.isPrerelease ? 'Pre-release' : 'Released';
    const hero = `<div class="modhero"><div class="art" data-reveal>${iconHtml(mod, doc.url, 160)}</div>
<div><h1 data-reveal>${esc(mod.name)}</h1><p class="lead" data-reveal style="--i:1">${esc(mod.description)}</p>
<p class="actions" data-reveal style="--i:2">${primary}${more}</p></div>
<dl class="facts" data-reveal style="--i:3"><div><dt>Version</dt><dd>${esc(mod.version)}</dd></div><div><dt>Status</dt><dd>${status}${mod.hasUnreleasedChanges ? ' · unreleased changes' : ''}</dd></div></dl></div>`;
    return shell({
        title: `${mod.name} · chimericdream’s Minecraft mods`,
        description: mod.description,
        url: doc.url,
        crumbs: [esc(mod.name)],
        side: modLinksList(links, doc.url),
        hero,
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
    const link = (mod: ModInfo) => `<a href="${esc(relUrl(url, `mods/${mod.folder}/changelog`))}">${esc(mod.name)}</a>`;

    const upcoming = entries.filter(e => e.unreleased).map(e =>
        `<section class="feed-section"><h2>${link(e.mod)}</h2>${render(e.unreleased!.body, e.key)}</section>`).join('');
    const shipped = entries.filter(e => e.latest).map(e =>
        `<section class="feed-section"><h2>${link(e.mod)} <small class="ver">${esc(e.latest!.heading)}</small></h2>${render(e.latest!.body, e.key)}</section>`).join('');

    return shell({
        title: 'Changelog · chimericdream’s Minecraft mods',
        url,
        body: `<article class="hl-page" style="max-width:none"><h1>Changelog</h1>
<h2>In development</h2>${upcoming ? `<div class="feed">${upcoming}</div>` : '<p class="muted">Nothing unreleased right now.</p>'}
<h2>Latest releases</h2><div class="feed">${shipped}</div></article>`,
    });
};
