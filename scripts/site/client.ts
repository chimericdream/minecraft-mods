export const THEME_INIT = `try{const theme=localStorage.getItem('chimeric-theme');if(theme==='light'||theme==='dark')document.documentElement.dataset.theme=theme}catch{}`;

export const SITE_JS = String.raw`
(()=>{
const theme=document.getElementById('theme-choice');
theme.hidden=false;
theme.value=document.documentElement.dataset.theme||'system';
theme.addEventListener('change',()=>{
  if(theme.value==='system')delete document.documentElement.dataset.theme;
  else document.documentElement.dataset.theme=theme.value;
  try{localStorage.setItem('chimeric-theme',theme.value)}catch{}
});
const nav=document.querySelector('.section-nav');
if(nav){const desktop=matchMedia('(min-width:1001px)');const sync=()=>nav.open=desktop.matches;sync();desktop.addEventListener('change',sync)}
const toc=document.querySelector('.toc');
if(toc){const wide=matchMedia('(min-width:1201px)');const sync=()=>toc.open=wide.matches;sync();wide.addEventListener('change',sync)}
const search=document.getElementById('catalog-search');
if(!search)return;
document.querySelector('.catalog-tools').hidden=false;
const cards=[...document.querySelectorAll('.card[data-search]')];
const buttons=[...document.querySelectorAll('[data-catalog-filter]')];
let filter='all';
const update=()=>{
  const words=search.value.trim().toLocaleLowerCase().split(/\s+/).filter(Boolean);
  let count=0;
  for(const card of cards){
    card.hidden=!words.every(word=>card.dataset.search.includes(word))||(filter==='guide'&&card.dataset.guide!=='true')||(filter==='changes'&&card.dataset.changes!=='true');
    if(!card.hidden)count++;
  }
  document.getElementById('catalog-count').textContent=count+' of '+cards.length+' mods';
  document.getElementById('catalog-empty').hidden=count!==0;
  buttons.forEach(button=>button.setAttribute('aria-pressed',String(button.dataset.catalogFilter===filter)));
};
search.addEventListener('input',update);
buttons.forEach(button=>button.addEventListener('click',()=>{filter=button.dataset.catalogFilter;update()}));
document.getElementById('catalog-reset').addEventListener('click',()=>{search.value='';filter='all';update();search.focus()});
update();
})();
`;
