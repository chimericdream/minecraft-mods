import path from 'node:path';

import type {Guide, GuidePage} from './guide.ts';
import {esc, REPO_URL, relUrl, rootPrefix} from './util.ts';

/** Site-relative location of the brand mark. The build copies `docs/design/logo-placeholder.svg` here. */
export const MARK_URL = 'brand/mark.svg';

// Light is snow and violet-black ink; dark is the avatar's black with its violet and crimson. The old token
// names (--card, --line, --accent, ...) keep their meaning because the local dashboard styles itself with them:
// --accent is readable as text on --bg; --action is the crimson fill behind white button text.
const LIGHT = {
    bg: '#eceff1', surface: '#e1e4e9', 'surface-2': '#d4d8df', tile: '#dde1e7', card: '#f6f7f8',
    fg: '#160b22', muted: '#4b4358', line: '#c3c8d0',
    accent: '#8b1a1a', 'accent-line': '#b0343a', action: '#8b1a1a', 'action-hover': '#a52222', 'action-ink': '#ffffff', focus: '#8b1a1a',
    field: '#2d1050', 'field-ink': '#eceff1',
    good: '#1d6b34', bad: '#a3161c', warn: '#7a5200', note: '#4b2a80', mark: '#f5df8f',
};
const DARK = {
    bg: '#110a19', surface: '#1a1026', 'surface-2': '#241338', tile: '#1d1229', card: '#1a1026',
    fg: '#eceff1', muted: '#bdb5c9', line: '#3a2c50',
    accent: '#ec7378', 'accent-line': '#d0545a', action: '#8b1a1a', 'action-hover': '#a82828', 'action-ink': '#ffffff', focus: '#e8767b',
    field: '#2d1050', 'field-ink': '#eceff1',
    good: '#5fd07a', bad: '#ff7b72', warn: '#e3b341', note: '#a58ae0', mark: '#4d3d00',
};
const vars = (t: Record<string, string>) => Object.entries(t).map(([k, v]) => `--${k}:${v}`).join(';');

/** Color tokens shared by every page, including the local-only GitHub panel. Follows the OS unless `data-theme` is set. */
export const THEME_CSS = `
:root{${vars(LIGHT)};color-scheme:light}
@media (prefers-color-scheme:dark){:root:not([data-theme=light]){${vars(DARK)};color-scheme:dark}}
:root[data-theme=dark]{${vars(DARK)};color-scheme:dark}
`;

export const FONTS_URL = 'https://fonts.googleapis.com/css2?family=Source+Serif+4:ital,opsz,wght@0,8..60,400;0,8..60,600;1,8..60,400&family=Young+Serif&display=swap';

