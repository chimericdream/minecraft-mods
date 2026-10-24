import {existsSync, readdirSync, readFileSync, statSync} from 'node:fs';
import path from 'node:path';

import {docUrl} from './docs.ts';
import {shell} from './layout.ts';
import type {ModInfo} from './types.ts';
import {REPO_ROOT} from './util.ts';

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
        value: (grab(/Value\s+(\u2605+)/) ?? '').length,
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
.filters{display:flex;flex-wrap:wrap;gap:20px 24px;align-items:center;margin:32px 0;font:14px/1.5 'Work Sans',sans-serif;padding:24px;background:var(--soft)}
.filters fieldset{border:0;padding:0;margin:0;display:flex;flex-wrap:wrap;gap:8px;align-items:center;min-width:0}.filters legend{float:left;margin-right:10px;color:var(--muted);font-size:14px}
.filters fieldset>span{display:flex;flex-wrap:wrap;gap:5px}
.extra-filters{flex-basis:100%;min-width:0}.extra-filters>summary{display:none}.filter-groups{display:flex;flex-wrap:wrap;gap:20px 24px;align-items:center}
.filters input[type=search],.filters select{padding:10px 14px;border:0;border-radius:0;background:var(--paper);color:var(--fg);min-height:44px;font-size:15px;max-width:100%}
.filters input[type=search]{flex:1;min-width:200px}
.filters button{font:14px/1.5 'Work Sans',sans-serif;background:var(--paper);border:0;color:var(--fg);padding:9px 12px;min-height:40px;cursor:pointer}
.filters .pill.on{background:var(--action);color:var(--action-ink)}
.wrap{overflow:auto;background:var(--paper);max-width:100%;scrollbar-width:thin}
.wrap table{border-collapse:collapse;width:100%;font:14px/1.5 'Work Sans',sans-serif}
.wrap th,.wrap td{padding:14px 16px;border-bottom:1px solid var(--line);text-align:left;vertical-align:top}
.wrap th{position:sticky;top:0;background:var(--soft);white-space:nowrap;font-weight:500}
.wrap th button{border:0;padding:0;background:none;font:inherit;color:var(--fg);cursor:pointer;min-height:32px;text-align:left}
.wrap th.asc button::after{content:' ↑';color:var(--accent)}.wrap th.desc button::after{content:' ↓';color:var(--accent)}
.wrap td.n{white-space:nowrap}.wrap td:first-child{min-width:220px}.wrap td.sum{min-width:280px;max-width:420px;font-size:14px}
.wrap td.sum div{display:-webkit-box;-webkit-line-clamp:3;-webkit-box-orient:vertical;overflow:hidden}
.vote-Yes{color:var(--good);font-weight:600}.vote-Maybe{color:var(--warn);font-weight:600}.vote-No{color:var(--bad)}
.stars{color:var(--warn);font-variant-numeric:tabular-nums}
.wrap tr:hover td{background:var(--soft)}
.table-hint{font:14px/1.6 'Work Sans',sans-serif;color:var(--muted)}
@media(max-width:760px){.filters{padding:18px;gap:20px}.filters input[type=search]{flex-basis:100%}.filters>select{flex:1;min-width:0}.filters fieldset{flex-basis:100%}.filters button{min-height:44px}.extra-filters>summary{display:list-item;cursor:pointer;padding:10px 0;min-height:44px}.filter-groups{padding-top:20px}.wrap th,.wrap td{padding:12px}.wrap td:first-child{min-width:190px}}
`;

const JS = String.raw`
(()=>{
const ROOT=document.body.dataset.root;
const rows=JSON.parse(document.getElementById('data').textContent);
const $=id=>document.getElementById(id);
const ORDER={effort:['S','M','L','XL'],vote:['Yes','Maybe','No']};
const COLS=[
  ['title','Idea'],['modName','Mod'],['theme','Theme'],['shortlist','#'],['tier','Tier'],['effort','Effort'],
  ['value','Value'],['votes','Votes'],['vote','My vote'],['status','Status'],['openQuestions','Open Qs'],['summary','Summary']];
const state={q:'',mod:'',theme:'',effort:new Set(),value:new Set(),vote:new Set(),status:new Set(),tier:new Set(),sort:'modName',dir:1};
const uniq=k=>[...new Set(rows.map(r=>r[k]))];
const rank=(k,v)=>{const o=ORDER[k];if(!o)return v;const i=o.indexOf(String(v).split(/[–-]/)[0]);return i<0?99:i};

// --- build filter controls
const mods=[...new Map(rows.map(r=>[r.mod,r.modName]))].sort((a,b)=>a[1].localeCompare(b[1]));
$('mod').innerHTML='<option value="">all mods</option>'+mods.map(([f,n])=>'<option value="'+f+'">'+n+'</option>').join('');
const pills=(id,key,vals,label)=>{
  const el=$(id);
  vals.forEach(v=>{const b=document.createElement('button');b.type='button';b.setAttribute('aria-pressed','false');b.className='pill';b.textContent=label?label(v):v;b.dataset.v=v;
    b.onclick=()=>{const s=state[key];const x=typeof v==='number'?v:String(v);s.has(x)?s.delete(x):s.add(x);b.classList.toggle('on',s.has(x));b.setAttribute('aria-pressed',String(s.has(x)));render()};el.appendChild(b)});
};
pills('f-effort','effort',uniq('effort').sort((a,b)=>rank('effort',a)-rank('effort',b)||a.localeCompare(b)));
pills('f-value','value',[3,2,1],v=>v+'/3');
pills('f-vote','vote',[...ORDER.vote,'—']);
pills('f-tier','tier',[1,2,3],v=>'T'+v);
pills('f-status','status',uniq('status').sort());
const fillThemes=()=>{
  const ts=[...new Set(rows.filter(r=>!state.mod||r.mod===state.mod).map(r=>r.theme))].sort();
  $('theme').innerHTML='<option value="">all themes</option>'+ts.map(t=>'<option>'+t+'</option>').join('');
  if(!ts.includes(state.theme))state.theme='';$('theme').value=state.theme;
};
fillThemes();

// --- header
const head=$('head');
COLS.forEach(([k,label])=>{const th=document.createElement('th');th.scope='col';th.dataset.k=k;const button=document.createElement('button');button.type='button';button.textContent=label;button.setAttribute('aria-label','Sort by '+label);th.appendChild(button);
  button.onclick=()=>{if(state.sort===k)state.dir*=-1;else{state.sort=k;state.dir=1}render()};head.appendChild(th)});

const esc=s=>String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
const cmp=(a,b)=>{
  const k=state.sort;let x=a[k],y=b[k];
  if(k==='effort'||k==='vote'){x=rank(k,x);y=rank(k,y)}
  if(x==null)x=-1;if(y==null)y=-1;
  const c=typeof x==='number'&&typeof y==='number'?x-y:String(x).localeCompare(String(y),undefined,{numeric:true});
  return c*state.dir||a.modName.localeCompare(b.modName)||a.title.localeCompare(b.title);
};
const match=r=>{
  if(state.mod&&r.mod!==state.mod)return false;
  if(state.theme&&r.theme!==state.theme)return false;
  if(state.effort.size&&!state.effort.has(r.effort))return false;
  if(state.value.size&&!state.value.has(r.value))return false;
  if(state.vote.size&&!state.vote.has(r.vote))return false;
  if(state.status.size&&!state.status.has(r.status))return false;
  if(state.tier.size&&!state.tier.has(r.tier))return false;
  if(state.q){const t=(r.modName+' '+r.theme+' '+r.title+' '+r.summary+' '+r.source).toLowerCase();
    if(!state.q.toLowerCase().split(/\s+/).every(w=>t.includes(w)))return false}
  return true;
};
function render(){
  const out=rows.filter(match).sort(cmp);
  $('count').textContent=out.length+' of '+rows.length+' ideas';
  head.querySelectorAll('th').forEach(th=>{const selected=th.dataset.k===state.sort;th.className=selected?(state.dir>0?'asc':'desc'):'';th.setAttribute('aria-sort',selected?(state.dir>0?'ascending':'descending'):'none')});
  $('body').innerHTML=out.map(r=>'<tr><td><a href="'+ROOT+esc(r.href)+'">'+esc(r.title)+'</a></td>'+
    '<td class="n">'+esc(r.modName)+'</td><td class="n">'+esc(r.theme)+'</td><td class="n">'+(r.shortlist??'')+'</td>'+
    '<td class="n">'+(r.tier?'T'+r.tier:'')+'</td><td class="n">'+esc(r.effort)+'</td>'+
    '<td class="n stars">'+(r.value+'/3')+'</td><td class="n">'+(r.votes??'—')+'</td>'+
    '<td class="n vote-'+esc(r.vote)+'" title="'+esc(r.voteNote)+'">'+esc(r.vote)+'</td><td class="n">'+esc(r.status)+'</td>'+
    '<td class="n">'+(r.openQuestions||'')+'</td><td class="sum"><div title="'+esc(r.summary)+'">'+esc(r.summary)+'</div></td></tr>').join('');
  save();
}

// --- state <-> URL hash, so a filtered view can be bookmarked
function save(){
  const p=new URLSearchParams();
  for(const k of ['q','mod','theme'])if(state[k])p.set(k,state[k]);
  for(const k of ['effort','value','vote','status','tier'])if(state[k].size)p.set(k,[...state[k]].join(','));
  if(state.sort!=='modName'||state.dir!==1)p.set('sort',state.sort+(state.dir<0?'-':''));
  history.replaceState(null,'','#'+p);
}
(()=>{
  const p=new URLSearchParams(location.hash.slice(1));
  state.q=p.get('q')||'';state.mod=p.get('mod')||'';fillThemes();state.theme=p.get('theme')||'';
  for(const k of ['effort','value','vote','status','tier'])(p.get(k)||'').split(',').filter(Boolean).forEach(v=>{
    const x=['value','tier'].includes(k)?Number(v):v;state[k].add(x)});
  const s=p.get('sort');if(s){state.dir=s.endsWith('-')?-1:1;state.sort=s.replace(/-$/,'')}
  $('q').value=state.q;$('mod').value=state.mod;$('theme').value=state.theme;
  document.querySelectorAll('.pill').forEach(b=>{const k=b.parentElement.dataset.key;const v=['value','tier'].includes(k)?Number(b.dataset.v):b.dataset.v;b.classList.toggle('on',state[k].has(v));b.setAttribute('aria-pressed',String(state[k].has(v)))});
})();
$('q').oninput=e=>{state.q=e.target.value;render()};
$('mod').onchange=e=>{state.mod=e.target.value;fillThemes();render()};
$('theme').onchange=e=>{state.theme=e.target.value;render()};
$('reset').onclick=()=>{location.hash='';location.reload()};
const narrow=matchMedia('(max-width:760px)');
$('advanced-filters').open=!narrow.matches;
narrow.addEventListener('change',e=>{$('advanced-filters').open=!e.matches});
document.addEventListener('keydown',e=>{if(e.key==='/'&&!['INPUT','SELECT','TEXTAREA'].includes(document.activeElement.tagName)){e.preventDefault();$('q').focus()}});
render();
})();
`;

export const ideasPage = (rows: IdeaRow[]): string => {
    const json = JSON.stringify(rows).replace(/</g, '\\u003c');
    return shell({
        title: 'Roadmap · Chimeric Mods',
        url: 'ideas/',
        wide: true,
        head: `<style>${CSS}</style>`,
        body: `<h1>Roadmap</h1>
