import assert from 'node:assert/strict';
import {existsSync, readFileSync, readdirSync, statSync, writeFileSync} from 'node:fs';
import path from 'node:path';
import {Browser} from 'file:///C:/Users/bill/.codex/plugins/cache/taste-skill/taste-skill/2.1.4/skills/taste-skill/scripts/lib/cdp.mjs';

const root = path.resolve('site/dist');
const files = [];
const walk = dir => {
    for (const entry of readdirSync(dir, {withFileTypes: true})) {
        const full = path.join(dir, entry.name);
        if (entry.isDirectory()) walk(full);
        else if (entry.name.endsWith('.html')) files.push(full);
    }
};
walk(root);
const broken = [];
let references = 0;
for (const file of files) {
    const relative = path.relative(root, file).replaceAll('\\', '/');
    const base = new URL(relative, 'https://verify.invalid/minecraft-mods/');
    const html = readFileSync(file, 'utf8').replace(/<script\b[^>]*>[\s\S]*?<\/script>/gi, '');
    for (const match of html.matchAll(/\b(?:href|src)=["']([^"']+)["']/g)) {
        const href = match[1].replaceAll('&amp;', '&');
        const target = new URL(href, base);
        if (target.origin !== base.origin) continue;
        references++;
        if (!target.pathname.startsWith('/minecraft-mods/')) {
            broken.push({page: relative, href, reason: 'escapes the deployment base path'});
            continue;
        }
        const local = path.join(root, decodeURIComponent(target.pathname.slice('/minecraft-mods/'.length)));
        if (!existsSync(local) || (statSync(local).isDirectory() && !existsSync(path.join(local, 'index.html')))) {
            broken.push({page: relative, href, reason: 'missing file'});
        }
    }
}
assert.equal(broken.length, 0, JSON.stringify(broken.slice(0, 12)));
console.log(`Static audit: ${files.length} pages, ${references} internal references, no missing destinations or base-path escapes.`);