export const SITE_CSS = `
${THEME_CSS}
:root{--margin:clamp(16px,2.4vw,32px);--gap:clamp(12px,1.4vw,20px);--measure:68ch;
--display:'Young Serif',Georgia,serif;--text:'Source Serif 4',Georgia,serif;
--step-small:clamp(.875rem,.8rem + .25vw,1rem);--step-body:clamp(1.0625rem,1rem + .25vw,1.1875rem);
--step-lead:clamp(1.25rem,1.1rem + .7vw,1.625rem);--step-head:clamp(2rem,1.3rem + 3.2vw,4rem);--step-tile:clamp(1.75rem,2.6vw,2.6rem);--step-display:clamp(2.125rem,.4rem + 6.6vw,7.5rem);
--ease:cubic-bezier(.2,.7,.2,1);--t:240ms}
*,*::before,*::after{box-sizing:border-box}
html{-webkit-text-size-adjust:100%;text-size-adjust:100%}
body{margin:0;background:var(--bg);color:var(--fg);font:400 var(--step-body)/1.55 var(--text);font-optical-sizing:auto;-webkit-font-smoothing:antialiased}
img{max-width:100%;height:auto}
a{color:inherit;text-decoration:underline;text-decoration-color:var(--accent-line);text-decoration-thickness:.09em;text-underline-offset:.2em;transition:text-decoration-color var(--t)}
a:hover{text-decoration-color:currentColor}
:focus-visible{outline:3px solid var(--focus);outline-offset:3px}
::selection{background:var(--field);color:var(--field-ink)}
h1,h2,h3{font-family:var(--display);font-weight:400;letter-spacing:-.02em;line-height:1.08;text-wrap:balance}
h4,h5,h6{font-weight:600;line-height:1.3}
code,pre{font-family:ui-monospace,'Cascadia Code',Consolas,monospace}
.sr-only{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap}
.skip{position:absolute;left:var(--margin);top:-80px;z-index:10;background:var(--action);color:var(--action-ink);padding:10px 16px}
.skip:focus{top:8px}
.muted{color:var(--muted)}
.error{color:var(--bad)}
.px{image-rendering:pixelated}

/* Bar */
.bar{display:flex;align-items:center;flex-wrap:wrap;gap:8px clamp(16px,2.4vw,40px);padding:14px var(--margin)}
.brand{display:flex;align-items:center;gap:10px;text-decoration:none;font-family:var(--display);font-size:var(--step-lead);letter-spacing:-.01em;margin-right:auto}
.brand img{width:40px;height:40px;display:block}
.bar nav{display:flex;flex-wrap:wrap;gap:4px clamp(16px,2vw,32px)}
.bar nav a{text-decoration:none;padding:8px 0;display:inline-block}
.bar nav a[aria-current]{text-decoration:underline;text-decoration-color:var(--accent);text-decoration-thickness:3px;text-underline-offset:8px}
.bar nav a:hover{text-decoration:underline}
.theme{font:inherit;font-size:var(--step-small);background:var(--surface);color:var(--fg);border:0;padding:0 14px;min-height:40px;cursor:pointer;transition:background-color var(--t)}
.theme:hover{background:var(--surface-2)}

/* Buttons and small marks */
.btn{display:inline-flex;align-items:center;min-height:50px;padding:0 24px;background:var(--action);color:var(--action-ink);font:600 var(--step-body)/1.1 var(--text);text-decoration:none;transition:background-color var(--t),transform var(--t) var(--ease)}
.btn:hover{background:var(--action-hover)}
.btn:active{transform:translateY(1px) scale(.98)}
.btn.alt{background:var(--surface-2);color:var(--fg)}
.btn.alt:hover{background:var(--line)}
.tag{--c:var(--muted);display:inline-flex;align-items:center;gap:6px;font:600 var(--step-small)/1.5 var(--text);padding:0 7px;background:var(--surface);color:var(--fg);vertical-align:.08em;white-space:nowrap}
.tag::before{content:'';width:7px;height:7px;background:var(--c);flex:none}
.tag.beta{--c:var(--warn)}.tag.unrel{--c:var(--accent-line)}.tag.new{--c:var(--good)}
.chip{display:inline-flex;font-size:var(--step-small);line-height:1.6;margin:0 4px 6px 0;white-space:nowrap;vertical-align:middle}
.chip>span{padding:0 8px;background:var(--surface-2);color:var(--muted)}
.chip>b{padding:0 8px;background:var(--surface);font-weight:600}
.icon{width:48px;height:48px;image-rendering:pixelated;flex:none;display:block}
.ph{display:grid;place-items:center;font-family:var(--display);line-height:1;color:var(--field-ink);background:var(--field);image-rendering:auto}

/* Reading layout: sidebar and article */
.layout{display:grid;grid-template-columns:minmax(200px,250px) minmax(0,1fr);gap:clamp(24px,5vw,80px);max-width:1440px;margin:0 auto;padding:0 var(--margin)}
.layout.no-side{grid-template-columns:minmax(0,1fr)}
.layout.has-toc{grid-template-columns:minmax(200px,250px) minmax(0,68ch) fit-content(240px);justify-content:space-between}
.layout.has-toc.no-side{grid-template-columns:minmax(0,68ch) fit-content(260px)}
.toc{padding-top:clamp(16px,3vw,40px);font-size:var(--step-small);line-height:1.4}
.toc>div{position:sticky;top:16px;max-height:calc(100vh - 32px);overflow:auto;padding:0 4px 24px 0}
.toc h2{font-size:var(--step-body);margin:0 0 8px}
.toc ul{list-style:none;margin:0;padding:0}
.toc li.sub{padding-left:14px}
.toc a{display:block;padding:4px 0;text-decoration:none;color:var(--muted);transition:color var(--t)}
.toc a:hover{color:var(--fg);text-decoration:underline}
.layout.wide{max-width:none}
main{min-width:0;padding:clamp(16px,3vw,40px) 0 clamp(48px,5vw,80px)}
.side{padding-top:clamp(16px,3vw,40px);font-size:var(--step-small);line-height:1.4}
.side-box{position:sticky;top:16px;max-height:calc(100vh - 32px);overflow:auto;padding:4px 4px 24px 0}
.side-box>summary{display:none}
.side h2{font-size:var(--step-body);margin:28px 0 8px}
.side h2:first-of-type{margin-top:0}
.side ul{list-style:none;margin:0;padding-left:14px}
.side-box>ul,.side-box>h2+ul{padding-left:0}
.side li{margin:0}
.side a{display:block;padding:5px 0;text-decoration:none}
.side a:hover{text-decoration:underline}
.side summary{cursor:pointer;padding:5px 0;list-style-position:outside}
.side summary:has(a){padding:0}
.side summary a{display:inline-block}
.side .cur{font-weight:600;text-decoration:underline;text-decoration-color:var(--accent);text-decoration-thickness:3px}
.crumbs{font-size:var(--step-small);color:var(--muted);margin:0 0 12px;display:flex;flex-wrap:wrap;gap:4px 10px}
.crumbs a{color:var(--fg)}
article{max-width:var(--measure)}
article h1,main>h1{font-size:var(--step-head);margin:0 0 .5em}
article h2{font-size:clamp(1.625rem,1.2rem + 1.4vw,2.375rem);margin:1.9em 0 .5em}
article h3{font-size:clamp(1.25rem,1.1rem + .6vw,1.5rem);margin:1.7em 0 .4em}
article h4{font-size:1em;margin:1.5em 0 .3em}
article h1+h2,article h2+h3{margin-top:.6em}
article p,article ul,article ol{margin:0 0 1em}
article ul,article ol{padding-left:1.3em}
@media (min-width:900px){article ul.dense{columns:2;column-gap:2.5em}article ul.dense>li{break-inside:avoid}}
article li{margin:.25em 0}
article li::marker{color:var(--accent-line)}
article li>ul,article li>ol{margin:.25em 0 .4em}
article img{height:auto}
article p>img:only-child{display:block;margin:1.4em 0}
article table{border-collapse:collapse;display:block;overflow-x:auto;font-size:.94em;line-height:1.45;margin:1.4em 0 1.6em;font-variant-numeric:tabular-nums}
article th{text-align:left;font-weight:600;background:var(--surface)}
article th,article td{padding:.55em .9em;border-bottom:1px solid var(--line);vertical-align:top}
article pre{background:var(--surface);padding:14px 16px;overflow-x:auto;font-size:.82em;line-height:1.55}
article code{font-size:.86em}
article :not(pre)>code{background:var(--surface);padding:.1em .35em}
article blockquote{margin:1.4em 0;padding:.9em 1.2em;background:var(--surface)}
article blockquote>:last-child{margin-bottom:0}
article hr{border:0;height:24px;margin:1.6em 0}
.edit-link{margin-top:48px;font-size:var(--step-small);color:var(--muted)}
.item{font-weight:600;background:var(--surface);padding:0 .3em;white-space:nowrap}
.featured{font-size:1.15em;margin:1.2em 0}
.missing-img{color:var(--muted);font-style:italic}
.callout{--c:var(--note);background:color-mix(in srgb,var(--c) 9%,var(--bg));padding:1em 1.25em;margin:1.5em 0}
.callout>:last-child{margin-bottom:0}
.callout-title{font-family:var(--display);font-size:1.15em;margin:0 0 .35em;display:flex;align-items:center;gap:.55em}
.callout-title::before{content:'';width:.7em;height:.7em;background:var(--c);flex:none}
.callout-tip{--c:var(--good)}.callout-warning,.callout-important{--c:var(--warn)}.callout-danger{--c:var(--bad)}

/* Crafting grids */
.recipe{margin:1.6em 0;padding:18px 20px;background:var(--surface);width:fit-content;max-width:100%}
.recipe figcaption{font-size:var(--step-small);color:var(--muted);margin:0 0 12px}
.craft{display:flex;align-items:center;flex-wrap:wrap;gap:14px 18px}
.slots{display:grid;grid-template-columns:repeat(var(--cols),46px);gap:4px}
.slots>span{height:46px;display:grid;place-items:center;background:var(--surface-2);font:var(--step-body)/1 var(--display)}
.slots>span:empty{background:color-mix(in srgb,var(--surface-2) 45%,var(--surface))}
.craft-arrow{font-size:1.5rem;color:var(--muted)}
.craft-out{min-height:58px;display:flex;align-items:center;gap:8px;padding:6px 16px;background:var(--bg);font:var(--step-body)/1.2 var(--display)}
.craft-out small{font:600 var(--step-small) var(--text);color:var(--muted)}
.recipe-legend{list-style:none;margin:14px 0 0;padding:0;font-size:var(--step-small);display:grid;grid-template-columns:repeat(auto-fill,minmax(170px,1fr));gap:4px 18px}
.recipe-legend b{font:var(--step-small) var(--display);display:inline-block;min-width:1.5em}
.recipe-ref{font-size:var(--step-small);color:var(--muted)}

/* Changelog feed */
.feed-section{margin:0 0 clamp(40px,5vw,64px)}
.feed-section>h3{display:flex;align-items:center;gap:14px;flex-wrap:wrap;margin-bottom:.6em}
.feed-section>h3 .icon{width:36px;height:36px}
.feed-section>h3 .ver{font:600 var(--step-small) var(--text);color:var(--muted);letter-spacing:0}

/* Contact-sheet frames: one mod's own textures, crisp, on a tile one step off the ground */
.frames{display:flex;flex-wrap:wrap;gap:8px;list-style:none;margin:0;padding:0}
.frame{width:var(--f,96px);height:var(--f,96px);background:var(--tile);display:grid;place-items:center;flex:none;overflow:hidden}
.frame img{width:64%;height:64%;image-rendering:pixelated;transition:transform 320ms var(--ease)}
.frame .icon{width:100%;height:100%}
.frame .ph{font-size:calc(var(--f,96px) * .5)}
.entry-sheet.solo .ph{font-size:min(18vw,15rem)}

/* Home: hero */
.home{padding:0 var(--margin)}
.hero{display:grid;grid-template-columns:repeat(12,minmax(0,1fr));column-gap:var(--gap);padding-top:clamp(28px,5vw,80px)}
.hero h1{grid-column:1/-1;font-size:var(--step-display);line-height:.96;letter-spacing:-.03em;margin:0;max-width:16ch}
.hero-copy{grid-column:1/span 6;margin-top:clamp(24px,3vw,44px)}
.hero-copy p{font-size:var(--step-lead);line-height:1.38;margin:0 0 28px;max-width:36ch}
.hero-facts{grid-column:8/-1;align-self:end;display:grid;grid-template-columns:1fr 1fr;gap:20px var(--gap);margin:clamp(24px,3vw,44px) 0 0;font-size:var(--step-small);line-height:1.45}
.hero-facts p{margin:0}
.hero-facts b{font-weight:600}
.sheet{display:flex;flex-wrap:wrap;align-items:flex-start;gap:clamp(32px,3.4vw,48px) clamp(24px,2.6vw,40px);list-style:none;margin:clamp(48px,5.5vw,88px) 0 0;padding:0;--f:clamp(56px,7.4vw,116px)}
.run{max-width:100%;transition:opacity var(--t)}
.run>a{display:flex;flex-direction:column;gap:12px;text-decoration:none}
.run-head{max-width:max(100%,30ch)}
.run h2{font-size:var(--step-lead);margin:0 0 4px;text-decoration:underline;text-decoration-color:transparent;text-decoration-thickness:.07em;text-underline-offset:.16em;transition:text-decoration-color var(--t)}
.run p{margin:0;font-size:var(--step-small);line-height:1.45;max-width:44ch}
.run.solo{--f:calc(var(--fs,clamp(56px,7.4vw,116px)) * 2 + 8px)}
.run.solo .run-head{max-width:calc(var(--f) + 16ch)}
.run a:hover h2,.run a:focus-visible h2{text-decoration-color:var(--accent)}
.run a:hover .frame img{transform:scale(1.1)}
@media (hover:hover){.sheet:has(.run a:hover) .run:not(:has(a:hover)){opacity:.4}}

/* Home: the catalogue index */
.cat{margin-top:clamp(120px,13vw,200px)}
.cat-head{display:grid;grid-template-columns:repeat(12,minmax(0,1fr));column-gap:var(--gap);align-items:end;margin-bottom:clamp(24px,3vw,40px)}
.cat-head h2{grid-column:1/span 7;font-size:var(--step-head);margin:0}
.find{grid-column:8/-1;display:flex;align-items:center;gap:12px;flex-wrap:wrap}
.find[hidden]{display:none}
.find label{font-size:var(--step-body)}
.find input{flex:1;min-width:0;font:inherit;font-size:var(--step-body);min-height:52px;padding:0 16px;background:var(--surface);color:var(--fg);border:0;border-radius:0;transition:background-color var(--t)}
.find input:focus{background:var(--surface-2)}
.find input::placeholder{color:var(--muted)}
.find output{width:100%;font-size:var(--step-small);color:var(--muted)}
.cat-body{position:relative;display:grid;grid-template-columns:repeat(12,minmax(0,1fr));column-gap:var(--gap)}
.index{grid-column:1/span 8;list-style:none;margin:0;padding:0;columns:2;column-gap:var(--gap)}
.index li{break-inside:avoid}
.index li[hidden]{display:none}
.index a{display:grid;grid-template-columns:28px minmax(0,1fr) auto;align-items:center;column-gap:12px;min-height:46px;padding:4px 0;text-decoration:none;transition:opacity var(--t)}
.index .icon{width:28px;height:28px}
.index .ph{font-size:var(--step-small)}
.nm{font-size:var(--step-body);line-height:1.3}
.nm .tag{margin-left:6px}
.ver{font-size:var(--step-small);color:var(--muted);font-variant-numeric:tabular-nums;white-space:nowrap}
.desc{grid-column:2/-1;font-size:var(--step-body);line-height:1.4;color:var(--muted);padding-bottom:6px}
.index a:hover .nm,.index a:focus-visible .nm{text-decoration:underline;text-decoration-color:var(--accent);text-decoration-thickness:2px;text-underline-offset:.2em}
.index .pv{display:none}
.empty{grid-column:1/span 8;font-size:var(--step-lead);margin:8px 0 0}
.preview{display:none}
@media (hover:hover) and (pointer:fine) and (min-width:900px){
.desc{display:none}
.index:has(a:hover,a:focus-visible) a:not(:hover,:focus-visible){opacity:.42}
.preview{display:block;grid-column:9/-1;grid-row:1;position:absolute;top:0;left:0;right:0;background:var(--surface);padding:20px;transition:transform 360ms var(--ease)}
.preview .frames{display:grid;grid-template-columns:repeat(4,1fr)}
.preview .frame{width:auto;height:auto;aspect-ratio:1}
.preview .frames.one{grid-template-columns:1fr 1fr}
.preview h3{font-size:var(--step-lead);margin:18px 0 6px}
.preview p{margin:0 0 12px;font-size:var(--step-small);line-height:1.45}
.preview .pv-links{display:flex;flex-wrap:wrap;gap:6px 18px}
.preview-in{animation:pv-in 260ms var(--ease)}
}
@keyframes pv-in{from{opacity:0;transform:translateY(8px)}to{opacity:1;transform:none}}

/* Home: the workshop board, the mark cut across tiles */
.board-wrap{margin-top:clamp(120px,13vw,200px)}
.board{container-type:inline-size;display:grid;grid-template-columns:repeat(6,minmax(0,1fr));gap:8px}
.t{--g:8px;transition:opacity 600ms var(--ease) calc(var(--d,0) * 45ms),transform 800ms var(--ease) calc(var(--d,0) * 45ms),background-color var(--t);--tile:calc((100cqw - 5 * var(--g)) / 6);--bh:calc(4 * var(--tile) + 3 * var(--g));position:relative;aspect-ratio:1;overflow:hidden;background:#000}
.t:not(.c)>span{position:absolute;inset:0;background-image:var(--mark);background-repeat:no-repeat;background-size:var(--bh) var(--bh);background-position:calc((100cqw - var(--bh)) / 2 - var(--col) * (var(--tile) + var(--g))) calc(-1 * var(--row) * (var(--tile) + var(--g)));transition:transform 420ms var(--ease)}
.t:not(.c):hover>span{transform:scale(1.08)}
.t.c{background:var(--field);color:var(--field-ink);padding:clamp(14px,1.4vw,22px);display:flex;flex-direction:column;justify-content:flex-end;text-decoration:none}
.t.c h2{font-size:var(--step-tile);margin:0;align-self:flex-start;margin-bottom:auto}
.t.c p{margin:0;font-size:var(--step-body);line-height:1.35}
.t.c p b{display:block;font:400 var(--step-head)/1 var(--display);letter-spacing:-.03em;margin-bottom:8px}
.t.act{background:var(--action);color:var(--action-ink)}
.t.act:hover{background:var(--action-hover)}
.t.act span{font:400 var(--step-tile)/1.1 var(--display)}
.board-text{display:grid;grid-template-columns:repeat(12,minmax(0,1fr));column-gap:var(--gap);margin-top:clamp(28px,3vw,48px)}
.board-text p{grid-column:7/-1;margin:0;font-size:var(--step-lead);line-height:1.42}

/* Mod page: the catalogue entry */
.entry{display:grid;grid-template-columns:repeat(12,minmax(0,1fr));column-gap:var(--gap);row-gap:32px;max-width:1440px;margin:0 auto;padding:clamp(24px,4vw,64px) var(--margin) clamp(40px,5vw,72px)}
.entry-text{grid-column:1/span 7;align-self:center}
.entry-title{display:flex;flex-direction:column;align-items:flex-start;gap:clamp(16px,1.8vw,28px);margin-bottom:24px}
.entry-title .icon{width:clamp(56px,6vw,88px);height:clamp(56px,6vw,88px)}
.entry h1{font-size:var(--step-display);margin:0;line-height:.96;letter-spacing:-.03em}
.entry .lede{font-size:var(--step-lead);line-height:1.38;margin:0 0 16px;max-width:40ch}
.entry .meta{margin:0 0 28px;font-size:var(--step-small);color:var(--muted)}
.entry .meta b{color:var(--fg);font-weight:600}
.actions{display:flex;flex-wrap:wrap;gap:10px}
.entry-sheet{grid-column:8/-1;align-self:center;--f:calc((100cqw - 24px) / 4);container-type:inline-size}
.entry-sheet .frames{display:grid;grid-template-columns:repeat(4,1fr)}
.entry-sheet .frame{width:auto;height:auto;aspect-ratio:1}
.entry-sheet .frame:hover img{transform:scale(1.1)}
.entry-sheet.few .frames{grid-template-columns:repeat(2,1fr)}
.entry-sheet.solo .frames{grid-template-columns:1fr}

/* Footer: one quiet line */
.foot{display:grid;grid-template-columns:1fr auto 1fr;align-items:baseline;gap:12px 24px;padding:clamp(32px,4vw,64px) var(--margin) 28px;font-size:var(--step-small);color:var(--muted)}
.foot nav{display:flex;flex-wrap:wrap;gap:6px 24px}
.foot nav a{color:var(--fg);text-decoration:none}
.foot nav a:hover{text-decoration:underline}
.foot>:last-child{justify-self:end}

/* Motion: hero parts arrive in order on load; section parts rise once as they scroll in */
@media (prefers-reduced-motion:no-preference){
.hero h1,.hero-copy,.hero-facts,.run,.entry-title,.entry .lede,.entry .meta,.entry .actions,.entry-sheet .frame,main>.crumbs,.side-box,.toc>div,article>:nth-child(-n+5){animation:rise 700ms var(--ease) both}
.side-box{animation-delay:120ms}.toc>div{animation-delay:180ms}article>:nth-child(2){animation-delay:100ms}article>:nth-child(3){animation-delay:160ms}article>:nth-child(4){animation-delay:220ms}article>:nth-child(5){animation-delay:280ms}
.entry .lede{animation-delay:80ms}.entry .meta{animation-delay:140ms}.entry .actions{animation-delay:200ms}article>:first-child{animation-delay:40ms}
.entry-sheet .frame:nth-child(1){animation-delay:160ms}.entry-sheet .frame:nth-child(2){animation-delay:210ms}.entry-sheet .frame:nth-child(3){animation-delay:260ms}.entry-sheet .frame:nth-child(4){animation-delay:310ms}.entry-sheet .frame:nth-child(5){animation-delay:360ms}.entry-sheet .frame:nth-child(6){animation-delay:410ms}.entry-sheet .frame:nth-child(7){animation-delay:460ms}.entry-sheet .frame:nth-child(8){animation-delay:510ms}
.hero-copy{animation-delay:80ms}.hero-facts{animation-delay:160ms}
.run:nth-child(1){animation-delay:240ms}.run:nth-child(2){animation-delay:320ms}.run:nth-child(3){animation-delay:400ms}.run:nth-child(4){animation-delay:480ms}
.js .reveal>*{opacity:0;transform:translateY(24px);transition:opacity 700ms var(--ease),transform 900ms var(--ease)}
.js .reveal>:nth-child(2){transition-delay:80ms}.js .reveal>:nth-child(3){transition-delay:160ms}
.js .reveal.is-in>*{opacity:1;transform:none}
.js .board .t{opacity:0;transform:translateY(24px)}
.js .board.is-in .t{opacity:1;transform:none}
}
@keyframes rise{from{opacity:0;transform:translateY(24px)}to{opacity:1;transform:none}}

@media (max-width:1200px){
.toc{display:none}
.layout.has-toc{grid-template-columns:minmax(200px,250px) minmax(0,1fr);justify-content:normal}
.layout.has-toc.no-side{grid-template-columns:minmax(0,1fr)}
}
@media (max-width:900px){
.layout,.layout.has-toc{grid-template-columns:minmax(0,1fr);gap:0}
.side-box:not([open])>:not(summary){display:none}
.side{padding-top:12px;padding-bottom:28px}
article{max-width:none}
.side-box{position:static;max-height:none;overflow:visible;background:var(--surface);padding:0}
.side-box>summary{display:block;list-style:none;cursor:pointer;padding:12px 16px;font-family:var(--display);font-size:var(--step-body)}
.side-box>summary::-webkit-details-marker{display:none}
.side-box>summary::after{content:' +';color:var(--muted)}
.side-box[open]>summary::after{content:' \\2212'}
.side-box>:not(summary){margin-left:16px;margin-right:16px}
.side-box>:last-child{padding-bottom:16px}
.hero-copy,.hero-facts{grid-column:1/-1}
.hero-facts{margin-top:8px}
.cat-head h2,.find,.index,.empty{grid-column:1/-1}
.find{margin-top:20px}
.entry-text{grid-column:1/-1}
.entry-sheet{grid-column:1/-1;max-width:520px}
.entry{row-gap:56px}
.entry-sheet.solo{max-width:240px}
.entry-sheet.initial{display:none}
.board-text p{grid-column:1/-1}
.board{grid-template-columns:repeat(4,minmax(0,1fr));gap:6px}
.t{--g:6px;--tile:calc((100cqw - 3 * var(--g)) / 4);--bh:100cqw}
.t:not(.c)>span{background-position:calc(-1 * (var(--col) - 1) * (var(--tile) + var(--g))) calc(-1 * var(--row) * (var(--tile) + var(--g)))}
.t.edge{display:none}
.t.c{order:2;grid-column:span 2;aspect-ratio:auto;min-height:150px}
.t.c.hd,.t.act{grid-column:1/-1}
.t.c.hd{order:1;min-height:0}
}
@media (max-width:600px){
.bar nav{width:100%;order:3;justify-content:space-between}
.index{columns:1}
.index a{min-height:52px}
.foot{grid-template-columns:1fr;gap:8px}
.foot>:last-child{justify-self:start}
}
`;

