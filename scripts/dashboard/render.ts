import type {DashboardData, GithubItem, IdeasInfo, ModInfo} from './types.ts';
import {THEME_CSS} from './theme.ts';
import {ago, esc} from './util.ts';

/**
 * A dashboard section. To drop one that turns out not to be useful, remove it from SECTIONS at the
 * bottom of this file; each one is self-contained.
 */
type Section = {id: string; title: string; render: (d: DashboardData) => string};

const initials = (name: string) =>
    name.split(/[\s-]+/).filter(Boolean).slice(0, 2).map(w => w[0]!.toUpperCase()).join('');

const hue = (s: string) => [...s].reduce((h, c) => (h * 31 + c.charCodeAt(0)) % 360, 7);

const iconHtml = (m: ModInfo) => m.icon
    ? `<img class="icon" src="${m.icon}" alt="" width="48" height="48">`
    : `<div class="icon ph" style="--h:${hue(m.id)}" aria-hidden="true">${esc(initials(m.name))}</div>`;

const STAGE_LABEL = {raw: 'raw answers', combined: 'combined', shortlist: 'shortlisted'} as const;

const ideasLine = (i: IdeasInfo | undefined): string => {
    if (!i) {
        return '<span class="muted">no ideas folder</span>';
    }
    const bits = [`${i.ideaCount} idea${i.ideaCount === 1 ? '' : 's'}`];
    if (i.identityDraft) bits.push('identity: draft');
    if (i.latestBrainstorm) bits.push(`${esc(i.latestBrainstorm.name)}: ${STAGE_LABEL[i.latestBrainstorm.stage]}`);
    return bits.join(' · ');
};

const modCard = (m: ModInfo, d: DashboardData): string => {
    const items = d.github.ok ? d.github.data.items.filter(i => i.mods.includes(m.folder)) : [];
    const issues = items.filter(i => i.kind === 'issue').length;
    const prs = items.filter(i => i.kind === 'pr').length;
    const badges = [
        m.isNewMod ? '<span class="badge new">new</span>' : '',
        m.isPrerelease ? '<span class="badge beta">pre-release</span>' : '',
        m.hasUnreleasedChanges ? '<span class="badge unrel" title="CHANGELOG has unreleased changes">unreleased changes</span>' : '',
    ].join('');
    const counts = d.github.ok
        ? `<span class="counts">${issues} issue${issues === 1 ? '' : 's'} · ${prs} PR${prs === 1 ? '' : 's'}</span>`
        : '';
    return `<article class="card" id="mod-${esc(m.folder)}" data-name="${esc((m.name + ' ' + m.folder).toLowerCase())}"
      data-unrel="${m.hasUnreleasedChanges}" data-beta="${m.isPrerelease}">
    <header>${iconHtml(m)}<div><h3>${esc(m.name)}</h3>
      <div class="ver">${esc(m.version)} ${badges}</div></div></header>
    <p class="desc">${esc(m.description)}</p>
    <div class="meta">${ideasLine(d.ideas[m.folder])}</div>
    <div class="meta">${m.latestTag ? `latest tag <code>${esc(m.latestTag.split('/').pop()!)}</code> · ` : 'no tags · '}
      touched ${ago(m.lastCommit)} ${counts}</div>
    <nav>${m.links.map(l => `<a href="${esc(l.href)}">${esc(l.label)}</a>`).join('')}</nav>
  </article>`;
};

const modsSection: Section = {
    id: 'mods', title: 'Mods',
    render: d => `<div class="toolbar">
    <input type="search" id="q" placeholder="Filter mods…" aria-label="Filter mods">
    <label><input type="checkbox" id="f-unrel"> unreleased changes only</label>
    <label><input type="checkbox" id="f-beta"> pre-release only</label>
  </div>
  <div class="grid" id="grid">${d.mods.map(m => modCard(m, d)).join('')}</div>`,
};

const modName = (d: DashboardData, folder: string) => d.mods.find(m => m.folder === folder)?.name ?? folder;

const itemRow = (i: GithubItem, d: DashboardData): string => {
    const chips = i.mods.length
        ? i.mods.map(f => `<span class="chip">${esc(modName(d, f))}</span>`).join('')
        : '<span class="chip none">unassigned</span>';
    const state = i.kind === 'pr'
        ? `${i.isDraft ? '<span class="badge">draft</span>' : ''}${i.checks && i.checks !== 'none' ? `<span class="badge chk-${i.checks}">CI ${i.checks}</span>` : ''}`
        : '';
    const labels = i.labels.map(l => `<span class="label">${esc(l)}</span>`).join('');
    return `<tr data-mods="${esc(i.mods.join(' '))}">
    <td class="num"><a href="${esc(i.url)}">#${i.number}</a></td>
    <td><a href="${esc(i.url)}">${esc(i.title)}</a> ${state}<div class="labels">${labels}</div></td>
    <td>${chips}</td><td class="age">${ago(i.createdAt)}</td></tr>`;
};

