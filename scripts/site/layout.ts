import path from 'node:path';

import type {Guide, GuidePage} from './guide.ts';
import {esc, REPO_URL, relUrl, rootPrefix} from './util.ts';

export {SITE_CSS, THEME_CSS} from './styles.ts';
import {SITE_JS, THEME_INIT} from './client.ts';

const readingLayout = (body: string): string => body.replace(/<article>([\s\S]*?)<\/article>/, (_, content: string) => {
    const headings: {level: string; id: string; label: string}[] = [];
    const article = content.replace(/<h([23]) id="([^"]+)">([\s\S]*?)<\/h[23]>/g, (_heading: string, level: string, id: string, label: string) => {
        const text = label.replace(/<[^>]+>/g, '');
        headings.push({level, id, label: text});
        return '<h' + level + ' id="' + id + '">' + label + '<a class="permalink" href="#' + id + '" aria-label="Link to section: ' + text.replace(/"/g, '&quot;') + '">#</a></h' + level + '>';
    });
    if (headings.length < 2) return '<article>' + article + '</article>';
    const items = headings.map(heading => '<li' + (heading.level === '3' ? ' class="sub"' : '') + '><a href="#' + heading.id + '">' + heading.label + '</a></li>').join('');
    return '<div class="reader"><details class="toc"><summary>On this page</summary><nav aria-label="On this page"><ol>' + items + '</ol></nav></details><article>' + article + '</article></div>';
});

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
    const crumbs = opts.crumbs?.length ? '<nav class="crumbs" aria-label="Breadcrumb"><a href="' + up + '">Mods</a>' + opts.crumbs.map(crumb => '<span aria-hidden="true">/</span>' + crumb).join('') + '</nav>' : '';
    const cls = ['layout', opts.side ? '' : 'no-side', opts.wide ? 'wide' : ''].filter(Boolean).join(' ');
    const current = (section: string) => (section === '' ? opts.url === '' || opts.url.startsWith('mods/') : opts.url.startsWith(section)) ? ' aria-current="' + (opts.url === section ? 'page' : 'true') + '"' : '';
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(opts.title)}</title><script>${THEME_INIT}</script><link rel="stylesheet" href="${up}site.css">${opts.head ?? ''}</head>
<body data-root="${up}"><a class="skip" href="#main">Skip to content</a>
<header class="top"><div class="top-inner"><a class="brand" href="${up}"><span class="brand-mark" aria-hidden="true">c</span>Chimeric Mods</a>
<nav aria-label="Global"><a href="${up}"${current('')}>Explore mods</a><a href="${up}ideas/"${current('ideas/')}>Roadmap</a><a href="${up}changelog/"${current('changelog/')}>Changelog</a></nav>
<div class="top-tools"><a href="${REPO_URL}">GitHub &nearr;</a><label class="sr-only" for="theme-choice">Color theme</label><select class="theme-control" id="theme-choice" hidden><option value="system">System</option><option value="light">Light</option><option value="dark">Dark</option></select></div></div></header>
<div class="${cls}">${opts.side ? `<aside class="side"><details class="section-nav"><summary>Browse this mod</summary><nav aria-label="Mod documentation">${opts.side}</nav></details></aside>` : ''}<main id="main" tabindex="-1">${crumbs}${readingLayout(opts.body)}</main></div>
<footer class="footer"><span>Chimeric Mods &middot; A collection by chimericdream</span><a href="${up}">Back to the mod collection &uarr;</a></footer><script>${SITE_JS}</script>
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
    const index = guide.pages.indexOf(page);
    const pageLink = (target: GuidePage | undefined, label: string) => target ? `<a href="${esc(relUrl(page.url, target.url))}"><span>${label}</span>${esc(target.title)}</a>` : '';
    const pager = `<nav class="pager" aria-label="Guide pages">${pageLink(guide.pages[index - 1], 'Previous')}${pageLink(guide.pages[index + 1], 'Next')}</nav>`;
    return shell({
        title: `${page.title} · ${guide.title}`,
        url: page.url,
        crumbs: [`<a href="${esc(relUrl(page.url, links.overview))}">${esc(guide.mod.name)}</a>`, `<a href="${esc(relUrl(page.url, home.url))}">Guide</a>`],
        side: `${modLinksList(links, page.url)}<h2>Guide</h2>${renderGuideNav(navTree(guide.pages), page)}`,
        body: `<div class="eyebrow">${esc(guide.title)} / Guide</div><article><h1>${esc(page.title)}</h1>${page.body}</article>${pager}
<p class="edit-link"><a href="${REPO_URL}/blob/main/${esc(guide.mod.folder)}/common/src/main/resources/assets/${esc(guide.mod.id)}/opus-books/guide/en_us/${esc(page.rel)}">View guide source on GitHub</a></p>`,
    });
};
