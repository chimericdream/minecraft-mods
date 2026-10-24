import {existsSync, readFileSync} from 'node:fs';
import path from 'node:path';

import type {Doc, DocKey} from './docs.ts';
import {ARROW_SVG, modLinksList, shell, type ModLinks} from './layout.ts';
import type {Texture} from './textures.ts';
import type {ModInfo} from './types.ts';
import {esc, relUrl, REPO_ROOT, REPO_URL} from './util.ts';

const initials = (name: string) =>
    name.split(/[\s-]+/).filter(Boolean).slice(0, 2).map(w => w[0]!.toUpperCase()).join('');

/** Site-relative location of a mod's copied icon. */
export const iconUrl = (mod: ModInfo) => `mods/${mod.folder}/icon.png`;

const iconHtml = (mod: ModInfo, from: string, size = 48) => mod.iconFile
    ? `<img class="icon" src="${esc(path.posix.relative(from, iconUrl(mod)))}" alt="" width="${size}" height="${size}" loading="lazy">`
    : `<div class="icon ph" aria-hidden="true">${esc(initials(mod.name))}</div>`;

const badges = (mod: ModInfo) => [
    mod.isNewMod ? '<span class="badge new">New</span>' : '',
    mod.isPrerelease ? '<span class="badge beta">Pre-release</span>' : '',
    mod.hasUnreleasedChanges && !mod.isNewMod ? '<span class="badge unrel" title="The changelog has unreleased changes">Changes on the way</span>' : '',
].join('');

/** The README's italic one-liner (`_Upgrade your hoppers!_`), the author's own pitch for the mod. */
const tagline = (mod: ModInfo): string => {
    const file = path.join(REPO_ROOT, mod.folder, 'README.md');
    if (!existsSync(file)) return mod.description;
    return readFileSync(file, 'utf8').match(/^_([^_\n]+)_\s*$/m)?.[1]?.trim() ?? mod.description;
};

const findText = (mod: ModInfo) => `${mod.name} ${mod.description}`.toLowerCase();

/** Four mods that show the range, from the biggest to the strangest. `picture` is relative to the mod's assets. */
export const FEATURED: {folder: string; area: string; label: string; picture?: string}[] = [
    {folder: 'minekea', area: 'mk', label: 'The big one'},
    {folder: 'hopper-xtreme', area: 'hx', label: 'The useful one', picture: 'textures/gui/guide/hopper_with_items.png'},
    {folder: 'effective-gear', area: 'eg', label: 'The small one'},
    {folder: 'camel-nostrils', area: 'cn', label: 'The weird one'},
];

