import {copyFileSync, existsSync, mkdirSync, readdirSync, readFileSync, statSync} from 'node:fs';
import path from 'node:path';

import {Marked} from 'marked';

import {modLinksList, reading, shell, type ModLinks} from './layout.ts';
import type {ModInfo} from './types.ts';
import {esc, REPO_ROOT, REPO_URL, relUrl, slugger} from './util.ts';

/** Repo-relative, forward-slash path of a markdown source file, e.g. `minekea/ideas/README.md`. */
export type DocKey = string;

export type Doc = {key: DocKey; mod: ModInfo; url: string};

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

/** `minekea/ideas/decor/x.md` -> `mods/minekea/ideas/decor/x/`; READMEs take their folder's URL. */
export const docUrl = (folder: string, key: DocKey): string => {
    const rel = key.slice(folder.length + 1).replace(/\.md$/, '');
    const name = rel === 'CHANGELOG' ? 'changelog' : rel.replace(/(^|\/)README$/, '');
    return `mods/${folder}/${name ? name + '/' : ''}`;
};

/** The README, CHANGELOG and everything under ideas/ for each mod. TEST_PLAN and design notes stay out. */
export const collectDocs = (mods: ModInfo[]): Map<DocKey, Doc> => {
    const docs = new Map<DocKey, Doc>();
    for (const mod of mods) {
        const keys: DocKey[] = ['README.md', 'CHANGELOG.md']
            .filter(f => existsSync(path.join(REPO_ROOT, mod.folder, f))).map(f => `${mod.folder}/${f}`);
        const ideas = path.join(REPO_ROOT, mod.folder, 'ideas');
        if (existsSync(ideas)) {
            walkMd(ideas, `${mod.folder}/ideas`, keys);
        }
        for (const key of keys) {
            docs.set(key, {key, mod, url: docUrl(mod.folder, key)});
        }
    }
    return docs;
};

const isExternal = (href: string) => /^([a-z][a-z0-9+.-]*:|\/\/)/i.test(href);

export type RenderOpts = {
    key: DocKey;
    /** URL of the page the HTML will be placed on, for relative links. */
    fromUrl: string;
    docs: Map<DocKey, Doc>;
    outDir: string;
    warn: (msg: string) => void;
};

/** Render one repo markdown file. Links to other rendered docs become site links; other files link to GitHub. */
export const renderDoc = (source: string, opts: RenderOpts): string => {
    const {key, fromUrl, docs, outDir, warn} = opts;
    const slug = slugger();
    const fromDir = path.posix.dirname(key);

    const resolve = (rawPath: string): string => path.posix.normalize(path.posix.join(fromDir, decodeURIComponent(rawPath)));

    const marked = new Marked({gfm: true});
    marked.use({
        renderer: {
            heading({tokens, depth}) {
                const html = this.parser.parseInline(tokens);
                const id = slug(html.replace(/<[^>]+>/g, ''));
                return `<h${depth} id="${esc(id)}">${html}</h${depth}>\n`;
            },
            link({href, title, tokens}) {
                const inner = this.parser.parseInline(tokens);
                const t = title ? ` title="${esc(title)}"` : '';
                if (href.startsWith('#')) return `<a href="${esc(href)}"${t}>${inner}</a>`;
                if (isExternal(href)) return `<a href="${esc(href)}"${t} target="_blank" rel="noopener">${inner}</a>`;

                const [file, hash] = href.split('#') as [string, string | undefined];
                const suffix = hash ? `#${hash}` : '';
                let target = resolve(file);
                if (!docs.has(target) && docs.has(`${target.replace(/\/$/, '')}/README.md`)) {
                    target = `${target.replace(/\/$/, '')}/README.md`;
                }
                const doc = docs.get(target);
                if (doc) return `<a href="${esc(relUrl(fromUrl, doc.url) + suffix)}"${t}>${inner}</a>`;

                const abs = path.join(REPO_ROOT, target);
                if (target.startsWith('..') || !existsSync(abs)) {
                    warn(`${key}: unresolved link ${href}`);
                    return inner;
                }
                const kind = statSync(abs).isDirectory() ? 'tree' : 'blob';
                return `<a href="${REPO_URL}/${kind}/main/${esc(target)}${suffix}"${t} target="_blank" rel="noopener">${inner}</a>`;
            },
            image({href, title, text}) {
                const t = title ? ` title="${esc(title)}"` : '';
                if (isExternal(href)) return `<img src="${esc(href)}" alt="${esc(text)}"${t}>`;
                const target = resolve(href.split('#')[0]!);
                const abs = path.join(REPO_ROOT, target);
                if (target.startsWith('..') || !existsSync(abs)) {
                    warn(`${key}: missing image ${href}`);
                    return `<span class="missing-img">${esc(text)}</span>`;
                }
                const dest = `_assets/${target}`;
                mkdirSync(path.dirname(path.join(outDir, dest)), {recursive: true});
                copyFileSync(abs, path.join(outDir, dest));
                return `<img src="${esc(path.posix.relative(fromUrl, dest))}" alt="${esc(text)}"${t} loading="lazy">`;
            },
        },
    });
    return marked.parse(source, {async: false}) as string;
};

