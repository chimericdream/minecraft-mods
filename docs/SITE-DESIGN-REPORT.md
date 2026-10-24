# Docs site redesign report

The site now presents chimericdream's mods as a catalogue from a fantasy workshop: a stepped bookish title, real pixel artwork at unequal sizes, four featured mods, and a searchable directory of the other 30. The README, changelog, guide, and ideas publishing pipeline still generates 629 pages with Bun and marked.

## Direction and decisions

- **Dealt direction:** Photographs at uneven sizes, seed **649393584**, with `--keep palette`.
- **Idea:** chimericdream's useful furniture and absurd camel snout become an uneven catalogue of wares from the same imaginary workshop.
- **Live thing:** search all 34 mods by name, description, loader, or supported Minecraft version. An announced result count, empty state, and Clear button accompany it. Every mod remains readable without JavaScript.
- **Type:** the type redraw dealt EB Garamond; Source Sans 3 handles reading and controls. Both load from Google Fonts. Switzer's Fontshare delivery conflicted with the brief's Google Fonts/self-hosting requirement, which prompted the redraw.
- **List redraw:** the original pushed-right table holds 5–14 entries. The chosen photo-bullet catalogue accommodates the required 30 entries in two compact columns and one column on phones.
- **Palette:** snow-like light ground and violet-black dark ground, with crimson adjusted for contrast. Violet is explicitly allowed as the required brand color.
- **Imagery:** existing repository artwork only. No stock, generated images, bundled photographs, or video. The existing avatar SVG remains a replaceable source asset at `docs/design/logo-placeholder.svg`.
- **Reading surfaces:** a consistent reading measure, aligned mastheads, semantic recipe cells, large callouts, and the existing mod navigation tree. Navigation and advanced roadmap filters collapse on phones.
- **Default theme:** OS preference, with remembered System/Light/Dark selection.

The full inventory, copy deck, artwork register, warning dispositions, and three critique rounds are recorded in [design notes](../.taste-skill/design-notes.md). Existing README and guide credits remain published. No sample content needs replacement; the avatar is the placeholder explicitly supplied in the brief.

## Before and after

Open the [interactive screenshot comparison](http://localhost:7300/review/) while the review server is running, or open [comparison.html](../.taste-skill/comparison.html) locally. It selects page, width, and theme. The completed site is at [localhost:7300](http://localhost:7300/).

| Home measurement | Before | After |
| --- | --- | --- |
| Inspector failures, light | 6 | 0 |
| Inspector failures, dark | 6 | 0 |
| Desktop headline | 32px | 115px |
| Tablet headline | 32px | 64px |
| Phone headline | 32px | 48px |
| Body text | 14px | 17px |
| Home type sizes, desktop | 7 | 5 |
| Title lines at all widths | 1 | 2 |
| Motion elements, desktop/tablet/phone | 0 / 0 / 0 | Light: 21 / 19 / 21; dark: 21 / 17 / 21 |
| Functional catalogue search | None | All 34 mods |

The final inspector ran at **1440, 768, and 390 pixels in both themes**, reporting **0 FAIL and 2 WARN** in each theme. The warnings are intentional: Source Sans 3 complements the dealt display face, and no project video was available that met the brief. No low-contrast home text or viewport overflow was reported. All final inspector screenshots were opened and visually reviewed.

Final self-critique: **design 7.5, usability 8, creativity 7.5, content 8; weighted overall 7.7/10**. The main visual limitation is reliance on icons and a repeated catalogue book; a real in-game Minekea scene would provide stronger evidence. The phone feature area is text-dense, and the complete roadmap requires horizontal scrolling for its secondary columns.

Screenshot records:

- Home light: [before](../.taste-skill/before/light/desktop-fold.jpg), [after](../.taste-skill/after/light/desktop-fold.jpg).
- Home dark: [before](../.taste-skill/before/recovered-dark/desktop-fold.jpg), [after](../.taste-skill/after/dark/desktop-fold.jpg).
- Phone light: [before](../.taste-skill/before/light/mobile-fold.jpg), [after](../.taste-skill/after/light/mobile-fold.jpg).
- Phone dark: [before](../.taste-skill/before/recovered-dark/mobile-fold.jpg), [after](../.taste-skill/after/dark/mobile-fold.jpg).
- Overview, guide, roadmap, changelog: 24 captures per phase in `.taste-skill/before/surfaces/` and `.taste-skill/after/surfaces/`.
- Machine-readable home reports: [light](../.taste-skill/after/light/report.json), [dark](../.taste-skill/after/dark/report.json).

The original site was started with `bun run site --serve --port 7300`, and baseline screenshots were recorded before source edits. Its dark tablet home capture caught the development watcher deleting/rebuilding the output. That invalid image is preserved in `before/dark`; the complete repaired dark set in `before/recovered-dark` was rendered from the unchanged HEAD versions of the original generator, with the same content and assets. A stable review server prevented this race in the final captures.

## Validation

- `bun run site`: passed, 34 mods, 19 guides / 205 guide pages, 421 docs, 216 ideas.
- `bun run lint`: passed, including UTF-8 and LF checks.
- `git diff --check`: passed.
- Skill source lint: 0 failures; its remaining warning covers existing roadmap missing-value dashes.
- [Browser verification](../.taste-skill/verification.json): 24 checks passed. All internal links and image paths across 629 pages resolve; no absolute internal links were introduced.
- Verified `/minecraft-mods/` deployment prefix, search/clear/empty states, theme persistence and OS overrides, keyboard disclosure activation, roadmap search/sorting/bookmarks/reset, recipes, no-JavaScript content, and reduced motion.
- `bunx tsc --noEmit` remains blocked by four existing `Bun.YAML` typing errors in the unchanged `scripts/site/guide.ts` at lines 32, 96, 108, and 195. It reports no errors in the redesigned source files.
- Existing build warnings about two cross-book links and four unresolved source Markdown links remain unchanged.

No files under `scripts/site/local/` were changed. `THEME_CSS` still exports every original token consumed by the local dashboard.

## Reproduce

Run the development site with `bun run site --serve --port 7300`. For stable screenshot review, build with `bun run site`, then run `bun .taste-skill/serve.ts` on port 7300.

The recorded browser scripts use the installed skill's CDP helper and debugger ports 7301, 7302, and 7304. All task servers and debugger ports use the requested 7xxx range. Run inspections sequentially because they share debugger port 7301.
