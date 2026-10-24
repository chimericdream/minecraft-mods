import path from 'node:path';

import type {Guide, GuidePage} from './guide.ts';
import {esc, REPO_URL, relUrl, rootPrefix} from './util.ts';

/*
 * Colour tokens. Light is the default ground; dark is its own ground, not an inversion. The page follows the
 * OS setting unless a visitor picks one (`data-theme` on <html>, remembered in localStorage).
 * The local-only dashboard imports THEME_CSS, so the token names below are shared and must stay.
 */
const LIGHT = `--bg:#f1eee8;--card:#e6e1d8;--fg:#1b1424;--muted:#5b5366;--line:#cfc8bb;--accent:#8b1a1a;--fill:#8b1a1a;--on-fill:#f7f4ef;--good:#1a7f37;--bad:#b3261e;--warn:#8a5a00;--mark:#fff3a3;--scrim:rgba(18,14,24,.5);--frame-scrim:rgba(18,14,24,.3)`;
const DARK = `--bg:#0c0a10;--card:#161220;--fg:#eceff1;--muted:#b9b2c4;--line:#2e2740;--accent:#e26b6b;--fill:#8b1a1a;--on-fill:#eceff1;--good:#56d364;--bad:#ff7b72;--warn:#e3b341;--mark:#5c4b00;--scrim:rgba(8,6,12,.62);--frame-scrim:rgba(8,6,12,.3)`;

export const THEME_CSS = `
:root{${LIGHT}}
@media (prefers-color-scheme:dark){:root:not([data-theme="light"]){${DARK}}}
:root[data-theme="dark"]{${DARK}}
`;

const FONT_CSS = `
@font-face{font-family:"Sentient";src:url("assets/fonts/Sentient-Light.woff2") format("woff2");font-weight:300;font-display:swap}
@font-face{font-family:"Sentient";src:url("assets/fonts/Sentient-Regular.woff2") format("woff2");font-weight:400;font-display:swap}
@font-face{font-family:"Sentient";src:url("assets/fonts/Sentient-Medium.woff2") format("woff2");font-weight:500;font-display:swap}
`;