<p class="muted">Every active feature idea across the mods. <span id="count" role="status" aria-live="polite"></span></p>
<div class="filters">
<input type="search" id="q" placeholder="Search ideas ( / )" aria-label="Search ideas">
<select id="mod" aria-label="Mod"></select><select id="theme" aria-label="Theme"></select>
<details class="extra-filters" id="advanced-filters" open><summary>More filters</summary><div class="filter-groups">
<fieldset><legend>Effort</legend><span id="f-effort" data-key="effort"></span></fieldset>
<fieldset><legend>Value</legend><span id="f-value" data-key="value"></span></fieldset>
<fieldset><legend>My vote</legend><span id="f-vote" data-key="vote"></span></fieldset>
<fieldset><legend>Tier</legend><span id="f-tier" data-key="tier"></span></fieldset>
<fieldset><legend>Status</legend><span id="f-status" data-key="status"></span></fieldset>
<button type="button" id="reset">Reset</button></div></details></div>
<p class="table-hint">Choose a column heading to sort. Scroll the table sideways for votes, status, and summaries.</p>
<div class="wrap" role="region" aria-label="Feature ideas" tabindex="0"><table aria-label="Active feature ideas"><thead><tr id="head"></tr></thead><tbody id="body"></tbody></table></div>
<script type="application/json" id="data">${json}</script>
<script>${JS}</script>`,
    });
};
