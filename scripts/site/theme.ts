/**
 * Color tokens shared by every page, including the local-only GitHub panel. Seeded from the author's avatar
 * (docs/SITE-DESIGN-BRIEF.md §5): dusk violet and deep violet for the ground and brand tiles, snow and moon grey
 * for ink, crimson for the one accent. Light and dark each have their own ground, ink and accent; neither is the
 * other inverted. The OS setting wins until `data-theme` is set on <html> by the toggle.
 */
const LIGHT = [
    '--bg:#e9e6ee', '--card:#f6f4f9', '--card2:#dcd6e8', '--fg:#1d0c31', '--muted:#4b3f63', '--line:#cbc3da',
    '--accent:#8b1a1a', '--accent-fill:#8b1a1a', '--on-accent:#fff', '--violet:#2d1050', '--on-violet:#eceff1',
    '--good:#1a6b32', '--bad:#a31717', '--warn:#7a4d00', '--mark:#f3dd8b',
].join(';');

const DARK = [
    '--bg:#0f0818', '--card:#1a0f2b', '--card2:#2a1646', '--fg:#eceff1', '--muted:#b8afca', '--line:#3a2858',
    '--accent:#ff9a9a', '--accent-fill:#8b1a1a', '--on-accent:#fff', '--violet:#34135e', '--on-violet:#eceff1',
    '--good:#6fd68b', '--bad:#ff8a80', '--warn:#e8b84a', '--mark:#5c4b00',
].join(';');

export const THEME_CSS = `
:root{${LIGHT};color-scheme:light}
@media (prefers-color-scheme:dark){:root:not([data-theme=light]){${DARK};color-scheme:dark}}
:root[data-theme=dark]{${DARK};color-scheme:dark}
`;
