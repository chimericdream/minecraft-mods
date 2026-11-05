import {mkdir} from 'node:fs/promises';
import path from 'node:path';

import {REPO_ROOT, issueDir, parseIssueNumber} from './shared.ts';

/**
 * Compat repro scaffold (see ideas/compat-repro-scaffold.md).
 *
 * Reads a manifest for one issue, checks it strictly, and writes a CurseForge modpack zip that the
 * CurseForge app or Prism Launcher can import. Mods that are only on Modrinth are printed as links so the
 * user can download them by hand.
 *
 * This script makes no network requests, downloads nothing, and runs nothing. It reads one JSON file and
 * writes one zip file that holds a `manifest.json` and an empty `overrides/` folder. The launcher does the
 * downloading when the user imports the pack. The manifest is untrusted input (it is built from a bug
 * report), so every field is checked against a strict pattern before it is used.
 */

const MAX_MODS = 40;

const SLUG_PATTERN = /^[a-z0-9_-]{1,64}$/;
const VERSION_PATTERN = /^[A-Za-z0-9._+-]{1,64}$/;
const MC_VERSION_PATTERN = /^[0-9]+(\.[0-9]+){1,2}$/;
const MODRINTH_VERSION_ID_PATTERN = /^[A-Za-z0-9]{8}$/;
/** Names are only shown in the terminal. No control characters, so no terminal escape sequences. */
const NAME_PATTERN = /^[^\u0000-\u001f\u007f]{1,120}$/;

type ManifestMod = {
    name: string;
    version: string;
    curseforge?: {projectId: number; fileId: number};
    modrinth?: {slug: string; versionId: string};
    note?: string;
};

type Manifest = {
    issue: number;
    minecraft: string;
    loader: 'neoforge' | 'fabric';
    loaderVersion: string;
    mods: ManifestMod[];
};

const fail = (message: string): never => {
    console.error(`Error: ${message}`);
    process.exit(1);
};

const isRecord = (value: unknown): value is Record<string, unknown> =>
    typeof value === 'object' && value !== null && !Array.isArray(value);

const matches = (value: unknown, pattern: RegExp): value is string =>
    typeof value === 'string' && pattern.test(value);

const isId = (value: unknown): value is number => Number.isInteger(value) && (value as number) > 0;

/** Strict schema check. Anything that does not match exactly stops the run. */
const validateManifest = (raw: unknown, issue: number): Manifest => {
    if (!isRecord(raw)) {
        return fail('manifest is not an object');
    }

    if (raw.issue !== issue) {
        fail(`manifest issue (${String(raw.issue)}) does not match ${issue}`);
    }

    if (!matches(raw.minecraft, MC_VERSION_PATTERN)) {
        fail('manifest "minecraft" is not a version like 26.2');
    }

    if (raw.loader !== 'neoforge' && raw.loader !== 'fabric') {
        fail('manifest "loader" must be "neoforge" or "fabric"');
    }

    if (!matches(raw.loaderVersion, VERSION_PATTERN)) {
        fail('manifest "loaderVersion" has unexpected characters');
    }

    if (!Array.isArray(raw.mods) || raw.mods.length === 0 || raw.mods.length > MAX_MODS) {
        fail(`manifest "mods" must be a list of 1 to ${MAX_MODS} entries`);
    }

    const seen = new Set<string>();

    for (const mod of raw.mods as unknown[]) {
        if (!isRecord(mod)) {
            return fail('a mod entry is not an object');
        }

        const label = JSON.stringify(mod.name);

        if (!matches(mod.name, NAME_PATTERN)) {
            fail(`a mod "name" is missing, too long, or has control characters: ${label}`);
        }

        if (!matches(mod.version, VERSION_PATTERN)) {
            fail(`mod ${label}: "version" has unexpected characters`);
        }

        if (mod.note !== undefined && !matches(mod.note, NAME_PATTERN)) {
            fail(`mod ${label}: "note" is too long or has control characters`);
        }

        if ((mod.curseforge === undefined) === (mod.modrinth === undefined)) {
            fail(`mod ${label}: give exactly one of "curseforge" or "modrinth"`);
        }

        let key: string;

        if (mod.curseforge !== undefined) {
            const cf = mod.curseforge;

            if (!isRecord(cf) || !isId(cf.projectId) || !isId(cf.fileId)) {
                return fail(`mod ${label}: curseforge needs integer "projectId" and "fileId"`);
            }

            key = `cf:${cf.projectId}`;
        } else {
            const mr = mod.modrinth;

            if (!isRecord(mr) || !matches(mr.slug, SLUG_PATTERN) || !matches(mr.versionId, MODRINTH_VERSION_ID_PATTERN)) {
                return fail(`mod ${label}: modrinth needs a "slug" and an 8-character "versionId"`);
            }

            key = `mr:${mr.slug}`;
        }

        if (seen.has(key)) {
            fail(`mod ${label}: the same project is listed twice`);
        }

        seen.add(key);
    }

    return raw as unknown as Manifest;
};

const readManifest = async (issue: number): Promise<Manifest> => {
    const candidates = [
        path.join(issueDir(issue), 'manifest.json'),
        path.join(REPO_ROOT, 'tools', 'compat', 'examples', `${issue}.manifest.json`),
    ];

    for (const candidate of candidates) {
        const file = Bun.file(candidate);

        if (await file.exists()) {
            console.log(`Manifest: ${candidate}`);
            return validateManifest(await file.json(), issue);
        }
    }

    return fail(`no manifest found. Looked in:\n  ${candidates.join('\n  ')}`);
};