/** Runs before first paint, so a remembered theme never flashes the other one. */
const THEME_BOOT = `<script>document.documentElement.classList.add('js');try{var t=localStorage.getItem('theme');if(t==='light'||t==='dark')document.documentElement.dataset.theme=t}catch(e){}</script>`;

/** Shared behaviour: the theme switch, and on the home page the catalogue filter and preview. Kept small on purpose. */
export const SITE_JS = String.raw`(()=>{
const root=document.documentElement;
const btn=document.querySelector('.theme');
if(btn){
  const names={'':'auto',light:'light',dark:'dark'};
  const show=()=>{const v=root.dataset.theme||'';btn.textContent='Theme: '+names[v];btn.setAttribute('aria-label','Colour theme: '+names[v]+'. Press to change.')};
  btn.hidden=false;show();
  btn.addEventListener('click',()=>{
    const order=['','light','dark'];const v=order[(order.indexOf(root.dataset.theme||'')+1)%3];
    if(v)root.dataset.theme=v;else delete root.dataset.theme;
    try{v?localStorage.setItem('theme',v):localStorage.removeItem('theme')}catch(e){}
    show();
  });
}
// Section parts rise once as they come into view. Without the observer nothing is ever hidden.
const reveal=document.querySelectorAll('.reveal,.board');
if('IntersectionObserver' in window){
  const io=new IntersectionObserver(es=>es.forEach(e=>{if(e.isIntersecting){e.target.classList.add('is-in');io.unobserve(e.target)}}),{rootMargin:'0px 0px -10% 0px',threshold:.08});
  reveal.forEach(el=>io.observe(el));
}else reveal.forEach(el=>el.classList.add('is-in'));
const cat=document.querySelector('.cat');
if(!cat)return;
const rows=[...cat.querySelectorAll('.index>li')];
const preview=cat.querySelector('.preview');
const body=cat.querySelector('.cat-body');
let current=null;
const show=(li)=>{
  if(!preview||li===current)return;current=li;
  preview.innerHTML='<div class="preview-in">'+li.querySelector('.pv').innerHTML+'</div>';
  const a=li.querySelector('a');
  const y=a.getBoundingClientRect().top-body.getBoundingClientRect().top-preview.offsetHeight/3;
  preview.style.transform='translateY('+Math.max(0,Math.min(body.offsetHeight-preview.offsetHeight,y))+'px)';
};
rows.forEach(li=>{const a=li.querySelector('a');a.addEventListener('pointerenter',()=>show(li));a.addEventListener('focus',()=>show(li))});
// A different mod waits in the preview on each visit.
if(preview&&rows.length){const li=rows[Math.floor(Math.random()*rows.length)];show(li);preview.style.transform='none'}
const find=cat.querySelector('.find');
const input=find&&find.querySelector('input');
if(!input)return;
find.hidden=false;
const out=find.querySelector('output');
const empty=cat.querySelector('.empty');
const filter=()=>{
  const words=input.value.toLowerCase().split(/\s+/).filter(Boolean);
  let n=0;
  rows.forEach(li=>{const hit=words.every(w=>li.dataset.search.includes(w));li.hidden=!hit;if(hit)n++});
  out.textContent=words.length?n+' of '+rows.length+' mods':'';
  empty.hidden=n>0;
  const first=rows.find(li=>!li.hidden);if(first&&words.length)show(first);
};
input.addEventListener('input',filter);
input.addEventListener('keydown',e=>{if(e.key==='Escape'){input.value='';filter()}if(e.key==='Enter'){const f=rows.find(li=>!li.hidden);if(f)location.href=f.querySelector('a').href}});
document.addEventListener('keydown',e=>{if(e.key==='/'&&!/INPUT|TEXTAREA|SELECT/.test(document.activeElement.tagName)){e.preventDefault();input.focus()}});
})();
`;

