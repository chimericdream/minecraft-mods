# Chimericdream redesign

Direction: Photographs at uneven sizes. Seed: 649393584.

## Subject inventory

- Chimericdream means a creature assembled from mixed parts, like this collection.
- Crimson dragon, twin snowy mountains, crescent moon, black and violet.
- 34 mods generated from repository content.
- Minekea's build-and-store catalogue and tiny blue in-game book.
- Hopper X-Treme's diamond-rimmed hopper and upward-moving huppers.
- Effective Gear's armor trim bonuses.
- Camel Nostrils' detached snout and upside-down furniture.
- Fabric and NeoForge distribution.
- Opus turns the same Markdown into an in-game book.
- ChimericLib and Architectury dependency information.
- A living roadmap and combined release history.

## Argument and copy deck (before markup)

Reader: Minecraft players choosing a mod, then pack authors checking compatibility.
Belief: This is one maker's useful and delightfully strange Minecraft collection.
Next: Find and open a mod.

Hero: Useful things. / Strange things.
Support: Minecraft mods by chimericdream, from a better bookshelf to a camel without a nose.
Action: Browse mods.
Proof: the actual Minekea catalogue book and upgraded hopper.

Showcase: Four from the workshop.
Minekea: Furniture, storage, and blocks for a world that feels like yours.
Hopper X-Treme: Faster. Filtered. Even upward.
Effective Gear: Put those armor trims to work.
Camel Nostrils: This mod is weird. Really weird.
Proof: their real artwork, metadata, descriptions, and guide links.

Directory: Find your next mod.
Support: The rest of the collection, in alphabetical order. Search by name or what it does.
Proof: all remaining mods with versions, loaders, release status, and original descriptions.

Close: Still being made.
Proof: real roadmap and changelog links, with the dragon-and-mountain brand artwork.

## Idea and live thing

On this page, chimericdream's useful furniture and absurd camel snout become an uneven catalogue of wares from the same imaginary workshop.

Live thing: search the real catalogue by name or description, with matching featured entries and an announced result count. Without JavaScript every entry remains visible.

## Decisions and redraws

- Palette retained as required: snow-like light ground, near-black violet dark ground, crimson links/actions. Brand violet is allowed explicitly in inspection.
- Type redrawn once: Switzer is served by Fontshare, while the brief asks for Google Fonts or self-hosted type and a fantasy flavor. The redraw dealt EB Garamond. Pair with Source Sans 3 for practical reading and controls.
- List redrawn to `list-menu-photo-bullets`: the original pushed-right table holds only 5–14 rows and leaves a large void; the brief requires a dense directory of 30 entries. Use two columns of horizontal entries with small real icons.
- Actual repository artwork replaces photo slots. No stock, generated imagery, decorative fantasy drawings, or unrelated video: the brief expressly excludes them.
- Stepped hero title, uneven showcase proportions, and split image/menu footer retain the dealt geometry, adjusted to keep navigation fast and text visible.
- Reading surfaces use a restrained book layout with the existing per-mod tree and collapsible navigation on phones.

## Baseline

Captured the unmodified site with `bun run site --serve --port 7300` before source edits.
Home: inspector at 1440/768/390, light and dark, in `before/light` and `before/dark`.
Other surfaces: Minekea overview and guide, roadmap, and changelog at the same widths/themes in `before/surfaces`.
Light home: 6 FAIL, 9 WARN. Full baseline reports retained for comparison.
The original dark tablet home image caught a rebuild and is invalid. `before/recovered-dark` repairs that comparison using the unchanged HEAD generator and identical repository content, with 6 FAIL and 8 WARN across all three widths. The original raw set is retained.

ROUND 0
Seen in one second: a grid of documentation cards.
Idea (one sentence): no concept beyond the documentation directory.
Live thing: none on the home page.
Layout words by section: hero=paragraph / catalogue=equal cards / footer=none
Scores: design 4.5 · usability 6.5 · creativity 3 · content 7.5 → overall 5.1
Caps applied: inspector FAILs; no dominant image; no live thing; no primary button.
Top three problems (most visible first), each with the screenshot it shows in and the fix:
1. `before/light/desktop-fold.jpg`: every mod has equal visual weight. Feature four with unequal compositions.
2. `before/light/mobile-fold.jpg`: long introductory text delays finding a mod. Use a short title and direct catalogue action.
3. `before/light/desktop-page-01.jpg`: small grey descriptions and outlined cards read as documentation tooling. Increase text size and use a compact unboxed directory.

