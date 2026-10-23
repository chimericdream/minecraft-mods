import path from 'node:path';

import type {Guide, GuidePage} from './guide.ts';
import {esc, REPO_URL, relUrl, rootPrefix} from './util.ts';

/** Where the site's mark lives in the repo. Swap the file to swap the logo; nothing else references its contents. */
export const LOGO_SOURCE = 'docs/design/logo-placeholder.svg';
export const LOGO_URL = 'logo.svg';

const LIGHT = `--bg:#f3eff2;--surface:#e9e2ea;--surface2:#ddd3e3;--fg:#1d0c31;--muted:#514460;--card:#e9e2ea;--line:#cbbfd3;--accent:#8b1a1a;--action:#8b1a1a;--action-ink:#fff;--good:#1a6b35;--bad:#a3191f;--warn:#8a5a00;--mark:#f6e3a1`;
const DARK = `--bg:#0e0817;--surface:#1d0c31;--surface2:#2d1050;--fg:#eceff1;--muted:#bdb6ca;--card:#1d0c31;--line:#3b2658;--accent:#ff9296;--action:#a8232b;--action-ink:#fff;--good:#6fd68a;--bad:#ff8a8a;--warn:#e8b94a;--mark:#5c4b00`;

/**
 * Color tokens shared by every page, including the local-only GitHub panel.
 * Follows the OS setting; `data-theme` on <html> (set by the toggle) overrides it.
 */
export const THEME_CSS = `
:root{${LIGHT};color-scheme:light}
@media (prefers-color-scheme:dark){:root:not([data-theme="light"]){${DARK};color-scheme:dark}}
:root[data-theme="dark"]{${DARK};color-scheme:dark}
`;

const FONTS = `<link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Alegreya:ital,wght@0,400..800;1,400..700&display=swap">`;

const THEME_BOOT = `<script>document.documentElement.classList.add('js');try{var t=localStorage.getItem('theme');if(t)document.documentElement.dataset.theme=t}catch(e){}</script>`;

