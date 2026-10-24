# Design argument and copy deck

Inventory: chimericdream's composite-creature name; crimson dragon; twin snowy peaks;
crescent moon; 34 mods; Minekea furniture; Hopper X-Treme's practical upgrade;
Effective Gear's tiny bonuses; spouse-suggested Camel Nostrils absurdity; Opus books;
shared ChimericLib; real pixel artwork and crafting recipes.

Audience: players finding a mod, followed by pack authors checking compatibility.
Argument: a varied maker's collection (four real featured mods); find your next mod
(complete generated catalogue and filter); read and follow its development (existing
guide, roadmap and changelog links).

Direction: The printed annual, seed 1973745401. Its column axis and unequal mosaic
can make these disparate creations feel like one maker's illustrated catalogue.
Idea: On this page, chimericdream's practical and absurd Minecraft creations become
the unequal plates of a fantasy workshop catalogue.
Live thing: filter the real generated collection by name or description.

Copy deck:
- Minecraft, with character
- Furniture, useful little fixes, and the occasional camel nose. 34 Minecraft mods
  by chimericdream, for Fabric and NeoForge.
- Browse the mods
- Minekea / Furniture, storage & building blocks.
- Hopper X-Treme / A practical upgrade to the humble hopper.
- Effective Gear / Small bonuses for the gear you already use.
- Camel Nostrils / This mod is weird. Really weird.
- The mod catalogue / Find something for your next world.
- In the workshop / Roadmap / Changelog / GitHub

Decisions and exceptions: preserve brand violet (brief overrides house ban); use
only existing artwork, no stock photos or invented assets. Literata is the dealt
face, used throughout. The list blueprint was redrawn to two-column menu/photo
bullets because the original assumes 5–14 rows and this catalogue has 30.
The masthead also performs the four-item showcase, avoiding two repeated image
bands. Footer is compact because there are no offices/legal/contact columns.
Reading surfaces retain their content and navigation, with no decorative motion.

ROUND 0
Seen in one second: a generic documentation card grid.
Idea (one sentence): none beyond a mod directory.
Live thing: links to each mod.
Layout words by section: hero=intro / directory=cards
Scores: design 5 · usability 6 · creativity 4 · content 8 → overall 5.4
Caps applied: inspector failures; no dominant element; small body text.
Top three problems (most visible first), each with the screenshot it shows in and the fix:
1. Desktop fold: 34 equal cards hide the range; feature four unequal plates.
2. All folds: 14px muted descriptions; use 16px full-ink text and clear type hierarchy.
3. Phone fold: introductory plumbing copy delays finding a mod; put catalogue access on the first screen.

ROUND 1
Seen in one second: pixel objects above a large editorial title.
Idea (one sentence): the maker's practical and absurd creations form a workshop catalogue.
Live thing: name and description filtering of the complete collection.
Layout words by section: hero=unequal mosaic and axis / catalogue=two-column menu / footer=wordmark
Scores: design 6.5 · usability 7.5 · creativity 7 · content 8 → overall 7.0
Caps applied: inspector failures; no photography (deliberate brief constraint).
Top three problems (most visible first), each with the screenshot it shows in and the fix:
1. round1 desktop-page-01: alternating catalogue fills resemble cards; remove those fills.
2. round1 mobile-fold: introduction is too small against the title; increase to 18px.
3. round2 light/390/mod: breadcrumb is centered; remove the duplicate layout class on its inner span.

ROUND 2 (final)
Seen in one second: the four real mod artworks and a strong literary Minecraft title.
Idea (one sentence): chimericdream's useful and absurd creations become the unequal plates of a fantasy workshop catalogue.
Live thing: a working filter across all 34 generated mod names and descriptions.
Layout words by section: hero=unequal mosaic and axis / catalogue=two-column menu / footer=wordmark
Scores: design 6.5 · usability 8 · creativity 7 · content 8 → overall 7.2
Caps applied: photography cap retained literally from the rubric; the brief explicitly prohibits stock and invented imagery.
Top three problems (most visible first), each with the screenshot it shows in and the fix:
1. after desktop-fold: Minekea's flat logo is less evocative than its neighbors' item artwork; a real in-game furniture screenshot would improve this plate when available.
2. after mobile-page-01: the complete catalogue is long; the working search and jump button mitigate it without withholding content.
3. after light/390/roadmap: the existing many-filter model fills the first screen; controls are now keyboard accessible and the full table remains horizontally scrollable.

## Validation and comparison

| Measurement | Before | After |
| --- | --- | --- |
| Inspector failures | 6 | 0 |
| Inspector warnings | 8 | 3 |
| Desktop title | 32px system UI | 93.6px Literata |
| Body text | 14px | 16px (reading articles 17–18px) |
| Title lines at 1440 / 768 / 390 | 1 / 1 / 1 | 2 / 2 / 2 |
| Homepage organization | 34 equal cards | 4 featured plates + 30 catalogue rows |

Final warning defenses: the size-count includes metadata, navigation, editorial title,
and footer wordmark, all with distinct information roles. No footage exists and the
brief rules out borrowed or invented imagery. The measured 56px right edge is a
plate's padded content; actual masthead, main and header outer gutters are 32px on
both sides (20px on phones).

Before and after folders include home, mod, guide, roadmap and changelog captures
at 1440, 768 and 390 in both themes. Final capture checks found no horizontal page
overflow or broken images. All final inspector sheets were opened, along with
contact sheets for all 30 representative page/theme/width combinations.

Build passed (34 mods, 19 guides / 205 guide pages, 421 docs, 216 ideas).
Repository lint and git diff whitespace checks passed. Skill source lint passed.
All 19,728 generated internal file references resolve; this does not assert that
external services or fragment IDs are valid. Browser checks passed for default,
matching and empty catalogue states, clear, theme persistence, independent roadmap
filters, mobile navigation, configured download destination, system dark mode,
reduced motion and a no-JavaScript catalogue. No runtime exceptions were observed.
Full TypeScript check has four existing Bun.YAML typing errors in guide.ts;
there are no remaining errors in the modified files.

No external photographs, clips or generated artwork were added. Imagery comes from
the repository's mod icons and supplied logo-placeholder.svg; existing README
credits remain published. The supplied avatar trace can be replaced at its source
path. No invented sample content was introduced. Existing weak mod descriptions
and README version badges were preserved as source content.
