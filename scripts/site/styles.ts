export const THEME_CSS = `
:root{color-scheme:light;--bg:#f4f2eb;--fg:#25382f;--muted:#59685e;--card:#fffef9;--line:#d6d9cd;--accent:#285e45;--good:#286241;--bad:#a23232;--warn:#85531b;--mark:#f4e2a7;--soft:#e7ece0;--inverse:#fffef9;--shadow:0 8px 24px #25382f08}
@media(prefers-color-scheme:dark){:root:not([data-theme=light]){color-scheme:dark;--bg:#18221e;--fg:#e6e9dc;--muted:#a8b8ab;--card:#202e27;--line:#3b4b40;--accent:#a6d5ad;--good:#9dce9d;--bad:#ffaaa0;--warn:#e4bb7c;--mark:#655328;--soft:#2b3b30;--inverse:#18221e;--shadow:none}}
:root[data-theme=dark]{color-scheme:dark;--bg:#18221e;--fg:#e6e9dc;--muted:#a8b8ab;--card:#202e27;--line:#3b4b40;--accent:#a6d5ad;--good:#9dce9d;--bad:#ffaaa0;--warn:#e4bb7c;--mark:#655328;--soft:#2b3b30;--inverse:#18221e;--shadow:none}
`;

export const SITE_CSS = `
${THEME_CSS}
*{box-sizing:border-box}
html{scroll-padding-top:100px}
body{margin:0;background:var(--bg);color:var(--fg);font:16px/1.65 "Segoe UI",system-ui,sans-serif;overflow-wrap:break-word}
a{color:var(--accent);text-decoration-thickness:1px;text-underline-offset:4px}a:hover{text-decoration:underline}
button,input,select{font:inherit;color:var(--fg)}button,select,summary{cursor:pointer}
button,select,input[type=search]{border:1px solid var(--line);background:var(--card);border-radius:4px;min-height:42px;padding:8px 12px}
button:hover{border-color:var(--accent);background:var(--soft)}input[type=search]{width:100%;min-width:0}
input::placeholder{color:var(--muted);opacity:1}
:focus-visible{outline:3px solid var(--accent);outline-offset:4px}[hidden]{display:none!important}
.sr-only{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap}
.skip{position:fixed;left:12px;top:-100px;padding:12px 20px;background:var(--card);z-index:10}.skip:focus{top:12px}
.muted,.ver{color:var(--muted)}
.eyebrow{font:600 11px/1.5 "Segoe UI",system-ui,sans-serif;letter-spacing:.14em;text-transform:uppercase;color:var(--muted)}
.top{position:sticky;top:0;z-index:5;border-bottom:1px solid var(--line);background:var(--card)}
.top-inner{max-width:1440px;margin:auto;display:flex;gap:32px;align-items:center;padding:14px 32px}
.brand{display:flex;align-items:center;gap:10px;font-size:17px;font-weight:750;color:var(--fg);text-decoration:none;white-space:nowrap}
.brand-mark{width:28px;height:28px;background:var(--accent);color:var(--inverse);display:grid;place-items:center;font:700 21px Georgia,serif;border-radius:2px 10px 2px 2px}
.top nav{display:flex;gap:24px;font-size:14px}.top nav a{color:var(--muted);text-decoration:none;padding:6px 0}
.top nav a[aria-current]{color:var(--accent);box-shadow:0 2px var(--accent);font-weight:650}
.top-tools{margin-left:auto;display:flex;gap:18px;align-items:center;font-size:13px}.top-tools>a{color:var(--muted);text-decoration:none}
.theme-control{min-height:34px;padding:3px 6px;font-size:12px;max-width:100px}
.layout{display:grid;grid-template-columns:236px minmax(0,1fr);max-width:1440px;margin:0 auto;min-height:75vh}
.layout.no-side{grid-template-columns:minmax(0,1fr);max-width:1280px}.layout.wide{max-width:1440px}
.side{border-right:1px solid var(--line);padding:28px 20px;min-width:0}
.section-nav{position:sticky;top:95px;max-height:calc(100dvh - 115px);overflow:auto;scrollbar-width:thin}
.section-nav>summary{font-size:12px;font-weight:650;color:var(--muted);padding:10px 0}
.side h2{font:650 11px/1.6 "Segoe UI",sans-serif;letter-spacing:.12em;text-transform:uppercase;color:var(--muted);margin:24px 0 10px}
.side ul{list-style:none;margin:0;padding-left:12px}.side nav>ul{padding-left:0}.side li{margin:3px 0;font-size:13px;line-height:1.5}
.side a{display:inline-block;padding:6px 8px;color:var(--muted);text-decoration:none;border-radius:3px}
.side a:hover{background:var(--soft);color:var(--fg)}.side .cur{font-weight:650;color:var(--accent);background:var(--soft);box-shadow:inset 2px 0 var(--accent)}.side summary{padding:2px 0}
main{padding:32px 40px 64px;min-width:0}
.crumbs{display:flex;flex-wrap:wrap;gap:7px;font-size:12px;color:var(--muted);margin:0 0 24px}.crumbs a{color:var(--muted);text-decoration:none}
h1,h2,h3{line-height:1.2;overflow-wrap:anywhere}h1{font:500 clamp(30px,3.2vw,44px)/1.12 Georgia,"Times New Roman",serif;letter-spacing:-.035em;margin:8px 0 22px}
h2{font-size:24px;letter-spacing:-.025em;margin:36px 0 16px}h3{font-size:19px;margin:28px 0 12px}
.page-intro{max-width:680px;color:var(--muted);margin:0 0 28px}
.reader{display:grid;grid-template-columns:minmax(0,740px) 184px;gap:36px;align-items:start}
.reader article{grid-column:1;grid-row:1;min-width:0;max-width:740px}
.toc{grid-column:2;grid-row:1;position:sticky;top:100px;font-size:12px;border-left:1px solid var(--line);padding-left:18px;max-height:calc(100dvh - 130px);overflow:auto;scrollbar-width:thin}
.toc summary{font-weight:650;letter-spacing:.06em;text-transform:uppercase;font-size:10px;margin-bottom:12px}.toc ol{list-style:none;padding:0;margin:0}.toc li{margin:8px 0}.toc .sub{padding-left:10px}
.toc a{color:var(--muted);text-decoration:none}.toc a:hover{color:var(--accent)}
article{max-width:740px;min-width:0}article p,article li{line-height:1.8}article h1{margin-top:0}article h2{padding-top:12px;border-top:1px solid var(--line);margin-top:36px}

.permalink{font:400 .7em system-ui;margin-left:8px;opacity:0;text-decoration:none}:is(h2,h3):hover .permalink,.permalink:focus-visible{opacity:1}
article img{max-width:100%;height:auto;border-radius:3px}article img[src*="shields.io"]{border-radius:0;vertical-align:middle}
article table{border-collapse:collapse;display:block;max-width:100%;overflow-x:auto;font-size:14px;margin:24px 0}
article th,article td{border:1px solid var(--line);padding:10px 14px;vertical-align:top}
article th:not([align]),article td:not([align]){text-align:left}article th{background:var(--soft);font-weight:650}
article pre{max-width:100%;overflow-x:auto;background:var(--card);border:1px solid var(--line);border-radius:4px;padding:18px 20px;font:13px/1.7 ui-monospace,Consolas,monospace}
code{font-family:ui-monospace,Consolas,monospace;font-size:.88em}pre code{font-size:inherit}article :not(pre)>code{background:var(--soft);border-radius:3px;padding:2px 5px;overflow-wrap:anywhere}
article blockquote{margin:20px 0;padding:4px 20px;border-left:3px solid var(--line);color:var(--muted)}
.edit-link{margin-top:32px;font-size:12px;color:var(--muted)}.item{font-weight:600;border-bottom:1px dotted var(--muted)}
.featured{font-size:1.15em;margin:12px 0}.missing-img{color:var(--muted);font-style:italic}
.callout{border:1px solid var(--line);border-left:3px solid var(--accent);background:var(--soft);padding:4px 20px;margin:24px 0;border-radius:3px}
.callout-title{font-weight:650;font-size:14px;margin:12px 0 0}.callout-warning,.callout-important{border-left-color:var(--warn)}.callout-danger{border-left-color:var(--bad)}.callout-tip{border-left-color:var(--good)}
.recipe{margin:24px 0;padding:16px 20px;border:1px solid var(--line);border-top:3px solid var(--accent);border-radius:3px;background:var(--card);width:fit-content;max-width:100%}
.recipe figcaption{font-size:13px;color:var(--muted);font-weight:650}
.recipe-flow{display:flex;align-items:center;gap:12px;flex-wrap:wrap;margin:12px 0 16px}
.recipe-slots{display:grid;grid-template-columns:repeat(var(--recipe-columns),32px);gap:3px;padding:0;margin:0;list-style:none}
.recipe-slots li{display:grid;place-items:center;min-height:32px;border:1px solid var(--line);border-radius:3px;background:var(--soft);font:600 13px ui-monospace,Consolas,monospace}
.recipe-slots .recipe-slot-empty{background:var(--bg)}
.recipe-arrow{color:var(--accent)}.recipe-result{min-width:0}
.recipe-result span{display:block;color:var(--muted);font-size:11px}.recipe-result strong{font-size:14px}
.recipe-legend{list-style:none;margin:0;padding:0;font-size:13px;columns:2}.recipe-legend b{display:inline-block;width:1.5em;font-family:ui-monospace,Consolas,monospace}.error{color:var(--bad)}
.badge{display:inline-block;font:500 10px/1.6 "Segoe UI",sans-serif;padding:2px 6px;border-radius:3px;border:1px solid var(--line);vertical-align:middle;letter-spacing:.01em}
.badge.beta{color:var(--warn)}.badge.unrel{color:var(--accent);background:var(--soft)}.badge.new{color:var(--good)}
.readme-badge{font-size:12px;padding:3px 8px;margin:2px 2px 2px 0;background:var(--card)}
article p:has(.readme-badge){margin:4px 0}
.catalog-intro{display:grid;grid-template-columns:1fr 270px;gap:48px;padding:10px 0 32px;border-bottom:1px solid var(--line)}
.catalog-intro h1{font-size:clamp(36px,4vw,54px);max-width:620px;margin:14px 0 18px}.catalog-intro p{max-width:580px;margin:0;color:var(--muted)}
.field-note{border-left:2px solid var(--accent);padding:6px 0 6px 24px;align-self:center;font-size:13px}.field-note strong{display:block;font:500 28px Georgia,serif;color:var(--fg);margin-bottom:8px}.field-note a{display:inline-block;margin-top:16px;font-weight:600}
.catalog-tools{display:flex;gap:24px;align-items:end;padding:28px 0 16px}.catalog-search{flex:1;max-width:480px}.catalog-search label{display:block;font-size:12px;font-weight:650;margin-bottom:8px}
.catalog-filters{display:flex;gap:6px;flex-wrap:wrap}.catalog-filters button{font-size:12px}button[aria-pressed=true]{color:var(--inverse);background:var(--accent);border-color:var(--accent)}
.catalog-meta{display:flex;justify-content:space-between;align-items:center;font-size:12px;color:var(--muted);margin:0 0 18px}.catalog-meta p{margin:0}
.cards{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:16px;padding:0;list-style:none;margin:0}
.card{border:1px solid var(--line);border-radius:4px;padding:22px;background:var(--card);display:flex;flex-direction:column;gap:16px;box-shadow:var(--shadow);transition:transform .15s,border-color .15s}.card:hover{border-color:var(--accent);transform:translateY(-2px)}
.card header{display:flex;gap:14px;align-items:center;min-width:0}.card header>div{min-width:0}.card h2{margin:0;font-size:17px;line-height:1.3;letter-spacing:-.01em}.card h2 a{color:var(--fg);text-decoration:none}
.card p{margin:0;font-size:13px;color:var(--muted);line-height:1.7}.card .ver{font:11px/1.5 ui-monospace,Consolas,monospace;margin-top:5px}
.card-status{display:flex;gap:5px;flex-wrap:wrap}.card-status:empty{display:none}.card nav{margin-top:auto;display:flex;gap:12px;flex-wrap:wrap;border-top:1px solid var(--line);padding-top:14px;font-size:12px}.card nav a{text-decoration:none}.card nav a:first-child{font-weight:650}
.icon{width:48px;height:48px;border-radius:4px;image-rendering:pixelated;flex:none;object-fit:contain}.ph{display:grid;place-items:center;font:500 22px Georgia,serif;color:var(--accent);background:var(--soft);border:1px solid var(--line);image-rendering:auto}
.modhead{display:flex;gap:20px;align-items:start;margin-bottom:20px;max-width:800px}.modhead .icon{width:64px;height:64px}.modhead h1{margin:6px 0 12px}.modhead p{margin:0;color:var(--muted)}
.mod-version{display:flex;gap:8px;align-items:center;flex-wrap:wrap;font-size:12px;margin-top:12px}.actions{display:flex;gap:8px;flex-wrap:wrap;margin:24px 0 32px}
.actions a{border:1px solid var(--line);border-radius:4px;padding:9px 16px;background:var(--card);font-size:13px;text-decoration:none}.actions a:first-child{background:var(--accent);color:var(--inverse);border-color:var(--accent)}
.planning-note{font-size:13px;color:var(--muted);padding:14px 18px;background:var(--soft);border-left:3px solid var(--warn);margin-bottom:24px;max-width:740px}
.pager{display:flex;gap:16px;justify-content:space-between;border-top:1px solid var(--line);margin-top:40px;padding-top:24px;max-width:740px}.pager a{flex:1;padding:16px;border:1px solid var(--line);background:var(--card);text-decoration:none;font-size:14px}.pager span{display:block;font-size:11px;color:var(--muted);margin-bottom:4px}
.feed-section{margin:20px 0;padding:8px 24px 20px;background:var(--card);border:1px solid var(--line);border-radius:4px}.feed-section h2{border:0;margin:8px 0;font-size:20px}.feed-section .ver{display:block;font:12px/1.6 ui-monospace,monospace;margin-top:6px}.feed-section h3{font-size:16px}.feed-section ul{padding-left:20px}.feed-jump{display:flex;gap:18px;font-size:13px;margin:20px 0 32px}
.empty-state{padding:32px;border:1px dashed var(--line);background:var(--card);text-align:center}.empty-state p{color:var(--muted)}
.footer{border-top:1px solid var(--line);max-width:1376px;margin:0 auto;padding:24px 32px;display:flex;justify-content:space-between;gap:16px;color:var(--muted);font-size:12px}
@media(min-width:1001px){.section-nav>summary{display:none}.section-nav::details-content{content-visibility:visible;display:block}.section-nav:not([open])>nav{display:block}}
@media(max-width:1200px){main{padding:28px}.reader{grid-template-columns:minmax(0,1fr)}.reader article{grid-row:2}.toc{grid-column:1;grid-row:1;position:static;max-height:none;border:1px solid var(--line);padding:12px 16px;margin-bottom:4px}.toc ol{columns:2}.toc summary{margin:0}.toc[open] summary{margin-bottom:12px}.cards{grid-template-columns:repeat(2,minmax(0,1fr))}}
@media(max-width:1000px){.layout{grid-template-columns:minmax(0,1fr)}.side{border:0;border-bottom:1px solid var(--line);padding:8px 28px}.section-nav{position:static;max-height:none}.section-nav>summary{font-size:13px;padding:10px 0;color:var(--accent)}.section-nav nav{padding-bottom:16px;max-height:60dvh;overflow:auto}.catalog-intro{gap:24px;grid-template-columns:1fr 220px}}
@media(max-width:600px){html{scroll-padding-top:136px}.top-inner{gap:10px;padding:12px 20px;flex-wrap:wrap}.brand{font-size:16px}.top nav{order:3;width:100%;gap:24px;font-size:13px}.top-tools{gap:8px}.top-tools>a{display:none}.theme-control{max-width:82px;font-size:11px}.layout main{padding:24px 20px 40px}.side{padding:6px 20px}.crumbs{margin-bottom:20px}.catalog-intro{display:block;padding:0 0 24px}.catalog-intro h1{font-size:37px}.catalog-intro p{font-size:14px}.field-note{display:none}.catalog-tools{display:block;padding:22px 0 14px}.catalog-filters{margin-top:12px}.catalog-filters button{padding:6px 10px}.catalog-meta{gap:12px;align-items:start}.cards{grid-template-columns:1fr;gap:12px}.card{padding:20px}.toc ol{columns:1}.reader{gap:20px}.modhead{gap:14px}.modhead .icon{width:44px;height:44px}.modhead h1{font-size:32px}.modhead p{font-size:14px}.actions{gap:6px}.actions a{padding:8px 10px;font-size:12px}.recipe{padding:12px}.recipe-legend{columns:1}.pager{flex-direction:column}.feed-section{padding:6px 16px 16px}.footer{padding:20px;flex-direction:column}.permalink{opacity:1}}
@media(prefers-reduced-motion:reduce){*,*::before,*::after{scroll-behavior:auto!important;transition:none!important;animation:none!important}}
`;
