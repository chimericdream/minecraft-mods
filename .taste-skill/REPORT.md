# Chimericdream site redesign

[Open the preview](http://localhost:6101/).

The site is now a fantasy workshop catalogue: a tabbed collection of real mod artwork, four unevenly featured entries, a searchable index, and book-like reading pages. STIX Two Text supplies the literary character; Work Sans handles navigation and controls. Snow, shadow violet, and crimson remain the brand colours.

The preview is running with `bun run site --serve --port 6101 --no-watch`. The optional `--no-watch` flag freezes automatic rebuilds during capture. Normal `--serve` still watches content. Generator changes require a restart.

## Direction and decisions

**The page is the object**, seed **62318**, dealt with `--keep palette`.

**Idea:** On this page, chimericdream's mixed collection and the real Minekea Catalogue become a tabbed workshop catalogue, with each mod's documents bound together as a reading folio.

**Live thing:** search all 34 mods by name, README content, loader, or release status. Exact names lead broader text matches. The initial index shows the other 30 mods; the featured four also appear in relevant search results. The unenhanced HTML retains all four features and the 30-entry index.

Four redraws/adaptations were recorded:

1. **Card over a dim room → Tabbed folder stack.** The original hero required a room photograph that does not exist; stock photography is forbidden by the brief.
2. **Anton → STIX Two Text.** The original condensed sports/nightlife capitals conflicted with the fantasy book/catalogue brief. Work Sans from the initial deal remains the compact interface face, providing the requested pair.
3. **Output slips along the edge → Band of unequal panels.** The original required a full-bleed photo and six textual specimens; the brief supplies four different mods with real raster artwork.
4. **Accordion categories → Two-column menu with image bullets.** There are 30 individual remaining mods, rather than six to nine real categories.

The dealt founder's letter was omitted because no biography or first-person content was supplied. The retained violet uses the skill's existing-brand exception. The parchment-coloured sheets in the light theme follow the brief's explicit parchment allowance.

The desktop feature heading holds beside the four entries as they scroll. Text, pictures, and index entries use different entrance treatments; the genuine Minekea book sprite drifts slightly. The inspector observed 66 moving elements. Reading surfaces stay still. Reduced motion removes animation and travel without removing content.

## What changed

- Four featured mods use different panel proportions and real repository artwork. Minekea's book links directly to its guide.
- A dense alphabetical index replaces the 34 equal outlined cards. Search includes the existing README text and has release filtering, keyboard focus via `/`, no-results feedback, and a live count.
- Overview pages show the configured version, Minecraft compatibility, loaders, a CurseForge action where a project ID exists, and document links near the top.
- Guides, ideas, and changelogs receive readable type, a controlled measure, flat callouts, scrollable tables/code, visible focus, and a skip link.
- The existing nested sidebar navigation survives. It becomes a native disclosure on phones and reopens when resized to desktop.
- The roadmap has real buttons for filters and sorting, pressed/sort states for assistive technology, idea titles first, and advanced filters behind “More filters” on phones.
- Both themes follow the OS initially. A manual system/light/dark choice persists without a theme flash.
- The Bun + marked stack and published content set remain intact. No dependency or lockfile changes were needed. `scripts/site/local/` has no source changes; its shared colour-token export still works.

## Before and after measurements

| Check | Before | After |
| --- | --- | --- |
| Home inspector, 1440/768/390 | 6 FAIL, 8 WARN | Dark: 0 FAIL, 1 WARN. Light: 0 FAIL, 2 WARN |
| Desktop headline | 32px, system UI | 108px, STIX Two Text |
| Phone headline | 32px | 48px, two lines |
| Predominant home body size | 14px | 18px |
| Smallest home reading/UI text | 11px | 14px |
| Equal outlined cards | 34 | 0 |
| Large raster pictures measured | 0 | 4 genuine mod assets |
| Moving elements measured | 0 | 66, disabled with reduced motion |
| Manual remembered theme | Absent | System / light / dark |
| Matched screenshot matrix | 5 surfaces × 3 widths × 2 themes | Same 30 views; all have titles and no page overflow |

The remaining inspector warnings are deliberate: no video is available or appropriate under the no-stock/no-invented-imagery constraint, and the light catalogue uses pale parchment sheets as the brief permits. The page ground itself is snow grey. Public-source lint has 0 FAIL; its remaining em-dash warning counts existing missing-value markers and title separators rather than new marketing prose. The broader folder also includes untouched local-dashboard outlines.

### Verification

- Build succeeds: **34 mods, 19 guides, 205 guide pages, 421 docs, 216 ideas**, matching the baseline.
- **630 generated HTML pages** audited, including the one local-only dashboard. The public page count remains 629.
- **20,479 internal links/assets checked** under a simulated `/minecraft-mods/` deployment prefix: no missing destinations and no base-path escapes.
- **20 browser checks pass**: search/ranking, feature terms, release filters, empty state, keyboard search, theme persistence, OS reset, download destination metadata, phone/desktop guide navigation, 216 ideas, roadmap filtering/sorting/ARIA states, phone disclosure, contained horizontal table scrolling, reduced motion, no script exceptions, and the no-JavaScript catalogue fallback.
- Final UTF-8/LF checks and `git diff --check` pass for changed sources.
- Shared public JavaScript: **3,906 bytes**. Stylesheet: **19,140 bytes**. Home HTML: **153,192 bytes** before compression, including the prebuilt README search text. No search backend or framework was added.

Evidence: [browser/link checks](verification.json), [dark inspector](after/report.json), [light inspector](after-light/report.json), [before inspector](before/report.json), [final matrix measurements](after/matrix/measurements.json).

The strict site TypeScript check still reports four `Bun.YAML` typing errors at the unchanged `guide.ts` lines 32, 96, 108, and 195. The installed Bun runtime builds those guides successfully; the repository's older Bun typings lack that API. Existing unresolved-content-link build warnings also remain. No unrelated renderer or dependency changes were made to suppress them.

CurseForge link destinations are generated from configured project IDs. A direct automated HTTP check was blocked with a provider-side 403; no download was performed. The internal-link audit does not claim to validate external services.

## Matched screenshots

Every matrix row links to a baseline captured before source edits and its final counterpart. Widths are CSS pixels.

| Surface | Width | Light before | Light after | Dark before | Dark after |
| --- | --- | --- | --- | --- | --- |
| Home | 1440 | [Before](before/matrix/home-1440-light.jpg) | [After](after/matrix/home-1440-light.jpg) | [Before](before/matrix/home-1440-dark.jpg) | [After](after/matrix/home-1440-dark.jpg) |
| Home | 768 | [Before](before/matrix/home-768-light.jpg) | [After](after/matrix/home-768-light.jpg) | [Before](before/matrix/home-768-dark.jpg) | [After](after/matrix/home-768-dark.jpg) |
| Home | 390 | [Before](before/matrix/home-390-light.jpg) | [After](after/matrix/home-390-light.jpg) | [Before](before/matrix/home-390-dark.jpg) | [After](after/matrix/home-390-dark.jpg) |
| Minekea | 1440 | [Before](before/matrix/minekea-1440-light.jpg) | [After](after/matrix/minekea-1440-light.jpg) | [Before](before/matrix/minekea-1440-dark.jpg) | [After](after/matrix/minekea-1440-dark.jpg) |
| Minekea | 768 | [Before](before/matrix/minekea-768-light.jpg) | [After](after/matrix/minekea-768-light.jpg) | [Before](before/matrix/minekea-768-dark.jpg) | [After](after/matrix/minekea-768-dark.jpg) |
| Minekea | 390 | [Before](before/matrix/minekea-390-light.jpg) | [After](after/matrix/minekea-390-light.jpg) | [Before](before/matrix/minekea-390-dark.jpg) | [After](after/matrix/minekea-390-dark.jpg) |
| Beams guide | 1440 | [Before](before/matrix/guide-1440-light.jpg) | [After](after/matrix/guide-1440-light.jpg) | [Before](before/matrix/guide-1440-dark.jpg) | [After](after/matrix/guide-1440-dark.jpg) |
| Beams guide | 768 | [Before](before/matrix/guide-768-light.jpg) | [After](after/matrix/guide-768-light.jpg) | [Before](before/matrix/guide-768-dark.jpg) | [After](after/matrix/guide-768-dark.jpg) |
| Beams guide | 390 | [Before](before/matrix/guide-390-light.jpg) | [After](after/matrix/guide-390-light.jpg) | [Before](before/matrix/guide-390-dark.jpg) | [After](after/matrix/guide-390-dark.jpg) |
| Roadmap | 1440 | [Before](before/matrix/roadmap-1440-light.jpg) | [After](after/matrix/roadmap-1440-light.jpg) | [Before](before/matrix/roadmap-1440-dark.jpg) | [After](after/matrix/roadmap-1440-dark.jpg) |
| Roadmap | 768 | [Before](before/matrix/roadmap-768-light.jpg) | [After](after/matrix/roadmap-768-light.jpg) | [Before](before/matrix/roadmap-768-dark.jpg) | [After](after/matrix/roadmap-768-dark.jpg) |
| Roadmap | 390 | [Before](before/matrix/roadmap-390-light.jpg) | [After](after/matrix/roadmap-390-light.jpg) | [Before](before/matrix/roadmap-390-dark.jpg) | [After](after/matrix/roadmap-390-dark.jpg) |
| Changelog | 1440 | [Before](before/matrix/changelog-1440-light.jpg) | [After](after/matrix/changelog-1440-light.jpg) | [Before](before/matrix/changelog-1440-dark.jpg) | [After](after/matrix/changelog-1440-dark.jpg) |
| Changelog | 768 | [Before](before/matrix/changelog-768-light.jpg) | [After](after/matrix/changelog-768-light.jpg) | [Before](before/matrix/changelog-768-dark.jpg) | [After](after/matrix/changelog-768-dark.jpg) |
| Changelog | 390 | [Before](before/matrix/changelog-390-light.jpg) | [After](after/matrix/changelog-390-light.jpg) | [Before](before/matrix/changelog-390-dark.jpg) | [After](after/matrix/changelog-390-dark.jpg) |

Full-home captures are also available with `-full.jpg` after each home filename. Minekea's 1440px captures have full-page versions too. The skill inspector's page sheets cover the full home scroll in [dark](after/) and [light](after-light/).

Early intermediate captures mixed old and new pages during automatic rebuilds and are not used as final evidence. The final matrix was taken against a fixed preview, rejects pages without a title/h1, and has no incomplete views.

## Imagery and content

All imagery is existing repository material, with its original licensing and credits retained:

| Material | Source | Use |
| --- | --- | --- |
| Dragon/mountains/moon mark | `docs/design/logo-placeholder.svg` | Referenced by path, copied to the generated brand directory; replaceable without CSS edits |
| Minekea Catalogue book | `minekea/common/src/main/resources/assets/minekea/textures/item/minekea_catalogue.png` | Large featured artwork and direct guide link |
| Hopper X-Treme icon | `hopper-xtreme/common/src/main/resources/assets/hopperxtreme/icon.png` | Featured artwork and directory icon |
| Effective Gear icon | `effective-gear/common/src/main/resources/assets/effectivegear/icon.png` | Featured artwork and directory icon |
| Camel Nostrils icon | `camel-nostrils/common/src/main/resources/assets/camelnostrils/icon.png` | Featured artwork and directory icon |
| Other mod icons and guide textures | Existing per-mod resource directories | Existing content, rendered crisply |

No found photographs, bundled photographs, video, new drawings, generated imagery, or invented screenshots were used. Therefore there are no new external image-credit lines or stand-in photographs to replace. Google Fonts supplies STIX Two Text and Work Sans. No invented customer proof, biography, or sample mod content was added; the four featured summaries paraphrase their real READMEs.

## Final critique

ROUND 3

Seen in one second: a large Minecraft title above a violet tabbed catalogue and genuine game artwork.

Idea (one sentence): the author's mixed collection and real Catalogue guide become a browsable workshop folio.

Live thing: real README search with name ranking, release filtering, and accessible result feedback.

Layout words by section: hero=poster / features=unequal sheets with held heading / collection=two-column index / footer=back cover / guides=reading folio / roadmap=filterable table.

Scores: design 8 · usability 8.5 · creativity 7.5 · content 5 → overall 7.8

Caps applied: content capped at 5 because a few existing descriptions still contain placeholder text; the brief requires preserving source content. No inspector-failure, heading-wrap, overflow, or motion caps apply.

Top three remaining limits (most visible first), each with the screenshot and the appropriate next step:

1. `after-light/desktop-page-01.jpg`: a few directory descriptions remain unfinished in their source (for example Cobblicious and Beacon & Conduit Tweaks). They should be written in each mod's `gradle.properties` as a separate content task.
2. `after/matrix/minekea-1440-light.jpg`: the existing README version badge lags the configured version shown by the new header. The README should be updated by its normal content workflow; its published content was preserved here.
3. `after/desktop-fold.jpg`: authentic pixel artwork conveys the mods, but cannot show furniture in a real build. Future genuine in-game screenshots can strengthen installation appeal; the current site deliberately remains complete without them.

The final screenshots were checked for focal point, five-second orientation, subject specificity, edges, spacing, type, colour, imagery, section variety, phone composition, fullness, substance, collisions, and the live interaction. All final inspector-listed screenshots in both themes were opened. The reading-surface matrix confirms both theme grounds and no page overflow at all three widths.

The [working notes](design-notes.md) contain the inventory, argument, copy deck, baseline critique, and detailed adaptation decisions.
