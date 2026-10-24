# Docs site redesign

The site is now a catalogue of chimericdream's improbable wares, with book-like reading pages. The
implementation keeps Bun, marked, static HTML, all published documents and the existing routes. The
local dashboard's source is untouched; its shared theme tokens still work.

## Direction and decisions

The design-taste-frontend draw selected **The printed annual**, seed **1410661816**, with the brand
palette kept. The site's particular mixture supplies the concept:

> On this page, chimericdream's furniture, huppers, armor trims and camel noses become a catalogue of
> improbable wares under the dragon's mark.

The introductory spread and directory reuse the direction's 42% axis. Four unequal panels introduce
the requested mods; a two-column catalogue lists the other 30. The phone edition gives each featured
entry a readable spread. Search filters the real names and descriptions of all 34 mods, including the
featured four; without JavaScript the full collection remains accessible.

Four redraws were recorded:

1. **Hero:** the original full-screen photograph does not exist. The replacement was **Two-by-two
   board**, using actual mod artwork and fields of color.
2. **Type:** the brief asks for a distinctive pair with comfortable book reading. **Libre Franklin /
   Source Serif 4** replaced the single Hanken Grotesk family. Source Serif also carries book titles;
   Libre Franklin handles catalogue headings and controls.
3. **Showcase:** four portrait photographs do not exist. **Band of unequal panels** holds the four
   real mods instead.
4. **Directory:** a pinned deck would hide the 30 entries visitors need to scan. **Two-column menu,
   photo bullets** supplies the dense list.

The board and showcase form one spread, so featured entries are not repeated as two separate sections.
The signature slow crossfade cycles Minekea's actual catalogue, wrench and lantern textures. It has a
pause control, pauses offscreen and in background tabs, and stays still under reduced motion. Separate
section and row entrances use transform and opacity; text remains available if scripting fails.

The light ground is pale parchment `#f2f0eb`, with crimson `#8b1a32`. Dark uses shadow violet `#160e20`,
snow ink, and a lighter crimson accent for contrast. OS preference is the default; a remembered manual
choice is available. Violet is a deliberate brand exception to the skill's house palette rule.

Mod pages expose download links from the existing CurseForge project IDs, loader names and Minecraft
compatibility. Existing dependency badges and README content remain published. Guides retain their
navigation tree, with a sticky desktop tree and a collapsible phone tree. Callouts, tables, code and
recipes use the same reading system. Roadmap filter buttons and sort controls now support the keyboard
and expose pressed/sort state.

## Evidence

Before screenshots were captured from the unchanged site running with
`bun run site --serve --port 8123`, before changing generator files. Both before and after matrices cover
the home, Minekea overview, Opus kitchen-sink guide, roadmap and combined changelog in light and dark at
1440, 768 and 390 pixels. Each matrix has 30 first-screen images and six full home-page captures.

Screenshots and audit evidence are committed under `.taste-skill/`; local automation scratch files are
ignored:

- `before/`: original skill audit plus `light/{width}/` and `dark/{width}/` screenshots.
- `after/`: final dark skill audit, both-theme page matrix, guide detail screenshots and functional
  verification JSON.
- `after-light/`: final light skill audit.
- `direction.json`: the draw and selected blueprints.

| Measurement | Before | After |
| --- | --- | --- |
| Skill audit failures, home at 1440 / 768 / 390 | 6 combined | 0 in both themes |
| Desktop title | 32px, system UI | 80px, Source Serif 4 |
| Phone title | 32px | 52px, two lines |
| Directory running text | 14px | 16px, full ink |
| Long-form body | 16px, system UI | 18px, Source Serif 4 |
| Uniform outlined mod cards | 34 | 0 |
| Home discovery | Browse 34 equal cards | Four featured entries, 30-entry list, search all 34 |
| Theme selection | OS only | OS or remembered light/dark |

Validation completed:

- `bun run site` completes successfully; the production output contains no `/local/` directory.
- `bun run lint` passes, including UTF-8 and LF checks.
- The skill source lint reports **0 FAIL**, with one warning for existing em-dash data placeholders.
- The unmodified skill inspector reports **0 FAIL** at all three widths in both themes. A wrapper only
  selects the browser media preference and isolated 8xxx debugging port.
- All **629 public HTML pages** were scanned: **20,364 local references**, zero missing targets and zero
  root-relative references. This checks the relative path structure needed under `/minecraft-mods/`.
