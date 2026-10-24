/** Applied before CSS to avoid a flash when restoring a manual theme. */
export const THEME_INIT = `try{const t=localStorage.getItem('chimeric-theme');if(t==='light'||t==='dark')document.documentElement.dataset.theme=t}catch{}`;

/** One small progressive enhancement bundle; all content and navigation work without it. */
export const SITE_JS = `
(() => {
    const picker = document.getElementById('theme-picker');
    picker.value = document.documentElement.dataset.theme || 'system';
    picker.addEventListener('change', () => {
        const theme = picker.value;
        if (theme === 'system') delete document.documentElement.dataset.theme;
        else document.documentElement.dataset.theme = theme;
        try {
            if (theme === 'system') localStorage.removeItem('chimeric-theme');
            else localStorage.setItem('chimeric-theme', theme);
        } catch {}
    });
    const menu = document.querySelector('.side-menu');
    if (menu) {
        const narrow = matchMedia('(max-width:760px)');
        menu.open = !narrow.matches;
        narrow.addEventListener('change', event => { menu.open = !event.matches; });
    }

    const search = document.getElementById('mod-search');
    if (search) {
        const release = document.getElementById('release-filter');
        const rows = [...document.querySelectorAll('.catalogue-row')];
        const count = document.getElementById('mod-count');
        const empty = document.getElementById('no-results');
        const filter = () => {
            const words = search.value.toLocaleLowerCase().trim().split(/\\s+/).filter(Boolean);
            const active = words.length > 0 || release.value !== 'all';
            let found = 0;
            for (const row of rows) {
                const matches = words.every(word => row.dataset.search.includes(word)) &&
                    (release.value === 'all' || row.dataset.release === release.value);
                row.hidden = !matches || (!active && row.dataset.featured === 'true');
                if (!row.hidden) found++;
            }
            // Exact names lead broader README matches, while the unfiltered catalogue stays alphabetical.
            const query = search.value.toLocaleLowerCase().trim();
            const rank = row => !query ? 0 : row.dataset.name === query ? 3 :
                row.dataset.name.includes(query) ? 2 : words.some(word => row.dataset.name.includes(word)) ? 1 : 0;
            document.getElementById('mod-list').append(...[...rows].sort((a, b) =>
                rank(b) - rank(a) || a.dataset.name.localeCompare(b.dataset.name)));
            count.textContent = active ? found + ' of ' + rows.length + ' mods' : found + ' more mods to discover';
            empty.hidden = found > 0;
        };
        search.addEventListener('input', filter);
        release.addEventListener('change', filter);
        document.getElementById('catalogue-search').addEventListener('submit', event => event.preventDefault());
        filter();
        document.addEventListener('keydown', event => {
            if (event.key === '/' && !/INPUT|TEXTAREA|SELECT/.test(document.activeElement.tagName)) {
                event.preventDefault();
                search.focus();
            }
        });
    }

    // Reading pages stay still. The catalogue's sheets, artwork, and index arrive in reading order.
    if (document.querySelector('.home-intro') && 'IntersectionObserver' in window &&
        !matchMedia('(prefers-reduced-motion:reduce)').matches) {
        const observer = new IntersectionObserver(entries => {
            for (const entry of entries) {
                if (!entry.isIntersecting) continue;
                entry.target.classList.add('motion-in');
                observer.unobserve(entry.target);
            }
        }, {threshold: 0.08});
        document.querySelectorAll('.home-intro > *, .folder-tabs a, .feature-intro > *, .feature-copy > *, .feature-art, .catalogue-heading > *, .catalogue-controls, .catalogue-row, .footer-message > *, .footer-links, .footer-wordmark')
            .forEach((element, index) => {
                element.style.animationDelay = (index % 3) * 70 + 'ms';
                observer.observe(element);
            });
    }
})();
`;
