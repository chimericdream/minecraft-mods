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

const FEATURED = ['minekea', 'hopper-xtreme', 'effective-gear', 'camel-nostrils'];
const featureCopy: Record<string, string> = {
    minekea: 'Furniture, secret doors and storage for a world worth coming home to. Some assembly required.',
    'hopper-xtreme': 'Faster hoppers. Filtered hoppers. Even hoppers that send things up.',
    'effective-gear': 'Give your armor trims a purpose. Matching sets come with useful bonuses.',
    'camel-nostrils': 'This mod is weird. Really weird. Start with a camel’s nose and see where it goes.',
};
const loaderNames = (mod: ModInfo) => mod.loaders.map(l => l === 'neoforge' ? 'NeoForge' : l === 'fabric' ? 'Fabric' : l).join(' + ');

export const homePage = (mods: ModInfo[], links: Map<string, ModLinks>): string => {
    const featured = FEATURED.map(folder => mods.find(mod => mod.folder === folder)).filter((mod): mod is ModInfo => !!mod);
    const wares = featured.map(mod => {
        const l = links.get(mod.folder)!;
        return `<li class="ware"><h2><a class="ware-title" href="${esc(l.overview)}">${esc(mod.name)}</a></h2>
<p>${esc(featureCopy[mod.folder]!)}</p>${mod.folder === 'minekea' ? `<div class="ware-art" aria-label="Minekea items"><img class="art-slide" src="art/catalogue.png" width="176" height="176" alt="The Minekea Catalogue"><img class="art-slide" src="art/wrench.png" width="176" height="176" alt="Minekea wrench" aria-hidden="true"><img class="art-slide" src="art/lantern.png" width="176" height="176" alt="Ancient lantern" aria-hidden="true"><button type="button" class="art-toggle" hidden>Pause artwork</button></div>` : iconHtml(mod, '')}<span class="ver">Version ${esc(mod.version)}</span>
<nav class="ware-links" aria-label="${esc(mod.name)} resources"><a href="${esc(l.overview)}">Explore mod</a>${l.guide ? `<a href="${esc(l.guide)}">Read guide</a>` : ''}</nav></li>`;
    }).join('');
    const rows = mods.map(mod => {
        const l = links.get(mod.folder)!;
        const isFeatured = FEATURED.includes(mod.folder);
        const nav = [['Guide', l.guide], ['Changelog', l.changelog], ['Ideas', l.ideas]]
            .filter(([, url]) => url).map(([label, url]) => `<a href="${esc(url!)}">${label}</a>`).join('');
        return `<li class="mod-row" data-featured="${isFeatured}" data-search="${esc(`${mod.name} ${mod.description} ${mod.folder} ${loaderNames(mod)}`.toLowerCase())}"${isFeatured ? ' hidden' : ''}>
${iconHtml(mod, '')}<div><h3><a href="${esc(l.overview)}">${esc(mod.name)}</a></h3><p>${esc(mod.description)}</p>
<div class="row-meta"><span>${esc(mod.version)}</span><span>${esc(loaderNames(mod))}</span>${badges(mod)}</div><nav aria-label="${esc(mod.name)} resources">${nav}</nav></div></li>`;
    }).join('');
    return shell({
        title: 'Minecraft mods by chimericdream',
        url: '',
        body: `<section class="catalogue-intro" aria-labelledby="collection-title"><h1 id="collection-title">A curious collection</h1>
<div class="intro-copy"><p>Minecraft mods by chimericdream, from useful little fixes to wonderfully strange ideas. Built for Fabric and NeoForge.</p><nav aria-label="Browse mods"><a class="button" href="#directory">Find a mod</a></nav></div></section>
<section aria-label="Four mods from the collection"><ul class="wares">${wares}</ul></section>
<section class="directory" id="directory" aria-labelledby="directory-title"><div class="directory-top"><div><h2 id="directory-title">The rest of the collection</h2><p id="mod-count" role="status">${mods.length - featured.length} more mods</p></div>
<div class="search-control" hidden><label for="mod-search">Find something for your world</label><div class="search-input"><input type="search" id="mod-search" placeholder="Search all ${mods.length} mods" autocomplete="off" aria-controls="mod-list"><button type="button" id="clear-search">Clear</button></div></div></div>
<ul class="mod-list" id="mod-list">${rows}</ul><p class="empty-search" hidden>No mods match that search. Try another name or clear the search to browse.</p></section>`,
    });
};

export const overviewPage = (doc: Doc, body: string, links: ModLinks): string => {
    const mod = doc.mod;
    const actions = [
        mod.downloadUrl ? `<a class="button" href="${esc(mod.downloadUrl)}">Download on CurseForge</a>` : '',
        links.guide ? `<a${!mod.downloadUrl ? ' class="button"' : ''} href="${esc(relUrl(doc.url, links.guide))}">Read the guide</a>` : '',
        links.changelog ? `<a href="${esc(relUrl(doc.url, links.changelog))}">Changelog</a>` : '',
        links.ideas ? `<a href="${esc(relUrl(doc.url, links.ideas))}">Ideas</a>` : '',
        `<a href="${REPO_URL}/tree/main/${esc(mod.folder)}">Source</a>`,
    ].join('');
    return shell({
        title: `${mod.name} · chimericdream`,
        url: doc.url,
        crumbs: [esc(mod.name)],
        side: modLinksList(links, doc.url),
        body: `<div class="modhead">${iconHtml(mod, doc.url)}<div><h1>${esc(mod.name)}</h1><p>${esc(mod.description)}</p>
<div class="mod-meta"><span>Version ${esc(mod.version)}</span><span>Minecraft ${esc(mod.minecraftVersion)}</span><span>${esc(loaderNames(mod))}</span></div>${badges(mod)}</div></div>
<div class="actions">${actions}</div><article>${body}</article><p class="edit-link"><a href="${REPO_URL}/blob/main/${esc(doc.key)}">View source on GitHub</a></p>`,
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