/** "On this page": the h2 and h3 headings of a reading page, in the right-hand column, when there are enough to need one. */
const tocHtml = (body: string): string => {
    const items = [...body.matchAll(/<h([23]) id="([^"]+)"[^>]*>([\s\S]*?)<\/h\1>/g)].map(([, depth, id, inner]) => ({
        depth, id: id!, label: inner!.replace(/<span class="ver">[\s\S]*?<\/span>/g, '').replace(/<[^>]+>/g, '').trim(),
    })).filter(i => i.label);
    if (items.length < 3) return '';
    const list = items.slice(0, 48).map(i => `<li${i.depth === '3' ? ' class="sub"' : ''}><a href="#${i.id}">${i.label}</a></li>`).join('');
    return `<nav class="toc" aria-label="On this page"><div><h2>On this page</h2><ul>${list}</ul></div></nav>`;
};

const section = (url: string) => url.startsWith('ideas/') ? 'ideas' : url.startsWith('changelog/') ? 'changelog' : 'mods';

export const shell = (opts: {
    title: string;
    url: string;
    body: string;
    description?: string;
    crumbs?: string[];
    side?: string;
    wide?: boolean;
    head?: string;
    /** Full-width content above the sidebar layout (the mod page's entry header). */
    lead?: string;
    /** The body is its own full-width composition (the home page). */
    bare?: boolean;
}): string => {
    const toc = opts.bare || opts.wide ? '' : tocHtml(opts.body);
    const up = rootPrefix(opts.url) || './';
    const cur = section(opts.url);
    const navLink = (key: string, href: string, label: string) =>
        `<a href="${href}"${cur === key ? ' aria-current="page"' : ''}>${label}</a>`;
    const crumbs = opts.crumbs?.length ? `<nav class="crumbs" aria-label="Breadcrumb">${opts.crumbs.join('<span aria-hidden="true">/</span>')}</nav>` : '';
    const cls = ['layout', opts.side ? '' : 'no-side', opts.wide ? 'wide' : '', toc ? 'has-toc' : ''].filter(Boolean).join(' ');
    const side = opts.side
        ? `<aside class="side"><details class="side-box" open><summary>Contents</summary>${opts.side}</details></aside>
<script>if(matchMedia('(max-width:900px)').matches)document.currentScript.previousElementSibling.firstElementChild.removeAttribute('open')</script>`
        : '';
    const main = opts.bare
        ? `<main id="main" class="home">${opts.body}</main>`
        : `${opts.lead ?? ''}<div class="${cls}">${side}<main id="main">${crumbs}${opts.body}</main>${toc}</div>`;
    return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(opts.title)}</title>${opts.description ? `<meta name="description" content="${esc(opts.description)}">` : ''}