const githubSection = (id: string, title: string, kind: 'issue' | 'pr'): Section => ({
    id, title,
    render: d => {
        if (!d.github.ok) {
            return `<p class="error">Couldn't load GitHub data: ${esc(d.github.error)}</p>`;
        }
        const items = d.github.data.items.filter(i => i.kind === kind)
            .sort((a, b) => b.createdAt.localeCompare(a.createdAt));
        if (items.length === 0) {
            return `<p class="muted">No open ${kind === 'pr' ? 'pull requests' : 'issues'}.</p>`;
        }
        const options = d.mods.filter(m => items.some(i => i.mods.includes(m.folder)))
            .map(m => `<option value="${esc(m.folder)}">${esc(m.name)}</option>`).join('');
        const unassigned = items.some(i => i.mods.length === 0) ? '<option value="_none">unassigned</option>' : '';
        return `<div class="toolbar"><label>Mod
      <select class="modfilter" data-table="${id}"><option value="">all (${items.length})</option>${options}${unassigned}</select></label></div>
    <table id="tbl-${id}"><thead><tr><th>#</th><th>Title</th><th>Mod</th><th>Opened</th></tr></thead>
    <tbody>${items.map(i => itemRow(i, d)).join('')}</tbody></table>`;
    },
});

const ideasSection: Section = {
    id: 'ideas', title: 'Ideas pipeline',
    render: d => {
        const rows = d.mods.map(m => ({m, i: d.ideas[m.folder]}));
        const attention = rows.filter(({i}) => i && (i.identityDraft || i.latestBrainstorm?.stage === 'shortlist' || i.latestBrainstorm?.stage === 'raw'));
        const noIdeas = rows.filter(({i}) => !i);
        const li = (m: ModInfo, note: string) => `<li><a href="../../${esc(m.folder)}/ideas/README.md">${esc(m.name)}</a> <span class="muted">${note}</span></li>`;
        const note = (i: IdeasInfo) => [
            i.identityDraft ? 'identity still draft' : '',
            i.latestBrainstorm?.stage === 'raw' ? `${i.latestBrainstorm.name}: needs combining` : '',
            i.latestBrainstorm?.stage === 'shortlist' ? `${i.latestBrainstorm.name}: shortlist waiting on votes/promotion` : '',
        ].filter(Boolean).join(' · ');
        return `<div class="two">
      <div><h4>Needs attention</h4><ul>${attention.map(({m, i}) => li(m, note(i!))).join('') || '<li class="muted">nothing</li>'}</ul></div>
      <div><h4>No ideas folder</h4><ul>${noIdeas.map(({m}) => `<li>${esc(m.name)}</li>`).join('') || '<li class="muted">none</li>'}</ul></div></div>`;
    },
};

const SECTIONS: Section[] = [
    modsSection,
    githubSection('prs', 'Pull requests', 'pr'),
    githubSection('issues', 'Issues', 'issue'),
    ideasSection,
];

