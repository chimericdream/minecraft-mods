import {THEME_CSS} from './theme.ts';

/** Google Fonts stylesheet for the display and text faces (docs/SITE-DESIGN-BRIEF.md §5). */
export const FONT_LINKS = `<link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Source+Serif+4:ital,opsz,wght@0,8..60,400;0,8..60,600;1,8..60,400&family=Young+Serif&display=swap">`;

/**
 * The site's design: one board of flat tiles on a violet ground, Young Serif over Source Serif 4, one crimson accent.
 * Sizes: small 14, body 18, lead 24, head 32-51, display 42-90. Corners are square, surfaces are separated by a step in
 * tone (never a line or a shadow), and everything that moves is transform or opacity with a reduced-motion fallback.
 */
export const SITE_CSS = `
${THEME_CSS}
:root{
--display:'Young Serif',Georgia,'Times New Roman',serif;--text:'Source Serif 4',Georgia,'Times New Roman',serif;
--small:.875rem;--body:clamp(1.0625rem,1rem + .25vw,1.1875rem);--lead:clamp(1.25rem,1.1rem + .6vw,1.5rem);
--head:clamp(2rem,1.3rem + 2.6vw,3.2rem);--display-size:clamp(2.6rem,1.2rem + 5.6vw,5.6rem);
--m:clamp(16px,2.4vw,32px);--gap:clamp(6px,.8vw,10px);--row:clamp(64px,7vw,108px);--row-s:clamp(92px,7vw,104px);
--pad:clamp(14px,1.7vw,26px)
}
*{box-sizing:border-box}
[hidden]{display:none!important}
html{-webkit-text-size-adjust:100%;scroll-padding-top:84px}
body{margin:0;background:var(--bg);color:var(--fg);font:400 var(--body)/1.6 var(--text);font-optical-sizing:auto;min-height:100svh;display:flex;flex-direction:column}
body>.layout,body>main{flex:1 0 auto}
a{color:var(--accent);text-decoration-thickness:.08em;text-underline-offset:.18em}
a:hover{text-decoration-thickness:.16em}
:focus-visible{outline:3px solid var(--accent);outline-offset:3px}
.tile :focus-visible,.violet :focus-visible,.crimson :focus-visible{outline-color:currentColor}
::selection{background:var(--accent-fill);color:var(--on-accent)}
code,kbd,pre{font-family:ui-monospace,SFMono-Regular,Consolas,monospace}
code{font-size:.88em}
img{max-width:100%}
.sr-only{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap}
.skip{position:absolute;left:var(--m);top:-60px;z-index:50;background:var(--accent-fill);color:var(--on-accent);padding:10px 18px;text-decoration:none}
.skip:focus{top:8px}
.muted{color:var(--muted)}
h1,h2,h3,h4{font-family:var(--display);font-weight:400;line-height:1.1;letter-spacing:-.01em;text-wrap:balance}

/* ---- header and footer ---- */
.top{position:sticky;top:0;z-index:20;display:flex;align-items:center;gap:6px 28px;flex-wrap:wrap;padding:8px var(--m);background:var(--card)}
.top .brand{display:flex;align-items:center;gap:12px;font:400 1.4rem/1 var(--display);color:var(--fg);text-decoration:none;min-height:44px}
.top .brand img{width:40px;height:40px;display:block}
.top nav,.top .tools{display:flex;gap:4px;flex-wrap:wrap}
.top .tools{margin-left:auto;align-items:center}
.top a.nav,.top button,.foot a{color:var(--fg);text-decoration:none;padding:0 14px;min-height:44px;display:inline-flex;align-items:center;font:inherit;font-size:1rem;background:none;border:0;cursor:pointer}
.top a.nav:hover,.top button:hover{background:var(--card2)}
.top a.nav[aria-current]{background:var(--card2)}
.theme-toggle{display:none!important}.js .theme-toggle{display:inline-flex!important}
.foot{margin:clamp(56px,8vw,120px) 0 0;padding:0 var(--m) var(--m)}
.foot-body{display:grid;grid-template-columns:repeat(12,minmax(0,1fr));gap:var(--gap)}
.foot-body>*{background:var(--card);padding:var(--pad);margin:0;min-width:0}
.foot-body>img.foot-mark{grid-column:span 3;width:100%;height:auto;aspect-ratio:1;padding:0;background:#000;display:block;object-fit:cover}
.foot-name{grid-column:span 4;font:400 var(--lead)/1.1 var(--display);display:flex;align-items:flex-end}
.foot-links{grid-column:span 2;display:flex;flex-direction:column;align-items:flex-start;justify-content:flex-end;gap:2px}
.foot-links a{padding:0;min-height:36px}
.foot-links a,.foot-note a{color:var(--fg)}
.foot-note{grid-column:span 3;color:var(--muted);font-size:var(--small);display:flex;flex-direction:column;justify-content:space-between;gap:12px}
.foot-note a{font-size:1rem}

/* ---- tiles and boards ---- */
.board{display:grid;grid-template-columns:repeat(12,minmax(0,1fr));grid-auto-rows:var(--row);gap:var(--gap)}
.tile{position:relative;display:flex;flex-direction:column;min-width:0;padding:var(--pad);background:var(--card);overflow:hidden}
.tile.violet{background:var(--violet);color:var(--on-violet)}
.tile.crimson{background:var(--accent-fill);color:var(--on-accent)}
.tile.tone{background:var(--card2)}
.tile.link{transition:transform .25s cubic-bezier(.2,.7,.2,1),background-color .2s}
.tile.link:hover{transform:translateY(-3px);background-color:var(--card2)}
.tile.violet.link:hover,.tile.crimson.link:hover{filter:brightness(1.14)}
.tile.link:active{transform:translateY(0)}
.tile h2,.tile h3{margin:0}
.tile p{margin:0}
.lead{font-size:var(--lead);line-height:1.35}
.small{font-size:var(--small)}
.chips{display:flex;flex-wrap:wrap;gap:6px;align-items:center}
.chip{display:inline-flex;align-items:center;min-height:24px;padding:0 9px;font-size:var(--small);line-height:1;background:var(--card2);color:var(--fg);white-space:nowrap}
.tile.tone .chip,.tile.violet .chip{background:rgb(255 255 255 / .14);color:inherit}
.chip.new{background:color-mix(in srgb,var(--good) 22%,transparent);color:var(--fg)}
.chip.beta{background:color-mix(in srgb,var(--warn) 24%,transparent);color:var(--fg)}
.chip.unrel{background:color-mix(in srgb,var(--accent) 22%,transparent);color:var(--fg)}
.tile.violet .chip.new,.tile.violet .chip.beta,.tile.violet .chip.unrel,.tile.crimson .chip{color:inherit;background:rgb(255 255 255 / .2)}
.btn{display:inline-flex;align-items:center;justify-content:center;gap:8px;min-height:48px;padding:0 22px;background:var(--accent-fill);color:var(--on-accent);font:600 1rem/1 var(--text);text-decoration:none;border:0;cursor:pointer;transition:transform .2s cubic-bezier(.2,.7,.2,1),background-color .2s;text-align:center}
.btn:hover{transform:translateY(-2px);text-decoration:none;background-color:#a52424}
.btn:active{transform:translateY(0)}
.btn.alt{background:var(--card2);color:var(--fg)}
.btn.alt:hover{background:var(--line)}
.tile .btn:focus-visible{outline-color:var(--fg)}

/* icons */
.icon{width:48px;height:48px;flex:none;image-rendering:pixelated;display:block}
.icon.big{width:clamp(72px,7vw,112px);height:clamp(72px,7vw,112px)}
.ph{display:grid;place-items:center;font:400 1.2rem/1 var(--display);color:var(--ph-ink);background:var(--ph-bg);image-rendering:auto}
.ph.big{font-size:2.4rem}
.ph.c0{--ph-bg:#2d1050;--ph-ink:#eceff1}.ph.c1{--ph-bg:#8b1a1a;--ph-ink:#fff}.ph.c2{--ph-bg:#4b3f63;--ph-ink:#fff}
.ph.c3{--ph-bg:#e2e2e4;--ph-ink:#1d0c31}.ph.c4{--ph-bg:#1d0c31;--ph-ink:#e2e2e4}.ph.c5{--ph-bg:#6b2a4e;--ph-ink:#fff}

/* ---- home ---- */
.home{padding:var(--gap) var(--m) 0}
.hero{margin:0 0 clamp(72px,9vw,128px)}
.hero .name{grid-column:1/9;grid-row:1/5;justify-content:space-between;padding:clamp(18px,2.4vw,40px)}
.hero h1{font-size:var(--display-size);line-height:.98;letter-spacing:-.025em;margin:0;max-width:11em}
.hero .name .lead{max-width:34em;margin-top:18px}
.hero .mark{grid-column:9/13;grid-row:1/5;padding:0;background:#000}
.hero .mark img{position:absolute;inset:0;width:100%;height:100%;object-fit:cover;object-position:50% 40%;display:block}
.hero .find{grid-column:1/6;grid-row:5/7;justify-content:center;gap:10px}
.hero .go{grid-column:6/9;grid-row:5/7;justify-content:flex-end;gap:6px;font:400 var(--lead)/1.1 var(--display)}
.hero .go .arrow{font-size:2.4rem;line-height:1}
.hero .facts{grid-column:9/13;grid-row:5/7;justify-content:center;gap:4px}
.find label{font:400 var(--lead)/1.1 var(--display)}
.find form{display:flex;flex-direction:column;gap:10px}
.find .row{display:flex;gap:var(--gap)}
.find input{flex:1;min-width:0;min-height:48px;padding:0 14px;font:inherit;font-size:1rem;color:var(--fg);background:var(--bg);border:0}
.find input::placeholder{color:var(--muted);opacity:1}
.find .count{color:var(--muted);font-size:var(--small)}
.facts .fact{font:400 var(--lead)/1.2 var(--display)}
.facts .small{color:var(--muted);font-size:1rem}
.sec{margin:0 0 clamp(48px,6vw,96px)}
.sec>h2{font-size:var(--head);margin:0 0 6px}
.sec>.sub{margin:0 0 clamp(16px,2vw,28px);color:var(--muted);max-width:44em}
.feat{margin:0;grid-auto-rows:clamp(82px,9.6vw,148px)}
.feat .m1 .icon,.feat .m2 .icon{width:clamp(96px,10vw,150px);height:clamp(96px,10vw,150px)}
.feat .t{justify-content:space-between;gap:12px}
.feat h3{font-size:var(--lead);line-height:1.1}
.feat .t p{max-width:30em}
.feat .xl{grid-column:1/6;grid-row:1/5}
.feat .lg{grid-column:6/9;grid-row:1/5}
.feat .m1{grid-column:9/13;grid-row:1/3}
.feat .m2{grid-column:9/13;grid-row:3/5}
.feat .m1,.feat .m2{flex-direction:row;justify-content:space-between;align-items:flex-start;gap:14px}
.feat .m1 .meta,.feat .m2 .meta{flex:1;min-width:0;height:100%;justify-content:space-between}
.feat .xl .icon.big{width:clamp(110px,20vw,300px);height:clamp(110px,20vw,300px)}
.feat .lg .icon.big{width:clamp(100px,15vw,230px);height:clamp(100px,15vw,230px)}
.feat .xl .ph.big,.feat .lg .ph.big{font-size:clamp(2.4rem,5vw,4.4rem)}
.feat .xl h3{font-size:var(--head)}
.feat .lg h3{font-size:var(--head)}
.feat .xl .lead,.feat .lg .lead{font-size:var(--lead)}
.feat .head{display:flex;gap:16px;align-items:flex-start;justify-content:space-between}
.feat .m2 .quote{font:400 var(--lead)/1.15 var(--display)}
.feat .stat{color:inherit;opacity:.85;font-size:var(--small)}
.feat .t .meta{display:flex;flex-direction:column;gap:10px}
.dir{grid-auto-rows:minmax(var(--row-s),auto);list-style:none;margin:0;padding:0}
a.tile{color:inherit;text-decoration:none}
.pagehead .muted{color:inherit;opacity:.85}
.dir .s{grid-column:span 3;flex-direction:row;align-items:center;justify-content:space-between;gap:14px;padding:12px 14px 12px var(--pad)}
.dir .s .txt{flex:1;display:flex;flex-direction:column;min-width:0;gap:2px}
.dir .s h3{font-size:var(--body);line-height:1.15}
.dir .s .line{display:flex;gap:8px;align-items:center;flex-wrap:wrap;color:var(--muted);font-size:1rem}
.dir .s .desc{font-size:1rem;line-height:1.35;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;color:var(--muted)}
.dir .s .icon{width:72px;height:72px}
.dir .s .ph{font-size:1.6rem}
.dir .s .chip{min-height:20px;padding:0 7px}
.nomatch{padding:var(--pad);background:var(--card);margin-top:var(--gap)}
.stand{grid-auto-rows:auto}
.stand .d{grid-column:span 4;gap:10px;min-height:clamp(150px,13vw,200px);justify-content:space-between}
.stand h3{font-size:var(--head)}
.stand .d .small{opacity:.9}
.stand .d.arch{background:#c4451a;color:#fff}
.stand .d.clib{background:var(--violet);color:var(--on-violet)}
.stand .d.opus{background:#7a1f2b;color:#fff}

/* ---- inner pages ---- */
.crumbs{display:flex;flex-wrap:wrap;gap:4px 10px;align-items:center;margin:0 0 14px;font-size:var(--small);color:var(--muted)}
.crumbs a{color:var(--muted)}
.crumbs span[aria-hidden]{opacity:.6}
.layout{display:grid;grid-template-columns:clamp(220px,19vw,290px) minmax(0,1fr);gap:var(--gap);padding:var(--gap) var(--m) 0;max-width:1640px;margin:0 auto;width:100%;align-items:start}
.layout.no-side{grid-template-columns:minmax(0,1fr)}
.layout.wide{max-width:none}
.layout.no-side>main,.layout.wide>main{padding-top:6px}
.side{position:sticky;top:60px;max-height:calc(100svh - 72px);overflow:auto;background:var(--card);padding:18px 14px 22px;font-size:1rem;line-height:1.35;overscroll-behavior:contain}
.side .who{display:flex;gap:12px;align-items:center;margin:0 4px 14px;color:var(--fg);text-decoration:none;font:400 var(--lead)/1.05 var(--display)}
.side h2,.side summary.h{font:400 var(--small)/1.2 var(--text);letter-spacing:0;color:var(--muted);margin:18px 4px 6px;padding:0 0 0 20px;cursor:pointer;list-style:none;display:block;position:relative}
.side summary.h::-webkit-details-marker{display:none}
.side h2{padding-left:0}
.side ul{list-style:none;margin:0;padding:0}
.side ul ul{margin:2px 0 6px 12px}
.side li{margin:0}
.side a{display:block;padding:6px 8px;color:var(--fg);text-decoration:none}
.side a:hover{background:var(--card2)}
.side a[aria-current]{background:var(--violet);color:var(--on-violet)}
.side details>summary{display:block;cursor:pointer;list-style:none;position:relative;padding-right:0}
.side details>summary::-webkit-details-marker{display:none}
.side details>summary>a{padding-left:24px}
.side details>summary:not(:has(a)){padding:6px 8px 6px 24px}
.side details>summary::before{content:"";position:absolute;left:9px;top:50%;margin-top:-5px;border:5px solid transparent;border-left:7px solid var(--muted);border-right:0;transition:transform .2s}
.side details[open]>summary::before{transform:rotate(90deg)}
main{min-width:0}
.page{background:var(--card);padding:clamp(18px,3.4vw,56px)}
.reading{max-width:44rem;margin:0 auto;min-width:0}
.page.has-toc{display:grid;grid-template-columns:minmax(0,44rem) minmax(11rem,16rem);gap:clamp(24px,4vw,64px);justify-content:space-between;align-items:start}
.page.has-toc .reading{margin:0}
.toc{position:sticky;top:76px;max-height:calc(100svh - 96px);overflow:auto;font-size:var(--small);line-height:1.35;background:var(--bg);padding:14px 12px}
.toc p{margin:0 0 6px 8px;color:var(--muted)}
.toc ul{list-style:none;margin:0;padding:0}
.toc a{display:block;padding:5px 8px;color:var(--fg);text-decoration:none}
.toc a:hover{background:var(--card2)}
.reading>*:first-child{margin-top:0}
.reading h1{font-size:var(--head);line-height:1.05;margin:0 0 .3em}
.deck{font-size:var(--lead);line-height:1.35;color:var(--muted);margin:0 0 1.4em!important}
.reading h2{font-size:var(--lead);margin:2.2em 0 .5em;line-height:1.15}
.reading h3{font-size:var(--body);font-family:var(--text);font-weight:600;letter-spacing:0;margin:1.8em 0 .4em}
.reading h4{font-size:var(--body);font-family:var(--text);font-weight:600;font-style:italic;margin:1.4em 0 .3em}
.reading p,.reading ul,.reading ol,.reading dl{margin:0 0 1em}
.reading li{margin:.25em 0}
.reading ul,.reading ol{padding-left:1.4em}
.reading li::marker{color:var(--accent)}
.reading img{height:auto;display:inline-block;vertical-align:middle}
.reading img[src*="/_img/"]{image-rendering:pixelated}
.reading p>img:only-child:not([src*="shields.io"]){display:block;margin:1.2em 0}
.reading p:has(>img[src*="shields.io"]){display:flex;flex-wrap:wrap;gap:6px;align-items:center;margin:0 0 8px}
.reading p:has(>a>img[src*="shields.io"]){display:flex;flex-wrap:wrap;gap:6px;align-items:center;margin:0 0 8px}
.reading img[src*="shields.io"]{height:22px;width:auto;display:block}
.reading hr{border:0;height:2em;margin:0}
.reading table{display:block;overflow-x:auto;border-collapse:collapse;margin:0 0 1.2em;max-width:100%}
.reading th,.reading td{padding:8px 14px;text-align:left;vertical-align:top;font-size:1rem;line-height:1.4}
.reading th{background:var(--violet);color:var(--on-violet);font-weight:600;white-space:nowrap}
.reading tr:nth-child(even) td{background:var(--bg)}
.reading pre{overflow-x:auto;background:var(--bg);padding:14px 16px;font-size:.9rem;line-height:1.5;margin:0 0 1.2em}
.reading :not(pre)>code{background:var(--bg);padding:.08em .35em}
.reading blockquote{margin:1.2em 0;padding:.6em 1.2em;background:var(--bg);font-style:italic}
.reading blockquote>*:last-child{margin-bottom:0}
.reading em{font-style:italic}
.reading .missing-img{color:var(--muted);font-style:italic}
.item{font-weight:600;background:var(--bg);padding:.02em .4em;white-space:nowrap}
.featured{display:inline-flex;align-items:baseline;gap:.5em;margin:.4em 0 1em;padding:.5em 1em;background:var(--violet);color:var(--on-violet);font:400 var(--lead)/1.1 var(--display)}
.featured .item{background:none;font-weight:400;padding:0;white-space:normal}
.callout{margin:1.4em 0;padding:1em 1.3em;background:color-mix(in srgb,var(--c) 16%,var(--card));--c:var(--violet)}
.callout>*:last-child{margin-bottom:0}
.callout-title{font:400 var(--lead)/1.1 var(--display);margin:0 0 .35em;color:var(--fg)}
.callout-title::before{content:"";display:inline-block;width:.7em;height:.7em;background:var(--c);margin-right:.5em;vertical-align:baseline}
.callout-tip{--c:var(--good)}.callout-warning,.callout-important{--c:var(--warn)}.callout-danger{--c:var(--bad)}.callout-note,.callout-kind{--c:var(--accent)}
.recipe{margin:1.6em 0;padding:16px 20px;background:var(--bg);width:fit-content;max-width:100%}
.recipe figcaption{font-size:var(--small);color:var(--muted);margin-bottom:10px}
.recipe-row{display:flex;align-items:center;gap:clamp(10px,2vw,22px);flex-wrap:wrap}
.rgrid{display:grid;grid-template-columns:repeat(var(--cols),clamp(42px,6vw,54px));gap:4px}
.rc{display:grid;place-items:center;aspect-ratio:1;background:var(--card2);font:400 1.25rem/1 var(--display)}
.rc.empty{background:var(--card)}
.rarrow::before{content:"→";font:400 1.8rem/1 var(--display);color:var(--accent)}
.rresult{background:var(--violet);color:var(--on-violet);padding:12px 18px;font:400 var(--lead)/1.1 var(--display)}
.recipe-legend{list-style:none;margin:12px 0 0;padding:0;font-size:var(--small);columns:2 14em;gap:20px}
.recipe-legend li{margin:2px 0}
.recipe-legend b{display:inline-block;width:1.6em;font:400 1rem/1 var(--display);color:var(--accent)}
.recipe-ref{font-size:var(--small);color:var(--muted)}
.error{color:var(--bad)}
.edit-link{margin:2.4em 0 0;font-size:var(--small);color:var(--muted)}
.edit-link a{color:var(--muted)}

/* mod overview header */
.modhead{margin:0 0 var(--gap);grid-auto-rows:auto}
.modhead .mi{grid-column:span 2;align-items:center;justify-content:center;padding:var(--pad)}
.modhead .mi .icon,.modhead .mi .ph{width:100%;height:100%;max-width:150px;max-height:150px}
.modhead .mi .ph{font-size:clamp(2rem,4vw,3.4rem)}
.modhead .mt{grid-column:span 7;justify-content:space-between;gap:14px;min-height:240px}
.modhead h1{font-size:var(--display-size);line-height:.98;letter-spacing:-.025em;margin:0}
.modhead .mt .lead{max-width:34em;margin-bottom:14px}
.modhead .ma{grid-column:span 3;gap:var(--gap);padding:var(--gap)}
.modhead .ma .btn{flex:1;min-height:48px}
.modhead .ver{font:400 var(--lead)/1 var(--display)}

/* changelog */
.feed{column-width:30rem;column-gap:var(--gap)}
.feed-section{break-inside:avoid;background:var(--card);padding:var(--pad);margin:0 0 var(--gap)}
.feed-section h2{font-size:var(--lead);margin:0 0 .5em}
.feed-section h2 a{color:var(--fg)}
.feed-section h2 .ver{font:400 var(--small)/1 var(--text);color:var(--muted);margin-left:.5em}
.feed-section :is(h3,h4){font-size:var(--body);font-family:var(--text);font-weight:600;margin:1em 0 .2em}
.feed-section ul{padding-left:1.3em;margin:0 0 .6em}
.feed-section p{margin:0 0 .6em}
.jump{display:flex;flex-wrap:wrap;gap:4px;margin:0 0 var(--gap);padding:0;list-style:none}
.jump a{display:inline-flex;align-items:center;min-height:36px;padding:0 12px;background:var(--card);color:var(--fg);text-decoration:none;font-size:var(--small)}
.jump a:hover{background:var(--card2)}
.feedhead{background:var(--violet);color:var(--on-violet);padding:clamp(18px,3vw,40px);margin:0 0 var(--gap)}
.feedhead h1{font-size:var(--display-size);line-height:.98;margin:0 0 .15em}
.feedhead p{margin:0;font-size:var(--lead);max-width:34em}
.feedhead+h2,.feed-title{font-size:var(--head);margin:clamp(48px,6vw,96px) 0 var(--gap)}
.pagehead+.feed-title{margin-top:clamp(24px,3vw,48px)}
.pagehead{background:var(--violet);color:var(--on-violet);padding:clamp(18px,3vw,40px);margin:0 0 var(--gap)}
.pagehead h1{font-size:var(--display-size);line-height:.98;margin:0 0 .15em}
.pagehead p{margin:0;font-size:var(--lead);max-width:36em}

/* ---- motion ---- */
.reveal-on [data-reveal]{opacity:0;transform:translateY(22px);transition:opacity .6s cubic-bezier(.2,.7,.2,1) var(--d,0ms),transform .6s cubic-bezier(.2,.7,.2,1) var(--d,0ms);animation:reveal-failsafe 0s 2.4s forwards}
.reveal-on [data-reveal].in{opacity:1;transform:none;animation:none}
.reveal-on .tile.link[data-reveal].in{transition:opacity .6s cubic-bezier(.2,.7,.2,1) var(--d,0ms),transform .25s cubic-bezier(.2,.7,.2,1),background-color .2s}
@keyframes reveal-failsafe{to{opacity:1;transform:none}}
.reveal-on .mark img{clip-path:inset(0 0 100% 0);transform:scale(1.14)}
.reveal-on .mark.in img{clip-path:inset(0);transform:none;transition:clip-path 1.1s cubic-bezier(.7,0,.2,1) .15s,transform 1.4s cubic-bezier(.2,.7,.2,1) .15s}
@media (prefers-reduced-motion:reduce){
html{scroll-behavior:auto}
*,*::before,*::after{transition-duration:.01ms!important;animation-duration:.01ms!important;animation-delay:0s!important}
.tile.link:hover,.btn:hover{transform:none}
}

/* ---- tablet ---- */
@media (max-width:1100px){
.board{grid-template-columns:repeat(6,minmax(0,1fr))}
.hero{grid-auto-rows:minmax(64px,auto)}
.hero .name{grid-column:1/7;grid-row:auto;min-height:clamp(280px,44vw,380px)}
.hero .mark{grid-column:1/7;grid-row:auto;height:clamp(220px,40vw,340px)}
.hero .mark img{object-fit:contain}
.hero .find{grid-column:1/7;grid-row:auto}
.hero .go{grid-column:1/4;grid-row:auto}
.hero .facts{grid-column:4/7;grid-row:auto}
.feat .xl{grid-column:1/7;grid-row:span 4}
.feat .lg{grid-column:1/4;grid-row:span 4}
.feat .m1{grid-column:4/7;grid-row:span 3}
.feat .m2{grid-column:1/7;grid-row:span 2}
.dir .s{grid-column:span 3}
.stand .d{grid-column:span 3}
.stand .d.opus{grid-column:1/7}
.page.has-toc{display:block}
.toc{display:none}
.layout{grid-template-columns:minmax(0,1fr)}
.side{position:static;max-height:none;order:2;margin-top:var(--gap)}
.side>ul{display:flex;flex-wrap:wrap;gap:4px}
.modhead .mi{grid-column:span 2}.modhead .mt{grid-column:span 4}.modhead .ma{grid-column:1/7;flex-direction:row;flex-wrap:wrap}
.foot-body>img.foot-mark{grid-column:span 4}.foot-name{grid-column:span 8}.foot-links{grid-column:span 6;justify-content:flex-start}.foot-note{grid-column:span 6;gap:24px}
}
@media (max-width:640px){
.board{grid-template-columns:repeat(2,minmax(0,1fr));grid-auto-rows:auto}
.hero .name,.hero .mark,.hero .find,.hero .go,.hero .facts{grid-column:span 2}
.hero .go{grid-column:span 1;min-height:110px}.hero .facts{grid-column:span 1;min-height:110px}
.hero .name{min-height:300px;gap:24px}
.hero .go .arrow{font-size:1.8rem}
.feat>.tile{grid-column:span 2!important;grid-row:auto!important;min-height:200px}
.feat .xl{min-height:300px}
.dir .s{grid-column:span 1;flex-direction:column-reverse;align-items:flex-start;justify-content:flex-end;gap:8px;padding:12px;min-height:116px}
.stand .d,.stand .d.opus{grid-column:span 2}
.modhead .mi{grid-column:span 2;aspect-ratio:auto;min-height:0;padding:14px}
.modhead .mi .icon,.modhead .mi .ph{max-width:84px;max-height:84px}
.modhead .mt{grid-column:span 2;min-height:0}
.modhead .ma{grid-column:span 2}
.modhead .mi{grid-column:span 2}
.foot-body>img.foot-mark{grid-column:span 5}.foot-name{grid-column:span 7;overflow-wrap:anywhere}.foot-links,.foot-note{grid-column:1/-1}
.top{gap:0 8px}
.crumbs{padding-left:var(--pad)}
.top .brand{font-size:1.2rem}
.top nav{order:3;width:100%}
.top nav a:first-child{padding-left:0}
.top .tools{margin-left:auto}
.top .tools a.nav{display:none}
.top a.nav,.top button{padding:0 10px}
.reading table{font-size:.95rem}
}
`;
