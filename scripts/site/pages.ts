import {existsSync, readFileSync} from 'node:fs';
import path from 'node:path';

import type {Doc, DocKey} from './docs.ts';
import {modLinksList, shell, type ModLinks} from './layout.ts';
import type {ModInfo} from './types.ts';
import {esc, relUrl, REPO_ROOT, REPO_URL} from './util.ts';

const initials = (name: string) =>
    name.split(/[\s-]+/).filter(Boolean).slice(0, 2).map(w => w[0]!.toUpperCase()).join('');

/** Site-relative location of a mod's copied icon. */
export const iconUrl = (mod: ModInfo) => `mods/${mod.folder}/icon.png`;

const iconHtml = (mod: ModInfo, from: string) => mod.iconFile
    ? `<img class="icon" src="${esc(path.posix.relative(from, iconUrl(mod)))}" alt="" width="48" height="48">`
    : `<div class="icon ph" aria-hidden="true">${esc(initials(mod.name))}</div>`;

const badges = (mod: ModInfo) => [
    mod.isNewMod ? '<span class="badge new">new</span>' : '',
    mod.isPrerelease ? '<span class="badge beta">pre-release</span>' : '',
    mod.hasUnreleasedChanges ? '<span class="badge unrel" title="The changelog has unreleased changes">unreleased changes</span>' : '',
].join('');

export const homePage = (mods: ModInfo[], links: Map<string, ModLinks>): string => {
    const cards = mods.map(mod => {
        const l = links.get(mod.folder)!;
        const nav = [
            ['Read guide', l.guide], ['Overview', l.overview], ['Changes', l.changelog], ['Ideas', l.ideas],
        ].filter(([, url]) => url).map(([label, url]) => `<a href="${esc(url!)}">${label}</a>`).join('');
        return `<li class="card" data-search="${esc(`${mod.name} ${mod.folder} ${mod.description}`.toLocaleLowerCase())}" data-guide="${Boolean(l.guide)}" data-changes="${mod.hasUnreleasedChanges}"><header>${iconHtml(mod, '')}<div><h2><a href="${esc(l.overview)}">${esc(mod.name)}</a></h2>
<div class="ver">v${esc(mod.version)}</div></div></header>
<p>${esc(mod.description)}</p><div class="card-status">${badges(mod)}</div><nav aria-label="${esc(mod.name)} documentation">${nav}</nav></li>`;
    }).join('');
    return shell({
        title: 'Chimeric Mods',
        url: '',
        body: `<section class="catalog-intro"><div><div class="eyebrow">A Minecraft field guide</div><h1>Explore Chimeric Mods</h1>
<p>Find building blocks, tools, and gameplay tweaks for your next Minecraft world. Read the guides for recipes and examples.</p></div>
<aside class="field-note"><strong>Fabric and NeoForge</strong><p>A collection by chimericdream.</p><a href="changelog/">See what's changed &rarr;</a></aside></section>
<div class="catalog-tools" hidden><div class="catalog-search"><label for="catalog-search">Find a mod</label><input id="catalog-search" type="search" placeholder="Try a name, storage, or pumpkins" aria-controls="catalog"></div>
<div class="catalog-filters" role="group" aria-label="Filter mods"><button data-catalog-filter="all" aria-pressed="true">All mods</button><button data-catalog-filter="guide" aria-pressed="false">With a guide</button><button data-catalog-filter="changes" aria-pressed="false">In development</button></div></div>
<div class="catalog-meta"><p id="catalog-count" role="status">${mods.length} mods</p></div>
<ul class="cards" id="catalog">${cards}</ul><div class="empty-state" id="catalog-empty" hidden><h2>No mods found</h2><p>Try a different name or a broader description.</p><button id="catalog-reset">Clear search and filters</button></div>`,
    });
};

export const overviewPage = (doc: Doc, body: string, links: ModLinks): string => {
    const mod = doc.mod;
    const actions = [
        links.guide ? `<a href="${esc(relUrl(doc.url, links.guide))}">In-game guide</a>` : '',
        links.changelog ? `<a href="${esc(relUrl(doc.url, links.changelog))}">Changelog</a>` : '',
        links.ideas ? `<a href="${esc(relUrl(doc.url, links.ideas))}">Ideas</a>` : '',
        `<a href="${REPO_URL}/tree/main/${esc(mod.folder)}">Source</a>`,
    ].join('');
    return shell({
        title: `${mod.name} · Chimeric Mods`,
        url: doc.url,
        crumbs: [esc(mod.name)],
        side: modLinksList(links, doc.url),
        body: `<div class="modhead">${iconHtml(mod, doc.url)}<div><div class="eyebrow">Mod overview</div><h1>${esc(mod.name)}</h1>
<p>${esc(mod.description)}</p><div class="mod-version"><span>Project version ${esc(mod.version)}</span>${badges(mod)}</div></div></div><div class="actions">${actions}</div>
<article>${body}</article><p class="edit-link"><a href="${REPO_URL}/blob/main/${esc(doc.key)}">View source on GitHub</a></p>`,
    });
};

/** The overview already displays the title and project description. */
export const readmeBody = (source: string, description: string): string => {
    const body = source.replace(/^# .*\n+/, '');
    const firstSection = body.search(/^#{1,6} /m);
    const endOfIntro = firstSection === -1 ? body.length : firstSection;
    const intro = body.slice(0, endOfIntro).split('\n\n').filter(block => block.trim() !== `_${description}_`);
    return intro.join('\n\n') + body.slice(endOfIntro);
};

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
        title: 'Changelog · Chimeric Mods',
        url,
        body: `<div class="eyebrow">The collection / Release notes</div><h1>What's changed</h1>
<p class="page-intro">Upcoming changes and the latest release notes from each mod, in alphabetical order.</p>
<nav class="feed-jump" aria-label="Changelog sections"><a href="#in-development">In development</a><a href="#latest-releases">Latest releases</a></nav>
<article><h2 id="in-development">In development</h2><p class="muted">Changes awaiting release.</p>${upcoming || '<p class="muted">Nothing unreleased right now.</p>'}
<h2 id="latest-releases">Latest releases</h2><p class="muted">The latest version entry in each mod's changelog. Open a mod for its full history.</p>${shipped}</article>`,
    });
};
