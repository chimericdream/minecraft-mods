import {copyFileSync, existsSync, mkdirSync, readdirSync, readFileSync, statSync, writeFileSync} from 'node:fs';
import path from 'node:path';

import {Marked} from 'marked';

import {esc, relUrl, REPO_ROOT, slugger} from './util.ts';
import {asciiFormatter} from './recipes/ascii.ts';
import {displayName} from './recipes/names.ts';
import {parseRecipe} from './recipes/parse.ts';
import type {RecipeFormatter} from './recipes/types.ts';

/** In-game guides ("Opus books") live at <mod>/common/src/main/resources/assets/<id>/opus-books/guide/<lang>/. */
const LANG = 'en_us';

export type ModRef = {folder: string; id: string; name: string};

export type GuidePage = {
    /** Path inside the guide, e.g. `01-pumpkins/02-stencils.md`. */
    rel: string;
    title: string;
    /** Directory-style URL relative to the site root, with trailing slash. */
    url: string;
    body: string;
};

export type Guide = {mod: ModRef; title: string; description: string; pages: GuidePage[]};

const FRONTMATTER = /^---\r?\n([\s\S]*?)\r?\n---\r?\n?/;

const splitFrontmatter = (source: string): {meta: Record<string, unknown>; body: string} => {
    const m = source.match(FRONTMATTER);
    return m ? {meta: (Bun.YAML.parse(m[1]!) ?? {}) as Record<string, unknown>, body: source.slice(m[0].length)} : {meta: {}, body: source};
};

const walk = (dir: string, base = ''): string[] =>
    readdirSync(dir).sort().flatMap(name => {
        const rel = base ? `${base}/${name}` : name;
        return statSync(path.join(dir, name)).isDirectory() ? walk(path.join(dir, name), rel) : name.endsWith('.md') ? [rel] : [];
    });

/** `index.md` -> the folder's own URL; `foo.md` -> `foo/`. */
export const pageUrl = (modFolder: string, rel: string): string => {
    const noExt = rel.replace(/\.md$/, '').replace(/(^|\/)index$/, '');
    return `mods/${modFolder}/guide/${noExt ? noExt + '/' : ''}`;
};

export const findGuideDir = (folder: string): {dir: string; assets: string; id: string} | null => {
    const assetsRoot = path.join(REPO_ROOT, folder, 'common', 'src', 'main', 'resources', 'assets');
    if (!existsSync(assetsRoot)) return null;
    for (const id of readdirSync(assetsRoot)) {
        const dir = path.join(assetsRoot, id, 'opus-books', 'guide', LANG);
        if (existsSync(path.join(dir, 'index.md'))) return {dir, assets: assetsRoot, id};
    }
    return null;
};

const CALLOUTS: Record<string, string> = {
    NOTE: 'Note', TIP: 'Tip', IMPORTANT: 'Important', WARNING: 'Warning', DANGER: 'Danger', KIND: 'Kind',
};

type RenderCtx = {
    mod: ModRef;
    pageUrl: string;
    pageRel: string;
    pages: Map<string, string>;
    /** Namespace -> mod folder, so `texture:opus:...` finds the right resources. */
    folderOf: Map<string, string>;
    outDir: string;
    formatRecipe: RecipeFormatter;
    warn: (msg: string) => void;
};

const isExternal = (href: string) => /^(https?:|mailto:|\/\/)/i.test(href);