const HOME_CSS = `
.home{padding:var(--gap) var(--margin) 0;display:grid;gap:var(--gap)}
.hero{min-height:calc(100svh - 16px);grid-template-rows:auto auto 1fr}
.band{grid-column:1/-1;overflow:hidden;padding:clamp(6px,1vw,16px) 0 clamp(8px,1.2vw,18px);background:var(--night);color:var(--on-night)}
.band h1{margin:0;font-size:var(--step-display);line-height:.8;letter-spacing:-.045em;white-space:nowrap;text-align:center}
.hero .navs{grid-column:1/-1;display:grid;grid-template-columns:repeat(4,minmax(0,1fr)) auto;gap:var(--gap)}
.hero .navs .tile{min-height:64px;padding:18px 22px}
.hero .navs .theme{min-height:64px;padding:0 22px}
.rail-l{grid-column:1/4;display:grid;grid-template-rows:auto 1fr;gap:var(--gap)}
.centre{grid-column:4/11;display:grid;grid-template-rows:auto 1fr;gap:var(--gap)}
.rail-r{grid-column:11/13;display:grid;grid-template-rows:repeat(4,1fr) 1.6fr;gap:var(--gap)}
.rail-r .tile{font-size:var(--step-small);padding:16px 18px}
.rail-r p{margin:0;font:800 var(--step-lead)/1.05 var(--display)}
.rail-r a.tile{display:flex;flex-direction:column;justify-content:space-between;text-decoration:none;font:800 var(--step-lead)/1.05 var(--display)}
.rail-r a.tile svg{align-self:flex-end;width:40px;height:40px;transform:rotate(90deg);transition:transform .25s var(--ease)}
.rail-r a.tile:hover svg{transform:rotate(90deg) translate(4px,-4px)}
.plate{background:var(--night);display:grid;place-items:center;overflow:hidden;min-height:340px;padding:0}
.plate img{width:auto;height:100%;max-height:62svh;aspect-ratio:1;animation:drift 18s var(--ease) infinite alternate}
@keyframes drift{from{transform:scale(1)}to{transform:scale(1.06) translateY(-1.5%)}}
.statement p{margin:0;font-size:var(--step-lead);line-height:1.25;max-width:46ch}
.finder label{display:block;font:800 var(--step-lead)/1 var(--display);margin:0 0 12px}
.finder input{width:100%;font:inherit;font-size:var(--step-body);padding:12px 14px;min-height:48px;background:var(--bg);color:var(--fg);border:2px solid var(--fg);border-radius:0}
.finder input:focus-visible{outline-offset:0}
.finder ul{list-style:none;margin:10px 0 0;padding:0;display:grid;gap:4px}
.finder li a{display:flex;gap:10px;align-items:center;padding:6px 8px;background:var(--bg);color:var(--fg);text-decoration:none;font-weight:600}
.finder li a:hover,.finder li a:focus-visible{background:var(--gold);color:var(--on-gold)}
.finder li img,.finder li .ph{width:28px;height:28px;font-size:13px}
.finder .none{color:var(--muted);font-size:var(--step-small);margin:10px 0 0}
.finder .hint{font-size:var(--step-small);color:var(--muted);margin:10px 0 0}
.counts ul{list-style:none;margin:0;padding:0;display:grid;gap:14px}
.counts li{font:800 var(--step-lead)/1.1 var(--display)}

.marquee{background:var(--gold);color:var(--on-gold);overflow:hidden;padding:clamp(12px,1.6vw,24px) 0}
.marquee .track{display:flex;width:max-content;animation:slide 90s linear infinite}
.marquee:hover .track{animation-play-state:paused}
.marquee span{font:800 var(--step-display)/.9 var(--display);letter-spacing:-.03em;padding-right:.6em;white-space:nowrap}
.marquee span:nth-child(even){color:var(--crimson)}
@keyframes slide{to{transform:translateX(-50%)}}

.feat{grid-template-columns:repeat(12,minmax(0,1fr));grid-template-areas:"hd hd hd hd mk mk mk mk mk mk mk mk" "hx hx hx hx mk mk mk mk mk mk mk mk" "eg eg eg eg eg eg cn cn cn cn cn cn"}
.feat .hd{grid-area:hd;display:flex;flex-direction:column;justify-content:space-between;gap:24px}
.feat h2{font-size:var(--step-head);margin:0}
.feat .hd p{margin:0;font-size:var(--step-lead);line-height:1.3}
.card{display:flex;flex-direction:column;gap:14px}
.card h3{font-size:var(--step-head);margin:0;line-height:.9}
.card h3 a{color:inherit;text-decoration:none}.card h3 a:hover{text-decoration:underline}
.card .label{font:800 var(--step-lead)/1 var(--display);color:var(--gilt);margin:0}
.card.crimson .label,.card.gold .label{color:inherit;opacity:.85}
.card .say{margin:0;font-size:var(--step-lead);line-height:1.3;max-width:36ch}
.card .meta{margin:0;font-size:var(--step-small)}
.card .go{margin-top:auto;display:flex;gap:8px;flex-wrap:wrap}
.card .go a{display:inline-block;padding:10px 16px;min-height:44px;background:var(--bg);color:var(--fg);text-decoration:none;font-weight:600;transition:background .15s,color .15s,transform .15s}
.card .go a:hover{background:var(--fg);color:var(--bg)}.card .go a:active{transform:scale(.97)}
.card .go a.primary{background:var(--crimson);color:var(--on-crimson)}
.card.crimson .go a.primary{background:var(--on-crimson);color:var(--crimson)}
.mosaic{display:grid;grid-template-columns:repeat(8,1fr);gap:4px;margin:calc(-1 * clamp(18px,1.8vw,24px)) calc(-1 * clamp(18px,1.8vw,24px)) 8px;background:var(--tile)}
.mosaic span{aspect-ratio:1;background:var(--bg);display:grid;place-items:center;overflow:hidden}
.mosaic img{width:62%;height:auto;image-rendering:pixelated;transition:transform .3s var(--ease)}
.mosaic span:hover img{transform:scale(1.25)}
.mosaic span.big{grid-column:span 2;grid-row:span 2}.mosaic span.big img{width:84%}
.shot{margin:calc(-1 * clamp(18px,1.8vw,24px)) calc(-1 * clamp(18px,1.8vw,24px)) 8px;aspect-ratio:4/3;overflow:hidden;background:#c6c6c6}
.shot img{width:125%;max-width:none;height:auto;image-rendering:pixelated;display:block}
.card.wide{display:grid;grid-template-columns:minmax(160px,38%) minmax(0,1fr);gap:14px 28px;align-content:start}
.card.wide>.art{grid-row:1/span 6}.card.wide>*:not(.art){grid-column:2}
.art{display:flex;gap:12px;flex-direction:column}
.art img{width:100%;max-width:260px;height:auto;image-rendering:pixelated}
.art .row{display:flex;gap:10px}.art .row img{width:48px}

.cat{background:var(--tile);padding:clamp(56px,6vw,96px) clamp(20px,3vw,48px) clamp(40px,4vw,64px)}
.cat-head{display:flex;justify-content:space-between;align-items:flex-end;gap:16px;flex-wrap:wrap;margin:0 0 12px}
.cat-head h2{font-size:var(--step-head);margin:0}
.cat-head p{margin:0;max-width:44ch}
.group{display:flex;justify-content:space-between;align-items:baseline;gap:16px;margin:72px 0 24px}
.group h3{font-size:var(--step-sub);margin:0}
.group span{color:var(--muted)}
.cat ul{list-style:none;margin:0;padding:0;display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:36px 56px}
.cat li{display:grid;grid-template-columns:56px minmax(0,1fr);gap:18px;align-items:start}
.cat li .icon{width:56px;height:56px}
.cat h4{font-size:var(--step-lead);margin:0 0 6px;line-height:1}
.cat h4 a{color:var(--fg);text-decoration:none}.cat h4 a:hover{text-decoration:underline;color:var(--accent)}
.cat h4 .ver{margin-left:6px}
.cat li p{margin:0 0 6px}
.cat li nav{display:flex;gap:16px;font-size:var(--step-small)}
.cat li .badge{margin:0 0 0 8px}
.cat [hidden]{display:none}

.need{grid-template-columns:repeat(12,minmax(0,1fr))}
.need .hd{grid-column:1/-1;padding-bottom:8px}
.need h2{font-size:var(--step-head);margin:0 0 20px}
.need .hd p{margin:0;font-size:var(--step-lead);line-height:1.3;max-width:52ch}
.libs{grid-column:1/9}
.libs ul{list-style:none;margin:0;padding:0;display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:28px 40px}
.libs h3{font-size:var(--step-lead);margin:0 0 6px}
.libs h3 a{color:inherit}
.libs p{margin:0}
.plat{grid-column:9/13;list-style:none;margin:0;display:grid;align-content:start;gap:22px}
.plat li{font:800 var(--step-lead)/1.1 var(--display)}

@media (max-width:1100px){
.rail-l{grid-column:1/5}.centre{grid-column:5/13}.rail-r{grid-column:1/-1;grid-template-columns:repeat(4,1fr) 1.6fr;grid-template-rows:auto}
.feat{grid-template-areas:"hd hd hd hd hd hd hd hd hd hd hd hd" "mk mk mk mk mk mk mk mk mk mk mk mk" "hx hx hx hx hx hx eg eg eg eg eg eg" "cn cn cn cn cn cn cn cn cn cn cn cn"}
.libs,.plat{grid-column:1/-1}
}
@media (max-width:760px){
.hero{min-height:0}
.card.wide{display:flex}
.hero .navs{grid-template-columns:1fr 1fr}.hero .navs .theme{grid-column:1/-1;min-height:48px}
.rail-l,.centre{grid-column:1/-1}
.centre{order:-1;grid-row:3}.centre .plate{order:2}
.plate{min-height:0;aspect-ratio:4/3}.plate img{height:100%;max-height:none}
.rail-r{grid-template-columns:1fr 1fr;grid-template-rows:auto}.rail-r a.tile{grid-column:1/-1;min-height:96px}
.feat{grid-template-areas:"hd" "mk" "hx" "eg" "cn";grid-template-columns:minmax(0,1fr)}
.mosaic{grid-template-columns:repeat(6,1fr)}.mosaic span:nth-child(n+19){display:none}
.cat ul{grid-template-columns:minmax(0,1fr)}
.cat li{grid-template-columns:44px minmax(0,1fr);gap:14px}.cat li .icon{width:44px;height:44px}
.libs ul{grid-template-columns:minmax(0,1fr)}
.group{margin-top:48px}
}
`;

