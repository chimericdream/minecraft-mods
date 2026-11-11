const assert = require('node:assert/strict');
const fs = require('node:fs/promises');
const path = require('node:path');
const {chromium} = require(process.env.SITE_PLAYWRIGHT || 'playwright-core');

const dist = path.resolve(__dirname, '../../site/dist');
const output = path.join(__dirname, 'redesign');
const origin = 'https://preview.test';
const guide = 'mods/minekea/guide/01-building-blocks/09-framed-planks/';
const kitchen = 'mods/opus/guide/99-appendices/kitchen-sink/';
const pumpkins = 'mods/all-hallows-steve/guide/01-pumpkins/01-carving-station/';
const shots = [
    ['01-home-desktop', ''],
    ['02-mod-overview-desktop', 'mods/minekea/'],
    ['03-guide-index-desktop', 'mods/minekea/guide/'],
    ['04-guide-callouts-desktop', kitchen + '#callouts'],
    ['05-guide-recipes-desktop', kitchen + '#recipes', 1440, 'light', '.recipe'],
    ['06-guide-images-desktop', pumpkins, 1440, 'light', 'article img'],
    ['07-ideas-index-desktop', 'mods/minekea/ideas/'],
    ['08-idea-detail-desktop', null],
    ['09-roadmap-desktop', 'ideas/'],
    ['10-changelog-desktop', 'changelog/'],
    ['11-home-mobile', '', 390],
    ['12-guide-mobile', guide, 390],
    ['13-roadmap-mobile', 'ideas/', 390],
    ['14-home-dark-desktop', '', 1440, 'dark'],
    ['15-guide-tablet', kitchen, 768, 'light', 'article table'],
    ['16-roadmap-mobile-filters', 'ideas/', 390],
];

async function contextFor(browser, base, javaScriptEnabled = true) {
    const context = await browser.newContext({javaScriptEnabled, reducedMotion: 'reduce'});
    // Serve the output through Playwright so the browser resolves URLs under a real HTTP origin.
    // This also works in environments where loopback sockets or external image hosts are blocked.
    await context.route('**/*', async route => {
        const url = new URL(route.request().url());
        if (url.origin !== origin) return route.abort();
        if (!url.pathname.startsWith(base)) return route.fulfill({status: 404});
        const file = path.resolve(dist, decodeURIComponent(url.pathname.slice(base.length)));
        if (file !== dist && !file.startsWith(dist + path.sep)) return route.fulfill({status: 404});
        try {
            const info = await fs.stat(file);
            if (info.isDirectory() && !url.pathname.endsWith('/')) {
                return route.fulfill({status: 301, headers: {location: url.pathname + '/' + url.search}});
            }
            await route.fulfill({path: info.isDirectory() ? path.join(file, 'index.html') : file});
        } catch {
            await route.fulfill({status: 404});
        }
    });
    return context;
}

async function visit(page, base, route) {
    const response = await page.goto(origin + base + route);
    if (response) assert.equal(response.status(), 200, route);
    assert.equal(page.url(), origin + base + route);
}

async function checkInteractions(page, base) {
    await visit(page, base, '');
    await page.keyboard.press('Tab');
    assert.equal(await page.evaluate(() => document.activeElement.textContent), 'Skip to content');
    await page.keyboard.press('Enter');
    assert.equal(await page.evaluate(() => document.activeElement.id), 'main');
    const search = page.locator('#catalog-search');
    const cards = page.locator('.card:visible');
    assert.equal(await cards.count(), 34);
    await search.fill('minekea');
    assert.equal(await cards.count(), 1);
    await search.fill('unmatchable-9238');
    assert(await page.locator('#catalog-empty').isVisible());
    await page.locator('#catalog-reset').click();
    await page.getByRole('button', {name: 'With a guide'}).press('Space');
    assert.equal(await cards.count(), 19);
    await cards.filter({has: page.getByRole('heading', {name: 'Minekea', exact: true})}).getByRole('link', {name: 'Read guide'}).click();
    assert(new URL(page.url()).pathname.endsWith('/mods/minekea/guide/'));

    await page.locator('#theme-choice').selectOption('dark');
    await visit(page, base, guide);
    assert.equal(await page.locator('html').getAttribute('data-theme'), 'dark');
    await page.locator('#theme-choice').selectOption('light');
    await page.emulateMedia({colorScheme: 'dark'});
    assert.equal(await page.evaluate(() => getComputedStyle(document.documentElement).colorScheme), 'light');
    await page.locator('#theme-choice').selectOption('system');
    assert.equal(await page.evaluate(() => getComputedStyle(document.documentElement).colorScheme), 'dark');
    await page.emulateMedia({colorScheme: 'light'});

    await page.setViewportSize({width: 390, height: 844});
    assert.equal(await page.locator('.section-nav').getAttribute('open'), null);
    await page.getByText('Browse this mod', {exact: true}).press('Space');
    assert(await page.locator('.section-nav').getAttribute('open') !== null);
    assert(await page.locator('.section-nav a.cur').last().isVisible());
    await page.getByText('Browse this mod', {exact: true}).press('Space');
    await page.getByRole('navigation', {name: 'Guide pages'}).getByRole('link', {name: /Next/}).click();
    assert(!new URL(page.url()).pathname.endsWith(guide));
    await visit(page, base, kitchen + '#recipes');
    assert(await page.locator('#recipes').isVisible());
    assert(await page.locator('.recipe-result strong').count() > 0);
    await page.locator('.toc summary').press('Space');
    await page.locator('.toc a[href="#callouts"]').click();
    assert.equal(new URL(page.url()).hash, '#callouts');
    assert(await page.locator('#callouts').evaluate(heading => heading.getBoundingClientRect().top > document.querySelector('.top').offsetHeight));
    await visit(page, base, pumpkins);
    await page.locator('article img').first().scrollIntoViewIfNeeded();
    await page.locator('article img').first().evaluate(image => image.decode());

    await visit(page, base, 'ideas/');
    const ideas = page.locator('.idea-card:visible');
    assert.equal(await ideas.count(), 216);
    await page.locator('#mod').selectOption('minekea');
    assert.equal(await ideas.count(), 19);
    const theme = await page.locator('#theme option').nth(1).getAttribute('value');
    await page.locator('#theme').selectOption(theme);
    assert(await ideas.count() > 0 && await ideas.count() < 19);
    await page.locator('#q').fill('unmatchable-9238');
    assert.equal(await ideas.count(), 0);
    assert(await page.getByRole('heading', {name: 'No matching ideas'}).isVisible());
    await page.reload();
    assert.equal(await page.locator('#q').inputValue(), 'unmatchable-9238');
    assert.equal(await page.locator('#mod').inputValue(), 'minekea');
    assert.equal(await page.locator('#theme').inputValue(), theme);
    await page.locator('#reset').press('Enter');
    assert.equal(await ideas.count(), 216);
    assert.equal(new URL(page.url()).hash, '');
    await page.locator('.advanced summary').press('Space');
    for (const key of ['effort', 'value', 'vote', 'status', 'tier']) {
        const pill = page.locator('#f-' + key + ' button').first();
        await pill.press('Space');
        assert.equal(await pill.getAttribute('aria-pressed'), 'true');
        assert(await ideas.count() > 0 && await ideas.count() < 216);
        assert(new URLSearchParams(new URL(page.url()).hash.slice(1)).has(key));
        await pill.press('Space');
    }
    await page.locator('#sort').selectOption('title');
    for (const direction of [1, -1]) {
        const titles = await ideas.locator('h2').allTextContents();
        assert.deepEqual(titles, [...titles].sort((a, b) => direction * a.localeCompare(b, undefined, {numeric: true})));
        if (direction === 1) await page.locator('#direction').press('Space');
    }
    await page.reload();
    assert.equal(await page.locator('#direction').textContent(), 'Descending');
    await page.locator('h1').click();
    await page.keyboard.press('/');
    assert.equal(await page.evaluate(() => document.activeElement.id), 'q');
    await ideas.first().locator('h2 a').click();
    assert(new URL(page.url()).pathname.startsWith(base + 'mods/'));
}