/** The `manifest.json` that goes inside the zip. Matches the CurseForge modpack format. */
const buildPackManifest = (manifest: Manifest) => ({
    minecraft: {
        version: manifest.minecraft,
        modLoaders: [{id: `${manifest.loader}-${manifest.loaderVersion}`, primary: true}],
    },
    manifestType: 'minecraftModpack',
    manifestVersion: 1,
    name: `Compat repro #${manifest.issue}`,
    version: '1.0.0',
    author: 'compat-scaffold',
    files: manifest.mods
        .filter((mod) => mod.curseforge !== undefined)
        .map((mod) => ({
            projectID: mod.curseforge!.projectId,
            fileID: mod.curseforge!.fileId,
            required: true,
            isLocked: false,
        })),
    overrides: 'overrides',
});

type ZipEntry = {name: string; data: Uint8Array};

/**
 * A minimal zip writer: stored (not compressed) entries, a fixed timestamp so the output is the same
 * every time. Enough for a manifest and an empty folder, and it needs no dependency.
 */
const buildZip = (entries: ZipEntry[]): Uint8Array => {
    const encoder = new TextEncoder();
    const DOS_DATE = (0 << 9) | (1 << 5) | 1; // 1980-01-01
    const parts: Uint8Array[] = [];
    const central: Uint8Array[] = [];
    let offset = 0;

    for (const {name, data} of entries) {
        const nameBytes = encoder.encode(name);
        const crc = data.length === 0 ? 0 : Number(Bun.hash.crc32(data));

        const local = new DataView(new ArrayBuffer(30));
        local.setUint32(0, 0x04034b50, true);
        local.setUint16(4, 20, true); // version needed
        local.setUint16(6, 0x0800, true); // UTF-8 names
        local.setUint16(8, 0, true); // method: stored
        local.setUint16(10, 0, true); // time
        local.setUint16(12, DOS_DATE, true);
        local.setUint32(14, crc, true);
        local.setUint32(18, data.length, true);
        local.setUint32(22, data.length, true);
        local.setUint16(26, nameBytes.length, true);
        local.setUint16(28, 0, true); // extra length

        const header = new DataView(new ArrayBuffer(46));
        header.setUint32(0, 0x02014b50, true);
        header.setUint16(4, 20, true); // version made by
        header.setUint16(6, 20, true); // version needed
        header.setUint16(8, 0x0800, true);
        header.setUint16(10, 0, true);
        header.setUint16(12, 0, true);
        header.setUint16(14, DOS_DATE, true);
        header.setUint32(16, crc, true);
        header.setUint32(20, data.length, true);
        header.setUint32(24, data.length, true);
        header.setUint16(28, nameBytes.length, true);
        header.setUint32(42, offset, true);

        const localBytes = new Uint8Array(local.buffer);
        parts.push(localBytes, nameBytes, data);
        central.push(new Uint8Array(header.buffer), nameBytes);
        offset += localBytes.length + nameBytes.length + data.length;
    }

    const centralSize = central.reduce((sum, chunk) => sum + chunk.length, 0);
    const end = new DataView(new ArrayBuffer(22));
    end.setUint32(0, 0x06054b50, true);
    end.setUint16(8, entries.length, true);
    end.setUint16(10, entries.length, true);
    end.setUint32(12, centralSize, true);
    end.setUint32(16, offset, true);

    const chunks = [...parts, ...central, new Uint8Array(end.buffer)];
    const out = new Uint8Array(chunks.reduce((sum, chunk) => sum + chunk.length, 0));
    let position = 0;

    for (const chunk of chunks) {
        out.set(chunk, position);
        position += chunk.length;
    }

    return out;
};

/** A clickable terminal hyperlink (OSC 8). Terminals without support still show the plain URL. */
const link = (url: string): string => `\u001b]8;;${url}\u0007${url}\u001b]8;;\u0007`;

const main = async (): Promise<void> => {
    const issue = parseIssueNumber(process.argv[2]);
    const manifest = await readManifest(issue);

    const packManifest = buildPackManifest(manifest);
    const zip = buildZip([
        {name: 'manifest.json', data: new TextEncoder().encode(`${JSON.stringify(packManifest, null, 4)}\n`)},
        {name: 'overrides/', data: new Uint8Array(0)},
    ]);

    const outDir = issueDir(issue);
    const outFile = path.join(outDir, `compat-${issue}.zip`);
    await mkdir(outDir, {recursive: true});
    await Bun.write(outFile, zip);

    const curseforge = manifest.mods.filter((mod) => mod.curseforge !== undefined);
    const modrinth = manifest.mods.filter((mod) => mod.modrinth !== undefined);

    console.log(`\nWrote ${outFile}`);
    console.log(`  ${manifest.loader} ${manifest.loaderVersion}, Minecraft ${manifest.minecraft}`);
    console.log(`\nIn the pack (${curseforge.length}, the launcher downloads these):`);

    for (const mod of curseforge) {
        console.log(`  - ${mod.name} ${mod.version}${mod.note ? `  (${mod.note})` : ''}`);
    }

    if (modrinth.length > 0) {
        console.log(`\nNot in the pack (${modrinth.length}, only on Modrinth). Download each and copy the .jar into the instance's mods folder:`);

        for (const mod of modrinth) {
            const url = `https://modrinth.com/mod/${mod.modrinth!.slug}/version/${mod.modrinth!.versionId}`;
            console.log(`  - ${mod.name} ${mod.version}: ${link(url)}${mod.note ? `  (${mod.note})` : ''}`);
        }
    }

    console.log('\nNext: import the zip in the CurseForge app (Create Custom Profile > Import) or Prism Launcher (Add Instance > Import).');
    console.log('Nothing was downloaded or started by this script.');
};

await main();
