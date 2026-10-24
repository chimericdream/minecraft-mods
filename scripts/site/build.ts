import {copyFileSync, mkdirSync, rmSync} from 'node:fs';
import path from 'node:path';

import {collectDocs, docPage, readDoc, renderDoc, type Doc} from './docs.ts';
import {buildGuide, writeFile, type Guide} from './guide.ts';
import {collectIdeaRows, ideasPage} from './ideas.ts';
import {guidePage, SITE_CSS, SITE_JS, siteVars, type ModLinks} from './layout.ts';
import {collectGithub} from './local/github.ts';
import {collectIdeas} from './local/ideas-status.ts';
import {renderDashboard} from './local/render.ts';
import {changelogPage, FEATURED, homePage, iconUrl, overviewPage, readmeBody} from './pages.ts';
import {collectMods} from './mods.ts';
import {collectTextures, copyModAsset, type Texture} from './textures.ts';
import type {DashboardData, IdeasInfo} from './types.ts';
import {REPO_ROOT} from './util.ts';

export const OUT_DIR = path.join(REPO_ROOT, 'site', 'dist');

export type BuildOptions = {
    /** Also write the local-only page with GitHub issues/PRs and the ideas pipeline. Never deployed. */
    local: boolean;
    refresh: boolean;
};

export const buildSite = async (opts: BuildOptions): Promise<void> => {
    const outDir = OUT_DIR;
    rmSync(outDir, {recursive: true, force: true});

    const warnings: string[] = [];
    const warn = (msg: string) => warnings.push(msg);

    const mods = (await collectMods()).sort((a, b) => a.name.localeCompare(b.name));
    const docs = collectDocs(mods);
    siteVars.mc = mods[0]?.mcCompat ?? '';
    const folderOf = new Map(mods.map(m => [m.id, m.folder]));

    const guides = new Map<string, Guide>();
    for (const mod of mods) {
        const guide = buildGuide(mod, folderOf, outDir, warn);
        if (guide) guides.set(mod.folder, guide);
    }

    const links = new Map<string, ModLinks>(mods.map(mod => {
        const f = mod.folder;
        const guide = guides.get(f)?.pages.find(p => p.rel === 'index.md');
        return [f, {
            overview: `mods/${f}/`,
            guide: guide?.url ?? null,
            changelog: docs.get(`${f}/CHANGELOG.md`)?.url ?? null,
            ideas: docs.get(`${f}/ideas/README.md`)?.url ?? null,
        }];
    }));

    const textures = new Map<string, Texture[]>(mods.map(mod => [mod.folder, collectTextures(mod, outDir)]));
    const pictures = new Map<string, string>();
    for (const f of FEATURED) {
        const mod = mods.find(m => m.folder === f.folder);
        const url = mod && f.picture ? copyModAsset(mod, outDir, f.picture) : null;
        if (url) pictures.set(f.folder, url);
    }

    for (const mod of mods) {
        if (mod.iconFile) {
            const dest = path.join(outDir, iconUrl(mod));
            writeFile(dest, '');
            copyFileSync(mod.iconFile, dest);
        }
    }

    for (const [folder, guide] of guides) {
        for (const page of guide.pages) {
            writeFile(path.join(outDir, page.url, 'index.html'), guidePage(guide, page, links.get(folder)!));
        }
    }

    const docsOf = (folder: string): Doc[] => [...docs.values()].filter(d => d.mod.folder === folder);
    for (const doc of docs.values()) {
        const {source, title} = readDoc(doc.key);
        const isOverview = doc.key === `${doc.mod.folder}/README.md`;
        const isChangelog = doc.key === `${doc.mod.folder}/CHANGELOG.md`;
        const body = renderDoc(isOverview ? readmeBody(source) : isChangelog ? `# Changelog\n\n${source}` : source,
            {key: doc.key, fromUrl: doc.url, docs, outDir, warn});
        const l = links.get(doc.mod.folder)!;
        const html = isOverview ? overviewPage(doc, body, l, textures.get(doc.mod.folder) ?? [], mods) : docPage(doc, body, isChangelog ? 'Changelog' : title, l, docsOf(doc.mod.folder));
        writeFile(path.join(outDir, doc.url, 'index.html'), html);
    }

    const ideaRows = collectIdeaRows(mods);
    writeFile(path.join(outDir, 'ideas', 'index.html'), ideasPage(ideaRows));
    writeFile(path.join(outDir, 'changelog', 'index.html'), changelogPage(mods, (source, key) =>
        renderDoc(source, {key, fromUrl: 'changelog/', docs, outDir, warn})));
    const pageCount = [...guides.values()].reduce((n, g) => n + g.pages.length, 0);
    writeFile(path.join(outDir, 'index.html'), homePage({mods, links, textures, pictures, guides: guides.size, ideas: ideaRows.length}));
    writeFile(path.join(outDir, 'site.css'), SITE_CSS);
    writeFile(path.join(outDir, 'site.js'), SITE_JS);
    // The mark is referenced by path so the original vector can replace the placeholder trace.
    mkdirSync(path.join(outDir, 'brand'), {recursive: true});
    copyFileSync(path.join(REPO_ROOT, 'docs', 'design', 'logo-placeholder.svg'), path.join(outDir, 'brand', 'logo.svg'));
    // Keep GitHub Pages from running Jekyll, which would drop the `_img` and `_assets` folders.
    writeFile(path.join(outDir, '.nojekyll'), '');

    if (opts.local) {
        const ideas: Record<string, IdeasInfo> = {};
        for (const m of mods) {
            const info = collectIdeas(m.folder);
            if (info) ideas[m.folder] = info;
        }
        const github = await collectGithub(mods, opts.refresh);
        if (!github.ok) warn(`GitHub section unavailable: ${github.error}`);
        const data: DashboardData = {generatedAt: new Date().toISOString(), mods, ideas, github};
        writeFile(path.join(outDir, 'local', 'index.html'), renderDashboard(data, ideaRows.length));
    }

    for (const w of [...new Set(warnings)]) console.warn(`warning: ${w}`);
    console.log(`Built ${mods.length} mods, ${guides.size} guides (${pageCount} pages), ${docs.size} docs and ${ideaRows.length} ideas into ${outDir}`);
};