export const SITE_CSS = `
${THEME_CSS}
:root{--serif:'Alegreya',Georgia,'Times New Roman',serif;--mono:ui-monospace,Consolas,monospace;--gutter:clamp(16px,3vw,40px);--ease:cubic-bezier(.2,.7,.2,1)}
*{box-sizing:border-box}
html{scroll-behavior:smooth}
body{margin:0;background:var(--bg);color:var(--fg);font:400 1.15rem/1.55 var(--serif);-webkit-font-smoothing:antialiased}
h1,h2,h3,h4{font-weight:500;line-height:1.05;letter-spacing:-.02em;text-wrap:balance}
a{color:var(--accent);text-decoration:underline;text-decoration-thickness:.08em;text-underline-offset:.2em}
a:hover{text-decoration-thickness:.16em}
:focus-visible{outline:3px solid var(--accent);outline-offset:3px}
::selection{background:var(--action);color:var(--action-ink)}
code{font:.88em var(--mono)}
.sr-only{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap}
.muted{color:var(--muted)}
.skip{position:absolute;left:8px;top:-60px;background:var(--action);color:var(--action-ink);padding:8px 14px;z-index:9}.skip:focus{top:8px}

/* header */
.top{display:flex;gap:6px 28px;align-items:center;flex-wrap:wrap;padding:12px var(--gutter);background:var(--surface)}
.top .brand{display:flex;align-items:center;gap:12px;color:var(--fg);font-size:1.45rem;font-weight:700;letter-spacing:-.02em;text-decoration:none}
.top .brand img{width:40px;height:40px;display:block}
.top nav{display:flex;gap:6px 24px;font-size:1rem}
.top nav a,.top .tools a{color:var(--fg);text-decoration:none;padding:6px 0}
.top nav a:hover,.top .tools a:hover{text-decoration:underline;text-decoration-thickness:.1em;text-underline-offset:.25em}
.top .crumbs{color:var(--muted);font-size:1rem;display:flex;gap:8px}
.top .tools{margin-left:auto;display:flex;gap:18px;align-items:center;font-size:1rem}
.theme-toggle{font:inherit;font-size:1rem;color:var(--fg);background:var(--surface2);border:0;padding:7px 14px;cursor:pointer}
.theme-toggle:hover{background:var(--action);color:var(--action-ink)}

/* frames */
.layout{display:grid;grid-template-columns:minmax(240px,300px) minmax(0,1fr);max-width:1680px;margin:0 auto}
.layout.no-side{grid-template-columns:minmax(0,1fr)}
.layout.no-side main{max-width:1180px;width:100%;margin:0 auto}
.layout.wide.no-side main{max-width:none}
main{padding:clamp(24px,4vw,56px) var(--gutter) 40px;min-width:0}
main.bleed{padding:0;max-width:none}
.side{background:var(--surface);font-size:1rem}
.side-inner{position:sticky;top:0;max-height:100svh;overflow:auto;padding:20px 20px 32px}
.side summary{cursor:pointer;font-weight:700;padding:4px 0}
.side h2{font-size:1rem;color:var(--muted);font-weight:500;letter-spacing:0;margin:22px 0 6px}
.side ul{list-style:none;margin:0;padding-left:14px}.side ul.root{padding-left:0}
.side li{margin:1px 0}
.side a{display:block;color:var(--fg);text-decoration:none;padding:4px 10px;margin:0 -10px}
.side a:hover{background:var(--surface2)}
.side .cur{background:var(--surface2);font-weight:700}
.side details summary{font-weight:400;padding:4px 0}
.side details details summary a{display:inline}

/* reading surface */
article{max-width:74ch;margin-inline:auto}
article>:first-child{margin-top:0}
article h1{font-size:clamp(2.4rem,1.6rem + 3vw,4rem);margin:0 0 .5em}
article h2{font-size:clamp(1.8rem,1.4rem + 1.4vw,2.5rem);margin:1.8em 0 .5em}
article h3{font-size:clamp(1.4rem,1.2rem + .6vw,1.75rem);margin:1.5em 0 .4em}
article h4{font-size:1.2rem;margin:1.3em 0 .3em}
article p,article li{max-width:70ch}
article li{margin:.2em 0}
article img{max-width:100%;height:auto;vertical-align:middle}
article img[src*="shields.io"],article img[src*="badge"]{height:22px;width:auto;margin:3px 4px 3px 0}
article table{border-collapse:collapse;display:block;overflow-x:auto;font-size:1rem;max-width:100%}
article th,article td{padding:7px 14px;text-align:left;vertical-align:top}
article th{background:var(--surface2);font-weight:700}
article tbody tr:nth-child(even) td{background:var(--surface)}
article pre{overflow-x:auto;background:var(--surface);padding:14px 18px;font-size:.92rem;line-height:1.45}
article :not(pre)>code{background:var(--surface);padding:.05em .35em}
article blockquote{margin:1.2em 0;padding:.1em 0 .1em 22px;border-left:6px solid var(--surface2);color:var(--muted);font-style:italic}
article hr{border:0;height:24px;background:none}
.edit-link{margin:48px auto 0;max-width:74ch;font-size:1rem;color:var(--muted)}
.edit-link a{color:var(--muted)}
.item{font-weight:700;background:var(--surface2);padding:0 .3em}
.featured{font-size:1.25em;margin:.6em 0}
.missing-img{color:var(--muted);font-style:italic}
.callout{--c:var(--accent);border-left:6px solid var(--c);background:color-mix(in srgb,var(--c) 13%,var(--bg));padding:6px 22px 8px;margin:1.4em 0}
.callout-title{font-weight:700;margin:.5em 0 0}
.callout-tip{--c:var(--good)}.callout-warning,.callout-important{--c:var(--warn)}.callout-danger{--c:var(--bad)}
.recipe{margin:1.4em 0;padding:12px 22px;background:var(--surface);width:fit-content;max-width:100%}
.recipe figcaption{font-size:1rem;color:var(--muted)}
.recipe-diagram{margin:8px 0;padding:0;background:none;font:14px/1.15 var(--mono);overflow-x:auto}
.recipe-legend{list-style:none;margin:0;padding:0;font-size:1rem;columns:2}
.recipe-legend b{display:inline-block;width:1.5em;font-family:var(--mono)}
.error{color:var(--bad)}

/* status words */
.badge{display:inline-block;font-size:.85rem;font-weight:700;padding:0 8px;background:var(--surface2);color:var(--fg);margin-left:6px;vertical-align:middle;line-height:1.5}
.badge.beta{background:var(--warn);color:var(--bg)}.badge.unrel{color:var(--fg)}.badge.new{background:var(--good);color:var(--bg)}

/* buttons */
.btn{display:inline-flex;align-items:center;min-height:48px;padding:0 26px;font:700 1.15rem var(--serif);background:var(--action);color:var(--action-ink);text-decoration:none;transition:transform .2s var(--ease),background .2s}
.btn:hover{text-decoration:none;transform:translateY(-2px)}.btn:active{transform:translateY(1px)}
.btn.quiet{background:var(--surface2);color:var(--fg)}
.btn.quiet:hover{background:var(--fg);color:var(--bg)}
.actions{display:flex;gap:10px;flex-wrap:wrap;margin:0}

/* home */
.hero{display:grid;grid-template-columns:minmax(0,1fr) minmax(180px,22vw);gap:32px var(--gutter);align-items:center;padding:clamp(32px,6vw,88px) var(--gutter) clamp(28px,4vw,56px)}
.hero h1{font-size:clamp(2.8rem,1rem + 6.4vw,7.6rem);line-height:.95;margin:0;letter-spacing:-.035em}
.hero .lead{font-size:clamp(1.3rem,1.1rem + .8vw,1.75rem);line-height:1.35;max-width:38ch;margin:28px 0 28px}
.crest{width:100%;height:auto;display:block;justify-self:end;max-width:360px;animation:drift 9s ease-in-out infinite alternate}
@keyframes drift{to{transform:translateY(-10px) rotate(-1.2deg)}}
.row-scroll{display:flex;gap:12px;overflow-x:auto;scroll-snap-type:x proximity;padding:0 var(--gutter) 22px;scroll-padding-left:var(--gutter);cursor:grab;scrollbar-color:var(--surface2) transparent}
.row-scroll.drag{cursor:grabbing;scroll-snap-type:none}
.feat{flex:none;scroll-snap-align:start;width:min(380px,82vw);background:var(--surface);display:flex;flex-direction:column;color:var(--fg);text-decoration:none;transition:transform .3s var(--ease)}
.feat.wide{width:min(620px,86vw)}
.feat:hover{text-decoration:none;transform:translateY(-4px)}
.feat-art{display:grid;place-items:center;height:260px;background:var(--surface2)}
.feat.wide .feat-art{height:300px}
.feat-art img{width:168px;height:168px;image-rendering:pixelated;-webkit-user-drag:none;transition:transform .4s var(--ease)}
.feat.wide .feat-art img{width:200px;height:200px}
.feat:hover .feat-art img{transform:scale(1.06)}
.feat.flip .feat-art img{transform:rotate(180deg)}.feat.flip:hover .feat-art img{transform:rotate(180deg) scale(1.06)}
.feat.crimson .feat-art{background:var(--action)}
.feat.snow .feat-art{background:#eceff1}
.feat.night .feat-art{background:#000}
.feat-body{padding:20px 24px 26px;display:flex;flex-direction:column;gap:8px;flex:1}
.feat h3{margin:0;font-size:clamp(1.8rem,1.4rem + 1vw,2.4rem);font-weight:700}
.feat p{margin:0;font-size:1.15rem;line-height:1.35}
.feat .ver{margin-top:auto;color:var(--muted);font-size:1rem}
.section{padding:clamp(72px,10vw,150px) var(--gutter) 0}
.section>h2{font-size:clamp(2rem,1.3rem + 2.6vw,3.6rem);margin:0 0 24px;font-weight:500}
.section-head{display:flex;flex-wrap:wrap;gap:16px 40px;align-items:flex-end;justify-content:space-between;margin-bottom:28px}
.section-head h2{margin:0;font-size:clamp(2rem,1.3rem + 2.6vw,3.6rem);font-weight:500}
.find{display:flex;align-items:center;gap:12px;font-size:1rem}
.find input{font:inherit;color:var(--fg);background:var(--surface);border:0;padding:12px 16px;min-width:min(320px,80vw);min-height:48px}
.find input::placeholder{color:var(--muted)}
.sheet{display:grid;grid-template-columns:repeat(auto-fill,minmax(min(100%,400px),1fr));gap:4px 28px;list-style:none;margin:0;padding:0}
.sheet li{min-width:0}
.sheet a{display:grid;grid-template-columns:52px minmax(0,1fr) auto;gap:2px 16px;align-items:center;padding:10px 12px;color:var(--fg);text-decoration:none;min-height:72px;transition:background .2s}
.sheet a:hover{background:var(--surface)}
.sheet a:hover .nm{text-decoration:underline;text-decoration-thickness:.1em}
.sheet .icon,.sheet .ph{grid-row:span 2;width:52px;height:52px}
.sheet .nm{font-weight:700;font-size:1.15rem;line-height:1.2}
.sheet .ds{grid-column:2/4;font-size:1rem;color:var(--muted);line-height:1.3;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden}
.sheet .vr{font-size:1rem;color:var(--muted);font-variant-numeric:tabular-nums;white-space:nowrap;text-align:right}
.none{display:none;color:var(--muted)}
.about{display:grid;grid-template-columns:minmax(150px,1fr) 3fr;gap:32px var(--gutter)}
.about ul{margin:0;padding:0;list-style:none;display:grid;gap:20px;align-content:start}
.about li{color:var(--fg);font-size:1.5rem;line-height:1.2}
.about li b{font-weight:700;font-variant-numeric:tabular-nums}
.about p{margin:0;font-size:clamp(1.4rem,1rem + 1.2vw,2rem);line-height:1.3;max-width:46ch}
.about p+p{margin-top:1em}
.about .actions{margin-top:28px;font-size:1rem}
.foot{display:grid;grid-template-columns:1fr auto 1fr;gap:12px 24px;align-items:baseline;padding:clamp(80px,14vh,160px) var(--gutter) 28px;font-size:1rem;color:var(--muted)}
.foot nav{display:flex;gap:6px 24px;flex-wrap:wrap;justify-content:center}
.foot nav a{color:var(--fg)}
.foot span:last-child{text-align:right}

/* ideas, changelog, icons */
.icon{width:56px;height:56px;image-rendering:pixelated;flex:none;display:block}
.ph{display:grid;place-items:center;font-weight:700;color:#fff;background:hsl(var(--h) 38% 32%);image-rendering:auto;font-size:1.1rem}
.feed{display:grid;grid-template-columns:repeat(auto-fill,minmax(min(100%,440px),1fr));gap:12px;margin-bottom:56px;align-items:start}
.feed-section{background:var(--surface);padding:22px 26px 14px;min-width:0}
.feed-section>h2{margin:0 0 .5em;font-size:1.6rem}
.feed-section h3{font-size:1.2rem;margin:1em 0 .2em}
.feed-section p,.feed-section li{max-width:none}
.ver{color:var(--muted);font-weight:400;font-size:.7em}
.hl-page{max-width:1180px}

/* mod page */
.modhero{display:grid;grid-template-columns:auto minmax(0,1fr) minmax(220px,340px);gap:24px clamp(24px,3vw,48px);align-items:center;background:var(--surface2);padding:clamp(28px,4vw,56px) var(--gutter)}
.modhero .art{width:clamp(110px,14vw,200px);aspect-ratio:1;display:grid;place-items:center;background:var(--bg)}
.modhero .art .icon,.modhero .art .ph{width:78%;height:78%;font-size:2.4rem}
.modhero h1{margin:0;font-size:clamp(2.4rem,1.2rem + 4.4vw,5.4rem);line-height:.98;letter-spacing:-.03em}
.modhero .lead{font-size:clamp(1.2rem,1.05rem + .6vw,1.55rem);line-height:1.35;margin:14px 0 22px;max-width:36ch}
.facts{margin:0;display:grid;gap:12px;align-content:start}
.facts div{display:grid;grid-template-columns:7.5em 1fr;gap:12px;align-items:baseline}
.facts dt{color:var(--muted);font-size:1rem}.facts dd{margin:0;font-weight:700}
.modhero .badge{margin-left:0;margin-right:6px}
.modhero .btn.quiet{background:var(--bg)}
.modhero .btn.quiet:hover{background:var(--fg);color:var(--bg)}

/* motion */
.js [data-reveal]{opacity:0;transform:translateY(26px)}
.js [data-reveal].in{opacity:1;transform:none;transition:opacity .7s var(--ease),transform .7s var(--ease);transition-delay:calc(var(--i,0)*80ms)}
.js .feat[data-reveal].in{transition:opacity .7s var(--ease),transform .7s var(--ease)}
.js .feat[data-reveal].in.done:hover{transform:translateY(-4px)}
@media (prefers-reduced-motion:reduce){
html{scroll-behavior:auto}
.crest{animation:none}
.js [data-reveal]{opacity:1;transform:none}
.btn,.feat,.feat-art img,.sheet a{transition:none}
.btn:hover,.feat:hover{transform:none}.feat:hover .feat-art img{transform:none}
}

@media (max-width:1000px){
.modhero{grid-template-columns:auto minmax(0,1fr)}.modhero .facts{grid-column:1/-1}
}
@media (max-width:900px){
.layout{grid-template-columns:1fr}
.side-inner{position:static;max-height:none}
.about{grid-template-columns:1fr}.about ul{grid-template-columns:1fr 1fr}
.foot{grid-template-columns:1fr;gap:10px}.foot nav,.foot span:last-child{justify-content:flex-start;text-align:left}
}
@media (max-width:640px){
.hero{grid-template-columns:1fr}.crest{max-width:180px;justify-self:start;order:-1}
.modhero{grid-template-columns:1fr}.modhero .art{width:120px}
.top .tools{margin-left:0;width:100%}
.sheet a{min-height:76px}
.actions .btn:first-child:not(:only-child){width:100%;justify-content:center}
}
`;

