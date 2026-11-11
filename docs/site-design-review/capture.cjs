const fs = require('node:fs/promises');
const path = require('node:path');
const assert = require('node:assert/strict');

const args = process.argv.slice(2);
const option = (name, fallback) => {
    const index = args.indexOf(`--${name}`);
    return index === -1 ? fallback : args[index + 1];
};
const playwright = require(option('playwright', 'playwright-core'));
const baseUrl = option('base-url', 'http://127.0.0.1:4180/');
const output = path.resolve(option('output', path.join(__dirname, 'screenshots')));
const desktop = {width: 1440, height: 1000};
const mobile = {width: 390, height: 844};
const shots = [
    {name: '01-home-desktop', route: '/', viewport: desktop},
    {name: '02-mod-overview-desktop', route: '/mods/minekea/', viewport: desktop},
    {name: '03-guide-index-desktop', route: '/mods/minekea/guide/', viewport: desktop},
    {name: '04-guide-callouts-desktop', route: '/mods/opus/guide/99-appendices/kitchen-sink/#callouts', viewport: desktop},
    {name: '05-guide-recipes-desktop', route: '/mods/opus/guide/99-appendices/kitchen-sink/#recipes', viewport: desktop},
    {name: '06-guide-images-desktop', route: '/mods/all-hallows-steve/guide/01-pumpkins/01-carving-station/', viewport: desktop},
    {name: '07-ideas-index-desktop', route: '/mods/minekea/ideas/', viewport: desktop},
    {name: '08-idea-detail-desktop', route: '/ideas/', viewport: desktop, discoverIdea: true},
    {name: '09-roadmap-desktop', route: '/ideas/', viewport: desktop},
    {name: '10-changelog-desktop', route: '/changelog/', viewport: desktop},
    {name: '11-home-mobile', route: '/', viewport: mobile},
    {name: '12-guide-mobile', route: '/mods/minekea/guide/01-building-blocks/09-framed-planks/', viewport: mobile},
    {name: '13-roadmap-mobile', route: '/ideas/', viewport: mobile},
    {name: '14-home-dark-desktop', route: '/', viewport: desktop, scheme: 'dark'},
];

async function main() {
    await fs.mkdir(output, {recursive: true});
    const browser = await playwright.chromium.launch({headless: true});
    try {
        const context = await browser.newContext({deviceScaleFactor: 1});
        const page = await context.newPage();
        const errors = [];
        page.on('pageerror', error => errors.push(error.message));
        const captures = [];
        for (const shot of shots) {
            await page.setViewportSize(shot.viewport);
            await page.emulateMedia({colorScheme: shot.scheme || 'light', reducedMotion: 'reduce'});
            let route = shot.route;
            if (shot.discoverIdea) {
                await page.goto(new URL('ideas/', baseUrl).href);
                route = await page.locator('#body td a').first().getAttribute('href');
                route = new URL(route, page.url()).href;
            }
            const response = await page.goto(new URL(route, baseUrl).href, {waitUntil: 'networkidle'});
            if (response) assert.equal(response.status(), 200, route);
            await page.evaluate(async () => {
                await document.fonts.ready;
                await Promise.all([...document.images].filter(image => image.complete).map(image => image.decode().catch(() => {})));
            });
            if (new URL(page.url()).hash) {
                await page.locator(new URL(page.url()).hash).scrollIntoViewIfNeeded();
            }
            const metrics = await page.evaluate(() => {
                const box = selector => {
                    const element = document.querySelector(selector);
                    if (!element) return null;
                    const rect = element.getBoundingClientRect();
                    return {top: Math.round(rect.top + scrollY), width: Math.round(rect.width), height: Math.round(rect.height)};
                };
                return {
                    title: document.title,
                    viewport: {width: innerWidth, height: innerHeight},
                    scroll: {x: scrollX, y: Math.round(scrollY)},
                    document: {width: document.documentElement.scrollWidth, height: document.documentElement.scrollHeight},
                    article: box('article'), sidebar: box('.side'), table: box('.wrap'),
                    headings: [...document.querySelectorAll('article h1, article h2')].map(heading => heading.textContent),
                    images: [...document.images].map(image => ({src: image.getAttribute('src'), loaded: image.complete && image.naturalWidth > 0})),
                    count: document.querySelector('#count')?.textContent || null,
                    nonKeyboardPills: document.querySelectorAll('span.pill:not([tabindex])').length,
                };
            });
            const filename = `${shot.name}.png`;
            await page.screenshot({path: path.join(output, filename), fullPage: false, animations: 'disabled'});
            captures.push({file: `screenshots/${filename}`, route: new URL(page.url()).pathname + new URL(page.url()).hash, scheme: shot.scheme || 'light', ...metrics});
            console.log(`${filename}: ${metrics.title}`);
        }

        await page.emulateMedia({colorScheme: 'light'});
        await page.goto(new URL('ideas/', baseUrl).href);
        const total = await page.locator('#body tr').count();
        await page.locator('#mod').selectOption('minekea');
        const modFiltered = await page.locator('#body tr').count();
        assert(modFiltered > 0 && modFiltered < total);
        await page.locator('#q').fill('unmatchable-audit-query-9238');
        assert.equal(await page.locator('#body tr').count(), 0);
        const emptyStateText = await page.locator('#count').textContent();
        const filteredUrl = page.url();
        await page.reload();
        assert.equal(await page.locator('#q').inputValue(), 'unmatchable-audit-query-9238');
        assert.equal(await page.locator('#mod').inputValue(), 'minekea');
        assert.equal(await page.locator('#body tr').count(), 0);
        await page.locator('#reset').click();
        await page.waitForLoadState('networkidle');
        assert.equal(await page.locator('#body tr').count(), total);
        await page.locator('th[data-k=title]').click();
        assert.equal(await page.locator('th[data-k=title]').getAttribute('class'), 'asc');
        const ascendingTitles = await page.locator('#body td:nth-child(3)').allTextContents();
        assert.deepEqual(ascendingTitles, [...ascendingTitles].sort((first, second) => first.localeCompare(second, undefined, {numeric: true})));
        await page.locator('th[data-k=title]').click();
        assert.equal(await page.locator('th[data-k=title]').getAttribute('class'), 'desc');
        const descendingTitles = await page.locator('#body td:nth-child(3)').allTextContents();
        assert.deepEqual(descendingTitles, [...descendingTitles].sort((first, second) => second.localeCompare(first, undefined, {numeric: true})));
        await page.keyboard.press('/');
        assert.equal(await page.locator('#q').evaluate(element => element === document.activeElement), true);
        assert.deepEqual(errors, []);
        const manifest = {
            capturedAt: new Date().toISOString(), browser: browser.version(), baseUrl,
            scope: 'Public site only; viewport screenshots, not full-page captures. Mobile is viewport sizing, not touch-device emulation.',
            captures, checks: {totalIdeas: total, minekeaIdeas: modFiltered, emptyStateText, filteredUrl, hashRestored: true, resetRestored: true, ascendingAndDescendingSortVerified: true, slashFocusVerified: true, pageErrors: errors},
        };
        await fs.writeFile(path.join(output, '..', 'manifest.json'), JSON.stringify(manifest, null, 2) + '\n', 'utf8');
        console.log('Saved manifest.json; roadmap interaction checks passed.');
    } finally {
        await browser.close();
    }
}

main().catch(error => {
    console.error(error);
    process.exitCode = 1;
});
