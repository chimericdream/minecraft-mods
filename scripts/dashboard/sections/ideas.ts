import {existsSync, readdirSync, readFileSync, statSync} from 'node:fs';
import path from 'node:path';

import type {IdeasInfo} from '../types.ts';
import {REPO_ROOT} from '../util.ts';

const dirs = (p: string) => readdirSync(p).filter(n => statSync(path.join(p, n)).isDirectory());
const mdFiles = (p: string) => readdirSync(p).filter(n => n.endsWith('.md'));

export const collectIdeas = (folder: string): IdeasInfo | null => {
    const root = path.join(REPO_ROOT, folder, 'ideas');
    if (!existsSync(root)) {
        return null;
    }

    const readme = path.join(root, 'README.md');
    const hasReadme = existsSync(readme);
    // The identity section is the first "## " section; a "Draft" marker in it means not yet approved.
    const identityDraft = hasReadme && /draft/i.test(readFileSync(readme, 'utf8').split(/\n##\s/)[1] ?? '');

    const themes = dirs(root)
        .filter(n => n !== 'brainstorms')
        .map(name => ({name, count: mdFiles(path.join(root, name)).length}));

    let latestBrainstorm: IdeasInfo['latestBrainstorm'] = null;
    const bsRoot = path.join(root, 'brainstorms');
    if (existsSync(bsRoot)) {
        const latest = dirs(bsRoot).sort().at(-1);
        if (latest) {
            const files = mdFiles(path.join(bsRoot, latest));
            latestBrainstorm = {
                name: latest,
                stage: files.includes('shortlist.md') ? 'shortlist' : files.includes('combined-ideas.md') ? 'combined' : 'raw',
            };
        } else if (mdFiles(bsRoot).length > 0) {
            // Older flat layout: loose files directly in brainstorms/.
            latestBrainstorm = {name: '(legacy)', stage: mdFiles(bsRoot).includes('shortlist.md') ? 'shortlist' : 'raw'};
        }
    }

    return {
        folder,
        hasReadme,
        identityDraft,
        themes,
        ideaCount: themes.reduce((sum, t) => sum + t.count, 0),
        latestBrainstorm,
    };
};
