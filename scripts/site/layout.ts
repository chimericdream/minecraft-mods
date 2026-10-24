import path from 'node:path';

import {HEAD_BOOT} from './client.ts';
import type {Guide, GuidePage} from './guide.ts';
import {iconHtml} from './icons.ts';
import {FONT_LINKS} from './styles.ts';
import type {ModInfo} from './types.ts';
import {esc, REPO_URL, relUrl, rootPrefix} from './util.ts';

export {THEME_CSS} from './theme.ts';
export {SITE_CSS} from './styles.ts';

/** Where the build copies the mark, relative to the site root. The source file is `LOGO_SOURCE` in build.ts. */
export const LOGO_URL = '_site/logo.svg';
export const LOGO_ALT = 'chimericdream: a crimson dragon draped across twin snow-capped mountains under a crescent moon';

const NAV = [
    {label: 'Mods', href: '', match: (u: string) => u === '' || u.startsWith('mods/')},
    {label: 'Roadmap', href: 'ideas/', match: (u: string) => u === 'ideas/'},
    {label: 'Changelog', href: 'changelog/', match: (u: string) => u === 'changelog/'},
];

export const shell = (opts: {
    title: string;
    url: string;
    body: string;
    crumbs?: string[];
    side?: string;
    wide?: boolean;
    /** The home page is a full-width board with no sidebar or page panel. */
    home?: boolean;
    head?: string;
}): string => {
    const up = rootPrefix(opts.url) || './';
    const crumbs = opts.crumbs?.length
        ? `<nav class="crumbs" aria-label="Breadcrumb"><a href="${up}">Mods</a>${opts.crumbs.map(c => `<span aria-hidden="true">›</span>${c}`).join('')}</nav>`
        : '';
    const cls = ['layout', opts.side ? '' : 'no-side', opts.wide ? 'wide' : ''].filter(Boolean).join(' ');
    const nav = NAV.map(n => `<a class="nav" href="${up}${n.href}"${n.match(opts.url) ? ' aria-current="page"' : ''}>${n.label}</a>`).join('');
    const main = opts.home
        ? `<main id="main" class="home">${opts.body}</main>`
        : `<div class="${cls}">${opts.side ? `<aside class="side" aria-label="This mod">${opts.side}</aside>` : ''}<main id="main">${crumbs}${opts.body}</main></div>`;
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(opts.title)}</title><meta name="color-scheme" content="light dark">
<link rel="icon" href="${up}${LOGO_URL}" type="image/svg+xml">${FONT_LINKS}${HEAD_BOOT}
<link rel="stylesheet" href="${up}site.css">${opts.head ?? ''}<script src="${up}site.js" defer></script></head>
<body data-root="${up}" id="top"><a class="skip" href="#main">Skip to content</a>
<header class="top"><a class="brand" href="${up}"><img src="${up}${LOGO_URL}" alt="" width="40" height="40">chimericdream</a>
<nav aria-label="Site">${nav}</nav>
<div class="tools"><a class="nav" href="${REPO_URL}">GitHub</a><button class="theme-toggle" type="button">Theme</button></div></header>
${main}
<footer class="foot"><div class="foot-body"><img class="foot-mark" src="${up}${LOGO_URL}" alt="${esc(LOGO_ALT)}" width="750" height="750" loading="lazy">
<p class="foot-name">chimericdream</p>
<div class="foot-links"><a href="${up}">Mods</a><a href="${up}ideas/">Roadmap</a><a href="${up}changelog/">Changelog</a><a href="${REPO_URL}">Source on GitHub</a></div>
<p class="foot-note"><span>Every page here is generated from the READMEs, changelogs, in-game guides and ideas folders in the repository.</span><a href="#top">Back to top</a></p></div></footer>
</body></html>
`;
};

const stripTags = (html: string) => html.replace(/<[^>]+>/g, '').replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').trim();

/** The `<article>` for a reading page: the text column plus, when the page has enough headings, an "On this page" rail. */
export const readingPage = (inner: string): string => {
    const found = [...inner.matchAll(/<h([23]) id="([^"]+)">(.*?)<\/h[23]>/g)];
    const level = found.some(m => m[1] === '2') ? '2' : '3';
    const items = found.filter(m => m[1] === level).slice(0, 80);
    const toc = items.length >= 3
        ? `<nav class="toc" aria-label="On this page"><p>On this page</p><ul>${items.map(m => `<li><a href="#${m[2]}">${esc(stripTags(m[3]!))}</a></li>`).join('')}</ul></nav>`
        : '';
    return `<article class="page${toc ? ' has-toc' : ''}"><div class="reading">${inner}</div>${toc}</article>`;
};

/** The links a mod's pages share: where its overview, guide, changelog and ideas live. */
export type ModLinks = {overview: string; guide: string | null; changelog: string | null; ideas: string | null};

/** The sidebar's opening block: the mod's own mark and name, then links to its overview, guide, changelog and ideas. */
export const modLinksList = (links: ModLinks, from: string, mod?: ModInfo): string => {
    const item = (label: string, url: string | null) => url ? `<li><a href="${esc(relUrl(from, url))}"${url === from ? ' aria-current="page"' : ''}>${label}</a></li>` : '';
    const who = mod ? `<a class="who" href="${esc(relUrl(from, links.overview))}">${iconHtml(mod, from)}<span>${esc(mod.name)}</span></a>` : '';
    return `${who}<h2>In this mod</h2><ul>${item('Overview', links.overview)}${item('Guide', links.guide)}${item('Changelog', links.changelog)}${item('Ideas', links.ideas)}</ul>`;
};

/** A titled sidebar section that folds on narrow screens (see `data-fold` in client.ts). */
export const sideGroup = (title: string, inner: string): string =>
    `<details class="group" data-fold open><summary class="h">${esc(title)}</summary>${inner}</details>`;

type NavNode = {dirs: Map<string, NavNode>; files: GuidePage[]};

const navTree = (pages: GuidePage[]): NavNode => {
    const root: NavNode = {dirs: new Map(), files: []};
    for (const p of pages) {
        const parts = p.rel.split('/');
        let node = root;
        for (const dir of parts.slice(0, -1)) {
            if (!node.dirs.has(dir)) node.dirs.set(dir, {dirs: new Map(), files: []});
            node = node.dirs.get(dir)!;
        }
        node.files.push(p);
    }
    return root;
};

const renderGuideNav = (node: NavNode, current: GuidePage): string => {
    const link = (p: GuidePage) =>
        `<a href="${esc(relUrl(current.url, p.url))}"${p === current ? ' aria-current="page"' : ''}>${esc(p.title)}</a>`;
    const files = node.files.filter(f => path.posix.basename(f.rel) !== 'index.md').map(f => `<li>${link(f)}</li>`).join('');
    const dirs = [...node.dirs].map(([name, child]) => {
        const index = child.files.find(f => path.posix.basename(f.rel) === 'index.md');
        const inner = renderGuideNav(child, current);
        const open = inner.includes('aria-current') || index === current ? ' open' : '';
        return `<li><details${open}><summary>${index ? link(index) : esc(name)}</summary>${inner}</details></li>`;
    }).join('');
    return `<ul>${files}${dirs}</ul>`;
};

export const guidePage = (guide: Guide, page: GuidePage, links: ModLinks, mod: ModInfo): string => {
    const home = guide.pages.find(p => p.rel === 'index.md')!;
    return shell({
        title: `${page.title} · ${guide.title}`,
        url: page.url,
        crumbs: [`<a href="${esc(relUrl(page.url, links.overview))}">${esc(guide.mod.name)}</a>`, `<a href="${esc(relUrl(page.url, home.url))}">Guide</a>`],
        side: modLinksList(links, page.url, mod) + sideGroup('Guide', renderGuideNav(navTree(guide.pages), page)),
        body: readingPage(`<h1>${esc(page.title)}</h1><p class="deck">A page of the ${esc(guide.title)}, the in-game guide to ${esc(guide.mod.name)}.</p>${page.body}`),
    });
};
