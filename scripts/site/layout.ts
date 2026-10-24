import path from 'node:path';

import type {Guide, GuidePage} from './guide.ts';
import {esc, REPO_URL, relUrl, rootPrefix} from './util.ts';

/*
 * Palette: the avatar's black, snow and crimson, with gold (violet's complement) as the second field colour so the
 * violet stays in the mark. Dark and light each get their own ground; light is the moon-grey of the avatar, not white.
 * `--bg --fg --muted --card --line --accent --good --bad --warn --mark` are shared with the local dashboard.
 */
const LIGHT = '--bg:#e4e5e9;--fg:#17131a;--muted:#4a4552;--card:#f3f4f6;--tile:#f3f4f6;--tile-2:#d6d7dd;--line:#b9b8c2;--accent:#8b1a1a;--crimson:#8b1a1a;--on-crimson:#eceff1;--gold:#c9a24a;--on-gold:#1a1308;--gilt:#6e5010;--night:#0d0b10;--on-night:#eceff1;--good:#1d6b34;--bad:#a3161a;--warn:#7a5200;--mark:#f0d9a6;color-scheme:light';
const DARK = '--bg:#08070a;--fg:#eceff1;--muted:#b3aeb8;--card:#141217;--tile:#141217;--tile-2:#1f1c23;--line:#3a3540;--accent:#f0898b;--crimson:#8b1a1a;--on-crimson:#eceff1;--gold:#c9a24a;--on-gold:#1a1308;--gilt:#dcb862;--night:#000;--on-night:#eceff1;--good:#7bd88f;--bad:#ff8a80;--warn:#e8c46a;--mark:#4a3510;color-scheme:dark';

/** Color tokens shared by every page, including the local-only GitHub panel. */
export const THEME_CSS = `
:root{${LIGHT}}
@media (prefers-color-scheme:dark){:root:not([data-theme="light"]){${DARK}}}
:root[data-theme="dark"]{${DARK}}
`;

const FONTS = 'https://fonts.googleapis.com/css2?family=Grenze:wght@600;800&family=Source+Serif+4:ital,opsz,wght@0,8..60,400;0,8..60,600;1,8..60,400&display=swap';

