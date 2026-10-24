/** Public styles. The local dashboard consumes only these shared colour tokens. */
const DARK = '--bg:#17121e;--fg:#eceff1;--muted:#bdb1c4;--card:#241b2d;--line:#493a53;--accent:#f0a3b4;--good:#9aceaa;--bad:#f5a09b;--warn:#ebc080;--mark:#574626;--paper:#241b2d;--paper-deep:#30213c;--binding:#392345;--soft:#211929;--action:#a92e48;--action-ink:#fff5f0;--focus:#f0a3b4';

export const THEME_CSS = `
:root{color-scheme:light;--bg:#eeedf0;--fg:#271f2c;--muted:#665b6e;--card:#f7f4ee;--line:#cfc7d2;--accent:#8b1e38;--good:#2c633b;--bad:#9d2929;--warn:#735011;--mark:#e9daaa;--paper:#f7f4ee;--paper-deep:#e1dae4;--binding:#3a2349;--soft:#e5dfe7;--action:#8b1e38;--action-ink:#fff5f0;--focus:#8b1e38}
@media(prefers-color-scheme:dark){:root:not([data-theme=light]){color-scheme:dark;${DARK}}}
:root[data-theme=dark]{color-scheme:dark;${DARK}}
`;

export const SITE_CSS = `${THEME_CSS}
*{box-sizing:border-box}
html{scroll-behavior:smooth;scroll-padding-top:28px}
body{margin:0;background:var(--bg);color:var(--fg);font:18px/1.6 'STIX Two Text',Georgia,serif;-webkit-font-smoothing:antialiased}
a{color:var(--accent);text-decoration-thickness:.06em;text-underline-offset:.2em}
a:hover{text-decoration:underline}
button,input,select{font:inherit;color:inherit}
button,select{cursor:pointer}
a,button,summary,input,select{-webkit-tap-highlight-color:transparent}
:focus-visible{outline:3px solid var(--focus);outline-offset:5px}
::selection{background:var(--mark);color:var(--fg)}
[hidden]{display:none!important}
.sr-only{position:absolute;width:1px;height:1px;overflow:hidden;clip-path:inset(50%);white-space:nowrap}
.skip-link{position:absolute;top:8px;left:16px;transform:translateY(-150%);z-index:20;background:var(--action);color:var(--action-ink);padding:10px 20px}
.skip-link:focus{transform:none}
.muted{color:var(--muted)}
.top,.footer-inner{max-width:1440px;margin:auto;padding:22px 40px;display:flex;align-items:center;gap:32px}
.top{font-family:'Work Sans',sans-serif;font-size:14px;min-height:96px}
.top a{text-decoration:none;color:var(--fg)}
.brand{display:flex;gap:12px;align-items:center;margin-right:auto;font-family:'STIX Two Text',Georgia,serif;font-size:28px;letter-spacing:-.04em}
.brand img{width:46px;height:46px;object-fit:cover}
.top nav{display:flex;gap:26px;align-items:center}
.top nav a{padding:10px 0}
.top nav a[aria-current=page]{color:var(--accent);text-decoration:underline}
.theme-label{display:flex;align-items:center;gap:8px;color:var(--muted)}
#theme-picker{appearance:auto;border:0;background:var(--soft);padding:9px 7px;font:14px 'Work Sans',sans-serif;min-height:40px}
.crumbs{max-width:1440px;margin:0 auto;padding:14px 40px;background:var(--soft);font:14px/1.5 'Work Sans',sans-serif;display:flex;gap:12px;flex-wrap:wrap}
.crumbs a{color:var(--fg)}
.layout{display:grid;grid-template-columns:248px minmax(0,1fr);max-width:1360px;margin:40px auto 80px;gap:56px;padding:0 40px}
.layout.no-side{display:block;max-width:1160px}
.layout.wide{max-width:1600px}
.layout.home{display:block;max-width:1440px;margin-top:0;padding:0 40px}
.side{font:15px/1.5 'Work Sans',sans-serif;min-width:0}
.side-menu{position:sticky;top:24px;max-height:calc(100svh - 48px);overflow:auto;scrollbar-width:thin;padding:22px 20px;background:var(--soft)}
.side-menu>summary{display:none}
.side h2{font:600 16px/1.4 'Work Sans',sans-serif;letter-spacing:0;margin:28px 0 12px}
.side h2:first-child{margin-top:0}
.side ul{list-style:none;margin:0;padding-left:16px}.side nav>ul{padding-left:0}
.side li{margin:5px 0}.side a{color:var(--muted);display:inline-block;padding:4px 0;text-decoration:none}
.side summary{cursor:pointer;padding:3px 0}
.side .cur{font-weight:600;color:var(--accent);text-decoration:underline}
main{min-width:0}
h1,h2,h3,h4,h5,h6{font-family:'STIX Two Text',Georgia,serif;font-weight:400;letter-spacing:-.03em;line-height:1.13;text-wrap:balance}
h1{font-size:clamp(38px,4.4vw,64px);margin:0 0 28px}
h2{font-size:36px;margin:48px 0 20px}h3{font-size:27px;margin:32px 0 14px}h4,h5,h6{font-size:22px;margin:28px 0 12px}
p{margin:0 0 20px;text-wrap:pretty}
article{max-width:74ch}
article>h1{margin-top:0}
article p,article li{max-width:70ch}
article ul,article ol{padding-left:26px}article li{margin:6px 0}
article img{max-width:100%;height:auto;image-rendering:pixelated}
article a img{vertical-align:middle}
article table{border-collapse:collapse;display:block;max-width:100%;overflow-x:auto;font:15px/1.5 'Work Sans',sans-serif;margin:24px 0}
article th,article td{padding:12px 14px;text-align:left;vertical-align:top;border-bottom:1px solid var(--line)}
article th{background:var(--soft);font-weight:600}article td{background:var(--paper)}
article pre{overflow-x:auto;background:var(--soft);padding:20px;font-size:14px;line-height:1.6}
article :not(pre)>code{background:var(--soft);padding:2px 5px;font-size:.85em;overflow-wrap:anywhere}
article blockquote{margin:24px 0;padding:20px 24px;background:var(--soft);color:var(--fg)}
article blockquote p:last-child{margin-bottom:0}
.edit-link{margin-top:48px;font:14px 'Work Sans',sans-serif}
.item{font-weight:600}.featured{font-size:1.2em;margin:8px 0}.missing-img{color:var(--muted);font-style:italic}
.callout{background:var(--soft);padding:20px 24px;margin:24px 0}
.callout p:last-child{margin-bottom:0}.callout-title{font:600 15px 'Work Sans',sans-serif;margin:0 0 12px;color:var(--accent)}
.callout-warning .callout-title,.callout-important .callout-title{color:var(--warn)}.callout-danger .callout-title{color:var(--bad)}.callout-tip .callout-title{color:var(--good)}
.recipe{margin:28px 0;padding:20px 24px;background:var(--paper);width:fit-content;max-width:100%}
.recipe figcaption{font:15px 'Work Sans',sans-serif;color:var(--muted)}
.recipe-diagram{margin:16px 0;padding:0;background:none;font:14px/1.15 Consolas,monospace;overflow-x:auto}
.recipe-legend{list-style:none;margin:0;padding:0;font:14px/1.6 'Work Sans',sans-serif;columns:2}
.recipe-legend b{display:inline-block;width:1.5em}
.error{color:var(--bad)}
.badge{display:inline-block;font:13px/1.6 'Work Sans',sans-serif;margin-right:10px;color:var(--muted)}
.badge.beta{color:var(--warn)}.badge.unrel{color:var(--accent)}.badge.new{color:var(--good)}
.icon{width:48px;height:48px;object-fit:contain;image-rendering:pixelated;flex:none}
.ph{display:grid;place-items:center;font:600 18px 'Work Sans',sans-serif;color:var(--fg);background:var(--soft)}
.modhead{display:grid;grid-template-columns:104px 1fr;gap:28px;align-items:center;margin:4px 0 28px}
.modhead>.icon{width:104px;height:104px}.modhead h1{margin:0 0 14px}.modhead p{margin:0;font-size:22px;line-height:1.45}
.mod-meta{display:flex;gap:16px;flex-wrap:wrap;font:14px/1.6 'Work Sans',sans-serif;margin:20px 0;color:var(--muted)}
.actions{display:flex;gap:12px;align-items:center;flex-wrap:wrap;margin:24px 0 40px;font:14px 'Work Sans',sans-serif}
.actions a{padding:10px 14px;min-height:44px;background:var(--soft);text-decoration:none;color:var(--fg)}
.actions a.primary,.primary{display:inline-flex;justify-content:center;align-items:center;min-height:48px;padding:13px 24px;background:var(--action);color:var(--action-ink);text-decoration:none;font:500 15px 'Work Sans',sans-serif;border:0}
.feed-section{margin:28px 0 40px;padding:28px;background:var(--paper)}
.feed-section h2{font-size:28px;margin:0 0 20px}.feed-section .ver{display:block;font:14px 'Work Sans',sans-serif;margin-top:10px;color:var(--muted)}
.home-intro{text-align:center;padding:54px 0 56px}
.home-intro h1{font-size:clamp(52px,7.8vw,108px);line-height:1;letter-spacing:-.055em;max-width:1100px;margin:0 auto 30px}
.home-intro p{font-size:22px;line-height:1.4;max-width:55ch;margin:0 auto 28px}
.folder-tabs{display:flex;align-items:end;gap:5px;padding-left:24px;font:15px 'Work Sans',sans-serif}
.folder-tabs a{padding:14px 34px 16px;min-height:50px;background:var(--paper-deep);color:var(--fg);text-decoration:none;clip-path:polygon(0 12px,12px 0,calc(100% - 12px) 0,100% 12px,100% 100%,0 100%)}
.folder-tabs a:first-child{background:var(--binding);color:#f3eef5}
.feature-sheet{background:var(--binding);color:#f3eef5;padding:40px;position:relative}
.feature-intro{display:flex;justify-content:space-between;gap:40px;align-items:baseline;margin-bottom:34px}
.feature-intro h2{font-size:40px;margin:0}.feature-intro p{max-width:34ch;margin:0;font-size:18px}
.feature-band{display:grid;grid-template-columns:1.15fr .85fr;gap:20px}
.feature-entry{display:grid;grid-template-columns:1fr 40%;align-items:center;min-width:0;gap:18px;padding:28px;background:var(--paper);color:var(--fg)}
.feature-entry h3{font-size:34px;margin:0 0 12px}.feature-entry h3 a{color:var(--fg);text-decoration:none}
.feature-entry p{font-size:18px;line-height:1.45;margin:0 0 20px}
.feature-entry .icon{width:100%;height:auto;aspect-ratio:1;max-width:220px;justify-self:center}
.feature-entry nav{display:flex;gap:18px;flex-wrap:wrap;font:14px 'Work Sans',sans-serif}
.feature-entry .ver{font:13px 'Work Sans',sans-serif;color:var(--muted);margin-bottom:12px;display:block}
.feature-minekea{grid-row:span 2;grid-template-columns:1fr;align-content:space-between;padding:34px}
.feature-minekea .feature-art{grid-row:1;justify-self:center;width:78%;max-width:270px}
.feature-minekea .icon{max-width:none}.feature-minekea h3{font-size:50px}
.feature-hopper-xtreme{background:var(--paper-deep)}
.feature-effective-gear{background:var(--paper)}
.feature-camel-nostrils{grid-column:1/-1;grid-template-columns:1.2fr .8fr;background:var(--paper-deep);padding:28px 34px;min-height:210px}
.feature-camel-nostrils .feature-art{justify-self:end;width:190px}.feature-camel-nostrils p{max-width:45ch}
.feature-art{display:block;min-width:0}
.feature-note{font:14px/1.5 'Work Sans',sans-serif;margin:28px 0 0;max-width:70ch}
.catalogue{padding:110px 0 40px;scroll-margin-top:24px}
.catalogue-heading{display:flex;align-items:baseline;justify-content:space-between;gap:30px;margin-bottom:30px}
.catalogue h2{font-size:clamp(44px,5.8vw,80px);margin:0;line-height:1}
.catalogue-heading p{font:15px 'Work Sans',sans-serif;margin:0}
.catalogue-controls{display:flex;align-items:end;gap:18px;margin:0 0 40px;font:14px 'Work Sans',sans-serif}
.catalogue-controls label{display:grid;gap:10px}.catalogue-controls label:first-child{flex:1}
.catalogue-controls input,.catalogue-controls select{min-height:50px;border:0;background:var(--paper);padding:12px 16px;border-radius:0;min-width:0;width:100%}
.catalogue-controls input{font-size:16px}
.catalogue-list{list-style:none;padding:0;margin:0;display:grid;grid-template-columns:1fr 1fr;column-gap:48px;row-gap:6px;background:var(--paper);padding:12px 0}
.catalogue-row{padding:20px 0;display:grid;grid-template-columns:48px minmax(0,1fr);gap:18px;align-content:start}

.catalogue-row h3{font-size:24px;line-height:1.15;margin:0 0 8px}.catalogue-row h3 a{color:var(--fg);text-decoration:none}
.catalogue-row p{font-size:17px;line-height:1.4;margin:0 0 10px}
.row-meta{display:flex;justify-content:space-between;gap:12px;flex-wrap:wrap;font:13px/1.5 'Work Sans',sans-serif;color:var(--muted);margin-bottom:12px}
.catalogue-row nav{display:flex;justify-content:space-between;gap:16px;flex-wrap:wrap;font:13px/1.8 'Work Sans',sans-serif}
.no-results{font-size:22px;padding:30px;background:var(--paper)}
.site-footer{margin-top:80px;background:var(--soft)}
.footer-inner{align-items:start;justify-content:space-between;padding-top:50px;padding-bottom:44px;gap:50px}
.footer-message h2{font-size:38px;margin:0 0 16px}.footer-message p{max-width:37ch;font-size:18px;margin:0}
.footer-links{display:flex;gap:32px;flex-wrap:wrap;font:15px/1.8 'Work Sans',sans-serif}
.footer-wordmark{max-width:1440px;margin:auto;padding:0 40px 20px;font-size:clamp(42px,11.5vw,164px);line-height:1.1;letter-spacing:-.06em;overflow-wrap:anywhere}
@media(min-width:1100px){.feature-intro h2{position:sticky;top:36px}.feature-band{align-items:stretch}}
@media(max-width:1000px){.top,.footer-inner{padding-left:24px;padding-right:24px;gap:22px}.top nav{gap:18px}.brand{font-size:25px}.brand img{width:38px;height:38px}.layout{padding:0 24px;gap:30px;grid-template-columns:210px minmax(0,1fr)}.layout.home{padding:0 24px}.crumbs{padding-left:24px;padding-right:24px}.feature-sheet{padding:28px}.feature-entry{padding:22px;grid-template-columns:1fr}.feature-minekea{grid-row:span 2}.feature-minekea .feature-art{width:100%}.feature-camel-nostrils{grid-template-columns:1fr 160px}.feature-entry:not(.feature-minekea) .feature-art{width:130px;grid-row:1;justify-self:start}.feature-camel-nostrils .feature-art{grid-row:auto!important;justify-self:end!important}.feature-intro h2{font-size:34px}.feature-intro{gap:24px}.catalogue-list{column-gap:22px}.footer-wordmark{padding-left:24px;padding-right:24px}.theme-label>span{display:none}}
@media(max-width:760px){body{font-size:18px}.top{padding:16px 20px;flex-wrap:wrap;gap:12px 20px;min-height:100px}.brand{flex:1;font-size:27px}.top nav{order:3;justify-content:space-between;width:100%;gap:12px}.top nav a{padding:5px 0;font-size:14px}.top nav .repo-link{margin-left:auto}.theme-label{margin-left:auto}.crumbs{padding:12px 20px}.layout,.layout.no-side,.layout.wide{display:block;padding:0 20px;margin:28px auto 50px}.layout.home{padding:0 20px;margin:0 auto}.side{margin-bottom:30px}.side-menu{position:static;max-height:none;padding:16px 20px}.side-menu>summary{display:list-item;font-size:16px;cursor:pointer}.side-menu nav{padding-top:20px}.home-intro{padding:40px 0}.home-intro h1{font-size:clamp(44px,12.5vw,76px);line-height:1.03;margin-bottom:24px}.home-intro p{font-size:19px;line-height:1.45;max-width:40ch}.folder-tabs{padding-left:0;gap:4px}.folder-tabs a{padding:13px 15px;font-size:13px;flex:1;text-align:center;min-width:0}.feature-sheet{padding:24px 20px;margin:0 -4px}.feature-intro{display:block;margin-bottom:24px}.feature-intro h2{font-size:32px;margin-bottom:14px}.feature-intro p{font-size:18px;max-width:34ch}.feature-band{display:flex;flex-direction:column;gap:16px}.feature-entry{padding:24px;grid-template-columns:1fr;gap:20px}.feature-entry h3{font-size:32px}.feature-entry p{font-size:18px}.feature-entry .feature-art{grid-row:1;justify-self:center!important;width:145px!important}.feature-minekea .feature-art{width:180px!important}.feature-minekea h3{font-size:40px}.feature-camel-nostrils{min-height:0}.feature-camel-nostrils .feature-art{grid-row:1!important}.feature-note{font-size:13px}.catalogue{padding-top:68px}.catalogue-heading{align-items:start;margin-bottom:26px}.catalogue h2{font-size:44px;max-width:7ch}.catalogue-heading p{font-size:13px;max-width:11ch;text-align:right;line-height:1.5}.catalogue-controls{flex-wrap:wrap;gap:18px}.catalogue-controls label:first-child{flex-basis:100%}.catalogue-controls label:last-child{width:100%}.catalogue-list{display:block}.catalogue-row{padding:22px 0;gap:14px}.catalogue-row:nth-child(n){background:transparent}.catalogue-row h3{font-size:24px}.catalogue-row p{font-size:17px}.modhead{grid-template-columns:1fr;gap:20px}.modhead>.icon{width:88px;height:88px}.modhead h1{font-size:44px}.modhead p{font-size:21px}.mod-meta{gap:10px 18px}.actions{gap:10px}.actions a{padding:12px}.actions a.primary{width:100%}article h1{font-size:38px}h2{font-size:31px}h3{font-size:25px}.recipe{padding:16px 12px}.recipe-legend{columns:1}.feed-section{padding:24px 18px}.feed-section h2{font-size:26px}.site-footer{margin-top:50px}.footer-inner{display:block;padding:36px 20px}.footer-message h2{font-size:34px}.footer-links{margin-top:28px;gap:24px}.footer-wordmark{padding:0 20px 24px;font-size:clamp(38px,11.6vw,88px)}}

/* The catalogue's type scale: metadata, text, lead, heading, display, and closing wordmark. */
:root{--type-small:14px;--type-body:18px;--type-lead:24px;--type-head:40px;--type-display:clamp(48px,7.8vw,108px)}
.top{font-size:var(--type-small)}.brand{font-size:var(--type-lead)}
.home-intro h1,.catalogue h2{font-size:var(--type-display)}
.home-intro p{font-size:var(--type-lead)}
.feature-intro h2,.feature-entry h3,.feature-minekea h3,.footer-message h2{font-size:var(--type-head)}
.feature-entry p,.feature-intro p,.catalogue-row p,.footer-message p{font-size:var(--type-body)}
.catalogue-row h3{font-size:var(--type-lead)}
.feature-entry .ver,.feature-entry nav,.catalogue-row nav,.row-meta,.badge,.feature-note,.catalogue-heading p,.catalogue-controls,.folder-tabs a,.footer-links,.primary{font-size:var(--type-small)}
.feature-minekea .feature-art{max-width:224px}
@media(min-width:1100px){.feature-sheet{display:grid;grid-template-columns:200px minmax(0,1fr);gap:32px}.feature-intro{position:sticky;top:32px;align-self:start;display:block;margin:0}.feature-intro h2{position:static;margin:0 0 24px}.feature-note{grid-column:2;margin:0}.feature-band{grid-template-columns:1.05fr .95fr}.feature-entry{padding:24px;grid-template-columns:1fr 40%}.feature-minekea{grid-template-columns:1fr}.feature-minekea h3{font-size:var(--type-head)}.feature-camel-nostrils{grid-template-columns:1.2fr .8fr}}
@media(max-width:760px){:root{--type-head:32px}.home-intro p{font-size:var(--type-body)}.catalogue-heading{display:block}.catalogue h2{max-width:none}.catalogue-heading p{text-align:left;max-width:none;margin-top:16px}.catalogue-list{padding:0}.feature-entry .feature-art{width:145px!important}.feature-minekea .feature-art{width:180px!important}.feature-camel-nostrils .feature-art{grid-row:1!important}}

.catalogue-controls input{font-size:var(--type-body)}
@media(min-width:761px) and (max-width:1099px){.feature-minekea{grid-row:auto;align-content:start;gap:28px}.feature-camel-nostrils{grid-column:auto;grid-template-columns:1fr}.feature-camel-nostrils .feature-art{grid-row:1!important;justify-self:start!important}}
@media(min-width:1100px){.feature-minekea{align-content:center;gap:32px}}
@media(prefers-reduced-motion:no-preference){
  :root{--ease-out:cubic-bezier(.16,1,.3,1)}
  .top a,.primary,.actions a,.folder-tabs a,.feature-art,.catalogue-row a,.footer-links a,button,select{transition:transform 180ms var(--ease-out)}
  .top a:hover,.primary:hover,.actions a:hover,.folder-tabs a:hover,.feature-art:hover,.footer-links a:hover{transform:translateY(-2px)}
  .primary:active,.actions a:active,button:active{transform:translateY(1px)}
  .motion-in{animation:copy-enter 750ms var(--ease-out) both}
  .feature-art.motion-in{animation:picture-enter 950ms var(--ease-out) both}
  .catalogue-row.motion-in{animation:row-enter 600ms var(--ease-out) both}
  .feature-minekea .icon{animation:book-drift 6s ease-in-out infinite alternate}
  @keyframes copy-enter{from{opacity:.6;transform:translateY(20px)}to{opacity:1;transform:none}}
  @keyframes picture-enter{from{opacity:.7;transform:scale(.94)}to{opacity:1;transform:none}}
  @keyframes row-enter{from{opacity:.6;transform:translateX(12px)}to{opacity:1;transform:none}}
  @keyframes book-drift{from{transform:translateY(3px)}to{transform:translateY(-5px)}}
}
@media(prefers-reduced-motion:reduce){html{scroll-behavior:auto}*,*::before,*::after{animation:none!important;transition:none!important}}
`;
