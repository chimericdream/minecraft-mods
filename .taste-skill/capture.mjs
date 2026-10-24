import {mkdirSync, writeFileSync} from 'node:fs';
import {Browser} from 'file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/lib/cdp.mjs';

const stage = process.argv[2] || 'before';
const root = `http://localhost:4010/`;
const routes = {home: '', mod: 'mods/minekea/', guide: 'mods/hopper-xtreme/guide/', roadmap: 'ideas/', changelog: 'changelog/'};
const browser = await Browser.launch();
const results = [];
try {
    for (const theme of ['light', 'dark']) {
        const folder = `.taste-skill/${stage}/${theme}`;
        mkdirSync(folder, {recursive: true});
        for (const width of [1440, 768, 390]) {
            const page = await browser.newPage({width, height: width === 768 ? 1024 : 900, mobile: width < 600});
            await page.send('Emulation.setEmulatedMedia', {features: [{name: 'prefers-color-scheme', value: theme}, {name: 'prefers-reduced-motion', value: 'reduce'}]});
            for (const [name, route] of Object.entries(routes)) {
                for (let attempt = 0; attempt < 5; attempt++) {
                    await page.goto(root + route, {settleMs: 350});
                    if (await page.eval('!!document.querySelector("main")')) break;
                    await new Promise(resolve => setTimeout(resolve, 1000));
                }
                if (!await page.eval('!!document.querySelector("main")')) throw new Error(`Page missing: ${route}`);
                await page.eval('document.fonts.ready');
                await page.eval('window.scrollTo({top:0,behavior:"instant"})');
                await new Promise(resolve => setTimeout(resolve, 150));
                await page.screenshot({path: `${folder}/${name}-${width}.jpg`, quality: 82});
                if (name === 'home') await page.screenshot({path: `${folder}/${name}-${width}-full.jpg`, quality: 78, fullPage: true});
                results.push(await page.eval(() => ({url: location.pathname, width: innerWidth, theme: getComputedStyle(document.body).backgroundColor, overflow: document.documentElement.scrollWidth > innerWidth, broken: [...document.images].filter(i => !i.complete || !i.naturalWidth).map(i => i.src)})));
            }
            await page.close();
        }
    }
    writeFileSync(`.taste-skill/${stage}/capture-results.json`, JSON.stringify(results, null, 2) + '\n');
    console.log(`Captured ${results.length} views in .taste-skill/${stage}, plus six full home pages.`);
    console.log(JSON.stringify(results.filter(r => r.overflow || r.broken.length)));
} finally { await browser.close(); }
