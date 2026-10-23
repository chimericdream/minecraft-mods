import path from 'node:path';

import type {Guide, GuidePage} from './guide.ts';
import {esc, REPO_URL, relUrl, rootPrefix} from './util.ts';

/** Color tokens shared by every page, including the local-only GitHub panel. */
export const THEME_CSS = `
:root{--bg:#f6f7f9;--fg:#1d2127;--muted:#667;--card:#fff;--line:#dde1e6;--accent:#2f6fed;--good:#1a7f37;--bad:#c62828;--warn:#9a6700;--mark:#fff3a3}
@media (prefers-color-scheme:dark){:root{--bg:#14171b;--fg:#e4e7eb;--muted:#9aa3ad;--card:#1d2127;--line:#2f353d;--accent:#7aa5ff;--good:#56d364;--bad:#ff7b72;--warn:#e3b341;--mark:#5c4b00}}
`;

export const SITE_CSS = `
${THEME_CSS}
*{box-sizing:border-box}
body{margin:0;background:var(--bg);color:var(--fg);font:16px/1.6 system-ui,sans-serif}
a{color:var(--accent);text-decoration:none}a:hover{text-decoration:underline}
code{font-size:.9em}
.sr-only{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap}
.muted{color:var(--muted)}
.top{display:flex;gap:20px;align-items:center;flex-wrap:wrap;padding:10px 16px;border-bottom:1px solid var(--line);background:var(--card)}
.top .brand{font-weight:700;color:var(--fg);margin-right:8px}
.top nav{display:flex;gap:16px;font-size:15px}
.top .crumbs{color:var(--muted);font-size:14px;display:flex;gap:8px}
.top .push{margin-left:auto}
.layout{display:grid;grid-template-columns:260px minmax(0,1fr);max-width:1200px;margin:0 auto}
.layout.no-side{grid-template-columns:minmax(0,1fr)}
.layout.wide{max-width:1600px}
.side{padding:16px;border-right:1px solid var(--line);font-size:14px}
.side h2{font-size:12px;text-transform:uppercase;letter-spacing:.05em;color:var(--muted);margin:16px 0 4px}
.side h2:first-child{margin-top:0}
.side ul{list-style:none;margin:0;padding-left:14px}.side>ul{padding-left:0}
.side li{margin:2px 0}.side summary{cursor:pointer}.side .cur{font-weight:700;color:var(--fg)}
main{padding:16px 24px 48px;min-width:0}
article h1{margin-top:8px}
article img{max-width:100%;height:auto;border-radius:4px}
article table{border-collapse:collapse;display:block;overflow-x:auto}
article th,article td{border:1px solid var(--line);padding:4px 10px;text-align:left;vertical-align:top}
article pre{overflow-x:auto;background:var(--card);border:1px solid var(--line);border-radius:6px;padding:10px 12px}
article :not(pre)>code{background:var(--card);border:1px solid var(--line);border-radius:4px;padding:0 4px}
article blockquote{margin:12px 0;padding:0 16px;border-left:4px solid var(--line);color:var(--muted)}
.edit-link{margin-top:32px;font-size:13px;color:var(--muted)}
.item{font-weight:600;border-bottom:1px dotted var(--muted)}
.featured{font-size:1.2em;margin:8px 0}
.missing-img{color:var(--muted);font-style:italic}
.callout{border-left:4px solid var(--accent);background:var(--card);padding:2px 16px;margin:16px 0;border-radius:0 6px 6px 0}
.callout-title{font-weight:700;margin:8px 0 0}
.callout-warning,.callout-important{border-color:var(--warn)}.callout-danger{border-color:var(--bad)}.callout-tip{border-color:var(--good)}
.recipe{margin:16px 0;padding:8px 16px;border:1px solid var(--line);border-radius:8px;background:var(--card);width:fit-content;max-width:100%}
.recipe figcaption{font-size:13px;color:var(--muted)}
.recipe-diagram{margin:8px 0;padding:0;border:0;background:none;font:14px/1.15 ui-monospace,Consolas,monospace;overflow-x:auto}
.recipe-legend{list-style:none;margin:0;padding:0;font-size:13px;columns:2}
.recipe-legend b{display:inline-block;width:1.5em;font-family:ui-monospace,Consolas,monospace}
.error{color:var(--bad)}
.badge{display:inline-block;font-size:11px;padding:0 7px;border-radius:9px;border:1px solid var(--line);margin-left:4px;vertical-align:middle}
.badge.beta{color:var(--warn)}.badge.unrel{color:var(--accent)}.badge.new{color:var(--good)}
.cards{display:grid;grid-template-columns:repeat(auto-fill,minmax(280px,1fr));gap:12px;padding:0;list-style:none}
.card{border:1px solid var(--line);border-radius:8px;padding:12px;background:var(--card);display:flex;flex-direction:column;gap:6px}
.card header{display:flex;gap:10px;align-items:center}
.card h2{margin:0;font-size:17px}.card p{margin:0;font-size:14px;color:var(--muted)}
.card .ver{font-size:13px;color:var(--muted)}
.card nav{margin-top:auto;display:flex;gap:12px;font-size:14px}
.icon{width:48px;height:48px;border-radius:8px;image-rendering:pixelated;flex:none}
.ph{display:grid;place-items:center;font-weight:700;color:#fff;background:hsl(var(--h) 45% 40%);image-rendering:auto}
.modhead{display:flex;gap:16px;align-items:center;margin-bottom:8px}
.modhead h1{margin:0}.modhead p{margin:2px 0 0;color:var(--muted)}
.actions{display:flex;gap:8px;flex-wrap:wrap;margin:12px 0 20px}
.actions a{border:1px solid var(--line);border-radius:6px;padding:3px 12px;background:var(--card);font-size:14px}
.feed-section{margin:0 0 28px}
.feed-section h2{margin-bottom:0}
@media (max-width:760px){.layout{grid-template-columns:1fr}.side{border-right:0;border-bottom:1px solid var(--line)}}
`;

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
    const crumbs = opts.crumbs?.length ? `<span class="crumbs">${opts.crumbs.map(c => `<span>›</span>${c}`).join('')}</span>` : '';
    const cls = ['layout', opts.side ? '' : 'no-side', opts.wide ? 'wide' : ''].filter(Boolean).join(' ');
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(opts.title)}</title><link rel="stylesheet" href="${up}site.css">${opts.head ?? ''}</head>
<body data-root="${up}"><header class="top"><a class="brand" href="${up}">Chimeric Mods</a>
<nav><a href="${up}">Mods</a><a href="${up}ideas/">Roadmap</a><a href="${up}changelog/">Changelog</a></nav>${crumbs}
<a class="push" href="${REPO_URL}">GitHub</a></header>
<div class="${cls}">${opts.side ? `<aside class="side">${opts.side}</aside>` : ''}<main>${opts.body}</main></div>
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