const firstHeading = (source: string): string | undefined => source.match(/^# (.+)$/m)?.[1]?.replace(/[*_`]/g, '').trim();

type NavNode = {dirs: Map<string, NavNode>; files: Doc[]};

const ideasNav = (docs: Doc[], folder: string): NavNode => {
    const root: NavNode = {dirs: new Map(), files: []};
    const prefix = `${folder}/ideas/`;
    for (const doc of docs) {
        const parts = doc.key.slice(prefix.length).split('/');
        let node = root;
        for (const dir of parts.slice(0, -1)) {
            if (!node.dirs.has(dir)) node.dirs.set(dir, {dirs: new Map(), files: []});
            node = node.dirs.get(dir)!;
        }
        node.files.push(doc);
    }
    return root;
};

const renderIdeasNav = (node: NavNode, current: Doc): string => {
    const label = (d: Doc) => path.posix.basename(d.key) === 'README.md' ? 'Overview' : path.posix.basename(d.key, '.md').replace(/-/g, ' ');
    const link = (d: Doc) => `<a href="${esc(relUrl(current.url, d.url))}"${d === current ? ' class="cur" aria-current="page"' : ''}>${esc(label(d))}</a>`;
    const files = node.files.map(d => `<li>${link(d)}</li>`).join('');
    const dirs = [...node.dirs].map(([name, child]) => {
        const inner = renderIdeasNav(child, current);
        return `<li><details${inner.includes('aria-current') ? ' open' : ''}><summary>${esc(name)}</summary>${inner}</details></li>`;
    }).join('');
    return `<ul>${files}${dirs}</ul>`;
};

export const docPage = (doc: Doc, body: string, title: string, links: ModLinks, modDocs: Doc[]): string => {
    const isIdeas = doc.key.startsWith(`${doc.mod.folder}/ideas/`);
    const ideaDocs = modDocs.filter(d => d.key.startsWith(`${doc.mod.folder}/ideas/`));
    const side = modLinksList(links, doc.url) + (isIdeas ? `<h2>Ideas</h2>${renderIdeasNav(ideasNav(ideaDocs, doc.mod.folder), doc)}` : '');
    return shell({
        title: `${title} · ${doc.mod.name}`,
        url: doc.url,
        crumbs: [`<a href="${esc(relUrl(doc.url, links.overview))}">${esc(doc.mod.name)}</a>`],
        side,
        body: `${reading(`<article class="prose">${body}</article>`)}<p class="edit-link"><a href="${REPO_URL}/blob/main/${esc(doc.key)}">View source on GitHub</a></p>`,
    });
};

export const readDoc = (key: DocKey): {source: string; title: string} => {
    const source = readFileSync(path.join(REPO_ROOT, key), 'utf8').replace(/\r\n/g, '\n');
    return {source, title: firstHeading(source) ?? path.posix.basename(key, '.md')};
};
