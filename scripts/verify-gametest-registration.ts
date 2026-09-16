import path from 'node:path';
import {existsSync} from 'node:fs';
import {Glob} from 'bun';

import {getProjectFolder, resolveSelectedProjects} from './util/shared.ts';

/**
 * GameTest classes (server-side `@GameTest` methods, or client-side `FabricClientGameTest`
 * implementations) are only ever run if their fully-qualified class name is *also* listed in the
 * test-only `fabric.mod.json`'s `fabric-gametest`/`fabric-client-gametest` entrypoints. A class that
 * compiles fine but is missing from that list silently never runs — no error, no failing test, it's
 * just absent from the results. See docs/TESTING.md.
 *
 * This script cross-checks source against fabric.mod.json for every mod with a `gametest` source set
 * and reports any class that has test content but isn't registered (or is registered but no longer
 * exists).
 */

type EntrypointKind = 'fabric-gametest' | 'fabric-client-gametest';

type SourceClass = {
    fqcn: string;
    file: string;
    kind: EntrypointKind;
};

const PACKAGE_RE = /^\s*package\s+([\w.]+)\s*;/m;
const SERVER_GAMETEST_RE = /@GameTest\b/;
const CLIENT_GAMETEST_RE = /\bimplements\s+(?:\w+\.)*FabricClientGameTest\b/;

const findSourceClasses = async (javaRoot: string): Promise<SourceClass[]> => {
    const glob = new Glob('**/*.java');
    const results: SourceClass[] = [];

    for await (const relPath of glob.scan({cwd: javaRoot})) {
        const file = path.join(javaRoot, relPath);
        const contents = await Bun.file(file).text();

        const isServerTest = SERVER_GAMETEST_RE.test(contents);
        const isClientTest = CLIENT_GAMETEST_RE.test(contents);

        if (!isServerTest && !isClientTest) {
            continue;
        }

        const packageMatch = contents.match(PACKAGE_RE);
        const packageName = packageMatch?.[1];
        const className = path.basename(relPath, '.java');
        const fqcn = packageName ? `${packageName}.${className}` : className;

        results.push({
            fqcn,
            file: path.relative(process.cwd(), file),
            kind: isServerTest ? 'fabric-gametest' : 'fabric-client-gametest',
        });
    }

    return results;
};

const loadRegisteredEntrypoints = async (modJsonPath: string): Promise<Record<EntrypointKind, string[]>> => {
    const file = Bun.file(modJsonPath);

    if (!(await file.exists())) {
        return {'fabric-gametest': [], 'fabric-client-gametest': []};
    }

    const json = await file.json();
    const entrypoints = json.entrypoints ?? {};

    return {
        'fabric-gametest': entrypoints['fabric-gametest'] ?? [],
        'fabric-client-gametest': entrypoints['fabric-client-gametest'] ?? [],
    };
};

type Problem = {
    project: string;
    kind: EntrypointKind;
    fqcn: string;
    issue: 'unregistered' | 'stale';
    file?: string;
};

const checkProject = async (project: string): Promise<Problem[]> => {
    const javaRoot = path.join(getProjectFolder(project), 'fabric', 'src', 'gametest', 'java');

    if (!existsSync(javaRoot)) {
        return [];
    }

    const modJsonPath = path.join(getProjectFolder(project), 'fabric', 'src', 'gametest', 'resources', 'fabric.mod.json');
    const [sourceClasses, registered] = await Promise.all([
        findSourceClasses(javaRoot),
        loadRegisteredEntrypoints(modJsonPath),
    ]);

    const problems: Problem[] = [];
    const seenByKind: Record<EntrypointKind, Set<string>> = {
        'fabric-gametest': new Set(),
        'fabric-client-gametest': new Set(),
    };

    for (const {fqcn, file, kind} of sourceClasses) {
        seenByKind[kind].add(fqcn);
        if (!registered[kind].includes(fqcn)) {
            problems.push({project, kind, fqcn, issue: 'unregistered', file});
        }
    }

    for (const kind of ['fabric-gametest', 'fabric-client-gametest'] as const) {
        for (const fqcn of registered[kind]) {
            if (!seenByKind[kind].has(fqcn)) {
                problems.push({project, kind, fqcn, issue: 'stale'});
            }
        }
    }

    return problems;
};

const main = async () => {
    const projects = await resolveSelectedProjects();
    const allProblems: Problem[] = [];

    for (const project of projects) {
        allProblems.push(...await checkProject(project));
    }

    if (allProblems.length === 0) {
        console.log('All GameTest classes are registered in their fabric.mod.json entrypoints.');
        return;
    }

    console.log(`Found ${allProblems.length} GameTest registration problem(s):\n`);

    for (const problem of allProblems) {
        if (problem.issue === 'unregistered') {
            console.log(`  [UNREGISTERED] ${problem.project}: ${problem.fqcn} (${problem.kind})`);
            console.log(`      has test content in ${problem.file} but is NOT listed in fabric.mod.json`);
            console.log(`      -> it will silently never run`);
        } else {
            console.log(`  [STALE]        ${problem.project}: ${problem.fqcn} (${problem.kind})`);
            console.log(`      listed in fabric.mod.json but no matching class was found on disk`);
        }
    }

    process.exit(1);
};

await main();
