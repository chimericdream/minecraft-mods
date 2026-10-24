import {existsSync, readFileSync} from 'node:fs';
import path from 'node:path';

import type {Doc, DocKey} from './docs.ts';
import {modLinksList, shell, type ModLinks} from './layout.ts';
import type {ModInfo} from './types.ts';
import {esc, relUrl, REPO_ROOT, REPO_URL} from './util.ts';

const initials = (name: string) =>
    name.split(/[\s-]+/).filter(Boolean).slice(0, 2).map(w => w[0]!.toUpperCase()).join('');

const loaderNames = (mod: ModInfo) => (mod.loaders ?? ['fabric', 'neoforge']).map(loader => loader === 'fabric' ? 'Fabric' : loader === 'neoforge' ? 'NeoForge' : loader).join(' / ');

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

const FEATURED = [
    ['minekea', 'Furniture, storage & building blocks.'],
    ['hopper-xtreme', 'A practical upgrade to the humble hopper.'],
    ['effective-gear', 'Small bonuses for the gear you already use.'],
    ['camel-nostrils', 'This mod is weird. Really weird.'],
] as const;

export const homePage = (mods: ModInfo[], links: Map<string, ModLinks>): string => {
    const featured = new Set<string>(FEATURED.map(([folder]) => folder));
    const plates = FEATURED.flatMap(([folder, description]) => {
        const mod = mods.find(m => m.folder === folder);
        if (!mod) return [];
        const l = links.get(folder)!;
        return [`<li class="plate">${iconHtml(mod, '')}<h2><a href="${esc(l.overview)}">${esc(mod.name)}</a></h2><p>${esc(description)}</p><div class="ver">${esc(mod.version)} &middot; ${esc(loaderNames(mod))} ${badges(mod)}</div></li>`];
    }).join('');
    const rows = mods.map(mod => {
        const l = links.get(mod.folder)!;
        const nav = [['Guide', l.guide], ['Changelog', l.changelog], ['Ideas', l.ideas]]
            .filter(([, url]) => url).map(([label, url]) => `<a href="${esc(url!)}">${label}</a>`).join('');
        const isFeatured = featured.has(mod.folder);
        return `<li class="mod-row" data-featured="${isFeatured}" data-search="${esc(`${mod.name} ${mod.description}`.toLocaleLowerCase())}"${isFeatured ? ' hidden' : ''}>
${iconHtml(mod, '')}<div><h3><a href="${esc(l.overview)}">${esc(mod.name)}</a></h3><p>${esc(mod.description)}</p>
<div class="ver">${esc(mod.version)} &middot; ${esc(loaderNames(mod))} ${badges(mod)}</div><nav aria-label="${esc(mod.name)} resources">${nav}</nav></div></li>`;
    }).join('');
    return shell({
        title: 'Minecraft mods by chimericdream', url: '', home: true,
        body: `<ul class="masthead" aria-label="Four mods from the collection">${plates}</ul>
<section class="hero-copy" aria-labelledby="home-title"><h1 id="home-title"><span class="title-line"><span>Minecraft,</span></span><span class="title-line"><span>with character</span></span></h1>
<div class="intro"><p>Furniture, useful little fixes, and the occasional camel nose. ${mods.length} Minecraft mods by chimericdream, for Fabric and NeoForge.</p><a class="button" href="#catalogue">Browse the mods</a></div></section>
<section class="catalogue" id="catalogue" aria-labelledby="catalogue-title"><div class="catalogue-head"><div><h2 id="catalogue-title">The mod catalogue</h2><p>Find something for your next world.</p></div>
<div class="catalogue-search" hidden><label for="mod-search">Find a mod<input type="search" id="mod-search" placeholder="Name or description" autocomplete="off"></label><button id="clear-search" type="button">Clear</button></div></div>
<p id="catalogue-count" class="catalogue-count" role="status">${mods.length - FEATURED.length} more mods. The four featured mods are above.</p><ul class="mod-list">${rows}</ul><p id="catalogue-empty" hidden>No mods match that search. Try a different name or clear the search.</p></section>`,
    });
};

export const overviewPage = (doc: Doc, body: string, links: ModLinks): string => {
    const mod = doc.mod;
    const actions = [
        mod.downloadUrl ? `<a href="${esc(mod.downloadUrl)}">Download on CurseForge</a>` : '',
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
        body: `<div class="modhead">${iconHtml(mod, doc.url)}<div><h1>${esc(mod.name)}</h1><div class="ver">${esc(mod.version)} &middot; ${esc(loaderNames(mod))} ${badges(mod)}</div>
<p>${esc(mod.description)}</p>${mod.minecraft ? `<p class="compatibility">Minecraft ${esc(mod.minecraft)}</p>` : ''}</div></div><div class="actions">${actions}</div>
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