const CSS = `
${THEME_CSS}
*{box-sizing:border-box}body{margin:0;background:var(--bg);color:var(--fg);font:15px/1.45 system-ui,sans-serif}
main{max-width:1200px;margin:0 auto;padding:16px}a{color:var(--accent);text-decoration:none}a:hover{text-decoration:underline}
h1{margin:8px 0 2px}.sub{color:var(--muted);margin:0 0 16px}
section{background:var(--card);border:1px solid var(--line);border-radius:10px;margin:0 0 16px;padding:0 16px 12px}
section>h2{margin:0;padding:12px 0;font-size:18px;display:flex;justify-content:space-between;align-items:center}
section.hidden>:not(h2){display:none}
.hide{font-size:12px;background:none;border:1px solid var(--line);color:var(--muted);border-radius:6px;padding:2px 8px;cursor:pointer}
.toolbar{display:flex;gap:16px;flex-wrap:wrap;align-items:center;margin-bottom:12px}
input[type=search],select{padding:6px 8px;border:1px solid var(--line);border-radius:6px;background:var(--bg);color:var(--fg)}
.grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(300px,1fr));gap:12px}
.card{border:1px solid var(--line);border-radius:8px;padding:12px;background:var(--bg)}
.card header{display:flex;gap:10px;align-items:center}.card h3{margin:0;font-size:16px}
.icon{width:48px;height:48px;border-radius:8px;image-rendering:pixelated;flex:none}
.ph{display:grid;place-items:center;font-weight:700;color:#fff;background:hsl(var(--h) 45% 40%);image-rendering:auto}
.ver{font-size:13px;color:var(--muted)}.desc{margin:8px 0;font-size:13px}.meta{font-size:12px;color:var(--muted);margin:2px 0}
.card nav{margin-top:8px;display:flex;gap:10px;flex-wrap:wrap;font-size:13px}
.badge{display:inline-block;font-size:11px;padding:0 6px;border-radius:9px;border:1px solid var(--line);margin-left:2px}
.badge.beta{color:var(--warn)}.badge.unrel{color:var(--accent)}.badge.new{color:var(--good)}
.chk-passing{color:var(--good)}.chk-failing{color:var(--bad)}.chk-pending{color:var(--warn)}
table{width:100%;border-collapse:collapse}th,td{text-align:left;padding:6px 8px;border-bottom:1px solid var(--line);vertical-align:top}
th{font-size:12px;color:var(--muted)}td.num,td.age{white-space:nowrap}
.chip,.label{display:inline-block;font-size:11px;border:1px solid var(--line);border-radius:9px;padding:0 6px;margin:1px 2px 1px 0}
.chip.none{color:var(--muted)}.labels{margin-top:2px}.label{color:var(--muted)}
.muted{color:var(--muted)}.error{color:var(--bad)}.two{display:grid;grid-template-columns:repeat(auto-fit,minmax(280px,1fr));gap:16px}
h4{margin:4px 0}ul{margin:0;padding-left:18px}code{font-size:12px}
@media (max-width:600px){main{padding:16px}}`;

const JS = `
const store=(k,v)=>{try{v===undefined?localStorage.removeItem(k):localStorage.setItem(k,v)}catch{}};
const load=k=>{try{return localStorage.getItem(k)}catch{return null}};
document.querySelectorAll('section').forEach(s=>{
  const id=s.id,btn=s.querySelector('.hide');
  const apply=h=>{s.classList.toggle('hidden',h);btn.textContent=h?'show':'hide'};
  apply(load('hide-'+id)==='1');
  btn.onclick=()=>{const h=!s.classList.contains('hidden');apply(h);store('hide-'+id,h?'1':undefined)};
});
const q=document.getElementById('q'),u=document.getElementById('f-unrel'),b=document.getElementById('f-beta');
const filterMods=()=>document.querySelectorAll('#grid .card').forEach(c=>{
  c.hidden=!(c.dataset.name.includes(q.value.toLowerCase())&&(!u.checked||c.dataset.unrel==='true')&&(!b.checked||c.dataset.beta==='true'))});
[q,u,b].forEach(e=>e&&e.addEventListener('input',filterMods));
document.querySelectorAll('.modfilter').forEach(sel=>sel.addEventListener('change',()=>{
  document.querySelectorAll('#tbl-'+sel.dataset.table+' tbody tr').forEach(r=>{
    const m=r.dataset.mods;r.hidden=sel.value&&!(sel.value==='_none'?m==='':m.split(' ').includes(sel.value))})}));`;

const statLine = (d: DashboardData): string => {
    const gh = d.github.ok ? d.github.data.items : [];
    const parts = [
        `${d.mods.length} mods`,
        `${d.mods.filter(m => m.hasUnreleasedChanges).length} with unreleased changes`,
        d.github.ok ? `${gh.filter(i => i.kind === 'pr').length} open PRs` : 'PRs unavailable',
        d.github.ok ? `${gh.filter(i => i.kind === 'issue').length} open issues` : 'issues unavailable',
    ];
    return `${parts.join(' · ')} · generated ${new Date(d.generatedAt).toLocaleString()}` +
        (d.github.ok ? ` · GitHub data from ${new Date(d.github.data.fetchedAt).toLocaleTimeString()}` : '');
};

export const renderDashboard = (d: DashboardData, ideaCount: number): string => `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Chimeric Mods Dashboard</title><style>${CSS}</style></head>
<body><main><h1>Chimeric Mods</h1><p class="sub">${esc(statLine(d))}</p>
<p class="sub"><a href="ideas.html">Browse all ${ideaCount} active ideas →</a></p>
${SECTIONS.map(s => `<section id="${s.id}"><h2>${esc(s.title)}<button class="hide" type="button">hide</button></h2>${s.render(d)}</section>`).join('\n')}
</main><script>${JS}</script></body></html>
`;
