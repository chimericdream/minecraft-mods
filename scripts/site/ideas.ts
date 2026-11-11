import {existsSync, readdirSync, readFileSync, statSync} from 'node:fs';
import path from 'node:path';

import {docUrl} from './docs.ts';
import {shell} from './layout.ts';
import type {ModInfo} from './types.ts';
import {esc, REPO_ROOT} from './util.ts';

export type IdeaRow = {
    mod: string;
    modName: string;
    theme: string;
    title: string;
    /** Rendered-page href, relative to the site root. */
    href: string;
    shortlist: number | null;
    tier: number | null;
    effort: string;
    value: number;
    votes: number | null;
    vote: string;
    voteNote: string;
    status: string;
    source: string;
    openQuestions: number;
    summary: string;
};

const stripMd = (s: string) => s.replace(/\[([^\]]+)\]\([^)]*\)/g, '$1').replace(/[*_`]/g, '').trim();

const parseIdea = (mod: ModInfo, theme: string, file: string): IdeaRow => {
    const key = `${mod.folder}/ideas/${theme}/${file}`;
    const lines = readFileSync(path.join(REPO_ROOT, key), 'utf8').split(/\r?\n/);
    const title = stripMd(lines.find(l => l.startsWith('# '))?.slice(2) ?? file.replace(/\.md$/, ''));

    // The header is the run of `> ` lines right after the title.
    const start = lines.findIndex(l => l.startsWith('# ')) + 1;
    const header: string[] = [];
    for (let i = start; i < lines.length; i++) {
        if (lines[i]!.startsWith('>')) header.push(lines[i]!.slice(1).trim());
        else if (lines[i]!.trim() === '' && header.length === 0) continue;
        else break;
    }
    const h = header.join(' · ');
    const grab = (re: RegExp) => h.match(re)?.[1]?.trim();

    const votes = grab(/Votes\s+(\d+)/);
    const shortlist = grab(/Shortlist\s+#(\d+)/);
    const source = grab(/Source:\s*(.+?)(?:\s·\s(?:Effort|Value|Status)|$)/);

    const section = (name: string) => {
        const i = lines.findIndex(l => l.trim().toLowerCase() === `## ${name}`);
        if (i === -1) return [];
        const rest = lines.slice(i + 1);
        const end = rest.findIndex(l => l.startsWith('## '));
        return end === -1 ? rest : rest.slice(0, end);
    };
    const summary = section('description').join('\n').trim().split(/\n\s*\n/)[0]?.replace(/\s+/g, ' ') ?? '';
    const questions = section('open questions').filter(l => /^\s*(?:[-*]|\d+\.)\s+\S/.test(l)).length;

    return {
        mod: mod.folder,
        modName: mod.name,
        theme,
        title,
        href: docUrl(mod.folder, key),
        shortlist: shortlist ? Number(shortlist) : null,
        tier: grab(/Tier\s+(\d)/) ? Number(grab(/Tier\s+(\d)/)) : null,
        effort: grab(/Effort\s+\*\*([^*]+)\*\*/) ?? '—',
        value: (grab(/Value\s+(★+)/) ?? '').length,
        votes: votes ? Number(votes) : null,
        vote: (grab(/My vote\s+\*\*([^*]+)\*\*/) ?? '—').match(/^(Yes|Maybe|No)(?![a-z])/i)?.[1] ?? '—',
        voteNote: grab(/My vote\s+\*\*([^*]+)\*\*/) ?? '',
        status: grab(/Status:\s*\*\*([^*]+)\*\*/) ?? '—',
        source: source ? stripMd(source) : shortlist ? `shortlist #${shortlist}` : '',
        openQuestions: questions,
        summary: stripMd(summary),
    };
};

/** Every active idea file: `<mod>/ideas/<theme>/*.md`, excluding brainstorms and the archive. */
export const collectIdeaRows = (mods: ModInfo[]): IdeaRow[] => {
    const rows: IdeaRow[] = [];
    for (const mod of mods) {
        const root = path.join(REPO_ROOT, mod.folder, 'ideas');
        if (!existsSync(root)) continue;
        for (const theme of readdirSync(root).sort()) {
            if (theme === 'brainstorms' || theme === 'archive') continue;
            const dir = path.join(root, theme);
            if (!statSync(dir).isDirectory()) continue;
            for (const file of readdirSync(dir).filter(f => f.endsWith('.md')).sort()) {
                rows.push(parseIdea(mod, theme, file));
            }
        }
    }
    return rows;
};