const FINDER_JS = `(()=>{
const box=document.getElementById('find'),out=document.getElementById('find-out');if(!box)return;
const items=[...document.querySelectorAll('[data-find]')].map(el=>{const a=el.querySelector('h3 a,h4 a');
  return {text:el.dataset.find,name:a.textContent,href:a.getAttribute('href'),icon:el.querySelector('.icon')}});
const seen=new Set(),list=items.filter(i=>!seen.has(i.href)&&seen.add(i.href));
const hint=document.querySelector('.finder .hint');
box.addEventListener('input',()=>{
  const words=box.value.toLowerCase().split(/\\s+/).filter(Boolean);out.innerHTML='';
  if(hint)hint.hidden=words.length>0;
  if(!words.length)return;
  const hits=list.filter(i=>words.every(w=>i.text.includes(w))).sort((a,b)=>(b.name.toLowerCase().startsWith(words[0]))-(a.name.toLowerCase().startsWith(words[0]))).slice(0,6);
  if(!hits.length){out.innerHTML='<li class="none">No mod matches that. Try a block or item name.</li>';return}
  hits.forEach(h=>{const li=document.createElement('li'),a=document.createElement('a');a.href=h.href;
    if(h.icon)a.appendChild(h.icon.cloneNode(true));a.appendChild(document.createTextNode(h.name));li.appendChild(a);out.appendChild(li)});
});
box.form.addEventListener('submit',e=>{const first=out.querySelector('a');if(first){e.preventDefault();location.href=first.href}});
box.addEventListener('keydown',e=>{if(e.key==='ArrowDown'){const f=out.querySelector('a');if(f){e.preventDefault();f.focus()}}});
out.addEventListener('keydown',e=>{const a=[...out.querySelectorAll('a')],i=a.indexOf(document.activeElement);
  if(e.key==='ArrowDown'&&a[i+1]){e.preventDefault();a[i+1].focus()}
  if(e.key==='ArrowUp'){e.preventDefault();(a[i-1]||box).focus()}});
document.addEventListener('keydown',e=>{if(e.key==='/'&&!/INPUT|TEXTAREA/.test(document.activeElement.tagName)){e.preventDefault();box.focus()}});
})();`;

