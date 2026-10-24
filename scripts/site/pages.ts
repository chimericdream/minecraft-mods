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
    const featured = ['minekea', 'hopper-xtreme', 'effective-gear', 'camel-nostrils'];
    const catalogueItem = (mod: ModInfo) => {
        const l = links.get(mod.folder)!;
        const nav = [
            ['Overview', l.overview], ['Guide', l.guide], ['Changelog', l.changelog], ['Ideas', l.ideas],
        ].filter(([, url]) => url).map(([label, url]) => `<a href="${esc(url!)}">${label}</a>`).join('');
        return `<li class="mod-row"${featured.includes(mod.folder) ? ' data-feature-row hidden' : ''} data-mod="${esc(`${mod.name} ${mod.description} ${mod.platforms.join(' ')}`.toLocaleLowerCase())}">${iconHtml(mod, '')}<div><h3><a href="${esc(l.overview)}">${esc(mod.name)}</a></h3>
<div class="row-meta">${esc(mod.version)} &middot; ${esc(platforms(mod))} &middot; Minecraft ${esc(mod.minecraft)}</div>${badges(mod)}
<p>${esc(mod.description)}</p><nav aria-label="${esc(mod.name)} documentation">${nav}</nav></div></li>`;
    };
    const featureCopy: Record<string, string> = {
        minekea: 'Furniture, storage, and building blocks. Some assembly required.',
        'hopper-xtreme': 'A familiar block with a few very practical upgrades.',
        'effective-gear': 'Small bonuses for the armor, weapons, and tools you already use.',
        'camel-nostrils': 'This mod is weird. Really weird.',
    };
    const artAlt: Record<string, string> = {minekea: 'Minekea glass jars displaying their contents on a shelf', 'hopper-xtreme': 'A diamond-rimmed hopper', 'effective-gear': 'Effective Gear mod artwork', 'camel-nostrils': 'Camel Nostrils mod artwork'};
    const features = featured.flatMap(folder => {
        const mod = mods.find(m => m.folder === folder);
        if (!mod) return [];
        const l = links.get(folder)!;
        return [`<article class="feature-entry"><div><h3><a href="${esc(l.overview)}">${esc(mod.name)}</a></h3><p>${featureCopy[folder]}</p><p class="ver">${esc(mod.version)} &middot; ${esc(platforms(mod))}</p>${badges(mod)}<p><a class="entry-link" href="${esc(l.overview)}">Explore ${esc(mod.name)}</a></p></div><img class="feature-art" src="art/${folder}.png" alt="${artAlt[folder]}" width="480" height="320" loading="lazy"></article>`];
    }).join('');
    const rows = mods.map(catalogueItem).join('');
    return shell({
        title: 'Minecraft mods by chimericdream',
        url: '',
        home: true,
        body: `<section class="hero" aria-labelledby="home-title"><h1 class="hero-title" id="home-title"><span>Useful mods.</span><span class="specimen" aria-hidden="true"><img src="art/catalogue.png" alt="" width="320" height="320"><img src="art/hopper-xtreme.png" alt="" width="320" height="320"><img src="art/camel-nostrils.png" alt="" width="320" height="320"></span><span>Odd ideas.</span></h1><div class="hero-bottom"><p>${mods.length} Minecraft mods by chimericdream, for Fabric and NeoForge. Furniture, useful fixes, and a few very strange ideas.</p><a class="button" href="#catalogue">Browse the mods</a></div></section>
<section class="feature-section" aria-labelledby="features-title"><div class="section-intro"><h2 id="features-title">From useful to unusual</h2></div><div class="feature-band">${features}</div></section>
<section class="catalogue" id="catalogue" aria-labelledby="catalogue-title"><div class="catalogue-intro"><h2 id="catalogue-title">Find your next mod</h2><p>The rest of the collection, from small fixes to shared libraries.</p><p>Every mod’s README, changelog, guide, and feature ideas are here as they are written.</p><p><a href="ideas/">See what’s taking shape</a><br><a href="changelog/">Read the latest changes</a></p></div><div><div class="catalogue-tools" data-search-controls hidden><label for="mod-search">Search all ${mods.length} mods</label><input id="mod-search" type="search" placeholder="Try furniture, hoppers, or gear" autocomplete="off" aria-describedby="mod-count"><p class="catalogue-count" id="mod-count" role="status"></p></div><p class="no-results" id="no-mods" hidden>No matching mods. Try a different name or a shorter search.</p><ul class="mod-list">${rows}</ul></div></section>`,
    });
};

const platforms = (mod: ModInfo): string => mod.platforms.map(p => p === 'neoforge' ? 'NeoForge' : p === 'fabric' ? 'Fabric' : p).join(' / ');

export const overviewPage = (doc: Doc, body: string, links: ModLinks): string => {
    const mod = doc.mod;
    const dependencyLabels: Record<string, string> = {'architectury-api': 'Architectury API', chimericlib: 'ChimericLib', opus: 'Opus'};
    const dependencyName = (id: string) => dependencyLabels[id] || id;
    const dependencies = (label: string, ids: string[]) => ids.length ? `<div><strong>${label}</strong><span>${esc(ids.map(dependencyName).join(', '))}</span></div>` : '';
    const preview = mod.folder === 'minekea' ? `<figure class="mod-preview"><img src="${esc(relUrl(doc.url, 'art').replace(/\/$/, '') + '/minekea.png')}" alt="Glass jars displaying their contents on a Minekea shelf" width="240" height="180"><figcaption>Glass jars and shelves from Minekea. Explore the catalogue for furniture, storage, and building blocks.</figcaption></figure>` : '';
    const actions = [
        mod.curseforgeProjectId ? `<a class="button" href="https://www.curseforge.com/projects/${esc(mod.curseforgeProjectId)}/files">Download on CurseForge</a>` : '',
        links.guide ? `<a${mod.curseforgeProjectId ? '' : ' class="button"'} href="${esc(relUrl(doc.url, links.guide))}">Read the guide</a>` : '',
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
<p>${esc(mod.description)}</p></div></div><div class="mod-meta"><div><strong>Version</strong><span>${esc(mod.version)}</span>${badges(mod)}</div><div><strong>Minecraft</strong><span>${esc(mod.minecraft)}</span></div><div><strong>Loaders</strong><span>${esc(platforms(mod))}</span></div>${dependencies('Required mods', mod.requiredMods)}${dependencies('Optional mods', mod.optionalMods)}</div><div class="actions">${actions}</div>${preview}
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
