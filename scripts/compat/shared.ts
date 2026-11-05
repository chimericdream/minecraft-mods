import path from 'node:path';

/** Repo root, resolved from this file so the scripts work from any current directory. */
export const REPO_ROOT = path.resolve(import.meta.dir, '..', '..');

/** Git-ignored working area for everything the compat tooling generates. */
export const COMPAT_ROOT = path.join(REPO_ROOT, '.compat');

/** Parses an issue number from a CLI argument. Only a plain positive integer is accepted. */
export const parseIssueNumber = (arg: string | undefined): number => {
    if (arg === undefined || !/^[1-9][0-9]{0,6}$/.test(arg)) {
        console.error('Usage: bun run compat:scaffold <issue-number>');
        process.exit(1);
    }

    return Number(arg);
};

export const issueDir = (issue: number): string => path.join(COMPAT_ROOT, String(issue));
