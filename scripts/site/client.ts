/** Small progressive enhancements; all content and links exist without JavaScript. */
export const SITE_JS = `
(() => {
  const choice = document.getElementById('theme-choice');
  try { choice.value = localStorage.getItem('chimeric-theme') || 'system'; } catch {}
  choice.addEventListener('change', () => {
    const value = choice.value;
    if (value === 'system') delete document.documentElement.dataset.theme;
    else document.documentElement.dataset.theme = value;
    try { localStorage.setItem('chimeric-theme', value); } catch {}
  });
  const side = document.querySelector('.side-panel');
  const phone = matchMedia('(max-width:760px)');
  if (side) {
    side.open = !phone.matches;
    phone.addEventListener('change', () => { side.open = !phone.matches; });
  }
  const search = document.getElementById('mod-search');
  if (search) {
    const items = [...document.querySelectorAll('[data-mod]')];
    const output = document.getElementById('mod-count');
    const empty = document.getElementById('no-mods');
    const filter = () => {
      const words = search.value.toLocaleLowerCase().trim().split(/\\s+/);
      let count = 0;
      items.forEach(item => {
        const match = words.every(word => item.dataset.mod.includes(word)) && (search.value.trim() || !item.hasAttribute('data-feature-row'));
        item.hidden = !match;
        if (match) count++;
      });
      output.textContent = count + ' of ' + items.length + ' mods';
      empty.hidden = count !== 0;
    };
    search.addEventListener('input', filter);
    document.querySelector('[data-search-controls]').hidden = false;
    filter();
  }
  if (!matchMedia('(prefers-reduced-motion: reduce)').matches) {
    const observer = new IntersectionObserver(entries => {
      entries.forEach(entry => {
        if (!entry.isIntersecting) return;
        const el = entry.target;
        el.classList.add(el.tagName === 'IMG' ? 'arrive-picture' : el.classList.contains('mod-row') ? 'arrive-row' : 'arrive-text');
        observer.unobserve(el);
      });
    }, {threshold:.08});
    document.querySelectorAll('.feature-entry h3,.feature-entry p,.feature-art,.mod-row,.section-intro h2,.catalogue-intro h2').forEach(el => observer.observe(el));
    const hero = document.querySelector('.hero');
    const reel = document.querySelector('.specimen');
    if (hero && reel) {
      const images = [...reel.querySelectorAll('img')];
      let selected = 0;
      let pending = false;
      const update = () => {
        const progress = Math.max(0,Math.min(1,-hero.getBoundingClientRect().top / (hero.offsetHeight * .6)));
        const next = Math.min(images.length-1,Math.floor(progress*images.length));
        if (next !== selected) {
          images[selected].style.opacity = '0';
          images[next].style.opacity = '1';
          images[next].animate([{transform:'translateY(18px)'},{transform:'translateY(0)'}],{duration:450,easing:'cubic-bezier(.16,1,.3,1)'});
          selected = next;
        }
        pending = false;
      };
      addEventListener('scroll', () => { if (!pending) { pending=true; requestAnimationFrame(update); } }, {passive:true});
      update();
    }
  }
})();
`;
