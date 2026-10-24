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
    const featured = [
        {folder: 'minekea', text: 'Furniture, secret doors, shelves, and storage. A whole workshop of possibilities, with some assembly required.'},
        {folder: 'hopper-xtreme', text: 'Faster transfers, filters, and hoppers that push upward. Give your item logistics a practical upgrade.'},
        {folder: 'effective-gear', text: 'Small tweaks and new bonuses for the armor, weapons, and tools you already carry.'},
        {folder: 'camel-nostrils', text: 'This mod is weird. Really weird. Camel snouts, zombie fish, and an anvil that falls upward.'},
    ];
    const featuredFolders = new Set(featured.map(f => f.folder));
    const features = featured.map(feature => {
        const mod = mods.find(m => m.folder === feature.folder);
        if (!mod) return '';
        const l = links.get(mod.folder)!;
        const art = mod.folder === 'minekea'
            ? '<img class="icon" src="brand/minekea-catalogue.png" alt="The Minekea Catalogue guide book" width="192" height="192">'
            : iconHtml(mod, '');
        return `<div class="feature-entry feature-${mod.folder}"><div class="feature-copy"><h3><a href="${esc(l.overview)}">${esc(mod.name)}</a></h3>
<span class="ver">${esc(mod.version)}${mod.isPrerelease ? ' · Pre-release' : ''}</span><p>${feature.text}</p>
<nav aria-label="${esc(mod.name)}"><a href="${esc(l.overview)}">Explore the mod</a>${l.guide ? `<a href="${esc(l.guide)}">${mod.folder === 'minekea' ? 'Read the Catalogue' : 'Read the guide'}</a>` : ''}</nav></div>
<a class="feature-art" href="${esc(mod.folder === 'minekea' ? l.guide ?? l.overview : l.overview)}" aria-label="${mod.folder === 'minekea' ? 'Read the Minekea Catalogue' : `Explore ${esc(mod.name)}`}">${art}</a></div>`;
    }).join('');
    const rows = mods.map(mod => {
        const l = links.get(mod.folder)!;
        const nav = [
            ['Overview', l.overview], ['Guide', l.guide], ['Changelog', l.changelog], ['Ideas', l.ideas],
        ].filter(([, url]) => url).map(([label, url]) => `<a href="${esc(url!)}">${label}</a>`).join('');
        const isFeatured = featuredFolders.has(mod.folder);
        const readme = path.join(REPO_ROOT, mod.folder, 'README.md');
        const features = existsSync(readme) ? readFileSync(readme, 'utf8').replace(/\s+/g, ' ') : '';
        const search = `${mod.name} ${mod.description} ${features} ${mod.folder} ${mod.loaders.join(' ')} ${mod.minecraft} ${mod.isPrerelease ? 'pre-release beta' : 'stable'} ${mod.isNewMod ? 'new' : ''} ${mod.hasUnreleasedChanges ? 'unreleased' : ''}`.toLocaleLowerCase();
        return `<li class="catalogue-row" data-name="${esc(mod.name.toLocaleLowerCase())}" data-search="${esc(search)}" data-release="${mod.isPrerelease ? 'beta' : 'stable'}" data-featured="${isFeatured}"${isFeatured ? ' hidden' : ''}>
${iconHtml(mod, '')}<div><h3><a href="${esc(l.overview)}">${esc(mod.name)}</a></h3><p>${esc(mod.description)}</p>
<div class="row-meta"><span>${esc(mod.version)}</span><span>${esc(mod.loaders.join(' / '))}</span>${badges(mod)}</div><nav aria-label="${esc(mod.name)} documents">${nav}</nav></div></li>`;
    }).join('');
    return shell({
        title: 'Minecraft mods by chimericdream',
        url: '',
        home: true,
        body: `<section class="home-intro" aria-labelledby="home-title"><h1 id="home-title">Minecraft, with character</h1>
<p>${mods.length} mods for Fabric and NeoForge, from useful little fixes to furniture, field guides, and wonderfully questionable ideas.</p>
<a class="primary" href="#catalogue">Find a mod</a></section>
<nav class="folder-tabs" aria-label="Catalogue tabs"><a href="#featured">The collection</a><a href="ideas/">Roadmap</a><a href="changelog/">Changelog</a></nav>
<section class="feature-sheet" id="featured" aria-labelledby="featured-title"><header class="feature-intro"><h2 id="featured-title">Four ways to play</h2><p>A few things from the workshop. Big, small, practical, and a little peculiar.</p></header>
<div class="feature-band">${features}</div><p class="feature-note">Every mod has its own overview, changelog, and ideas. Many have a guide you can read here or in the game.</p></section>
<section class="catalogue" id="catalogue" aria-labelledby="catalogue-title"><header class="catalogue-heading"><h2 id="catalogue-title">The collection</h2><p id="mod-count" role="status" aria-live="polite">${mods.length - featured.length} more mods to discover</p></header>
<form class="catalogue-controls" id="catalogue-search" role="search"><label>Find your next mod<input id="mod-search" type="search" placeholder="Search names, features, or loaders" autocomplete="off" aria-controls="mod-list"></label>
<label>Release<select id="release-filter" aria-controls="mod-list"><option value="all">All releases</option><option value="stable">Stable</option><option value="beta">Pre-release</option></select></label></form>
<noscript><p>Browse the full collection below. Search is available with JavaScript enabled.</p></noscript>
<ul class="catalogue-list" id="mod-list">${rows}</ul><p class="no-results" id="no-results" hidden>No mods match that search. Try a name, feature, or loader.</p></section>`,
    });
};

export const overviewPage = (doc: Doc, body: string, links: ModLinks): string => {
    const mod = doc.mod;
    const actions = [
        mod.downloadUrl ? `<a class="primary" href="${esc(mod.downloadUrl)}">Download on CurseForge</a>` : '',
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
        body: `<div class="modhead">${iconHtml(mod, doc.url)}<div><h1>${esc(mod.name)}</h1>
<p>${esc(mod.description)}</p></div></div><div class="mod-meta"><span>Version ${esc(mod.version)}</span><span>Minecraft ${esc(mod.minecraft)}</span><span>${esc(mod.loaders.join(' / '))}</span>${badges(mod)}</div><div class="actions">${actions}</div>
<article>${body}</article><p class="edit-link"><a href="${REPO_URL}/blob/main/${esc(doc.key)}">View source on GitHub</a></p>`,
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
        title: 'Changelog · Chimeric Mods',
        url,
        body: `<article><h1>Changelog</h1>
<h2>In development</h2>${upcoming || '<p class="muted">Nothing unreleased right now.</p>'}
<h2>Latest releases</h2>${shipped}</article>`,
    });
};
