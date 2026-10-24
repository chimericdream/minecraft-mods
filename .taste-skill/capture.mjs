import {existsSync, mkdirSync, readFileSync, writeFileSync} from 'node:fs';
import {Browser} from 'file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/lib/cdp.mjs';

const phase = process.argv[2] || 'before';
const selected = new Set(process.argv.slice(3));
const out = `.taste-skill/${phase}/matrix`;
mkdirSync(out, {recursive: true});
const targets = [
    ['home', ''],
    ['minekea', 'mods/minekea/'],
    ['guide', 'mods/minekea/guide/01-building-blocks/01-beams/'],
    ['roadmap', 'ideas/'],
    ['changelog', 'changelog/'],
];
const browser = await Browser.launch({extraArgs: ['--remote-debugging-port=6102']});
const measurements = selected.size && existsSync(`${out}/measurements.json`)
    ? JSON.parse(readFileSync(`${out}/measurements.json`, 'utf8')).filter(row => !selected.has(row.name)) : [];
try {
    const page = await browser.newPage();
    for (const [name, route] of targets.filter(([name]) => !selected.size || selected.has(name))) {
        for (const width of [1440, 768, 390]) {
            for (const theme of ['light', 'dark']) {
                await page.setViewport({width, height: width === 1440 ? 900 : width === 768 ? 1024 : 844, mobile: width < 600});
                await page.send('Emulation.setEmulatedMedia', {features: [
                    {name: 'prefers-color-scheme', value: theme},
                    {name: 'prefers-reduced-motion', value: 'reduce'},
                ]});
                await page.send('Page.addScriptToEvaluateOnNewDocument', {source: "try { localStorage.removeItem('chimeric-theme'); } catch {}"});
                await page.goto(`http://localhost:6101/${route}`, {settleMs: 350});
                for (let retry = 0; retry < 5 && !await page.eval("!!document.querySelector('h1') && !!document.title"); retry++) {
                    await page.goto(`http://localhost:6101/${route}`, {settleMs: 1800});
                }
                if (!await page.eval("!!document.querySelector('h1') && !!document.title")) {
                    throw new Error(`Incomplete page: ${name} ${width} ${theme}`);
                }
                await page.eval('document.fonts.ready.then(() => true)');
                await page.screenshot({path: `${out}/${name}-${width}-${theme}.jpg`, quality: 85});
                if (name === 'home' || (width === 1440 && name === 'minekea')) {
                    await page.screenshot({path: `${out}/${name}-${width}-${theme}-full.jpg`, quality: 80, fullPage: true});
                }
                measurements.push(await page.eval(({name, theme, width}) => ({name, theme, width,
                    title: document.title,
                    overflow: document.documentElement.scrollWidth > innerWidth,
                    h1: document.querySelector('h1')?.textContent,
                    links: document.links.length,
                    font: getComputedStyle(document.body).fontFamily,
                    background: getComputedStyle(document.body).backgroundColor,
                }), {name, theme, width}));
                console.log(`${name} ${width} ${theme}`);
            }
        }
    }
} finally { await browser.close(); }
writeFileSync(`${out}/measurements.json`, JSON.stringify(measurements, null, 2) + '\n');
