/** Small, progressively enhanced controls shared by public pages. */
export const SITE_JS = String.raw`
(() => {
    const root = document.documentElement;
    const theme = document.querySelector('#color-theme');
    const saved = () => { try { return localStorage.getItem('chimeric-theme'); } catch { return null; } };
    const setTheme = value => {
        if (value === 'light' || value === 'dark') root.dataset.theme = value;
        else delete root.dataset.theme;
        if (theme) theme.value = value || 'system';
    };
    setTheme(saved());
    theme?.addEventListener('change', () => {
        setTheme(theme.value);
        try { localStorage.setItem('chimeric-theme', theme.value); } catch {}
    });

    const input = document.querySelector('#mod-search');
    if (input) {
        input.closest('.search-tools').hidden = false;
        const entries = [...document.querySelectorAll('[data-mod]')];
        const count = document.querySelector('#mod-count');
        const empty = document.querySelector('#mod-empty');
        const filter = () => {
            const words = input.value.toLocaleLowerCase().trim().split(/\s+/).filter(Boolean);
            let shown = 0;
            for (const entry of entries) {
                const matches = words.every(word => entry.dataset.mod.includes(word));
                entry.hidden = !matches;
                if (matches) shown++;
            }
            count.textContent = shown + ' of ' + entries.length + ' mods';
            empty.hidden = shown !== 0;
            document.querySelector('#featured-mods').hidden = !entries.some(el => el.closest('#featured-mods') && !el.hidden);
        };
        input.addEventListener('input', filter);
        document.querySelector('#clear-search').addEventListener('click', () => { input.value = ''; filter(); input.focus(); });
        filter();
    }

    // Animate only below the fold; failed/disabled scripts always leave readable content.
    if ('IntersectionObserver' in window && !matchMedia('(prefers-reduced-motion: reduce)').matches) {
        const observer = new IntersectionObserver(entries => {
            for (const entry of entries) if (entry.isIntersecting) {
                entry.target.animate([
                    {opacity: .45, transform: entry.target.matches('.mod-entry') ? 'translateX(16px)' : 'translateY(20px)'},
                    {opacity: 1, transform: 'none'},
                ], {duration: entry.target.matches('.mod-entry') ? 550 : 800, easing: 'cubic-bezier(.16,1,.3,1)'});
                observer.unobserve(entry.target);
            }
        }, {threshold: .08});
        document.querySelectorAll('[data-reveal]').forEach(el => {
            if (el.getBoundingClientRect().top >= innerHeight - 40) observer.observe(el);
        });
    }
})();
`;

/** Runs before the stylesheet to avoid flashing the OS theme over a saved preference. */
export const THEME_INIT = `try{const t=localStorage.getItem('chimeric-theme');if(t==='light'||t==='dark')document.documentElement.dataset.theme=t}catch{}`;