const checks = [];
const check = (name, value) => { assert.ok(value, name); checks.push(name); console.log(`PASS ${name}`); };
const browser = await Browser.launch({extraArgs: ['--remote-debugging-port=6103']});
try {
    const page = await browser.newPage({width: 390, height: 844, mobile: true, reducedMotion: true});
    await page.send('Emulation.setEmulatedMedia', {features: [
        {name: 'prefers-color-scheme', value: 'light'}, {name: 'prefers-reduced-motion', value: 'reduce'},
    ]});
    await page.goto('http://localhost:6101/', {settleMs: 200});
    check('34 mods, four features, and 30 initial index entries', await page.eval(() =>
        document.querySelectorAll('.catalogue-row').length === 34 &&
        document.querySelectorAll('.feature-entry').length === 4 &&
        document.querySelectorAll('.catalogue-row:not([hidden])').length === 30));
    check('Name search ranks Minekea first, including featured mods', await page.eval(() => {
        const input = document.getElementById('mod-search'); input.value = 'minekea'; input.dispatchEvent(new Event('input'));
        return document.querySelector('.catalogue-row:not([hidden]) h3').textContent === 'Minekea';
    }));
    check('Feature search finds upward hoppers from README content', await page.eval(() => {
        const input = document.getElementById('mod-search'); input.value = 'upward hopper'; input.dispatchEvent(new Event('input'));
        return [...document.querySelectorAll('.catalogue-row:not([hidden]) h3')].some(h => h.textContent === 'Hopper X-Treme');
    }));
    check('No-results feedback and live count', await page.eval(() => {
        const input = document.getElementById('mod-search'); input.value = 'zzzz-not-a-real-mod'; input.dispatchEvent(new Event('input'));
        return !document.getElementById('no-results').hidden && document.getElementById('mod-count').textContent === '0 of 34 mods';
    }));
    check('Pre-release filter and reset to the alphabetical 30-mod index', await page.eval(() => {
        const input = document.getElementById('mod-search'); input.value = ''; input.dispatchEvent(new Event('input'));
        const filter = document.getElementById('release-filter'); filter.value = 'beta'; filter.dispatchEvent(new Event('change'));
        const found = [...document.querySelectorAll('.catalogue-row:not([hidden])')];
        const valid = found.length > 0 && found.every(row => row.dataset.release === 'beta');
        filter.value = 'all'; filter.dispatchEvent(new Event('change'));
        return valid && document.querySelectorAll('.catalogue-row:not([hidden])').length === 30;
    }));
    check('Slash shortcut can focus the search field', await page.eval(() => {
        document.activeElement.blur(); document.dispatchEvent(new KeyboardEvent('keydown', {key: '/', bubbles: true}));
        return document.activeElement.id === 'mod-search';
    }));
    check('Dark theme changes the rendered ground', await page.eval(() => {
        const picker = document.getElementById('theme-picker'); picker.value = 'dark'; picker.dispatchEvent(new Event('change'));
        return document.documentElement.dataset.theme === 'dark' && getComputedStyle(document.body).backgroundColor === 'rgb(23, 18, 30)';
    }));
    await page.goto('http://localhost:6101/mods/minekea/', {settleMs: 200});
    check('Theme persists across navigation and download is generated from project metadata', await page.eval(() =>
        document.getElementById('theme-picker').value === 'dark' &&
        document.querySelector('.actions .primary').href === 'https://www.curseforge.com/projects/594052/files'));
    check('System theme returns to the OS preference', await page.eval(() => {
        const picker = document.getElementById('theme-picker'); picker.value = 'system'; picker.dispatchEvent(new Event('change'));
        return !document.documentElement.dataset.theme && getComputedStyle(document.body).backgroundColor === 'rgb(238, 237, 240)';
    }));
    await page.goto('http://localhost:6101/mods/minekea/guide/01-building-blocks/01-beams/', {settleMs: 200});
    check('Phone guide navigation collapses, opens by its native summary, and marks the current chapter', await page.eval(() => {
        const menu = document.querySelector('.side-menu'); const collapsed = !menu.open;
        menu.querySelector(':scope > summary').click();
        return collapsed && menu.open && [...menu.querySelectorAll('[aria-current=page]')].some(a => a.textContent === 'Beams');
    }));
    await page.setViewport({width: 1440, height: 900});
    await page.eval('new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve)))');
    check('Guide tree remains open when resized to desktop', await page.eval(() => document.querySelector('.side-menu').open));
    await page.goto('http://localhost:6101/ideas/', {settleMs: 200});
    check('All 216 ideas render', await page.eval(() => document.querySelectorAll('#body tr').length === 216));
    check('Roadmap mod filtering works', await page.eval(() => {
        const mod = document.getElementById('mod'); mod.value = 'minekea'; mod.dispatchEvent(new Event('change'));
        const rows = [...document.querySelectorAll('#body tr')];
        return rows.length > 0 && rows.every(row => row.cells[1].textContent === 'Minekea');
    }));
    check('Roadmap filter buttons expose their pressed state', await page.eval(() => {
        const button = document.querySelector('#f-effort button'); button.click();
        return button.getAttribute('aria-pressed') === 'true';
    }));
    check('Roadmap sort buttons expose the active sort', await page.eval(() => {
        const header = document.querySelector('#head th[data-k=title]'); header.querySelector('button').click();
        const ascending = header.getAttribute('aria-sort') === 'ascending'; header.querySelector('button').click();
        return ascending && header.getAttribute('aria-sort') === 'descending';
    }));
    await page.goto('http://localhost:6101/ideas/', {settleMs: 200});
    await page.setViewport({width: 390, height: 844, mobile: true});
    await page.eval('new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve)))');
    check('Phone advanced filters open natively and the idea table begins on the first screen', await page.eval(() => {
        const filters = document.getElementById('advanced-filters'); const collapsed = !filters.open;
        filters.querySelector('summary').click(); const opens = filters.open;
        filters.querySelector('summary').click(); window.scrollTo(0, 0);
        return collapsed && opens && document.querySelector('.wrap').getBoundingClientRect().top < innerHeight;
    }));
    check('The full roadmap scrolls inside its region without overflowing the phone page', await page.eval(() =>
        document.documentElement.scrollWidth === innerWidth && document.querySelector('.wrap').scrollWidth > document.querySelector('.wrap').clientWidth));
    await page.goto('http://localhost:6101/', {settleMs: 200});
    check('Reduced motion leaves the complete page visible without active animations', await page.eval(() =>
        document.getAnimations().length === 0 && getComputedStyle(document.querySelector('h1')).opacity === '1'));
    check('No browser JavaScript exceptions', page.consoleErrors.length === 0);
    const noScript = await browser.newPage({width: 390, height: 844, mobile: true});
    await noScript.send('Emulation.setScriptExecutionDisabled', {value: true});
    await noScript.goto('http://localhost:6101/', {settleMs: 200});
    // CDP evaluation still works with page scripts disabled.
    check('No-JavaScript fallback retains the four features and 30-mod index', await noScript.eval(() =>
        document.querySelectorAll('.feature-entry').length === 4 && document.querySelectorAll('.catalogue-row:not([hidden])').length === 30));
} finally { await browser.close(); }

const result = {pages: files.length, internalReferences: references, broken, checks,
    cssBytes: statSync(path.join(root, 'site.css')).size, jsBytes: statSync(path.join(root, 'site.js')).size,
    homeBytes: statSync(path.join(root, 'index.html')).size};
writeFileSync('.taste-skill/verification.json', JSON.stringify(result, null, 2) + '\n');
console.log(JSON.stringify(result, null, 2));
