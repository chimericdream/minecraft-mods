import {existsSync, readFileSync} from 'node:fs';
import path from 'node:path';

import type {Doc, DocKey} from './docs.ts';
import {modLinksList, shell, type ModLinks} from './layout.ts';
import {assetUrl, creditLines, PICTURE, VIDEO} from './media.ts';
import type {ModInfo} from './types.ts';
import {esc, relUrl, REPO_ROOT, REPO_URL} from './util.ts';

const initials = (name: string) =>
    name.split(/[\s-]+/).filter(Boolean).slice(0, 2).map(w => w[0]!.toUpperCase()).join('');

const hue = (s: string) => [...s].reduce((h, c) => (h * 31 + c.charCodeAt(0)) % 360, 7);

/** Site-relative location of a mod's copied icon. */
export const iconUrl = (mod: ModInfo) => `mods/${mod.folder}/icon.png`;

const iconHtml = (mod: ModInfo, from: string, cls = 'icon') => mod.iconFile
    ? `<img class="${cls}" src="${esc(path.posix.relative(from, iconUrl(mod)))}" alt="" width="48" height="48">`
    : `<div class="${cls} ph" style="--h:${hue(mod.id)}" aria-hidden="true">${esc(initials(mod.name))}</div>`;

const badges = (mod: ModInfo) => [
    mod.isNewMod ? '<span class="badge new">new</span>' : '',
    mod.isPrerelease ? '<span class="badge beta">pre-release</span>' : '',
    mod.hasUnreleasedChanges ? '<span class="badge unrel" title="The changelog has unreleased changes">unreleased changes</span>' : '',
].filter(Boolean).join(' ');

/** The four mods the home page features, in the order they are shown. Each gets its own frame. */
const FEATURED: {folder: string; picture: string; video?: boolean}[] = [
    {folder: 'minekea', picture: PICTURE.bandsaw},
    {folder: 'hopper-xtreme', picture: PICTURE.cassone},
    {folder: 'effective-gear', picture: '', video: true},
    {folder: 'camel-nostrils', picture: PICTURE.scroll},
];

const frame = (mod: ModInfo, l: ModLinks, col: number, feature: (typeof FEATURED)[number]): string => {
    const media = feature.video
        ? `<video class="bg" autoplay muted loop playsinline preload="metadata" poster="${assetUrl('', VIDEO.poster)}" aria-hidden="true"><source src="${assetUrl('', VIDEO.forge)}" type="video/webm"></video>`
        : `<img class="bg" src="${assetUrl('', feature.picture)}" alt="" width="1920" height="1257" decoding="async">`;
    const thumb = mod.iconFile ? `<img class="thumb" src="mods/${esc(mod.folder)}/icon.png" alt="" width="96" height="96" loading="lazy">` : '';
    const guide = l.guide ? `<a class="more" href="${esc(l.guide)}">Read the guide</a>` : '';
    return `<article class="frame">${media}
<div class="frame-card reveal" style="--col:${col}">${thumb}
<h2><a href="${esc(l.overview)}">${esc(mod.name)}</a></h2>
<p>${esc(mod.description)}</p>
<p class="fact">Version ${esc(mod.version)}${badges(mod) ? ` · ${badges(mod)}` : ''}</p>
<div class="foot"><a class="btn" href="${esc(l.overview)}">Open ${esc(mod.name)}</a>${guide}</div>
</div></article>`;
};

