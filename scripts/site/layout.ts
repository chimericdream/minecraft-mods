import path from 'node:path';

import type {Guide, GuidePage} from './guide.ts';
import {esc, REPO_URL, relUrl, rootPrefix} from './util.ts';

export {THEME_CSS, SITE_CSS} from './styles.ts';

export const shell = (opts: {
    title: string;
    url: string;
    body: string;
    crumbs?: string[];
    side?: string;
    wide?: boolean;
    home?: boolean;
    head?: string;
}): string => {
    const up = rootPrefix(opts.url) || './';
    const crumbs = opts.crumbs?.length ? `<span>${opts.crumbs.map(c => `<span>›</span> ${c}`).join(' ')}</span>` : '';
    const cls = ['layout', opts.side ? '' : 'no-side', opts.wide ? 'wide' : '', opts.home ? 'home' : ''].filter(Boolean).join(' ');
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(opts.title)}</title>
<script>document.documentElement.classList.add('js');try{const t=localStorage.getItem('chimeric-theme');if(t==='light'||t==='dark')document.documentElement.dataset.theme=t}catch{}</script>
<link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Literata:wght@400;500;600&display=swap" rel="stylesheet">
${opts.head ?? ''}<link rel="stylesheet" href="${up}site.css"><script src="${up}site.js" defer></script></head>
<body data-root="${up}"><a class="skip" href="#main">Skip to content</a><header class="top"><a class="brand" href="${up}"><img src="${up}logo.svg" alt="" width="44" height="44">chimericdream</a>
<nav aria-label="Main"><a href="${up}">Mods</a><a href="${up}ideas/">Roadmap</a><a href="${up}changelog/">Changelog</a></nav>
<a class="push" href="${REPO_URL}">GitHub</a><label class="theme-control"><span>Theme</span><select id="color-theme" aria-label="Color theme"><option value="system">System</option><option value="light">Light</option><option value="dark">Dark</option></select></label></header>
${crumbs ? `<nav class="crumbs" aria-label="Breadcrumb">${crumbs}</nav>` : ''}
<div class="${cls}">${opts.side ? `<aside class="side"><button class="side-toggle" aria-expanded="false" aria-controls="side-content">Browse this mod</button><nav id="side-content" class="side-content" aria-label="Mod navigation">${opts.side}</nav></aside>` : ''}<main id="main">${opts.body}</main></div>
<footer class="site-footer"><div class="footer-inner"><p class="footer-name">chimericdream</p><div class="footer-bottom"><p>Minecraft mods, made with character.</p><nav aria-label="Footer"><a href="${up}ideas/">Roadmap</a><a href="${up}changelog/">Changelog</a><a href="${REPO_URL}">GitHub</a><a href="#main">Back to top</a></nav></div></div></footer>
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