export const SITE_CSS = `
${FONT_CSS}
${THEME_CSS}
:root{
  --step-small:clamp(.875rem,.84rem + .15vw,.95rem);
  --step-body:clamp(1.0625rem,1rem + .25vw,1.2rem);
  --step-lead:clamp(1.25rem,1.1rem + .6vw,1.6rem);
  --step-head:clamp(2rem,1.3rem + 2.6vw,3.5rem);
  --step-display:clamp(2.25rem,1.2rem + 3.4vw,4rem);
  --gutter:clamp(20px,3vw,40px);
  --section-y:clamp(120px,16vw,220px);
  --ease:cubic-bezier(.2,.7,.2,1);
}
*{box-sizing:border-box}
html{scroll-behavior:smooth;color-scheme:light}
:root[data-theme="dark"],:root:not([data-theme="light"]){color-scheme:dark}
body{margin:0;background:var(--bg);color:var(--fg);font:400 var(--step-body)/1.55 "Sentient",Georgia,serif;-webkit-font-smoothing:antialiased}
h1,h2,h3,h4,h5,h6{font-weight:300;letter-spacing:-.015em;line-height:1.05;margin:0}
p{margin:0 0 1em;max-width:62ch}
a{color:var(--accent);text-decoration:underline;text-decoration-thickness:1px;text-underline-offset:.22em}
a:hover{text-decoration-thickness:2px}
:focus-visible{outline:2px solid var(--accent);outline-offset:3px}
::selection{background:var(--accent);color:var(--on-fill)}
img{display:block;max-width:100%;height:auto}
code{font-size:.9em}
.sr-only{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap}
.muted{color:var(--muted)}
.small{font-size:var(--step-small)}

/* Buttons: one shape, one radius, flat. */
.btn,.btn-ghost,.linkish{font:500 var(--step-small)/1 "Sentient",Georgia,serif;letter-spacing:0;display:inline-flex;align-items:center;min-height:44px;padding:0 20px;border:0;border-radius:0;cursor:pointer;text-decoration:none;transition:background-color .25s var(--ease),color .25s var(--ease),transform .25s var(--ease)}
.btn{background:var(--fill);color:var(--on-fill)}
.btn:hover{background:var(--accent);color:var(--on-fill);text-decoration:none;transform:translateY(-1px)}
.btn:active{transform:translateY(0)}
.btn-ghost{background:none;color:var(--fg);padding:0 0;min-height:auto;text-decoration:underline;text-underline-offset:.25em}
.btn-ghost:hover{color:var(--accent)}
.linkish{background:none;color:var(--fg);padding:0 10px}
.linkish:hover{color:var(--accent)}

/* Header: a plain bar, no glass, no rules. */
.site-bar{display:flex;gap:clamp(16px,2.4vw,36px);align-items:center;flex-wrap:wrap;padding:18px var(--gutter);background:var(--bg)}
.site-bar .brand{display:inline-flex;align-items:center;gap:10px;color:var(--fg);text-decoration:none;font-weight:500;font-size:1.05rem;letter-spacing:.01em}
.site-bar .brand:hover{color:var(--accent)}
.site-bar .mark{width:28px;height:28px}
.site-bar nav{display:flex;gap:clamp(12px,1.8vw,26px);font-size:var(--step-small)}
.site-bar nav a{color:var(--fg);text-decoration:none}
.site-bar nav a:hover{color:var(--accent)}
.site-bar .end{margin-left:auto;display:flex;gap:10px;align-items:center;font-size:var(--step-small)}
.site-bar .end a{color:var(--fg)}
.site-bar .crumbs{color:var(--muted);font-size:var(--step-small);display:flex;gap:8px;flex-wrap:wrap}
.site-bar .crumbs a{color:var(--muted)}

/* Two-column reading layout for mod overviews, guides and the roadmap. */
.layout{display:grid;grid-template-columns:250px minmax(0,1fr);max-width:1320px;margin:0 auto;min-height:calc(100svh - 70px)}
.layout.no-side{grid-template-columns:minmax(0,1fr)}
.layout.wide{max-width:1600px}
.side{padding:28px var(--gutter) 40px;background:var(--card);font-size:var(--step-small);position:sticky;top:0;align-self:start;max-height:100svh;overflow:auto}
.side h2{font-size:1rem;font-weight:500;letter-spacing:0;color:var(--muted);margin:24px 0 8px}
.side h2:first-child{margin-top:0}
.side ul{list-style:none;margin:0;padding-left:12px}.side>ul{padding-left:0}
.side li{margin:4px 0}.side a{color:var(--fg);text-decoration:none}.side a:hover{color:var(--accent)}
.side summary{cursor:pointer}.side .cur{color:var(--accent);font-weight:500}
main{padding:clamp(32px,5vw,72px) var(--gutter) 120px;min-width:0}
.layout.no-side main{padding-left:var(--gutter);padding-right:var(--gutter)}
.layout.bare main{padding:0}

/* Reading surface: type and measure before anything else. */
article{max-width:68ch}
article h1{font-size:var(--step-display);margin:0 0 .6em}
article h2{font-size:var(--step-head);margin:2.2em 0 .5em}
article h3{font-size:var(--step-lead);margin:1.8em 0 .4em;font-weight:400}
article h4{font-size:var(--step-body);font-weight:500;margin:1.6em 0 .3em}
article ul,article ol{padding-left:1.25em;margin:0 0 1em}
article li{margin:.3em 0}
article img{max-width:100%;height:auto;margin:1.4em 0}
article table{border-collapse:collapse;display:block;overflow-x:auto;font-size:var(--step-small);max-width:100%}
article th,article td{padding:8px 14px;text-align:left;vertical-align:top}
article thead th{background:var(--card);font-weight:500}
article tbody tr:nth-child(even){background:var(--card)}
article pre{overflow-x:auto;background:var(--card);border-radius:0;padding:14px 18px;font-size:.9rem;line-height:1.5}
article :not(pre)>code{background:var(--card);padding:1px 6px;font-size:.88em}
article blockquote{margin:1.6em 0;padding:0;font-size:var(--step-lead);line-height:1.35;color:var(--fg);max-width:34ch}
article hr{border:0;height:0;margin:3em 0}
.edit-link{margin-top:56px;font-size:var(--step-small);color:var(--muted)}
.item{font-weight:500}
.featured{font-size:1.15em;margin:.6em 0}
.missing-img{color:var(--muted);font-style:italic}
.callout{background:var(--card);padding:6px 22px 16px;margin:1.8em 0;max-width:62ch}
.callout-title{font-weight:500;margin:12px 0 4px;color:var(--fg)}
.callout-warning,.callout-important{box-shadow:inset 6px 0 0 var(--warn)}
.callout-danger{box-shadow:inset 6px 0 0 var(--bad)}
.callout-tip{box-shadow:inset 6px 0 0 var(--good)}
.recipe{margin:1.6em 0;padding:16px 20px;background:var(--card);width:fit-content;max-width:100%}
.recipe figcaption{font-size:var(--step-small);color:var(--muted)}
.recipe-diagram{margin:8px 0;padding:0;border:0;background:none;font:14px/1.15 ui-monospace,Consolas,monospace;overflow-x:auto}
.recipe-legend{list-style:none;margin:0;padding:0;font-size:var(--step-small);columns:2}
.recipe-legend b{display:inline-block;width:1.5em;font-family:ui-monospace,Consolas,monospace}
.error{color:var(--bad)}
.badge{font-size:var(--step-small);color:var(--muted);white-space:nowrap}
.badge.beta{color:var(--warn)}.badge.unrel{color:var(--accent)}.badge.new{color:var(--good)}

/* Mod overview: the exhibit beside the name. */
.modhead{display:grid;grid-template-columns:minmax(0,clamp(160px,22vw,300px)) minmax(0,1fr);gap:clamp(24px,4vw,64px);align-items:end;margin-bottom:28px}
.modhead h1{font-size:var(--step-display);margin:0 0 .3em}
.modhead h1 .ver{font-size:var(--step-body);font-weight:400;color:var(--muted);margin-left:.4em;vertical-align:middle}
.modhead p{font-size:var(--step-lead);line-height:1.35;color:var(--fg);max-width:34ch}
.exhibit{margin:0;background:var(--card);aspect-ratio:1;display:grid;place-items:center;padding:clamp(16px,2.4vw,32px)}
img.exhibit{width:100%;height:auto;object-fit:contain}
.exhibit.ph{font-size:clamp(2.5rem,6vw,4.5rem);font-weight:300;color:#fff;background:hsl(var(--h) 40% 34%)}
.actions{display:flex;gap:12px 22px;flex-wrap:wrap;margin:8px 0 56px;align-items:center}
.feed-section{margin:0 0 56px}
.feed-section h2{font-size:var(--step-lead);margin-bottom:.4em}
.feed-section h2 .ver{font-size:var(--step-small);color:var(--muted);font-weight:400;margin-left:.5em}
.icon{width:48px;height:48px;flex:none;object-fit:contain}
.ph{display:grid;place-items:center;font-weight:500;color:#fff;background:hsl(var(--h) 40% 34%)}

/* Home. */
.hero{position:relative;min-height:calc(100svh - 70px);overflow:hidden;isolation:isolate;padding:var(--gutter);display:flex;align-items:flex-end}
.hero-text{position:relative;z-index:2;max-width:min(40rem,42vw);padding-bottom:clamp(8px,4vh,40px)}
.hero h1{font-size:var(--step-display);max-width:9.5em;margin-bottom:.5em}
.hero-text p{font-size:var(--step-body);max-width:44ch;margin-bottom:1.6em}
.print{position:absolute;margin:0;overflow:hidden;background:var(--card)}
.print img{width:100%;height:100%;object-fit:cover}
.print-large{left:50%;top:9%;width:min(40vw,620px);aspect-ratio:4/5;z-index:1}
.print-large .slide{position:absolute;inset:0;opacity:0;animation:slide 12s infinite var(--ease)}
.print-large .slide:first-child{opacity:1}
.print-large .slide:nth-child(2){animation-delay:6s}
@keyframes slide{0%,40%{opacity:1}50%,100%{opacity:0}}
.print-small{left:7%;top:13%;width:clamp(120px,12vw,180px);aspect-ratio:1;filter:brightness(.72);z-index:1}

section{position:relative}
.frames>.frame{position:relative;min-height:100svh;display:grid;grid-template-columns:repeat(4,minmax(0,1fr));align-items:center;padding:var(--gutter);overflow:hidden;isolation:isolate}
.frame .bg{position:absolute;inset:0;width:100%;height:100%;object-fit:cover;z-index:-2}
.frame::after{content:"";position:absolute;inset:0;background:var(--frame-scrim);z-index:-1}
.frame-card{grid-column:var(--col,1)/span 2;width:100%;max-width:600px;min-width:0;background:var(--card);color:var(--fg);padding:clamp(20px,2.4vw,36px);display:flex;flex-direction:column;gap:16px;min-height:min(64svh,640px)}
.frame-card h2{font-size:clamp(1.75rem,1.55rem + .7vw,2.25rem);font-weight:300;overflow-wrap:anywhere}
.frame-card h2 a{color:var(--fg);text-decoration:none}
.frame-card h2 a:hover{color:var(--accent)}
.frame-card p{font-size:var(--step-body);margin:0;max-width:none}
.frame-card .fact{font-size:var(--step-small);color:var(--muted)}
.frame-card .thumb{width:96px;height:96px;object-fit:contain;image-rendering:auto}
.frame-card .foot{margin-top:auto;display:flex;flex-direction:column;align-items:flex-start;gap:12px}
.frame-card .btn{width:100%;justify-content:center}
.frame-card .more{font-size:var(--step-small)}

.lineup-section{padding:var(--section-y) var(--gutter) 0;display:grid;grid-template-columns:repeat(12,minmax(0,1fr));gap:0 24px}
.lineup-head{grid-column:1/span 4;align-self:start}
.lineup-head h2{font-size:var(--step-head);margin-bottom:.5em}
.lineup-head p{margin-bottom:1.4em}
.filter{display:flex;flex-direction:column;gap:8px;max-width:360px;margin-bottom:12px}
.filter label{font-size:var(--step-small);color:var(--muted)}
.filter input{font:400 var(--step-body)/1 "Sentient",Georgia,serif;background:var(--card);color:var(--fg);border:0;border-radius:0;padding:14px 16px;min-height:48px;width:100%}
.filter input::placeholder{color:var(--muted);opacity:1}
.count{font-size:var(--step-small);color:var(--muted);margin:0}
.lineup-table{grid-column:6/span 7;width:100%;border-collapse:collapse}
.lineup-table caption{text-align:left;padding:0 0 12px;font-size:var(--step-small);color:var(--muted);caption-side:top}
.lineup-table th{font-size:var(--step-small);font-weight:500;color:var(--muted);text-align:left;padding:6px 14px}
.lineup-table td{padding:14px;vertical-align:baseline}
.lineup-table tbody tr:nth-child(even){background:var(--card)}
.lineup-table td.name{font-size:var(--step-lead);line-height:1.2;width:50%}
.lineup-table td.name a{color:var(--fg);text-decoration:none}
.lineup-table td.name a:hover{color:var(--accent)}
.lineup-table td.ver{font-size:var(--step-small);color:var(--muted);white-space:nowrap;font-variant-numeric:tabular-nums}
.lineup-table td.links{font-size:var(--step-small);white-space:nowrap}
.lineup-table td.links a{margin-right:12px}
.lineup-table tr[hidden]{display:none}

.how{padding:var(--section-y) var(--gutter) 0}
.how h2{width:50%;margin-left:50%;font-size:var(--step-head);margin-bottom:clamp(48px,6vw,96px)}
.stage{display:grid;grid-template-columns:repeat(12,minmax(0,1fr));gap:0 24px;align-items:start;margin-bottom:clamp(96px,11vw,180px)}
.stage figure{margin:0}
.stage figure img{width:100%;height:auto;aspect-ratio:3/4;object-fit:cover}
.stage .text{align-self:start;padding-top:12px}
.stage h3{font-size:var(--step-lead);font-weight:400;margin-bottom:.5em}
.stage-1 figure{grid-column:1/span 4}.stage-1 .text{grid-column:6/span 4}
.stage-2 figure{grid-column:1/span 5;margin-top:220px}.stage-2 figure img{aspect-ratio:1/1}.stage-2 .text{grid-column:7/span 4;margin-top:220px}
.stage-3 figure{grid-column:8/span 5}.stage-3 figure img{aspect-ratio:16/10}.stage-3 .text{grid-column:2/span 4;margin-top:180px}

.site-end{position:relative;min-height:100svh;display:grid;grid-template-rows:auto 1fr auto;overflow:hidden;isolation:isolate;color:var(--on-fill);background:var(--fill)}
.site-end>.bg{position:absolute;inset:0;width:100%;height:100%;object-fit:cover;z-index:-2}
.site-end::before{content:"";position:absolute;inset:0;background:var(--scrim);z-index:-1}
.end-top{display:grid;grid-template-columns:repeat(12,minmax(0,1fr));gap:24px;padding:clamp(28px,3vw,40px) var(--gutter)}
.end-top ul{list-style:none;margin:0;padding:0;font-size:var(--step-small);display:grid;gap:6px}
.end-top a{color:var(--on-fill)}
.end-top .navs{grid-column:1/span 4}.end-top .contact{grid-column:9/span 4;font-size:var(--step-small)}
.end-top .contact p{margin:0;max-width:none}
.wordmark{margin:0;padding:0 var(--gutter);font-weight:300;font-size:min(12.4vw,220px);line-height:.8;letter-spacing:-.03em;white-space:nowrap;align-self:end;color:var(--on-fill)}
.end-legal{padding:24px var(--gutter) clamp(24px,3vw,36px);font-size:13px;line-height:1.5;max-width:none;color:var(--on-fill)}
.end-legal p{margin:0 0 .5em;max-width:72ch;font-size:13px}

/* Motion: entrances only when the script runs; content never waits on it. */
.js .reveal{opacity:0;transform:translateY(20px);transition:opacity .9s var(--ease),transform .9s var(--ease)}
.js .reveal.in{opacity:1;transform:none}

@media (max-width:900px){
  .hero{min-height:auto;flex-direction:column;align-items:stretch;padding-bottom:var(--gutter)}
  .hero-text{max-width:none;margin-top:0;padding-top:520px}
  .print-large{left:14%;top:var(--gutter);width:72%;aspect-ratio:4/5}
  .print-small{left:var(--gutter);top:auto;bottom:auto;width:28%;transform:translateY(-130px)}
  .frames>.frame{grid-template-columns:1fr;align-items:end;padding-bottom:var(--gutter)}
  .frame-card{grid-column:1/-1;max-width:none;min-height:46svh}
  .lineup-section{grid-template-columns:1fr}
  .lineup-head,.lineup-table{grid-column:1/-1}
  .how h2{width:auto;margin-left:0}
  .stage figure,.stage .text,.stage-1 figure,.stage-1 .text,.stage-2 figure,.stage-2 .text,.stage-3 figure,.stage-3 .text{grid-column:1/-1;margin-top:0}
  .stage-2 figure{width:62%;margin-left:auto}.stage-3 figure{width:78%}
  .end-top .navs,.end-top .contact{grid-column:1/-1}
}
@media (max-width:760px){
  .layout{grid-template-columns:1fr}
  .side{position:static;max-height:none;border-bottom:0}
  .site-bar .end{margin-left:0;width:100%}
  .lineup-table td.name{width:auto}
  .lineup-table th.hide-s,.lineup-table td.hide-s{display:none}
  .modhead{grid-template-columns:1fr}
  .modhead .exhibit{width:min(60vw,240px)}
}
@media (prefers-reduced-motion:reduce){
  html{scroll-behavior:auto}
  *,*::before,*::after{animation:none!important;transition:none!important}
  .js .reveal{opacity:1;transform:none}
  .print-large .slide:not(:first-child){display:none}
  .print-large .slide:first-child{opacity:1}
}
`;

