import {existsSync, mkdirSync, readdirSync, readFileSync, statSync, writeFileSync} from 'node:fs';
import path from 'node:path';

import {marked} from 'marked';

import type {ModInfo} from './types.ts';
import {esc, REPO_ROOT} from './util.ts';
import {DOC_CSS, DOC_JS} from './doc-assets.ts';

/** Repo-relative, forward-slash path of a markdown source file, e.g. `minekea/ideas/README.md`. */
type DocKey = string;

type Heading = {level: number; id: string; text: string};

const TOP_DOCS = ['README.md', 'CHANGELOG.md', 'TEST_PLAN.md'];

const walkMd = (dir: string, base: string, out: DocKey[]) => {
    for (const name of readdirSync(dir).sort()) {
        const full = path.join(dir, name);
        if (statSync(full).isDirectory()) {
            walkMd(full, `${base}/${name}`, out);
        } else if (name.endsWith('.md')) {
            out.push(`${base}/${name}`);
        }
    }
};

/** Every markdown doc we render for one mod: top-level docs plus everything under ideas/. */
const modDocs = (folder: string): DocKey[] => {
    const out: DocKey[] = TOP_DOCS.filter(f => existsSync(path.join(REPO_ROOT, folder, f))).map(f => `${folder}/${f}`);
    const ideas = path.join(REPO_ROOT, folder, 'ideas');
    if (existsSync(ideas)) {
        walkMd(ideas, `${folder}/ideas`, out);
    }
    return out;
};

export const docPageHref = (key: DocKey, from: 'root' | DocKey = 'root'): string => {
    const target = `docs/${key}.html`;
    if (from === 'root') {
        return target;
    }
    const depth = `docs/${from}`.split('/').length - 1;
    return '../'.repeat(depth) + target;
};

const slugger = () => {
    const seen = new Map<string, number>();
    return (text: string) => {
        const base = text.toLowerCase().replace(/<[^>]+>/g, '').replace(/[^\p{L}\p{N}\s-]/gu, '').trim().replace(/\s+/g, '-') || 'section';
        const n = seen.get(base) ?? 0;
        seen.set(base, n + 1);
        return n === 0 ? base : `${base}-${n}`;
    };
};

type Ctx = {key: DocKey; docs: Set<DocKey>; headings: Heading[]; slug: (t: string) => string};
let ctx!: Ctx;

const isExternal = (href: string) => /^([a-z][a-z0-9+.-]*:|\/\/)/i.test(href);

/** Where a relative link from the current doc should point: another rendered page, or the source file. */
const resolveHref = (href: string): string => {
    if (!href || href.startsWith('#') || isExternal(href)) {
        return href;
    }
    const [rawPath, hash] = href.split('#') as [string, string | undefined];
    const decoded = decodeURIComponent(rawPath);
    let target = path.posix.normalize(path.posix.join(path.posix.dirname(ctx.key), decoded));
    if (decoded.endsWith('/') || !path.posix.extname(target)) {
        const readme = `${target.replace(/\/$/, '')}/README.md`;
        if (ctx.docs.has(readme)) {
            target = readme;
        }
    }
    const suffix = hash ? `#${hash}` : '';
    if (ctx.docs.has(target)) {
        return docPageHref(target, ctx.key) + suffix;
    }
    // Not a rendered doc: link to the file in the working tree (dist lives at <repo>/dashboard/dist).
    const depth = `docs/${ctx.key}`.split('/').length - 1;
    return '../'.repeat(depth) + '../../' + target + suffix;
};

marked.use({
    gfm: true,
    renderer: {
        heading({tokens, depth}) {
            const html = this.parser.parseInline(tokens);
            const text = html.replace(/<[^>]+>/g, '');
            const id = ctx.slug(text);
            ctx.headings.push({level: depth, id, text});
            return `<h${depth} id="${esc(id)}"><a class="anchor" href="#${esc(id)}" aria-label="Link to this section">#</a>${html}</h${depth}>\n`;
        },
        link({href, title, tokens}) {
            const out = resolveHref(href);
            const attrs = isExternal(href) ? ' target="_blank" rel="noopener"' : '';
            return `<a href="${esc(out)}"${title ? ` title="${esc(title)}"` : ''}${attrs}>${this.parser.parseInline(tokens)}</a>`;
        },
        image({href, title, text}) {
            return `<img src="${esc(resolveHref(href))}" alt="${esc(text)}"${title ? ` title="${esc(title)}"` : ''}>`;
        },
    },
});