const CSS = `
.roadmap-controls{padding:24px;background:var(--card);border:1px solid var(--line);border-radius:4px;margin:24px 0}
.filters{display:grid;grid-template-columns:2fr 1fr 1fr;gap:16px}.filters label{font-size:12px;font-weight:600}.filters input,.filters select{display:block;width:100%;margin-top:6px}
.advanced{margin-top:20px;border-top:1px solid var(--line);padding-top:16px;font-size:13px}.advanced summary{font-weight:600}.advanced>p{color:var(--muted);font-size:12px}
.filter-groups{display:flex;flex-wrap:wrap;gap:16px 32px;margin-top:16px}.filter-groups fieldset{border:0;padding:0;margin:0;min-width:0}.filter-groups legend{font-size:11px;color:var(--muted);margin-bottom:8px}.filter-groups fieldset>span{display:flex;flex-wrap:wrap;gap:6px}
.pill{font-size:12px;min-height:36px;padding:5px 10px}
.roadmap-toolbar{display:flex;gap:12px;align-items:center;flex-wrap:wrap;margin:24px 0 16px;font-size:12px}.roadmap-toolbar #count{margin-right:auto;font-weight:600}.sort-controls{display:flex;align-items:center;gap:8px;flex-wrap:wrap}.sort-controls select,.sort-controls button{font-size:12px}
.idea-list{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px;list-style:none;padding:0;margin:0}
.idea-card{border:1px solid var(--line);background:var(--card);border-radius:4px;padding:24px;display:flex;flex-direction:column;gap:14px;min-width:0;box-shadow:var(--shadow)}
.idea-card h2{font-size:22px;margin:0;line-height:1.3;letter-spacing:-.02em}.idea-card h2 a{text-decoration:none;color:var(--fg)}.idea-card h2 a:hover{color:var(--accent);text-decoration:underline}
.idea-context{font-size:11px;color:var(--muted);display:flex;gap:8px;flex-wrap:wrap}.idea-context a{font-weight:600;text-decoration:none}
.idea-summary{font-size:13px;color:var(--muted);margin:0;line-height:1.75}.idea-tags{display:flex;gap:6px;flex-wrap:wrap}.idea-tags .badge{font-size:11px}
.idea-card details{margin-top:auto;padding-top:12px;border-top:1px solid var(--line);font-size:12px}.idea-card summary{color:var(--accent);font-weight:600}
.idea-meta{display:grid;grid-template-columns:1fr 1fr;gap:12px;margin:16px 0 0}.idea-meta>div{min-width:0}.idea-meta dt{font-size:10px;text-transform:uppercase;letter-spacing:.05em;color:var(--muted)}.idea-meta dd{margin:2px 0 0;overflow-wrap:anywhere}.idea-meta .full{grid-column:1/-1}
@media(max-width:800px){.filters{grid-template-columns:1fr 1fr}.filters label:first-child{grid-column:1/-1}.idea-list{grid-template-columns:1fr}.roadmap-controls{padding:16px}.roadmap-toolbar{align-items:start}.roadmap-toolbar #count{width:100%}.idea-card{padding:20px}}
`;

