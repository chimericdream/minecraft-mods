import assert from 'node:assert/strict';
import {writeFileSync} from 'node:fs';
import {Browser} from 'file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/lib/cdp.mjs';

const browser = await Browser.launch();
const results = [];
const check = (name, value) => { assert.ok(value, name); results.push(name); };
try {
    const page = await browser.newPage({width:390,height:844,mobile:true,reducedMotion:true});
    const url = 'http://localhost:4010/';
    await page.goto(url);
    check('30 nonfeatured mods and four feature entries on home', await page.eval(() => document.querySelectorAll('.mod-row:not([hidden])').length === 30 && document.querySelectorAll('.feature-entry').length === 4));
    for (const query of ['hopper', 'camel', 'minekea', 'gear']) {
        await page.eval(q => { const e=document.getElementById('mod-search');e.value=q;e.dispatchEvent(new Event('input')); }, query);
        check(`Featured mod searchable: ${query}`, await page.eval(() => document.querySelectorAll('.mod-row:not([hidden])').length > 0));
    }
    await page.eval(() => { const e=document.getElementById('mod-search');e.value='no-such-mod-xyz';e.dispatchEvent(new Event('input')); });
    check('Search empty state', await page.eval(() => !document.getElementById('no-mods').hidden));
    await page.eval(() => { const e=document.getElementById('theme-choice');e.value='light';e.dispatchEvent(new Event('change')); });
    await page.goto(url+'mods/minekea/');
    check('Theme persists across navigation', await page.eval(() => document.documentElement.dataset.theme === 'light' && document.getElementById('theme-choice').value === 'light'));
    check('Phone sidebar starts collapsed', await page.eval(() => !document.querySelector('.side-panel').open));
    await page.eval(() => document.querySelector('.side-panel summary').click());
    check('Phone sidebar opens and exposes guide', await page.eval(() => document.querySelector('.side-panel').open && !![...document.querySelectorAll('.side a')].find(a => a.textContent === 'Guide')));
    check('Download uses repository project ID', await page.eval(() => document.querySelector('.actions .button').href === 'https://www.curseforge.com/projects/594052/files'));
    await page.eval(() => { const e=document.getElementById('theme-choice');e.value='system';e.dispatchEvent(new Event('change')); });
    check('System theme removes explicit override', await page.eval(() => !document.documentElement.dataset.theme && localStorage.getItem('chimeric-theme') === 'system'));
    await page.goto(url+'ideas/');
    const initial = await page.eval(() => document.querySelectorAll('#body tr').length);
    check('Roadmap renders all ideas', initial === 216);
    await page.eval(() => {const e=document.getElementById('q');e.value='hopper';e.dispatchEvent(new Event('input'));});
    check('Roadmap search filters rows', await page.eval(() => document.querySelectorAll('#body tr').length) < initial);
    await page.eval(() => document.querySelector('th[data-k="title"]').dispatchEvent(new KeyboardEvent('keydown',{key:'Enter',bubbles:true})));
    check('Roadmap sort responds to keyboard', await page.eval(() => document.querySelector('th[data-k="title"]').getAttribute('aria-sort') === 'ascending'));
    await page.eval(() => document.querySelector('.pill').click());
    check('Roadmap filter announces pressed state', await page.eval(() => document.querySelector('.pill').getAttribute('aria-pressed') === 'true'));
    await page.goto(url+'mods/hopper-xtreme/guide/');
    check('Guide tree has active page', await page.eval(() => !!document.querySelector('.side [aria-current="page"]')));
    check('No uncaught browser errors', page.consoleErrors.length === 0);
    check('Reduced motion produces no animations', await page.eval(() => document.getAnimations().length === 0));
    await page.send('Emulation.setScriptExecutionDisabled',{value:true});
    await page.goto(url);
    check('No-JS home retains 34 mods and primary action', await page.eval(() => document.querySelectorAll('.feature-entry').length === 4 && document.querySelectorAll('.mod-row:not([hidden])').length === 30 && !!document.querySelector('.hero .button')));
    await page.goto(url+'mods/hopper-xtreme/guide/');
    check('No-JS guide navigation remains open', await page.eval(() => document.querySelector('.side-panel').open));
    await page.send('Emulation.setScriptExecutionDisabled',{value:false});
    console.log(`Passed ${results.length} browser checks.`);
    writeFileSync('.taste-skill/verification.json',JSON.stringify(results,null,2)+'\n');
} finally { await browser.close(); }
