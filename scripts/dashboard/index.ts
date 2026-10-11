import {mkdirSync, writeFileSync} from 'node:fs';
import path from 'node:path';

import {buildDocs} from './docs.ts';
import {buildIdeasPage, collectIdeaRows} from './ideas-table.ts';
import {renderDashboard} from './render.ts';
import {collectGithub} from './sections/github.ts';
import {collectIdeas} from './sections/ideas.ts';
import {collectMods} from './sections/mods.ts';
import type {DashboardData, IdeasInfo} from './types.ts';
import {REPO_ROOT} from './util.ts';

const refresh = process.argv.includes('--refresh');
const open = process.argv.includes('--open');

const mods = await collectMods();
const ideas: Record<string, IdeasInfo> = {};
for (const m of mods) {
    const info = collectIdeas(m.folder);
    if (info) ideas[m.folder] = info;
}
const github = await collectGithub(mods, refresh);
if (!github.ok) {
    console.warn(`GitHub section unavailable: ${github.error}`);
}

const data: DashboardData = {generatedAt: new Date().toISOString(), mods, ideas, github};

const outDir = path.join(REPO_ROOT, 'dashboard', 'dist');
mkdirSync(outDir, {recursive: true});
buildDocs(mods, outDir);
const ideaRows = collectIdeaRows(mods);
buildIdeasPage(ideaRows, outDir);
const outFile = path.join(outDir, 'index.html');
writeFileSync(outFile, renderDashboard(data, ideaRows.length));
console.log(`Wrote ${outFile}`);

if (open) {
    Bun.spawn(process.platform === 'win32' ? ['cmd', '/c', 'start', '', outFile] : ['xdg-open', outFile]);
}
