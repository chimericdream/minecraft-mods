/**
 * Runs in <head> before first paint: applies a remembered theme and marks the page as scripted, so the reveal
 * animations only hide anything when script is running. Every effect has a CSS fail-safe and a no-script path.
 */
export const HEAD_BOOT = `<script>(()=>{const r=document.documentElement;r.classList.add('js');try{const t=localStorage.getItem('theme');if(t==='light'||t==='dark')r.dataset.theme=t}catch(e){}if(!matchMedia('(prefers-reduced-motion:reduce)').matches)r.classList.add('reveal-on')})()</script>`;

/** The site's only script (about 60 lines): theme toggle, mod finder, collapsing sidebar tree, entrance reveals. */
export const SITE_JS = `(()=>{
const root=document.documentElement;
const $=(s,c=document)=>c.querySelector(s);
const $$=(s,c=document)=>[...c.querySelectorAll(s)];

// theme toggle: remembers the choice, otherwise follows the OS
const btn=$('.theme-toggle');
const current=()=>root.dataset.theme||(matchMedia('(prefers-color-scheme:dark)').matches?'dark':'light');
if(btn){
  const label=()=>{btn.textContent=current()==='dark'?'Light theme':'Dark theme'};
  label();
  btn.addEventListener('click',()=>{
    const next=current()==='dark'?'light':'dark';
    root.dataset.theme=next;
    try{localStorage.setItem('theme',next)}catch(e){}
    label();
  });
}

// on narrow screens the guide tree starts folded so the page content comes first
if(matchMedia('(max-width:1100px)').matches)$$('details[data-fold]').forEach(d=>{d.open=false});

// home: filter the mod tiles as you type; "/" focuses the box, Enter opens the first match
const find=$('#finder');
if(find){
  const tiles=$$('[data-find]');
  const count=$('#find-count');
  const none=$('#nomatch');
  const run=()=>{
    const words=find.value.toLowerCase().split(/\\s+/).filter(Boolean);
    let shown=0;
    tiles.forEach(t=>{const ok=words.every(w=>t.dataset.find.includes(w));t.hidden=!ok;if(ok)shown++});
    $$('[data-group]').forEach(g=>{g.hidden=!$$('[data-find]',g).some(t=>!t.hidden)});
    if(count)count.textContent=words.length?shown+' of '+tiles.length+' mods':tiles.length+' mods';
    if(none)none.hidden=shown>0;
  };
  find.addEventListener('input',run);
  find.form.addEventListener('submit',e=>{
    e.preventDefault();
    const hit=tiles.find(t=>!t.hidden);
    if(hit)location.href=hit.href;
  });
  document.addEventListener('keydown',e=>{
    if(e.key==='/'&&!/INPUT|TEXTAREA|SELECT/.test(document.activeElement.tagName)){e.preventDefault();find.focus()}
  });
}

// entrance reveals: parts of a section arrive in turn as it scrolls into view
if(root.classList.contains('reveal-on')&&'IntersectionObserver' in window){
  $$('.reading>h1,.reading>.deck,.reading>h2,.reading>h3,.reading>ul,.reading>.callout,.reading>.recipe,.reading>table,.reading>pre,.reading>blockquote,.reading>p>img:only-child').forEach(el=>el.setAttribute('data-reveal',''));
  const items=$$('[data-reveal]');
  const io=new IntersectionObserver(entries=>{
    const seen=entries.filter(e=>e.isIntersecting).sort((a,b)=>a.boundingClientRect.top-b.boundingClientRect.top||a.boundingClientRect.left-b.boundingClientRect.left);
    seen.forEach((e,i)=>{e.target.style.setProperty('--d',Math.min(i,10)*70+'ms');e.target.classList.add('in');io.unobserve(e.target)});
  },{rootMargin:'0px 0px -6% 0px',threshold:.05});
  items.forEach(el=>io.observe(el));
}else $$('[data-reveal]').forEach(el=>el.classList.add('in'));
})();
`;
