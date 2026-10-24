/** Public catalogue styles; color tokens are also consumed by the local dashboard. */
const LIGHT = `--bg:#f3f1ed;--fg:#28212c;--muted:#645868;--card:#e7e2e8;--line:#bcb1c0;--accent:#8b1a32;--good:#27623e;--bad:#a32432;--warn:#785015;--mark:#e9dba9;--inverse:#f3f1ed;--night:#20152b;--plate:#ded5e2;--plate-alt:#e9dcd5;color-scheme:light`;
const DARK = `--bg:#17121e;--fg:#ece8ee;--muted:#bcb0c2;--card:#251d2e;--line:#594860;--accent:#f1a2ae;--good:#91d6a5;--bad:#ffabb4;--warn:#e7c27b;--mark:#544422;--inverse:#211323;--night:#0e0a14;--plate:#302039;--plate-alt:#3a262c;color-scheme:dark`;
export const THEME_CSS = `:root{${LIGHT}}@media(prefers-color-scheme:dark){:root:not([data-theme=light]){${DARK}}}:root[data-theme=dark]{${DARK}}:root[data-theme=light]{${LIGHT}}`;

export const SITE_CSS = `
${THEME_CSS}
*{box-sizing:border-box}
html{scroll-padding-top:24px}
body{margin:0;background:var(--bg);color:var(--fg);font:16px/1.65 'Literata',Georgia,serif}
button,input,select{font:inherit}
a{color:var(--accent);text-decoration-thickness:.08em;text-underline-offset:.22em}
a:hover{text-decoration:underline}
a:focus-visible,button:focus-visible,input:focus-visible,select:focus-visible,summary:focus-visible{outline:3px solid var(--accent);outline-offset:5px}
button,summary{cursor:pointer}
[hidden]{display:none!important}
h1,h2,h3,h4,h5,h6{font-weight:500;line-height:1.15;letter-spacing:-.025em;text-wrap:balance;overflow-wrap:anywhere}
h1{font-size:clamp(36px,4.5vw,64px);margin:0 0 32px}
h2{font-size:clamp(28px,3vw,40px)}
h3{font-size:24px}h4,h5,h6{font-size:20px}
p{max-width:65ch;text-wrap:pretty}
.sr-only{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap}
.skip{position:absolute;left:24px;top:-100px;background:var(--bg);padding:12px;z-index:20}.skip:focus{top:12px}
.muted,.ver{color:var(--muted)}
.top{display:flex;gap:32px;align-items:center;flex-wrap:wrap;padding:20px 32px;max-width:1600px;margin:auto}
.top a{text-decoration:none;color:var(--fg)}
.brand{display:flex;align-items:center;gap:12px;font-size:20px;font-weight:600;letter-spacing:-.04em}
.brand img{width:44px;height:44px}
.top nav{display:flex;gap:24px}.top nav a{padding:10px 0}
.top a:hover{text-decoration:underline}
.top .push{margin-left:auto}
.theme-control{display:flex;gap:8px;align-items:center;font-size:14px}
.theme-control select{background:var(--card);color:var(--fg);border:0;padding:10px;max-width:110px}
.crumbs{display:flex;gap:12px;flex-wrap:wrap;max-width:1536px;margin:0 auto;padding:12px 32px;color:var(--muted);font-size:14px}
.layout{display:grid;grid-template-columns:240px minmax(0,1fr);max-width:1280px;margin:32px auto 0;gap:48px;padding:0 32px}
.layout.no-side{grid-template-columns:minmax(0,1fr)}
.layout.wide,.layout.home{max-width:1600px}
main{min-width:0;padding-bottom:80px}
.side{font-size:16px;align-self:start;background:var(--card);padding:24px;overflow-wrap:anywhere}
.side h2{font-size:20px;margin:28px 0 12px}.side h2:first-child{margin-top:0}
.side ul{list-style:none;margin:0;padding-left:14px}.side>ul{padding-left:0}
.side li{margin:8px 0}.side summary{padding:4px 0}.side .cur{color:var(--fg);font-weight:600}
.side a{text-decoration:none}.side a:hover{text-decoration:underline}
.side-toggle{display:none}
article{font-size:18px;max-width:75ch}
article h2{margin:56px 0 24px}article h3{margin:36px 0 16px}
article img{max-width:100%;height:auto}
article img[src*='texture'],article img[src*='_assets']{image-rendering:pixelated}
article li{margin:8px 0}article ul,article ol{padding-left:24px}
article table{border-collapse:collapse;display:block;overflow-x:auto;max-width:100%;font-size:16px}
article th,article td{border:1px solid var(--line);padding:12px 16px;text-align:left;vertical-align:top}
article th{background:var(--card)}
article pre{overflow-x:auto;background:var(--card);padding:20px;max-width:100%;font-size:15px}
article :not(pre)>code{background:var(--card);padding:2px 5px;overflow-wrap:anywhere}
article blockquote{margin:24px 0;padding:16px 24px;background:var(--card)}
.edit-link{margin-top:48px;font-size:14px}
.item{font-weight:600}.featured{font-size:1.2em;margin:8px 0}
.missing-img{color:var(--muted);font-style:italic}
.callout{background:var(--card);padding:8px 24px;margin:24px 0}
.callout-title{font-weight:600;color:var(--accent)}
.callout-warning .callout-title,.callout-important .callout-title{color:var(--warn)}.callout-danger .callout-title{color:var(--bad)}.callout-tip .callout-title{color:var(--good)}
.recipe{margin:24px 0;padding:20px;background:var(--card);width:fit-content;max-width:100%}
.recipe figcaption{font-size:16px}.recipe-diagram{margin:16px 0;padding:0;background:none;font:14px/1.15 monospace;overflow-x:auto}
.recipe-legend{list-style:none;margin:0;padding:0;font-size:16px;columns:2}.recipe-legend b{display:inline-block;width:1.5em}
.error{color:var(--bad)}
.wide .filters{gap:16px;align-items:end;margin:32px 0}
.wide .filters fieldset{display:block}.wide .filters legend{float:none;font-size:14px;margin-bottom:8px;color:var(--fg)}
.wide .filters fieldset>span{display:flex;flex-wrap:wrap;gap:6px}
.wide .filters input,.wide .filters select{min-height:44px;font-size:16px;border-radius:0;padding:10px;max-width:100%}
.wide .filters .pill,.wide #reset{font-size:14px;border:0;border-radius:0;min-height:44px;padding:8px 12px;background:var(--card);color:var(--fg)}
.wide .filters .pill.on{background:var(--accent);color:var(--inverse)}
.wide .wrap{border:0;border-radius:0}.wide th{font-size:14px;color:var(--fg);padding:16px 12px}.wide td{padding:16px 12px;font-size:16px}.wide td.sum{font-size:16px;color:var(--fg);min-width:300px}.wide tr:nth-child(even){background:var(--bg)}
.badge{display:inline-block;font-size:13px;margin:0 8px 0 0;color:var(--muted)}
.badge.beta{color:var(--warn)}.badge.new{color:var(--good)}
.icon{width:48px;height:48px;object-fit:contain;image-rendering:pixelated;flex:none}
.ph{display:grid;place-items:center;font-weight:600;color:var(--fg);background:var(--card);font-size:20px}
.modhead{display:flex;gap:28px;align-items:center;margin-bottom:24px}
.modhead>.icon{width:112px;height:112px}.modhead h1{margin:0 0 16px}.modhead p{margin:8px 0}
.modhead .ver{font-size:14px;display:flex;gap:12px;flex-wrap:wrap}
.actions{display:flex;gap:12px;flex-wrap:wrap;margin:24px 0 48px}
.actions a,.button{display:inline-flex;align-items:center;justify-content:center;min-height:48px;padding:10px 20px;background:var(--card);color:var(--fg);text-decoration:none}
.actions a:first-child,.button{background:var(--accent);color:var(--inverse)}
.actions a:hover,.button:hover{ text-decoration:underline }
.feed-section{margin:32px 0;padding:24px;background:var(--card)}.feed-section h2{margin:0 0 24px;font-size:28px}.feed-section .ver{display:block;font-size:16px;margin-top:12px}
.home{margin-top:12px}.home main{padding-bottom:0}
.masthead{display:grid;grid-template-columns:5fr 2.8fr 3.3fr 2.8fr;margin:0;list-style:none;padding:0;min-height:320px}
.plate{min-width:0;background:var(--plate);padding:24px;display:flex;flex-direction:column;position:relative}
.plate:nth-child(even){background:var(--plate-alt)}
.plate:first-child{background:var(--night);color:#ece8ee}
.plate a{color:inherit;text-decoration:none}.plate a:hover{text-decoration:underline}
.plate h2{font-size:clamp(22px,2vw,30px);margin:16px 0 12px}
.plate p{font-size:16px;margin:0 0 16px;line-height:1.5}
.plate .icon{height:160px;width:100%;object-fit:contain;margin-bottom:auto}
.plate:first-child .icon{height:180px}
.plate .ver{font-size:13px;margin-top:auto}.plate:first-child .ver{color:#c3b5cb}
.hero-copy{display:grid;grid-template-columns:42% 58%;gap:0;padding:56px 0 72px;align-items:center}
.hero-copy .intro{grid-column:1;grid-row:1;padding-right:48px;font-size:18px}
.hero-copy .intro p{max-width:32ch;margin:0 0 28px}
.hero-copy h1{grid-column:2;grid-row:1;font-size:clamp(54px,6.5vw,100px);line-height:1.02;letter-spacing:-.045em;margin:0}
.hero-copy h1 span{display:block}.hero-copy h1>.title-line{overflow:hidden;padding-bottom:.12em;margin-bottom:-.12em}
.catalogue{padding:56px 0 80px}
.catalogue-head{display:grid;grid-template-columns:42% 58%;align-items:end;margin-bottom:40px}
.catalogue h2{margin:0 0 20px}.catalogue-head p{margin:0}
.catalogue-search{display:flex;gap:12px;align-items:end;flex-wrap:wrap}
.catalogue-search label{flex:1;font-size:16px}.catalogue-search input{display:block;width:100%;background:var(--card);color:var(--fg);border:0;border-bottom:2px solid var(--accent);padding:14px 16px;margin-top:8px;min-height:52px}
.catalogue-search button{background:var(--card);color:var(--fg);border:0;padding:14px 16px;min-height:52px}
.catalogue-count{font-size:14px;color:var(--muted);margin:16px 0 24px!important}
.mod-list{display:grid;grid-template-columns:1fr 1fr;column-gap:64px;row-gap:0;list-style:none;margin:0;padding:0}
.mod-row{display:grid;grid-template-columns:48px minmax(0,1fr);gap:20px;padding:24px 0}

.mod-row h3{font-size:24px;margin:0 0 10px}.mod-row h3 a{color:var(--fg);text-decoration:none}.mod-row h3 a:hover{text-decoration:underline}
.mod-row p{font-size:16px;margin:0 0 12px}.mod-row .ver{font-size:13px}
.mod-row nav{display:flex;gap:16px;flex-wrap:wrap;font-size:14px;margin-top:12px}
.site-footer{background:var(--night);color:#ece8ee;padding:48px 32px;margin-top:48px}
.footer-inner{max-width:1536px;margin:auto}.footer-name{font-size:clamp(36px,8vw,116px);line-height:1.1;letter-spacing:-.045em;margin:0 0 40px;overflow-wrap:anywhere}
.footer-bottom{display:flex;justify-content:space-between;gap:32px;flex-wrap:wrap}.site-footer a{color:#ece8ee}.site-footer nav{display:flex;gap:24px;flex-wrap:wrap}.site-footer p{margin-top:0}
@media(prefers-reduced-motion:no-preference){
  .hero-copy .title-line>span{animation:title-rise .8s cubic-bezier(.16,1,.3,1) both}.hero-copy .title-line:nth-child(2)>span{animation-delay:90ms}
  .hero-copy .intro{animation:ink-in .7s ease-out both}
  .plate .icon{animation:object-settle 1.1s cubic-bezier(.16,1,.3,1) both}
  .mod-row.arrive{animation:row-in .6s ease-out both}.mod-row:nth-child(even).arrive{animation-delay:70ms}
  .catalogue-head.arrive{animation:ink-in .7s ease-out both}
  .button,.actions a,button,.plate a,.mod-row a{transition:transform .18s ease-out,opacity .18s ease-out}
  .button:active,.actions a:active,button:active{transform:translateY(2px)}
  .plate a:hover,.mod-row a:hover{opacity:.8}
  @keyframes title-rise{from{transform:translateY(100%)}to{transform:translateY(0)}}
  @keyframes object-settle{from{transform:scale(.94)}to{transform:scale(1)}}
  @keyframes ink-in{from{opacity:.65;transform:translateY(16px)}to{opacity:1;transform:translateY(0)}}
  @keyframes row-in{from{opacity:.65;transform:translateX(12px)}to{opacity:1;transform:translateX(0)}}
}
@media(max-width:1000px){.top{gap:20px}.layout{gap:28px;grid-template-columns:210px minmax(0,1fr)}.masthead{grid-template-columns:1.4fr 1fr 1fr 1fr}.plate{padding:16px}.plate .icon,.plate:first-child .icon{height:130px}.plate h2{font-size:22px}.hero-copy .intro{padding-right:24px}.mod-list{column-gap:48px}.modhead{align-items:flex-start}.modhead>.icon{width:72px;height:72px}}
@media(max-width:760px){.top{padding:16px 20px;gap:12px 20px}.brand{font-size:18px}.brand img{width:36px;height:36px}.top nav{order:3;width:100%;gap:24px;font-size:14px}.top .push{margin-left:auto;font-size:14px}.theme-control{margin-left:auto}.theme-control>span{display:none}.theme-control select{padding:8px;min-height:44px}.top .push{display:none}.crumbs{padding:8px 20px}.layout{display:flex;flex-direction:column;margin-top:24px;padding:0 20px;gap:24px}.side{width:100%;padding:0;background:none}.side-toggle{display:block;width:100%;text-align:left;background:var(--card);color:var(--fg);padding:14px 16px;border:0}.side-content{padding:20px;background:var(--card)}.js .side-toggle[aria-expanded=false]+.side-content{display:none}.modhead{gap:16px}.modhead>.icon{width:64px;height:64px}.modhead h1{font-size:32px}article{font-size:17px}article h1{font-size:36px}article h2{font-size:28px}.actions{gap:8px}.actions a{font-size:14px;padding:10px 14px}.home{margin-top:0}.home main{display:flex;flex-direction:column}.hero-copy{order:-1;display:flex;flex-direction:column;align-items:stretch;padding:24px 0 40px}.hero-copy h1{order:-1;font-size:clamp(40px,8vw,60px);margin-bottom:24px}.hero-copy .intro{padding:0;font-size:18px}.hero-copy .intro p{max-width:48ch;margin-bottom:20px}.masthead{grid-template-columns:1.15fr 1fr;min-height:0}.plate{padding:20px 16px}.plate .icon,.plate:first-child .icon{height:120px}.plate h2{font-size:22px;min-height:50px}.plate p{font-size:16px}.catalogue{padding:64px 0 32px}.catalogue-head{display:block;margin-bottom:24px}.catalogue-search{margin-top:24px}.mod-list{grid-template-columns:1fr}.mod-row{gap:16px;padding:24px 0}.mod-row:nth-child(n){margin:0;padding:24px 0;background:none}.mod-row h3{font-size:22px}.site-footer{padding:40px 20px}.footer-name{margin-bottom:32px}.site-footer nav{gap:20px}.recipe-legend{columns:1}.feed-section{padding:20px}.feed-section h2{font-size:24px}}
`;