ROUND 1
Seen in one second: the stepped Useful things / Strange things title.
Idea (one sentence): the practical catalogue book and absurd camel snout are wares from the same workshop.
Live thing: filter the 34 real mods by name or description.
Layout words by section: hero=stepped / showcase=unequal panels / directory=two-column index / footer=split
Scores: design 7 · usability 7 · creativity 7.5 · content 8 → overall 7.2, capped at 6.0 pending inspection fixes
Caps applied: inspector failures in the initial render (search control mistaken for an eyebrow; unequal content edges). One mobile capture was invalidated by a development rebuild, so it was discarded and recaptured through a stable review server.
Top three problems (most visible first), each with the screenshot it shows in and the fix:
1. `after/round-1-light/tablet-fold.jpg`: the search label breaks into two lines. Use an accessible hidden label and a clear full-width placeholder.
2. `after/round-1-light/desktop-page-02.jpg`: directory links stop at inconsistent right edges. Distribute the navigation across each entry's width.
3. `after/round-1-light/desktop-fold.jpg`: ten type sizes soften the hierarchy. Consolidate the home page to five roles.

ROUND 2
Seen in one second: large bookish type with a blue pixel-art catalogue.
Idea (one sentence): the collection's mixed parts form an uneven catalogue of one maker's wares.
Live thing: a searchable catalogue that also finds featured mods.
Layout words by section: hero=stepped / showcase=unequal panels / directory=index / footer=split
Scores: design 7.5 · usability 8 · creativity 7.5 · content 8 → overall 7.7
Caps applied: none on the final inspection of this round; four large raster artwork instances satisfy the image floor. No photographs or video are appropriate under the brief.
Top three problems (most visible first), each with the screenshot it shows in and the fix:
1. `after/light/mobile-fold.jpg`: the hero book is slightly clipped inside its narrow frame. Fit it to the frame with an inset.
2. `after/surfaces/roadmap-dark-390.jpg`: advanced filters consume most of the phone screen. Collapse them on phones, keeping the search and result table close.
3. `after/surfaces/mod-light-1440.jpg`: the article starts to the right of the mod masthead. Align the reading column with the masthead.

ROUND 3
Seen in one second: Useful things / Strange things, with a real Minecraft book.
Idea (one sentence): chimericdream's useful and absurd creations become an uneven workshop catalogue.
Live thing: search all 34 real entries, with an announced count and a complete static fallback.
Layout words by section: hero=stepped / showcase=unequal panels / directory=index / footer=split
Scores: design 7.5 · usability 8 · creativity 7.5 · content 8 → overall 7.7
Caps applied: none; the images are actual project raster artwork, not invented illustrations or photographic placeholders.
Top three remaining limitations (most visible first), each with its screenshot and disposition:
1. `after/light/desktop-page-01.jpg`: the catalogue book appears in both hero and Minekea feature. A genuine in-game Minekea scene would add more evidence; no screenshot was invented or substituted.
2. `after/light/mobile-page-01.jpg`: featured descriptions are dense in two unequal columns. Readable at 16px, with the search and full-width directory providing fast routes to the content.
3. `after/surfaces/roadmap-light-1440.jpg`: the complete roadmap has more columns than a narrow viewport can show. Preserve the real table with keyboard-focusable horizontal scrolling and filters.

## Warning dispositions

- `palette-purple` is explicitly allowed because violet is a required brand seed.
- `font-not-dealt`: EB Garamond loaded successfully; Source Sans 3 was intentionally added for long-form reading and controls, fulfilling the brief's request for a readable pair.
- `no-footage`: the brief prohibits stock or invented imagery; no suitable project video exists in the supplied assets.
- Source lint's em-dash warning comes from existing roadmap missing-value markers and parsing syntax, not marketing copy.
- Recipes and roadmap tables preserve their own semantic data conventions; their cells are not decorative panel borders.

## Imagery register

No found photographs, stock photographs, bundled photographs, generated images, or video were used. No sample mod content was invented. Existing repository assets remain under their existing licenses and credits.

- `docs/design/logo-placeholder.svg`: existing author's placeholder trace of the dragon/mountains avatar. Copied as `brand.svg`; used by header and footer. Replace this source file when the original vector is supplied.
- `minekea/.../textures/item/minekea_catalogue.png`: existing Minekea in-game catalogue; copied as `catalogue.png`; hero and Minekea feature.
- `hopper-xtreme/.../assets/hopperxtreme/icon.png`: existing diamond-rimmed hopper; hero detail and Hopper X-Treme feature.
- `effective-gear/.../assets/effectivegear/icon.png`: existing gear and armor materials; Effective Gear feature.
- `camel-nostrils/.../assets/camelnostrils/icon.png`: existing camel snout; Camel Nostrils feature.
- Every other available mod icon is used in the directory; missing icons get neutral typographic initials. Existing README/guide image credits remain in their source content.