const SITE_JS = `
(()=>{
const root=document.documentElement;
const mq=matchMedia('(prefers-color-scheme: dark)');
const eff=()=>root.dataset.theme||(mq.matches?'dark':'light');
const btn=document.querySelector('.theme-toggle');
const label=()=>{if(!btn)return;btn.textContent=eff()==='dark'?'Light theme':'Dark theme';btn.hidden=false};
if(btn){
  btn.onclick=()=>{const t=eff()==='dark'?'light':'dark';root.dataset.theme=t;try{localStorage.setItem('theme',t)}catch{}label()};
  mq.addEventListener('change',label);label();
}

const side=document.querySelector('.side details');
if(side){const small=matchMedia('(max-width:900px)');side.open=!small.matches;small.addEventListener('change',e=>{side.open=!e.matches})}

// Entrances: parts of a section arrive in order, once.
if(document.querySelector('.modhero'))document.querySelectorAll('main article>*').forEach(el=>el.setAttribute('data-reveal',''));
const parts=document.querySelectorAll('[data-reveal]');
const reduce=matchMedia('(prefers-reduced-motion: reduce)').matches;
if(!('IntersectionObserver' in window)||reduce){parts.forEach(p=>p.classList.add('in'))}
else{
  const io=new IntersectionObserver(es=>es.forEach(e=>{
    if(!e.isIntersecting)return;
    e.target.classList.add('in');io.unobserve(e.target);
    setTimeout(()=>e.target.classList.add('done'),900);
  }),{rootMargin:'0px 0px -6% 0px'});
  parts.forEach(p=>io.observe(p));
}

// Drag the featured row sideways with a mouse.
document.querySelectorAll('.row-scroll').forEach(row=>{
  let x=0,left=0,down=false,moved=false;
  row.addEventListener('pointerdown',e=>{if(e.pointerType!=='mouse')return;down=true;moved=false;x=e.clientX;left=row.scrollLeft});
  addEventListener('pointermove',e=>{if(!down)return;const dx=e.clientX-x;if(Math.abs(dx)>4){moved=true;row.classList.add('drag')}row.scrollLeft=left-dx});
  addEventListener('pointerup',()=>{down=false;row.classList.remove('drag')});
  row.addEventListener('click',e=>{if(moved){e.preventDefault();moved=false}},true);
});

// Find a mod: filters the featured tiles and the directory together.
const q=document.getElementById('find');
if(q){
  const items=[...document.querySelectorAll('[data-find]')];
  const none=document.getElementById('find-none');
  q.addEventListener('input',()=>{
    const words=q.value.toLowerCase().split(/\\s+/).filter(Boolean);
    let shown=0;
    items.forEach(el=>{
      const ok=words.every(w=>el.dataset.find.includes(w));
      el.hidden=!ok;if(ok)shown++;
    });
    if(none)none.style.display=shown?'none':'block';
  });
}
})();
`;

