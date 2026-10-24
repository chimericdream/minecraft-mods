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
    const featured = new Map([
        ['minekea', 'Furniture, storage, and blocks for a world that feels like yours.'],
        ['hopper-xtreme', 'Faster. Filtered. Even upward.'],
        ['effective-gear', 'Put those armor trims to work.'],
        ['camel-nostrils', 'This mod is weird. Really weird.'],
    ]);
    const meta = (mod: ModInfo) => `${esc(mod.version)} &middot; ${esc(mod.loaders.map(l => l === 'fabric' ? 'Fabric' : l === 'neoforge' ? 'NeoForge' : l).join(' / '))}${badges(mod)}`;
    const searchText = (mod: ModInfo) => esc(`${mod.name} ${mod.description} ${mod.loaders.join(' ')} ${mod.minecraftVersion}`.toLocaleLowerCase());
    const navLinks = (mod: ModInfo, overview = true) => {
        const l = links.get(mod.folder)!;
        return [
            ...(overview ? [['Overview', l.overview]] : []), ['Guide', l.guide], ['Changelog', l.changelog], ['Ideas', l.ideas],
        ].filter(([, url]) => url).map(([label, url]) => `<a href="${esc(url!)}">${label}</a>`).join('');
    };
    const wares = [...featured].map(([folder, line]) => {
        const mod = mods.find(m => m.folder === folder);
        if (!mod) return '';
        const l = links.get(folder)!;
        const artwork = folder === 'minekea' ? 'catalogue.png' : mod.iconFile ? iconUrl(mod) : null;
        return `<li class="ware" data-mod="${searchText(mod)}">
<a class="ware-picture" href="${esc(l.overview)}" tabindex="-1" aria-hidden="true" data-reveal>${artwork ? `<img src="${esc(artwork)}" alt="" width="256" height="256" loading="lazy">` : iconHtml(mod, '')}</a>
<h3 data-reveal><a href="${esc(l.overview)}">${esc(mod.name)}</a></h3>
<p class="ware-line" data-reveal>${esc(line)}</p><p data-reveal>${esc(mod.description)}</p>
<p class="ver">${meta(mod)}</p><nav aria-label="${esc(mod.name)} pages">${navLinks(mod)}</nav></li>`;
    }).join('');
    const directory = mods.filter(mod => !featured.has(mod.folder)).map(mod => {
        const l = links.get(mod.folder)!;
        return `<li class="mod-entry" data-mod="${searchText(mod)}" data-reveal>${iconHtml(mod, '')}<div>
<h3><a href="${esc(l.overview)}">${esc(mod.name)}</a></h3><p>${esc(mod.description)}</p>
<p class="ver">${meta(mod)}</p><nav aria-label="${esc(mod.name)} pages">${navLinks(mod, false)}</nav></div></li>`;
    }).join('');
    return shell({
        title: 'Minecraft mods by chimericdream',
        url: '',
        home: true,
        body: `<section class="hero" aria-labelledby="home-title">
<h1 id="home-title"><span class="line"><span>Useful things.</span></span><span class="line"><span>Strange things.</span></span></h1>
<figure class="hero-small"><img src="mods/hopper-xtreme/icon.png" alt="A diamond-rimmed upgraded hopper" width="124" height="124"></figure>
<figure class="hero-book"><img src="catalogue.png" alt="Minekea’s blue in-game catalogue book" width="192" height="192"></figure>
<div class="hero-copy"><p>Minecraft mods by chimericdream, from a better bookshelf to a camel without a nose.</p><a class="button" href="#catalogue">Browse mods</a><p class="compat">${mods.length} mods for Fabric &amp; NeoForge</p></div></section>
<section id="catalogue" class="showcase" aria-labelledby="featured-title">
<div class="section-heading"><h2 id="featured-title" data-reveal>Four from the workshop</h2><p data-reveal>Big builds. Small fixes. Odd ideas.</p></div>
<div class="search-tools" hidden><label for="mod-search"><span class="sr-only">Find a mod</span><input id="mod-search" type="search" placeholder="Find a mod by name or what it does" autocomplete="off"></label><button id="clear-search" type="button">Clear</button><output id="mod-count" for="mod-search" aria-live="polite">${mods.length} mods</output></div>
<p id="mod-empty" hidden>No mods match your search. Try a name or a word like “storage”.</p>
<ul id="featured-mods" class="wares">${wares}</ul>
<section class="directory" aria-labelledby="directory-title"><div class="directory-intro"><h2 id="directory-title" data-reveal>The rest of the collection</h2><p data-reveal>More things to build with, play with, and make your world your own. Browse the mods below, or follow what’s next on the <a href="ideas/">roadmap</a>.</p></div><ul class="mod-list">${directory}</ul></section></section>`,
    });
};

export const overviewPage = (doc: Doc, body: string, links: ModLinks): string => {
    const mod = doc.mod;
    const actions = [
        mod.downloadUrl ? `<a class="button" href="${esc(mod.downloadUrl)}">Download on CurseForge</a>` : '',
        links.guide ? `<a${mod.downloadUrl ? '' : ' class="button"'} href="${esc(relUrl(doc.url, links.guide))}">In-game guide</a>` : '',
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
<p>${esc(mod.description)}</p></div></div><p class="mod-meta">Version ${esc(mod.version)} &middot; Minecraft ${esc(mod.minecraftVersion)} &middot; ${esc(mod.loaders.map(l => l === 'fabric' ? 'Fabric' : l === 'neoforge' ? 'NeoForge' : l).join(' / '))}${badges(mod)}</p><div class="actions">${actions}</div>
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
