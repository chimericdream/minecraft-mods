import {readFileSync} from 'node:fs';
import path from 'node:path';

import {SITE_JS, THEME_INIT} from './client.ts';

import type {Guide, GuidePage} from './guide.ts';
import {esc, REPO_URL, relUrl, rootPrefix} from './util.ts';

/** Color tokens shared by every page, including the local-only GitHub panel. */
const LIGHT = '--bg:#f0efeb;--fg:#251d2d;--muted:#655d6b;--surface:#e5e1e6;--card:#e5e1e6;--line:#c3bbc8;--accent:#8b1a35;--accent-hover:#6c1228;--accent-ink:#fff6f5;--field:#d5cedb;--good:#28623a;--bad:#a31e32;--warn:#775215;--mark:#e3d39e;color-scheme:light';
const DARK = '--bg:#151119;--fg:#eeeaf0;--muted:#bbb0c3;--surface:#251d2e;--card:#251d2e;--line:#4b3a57;--accent:#f29da9;--accent-hover:#ffc0c8;--accent-ink:#30151e;--field:#2d1050;--good:#a0d3a1;--bad:#ff9eac;--warn:#e3bf7c;--mark:#5e4e25;color-scheme:dark';

export const THEME_CSS = `
:root{${LIGHT}}
@media(prefers-color-scheme:dark){:root:not([data-theme=light]){${DARK}}}
:root[data-theme=dark]{${DARK}}
`;

export const SITE_CSS = `
${THEME_CSS}
:root{--font-display:'EB Garamond',Georgia,serif;--font-text:'Source Sans 3',Arial,sans-serif;--gutter:clamp(20px,5vw,72px)}
${readFileSync(new URL('./styles.css', import.meta.url), 'utf8')}
`;

export const shell = (opts: {
    title: string;
    url: string;
    body: string;
    crumbs?: string[];
    side?: string;
    wide?: boolean;
    head?: string;
    home?: boolean;
}): string => {
    const up = rootPrefix(opts.url) || './';
    const crumbs = opts.crumbs?.length ? `<nav aria-label="Breadcrumb" class="crumbs"><a href="${up}">Mods</a>${opts.crumbs.map(c => `<span aria-hidden="true">/</span>${c}`).join('')}</nav>` : '';
    const cls = ['layout', opts.side ? '' : 'no-side', opts.wide ? 'wide' : '', opts.home ? 'home-layout' : ''].filter(Boolean).join(' ');
    const current = (url: string) => opts.url === url ? ' aria-current="page"' : '';
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(opts.title)}</title><meta name="color-scheme" content="light dark">
<script>${THEME_INIT}</script>
<link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=EB+Garamond:wght@400;500&family=Source+Sans+3:wght@400;600&display=swap" rel="stylesheet">
<link rel="stylesheet" href="${up}site.css">${opts.head ?? ''}</head>
<body data-root="${up}"><a class="skip" href="#main">Skip to content</a>
<header class="top"><a class="brand" href="${up}"><img src="${up}brand.svg" width="48" height="48" alt="">chimericdream</a>
<nav aria-label="Main"><a href="${up}"${current('')}>Mods</a><a href="${up}ideas/"${current('ideas/')}>Roadmap</a><a href="${up}changelog/"${current('changelog/')}>Changelog</a><a href="${REPO_URL}">GitHub</a></nav>
<label class="theme-control"><span>Theme</span><select id="color-theme" aria-label="Color theme"><option value="system">System</option><option value="light">Light</option><option value="dark">Dark</option></select></label></header>
${crumbs}
<div class="${cls}">${opts.side ? `<details class="side" open><summary>In this mod</summary><nav aria-label="Mod navigation">${opts.side}</nav></details>` : ''}<main id="main" tabindex="-1">${opts.body}</main></div>
<footer class="site-footer"><div class="footer-inner"><div class="footer-art"><img src="${up}brand.svg" alt="A crimson dragon across twin snowy mountains under a crescent moon" width="320" height="320" loading="lazy"></div>
<div class="footer-menu"><h2>Still being made</h2><nav aria-label="Footer"><a href="${up}ideas/">The roadmap</a><a href="${up}changelog/">Latest changes</a><a href="${REPO_URL}">The source</a></nav><p>Useful additions, peculiar experiments, and the occasional upside-down anvil.</p></div></div><p class="footer-credit">Minecraft mods by chimericdream. Built from the same guides you read in game.</p></footer>
<script src="${up}site.js" defer></script>
<script>if(matchMedia('(max-width:760px)').matches){document.querySelector('.side')?.removeAttribute('open');document.querySelector('.advanced-filters')?.removeAttribute('open')}</script>
</body></html>
`;
};

/** The links a mod's pages share: where its overview, guide, changelog and ideas live. */
export type ModLinks = {overview: string; guide: string | null; changelog: string | null; ideas: string | null};

export const modLinksList = (links: ModLinks, from: string): string => {
    const item = (label: string, url: string | null) => url ? `<li><a href="${esc(relUrl(from, url))}"${url === from ? ' class="cur" aria-current="page"' : ''}>${label}</a></li>` : '';
    return `<h2>This mod</h2><ul>${item('Overview', links.overview)}${item('Guide', links.guide)}${item('Changelog', links.changelog)}${item('Ideas', links.ideas)}</ul>`;
};

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
        `<a href="${esc(relUrl(current.url, p.url))}"${p === current ? ' class="cur" aria-current="page"' : ''}>${esc(p.title)}</a>`;
    const files = node.files.filter(f => path.posix.basename(f.rel) !== 'index.md').map(f => `<li>${link(f)}</li>`).join('');
    const dirs = [...node.dirs].map(([name, child]) => {
        const index = child.files.find(f => path.posix.basename(f.rel) === 'index.md');
        const inner = renderGuideNav(child, current);
        const open = inner.includes('aria-current') || index === current ? ' open' : '';
        return `<li><details${open}><summary>${index ? link(index) : esc(name)}</summary>${inner}</details></li>`;
    }).join('');
    return `<ul>${files}${dirs}</ul>`;
};

export const guidePage = (guide: Guide, page: GuidePage, links: ModLinks): string => {
    const home = guide.pages.find(p => p.rel === 'index.md')!;
    return shell({
        title: `${page.title} · ${guide.title}`,
        url: page.url,
        crumbs: [`<a href="${esc(relUrl(page.url, links.overview))}">${esc(guide.mod.name)}</a>`, `<a href="${esc(relUrl(page.url, home.url))}">Guide</a>`],
        side: `${modLinksList(links, page.url)}<h2>Guide</h2>${renderGuideNav(navTree(guide.pages), page)}`,
        body: `<article><h1>${esc(page.title)}</h1>${page.body}</article>`,
    });
};