export const SITE_CSS = `
${THEME_CSS}
:root{
--display:'Grenze',Georgia,serif;--text:'Source Serif 4',Georgia,serif;
--step-small:clamp(.9375rem,.9rem + .1vw,1rem);--step-body:clamp(1.0625rem,1rem + .25vw,1.1875rem);
--step-lead:clamp(1.25rem,1rem + 1vw,1.875rem);--step-sub:clamp(1.75rem,1.3rem + 1.6vw,2.75rem);--step-head:clamp(2rem,1.3rem + 3.2vw,4rem);--step-display:15.6vw;
--gap:8px;--margin:clamp(16px,1.4vw,24px);--ease:cubic-bezier(.2,.7,.2,1)}
*{box-sizing:border-box}
html{-webkit-text-size-adjust:100%}
body{margin:0;background:var(--bg);color:var(--fg);font:400 var(--step-body)/1.55 var(--text);font-optical-sizing:auto}
h1,h2,h3,h4{font-family:var(--display);font-weight:800;line-height:.98;letter-spacing:-.015em;text-wrap:balance}
a{color:var(--accent);text-decoration:underline;text-decoration-thickness:.07em;text-underline-offset:.18em}
a:hover{text-decoration-thickness:.14em}
:focus-visible{outline:3px solid var(--accent);outline-offset:2px}
::selection{background:var(--gold);color:var(--on-gold)}
img{max-width:100%;height:auto}
.px{image-rendering:pixelated}
.sr-only{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap}
.muted{color:var(--muted)}
.skip{position:absolute;left:8px;top:8px;z-index:9;background:var(--crimson);color:var(--on-crimson);padding:8px 14px}.skip:not(:focus){clip-path:inset(50%);width:1px;height:1px;overflow:hidden;padding:0}

/* Bar: wordmark, a few links, the theme switch. */
.top{display:flex;gap:4px 22px;align-items:center;flex-wrap:wrap;padding:10px max(16px,var(--margin));background:var(--night);color:var(--on-night)}
.top a{color:inherit;text-decoration:none}.top a:hover{text-decoration:underline}
.brand{display:flex;align-items:center;gap:10px;font:800 28px/1 var(--display);letter-spacing:-.02em;margin-right:6px}
.brand img{width:36px;height:36px}
.top nav{display:flex;gap:4px 18px;flex-wrap:wrap;font-size:var(--step-small)}
.top .push{margin-left:auto}
.theme{font:inherit;font-size:var(--step-small);cursor:pointer;background:var(--tile-2);color:var(--fg);border:0;padding:8px 14px;min-height:40px;transition:transform .15s var(--ease),background .15s}
.top .theme{background:#2a262e;color:var(--on-night)}
.theme:hover{background:var(--gold);color:var(--on-gold)}.theme:active{transform:scale(.96)}
.crumbs{display:flex;gap:6px;flex-wrap:wrap;font-size:var(--step-small);color:var(--muted);margin:0 0 18px}
.crumbs a{color:inherit}
.boarded .crumbs{padding:0 clamp(18px,1.8vw,24px)}

/* Pages with a sidebar. */
.layout{display:grid;grid-template-columns:270px minmax(0,1fr);gap:var(--gap);max-width:1480px;margin:0 auto;padding:var(--gap) var(--margin)}
.layout.no-side{grid-template-columns:minmax(0,1fr)}
.layout.wide{max-width:none}
.layout.no-side:not(.wide){max-width:1280px}
.side{background:var(--tile);padding:22px 20px;font-size:var(--step-small);align-self:start;position:sticky;top:var(--gap);max-height:calc(100vh - 16px);overflow:auto}
.side h2{font-size:var(--step-lead);margin:22px 0 6px}.side h2:first-child{margin-top:0}
.side ul{list-style:none;margin:0;padding-left:14px}.side>ul,.side-body>ul{padding-left:0}
.side li{margin:3px 0}.side summary{cursor:pointer}
.side a{color:var(--fg);text-decoration:none}.side a:hover{text-decoration:underline}
.side .cur{font-weight:600;color:var(--accent);text-decoration:underline}
.side .all{columns:1}.side .all li{margin:2px 0}
.side-toggle{display:none}
main{min-width:0;background:var(--tile);padding:clamp(20px,3vw,48px)}
.layout.boarded main{background:none;padding:0}

/* Reading surfaces: guide, changelog, ideas. */
.prose{max-width:70ch}
.prose h1{font-size:var(--step-head);margin:0 0 24px}
.prose h2{font-size:var(--step-sub);margin:1.6em 0 .45em}
.prose h3{font-size:var(--step-lead);margin:1.4em 0 .4em}
.prose h4{font-size:var(--step-body);margin:1.2em 0 .3em}
.prose p,.prose ul,.prose ol{margin:0 0 1em}
.prose li{margin:.2em 0}.prose li::marker{color:var(--gilt)}
.prose img{vertical-align:middle}
.prose p>img:only-child{display:block}
.prose table{border-collapse:collapse;display:block;overflow-x:auto;margin:0 0 1.2em;font-size:var(--step-small)}
.prose th,.prose td{border-bottom:1px solid var(--line);padding:8px 14px 8px 0;text-align:left;vertical-align:top}
.prose th{font-weight:600}
.prose pre{overflow-x:auto;background:var(--tile-2);padding:14px 16px;font-size:15px;line-height:1.45}
.prose :not(pre)>code{background:var(--tile-2);padding:.05em .35em;font-size:.88em}
.prose blockquote{margin:0 0 1em;padding:2px 20px;background:var(--tile-2)}
.prose hr{border:0;height:8px;background:var(--bg);margin:2em calc(-1 * clamp(20px,3vw,48px))}
.edit-link{margin:40px 0 0;font-size:var(--step-small)}
.item{font-weight:600;color:var(--gilt)}
.featured{font-size:var(--step-lead);margin:.4em 0 .8em}
.missing-img{color:var(--muted);font-style:italic}
.callout{background:var(--tile-2);padding:4px 22px 6px 16px;margin:0 0 1.2em;border-left:6px solid var(--gold)}
.callout-title{font:800 var(--step-lead)/1.1 var(--display);margin:14px 0 6px}
.callout-tip{border-color:var(--good)}.callout-warning,.callout-important{border-color:var(--warn)}
.callout-danger{border-color:var(--crimson)}
.error{color:var(--bad)}

/* Recipes, drawn as crafting grids. */
.recipe{margin:0 0 1.4em;padding:18px 22px;background:var(--tile-2);width:fit-content;max-width:100%}
.recipe figcaption{font:600 var(--step-small)/1.3 var(--text);color:var(--muted);margin:12px 0 0}
.craft{display:flex;align-items:center;gap:16px;flex-wrap:wrap}
.slots{display:grid;grid-template-columns:repeat(var(--w),52px);gap:4px}
.slot{width:52px;height:52px;display:grid;place-items:center;background:var(--bg);font:600 var(--step-body)/1 var(--text);color:var(--gilt);position:relative}
.slot.empty{opacity:.55}
.slot img{width:36px;height:36px;image-rendering:pixelated}
.slot .n{position:absolute;right:3px;bottom:1px;font:600 14px/1 var(--text);color:var(--fg)}
.craft .arrow{font:800 32px/1 var(--display);color:var(--gilt)}
.craft .out{width:64px;height:64px;background:var(--gold);color:var(--on-gold)}
.craft .out .n{color:var(--on-gold)}
.out-name{font-weight:600}
.recipe-legend{list-style:none;margin:12px 0 0;padding:0;font-size:var(--step-small);columns:2;column-gap:28px}
.recipe-legend b{display:inline-block;width:1.4em;font-family:var(--display);color:var(--gilt)}
.recipe-ref{color:var(--muted)}

/* Status marks. */
.badge{display:inline-block;font:600 var(--step-small)/1 var(--text);padding:5px 9px;margin:0 0 0 6px;vertical-align:middle;background:var(--tile-2);color:var(--fg);white-space:nowrap}
.badge.beta{background:var(--gold);color:var(--on-gold)}.badge.new{background:var(--crimson);color:var(--on-crimson)}
.badge.unrel{background:var(--tile-2)}
.ver{font:400 var(--step-small)/1.3 var(--text);color:var(--muted);letter-spacing:0}

/* Boards: flat tiles on 8px gaps. */
.board{display:grid;grid-template-columns:repeat(12,minmax(0,1fr));gap:var(--gap)}
.tile{background:var(--tile);padding:clamp(18px,1.8vw,24px);min-width:0;position:relative}
.tile.gold{background:var(--gold);color:var(--on-gold)}.tile.gold a{color:inherit}
.tile.crimson{background:var(--crimson);color:var(--on-crimson)}.tile.crimson a{color:inherit}
.tile.night{background:var(--night);color:var(--on-night)}.tile.night a{color:inherit}
.tile.tone{background:var(--tile-2)}
.tile.link{display:flex;align-items:flex-start;text-decoration:none;color:var(--fg);transition:background .2s,color .2s}
.tile.link:hover{background:var(--gold);color:var(--on-gold)}
.tile.link:active{transform:scale(.985)}
a.tile.crimson{color:var(--on-crimson)}a.tile.crimson:hover{background:var(--fg);color:var(--bg)}
.icon{width:48px;height:48px;image-rendering:pixelated;flex:none}
.ph{display:grid;place-items:center;font:800 22px/1 var(--display);background:var(--gold);color:var(--on-gold);image-rendering:auto}

/* Reading pages: the text at a set measure, the page's own headings beside it. */
.read{display:grid;grid-template-columns:minmax(0,70ch) minmax(200px,280px);justify-content:space-between;gap:48px}
.toc{position:sticky;top:24px;align-self:start;font-size:var(--step-small);background:var(--tile-2);padding:20px 22px}
.toc h2{font-size:var(--step-lead);margin:0 0 10px}
.toc ul{list-style:none;margin:0;padding:0;display:grid;gap:6px}
.toc a{color:var(--fg);text-decoration:none}.toc a:hover{text-decoration:underline;color:var(--accent)}

/* Mod page header. */
.modhead{margin:0 0 var(--gap)}
.modhead .name{grid-column:span 8;display:flex;flex-direction:column;gap:18px;padding-bottom:28px}
.modhead h1{font-size:clamp(3rem,2rem + 4.4vw,6.5rem);line-height:.9;margin:8px 0 0;letter-spacing:-.025em}
.modhead .say{font-size:var(--step-lead);line-height:1.3;margin:0;max-width:34ch}
.modhead .plate{grid-column:span 4;display:grid;place-items:center;padding:24px;min-height:280px}
.modhead .plate .icon{width:min(240px,100%);height:auto;aspect-ratio:1}
.modhead .plate .ph{font-size:96px}
.acts{grid-column:1/-1;display:grid;grid-template-columns:repeat(auto-fit,minmax(150px,1fr));gap:var(--gap)}
.acts .tile{min-height:72px;font-size:var(--step-body);font-weight:600}
.acts .tile.crimson{grid-column:span 2;font:800 var(--step-sub)/1 var(--display);min-height:120px}
.facts{grid-column:1/-1}.facts p{margin:0 0 6px;font-size:var(--step-lead);line-height:1.3}.facts p:last-child{margin:0}.facts b{font-weight:600}
.drawer{grid-column:1/-1}
.drawer h2{font-size:var(--step-sub);margin:0 0 16px}
.drawer ul{list-style:none;margin:0;padding:0;display:grid;grid-template-columns:repeat(auto-fill,minmax(88px,1fr));gap:var(--gap)}
.drawer li{background:var(--bg);aspect-ratio:1;display:grid;place-items:center}
.drawer li:first-child{grid-column:span 2;grid-row:span 2}.drawer li:first-child img{width:86%}
.drawer img{width:64%;height:auto;image-rendering:pixelated;transition:transform .2s var(--ease)}
.drawer li:hover img{transform:scale(1.15)}
.readme{margin-top:var(--gap)}

/* Combined changelog. */
.feed{display:grid;gap:var(--gap)}
.feed-section{display:grid;grid-template-columns:minmax(200px,300px) minmax(0,70ch);gap:8px 40px;background:var(--tile);padding:clamp(20px,3vw,40px)}
.feed-section>h3{margin:0;font-size:var(--step-lead);align-self:start;position:sticky;top:16px}
.feed-section>h3 .ver{display:block;margin-top:8px}
.feed-section .prose>:last-child{margin-bottom:0}
.feed-head{background:var(--tile);padding:clamp(20px,3vw,40px)}
.feed-head h1{font-size:var(--step-head);margin:0 0 12px}.feed-head p{margin:0;max-width:60ch}
.feed>h2{font-size:var(--step-head);margin:40px 0 0;padding:28px clamp(20px,3vw,40px) 12px;background:var(--gold);color:var(--on-gold)}

/* Shared footer: the last two rows of the board. */
.foot{padding:var(--gap) var(--margin) var(--margin);margin-top:40px}
.foot .board{grid-auto-rows:auto}
.foot .say{grid-column:span 4;display:flex;flex-direction:column;justify-content:space-between;gap:24px;min-height:200px}
.foot .say p{margin:0}.foot .say p:first-child{font:800 var(--step-sub)/1 var(--display)}
.foot nav{grid-column:span 3}.foot nav ul{list-style:none;margin:0;padding:0;display:grid;gap:6px}
.foot nav a{color:var(--fg)}
.foot .build{grid-column:span 2}.foot .build p{margin:0 0 6px}
.foot .mail{grid-column:span 3;display:flex;flex-direction:column;justify-content:flex-end}
.foot .mail a{font-size:var(--step-lead);line-height:1.2}
.foot .mark{grid-column:span 8;min-height:300px;display:flex;align-items:center;justify-content:center;gap:clamp(12px,2vw,32px);overflow:hidden}
.foot .mark img{width:clamp(44px,9vw,150px);height:auto}
.foot .mark span{font:800 clamp(40px,9vw,150px)/.85 var(--display);letter-spacing:-.035em}
.foot .go{grid-column:span 4;min-height:300px;display:flex;flex-direction:column;justify-content:space-between;text-decoration:none;font:800 var(--step-sub)/1 var(--display)}
.foot .go svg{align-self:flex-end;width:64px;height:64px;transition:transform .25s var(--ease)}
.foot .go:hover svg{transform:translate(6px,-6px)}

/* Entrances: content only hides once the script has said it will reveal it. */
.js .reveal{opacity:0;transform:translateY(24px);transition:opacity .7s var(--ease),transform .7s var(--ease);transition-delay:calc(var(--i,0) * 75ms)}
.js .reveal.in{opacity:1;transform:none}

@media (max-width:1200px){.read{grid-template-columns:minmax(0,1fr)}.toc{display:none}}
@media (max-width:900px){
.layout{grid-template-columns:minmax(0,1fr)}
.acts .tile.crimson{grid-column:1/-1}
.side{position:static;max-height:none}
.side-toggle{display:block;width:100%;text-align:left;font:800 var(--step-lead)/1 var(--display);background:none;border:0;color:var(--fg);padding:0;cursor:pointer}
.side-toggle::after{content:' +'}.side.open .side-toggle::after{content:' −'}
.js .side:not(.open) .side-body{display:none}
.side-body{margin-top:16px}
.modhead .name,.modhead .plate{grid-column:1/-1}
.modhead .plate{min-height:0}.modhead .plate .icon{width:160px}
.feed-section{grid-template-columns:minmax(0,1fr)}.feed-section>h3{position:static}
.foot .say,.foot .mark,.foot .go{grid-column:1/-1}.foot nav,.foot .build,.foot .mail{grid-column:span 6}
.foot .mark,.foot .go{min-height:170px}
}
.top nav a:last-child{margin-left:8px}
@media (max-width:640px){.top nav{order:3;width:100%;gap:4px 20px}.top nav a:last-child{margin-left:0}.top{row-gap:8px}}
@media (max-width:520px){.acts:has(.crimson) .tile:last-child:nth-child(even),.acts:not(:has(.crimson)) .tile:last-child:nth-child(odd){grid-column:1/-1}
.foot .mark span{font-size:12.5vw}.foot nav,.foot .build,.foot .mail{grid-column:1/-1}.board{gap:6px}}
@media (prefers-reduced-motion:reduce){*,*::before,*::after{transition-duration:.01ms!important;animation:none!important}.js .reveal{opacity:1;transform:none}}
`;

