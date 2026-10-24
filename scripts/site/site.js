/* Reading, filtering and preferences work without a framework. */
(() => {
    const theme = document.querySelector('.theme-control select');
    if (theme) {
        theme.value = document.documentElement.dataset.theme || 'system';
        theme.addEventListener('change', () => {
            if (theme.value === 'system') delete document.documentElement.dataset.theme;
            else document.documentElement.dataset.theme = theme.value;
            try {
                if (theme.value === 'system') localStorage.removeItem('chimeric-theme');
                else localStorage.setItem('chimeric-theme', theme.value);
            } catch {}
        });
    }

    const tree = document.querySelector('.side-tree');
    if (tree) {
        const phone = matchMedia('(max-width:760px)');
        const fitTree = () => { tree.open = !phone.matches; };
        fitTree();
        phone.addEventListener('change', fitTree);
    }

    const search = document.querySelector('#mod-search');
    if (search) {
        document.querySelector('.search-control').hidden = false;
        const entries = [...document.querySelectorAll('.mod-row')];
        const count = document.querySelector('#mod-count');
        const update = () => {
            const words = search.value.toLowerCase().trim().split(/\s+/).filter(Boolean);
            let total = 0;
            for (const entry of entries) {
                const match = words.every(word => entry.dataset.search.includes(word));
                entry.hidden = !match || (!words.length && entry.dataset.featured === 'true');
                if (!entry.hidden) total++;
            }
            count.textContent = words.length ? `${total} of ${entries.length} mods` : `${total} more mods`;
            document.querySelector('.empty-search').hidden = total !== 0;
        };
        search.addEventListener('input', update);
        document.querySelector('#clear-search').addEventListener('click', () => {
            search.value = '';
            update();
            search.focus();
        });
        update();
    }

    const reduced = matchMedia('(prefers-reduced-motion: reduce)');
    const art = document.querySelector('.ware-art');
    if (art) {
        const slides = [...art.querySelectorAll('.art-slide')];
        const toggle = art.querySelector('.art-toggle');
        let current = 0;
        let paused = false;
        let visible = true;
        let animations = [];
        const advance = () => {
            if (paused || !visible || document.hidden || reduced.matches) return;
            for (const animation of animations) {
                animation.finish();
                animation.commitStyles();
                animation.cancel();
            }
            const previous = slides[current];
            current = (current + 1) % slides.length;
            const next = slides[current];
            previous.setAttribute('aria-hidden', 'true');
            next.removeAttribute('aria-hidden');
            animations = [previous.animate([{opacity: 1}, {opacity: 0}], {duration: 1600, fill: 'forwards'}), next.animate([{opacity: 0}, {opacity: 1}], {duration: 1600, fill: 'forwards'})];
        };
        const preference = () => {
            toggle.hidden = reduced.matches;
            if (reduced.matches) {
                for (const animation of animations) animation.finish();
            }
        };
        toggle.addEventListener('click', () => {
            paused = !paused;
            toggle.textContent = paused ? 'Play artwork' : 'Pause artwork';
            if (paused) for (const animation of animations) animation.finish();
        });
        new IntersectionObserver(entries => { visible = entries[0].isIntersecting; }).observe(art);
        reduced.addEventListener('change', preference);
        preference();
        setInterval(advance, 6000);
    }

    if (!reduced.matches && 'IntersectionObserver' in window) {
        const observer = new IntersectionObserver(entries => {
            for (const entry of entries) {
                if (!entry.isIntersecting) continue;
                const el = entry.target;
                el.style.animationDelay = `${Number(el.dataset.delay || 0)}ms`;
                el.classList.add(el.classList.contains('mod-row') ? 'arrive-row' : 'arrive-part');
                observer.unobserve(el);
            }
        }, {threshold: .08});
        document.querySelectorAll('.ware h2,.ware p,.ware .icon,.ware-art,.directory-top,.mod-row,.footer-name').forEach((el, index) => {
            el.dataset.delay = String((index % 3) * 70);
            observer.observe(el);
        });
    }
})();
