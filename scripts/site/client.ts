/** Small progressive enhancements. All content and navigation work without JavaScript. */
export const SITE_JS = `
const theme = document.querySelector('#color-theme');
if (theme) {
  theme.value = document.documentElement.dataset.theme || 'system';
  theme.addEventListener('change', () => {
    if (theme.value === 'system') delete document.documentElement.dataset.theme;
    else document.documentElement.dataset.theme = theme.value;
    try { localStorage.setItem('chimeric-theme', theme.value); } catch {}
  });
}
const menu = document.querySelector('.side-toggle');
if (menu) menu.addEventListener('click', () => menu.setAttribute('aria-expanded', String(menu.getAttribute('aria-expanded') !== 'true')));
const search = document.querySelector('#mod-search');
if (search) {
  document.querySelector('.catalogue-search').hidden = false;
  const rows = [...document.querySelectorAll('.mod-row')];
  const status = document.querySelector('#catalogue-count');
  const empty = document.querySelector('#catalogue-empty');
  const filter = () => {
    const query = search.value.trim().toLocaleLowerCase();
    let count = 0;
    rows.forEach(row => {
      row.hidden = query ? !row.dataset.search.includes(query) : row.dataset.featured === 'true';
      if (!row.hidden) count++;
    });
    status.textContent = query ? count + ' matching mods' : count + ' more mods. The four featured mods are above.';
    empty.hidden = count !== 0;
  };
  search.addEventListener('input', filter);
  document.querySelector('#clear-search').addEventListener('click', () => { search.value = ''; filter(); search.focus(); });
  filter();
}
if (!matchMedia('(prefers-reduced-motion: reduce)').matches && 'IntersectionObserver' in window) {
  const observer = new IntersectionObserver(entries => {
    for (const entry of entries) if (entry.isIntersecting) {
      entry.target.classList.add('arrive');
      observer.unobserve(entry.target);
    }
  }, {threshold: 0.08});
  document.querySelectorAll('.mod-row, .catalogue-head').forEach(el => observer.observe(el));
}
`;