/** Theme toggle and entrance script. Runs before paint to set the remembered theme; no content depends on it. */
const EARLY_JS = `<script>try{document.documentElement.classList.add('js');var t=localStorage.getItem('theme');if(t==='light'||t==='dark')document.documentElement.dataset.theme=t}catch(e){}</script>`;

const LATE_JS = `<script>
(function(){
  var root=document.documentElement;
  var btn=document.getElementById('theme-toggle');
  if(btn){
    var dark=function(){return root.dataset.theme==='dark'||(!root.dataset.theme&&matchMedia('(prefers-color-scheme: dark)').matches)};
    btn.addEventListener('click',function(){
      var next=dark()?'light':'dark';
      root.dataset.theme=next;
      try{localStorage.setItem('theme',next)}catch(e){}
    });
  }
  var items=document.querySelectorAll('.reveal');
  if('IntersectionObserver' in window&&!matchMedia('(prefers-reduced-motion: reduce)').matches){
    var io=new IntersectionObserver(function(entries){entries.forEach(function(e){if(e.isIntersecting){e.target.classList.add('in');io.unobserve(e.target)}})},{rootMargin:'0px 0px -8% 0px'});
    items.forEach(function(el){io.observe(el)});
  }else{items.forEach(function(el){el.classList.add('in')})}
  document.querySelectorAll('video[autoplay]').forEach(function(v){
    if(matchMedia('(prefers-reduced-motion: reduce)').matches){v.removeAttribute('autoplay');v.pause()}
    document.addEventListener('visibilitychange',function(){document.hidden?v.pause():(matchMedia('(prefers-reduced-motion: reduce)').matches||v.play())});
  });
})();
</script>`;