/** Theme switch, sidebar disclosure on phones, and section entrances. The home page adds its finder. */
export const SITE_JS = `(()=>{
const d=document.documentElement,K='theme';
const get=()=>{try{return localStorage.getItem(K)}catch(e){return null}};
const put=v=>{try{v?localStorage.setItem(K,v):localStorage.removeItem(K)}catch(e){}};
const name=v=>v==='light'?'Light':v==='dark'?'Dark':'Auto';
const apply=v=>{if(v)d.dataset.theme=v;else delete d.dataset.theme;
  document.querySelectorAll('[data-theme-toggle]').forEach(b=>{b.textContent='Theme: '+name(v);b.title='Switch between automatic, light and dark'})};
apply(get());
document.querySelectorAll('[data-theme-toggle]').forEach(b=>b.addEventListener('click',()=>{
  const order=[null,'light','dark'],next=order[(order.indexOf(get())+1)%3];put(next);apply(next)}));
const side=document.querySelector('.side'),st=side&&side.querySelector('.side-toggle');
if(st){st.addEventListener('click',()=>{const o=side.classList.toggle('open');st.setAttribute('aria-expanded',o)})}
const els=[...document.querySelectorAll('.reveal')];
if(!('IntersectionObserver' in window)){els.forEach(e=>e.classList.add('in'));return}
const io=new IntersectionObserver(es=>es.forEach(e=>{if(e.isIntersecting){e.target.classList.add('in');io.unobserve(e.target)}}),{rootMargin:'0px 0px -6% 0px'});
els.forEach(e=>io.observe(e));
})();
`;

