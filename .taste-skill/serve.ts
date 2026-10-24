// Stable review server: serves a completed build without the development watcher
// rebuilding when design notes or screenshots are saved.
import {existsSync,statSync} from 'node:fs';
import path from 'node:path';
Bun.serve({port:7300,fetch(req) {
    const url = new URL(req.url);
    const review = url.pathname.startsWith('/review/');
    const root = path.resolve(review ? '.taste-skill' : 'site/dist');
    const pathname = review ? url.pathname.slice('/review'.length).replace(/^\/$/,'/comparison.html') : url.pathname.replace(/^\/minecraft-mods(?=\/|$)/,'');
    const target = path.resolve(root,'.'+decodeURIComponent(pathname));
    if (!target.startsWith(root+path.sep) && target !== root) return new Response('Not found',{status:404});
    for (const file of [target,path.join(target,'index.html')]) {
        if(existsSync(file) && statSync(file).isFile()) return new Response(Bun.file(file));
    }
    return new Response('Not found',{status:404});
}});
console.log('Reviewing completed build at http://localhost:7300/ and /minecraft-mods/');