const JS = String.raw`
(()=>{
const rows=JSON.parse(document.getElementById('data').textContent);
const $=id=>document.getElementById(id);
const cards=[...document.querySelectorAll('.idea-card')];
const ORDER={effort:['S','M','L','XL'],vote:['Yes','Maybe','No']};
const COLS=[['modName','Mod'],['theme','Theme'],['title','Idea title'],['shortlist','Shortlist number'],['tier','Tier'],['effort','Effort'],['value','Value'],['votes','Recorded votes'],['vote','Maintainer vote'],['status','Status'],['openQuestions','Open questions'],['summary','Summary']];
const groups=['effort','value','vote','status','tier'];
const state={q:'',mod:'',theme:'',effort:new Set(),value:new Set(),vote:new Set(),status:new Set(),tier:new Set(),sort:'modName',dir:1};
const uniq=key=>[...new Set(rows.map(row=>row[key]))];
const rank=(key,value)=>{const order=ORDER[key];if(!order)return value;const index=order.indexOf(String(value).split(/[\u2013-]/)[0]);return index<0?99:index};
const option=(select,value,label)=>{const element=document.createElement('option');element.value=value;element.textContent=label;select.append(element)};
option($('mod'),'','All mods');
[...new Map(rows.map(row=>[row.mod,row.modName]))].sort((left,right)=>left[1].localeCompare(right[1])).forEach(([folder,name])=>option($('mod'),folder,name));
COLS.forEach(([key,label])=>option($('sort'),key,label));
const pills=(key,values,label)=>values.forEach(value=>{
  const button=document.createElement('button');button.type='button';button.className='pill';button.textContent=label?label(value):value;button.dataset.value=value;button.setAttribute('aria-pressed','false');
  button.addEventListener('click',()=>{state[key].has(value)?state[key].delete(value):state[key].add(value);render()});$('f-'+key).append(button);
});
for(const key of groups){const values=uniq(key).filter(value=>value!==null).sort((left,right)=>key==='effort'?rank(key,left)-rank(key,right):String(left).localeCompare(String(right),undefined,{numeric:true}));pills(key,values,key==='value'?value=>value+' / 3':key==='tier'?value=>'Tier '+value:null)}
const fillThemes=()=>{
  const themes=[...new Set(rows.filter(row=>!state.mod||row.mod===state.mod).map(row=>row.theme))].sort();
  $('theme').replaceChildren();option($('theme'),'','All themes');themes.forEach(theme=>option($('theme'),theme,theme));
  if(!themes.includes(state.theme))state.theme='';$('theme').value=state.theme;
};
const compare=(left,right)=>{
  const key=state.sort;let first=left[key],second=right[key];
  if(key==='effort'||key==='vote'){first=rank(key,first);second=rank(key,second)}
  if(first==null)first=-1;if(second==null)second=-1;
  const result=typeof first==='number'&&typeof second==='number'?first-second:String(first).localeCompare(String(second),undefined,{numeric:true});
  return result*state.dir||left.modName.localeCompare(right.modName)||left.title.localeCompare(right.title);
};
const matches=row=>{
  if(state.mod&&row.mod!==state.mod||state.theme&&row.theme!==state.theme)return false;
  if(groups.some(key=>state[key].size&&!state[key].has(row[key])))return false;
  const text=(row.modName+' '+row.theme+' '+row.title+' '+row.summary+' '+row.source).toLowerCase();
  return state.q.trim().toLowerCase().split(/\s+/).every(word=>text.includes(word));
};
function save(){
  const params=new URLSearchParams();
  for(const key of ['q','mod','theme'])if(state[key])params.set(key,state[key]);
  for(const key of groups)if(state[key].size)params.set(key,[...state[key]].join(','));
  if(state.sort!=='modName'||state.dir!==1)params.set('sort',state.sort+(state.dir<0?'-':''));
  history.replaceState(null,'',location.pathname+location.search+(params.size?'#'+params:''));
}
function render(){
  const results=rows.map((row,index)=>({row,index})).filter(entry=>matches(entry.row)).sort((left,right)=>compare(left.row,right.row));
  cards.forEach(card=>card.hidden=true);
  const fragment=document.createDocumentFragment();
  results.forEach(({index})=>{cards[index].hidden=false;fragment.append(cards[index])});$('body').append(fragment);
  $('count').textContent=results.length+' of '+rows.length+' ideas';$('empty').hidden=results.length!==0;
  $('sort').value=state.sort;$('direction').textContent=state.dir===1?'Ascending':'Descending';
  let active=0;
  for(const key of groups){active+=state[key].size;$('f-'+key).querySelectorAll('button').forEach(button=>{const value=['value','tier'].includes(key)?Number(button.dataset.value):button.dataset.value;button.setAttribute('aria-pressed',String(state[key].has(value)))})}
  $('filter-count').textContent=active?' ('+active+' selected)':'';save();
}
function restore(){
  const params=new URLSearchParams(location.hash.slice(1));
  state.q=params.get('q')||'';state.mod=params.get('mod')||'';if(!rows.some(row=>row.mod===state.mod))state.mod='';state.theme=params.get('theme')||'';fillThemes();
  for(const key of groups){state[key].clear();for(const raw of (params.get(key)||'').split(',').filter(Boolean)){const value=['value','tier'].includes(key)?Number(raw):raw;if(uniq(key).includes(value))state[key].add(value)}}
  const sort=params.get('sort')||'modName';state.sort=COLS.some(([key])=>key===sort.replace(/-$/,''))?sort.replace(/-$/,''):'modName';state.dir=sort.endsWith('-')?-1:1;
  $('q').value=state.q;$('mod').value=state.mod;
  render();
}
$('q').addEventListener('input',event=>{state.q=event.target.value;render()});
$('mod').addEventListener('change',event=>{state.mod=event.target.value;fillThemes();render()});
$('theme').addEventListener('change',event=>{state.theme=event.target.value;render()});
$('sort').addEventListener('change',event=>{state.sort=event.target.value;render()});
$('direction').addEventListener('click',()=>{state.dir*=-1;render()});
const reset=()=>{history.replaceState(null,'',location.pathname+location.search);restore();$('q').focus()};
$('reset').addEventListener('click',reset);$('empty-reset').addEventListener('click',reset);
window.addEventListener('hashchange',restore);
document.addEventListener('keydown',event=>{if(event.key==='/'&&!event.ctrlKey&&!event.metaKey&&!event.altKey&&!event.target.closest('input,textarea,select,[contenteditable]')){event.preventDefault();$('q').focus()}});
$('controls').hidden=false;$('sorting').hidden=false;
restore();
})();
`;

