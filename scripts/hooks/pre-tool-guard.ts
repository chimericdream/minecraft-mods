import {existsSync} from 'node:fs';

/**
 * Claude Code PreToolUse guard for the Bash and PowerShell tools. Exit code 2 blocks the call and
 * shows stderr to the model. Mechanical rules that used to be prose in docs/agents/*.md:
 *
 * - `python3` is the Microsoft Store stub on this machine; use `python`.
 * - Don't write files from Windows PowerShell 5.1 (`Set-Content`, `Out-File`, `>`): ANSI codepage.
 * - Don't run a bare `git commit` when the staged set spans more than one mod: commit by pathspec.
 */

const payload = JSON.parse(await Bun.stdin.text());
const tool: string = payload?.tool_name ?? '';
const command: string = payload?.tool_input?.command ?? '';

const block = (message: string): never => {
    console.error(message);
    process.exit(2);
};

// A command position: start of the command or just after a separator.
const SEGMENT_START = String.raw`(?:^|[;&|(]|&&|\|\|)\s*`;

if (new RegExp(`${SEGMENT_START}python3\\b`, 'm').test(command)) {
    block('Use `python`, not `python3` (python3 is the Microsoft Store stub here). See docs/agents/commands/local-tooling.md.');
}

if (tool === 'PowerShell') {
    const writesFile =
        /\b(Set-Content|Add-Content|Out-File)\b/i.test(command) ||
        // Redirection to a file; allow `2>$null`, `>$null` and `2>&1`-style stream merges.
        /\s>>?\s*(?!\$null\b|&)[^\s>]/i.test(command);

    if (writesFile) {
        block(
            'Do not write files from Windows PowerShell 5.1 (Set-Content / Add-Content / Out-File / >): ' +
            'it writes the ANSI codepage, not UTF-8. Use the Write/Edit tools, or Python with ' +
            'encoding="utf-8", newline="\\n".',
        );
    }
}

if (new RegExp(`${SEGMENT_START}git(?:\\s+-C\\s+\\S+)?\\s+commit\\b`, 'm').test(command)) {
    const run = (args: string[]): string[] => {
        const out = Bun.spawnSync(['git', ...args], {stdout: 'pipe', stderr: 'pipe'});
        return out.stdout.toString().split('\n').filter(Boolean);
    };

    const staged = run(['diff', '--cached', '--name-only']);
    const mods = [...new Set(staged.map((f) => f.split('/')[0]!))].filter(
        (dir) => staged.some((f) => f.startsWith(`${dir}/`)) && existsSync(`${dir}/gradle.properties`),
    );

    // A pathspec is present if the command mentions any staged file or one of its nested directories.
    // A bare top-level mod name is not enough: it shows up in commit messages like `feat(opus): ...`.
    const hasPathspec = staged.some((f) => {
        const parts = f.split('/');
        return parts.some((_, i) => (i > 0 || parts.length === 1) && command.includes(parts.slice(0, i + 1).join('/')));
    });

    if (mods.length > 1 && !hasPathspec) {
        block(
            `The staged set spans multiple mods (${mods.join(', ')}). ` +
            'Commit by explicit pathspec (`git commit <path> ...`) so unrelated staged work is not swept in. ' +
            'See docs/agents/releases/git.md.',
        );
    }
}