const renderMarkdown = (source: string, ctx: RenderCtx): string => {
    const where = `${ctx.mod.folder} guide/${ctx.pageRel}`;

    const copyTexture = (ns: string, texPath: string): string | null => {
        const folder = ctx.folderOf.get(ns);
        const file = folder && path.join(REPO_ROOT, folder, 'common', 'src', 'main', 'resources', 'assets', ns, 'textures', `${texPath}.png`);
        if (!file || !existsSync(file)) return null;
        const siteRel = `mods/${ctx.mod.folder}/guide/_img/${ns}/${texPath}.png`;
        const dest = path.join(ctx.outDir, siteRel);
        mkdirSync(path.dirname(dest), {recursive: true});
        copyFileSync(file, dest);
        return path.posix.relative(ctx.pageUrl, siteRel);
    };

    const slug = slugger();
    const marked = new Marked({gfm: true});
    marked.use({
        renderer: {
            code({text, lang}) {
                if (lang === 'recipe') {
                    try {
                        const raw = Bun.YAML.parse(text) as Record<string, unknown>;
                        // Id-only recipes are looked up from the server in game; there's nothing to draw here.
                        if (raw.recipe && !raw.result) {
                            return `<p class="recipe-ref">Recipe: <code>${esc(String(raw.recipe))}</code> (shown in game)</p>\n`;
                        }
                        return ctx.formatRecipe(parseRecipe(raw));
                    } catch (e) {
                        ctx.warn(`${where}: ${(e as Error).message}`);
                        return `<pre class="error">${esc(text)}</pre>`;
                    }
                }
                if (lang === 'item' || lang === 'entity') {
                    const parsed = Bun.YAML.parse(text);
                    // A bare scalar (`minecraft:campfire`) is shorthand for `id:`.
                    const props = (typeof parsed === 'string' ? {id: parsed} : parsed ?? {}) as {id?: string; label?: string; count?: number};
                    const name = props.label ?? (props.id ? displayName(props.id) : undefined);
                    if (!name) {
                        ctx.warn(`${where}: ${lang} block without id or label`);
                        return '';
                    }
                    const count = props.count && props.count > 1 ? ` ×${props.count}` : '';
                    return `<p class="featured featured-${lang}"><span class="item">${esc(name)}</span>${count}</p>\n`;
                }
                return `<pre><code>${esc(text)}</code></pre>\n`;
            },
            blockquote({tokens}) {
                const html = this.parser.parse(tokens);
                const m = html.match(/^<p>\[!(\w+)\](?:[ \t]+([^\n<]*))?\n?/);
                if (!m) return `<blockquote>${html}</blockquote>\n`;
                const kind = m[1]!.toUpperCase();
                const title = m[2]?.trim() || CALLOUTS[kind] || kind;
                const tail = html.slice(m[0].length);
                const rest = tail.startsWith('</p>') ? tail.slice(4) : `<p>${tail}`;
                return `<aside class="callout callout-${kind.toLowerCase()}"><p class="callout-title">${esc(title)}</p>${rest}</aside>\n`;
            },
            heading({tokens, depth}) {
                const html = this.parser.parseInline(tokens);
                const id = slug(html.replace(/<[^>]+>/g, ''));
                return `<h${depth} id="${esc(id)}">${html}</h${depth}>
`;
            },
            link({href, title, tokens}) {
                const inner = this.parser.parseInline(tokens);
                if (href.startsWith('#')) return `<a href="${esc(href)}">${inner}</a>`;
                const scheme = href.match(/^([a-z]+):/)?.[1];
                if (scheme && ['item', 'block', 'entity', 'tag'].includes(scheme)) {
                    return `<span class="item" data-ref="${esc(href)}">${inner}</span>`;
                }
                if (isExternal(href)) {
                    return `<a href="${esc(href)}"${title ? ` title="${esc(title)}"` : ''} target="_blank" rel="noopener">${inner}</a>`;
                }
                if (scheme === 'book') {
                    ctx.warn(`${where}: cross-book link ${href} rendered as text`);
                    return inner;
                }
                const [file, hash] = href.split('#') as [string, string | undefined];
                const target = path.posix.normalize(path.posix.join(path.posix.dirname(ctx.pageRel), decodeURIComponent(file)));
                const url = ctx.pages.get(target);
                if (!url) {
                    ctx.warn(`${where}: unresolved link ${href}`);
                    return inner;
                }
                return `<a href="${esc(relUrl(ctx.pageUrl, url) + (hash ? `#${hash}` : ''))}">${inner}</a>`;
            },
            image({href, title, text}) {
                const [rawHref, fragment] = href.split('#') as [string, string | undefined];
                const width = fragment?.match(/^width=(\d+)$/)?.[1];
                const scheme = rawHref.match(/^([a-z]+):/)?.[1];
                if (scheme === 'item' || scheme === 'block' || scheme === 'entity') {
                    return `<span class="item" data-ref="${esc(rawHref)}">${esc(text)}</span>`;
                }
                if (scheme === 'texture') {
                    const [, ns, ...rest] = rawHref.split(':');
                    const src = copyTexture(ns!, rest.join(':'));
                    if (src) {
                        return `<img src="${esc(src)}" alt="${esc(text)}"${title ? ` title="${esc(title)}"` : ''}${width ? ` width="${width}"` : ''} loading="lazy">`;
                    }
                    ctx.warn(`${where}: texture not found ${rawHref}`);
                    return `<span class="missing-img">${esc(text)}</span>`;
                }
                ctx.warn(`${where}: unsupported image ${rawHref}`);
                return `<span class="missing-img">${esc(text)}</span>`;
            },
        },
    });
    // `![alt](texture:...){width=400}` -> carry the width in the URL fragment so the image renderer sees it.
    const prepared = source.replace(/(!\[[^\]]*\]\([^)\s]+)(\)\{width=(\d+)\})/g, '$1#width=$3)');
    return marked.parse(prepared, {async: false}) as string;
};

const sortKey = (rel: string) => rel.split('/').map(s => (s === 'index.md' ? '' : s)).join('/');

export const buildGuide = (mod: ModRef, folderOf: Map<string, string>, outDir: string, warn: (m: string) => void, formatRecipe: RecipeFormatter = asciiFormatter): Guide | null => {
    const found = findGuideDir(mod.folder);
    if (!found) return null;

    const files = walk(found.dir).sort((a, b) => sortKey(a).localeCompare(sortKey(b)));
    const pages = new Map(files.map(f => [f, pageUrl(mod.folder, f)] as const));

    const book = Bun.YAML.parse(readFileSync(path.join(found.dir, '..', 'book.yml'), 'utf8')) as {title?: string; description?: string};
    const out: GuidePage[] = files.map(rel => {
        const {meta, body} = splitFrontmatter(readFileSync(path.join(found.dir, rel), 'utf8'));
        const url = pages.get(rel)!;
        const ctx: RenderCtx = {mod, pageUrl: url, pageRel: rel, pages, folderOf, outDir, formatRecipe, warn};
        return {rel, url, title: String(meta.title ?? displayName(rel.replace(/\.md$/, '').replace(/^\d+-/, '').replace(/-/g, '_'))), body: renderMarkdown(body, ctx)};
    });

    return {mod, title: book.title ?? `${mod.name} Guide`, description: book.description ?? '', pages: out};
};

export const writeFile = (file: string, content: string) => {
    mkdirSync(path.dirname(file), {recursive: true});
    writeFileSync(file, content);
};
