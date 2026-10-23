import {existsSync, readFileSync} from 'node:fs';
import path from 'node:path';

import type {Doc, DocKey} from './docs.ts';
import {modLinksList, shell, type ModLinks} from './layout.ts';
import type {ModInfo} from './types.ts';
import {esc, relUrl, REPO_ROOT, REPO_URL} from './util.ts';

const initials = (name: string) =>
    name.split(/[\s-]+/).filter(Boolean).slice(0, 2).map(w => w[0]!.toUpperCase()).join('');

const hue = (s: string) => [...s].reduce((h, c) => (h * 31 + c.charCodeAt(0)) % 360, 7);

/** Site-relative location of a mod's copied icon. */
export const iconUrl = (mod: ModInfo) => `mods/${mod.folder}/icon.png`;

const iconHtml = (mod: ModInfo, from: string) => mod.iconFile
    ? `<img class="icon" src="${esc(path.posix.relative(from, iconUrl(mod)))}" alt="" width="48" height="48">`
    : `<div class="icon ph" style="--h:${hue(mod.id)}" aria-hidden="true">${esc(initials(mod.name))}</div>`;

const badges = (mod: ModInfo) => [
    mod.isNewMod ? '<span class="badge new">new</span>' : '',
    mod.isPrerelease ? '<span class="badge beta">pre-release</span>' : '',
    mod.hasUnreleasedChanges ? '<span class="badge unrel" title="The changelog has unreleased changes">unreleased changes</span>' : '',
].join('');

export const homePage = (mods: ModInfo[], links: Map<string, ModLinks>): string => {
    const cards = mods.map(mod => {
        const l = links.get(mod.folder)!;
        const nav = [
            ['Overview', l.overview], ['Guide', l.guide], ['Changelog', l.changelog], ['Ideas', l.ideas],
        ].filter(([, url]) => url).map(([label, url]) => `<a href="${esc(url!)}">${label}</a>`).join('');
        return `<li class="card"><header>${iconHtml(mod, '')}<div><h2><a href="${esc(l.overview)}">${esc(mod.name)}</a></h2>
<div class="ver">${esc(mod.version)}${badges(mod)}</div></div></header>
<p>${esc(mod.description)}</p><nav>${nav}</nav></li>`;
    }).join('');
    return shell({
        title: 'Chimeric Mods',
        url: '',
        body: `<h1>Chimeric Mods</h1>
<p>Minecraft mods for Fabric and NeoForge. Every mod's README, changelog, in-game guide and feature ideas are published here as they are written. The <a href="ideas/">roadmap</a> lists the ideas under consideration, and the <a href="changelog/">changelog</a> shows what has shipped and what is coming.</p>
<ul class="cards">${cards}</ul>`,
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
        body: `<div class="modhead">${iconHtml(mod, doc.url)}<div><h1>${esc(mod.name)} <small class="ver">${esc(mod.version)}</small>${badges(mod)}</h1>
<p>${esc(mod.description)}</p></div></div><div class="actions">${actions}</div>
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