${THEME_BOOT}
<link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="${FONTS_URL}">
<link rel="stylesheet" href="${up}site.css"><link rel="icon" href="${up}${MARK_URL}" type="image/svg+xml">
<script src="${up}site.js" defer></script>${opts.head ?? ''}</head>
<body data-root="${up}"><a class="skip" href="#main">Skip to content</a>
<header class="bar"><a class="brand" href="${up}"><img src="${up}${MARK_URL}" alt="" width="40" height="40">chimericdream</a>
<nav aria-label="Site">${navLink('mods', up, 'Mods')}${navLink('ideas', `${up}ideas/`, 'Roadmap')}${navLink('changelog', `${up}changelog/`, 'Changelog')}<a href="${REPO_URL}">GitHub</a></nav>
<button class="theme" type="button" hidden>Theme</button></header>
${main}
<footer class="foot"><span>chimericdream · Minecraft mods</span>
<nav aria-label="Footer"><a href="${up}">Mods</a><a href="${up}ideas/">Roadmap</a><a href="${up}changelog/">Changelog</a><a href="${REPO_URL}">Source</a></nav>
<span>© ${new Date().getFullYear()} chimericdream</span></footer>
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
        crumbs: [`<a href="${esc(relUrl(page.url, links.overview))}">${esc(guide.mod.name)}</a>`, `<a href="${esc(relUrl(page.url, home.url))}">${esc(guide.title)}</a>`],
        side: `${modLinksList(links, page.url)}<h2>Guide</h2>${renderGuideNav(navTree(guide.pages), page)}`,
        body: `<article><h1>${esc(page.title)}</h1>${page.body}</article>`,
    });
};