const lineRow = (mod: ModInfo, l: ModLinks): string => {
    const links = [
        l.guide ? `<a href="${esc(l.guide)}">Guide</a>` : '',
        l.changelog ? `<a href="${esc(l.changelog)}">Changelog</a>` : '',
        l.ideas ? `<a href="${esc(l.ideas)}">Ideas</a>` : '',
    ].join('');
    const q = `${mod.name} ${mod.description}`.toLowerCase().replace(/"/g, '&quot;');
    return `<tr data-q="${q}"><td class="name"><a href="${esc(l.overview)}">${esc(mod.name)}</a></td>
<td class="ver hide-s">${esc(mod.version)}</td><td>${badges(mod)}</td><td class="links">${links}</td></tr>`;
};

/** Home page: the mod directory, with four mods featured, a live filter and how the pages are made. */
export const homePage = (mods: ModInfo[], links: Map<string, ModLinks>): string => {
    const byFolder = new Map(mods.map(m => [m.folder, m]));
    const featured = FEATURED.map((f, i) => ({mod: byFolder.get(f.folder)!, feature: f, col: i % 2 === 0 ? 1 : 3}));
    const rest = mods.filter(m => !FEATURED.some(f => f.folder === m.folder));
    const footerLinks = [['Mods', '#mods'], ['Roadmap', 'ideas/'], ['Changelog', 'changelog/']]
        .map(([label, href]) => `<li><a href="${esc(href)}">${label}</a></li>`).join('');
    const credits = creditLines().map(c => `<p>${esc(c)}</p>`).join('');

    return shell({
        title: 'Chimeric Mods · Minecraft mods for Fabric and NeoForge',
        url: '',
        bare: true,
        body: `<section class="hero" aria-label="Introduction">
<figure class="print print-large" aria-label="Engravings of alchemists' laboratories, shown in turn">
<img class="slide" src="${assetUrl('', PICTURE.lab)}" alt="Engraving of an alchemist's laboratory, with flasks, a furnace and a figure at a table" width="1920" height="2000">
<img class="slide" src="${assetUrl('', PICTURE.distillatio)}" alt="Engraving of an alchemist at work in a laboratory" width="1920" height="1438">
</figure>
<figure class="print print-small"><img src="${assetUrl('', PICTURE.chest)}" alt="A chest from the Metropolitan Museum's collection, against a plain ground" width="1276" height="1300"></figure>
<div class="hero-text">
<h1>Minecraft mods, each with a guide</h1>
<p>Chimeric Mods makes small, practical mods for Fabric and NeoForge. Each mod's README, changelog, in-game guide and ideas are published here as they are written.</p>
<a class="btn" href="#mods">Find a mod</a>
</div>
</section>

<section class="frames" aria-label="Four mods to start with">
${featured.map(({mod, feature, col}) => frame(mod, links.get(mod.folder)!, col, feature)).join('\n')}
</section>

<section class="lineup-section" id="mods" aria-labelledby="lineup-h">
<div class="lineup-head reveal">
<h2 id="lineup-h">The other ${rest.length} mods</h2>
<p>Every mod has an overview, a changelog and, where it exists, an in-game guide and its ideas.</p>
<div class="filter"><label for="mod-filter">Filter by name or description</label><input id="mod-filter" type="search" autocomplete="off" placeholder="Furniture, storage, armour…"></div>
<p class="count" id="mod-count" aria-live="polite"></p>
</div>
<table class="lineup-table">
<caption>Mods, with their version and the pages each one has</caption>
<thead><tr><th scope="col">Mod</th><th scope="col" class="hide-s">Version</th><th scope="col">Status</th><th scope="col">Read</th></tr></thead>
<tbody id="mod-rows">${rest.map(m => lineRow(m, links.get(m.folder)!)).join('\n')}</tbody>
</table>
<p class="none" id="mod-none" hidden>No mod matches that filter. Clear it to see all of them.</p>
</section>
<script>
(function(){
  var input=document.getElementById('mod-filter');
  var rows=[].slice.call(document.querySelectorAll('#mod-rows tr'));
  var count=document.getElementById('mod-count');
  var none=document.getElementById('mod-none');
  function apply(){
    var q=input.value.trim().toLowerCase(),shown=0;
    rows.forEach(function(r){var m=!q||r.dataset.q.indexOf(q)!==-1;r.hidden=!m;if(m)shown++});
    count.textContent=q?shown+' of '+rows.length+' shown':'';
    none.hidden=shown!==0;
  }
  input.addEventListener('input',apply);
})();
</script>

<section class="how" aria-labelledby="how-h">
<h2 id="how-h" class="reveal">Where the pages come from</h2>
<div class="stage stage-1">
<figure class="reveal"><img src="${assetUrl('', PICTURE.distillatio)}" alt="Engraving of an alchemist's laboratory" width="1920" height="1438" loading="lazy" decoding="async"></figure>
<div class="text reveal"><h3>Written beside the code</h3><p>Each mod keeps its README, changelog, ideas and in-game guide as plain Markdown in its own folder. Nothing on this site is typed in by hand.</p></div>
</div>
<div class="stage stage-2">
<figure class="reveal"><img src="${assetUrl('', PICTURE.anvil)}" alt="A blacksmith working at an anvil" width="1479" height="1206" loading="lazy" decoding="async"></figure>
<div class="text reveal"><h3>Built on every change</h3><p>One command, <code>bun run site</code>, reads those files and writes every page: the overviews, the guides, the changelog and the roadmap.</p></div>
</div>
<div class="stage stage-3">
<figure class="reveal"><img src="${assetUrl('', PICTURE.scroll)}" alt="A hieroglyphic scroll from an alchemical manuscript" width="1920" height="2474" loading="lazy" decoding="async"></figure>
<div class="text reveal"><h3>Read before you install</h3><p>Each in-game guide is published here page by page, so you can read how a mod works before it goes into your pack.</p></div>
</div>
</section>

<section class="site-end" aria-labelledby="end-h">
<img class="bg" src="${assetUrl('', PICTURE.mountain)}" alt="A rocky ridge rising out of mist" width="2400" height="1350" loading="lazy" decoding="async">
<div class="end-top">
<nav class="navs" aria-label="Footer"><ul>${footerLinks}</ul></nav>
<div class="contact"><p>Source, issues and ideas are on <a href="${REPO_URL}">GitHub</a>.</p></div>
</div>
<h2 class="wordmark" id="end-h">Chimeric Mods</h2>
<div class="end-legal"><p>Pictures and footage, credited:</p>${credits}</div>
</section>`,
    });
};

export const overviewPage = (doc: Doc, body: string, links: ModLinks): string => {
    const mod = doc.mod;
    const actions = [
        links.guide ? `<a class="btn" href="${esc(relUrl(doc.url, links.guide))}">In-game guide</a>` : '',
        links.changelog ? `<a class="btn-ghost" href="${esc(relUrl(doc.url, links.changelog))}">Changelog</a>` : '',
        links.ideas ? `<a class="btn-ghost" href="${esc(relUrl(doc.url, links.ideas))}">Ideas</a>` : '',
        `<a class="btn-ghost" href="${REPO_URL}/tree/main/${esc(mod.folder)}">Source</a>`,
    ].join('');
    return shell({
        title: `${mod.name} · Chimeric Mods`,
        url: doc.url,
        crumbs: [esc(mod.name)],
        side: modLinksList(links, doc.url),
        body: `<div class="modhead">${iconHtml(mod, doc.url, 'exhibit')}<div><h1>${esc(mod.name)} <span class="ver">${esc(mod.version)}</span></h1>
<p>${esc(mod.description)}</p>${badges(mod) ? `<p class="small">${badges(mod)}</p>` : ''}</div></div>
<div class="actions">${actions}</div>
<article>${body}</article><p class="edit-link"><a href="${REPO_URL}/blob/main/${esc(doc.key)}">View source on GitHub</a></p>`,
    });
};

/** README body without its leading `# Title`, which the overview header replaces. */
export const readmeBody = (source: string): string => source.replace(/^# .*\n+/, '');

type ChangelogSection = {heading: string; body: string};

const parseChangelog = (source: string): ChangelogSection[] =>
    source.replace(/\r\n/g, '\n').split(/^### /m).slice(1).map(chunk => {
        const nl = chunk.indexOf('\n');
        return {heading: chunk.slice(0, nl === -1 ? undefined : nl).trim(), body: nl === -1 ? '' : chunk.slice(nl + 1).trim()};
    });

/** Per-mod changelog pages are plain docs; this is the combined view of what is in progress and what shipped last. */
export const changelogPage = (mods: ModInfo[], render: (source: string, key: DocKey) => string): string => {
    const url = 'changelog/';
    const entries = mods.flatMap(mod => {
        const file = path.join(REPO_ROOT, mod.folder, 'CHANGELOG.md');
        if (!existsSync(file)) return [];
        const sections = parseChangelog(readFileSync(file, 'utf8'));
        const unreleased = sections.find(s => /^unreleased/i.test(s.heading));
        const latest = sections.find(s => !/^unreleased/i.test(s.heading));
        return [{mod, key: `${mod.folder}/CHANGELOG.md`, unreleased: unreleased?.body ? unreleased : undefined, latest}];
    });
    const link = (mod: ModInfo) => `<a href="${esc(relUrl(url, `mods/${mod.folder}/changelog`))}">${esc(mod.name)}</a>`;

    const upcoming = entries.filter(e => e.unreleased).map(e =>
        `<section class="feed-section"><h2>${link(e.mod)}</h2>${render(e.unreleased!.body, e.key)}</section>`).join('');
    const shipped = entries.filter(e => e.latest).map(e =>
        `<section class="feed-section"><h2>${link(e.mod)} <span class="ver">${esc(e.latest!.heading)}</span></h2>${render(e.latest!.body, e.key)}</section>`).join('');

    return shell({
        title: 'Changelog · Chimeric Mods',
        url,
        body: `<article><h1>Changelog</h1>
<h2>In development</h2>${upcoming || '<p class="muted">Nothing unreleased right now.</p>'}
<h2>Latest releases</h2>${shipped}</article>`,
    });
};
