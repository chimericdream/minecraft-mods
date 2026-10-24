import {Browser} from 'file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/lib/cdp.mjs';
import {mkdirSync, writeFileSync} from 'node:fs';
const stage = process.argv[2] || 'before';
const browser = await Browser.launch();
const results = [];
try {
  for (const theme of ['light', 'dark']) {
    for (const width of [1440, 768, 390]) {
      const page = await browser.newPage({width, height: 900});
      await page.send('Emulation.setEmulatedMedia', {features: [{name: 'prefers-color-scheme', value: theme}, {name: 'prefers-reduced-motion', value: 'reduce'}]});
      for (const [name, path] of [['home', ''], ['mod', 'mods/minekea/'], ['guide', 'mods/minekea/guide/'], ['roadmap', 'ideas/'], ['changelog', 'changelog/']]) {
        await page.goto('http://localhost:5000/' + path, {settleMs: 300});
        const dir = `.taste-skill/${stage}/${theme}/${width}`;
        mkdirSync(dir, {recursive: true});
        await page.screenshot({path: `${dir}/${name}.jpg`});
        if (name === 'home') await page.screenshot({path: `${dir}/${name}-full.jpg`, fullPage: true});
        results.push({theme, width, name, ...await page.eval(() => ({title: document.title, overflow: document.documentElement.scrollWidth > innerWidth, h1: document.querySelector('h1')?.textContent, errors: [...document.images].filter(i => !i.complete || !i.naturalWidth).map(i => i.src)}))});
      }
      await page.close();
    }
  }
  writeFileSync(`.taste-skill/${stage}/capture.json`, JSON.stringify(results, null, 2) + '\n');
} finally { await browser.close(); }