export const ideasPage = (rows: IdeaRow[]): string => {
    const json = JSON.stringify(rows).replace(/</g, '\\u003c');
    const cards = rows.map(row => {
        const metadata: [string, string | number | null][] = [['Shortlist', row.shortlist], ['Tier', row.tier], ['Effort', row.effort], ['Value', row.value + ' / 3'], ['Recorded votes', row.votes], ['Maintainer vote', row.voteNote || row.vote], ['Status', row.status], ['Open questions', row.openQuestions], ['Source', row.source || 'Not recorded']];
        return `<li class="idea-card"><div class="idea-context"><a href="../mods/${esc(row.mod)}/">${esc(row.modName)}</a><span>/ ${esc(row.theme)}</span></div>
<h2><a href="../${esc(row.href)}">${esc(row.title)}</a></h2><div class="idea-tags"><span class="badge unrel">${esc(row.status)}</span><span class="badge">Effort ${esc(row.effort)}</span><span class="badge">Value ${row.value} / 3</span></div>
<p class="idea-summary">${esc(row.summary) || 'Open this idea for its proposal and context.'}</p><details><summary>Planning details</summary><dl class="idea-meta">${metadata.map(([label, value]) => '<div><dt>' + label + '</dt><dd>' + esc(String(value ?? 'Not recorded')) + '</dd></div>').join('')}</dl></details></li>`;
    }).join('');
    return shell({
        title: 'Roadmap · Chimeric Mods',
        url: 'ideas/',
        wide: true,
        head: `<style>${CSS}</style>`,
        body: `<h1>Roadmap</h1>
<p class="page-intro">Browse feature ideas across the collection. Check the <a href="../changelog/">changelog</a> for released features.</p>
<div class="roadmap-controls" id="controls" hidden><div class="filters"><label for="q">Search ideas<input type="search" id="q" placeholder="Try a feature or mod name" aria-controls="body"></label>
<label for="mod">Mod<select id="mod"></select></label><label for="theme">Theme<select id="theme"></select></label></div>
<details class="advanced"><summary>More filters<span id="filter-count"></span></summary><p>Scores and votes come from the maintainer's planning records. Implemented ideas may still be awaiting release.</p><div class="filter-groups">
<fieldset><legend>Effort</legend><span id="f-effort"></span></fieldset><fieldset><legend>Value</legend><span id="f-value"></span></fieldset><fieldset><legend>Maintainer vote</legend><span id="f-vote"></span></fieldset><fieldset><legend>Tier</legend><span id="f-tier"></span></fieldset><fieldset><legend>Status</legend><span id="f-status"></span></fieldset></div></details></div>
<div class="roadmap-toolbar"><span id="count" role="status">${rows.length} ideas</span><div class="sort-controls" id="sorting" hidden><label for="sort">Sort by</label><select id="sort"></select><button id="direction">Ascending</button><button id="reset">Reset</button></div></div>
<ol class="idea-list" id="body">${cards}</ol><div class="empty-state" id="empty" hidden><h2>No matching ideas</h2><p>Try a broader search, another mod, or fewer filters.</p><button id="empty-reset">Clear search and filters</button></div>
<script type="application/json" id="data">${json}</script><script>${JS}</script>`,
    });
};
