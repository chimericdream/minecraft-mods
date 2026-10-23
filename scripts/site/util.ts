import path from 'node:path';

export const REPO_ROOT = path.join(import.meta.dir, '..', '..');

export const git = async (...args: string[]): Promise<string> => {
    const proc = Bun.spawn(['git', ...args], {cwd: REPO_ROOT, stdout: 'pipe', stderr: 'ignore'});
    const out = await new Response(proc.stdout).text();
    await proc.exited;
    return out.trim();
};

export const esc = (value: string): string =>
    value.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');

/** Loose key for matching mod names/labels/folders ("Hopper X-Treme" == "hopper-xtreme"). */
export const looseKey = (value: string): string => value.toLowerCase().replace(/[^a-z0-9]/g, '').replace(/s/g, '');

export const ago = (iso: string | null): string => {
    if (!iso) {
        return '—';
    }
    const days = Math.floor((Date.now() - new Date(iso).getTime()) / 86_400_000);
    if (days < 1) return 'today';
    if (days < 60) return `${days}d ago`;
    if (days < 730) return `${Math.floor(days / 30)}mo ago`;
    return `${Math.floor(days / 365)}y ago`;
};

export const REPO_URL = 'https://github.com/chimericdream/minecraft-mods';

/** Relative prefix from a directory-style URL back to the site root: `mods/a/guide/` -> `../../../`. */
export const rootPrefix = (url: string): string => '../'.repeat(url.split('/').filter(Boolean).length);

/** Relative link from one directory-style URL to another, always ending in a slash. */
export const relUrl = (from: string, to: string): string => {
    const r = path.posix.relative(from, to);
    return r ? `${r}/` : './';
};

/** GitHub-style heading ids, de-duplicated within a page. */
export const slugger = () => {
    const seen = new Map<string, number>();
    return (text: string) => {
        const base = text.toLowerCase().replace(/[^\p{L}\p{N}\s-]/gu, '').trim().replace(/\s+/g, '-') || 'section';
        const n = seen.get(base) ?? 0;
        seen.set(base, n + 1);
        return n === 0 ? base : `${base}-${n}`;
    };
};