/** Libraries a player may need, with where to get them. Counts come from each mod's fabric.mod.json. */
const LIBS: {name: string; href: string; what: string}[] = [
    {name: 'Architectury API', href: 'https://www.curseforge.com/minecraft/mc-mods/architectury-api', what: 'Lets one mod run on both Fabric and NeoForge.'},
    {name: 'ChimericLib', href: 'mods/chimeric-lib/', what: 'Shared code for these mods. Small, and it changes rarely.'},
    {name: 'YACL', href: 'https://modrinth.com/mod/yacl', what: 'Draws the settings screens.'},
    {name: 'Fabric API', href: 'https://modrinth.com/mod/fabric-api', what: 'Needed on Fabric only. NeoForge has it built in.'},
    {name: 'Opus', href: 'mods/opus/', what: 'Optional. Turns each mod\'s guide into a book you can read in game.'},
    {name: 'Mod Menu', href: 'https://modrinth.com/mod/modmenu', what: 'Optional, Fabric only. Opens the settings from the mod list.'},
];

export type HomeData = {
    mods: ModInfo[];
    links: Map<string, ModLinks>;
    textures: Map<string, Texture[]>;
    pictures: Map<string, string>;
    guides: number;
    ideas: number;
};

const month = (iso: string) => new Date(iso).toLocaleDateString('en-GB', {month: 'long', year: 'numeric'});

