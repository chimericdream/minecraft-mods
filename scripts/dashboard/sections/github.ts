import {existsSync, mkdirSync, readFileSync, writeFileSync} from 'node:fs';
import path from 'node:path';

import type {GithubData, GithubItem, GithubResult, ModInfo} from '../types.ts';
import {looseKey, REPO_ROOT} from '../util.ts';

const CACHE = path.join(REPO_ROOT, 'dashboard', '.cache', 'github.json');
const TTL_MS = 5 * 60_000;

const gh = async (...args: string[]): Promise<any> => {
    const proc = Bun.spawn(['gh', ...args], {cwd: REPO_ROOT, stdout: 'pipe', stderr: 'pipe'});
    const [out, err] = await Promise.all([new Response(proc.stdout).text(), new Response(proc.stderr).text()]);
    if ((await proc.exited) !== 0) {
        throw new Error(`gh ${args.slice(0, 2).join(' ')} failed: ${err.trim()}`);
    }
    return JSON.parse(out);
};

const checkState = (rollup: any[] | null | undefined): GithubItem['checks'] => {
    if (!rollup || rollup.length === 0) return 'none';
    const states = rollup.map(c => (c.conclusion ?? c.state ?? c.status ?? '').toString().toUpperCase());
    if (states.some(s => ['FAILURE', 'ERROR', 'TIMED_OUT', 'CANCELLED'].includes(s))) return 'failing';
    if (states.every(s => ['SUCCESS', 'NEUTRAL', 'SKIPPED'].includes(s))) return 'passing';
    return 'pending';
};

/** Label match first, then `[mod]`/`mod:` title prefix, then (PRs) the folders the diff touches. */
const mapMods = (item: {title: string; labels: string[]; files?: string[]}, mods: ModInfo[]): string[] => {
    const byKey = new Map<string, string>();
    for (const m of mods) {
        for (const k of [m.name, m.id, m.folder]) byKey.set(looseKey(k), m.folder);
    }
    const found = new Set<string>();
    for (const label of item.labels) {
        const f = byKey.get(looseKey(label));
        if (f) found.add(f);
    }
    if (found.size === 0) {
        const prefix = item.title.match(/^\[([^\]]+)\]|^([\w .&-]+?):/);
        const f = prefix && byKey.get(looseKey(prefix[1] ?? prefix[2]));
        if (f) found.add(f);
    }
    if (found.size === 0) {
        // Last resort for issues: the title names a mod ("blacklight and cobblicious reference...").
        const title = item.title.toLowerCase();
        for (const m of mods) {
            if (title.includes(m.folder) || title.includes(m.name.toLowerCase())) found.add(m.folder);
        }
    }
    if (found.size === 0 && item.files) {
        const folders = new Set(mods.map(m => m.folder));
        for (const file of item.files) {
            const top = file.split('/')[0];
            if (folders.has(top)) found.add(top);
        }
    }
    return [...found];
};

const fetchAll = async (mods: ModInfo[]): Promise<GithubData> => {
    const [issues, prs] = await Promise.all([
        gh('issue', 'list', '--state', 'open', '-L', '300', '--json', 'number,title,url,labels,author,createdAt'),
        gh('pr', 'list', '--state', 'open', '-L', '100', '--json',
            'number,title,url,labels,author,createdAt,isDraft,files,statusCheckRollup'),
    ]);

    const items: GithubItem[] = [
        ...issues.map((i: any): GithubItem => {
            const labels = i.labels.map((l: any) => l.name);
            return {
                kind: 'issue', number: i.number, title: i.title, url: i.url, labels,
                author: i.author?.login ?? '', createdAt: i.createdAt,
                mods: mapMods({title: i.title, labels}, mods),
            };
        }),
        ...prs.map((p: any): GithubItem => {
            const labels = p.labels.map((l: any) => l.name);
            return {
                kind: 'pr', number: p.number, title: p.title, url: p.url, labels,
                author: p.author?.login ?? '', createdAt: p.createdAt, isDraft: p.isDraft,
                checks: checkState(p.statusCheckRollup),
                mods: mapMods({title: p.title, labels, files: p.files?.map((f: any) => f.path)}, mods),
            };
        }),
    ];
    return {fetchedAt: new Date().toISOString(), items};
};

export const collectGithub = async (mods: ModInfo[], refresh: boolean): Promise<GithubResult> => {
    try {
        if (!refresh && existsSync(CACHE)) {
            const cached = JSON.parse(readFileSync(CACHE, 'utf8')) as GithubData;
            if (Date.now() - new Date(cached.fetchedAt).getTime() < TTL_MS) {
                return {ok: true, data: cached};
            }
        }
        const data = await fetchAll(mods);
        mkdirSync(path.dirname(CACHE), {recursive: true});
        writeFileSync(CACHE, JSON.stringify(data));
        return {ok: true, data};
    } catch (e) {
        return {ok: false, error: e instanceof Error ? e.message : String(e)};
    }
};
