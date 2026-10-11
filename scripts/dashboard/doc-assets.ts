import {THEME_CSS} from './theme.ts';

export const DOC_CSS = `${THEME_CSS}
*{box-sizing:border-box}body{margin:0;background:var(--bg);color:var(--fg);font:15px/1.6 system-ui,sans-serif}
a{color:var(--accent);text-decoration:none}a:hover{text-decoration:underline}.muted{color:var(--muted)}
.top{position:sticky;top:0;z-index:5;background:var(--card);border-bottom:1px solid var(--line);padding:8px 16px;display:flex;gap:16px;flex-wrap:wrap;justify-content:space-between;align-items:center}
.crumbs{font-size:14px}.crumbs span{color:var(--muted)}
.tools{display:flex;gap:8px;align-items:center}
input[type=search]{padding:5px 8px;border:1px solid var(--line);border-radius:6px;background:var(--bg);color:var(--fg);min-width:220px}
button{font:inherit;font-size:13px;background:none;border:1px solid var(--line);color:var(--fg);border-radius:6px;padding:3px 10px;cursor:pointer}
button:hover{border-color:var(--accent)}
.layout{display:grid;grid-template-columns:280px minmax(0,1fr);gap:24px;max-width:1300px;margin:0 auto;padding:16px}
.side{position:sticky;top:60px;align-self:start;max-height:calc(100vh - 76px);overflow:auto;font-size:13px;padding-right:8px}
.side h2{font-size:12px;text-transform:uppercase;letter-spacing:.05em;color:var(--muted);margin:16px 0 4px}
.side ul{list-style:none;margin:0;padding-left:12px}.side>details>ul{padding-left:0}
.side summary{cursor:pointer;color:var(--muted)}.side .files>summary{font-weight:600;color:var(--fg);margin-bottom:4px}
.side a.cur{font-weight:600}.toc{padding-left:0!important}.toc .l3{padding-left:12px}.toc .l4{padding-left:24px}
.toc a{display:block;padding:1px 0;color:var(--muted)}.toc a.active{color:var(--accent);font-weight:600}
.content{background:var(--card);border:1px solid var(--line);border-radius:10px;padding:8px 28px 28px;min-width:0}
.content h1,.content h2,.content h3,.content h4{line-height:1.25;scroll-margin-top:70px;position:relative}
.content h2{border-bottom:1px solid var(--line);padding-bottom:4px}
.content h1 .anchor,.content h2 .anchor,.content h3 .anchor,.content h4 .anchor{position:absolute;left:-18px;opacity:0;font-weight:400}
.content :is(h1,h2,h3,h4):hover .anchor{opacity:.6}
.content :is(h2,h3,h4).collapsible{cursor:pointer}
.content :is(h2,h3,h4).collapsible::before{content:"▾";display:inline-block;width:1.1em;color:var(--muted);font-size:.8em}
.content :is(h2,h3,h4).collapsed::before{content:"▸"}
.content img{max-width:100%}
code{font:12.5px ui-monospace,monospace;background:var(--bg);padding:1px 4px;border-radius:4px}
pre{background:var(--bg);border:1px solid var(--line);border-radius:6px;padding:10px;overflow:auto}pre code{padding:0;background:none}
blockquote{margin:12px 0;padding:2px 14px;border-left:3px solid var(--line);color:var(--muted)}
.tablewrap{overflow:auto;margin:12px 0}.tablewrap input{margin-bottom:6px;min-width:180px}
table{border-collapse:collapse;width:100%;font-size:14px}th,td{border:1px solid var(--line);padding:5px 9px;text-align:left;vertical-align:top}
th{background:var(--bg);white-space:nowrap}th.sortable{cursor:pointer;user-select:none}
th.sortable::after{content:" ↕";color:var(--muted);font-size:.8em}th.asc::after{content:" ↑";color:var(--accent)}th.desc::after{content:" ↓";color:var(--accent)}
mark{background:var(--mark);color:inherit;border-radius:2px}mark.cur{outline:2px solid var(--accent)}
li.task{list-style:none;margin-left:-1.2em}
@media (max-width:800px){.layout{grid-template-columns:1fr}.side{position:static;max-height:none}.content{padding:8px 16px 20px}}
`;

