# Site restyle notes

Direction: `contact-sheet` (seed 642539483), dealt with `--keep palette`. Chosen over `uneven-photographs`
and `pinned-poster` because every mod ships its own pixel-art textures; a sheet of a mod's real frames is
something only this collection has.

Redraws and deviations:
- Type: dealt Bricolage Grotesque (one grotesk). Replaced with the library's `young-source` system
  (Young Serif display / Source Serif 4 text) because the brief asks for a pair that suits the fantasy
  flavour; a random redraw gave a newspaper pairing, so the system was chosen from the library directly.
- Pictures: the skill's four-picture floor and footage warning are allowed off (`--allow few-pictures,no-footage`)
  at the author's request. No archive, stock or dealt photographs are used; the only pictures are the mods'
  own textures and icons, README/guide screenshots, and the brand mark.
- Sections: `intro` (statement tiles) dropped, no claim left for it. `showcase` mosaic rebuilt with the brand
  mark instead of a photograph, four colour tiles instead of five (all on the mark's black edge columns, so
  the picture stays whole when narrow screens drop those columns).
- Home lists all 34 mods in the A to Z index (not only the other 30), for the "find a mod in ten seconds" goal.
- Brand violet kept despite the house "no violet" rule: it is the brand's own colour.

Idea: on this page the chimera, one creature made of mixed parts, becomes the contact sheet: each mod is a
run of its own textures, and a run's length shows the mod's size (Minekea's eight frames, Effective Gear's
single enlarged icon).

Live thing: the A to Z catalogue. Type to filter (press /, Enter opens the first match); pointing at or
tabbing to a row brings that mod's frames, description and links into the preview frame beside it, and a
different mod waits there on each visit. Without JS: the full list, no filter box.

## Before / after (home, inspect.mjs)

| | before | after |
| --- | --- | --- |
| Result | 6 FAIL, 7 WARN | 0 FAIL, 0 WARN |
| Largest type | 32px (2.2vw) | 101px (7vw) |
| Body text | 14px, grey 6.3:1 | 16–19px, full ink |
| Sizes in use | many | 16 / 19 / 26 / 37 / 64 / 101 |
| Moving elements | 0 | 16+ |
| Primary button on first screen | none | "Find a mod" |

## Critique

ROUND 3 (final)
Seen in one second: a big serif title, "Mods from the dragon's workshop", over rows of real pixel-art items.
Idea (one sentence): each mod is a run of its own textures on a contact sheet; run length is the mod's size.
Live thing: filter plus preview frame in the A to Z catalogue.
Layout words by section: hero=text band then sheet / catalogue=two-column index with anchored preview /
workshop=mark cut across a tile board with fact tiles / footer=one quiet line.
Scores: design 7.5 · usability 8 · creativity 7.5 · content 7 → overall 7.6.
Caps applied: none on the home page. Mod pages and reading surfaces still carry inspector FAILs (see below),
so judged as pages they would cap at 6.0.
Top three problems (most visible first):
1. Hero facts block (desktop-fold, right side) is small and reads as a sidebar; it could be one size up or
   moved under the lede.
2. The A to Z tags ("pre-release", "changes coming") wrap under long names at 1440 (desktop-page-01), making
   uneven rows.
3. Hopper X-Treme gets only three frames because most of its item textures are "unset/placeholder" states the
   picker skips; a curated frame list per featured mod would show it better.

Remaining inspector findings, by page:
- Minekea: `empty-screens` on screens 2–3, the README's long list of short bullets in a reading column.
- Effective Gear: `emoji` ⚡ comes from the README.
- Guide, changelog, roadmap: `focal-point` and `still-page`; the brief asks these to favour typography over
  spectacle, so they get a quiet entrance and no display type. The roadmap also has `off-centre` at 768/390
  from its horizontally scrolling data table.
