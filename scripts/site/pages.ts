import {existsSync, readFileSync} from 'node:fs';
import path from 'node:path';

import type {Doc, DocKey} from './docs.ts';
import {iconHtml} from './icons.ts';
import {LOGO_ALT, LOGO_URL, modLinksList, readingPage, shell, type ModLinks} from './layout.ts';
import type {ModInfo} from './types.ts';
import {esc, relUrl, REPO_ROOT, REPO_URL} from './util.ts';

export {iconUrl} from './icons.ts';

/** The four mods the home page features: big and small, practical and silly (docs/SITE-DESIGN-BRIEF.md §8). */
const FEATURED = ['minekea', 'hopper-xtreme', 'effective-gear', 'camel-nostrils'] as const;

const ARCHITECTURY_URL = 'https://www.curseforge.com/minecraft/mc-mods/architectury-api';

export type HomeExtras = {
    /** Number of in-game guide pages per mod folder. */
    guidePages: Map<string, number>;
    minecraft: string;
    /** Number of mods that list Opus as an optional dependency. */
    opusOptional: number;
};

const statusChips = (mod: ModInfo) => [
    mod.isNewMod ? '<span class="chip new">new</span>' : '',
    mod.isPrerelease ? '<span class="chip beta">pre-release</span>' : '',
    mod.hasUnreleasedChanges ? '<span class="chip unrel" title="The changelog has unreleased changes">unreleased changes</span>' : '',
].join('');

const loaderChips = (mod: ModInfo) => mod.loaders.map(l => `<span class="chip">${esc(l)}</span>`).join('');

const curseforgeUrl = (mod: ModInfo) => mod.curseforgeId ? `https://www.curseforge.com/projects/${mod.curseforgeId}` : null;

