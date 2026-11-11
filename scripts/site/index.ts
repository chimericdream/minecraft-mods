import {existsSync, statSync, watch} from 'node:fs';
import path from 'node:path';

import {buildSite, OUT_DIR} from './build.ts';
import {REPO_ROOT} from './util.ts';

const args = process.argv.slice(2);
const flag = (name: string) => args.includes(`--${name}`);
const option = (name: string) => {
    const i = args.indexOf(`--${name}`);
    return i === -1 ? undefined : args[i + 1];
};

const serve = flag('serve');
const refresh = flag('refresh');
const run = () => buildSite({local: serve, refresh});

await run();

if (!serve) {
    process.exit(0);
}

const port = Number(option('port') ?? 4000);

const resolveFile = (pathname: string): string | null => {
    const rel = decodeURIComponent(pathname).replace(/^\/+/, '');
    const target = path.join(OUT_DIR, rel);
    if (!target.startsWith(OUT_DIR)) return null;
    for (const candidate of [target, path.join(target, 'index.html')]) {
        if (existsSync(candidate) && statSync(candidate).isFile()) return candidate;
    }
    return null;
};

const server = Bun.serve({
    port,
    fetch(req) {
        const url = new URL(req.url);
        // Directory-style URLs need the trailing slash so relative links resolve.
        if (!url.pathname.endsWith('/') && !path.extname(url.pathname) && resolveFile(url.pathname)) {
            return Response.redirect(`${url.pathname}/${url.search}`, 301);
        }
        const file = resolveFile(url.pathname);
        return file ? new Response(Bun.file(file)) : new Response('Not found', {status: 404});
    },
});
console.log(`Serving ${OUT_DIR} at ${server.url}  (local dashboard at ${server.url}local/)`);

if (flag('open')) {
    Bun.spawn(process.platform === 'win32' ? ['cmd', '/c', 'start', '', server.url.href] : ['xdg-open', server.url.href]);
}

// Rebuild when sources change. Generator code changes need a restart.
const IGNORED = /(^|[\\/])(node_modules|build|\.gradle|\.git|dist|\.cache|run|logs|\.sources)([\\/]|$)/;
const WATCHED = /\.(md|yml|png)$|gradle\.properties$/;
let timer: ReturnType<typeof setTimeout> | undefined;
let building = false;
let again = false;

const rebuild = async () => {
    if (building) {
        again = true;
        return;
    }
    building = true;
    try {
        await run();
    } catch (e) {
        console.error(e);
    }
    building = false;
    if (again) {
        again = false;
        void rebuild();
    }
};

watch(REPO_ROOT, {recursive: true}, (_event, filename) => {
    if (!filename || IGNORED.test(filename) || !WATCHED.test(filename)) return;
    clearTimeout(timer);
    timer = setTimeout(() => void rebuild(), 300);
});