const THEME_BOOT = `<script>try{var t=localStorage.getItem('theme');if(t)document.documentElement.dataset.theme=t}catch(e){}document.documentElement.classList.add('js')</script>`;

/** Wraps rendered Markdown in the reading column, with a list of its sections beside it when it has three or more. */
export const reading = (article: string): string => {
    const heads = [...article.matchAll(/<h2 id="([^"]+)">([\s\S]*?)<\/h2>/g)];
    const toc = heads.length >= 3
        ? `<nav class="toc" aria-label="On this page"><h2>On this page</h2><ul>${heads.map(([, id, html]) => `<li><a href="#${id}">${html!.replace(/<[^>]+>/g, '')}</a></li>`).join('')}</ul></nav>`
        : '';
    return `<div class="read">${article}${toc}</div>`;
};

export const ARROW_SVG = '<svg viewBox="0 0 64 64" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="5"><path d="M14 50 50 14M22 14h28v28"/></svg>';

/** Filled in by the build, which knows the Minecraft version. */
export const siteVars = {mc: ''};

const footer = (up: string): string => `<footer class="foot"><div class="board">
<div class="tile say"><p>Made by chimericdream</p><p>Every page here is built from the mods' own READMEs, changelogs and in-game guides. The mods are open source. Minecraft is a trademark of Mojang; these mods are not affiliated with it.</p></div>
<nav class="tile" aria-label="Footer"><ul><li><a href="${up}">All mods</a></li><li><a href="${up}ideas/">Roadmap</a></li><li><a href="${up}changelog/">Changelog</a></li><li><a href="${up}mods/chimeric-lib/">ChimericLib</a></li><li><a href="${up}mods/opus/">Opus guides</a></li></ul></nav>
<div class="tile tone build"><p>Built for Minecraft ${esc(siteVars.mc)}</p><p class="muted">Fabric and NeoForge</p></div>
<div class="tile mail"><a href="${REPO_URL}">Source on GitHub</a></div>
<div class="tile night mark" aria-hidden="true"><img src="${up}brand/logo.svg" alt="" width="150" height="150"><span>chimericdream</span></div>
<a class="tile crimson go" href="${REPO_URL}/issues">Report a bug or suggest an idea${ARROW_SVG}</a>
</div></footer>`;