/** The links a mod's pages share: where its overview, guide, changelog and ideas live. */
export type ModLinks = {overview: string; guide: string | null; changelog: string | null; ideas: string | null};

export const modLinksList = (links: ModLinks, from: string): string => {
    const item = (label: string, url: string | null) => url ? `<li><a href="${esc(relUrl(from, url))}"${url === from ? ' class="cur" aria-current="page"' : ''}>${label}</a></li>` : '';
    return `<h2>This mod</h2><ul>${item('Overview', links.overview)}${item('Guide', links.guide)}${item('Changelog', links.changelog)}${item('Ideas', links.ideas)}</ul>`;
};

export const shell = (opts: {
    title: string;
    url: string;
    body: string;
    crumbs?: string[];
    side?: string;
    wide?: boolean;
    /** Full-bleed page: sections set their own gutters, so <main> adds none. */
    bare?: boolean;
    head?: string;
    end?: string;
}): string => {
    const up = rootPrefix(opts.url) || './';
    const crumbs = opts.crumbs?.length ? opts.crumbs.map(c => `<span aria-hidden="true">›</span>${c}`).join('') : '';
    const cls = ['layout', opts.side ? '' : 'no-side', opts.wide ? 'wide' : '', opts.bare ? 'bare' : ''].filter(Boolean).join(' ');
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(opts.title)}</title><link rel="icon" href="${up}assets/logo.svg" type="image/svg+xml"><link rel="stylesheet" href="${up}site.css">${EARLY_JS}${opts.head ?? ''}</head>
<body data-root="${up}"><header class="site-bar"><a class="brand" href="${up}"><img class="mark" src="${up}assets/logo.svg" alt="" width="28" height="28">Chimeric Mods</a>
<nav aria-label="Site"><a href="${up}">Mods</a><a href="${up}ideas/">Roadmap</a><a href="${up}changelog/">Changelog</a></nav>${crumbs ? `<span class="crumbs">${crumbs}</span>` : ''}
<span class="end"><a href="${REPO_URL}">GitHub</a><button type="button" class="linkish" id="theme-toggle" aria-label="Switch between light and dark theme">Theme</button></span></header>
<div class="${cls}">${opts.side ? `<aside class="side" aria-label="Pages for this mod">${opts.side}</aside>` : ''}<main id="main">${opts.body}</main></div>
${opts.end ?? ''}${LATE_JS}
</body></html>
`;
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
        body: `<article><h1>${esc(page.title)}</h1>${page.body}</article>`,
    });
};
