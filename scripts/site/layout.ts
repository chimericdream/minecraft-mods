import {readFileSync} from 'node:fs';
import path from 'node:path';

import type {Guide, GuidePage} from './guide.ts';
import {esc, REPO_URL, relUrl, rootPrefix} from './util.ts';

/** Color tokens shared by every page, including the local-only GitHub panel. */
const LIGHT = `--bg:#f2f0eb;--fg:#241d2b;--muted:#63596b;--card:#e8e3e9;--surface:#e8e3e9;--line:#c9bfce;--accent:#8b1a32;--accent-ink:#fff7f5;--good:#24613b;--bad:#a02132;--warn:#795112;--mark:#e9d9a9;--field:#241333;--field-ink:#f2edef;--field-muted:#ccc1d3;--tile:#d9d2dd;--focus:#8b1a32;`;
const DARK = `--bg:#160e20;--fg:#efebf0;--muted:#bcb0c6;--card:#24182f;--surface:#24182f;--line:#4b3b57;--accent:#f2a3ad;--accent-ink:#28101b;--good:#9acfa8;--bad:#ffa6b2;--warn:#e5c28e;--mark:#65502a;--field:#29163a;--field-ink:#f2edef;--field-muted:#ccc1d3;--tile:#352443;--focus:#f2a3ad;`;
export const THEME_CSS = `
:root{color-scheme:light;${LIGHT}}
@media(prefers-color-scheme:dark){:root:not([data-theme=light]){color-scheme:dark;${DARK}}}
:root[data-theme=dark]{color-scheme:dark;${DARK}}
`;

export const SITE_CSS = THEME_CSS + readFileSync(path.join(import.meta.dir, 'styles.css'), 'utf8');

export const shell = (opts: {
    title: string;
    url: string;
    body: string;
    crumbs?: string[];
    side?: string;
    wide?: boolean;
    head?: string;
}): string => {
    const up = rootPrefix(opts.url) || './';
    const crumbs = opts.crumbs?.length ? `<div class="crumbs"><a href="${up}">Mods</a>${opts.crumbs.map(c => `<span aria-hidden="true">/</span>${c}`).join('')}</div>` : '';
    const cls = ['layout', opts.side ? '' : 'no-side', opts.wide ? 'wide' : '', !opts.url ? 'home-layout' : ''].filter(Boolean).join(' ');
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<script>try{const t=localStorage.getItem('chimeric-theme');if(t==='light'||t==='dark')document.documentElement.dataset.theme=t}catch{}</script>
<title>${esc(opts.title)}</title><link rel="stylesheet" href="${up}site.css">
<link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Libre+Franklin:wght@400;500;600;700&family=Source+Serif+4:ital,wght@0,400;0,500;0,600;1,400&display=swap" rel="stylesheet">
<script src="${up}site.js" defer></script>${opts.head ?? ''}</head>
<body data-root="${up}"><a class="skip-link" href="#main">Skip to content</a>
<header class="top"><a class="brand" href="${up}"><img src="${up}logo.svg" width="48" height="48" alt=""><span>chimericdream</span></a>
<nav aria-label="Main"><a href="${up}"${!opts.url ? ' aria-current="page"' : ''}>Mods</a><a href="${up}ideas/"${opts.url === 'ideas/' ? ' aria-current="page"' : ''}>Roadmap</a><a href="${up}changelog/"${opts.url === 'changelog/' ? ' aria-current="page"' : ''}>Changelog</a><a href="${REPO_URL}">GitHub</a></nav>
<label class="theme-control"><span class="sr-only">Color theme</span><select aria-label="Color theme"><option value="system">System</option><option value="light">Light</option><option value="dark">Dark</option></select></label></header>
${crumbs}<div class="${cls}">${opts.side ? `<aside class="side"><details class="side-tree" open><summary>Page navigation</summary><nav aria-label="Mod navigation">${opts.side}</nav></details></aside>` : ''}<main id="main" tabindex="-1">${opts.body}</main></div>
<footer class="site-footer"><a class="footer-name" href="${up}">chimericdream</a><div><p>Made for your next world.</p><nav aria-label="Footer"><a href="${up}ideas/">Roadmap</a><a href="${up}changelog/">Changelog</a><a href="${REPO_URL}">Source on GitHub</a></nav></div></footer>
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