type NavNode = {dirs: Map<string, NavNode>; files: DocKey[]};

const buildNav = (folder: string, keys: DocKey[]): NavNode => {
    const root: NavNode = {dirs: new Map(), files: []};
    for (const key of keys) {
        const rel = key.slice(folder.length + 1).split('/');
        let node = root;
        for (const dir of rel.slice(0, -1)) {
            if (!node.dirs.has(dir)) node.dirs.set(dir, {dirs: new Map(), files: []});
            node = node.dirs.get(dir)!;
        }
        node.files.push(key);
    }
    return root;
};

const renderNav = (node: NavNode, current: DocKey): string => {
    const files = node.files.map(k => {
        const label = path.posix.basename(k, '.md');
        return `<li><a href="${docPageHref(k, current)}"${k === current ? ' aria-current="page" class="cur"' : ''}>${esc(label)}</a></li>`;
    }).join('');
    const dirs = [...node.dirs].map(([name, child]) => {
        const html = renderNav(child, current);
        const open = html.includes('aria-current="page"') ? ' open' : '';
        return `<li><details${open}><summary>${esc(name)}</summary>${html}</details></li>`;
    }).join('');
    return `<ul>${files}${dirs}</ul>`;
};

const page = (opts: {key: DocKey; mod: ModInfo; title: string; body: string; headings: Heading[]; nav: string}): string => {
    const up = '../'.repeat(`docs/${opts.key}`.split('/').length - 1);
    const rel = opts.key.slice(opts.mod.folder.length + 1);
    const toc = opts.headings.filter(h => h.level >= 2 && h.level <= 4)
        .map(h => `<li class="l${h.level}"><a href="#${esc(h.id)}">${esc(h.text)}</a></li>`).join('');
    const kind = path.posix.basename(opts.key) === 'CHANGELOG.md' ? 'changelog' : 'doc';
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(opts.title)} — ${esc(opts.mod.name)}</title><link rel="stylesheet" href="${up}doc.css"></head>
<body data-kind="${kind}"><header class="top">
<nav class="crumbs"><a href="${up}index.html">Dashboard</a> › <a href="${up}index.html#mod-${esc(opts.mod.folder)}">${esc(opts.mod.name)}</a> › <span>${esc(rel)}</span></nav>
<div class="tools"><input type="search" id="find" placeholder="Search this page ( / )" aria-label="Search this page">
<span id="findcount" class="muted"></span><button type="button" id="collapse">Collapse all</button><button type="button" id="expand">Expand all</button></div>
</header>
<div class="layout"><aside class="side"><details class="files" open><summary>${esc(opts.mod.name)} docs</summary>${opts.nav}</details>
${toc ? `<h2>On this page</h2><ul class="toc">${toc}</ul>` : ''}</aside>
<main class="content"><article>${opts.body}</article></main></div>
<script src="${up}doc.js"></script></body></html>
`;
};

export const buildDocs = (mods: ModInfo[], outDir: string): Set<DocKey> => {
    const byMod = new Map(mods.map(m => [m.folder, modDocs(m.folder)] as const));
    const all = new Set<DocKey>([...byMod.values()].flat());

    writeFileSync(path.join(outDir, 'doc.css'), DOC_CSS);
    writeFileSync(path.join(outDir, 'doc.js'), DOC_JS);

    for (const mod of mods) {
        const keys = byMod.get(mod.folder)!;
        const navRoot = buildNav(mod.folder, keys);
        for (const key of keys) {
            ctx = {key, docs: all, headings: [], slug: slugger()};
            const source = readFileSync(path.join(REPO_ROOT, key), 'utf8');
            const body = marked.parse(source, {async: false}) as string;
            const title = ctx.headings.find(h => h.level === 1)?.text ?? path.posix.basename(key, '.md');
            const dest = path.join(outDir, docPageHref(key));
            mkdirSync(path.dirname(dest), {recursive: true});
            writeFileSync(dest, page({key, mod, title, body, headings: ctx.headings, nav: renderNav(navRoot, key)}));
        }
    }
    return all;
};