async function main() {
    await fs.mkdir(output, {recursive: true});
    const browser = await chromium.launch({executablePath: process.env.SITE_CHROMIUM});
    try {
        const captures = [];
        const base = '/minecraft-mods/';
        const context = await contextFor(browser, base);
        const page = await context.newPage();
        const errors = [];
        const missing = new Set();
        page.on('pageerror', error => errors.push(error.message));
        page.on('response', response => {
            if (response.url().startsWith(origin) && response.status() >= 400) missing.add(response.url());
        });
        for (const [name, route, width = 1440, colorScheme = 'light', scroll] of shots) {
            const height = width === 390 ? 844 : 1000;
            await page.setViewportSize({width, height});
            await page.emulateMedia({colorScheme});
            await visit(page, base, route ?? 'ideas/');
            if (route === null) await page.locator('.idea-card h2 a').first().click();
            if (scroll) {
                const target = page.locator(scroll).first();
                await target.evaluate(node => node.scrollIntoView({block: 'center'}));
                if (scroll.endsWith('img')) await target.evaluate(node => node.decode());
            }
            if (name.endsWith('mobile-filters')) {
                await page.locator('.advanced summary').press('Space');
                await page.locator('#f-status button').first().press('Space');
                await page.locator('.advanced').evaluate(node => node.scrollIntoView({block: 'start'}));
            }
            const metrics = await page.evaluate(() => ({
                width: document.documentElement.scrollWidth,
                scrollY: Math.round(scrollY),
                articleTop: document.querySelector('article')?.getBoundingClientRect().top + scrollY || null,
            }));
            assert.equal(metrics.width, width, name + ' overflows');
            const file = name + '.png';
            await page.screenshot({path: path.join(output, file)});
            captures.push({file, url: page.url().slice(origin.length), colorScheme, viewport: {width, height}, ...metrics});
        }
        await checkInteractions(page, base);
        assert.deepEqual(errors, []);
        assert.deepEqual([...missing], []);
        await context.close();

        const fallback = await contextFor(browser, '/', false);
        const withoutJS = await fallback.newPage();
        await withoutJS.setViewportSize({width: 390, height: 844});
        await visit(withoutJS, '/', '');
        assert.equal(await withoutJS.locator('.card:visible').count(), 34);
        await visit(withoutJS, '/', guide);
        await withoutJS.locator('.section-nav summary').first().click();
        assert(await withoutJS.locator('.section-nav a.cur').last().isVisible());
        await visit(withoutJS, '/', 'ideas/');
        assert.equal(await withoutJS.locator('.idea-card:visible').count(), 216);
        await fallback.close();
        await assert.rejects(fs.access(path.join(dist, 'local/index.html')), {code: 'ENOENT'});
        await fs.access(path.join(dist, '.nojekyll'));
        await fs.writeFile(path.join(output, 'manifest.json'), JSON.stringify({
            chromium: browser.version(), captures, browserErrors: errors, missingLocalResponses: [...missing],
            checks: ['catalog', 'navigation', 'theme', 'guide anchor and image', 'roadmap', 'no-JS links', 'mobile overflow', 'public output'],
        }, null, 2) + '\n');
        console.log(`Saved ${captures.length} captures. Browser checks passed.`);
    } finally {
        await browser.close();
    }
}

main().catch(error => {
    console.error(error);
    process.exitCode = 1;
});