export const homePage = ({mods, links, textures, pictures, guides, ideas}: HomeData): string => {
    const byFolder = new Map(mods.map(m => [m.folder, m]));
    const featured = FEATURED.filter(f => byFolder.has(f.folder));
    const rest = mods.filter(m => !featured.some(f => f.folder === m.folder));
    const mc = mods[0]?.mcCompat ?? '';
    const latest = mods.map(m => m.lastCommit).filter((d): d is string => !!d).sort().at(-1);

    const go = (mod: ModInfo) => {
        const l = links.get(mod.folder)!;
        return [
            `<a class="primary" href="${esc(l.overview)}">About ${esc(mod.name)}</a>`,
            l.guide ? `<a href="${esc(l.guide)}">Read the guide</a>` : '',
            l.changelog ? `<a href="${esc(l.changelog)}">Changelog</a>` : '',
        ].join('');
    };

    const featureCard = (f: typeof FEATURED[number], i: number) => {
        const mod = byFolder.get(f.folder)!;
        const tex = textures.get(mod.folder) ?? [];
        const pic = pictures.get(mod.folder);
        const tone = f.area === 'cn' ? 'crimson' : f.area === 'eg' ? 'gold' : '';
        let art = '';
        if (f.area === 'mk' && tex.length) {
            art = `<div class="mosaic" aria-hidden="true">${tex.slice(0, 21).map((t, j) => `<span${j === 0 ? ' class="big"' : ''}><img src="${esc(t.url)}" alt="" width="16" height="16" loading="lazy"></span>`).join('')}</div>`;
        } else if (pic) {
            art = `<div class="shot"><img src="${esc(pic)}" alt="The ${esc(mod.name)} guide's picture of a netherite hopper's inventory, holding redstone, amethyst and string" width="1024" height="1024" loading="lazy"></div>`;
        } else if (mod.iconFile) {
            const row = f.area === 'cn' ? `<div class="row">${tex.slice(0, 4).map(t => `<img src="${esc(t.url)}" alt="${esc(t.name)}" title="${esc(t.name)}" width="16" height="16" loading="lazy">`).join('')}</div>` : '';
            art = `<div class="art"><img src="${esc(iconUrl(mod))}" alt="The ${esc(mod.name)} icon" width="260" height="260" loading="lazy">${row}</div>`;
        }
        const wide = f.area === 'eg' || f.area === 'cn' ? ' wide' : '';
        return `<article class="tile card${wide} ${tone} reveal" style="grid-area:${f.area};--i:${i}" data-find="${esc(findText(mod))}">${art}
<p class="label">${esc(f.label)}</p><h3><a href="${esc(links.get(mod.folder)!.overview)}">${esc(mod.name)}</a></h3>
<p class="say">${esc(tagline(mod))}</p><p class="meta">Version ${esc(mod.version)}${badges(mod)}</p><div class="go">${go(mod)}</div></article>`;
    };

    const entry = (mod: ModInfo) => {
        const l = links.get(mod.folder)!;
        const nav = [['Guide', l.guide], ['Changelog', l.changelog], ['Ideas', l.ideas]]
            .filter(([, url]) => url).map(([label, url]) => `<a href="${esc(url!)}">${label}<span class="sr-only"> for ${esc(mod.name)}</span></a>`).join('');
        return `<li class="reveal" data-find="${esc(findText(mod))}">${iconHtml(mod, '', 56)}<div>
<h4><a href="${esc(l.overview)}">${esc(mod.name)}</a><span class="ver">${esc(mod.version)}</span></h4>
<p>${esc(mod.description)}${badges(mod)}</p><nav aria-label="${esc(mod.name)}">${nav}</nav></div></li>`;
    };
    const testing = rest.filter(m => m.isPrerelease || m.isNewMod);
    const released = rest.filter(m => !testing.includes(m));
    const group = (title: string, list: ModInfo[]) => list.length ? `<div class="group"><h3>${title}</h3><span>${list.length} mods</span></div><ul>${list.map(entry).join('')}</ul>` : '';

    const count = (name: string, field: 'requires' | 'recommends') => mods.filter(m => m[field].includes(name)).length;
    const libs = LIBS.map(lib => {
        const req = count(lib.name, 'requires');
        const rec = count(lib.name, 'recommends');
        const n = [req ? `required by ${req}` : '', rec ? `optional for ${rec}` : ''].filter(Boolean).join(', ');
        return `<li><h3><a href="${esc(lib.href)}">${esc(lib.name)}</a></h3><p>${esc(lib.what)}${n ? ` ${n[0]!.toUpperCase() + n.slice(1)} of the ${mods.length} mods.` : ''}</p></li>`;
    }).join('');

    const names = mods.map(m => `<span>${esc(m.name)}</span>`).join('');
    const unreleased = mods.filter(m => m.hasUnreleasedChanges).length;
    const arrow = ARROW_SVG;

    return shell({
        title: 'chimericdream · Minecraft mods for Fabric and NeoForge',
        description: `${mods.length} Minecraft mods for Fabric and NeoForge: furniture, storage, building blocks, small fixes and a few strange ones.`,
        url: '',
        bare: true,
        head: `<style>${HOME_CSS}</style>`,
        body: `<section class="board hero" aria-label="Introduction">
<div class="band"><h1>chimericdream</h1></div>
<nav class="navs" aria-label="Site"><a class="tile link" href="#catalogue">All ${mods.length} mods</a><a class="tile link" href="ideas/">Roadmap</a><a class="tile link" href="changelog/">Changelog</a><a class="tile link" href="${REPO_URL}">Source on GitHub</a><button type="button" class="theme" data-theme-toggle>Theme: Auto</button></nav>
<div class="rail-l"><form class="tile finder" action="#catalogue" role="search"><label for="find">Find a mod</label><input id="find" type="search" autocomplete="off" placeholder="Try “hopper” or “shelf”" aria-describedby="find-hint"><ul id="find-out" aria-live="polite"></ul><p class="hint" id="find-hint">Search by name or by what it adds. Press / to jump here.</p></form>
<div class="tile tone counts"><ul><li>${mods.length} mods in the workshop</li><li>${guides} with an in-game guide</li><li>${ideas} ideas on the roadmap</li><li>${unreleased} with changes on the way</li></ul></div></div>
<div class="centre"><div class="tile statement"><p>Minecraft mods for Fabric and NeoForge: furniture, storage, building blocks, small fixes and a few strange ones.</p></div>
<div class="tile plate"><img src="brand/logo.svg" alt="The chimericdream mark: a crimson dragon draped across two snowy mountains under a crescent moon" width="750" height="750"></div></div>
<div class="rail-r"><div class="tile"><p>For Fabric</p></div><div class="tile tone"><p>And NeoForge</p></div><div class="tile gold"><p>Minecraft ${esc(mc)}</p></div><div class="tile night"><p>${latest ? `Updated ${esc(month(latest))}` : 'Open source'}</p></div>
<a class="tile crimson" href="#catalogue">Browse every mod${arrow}</a></div>
</section>
<div class="marquee" aria-hidden="true"><div class="track">${names}${names}</div></div>
<section class="board feat" aria-labelledby="feat-h"><div class="tile tone hd reveal"><h2 id="feat-h">Four from the workshop</h2><p>Chosen to show the range: the largest mod here, a better hopper, a handful of small fixes, and one that should not exist.</p></div>
${featured.map(featureCard).join('')}</section>
<section class="cat" id="catalogue" aria-labelledby="cat-h"><div class="cat-head"><h2 id="cat-h">The rest of the catalogue</h2><p>Every other mod, A to Z. Each has a page with its features, changelog and ideas, and most have a guide.</p></div>
${group('Released', released)}${group('In testing', testing)}</section>
<section class="board need" aria-labelledby="need-h"><div class="tile hd reveal"><h2 id="need-h">Before you install</h2><p>Every mod runs on Fabric and NeoForge for Minecraft ${esc(mc)}. Most need the same few libraries, so a pack only carries one copy of each.</p></div>
<div class="tile tone libs reveal"><ul>${libs}</ul></div>
<ul class="tile gold plat reveal"><li>Minecraft ${esc(mc)}</li><li>Fabric and NeoForge</li><li>${mods.length} mods</li><li>${guides} in-game guides</li></ul></section>
<script>${FINDER_JS}</script>`,
    });
};

