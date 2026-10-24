import {mkdirSync, writeFileSync} from 'node:fs';
import {Browser} from 'file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/lib/cdp.mjs';
const phase = process.argv[2] || 'before';
const browser = await Browser.launch({extraArgs:['--remote-debugging-port=7302','--no-sandbox','--disable-gpu']});
const results = [];
try {
    const home = await browser.newPage();
    await home.goto('http://localhost:7300/mods/minekea/');
    const guide = await home.eval(() => [...document.querySelectorAll('a')].find(a => /In-game guide/.test(a.textContent))?.getAttribute('href'));
    const guideUrl = new URL(guide, 'http://localhost:7300/mods/minekea/').href;
    const pages = [['mod','http://localhost:7300/mods/minekea/'],['guide',guideUrl],['roadmap','http://localhost:7300/ideas/'],['changelog','http://localhost:7300/changelog/']];
    await home.close();
    for (const theme of ['light','dark']) for (const width of [1440,768,390]) {
        const page = await browser.newPage({width,height:900,mobile:width<600});
        await page.send('Emulation.setEmulatedMedia',{features:[{name:'prefers-color-scheme',value:theme}]});
        for (const [name,url] of pages) {
            await page.goto(url,{settleMs:500});
            if (!await page.eval(() => document.title)) await page.goto(url,{settleMs:1000});
            const dir = `.taste-skill/${phase}/surfaces`;
            mkdirSync(dir,{recursive:true});
            await page.screenshot({path:`${dir}/${name}-${theme}-${width}.jpg`});
            results.push({name,theme,width,url,...await page.eval(()=>({title:document.title,overflow:document.documentElement.scrollWidth>innerWidth}))});
        }
        await page.close();
    }
} finally { await browser.close(); }
writeFileSync(`.taste-skill/${phase}/surfaces/results.json`,JSON.stringify(results,null,2)+'\n');
console.log(JSON.stringify(results,null,2));