export const shell = (opts: {
    title: string;
    url: string;
    body: string;
    crumbs?: string[];
    side?: string;
    wide?: boolean;
    head?: string;
    /** Board pages lay their own tiles on the page ground instead of one sheet. */
    board?: boolean;
    /** The home page draws its own navigation tiles in place of the bar. */
    bare?: boolean;
    description?: string;
}): string => {
    const up = rootPrefix(opts.url) || './';
    const crumbs = opts.crumbs?.length ? `<nav class="crumbs" aria-label="Breadcrumbs"><a href="${up}">Mods</a>${opts.crumbs.map(c => `<span aria-hidden="true">/</span>${c}`).join('')}</nav>` : '';
    const cls = ['layout', opts.side ? '' : 'no-side', opts.wide ? 'wide' : '', opts.board ? 'boarded' : ''].filter(Boolean).join(' ');
    const side = opts.side ? `<aside class="side"><button type="button" class="side-toggle" aria-expanded="false">Contents</button><div class="side-body">${opts.side}</div></aside>` : '';
    const bar = `<header class="top"><a class="brand" href="${up}"><img src="${up}brand/logo.svg" alt="" width="36" height="36">chimericdream</a>
<nav aria-label="Site"><a href="${up}#catalogue">Mods</a><a href="${up}ideas/">Roadmap</a><a href="${up}changelog/">Changelog</a><a href="${REPO_URL}">GitHub</a></nav>
<span class="push"></span><button type="button" class="theme" data-theme-toggle>Theme: Auto</button></header>`;
    const desc = opts.description ? `<meta name="description" content="${esc(opts.description)}">` : '';
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(opts.title)}</title>${desc}${THEME_BOOT}
<link rel="icon" href="${up}brand/logo.svg" type="image/svg+xml">
<link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="${FONTS}"><link rel="stylesheet" href="${up}site.css">${opts.head ?? ''}</head>
<body data-root="${up}"><a class="skip" href="#main">Skip to content</a>
${opts.bare ? '' : bar}
${opts.bare ? `<main id="main" class="home">${opts.body}</main>` : `<div class="${cls}">${side}<main id="main">${crumbs}${opts.body}</main></div>`}
${footer(up)}
<script src="${up}site.js"></script>
</body></html>
`;
};

/** The links a mod's pages share: where its overview, guide, changelog and ideas live. */
export type ModLinks = {overview: string; guide: string | null; changelog: string | null; ideas: string | null};

export const modLinksList = (links: ModLinks, from: string): string => {
    const item = (label: string, url: string | null) => url ? `<li><a href="${esc(relUrl(from, url))}"${url === from ? ' class="cur" aria-current="page"' : ''}>${label}</a></li>` : '';
    return `<h2>This mod</h2><ul>${item('Overview', links.overview)}${item('Guide', links.guide)}${item('Changelog', links.changelog)}${item('Ideas', links.ideas)}</ul>`;
};

type NavNode = {dirs: Map<string, NavNode>; files: GuidePage[]};

const navTree = (pages: GuidePage[]): NavNode => {
    const root: NavNode = {dirs: new Map(), files: []};
    for (const p of pages) {
        const parts = p.rel.split('/');
        let node = root;
        for (const dir of parts.slice(0, -1)) {
            if (!node.dirs.has(dir)) node.dirs.set(dir, {dirs: new Map(), files: []});
            node = node.dirs.get(dir)!;
        }
        node.files.push(p);
    }
    return root;
};

const renderGuideNav = (node: NavNode, current: GuidePage): string => {
    const link = (p: GuidePage) =>
        `<a href="${esc(relUrl(current.url, p.url))}"${p === current ? ' class="cur" aria-current="page"' : ''}>${esc(p.title)}</a>`;
    const files = node.files.filter(f => path.posix.basename(f.rel) !== 'index.md').map(f => `<li>${link(f)}</li>`).join('');
    const dirs = [...node.dirs].map(([name, child]) => {
        const index = child.files.find(f => path.posix.basename(f.rel) === 'index.md');
        const inner = renderGuideNav(child, current);
        const open = inner.includes('aria-current') || index === current ? ' open' : '';
        return `<li><details${open}><summary>${index ? link(index) : esc(name)}</summary>${inner}</details></li>`;
    }).join('');
    return `<ul>${files}${dirs}</ul>`;
};

export const guidePage = (guide: Guide, page: GuidePage, links: ModLinks): string => {
    const home = guide.pages.find(p => p.rel === 'index.md')!;
    return shell({
        title: `${page.title} · ${guide.title}`,
        url: page.url,
        crumbs: [`<a href="${esc(relUrl(page.url, links.overview))}">${esc(guide.mod.name)}</a>`, `<a href="${esc(relUrl(page.url, home.url))}">Guide</a>`],
        side: `${modLinksList(links, page.url)}<h2>Guide</h2>${renderGuideNav(navTree(guide.pages), page)}`,
        body: reading(`<article class="prose"><h1>${esc(page.title)}</h1>${page.body}</article>`),
    });
};