export const overviewPage = (doc: Doc, body: string, links: ModLinks, textures: Texture[], mods: ModInfo[]): string => {
    const mod = doc.mod;
    const rel = (url: string) => esc(relUrl(doc.url, url));
    const q = encodeURIComponent(mod.name);
    const acts = [
        links.guide ? `<a class="tile crimson link" href="${rel(links.guide)}">Read the guide</a>` : '',
        `<a class="tile link" href="https://modrinth.com/mods?q=${q}">Find it on Modrinth</a>`,
        `<a class="tile link" href="https://www.curseforge.com/minecraft/search?search=${q}">Find it on CurseForge</a>`,
        links.changelog ? `<a class="tile link" href="${rel(links.changelog)}">Changelog</a>` : '',
        links.ideas ? `<a class="tile link" href="${rel(links.ideas)}">Ideas</a>` : '',
        `<a class="tile link" href="${REPO_URL}/tree/main/${esc(mod.folder)}">Source</a>`,
    ].filter(Boolean).map((a, i) => a.replace('class="tile', `style="--i:${i + 2}" class="reveal tile`)).join('');
    const plate = mod.iconFile
        ? `<img class="icon" src="${esc(path.posix.relative(doc.url, iconUrl(mod)))}" alt="" width="240" height="240">`
        : `<div class="icon ph" aria-hidden="true">${esc(initials(mod.name))}</div>`;
    const list = (xs: string[]) => xs.length ? xs.join(', ') : 'nothing else';
    const drawer = textures.length ? `<section class="tile drawer reveal" aria-labelledby="drawer-h"><h2 id="drawer-h">From the drawer</h2>
<ul>${textures.map(t => `<li><img src="${esc(path.posix.relative(doc.url, t.url))}" alt="${esc(t.name)}" title="${esc(t.name)}" width="16" height="16" loading="lazy"></li>`).join('')}</ul></section>` : '';
    return shell({
        title: `${mod.name} · chimericdream mods`,
        description: mod.description,
        url: doc.url,
        board: true,
        crumbs: [esc(mod.name)],
        side: `${modLinksList(links, doc.url)}<h2>All mods</h2><ul class="all">${mods.map(m => m === mod ? `<li><a class="cur" aria-current="page" href="./">${esc(m.name)}</a></li>` : `<li><a href="${rel(`mods/${m.folder}/`)}">${esc(m.name)}</a></li>`).join('')}</ul>`,
        body: `<div class="board modhead"><div class="tile name reveal"><h1>${esc(mod.name)}</h1><p class="say">${esc(mod.description)}</p>
<p class="ver">Version ${esc(mod.version)}${badges(mod)}</p></div><div class="tile night plate reveal" style="--i:1">${plate}</div>
<div class="acts">${acts}</div>
<div class="tile gold facts reveal" style="--i:2"><p>For Minecraft <b>${esc(mod.mcCompat)}</b> on <b>Fabric</b> and <b>NeoForge</b></p>
<p>Needs <b>${esc(list(mod.requires))}</b></p><p>Optional: <b>${esc(list(mod.recommends))}</b></p></div>
${drawer}</div>
<div class="tile readme reveal"><article class="prose">${body}</article><p class="edit-link"><a href="${REPO_URL}/blob/main/${esc(doc.key)}">View this page's source on GitHub</a></p></div>`,
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
        `<section class="feed-section"><h3>${link(e.mod)}</h3><div class="prose">${render(e.unreleased!.body, e.key)}</div></section>`).join('');
    const shipped = entries.filter(e => e.latest).map(e =>
        `<section class="feed-section"><h3>${link(e.mod)} <span class="ver">${esc(e.latest!.heading)}</span></h3><div class="prose">${render(e.latest!.body, e.key)}</div></section>`).join('');

    return shell({
        title: 'Changelog · chimericdream mods',
        url,
        board: true,
        body: `<div class="feed"><div class="feed-head"><h1>Changelog</h1><p>What is being worked on now, then the latest release of every mod. Each mod's page has its full history.</p></div>
<h2>In development</h2>${upcoming || '<p class="feed-head muted">Nothing unreleased right now.</p>'}
<h2>Latest releases</h2>${shipped}</div>`,
    });
};