export const DOC_JS = `
(()=>{
const content=document.querySelector('.content article');
const $=(s,r=document)=>r.querySelector(s);

// ---- Collapsible sections: wrap everything after a heading, up to the next heading of the same or higher rank.
const heads=[...content.querySelectorAll('h2,h3,h4')];
const sects=[];
for(const h of heads.slice().reverse()){
  const lvl=+h.tagName[1],wrap=document.createElement('div');wrap.className='sect';
  let n=h.nextElementSibling;
  while(n&&!(/^H[1-6]$/.test(n.tagName)&&+n.tagName[1]<=lvl)){const nx=n.nextElementSibling;wrap.appendChild(n);n=nx}
  if(!wrap.childNodes.length)continue;
  h.after(wrap);h.classList.add('collapsible');sects.push([h,wrap]);
  h.addEventListener('click',e=>{if(e.target.closest('a'))return;toggle(h,wrap)});
}
function toggle(h,w,collapse){const c=collapse??!h.classList.contains('collapsed');h.classList.toggle('collapsed',c);w.hidden=c}
$('#collapse').onclick=()=>sects.forEach(([h,w])=>toggle(h,w,true));
$('#expand').onclick=()=>sects.forEach(([h,w])=>toggle(h,w,false));
// Changelogs are long: show only the two newest releases open.
if(document.body.dataset.kind==='changelog'){
  const rel=sects.filter(([h])=>h.tagName==='H3').reverse();
  rel.forEach(([h,w],i)=>{if(i>1)toggle(h,w,true)});
}
// Deep links open whatever they land inside.
const reveal=el=>{for(let p=el.parentElement;p;p=p.parentElement){if(p.classList.contains('sect')){p.hidden=false;p.previousElementSibling?.classList.remove('collapsed')}}};
if(location.hash){const t=document.getElementById(decodeURIComponent(location.hash.slice(1)));if(t){reveal(t);if(t.classList.contains('collapsible'))toggle(t,t.nextElementSibling,false);t.scrollIntoView()}}

// ---- Tables: click headers to sort, filter box for longer ones.
const num=s=>{const m=s.replace(/,/g,'').match(/-?\\d+(\\.\\d+)?/);return m?parseFloat(m[0]):NaN};
content.querySelectorAll('table').forEach(t=>{
  const wrap=document.createElement('div');wrap.className='tablewrap';t.replaceWith(wrap);wrap.appendChild(t);
  const body=t.tBodies[0];if(!body)return;
  const rows=[...body.rows];
  if(rows.length>5){
    const f=document.createElement('input');f.type='search';f.placeholder='Filter rows…';wrap.prepend(f);
    f.oninput=()=>{const q=f.value.toLowerCase();rows.forEach(r=>r.hidden=q&&!r.textContent.toLowerCase().includes(q))};
  }
  [...t.tHead?.rows[0]?.cells??[]].forEach((th,col)=>{
    th.classList.add('sortable');
    th.onclick=()=>{
      const dir=th.classList.contains('asc')?-1:1;
      t.querySelectorAll('th').forEach(x=>x.classList.remove('asc','desc'));
      th.classList.add(dir===1?'asc':'desc');
      const val=r=>r.cells[col]?.textContent.trim()??'';
      const numeric=rows.every(r=>!val(r)||!isNaN(num(val(r))));
      rows.slice().sort((a,b)=>{
        const x=val(a),y=val(b);
        return dir*(numeric?(num(x)||0)-(num(y)||0):x.localeCompare(y,undefined,{numeric:true}));
      }).forEach(r=>body.appendChild(r));
    };
  });
});

// ---- In-page search with highlight; Enter / Shift+Enter cycle through matches.
const find=$('#find'),count=$('#findcount');let marks=[],idx=-1;
const clear=()=>{marks.forEach(m=>{m.replaceWith(document.createTextNode(m.textContent))});content.normalize();marks=[];idx=-1;count.textContent=''};
const run=()=>{
  clear();const q=find.value.trim();if(q.length<2)return;
  const re=new RegExp(q.replace(/[.*+?^\${}()|[\\]\\\\]/g,'\\\\$&'),'gi');
  const w=document.createTreeWalker(content,NodeFilter.SHOW_TEXT,{acceptNode:n=>n.parentElement.closest('script,style')?2:1});
  const nodes=[];while(w.nextNode())nodes.push(w.currentNode);
  for(const n of nodes){
    const text=n.textContent;if(!re.test(text)){re.lastIndex=0;continue}re.lastIndex=0;
    const frag=document.createDocumentFragment();let last=0,m;
    while((m=re.exec(text))){frag.append(text.slice(last,m.index));const k=document.createElement('mark');k.textContent=m[0];frag.append(k);marks.push(k);last=m.index+m[0].length}
    frag.append(text.slice(last));n.replaceWith(frag);
  }
  count.textContent=marks.length?marks.length+' matches':'no matches';
  if(marks.length)step(1);
};
const step=d=>{if(!marks.length)return;marks[idx]?.classList.remove('cur');idx=(idx+d+marks.length)%marks.length;const m=marks[idx];m.classList.add('cur');reveal(m);m.scrollIntoView({block:'center'});count.textContent=(idx+1)+' / '+marks.length};
find.addEventListener('input',run);
find.addEventListener('keydown',e=>{if(e.key==='Enter'){e.preventDefault();step(e.shiftKey?-1:1)}if(e.key==='Escape'){find.value='';clear();find.blur()}});
document.addEventListener('keydown',e=>{if(e.key==='/'&&document.activeElement!==find&&!/INPUT|TEXTAREA/.test(document.activeElement.tagName)){e.preventDefault();find.focus()}});

// ---- Highlight the current heading in the table of contents.
const links=new Map([...document.querySelectorAll('.toc a')].map(a=>[a.getAttribute('href').slice(1),a]));
const obs=new IntersectionObserver(es=>{for(const e of es)if(e.isIntersecting){links.forEach(a=>a.classList.remove('active'));links.get(e.target.id)?.classList.add('active')}},{rootMargin:'-70px 0px -75% 0px'});
heads.forEach(h=>obs.observe(h));
})();
`;
