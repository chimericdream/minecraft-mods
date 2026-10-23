import path from 'node:path';
import {readFileSync} from 'node:fs';

/**
 * Cheap, mechanical repo checks that used to live as prose rules in docs/agents/*.md:
 *
 * - text files must be valid UTF-8 (no stray Windows-1252 bytes), with no BOM
 * - text files must use LF line endings (gradlew.bat is the one CRLF exception)
 * - `.accesswidener` files may only live in `<mod>/common/src/main/resources/`
 * - each mod's `CHANGELOG.md` starts with `### Unreleased changes`
 * - each mod's `mod_version` looks like `x.y.z` or `x.y.z-beta.n`
 *
 * Usage:
 *   bun run lint                  # every tracked + untracked-not-ignored file
 *   bun run lint --staged         # only staged files (used by .githooks/pre-commit)
 *   bun run lint --hook           # read a Claude Code hook payload on stdin, check the edited file
 *   bun run lint <path> [<path>]  # specific files
 */

const CRLF_ALLOWED = new Set(['gradlew.bat']);
const SKIP_EXT = new Set(['.pdn']);
const ACCESS_WIDENER_RE = /^(?:[^/]+\/common\/src\/main\/resources\/[^/]+\.accesswidener|\.bun-create\/.+)$/;
const MOD_VERSION_RE = /^\d+\.\d+\.\d+(?:-beta\.\d+)?$/;

const git = (args: string[]): string[] => {
    const out = Bun.spawnSync(['git', ...args], {stdout: 'pipe', stderr: 'pipe'});
    return out.stdout.toString().split('\0').filter(Boolean);
};

const toRel = (file: string): string => path.relative(process.cwd(), path.resolve(file)).replaceAll(path.sep, '/');

const isText = (buf: Buffer): boolean => !buf.subarray(0, 8192).includes(0);

const checkFile = (rel: string): string[] => {
    const problems: string[] = [];

    if (SKIP_EXT.has(path.extname(rel).toLowerCase())) {
        return problems;
    }

    let buf: Buffer;
    try {
        buf = readFileSync(rel);
    } catch {
        return problems; // deleted or unreadable
    }

    if (rel.endsWith('.accesswidener') && !ACCESS_WIDENER_RE.test(rel)) {
        problems.push(`${rel}: access wideners may only be committed under <mod>/common/src/main/resources/ (docs/agents/gotchas/access-wideners.md)`);
    }

    if (!isText(buf)) {
        return problems;
    }

    try {
        new TextDecoder('utf-8', {fatal: true}).decode(buf);
    } catch (e) {
        const text = buf.toString('latin1');
        const bad = [...text].findIndex((c, i) => c.charCodeAt(0) >= 0x80 && !isValidUtf8At(buf, i));
        problems.push(`${rel}: not valid UTF-8 (first bad byte near offset ${bad}); was it written with PowerShell 5.1 / an ANSI codepage?`);
        return problems;
    }

    if (buf[0] === 0xef && buf[1] === 0xbb && buf[2] === 0xbf) {
        problems.push(`${rel}: has a UTF-8 BOM`);
    }

    if (!CRLF_ALLOWED.has(path.basename(rel)) && buf.includes(Buffer.from('\r\n'))) {
        problems.push(`${rel}: contains CRLF line endings (LF required; fix with: dos2unix "${rel}")`);
    }

    return problems;
};

// Best-effort offset finder for the error message only.
const isValidUtf8At = (buf: Buffer, i: number): boolean => {
    try {
        new TextDecoder('utf-8', {fatal: true}).decode(buf.subarray(0, i + 1));
        return true;
    } catch {
        return false;
    }
};

const checkMods = (): string[] => {
    const problems: string[] = [];
    const props = git(['ls-files', '-z', '--', '*/gradle.properties']).filter((p) => p.split('/').length === 2);

    for (const file of props) {
        const mod = path.dirname(file);
        const match = readFileSync(file, 'utf8').match(/^mod_version\s*=\s*(.+?)\s*$/m);
        if (match && !MOD_VERSION_RE.test(match[1]!)) {
            problems.push(`${file}: mod_version "${match[1]}" is not x.y.z or x.y.z-beta.n`);
        }

        try {
            const changelog = readFileSync(`${mod}/CHANGELOG.md`, 'utf8');
            if (!changelog.startsWith('### Unreleased changes')) {
                problems.push(`${mod}/CHANGELOG.md: must start with "### Unreleased changes"`);
            }
        } catch {
            // no changelog for this mod
        }
    }

    return problems;
};

const args = process.argv.slice(2);
let files: string[];
let runModChecks = false;

if (args.includes('--hook')) {
    // Claude Code PostToolUse payload: {"tool_input": {"file_path": "..."}}
    const payload = JSON.parse(await Bun.stdin.text());
    const target = payload?.tool_input?.file_path;
    if (!target) {
        process.exit(0);
    }
    files = [toRel(target)];
} else if (args.includes('--staged')) {
    files = git(['diff', '--cached', '--name-only', '--diff-filter=ACMR', '-z']);
    runModChecks = true;
} else if (args.length > 0) {
    files = args.map(toRel);
} else {
    files = git(['ls-files', '-z', '--cached', '--others', '--exclude-standard']);
    runModChecks = true;
}

const problems = [...new Set(files)].flatMap(checkFile);
if (runModChecks) {
    problems.push(...checkMods());
}

if (problems.length > 0) {
    console.error(problems.join('\n'));
    console.error(`\n${problems.length} lint problem(s).`);
    process.exit(args.includes('--hook') ? 2 : 1);
}