/** The first real feature bullet from a README's "Current Features" list, skipping the guide bullet and list lead-ins. */
const teaser = (mod: ModInfo): string | null => {
    const file = path.join(REPO_ROOT, mod.folder, 'README.md');
    if (!existsSync(file)) return null;
    const section = readFileSync(file, 'utf8').replace(/\r\n/g, '\n').split(/^#{2,3} Current Features\s*$/m)[1]?.split(/^#{2,3} /m)[0] ?? '';
    const bullet = [...section.matchAll(/^\* (.+(?:\n {2,}.+)*)/gm)].map(m => m[1]!.replace(/\s+/g, ' ')).find(b => !/in-game guide/i.test(b) && !b.endsWith(':'));
    return bullet ? bullet.replace(/\[([^\]]+)\]\([^)]*\)/g, '$1').replace(/[*_`]/g, '').split(/(?<=[.!?])\s/)[0]! : null;
};

const featuredTile = (mod: ModInfo, role: 'xl' | 'lg' | 'm1' | 'm2', l: ModLinks, extras: HomeExtras): string => {
    const find = esc(`${mod.name} ${mod.description} ${mod.loaders.join(' ')}`.toLowerCase());
    const guideCount = extras.guidePages.get(mod.folder);
    const stat = guideCount ? `<p class="stat">${guideCount} in-game guide pages</p>` : '';
    const open = (tone: string) => `<a class="tile link t ${role} ${tone}" href="${esc(l.overview)}" data-find="${find}" data-reveal>`;

    if (role === 'xl') {
        return `${open('violet')}<div class="head">${iconHtml(mod, '', 'big')}<div class="chips"><span class="chip">${esc(mod.version)}</span>${statusChips(mod)}</div></div>
<div class="meta"><h3>${esc(mod.name)}</h3><p class="lead">${esc(mod.tagline ?? mod.description)}</p>${stat}</div></a>`;
    }
    if (role === 'lg') {
        return `${open('tone')}<div class="head">${iconHtml(mod, '', 'big')}<div class="chips"><span class="chip">${esc(mod.version)}</span>${statusChips(mod)}</div></div>
<div class="meta"><h3>${esc(mod.name)}</h3><p class="lead">${esc(mod.description)}</p>${stat}</div></a>`;
    }
    if (role === 'm1') {
        return `${open('')}<div class="meta"><h3>${esc(mod.name)}</h3><p>${esc(mod.description)}</p><div class="chips"><span class="chip">${esc(mod.version)}</span>${statusChips(mod)}</div></div>${iconHtml(mod, '', 'big')}</a>`;
    }
    const sentences = (mod.tagline ?? mod.description).split(/(?<=[.!?])\s+/);
    const quote = sentences.length > 2 ? sentences.slice(0, 2).join(' ') : sentences.join(' ');
    const extra = teaser(mod);
    return `${open('crimson')}<div class="meta"><h3>${esc(mod.name)}</h3><p class="quote">${esc(quote)}</p>${extra ? `<p class="small">${esc(extra)}</p>` : ''}</div>${iconHtml(mod, '', 'big')}</a>`;
};

const dirTile = (mod: ModInfo, l: ModLinks): string => {
    const find = esc(`${mod.name} ${mod.description} ${mod.loaders.join(' ')}`.toLowerCase());
    const partial = mod.loaders.length === 1 ? `<span class="chip">${esc(mod.loaders[0]!)} only</span>` : '';
    return `<a class="tile s link" role="listitem" href="${esc(l.overview)}" data-find="${find}" data-reveal><div class="txt">
<h3>${esc(mod.name)}</h3>
<div class="line"><span>${esc(mod.version)}</span>${statusChips(mod)}${partial}</div>
<p class="desc">${esc(mod.description)}</p></div>${iconHtml(mod, '')}</a>`;
};

export const homePage = (mods: ModInfo[], links: Map<string, ModLinks>, extras: HomeExtras): string => {
    const byFolder = new Map(mods.map(m => [m.folder, m]));
    const featured = FEATURED.map(f => byFolder.get(f)).filter((m): m is ModInfo => Boolean(m));
    const rest = mods.filter(m => !featured.includes(m));
    const roles = ['xl', 'lg', 'm1', 'm2'] as const;
    const both = mods.filter(m => m.loaders.length === 2).length;
    const depTile = (cls: string, name: string, href: string, text: string, external = false) =>
        `<a class="tile d link ${cls}" href="${esc(href)}"${external ? ' target="_blank" rel="noopener"' : ''} data-reveal><h3>${esc(name)}</h3><p class="small">${text}</p></a>`;
    const chimericlib = links.get('chimeric-lib')?.overview;
    const opus = links.get('opus')?.overview;

    return shell({
        title: 'chimericdream · Minecraft mods for Fabric and NeoForge',
        url: '',
        home: true,
        body: `<section class="hero board" aria-label="Introduction">
<div class="tile violet name" data-reveal><h1>A workshop of Minecraft mods</h1>
<p class="lead">${mods.length} mods for Fabric and NeoForge by chimericdream: furniture, storage, building blocks and small fixes. Read the guides, follow the roadmap, take what you like.</p></div>
<div class="tile mark" data-reveal><img src="${LOGO_URL}" alt="${esc(LOGO_ALT)}" width="750" height="750"></div>
<div class="tile find" data-reveal><form role="search"><label for="finder">Find a mod</label>
<div class="row"><input id="finder" type="search" placeholder="Name or what it does ( / )" autocomplete="off"><button class="btn" type="submit">Open</button></div></form>
<p class="count" id="find-count">${mods.length} mods</p></div>
<a class="tile crimson link go" href="#featured" data-reveal><span>Start with these four</span><span class="arrow" aria-hidden="true">↓</span></a>
<div class="tile facts" data-reveal><p class="fact">Fabric and NeoForge</p><p class="fact">Minecraft ${esc(extras.minecraft)}</p>
<p class="small">${both} of the ${mods.length} mods run on both loaders.</p></div>
</section>
<section class="sec" id="featured" data-group><h2>Four to start with</h2>
<p class="sub">Big and small, practical and silly: the range of what gets built here.</p>
<div class="board feat">${featured.map((m, i) => featuredTile(m, roles[i]!, links.get(m.folder)!, extras)).join('')}</div></section>
<section class="sec" id="mods" data-group><h2>The rest of the workshop</h2>
<p class="sub">${rest.length} more mods, listed alphabetically. Each one has a page with its README, changelog and, where it has one, an in-game guide.</p>
<div class="board dir" role="list">${rest.map(m => dirTile(m, links.get(m.folder)!)).join('')}</div></section>
<p class="nomatch" id="nomatch" hidden>No mod matches that. Try a shorter word.</p>
<section class="sec"><h2>What they stand on</h2>
<p class="sub">Most of the mods lean on the same small foundation, so a modpack usually needs only a few of them.</p>
<div class="board stand">${[
            depTile('arch', 'Architectury API', ARCHITECTURY_URL, 'The layer that lets one build run on both Fabric and NeoForge. A separate download from CurseForge; each mod page lists what it needs.', true),
            chimericlib ? depTile('clib', 'ChimericLib', chimericlib, `The shared code the other ${mods.length - 1} mods are built on, published here as its own mod.`) : '',
            opus ? depTile('opus', 'Opus', opus, `Powers the in-game guides. An optional extra for ${extras.opusOptional} of the mods.`) : '',
        ].join('')}</div></section>`,
    });
};

export const overviewPage = (doc: Doc, body: string, links: ModLinks): string => {
    const mod = doc.mod;
    const cf = curseforgeUrl(mod);
    const buttons = [
        cf ? `<a class="btn" href="${esc(cf)}" target="_blank" rel="noopener">Download on CurseForge</a>` : '',
        links.guide ? `<a class="btn alt" href="${esc(relUrl(doc.url, links.guide))}">In-game guide</a>` : '',
        links.changelog ? `<a class="btn alt" href="${esc(relUrl(doc.url, links.changelog))}">Changelog</a>` : '',
        links.ideas ? `<a class="btn alt" href="${esc(relUrl(doc.url, links.ideas))}">Ideas</a>` : '',
        `<a class="btn alt" href="${REPO_URL}/tree/main/${esc(mod.folder)}">Source</a>`,
    ].join('');
    return shell({
        title: `${mod.name} · chimericdream`,
        url: doc.url,
        crumbs: [esc(mod.name)],
        side: modLinksList(links, doc.url, mod),
        body: `<header class="board modhead"><div class="tile mi" data-reveal>${iconHtml(mod, doc.url, 'big')}</div>
<div class="tile violet mt" data-reveal><h1>${esc(mod.name)}</h1>
<div><p class="lead">${esc(mod.description)}</p><div class="chips"><span class="chip">${esc(mod.version)}</span>${statusChips(mod)}${loaderChips(mod)}</div></div></div>
<div class="tile ma" data-reveal>${buttons}</div></header>
${readingPage(`${body}<p class="edit-link"><a href="${REPO_URL}/blob/main/${esc(doc.key)}">View source on GitHub</a></p>`)}`,
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
        `<section class="feed-section" id="dev-${esc(e.mod.folder)}" data-reveal><h2>${link(e.mod)}</h2>${render(e.unreleased!.body, e.key)}</section>`).join('');
    const shipped = entries.filter(e => e.latest).map(e =>
        `<section class="feed-section" id="rel-${esc(e.mod.folder)}" data-reveal><h2>${link(e.mod)} <span class="ver">${esc(e.latest!.heading)}</span></h2>${render(e.latest!.body, e.key)}</section>`).join('');
    const jump = entries.filter(e => e.latest).map(e => `<li><a href="#rel-${esc(e.mod.folder)}">${esc(e.mod.name)}</a></li>`).join('');

    return shell({
        title: 'Changelog · chimericdream',
        url,
        wide: true,
        body: `<header class="pagehead"><h1>Changelog</h1><p>What is in development and what shipped last, across all ${mods.length} mods.</p></header>
<h2 class="feed-title">In development</h2>
${upcoming ? `<div class="feed">${upcoming}</div>` : '<p class="muted">Nothing unreleased right now.</p>'}
<h2 class="feed-title">Latest releases</h2>
<ul class="jump" aria-label="Jump to a mod">${jump}</ul>
<div class="feed">${shipped}</div>`,
    });
};
