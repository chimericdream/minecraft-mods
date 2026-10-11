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