- **19 browser checks** passed: theme persistence and reset, phone tree expansion and guide navigation,
  featured-mod search, empty results and clear, no-script collection and navigation, reduced motion,
  216 roadmap rows, filter selection, sort direction, bookmarked filters, reset, guide textures and
  absence of client exceptions in the checked flows.
- The 30 final matrix views have no document overflow or failed loaded images. Lazy guide textures were
  also forced to load in the functional check; they load correctly. Early matrix logs counted unloaded
  lazy images as errors, so those are not broken source assets.
- A standalone TypeScript check was attempted, but this checkout does not have the TypeScript compiler
  installed. Bun's actual generation and browser behavior were verified instead.

Retained audit warnings have concrete reasons: the brief calls for a pale parchment light theme; no
honest gameplay video exists in the supplied assets; the margin heuristic measures occupied text and
reports 43px/79px, while the header padding and catalogue surface use equal outer gutters. The final
screenshots were opened and reviewed, including every fold and page sheet listed by both inspectors.

## Visual review

```text
ROUND 1
Seen in one second: a catalogue title above four unlike wares.
Idea (one sentence): the mixed collection becomes a workshop catalogue.
Live thing: search the actual mod collection.
Layout words by section: intro=spread / featured=unequal band / directory=menu / footer=masthead
Scores: design 6.5 · usability 7.2 · creativity 7.0 · content 8.0 → overall 6.9
Caps applied: 6.0 overall while motion and picture-size failures remained.
Top three problems (most visible first), each with the screenshot it shows in and the fix:
1. round-1/mobile-fold.jpg: the theme select wraps to its own row. Compact the wordmark and select.
2. round-1/mobile-page-01.jpg: narrow feature columns turn descriptions into tall strips. Give phone entries full-width spreads.
3. round-1/desktop-fold.jpg: the Minekea logo advertises a name rather than showing its world. Use the actual catalogue and item textures.

ROUND 2
Seen in one second: the blue catalogue book against violet paper.
Idea (one sentence): the catalogue presents useful and absurd inventions together.
Live thing: directory filtering, including featured entries.
Layout words by section: intro=spread / featured=unequal band / directory=menu / footer=masthead
Scores: design 7.3 · usability 8.0 · creativity 7.2 · content 8.0 → overall 7.5
Caps applied: 6.0 overall while the final repeat audit still reported failures.
Top three problems (most visible first), each with the screenshot it shows in and the fix:
1. after/mobile-fold.jpg: the theme label is truncated. Shorten options to System, Light and Dark.
2. after/mobile-page-01.jpg: the phone type scale contains incidental sizes. Consolidate the catalogue sizes.
3. Audit repeat: short Web Animation entrances are sampled inconsistently. Use real CSS entrance animations with animationstart events; retain the visible no-script state.

ROUND 3
Seen in one second: A curious collection, then a book, a hopper, gear and a camel nose.
Idea (one sentence): chimericdream's unlike inventions become the wares in one catalogue.
Live thing: fast filtering of all 34 real mods.
Layout words by section: intro=spread / featured=unequal band / directory=menu / footer=masthead
Scores: design 7.5 · usability 8.2 · creativity 7.2 · content 8.0 → overall 7.7
Caps applied: none. Final light and dark inspector runs have zero failures.
Top three problems (most visible first), each with the screenshot it shows in and the fix:
1. after/desktop-fold.jpg: icons and flat textures cannot show furniture in context. Add actual gameplay screenshots when available; the current image slots support that.
2. after/mobile-page-01.jpg: a 30-entry directory remains a long read on a phone. Search and the first-screen Find a mod action address discovery; no content is removed.
3. after/light/1440/mod.jpg: old README version badges can disagree with current generated metadata. Keep the source content as requested; update those READMEs separately.
```

## Imagery and launch content

No stock photographs, bundled photos, archival finds, generated imagery, sample products or invented
proof were used. The dragon mark remains a referenced file copied from
`docs/design/logo-placeholder.svg`; replacing that SVG updates the mark on regeneration. The four mod
icons and Minekea catalogue, wrench and ancient-lantern PNGs come from the repository and render with
crisp pixels. Existing texture attribution is preserved in Minekea's published README credits, including
the wrench attribution to malcolmriley under CC BY 4.0.

The remaining visual limitation is the lack of in-world screenshots. Nothing needs sample content
replacement before launch. The generator still emits the same six pre-existing unresolved/cross-book
content-link warnings seen in the baseline.