export const SITE_ASSETS: Record<string, string> = {'site.css': SITE_CSS, 'site.js': SITE_JS};

export const shell = (opts: {
    title: string;
    url: string;
    body: string;
    crumbs?: string[];
    side?: string;
    wide?: boolean;
    head?: string;
    /** Full-width band above the content (a mod's header). */
    hero?: string;
    /** No frame at all: the page lays out its own sections edge to edge (the home page). */
    bleed?: boolean;
    description?: string;
}): string => {
    const up = rootPrefix(opts.url) || './';
    const crumbs = opts.crumbs?.length ? `<span class="crumbs">${opts.crumbs.map(c => `<span>›</span>${c}`).join('')}</span>` : '';
    const cls = ['layout', opts.side ? '' : 'no-side', opts.wide ? 'wide' : ''].filter(Boolean).join(' ');
    const content = opts.bleed
        ? `<main id="main" class="bleed">${opts.body}</main>`
        : `<div class="${cls}">${opts.side ? `<aside class="side"><div class="side-inner"><details open><summary>Browse this mod</summary>${opts.side}</details></div></aside>` : ''}<main id="main">${opts.body}</main></div>`;
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(opts.title)}</title>${opts.description ? `<meta name="description" content="${esc(opts.description)}">` : ''}
<link rel="icon" type="image/svg+xml" href="${up}${LOGO_URL}">${THEME_BOOT}${FONTS}<link rel="stylesheet" href="${up}site.css">${opts.head ?? ''}</head>
<body data-root="${up}"><a class="skip" href="#main">Skip to content</a><header class="top"><a class="brand" href="${up}"><img src="${up}${LOGO_URL}" alt="" width="40" height="40">chimericdream</a>
<nav aria-label="Site"><a href="${up}">Mods</a><a href="${up}ideas/">Roadmap</a><a href="${up}changelog/">Changelog</a></nav>${crumbs}
<span class="tools"><button type="button" class="theme-toggle" hidden>Dark theme</button><a href="${REPO_URL}">GitHub</a></span></header>
${opts.hero ?? ''}${content}
${footer(up)}
<script src="${up}site.js" defer></script></body></html>
`;
};

export const footer = (up: string, year = new Date().getFullYear()): string => `<footer class="foot"><span>chimericdream’s Minecraft mods</span>
<nav aria-label="Footer"><a href="${up}">Mods</a><a href="${up}ideas/">Roadmap</a><a href="${up}changelog/">Changelog</a><a href="${REPO_URL}">Source</a></nav><span>© ${year}</span></footer>`;

/** The links a mod's pages share: where its overview, guide, changelog and ideas live. */
export type ModLinks = {overview: string; guide: string | null; changelog: string | null; ideas: string | null};

export const modLinksList = (links: ModLinks, from: string): string => {
    const item = (label: string, url: string | null) => url ? `<li><a href="${esc(relUrl(from, url))}"${url === from ? ' class="cur" aria-current="page"' : ''}>${label}</a></li>` : '';
    return `<ul class="root">${item('Overview', links.overview)}${item('Guide', links.guide)}${item('Changelog', links.changelog)}${item('Ideas', links.ideas)}</ul>`;
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

const renderGuideNav = (node: NavNode, current: GuidePage, top = true): string => {
    const link = (p: GuidePage) =>
        `<a href="${esc(relUrl(current.url, p.url))}"${p === current ? ' class="cur" aria-current="page"' : ''}>${esc(p.title)}</a>`;
    const files = node.files.filter(f => path.posix.basename(f.rel) !== 'index.md').map(f => `<li>${link(f)}</li>`).join('');
    const dirs = [...node.dirs].map(([name, child]) => {
        const index = child.files.find(f => path.posix.basename(f.rel) === 'index.md');
        const inner = renderGuideNav(child, current, false);
        const open = inner.includes('aria-current') || index === current ? ' open' : '';
        return `<li><details${open}><summary>${index ? link(index) : esc(name)}</summary>${inner}</details></li>`;
    }).join('');
    return `<ul${top ? ' class="root"' : ''}>${files}${dirs}</ul>`;
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
