# Docs site restyle report

The docs site was restyled following [`docs/SITE-DESIGN-BRIEF.md`](../../SITE-DESIGN-BRIEF.md) and the
`design-taste-frontend` skill's redesign workflow.

Every page type now passes the skill's inspector with 0 FAILs (before: home 6 FAIL, mod page 6, guide
page 7). For the mod and reading pages, a few rules were waived; the reasons are under "Inspector rules
overridden" below.

Files in this folder:

- [`before/shots/`](before/shots/) and [`after/shots/`](after/shots/): the same 9 pages in both themes at
  1440, 768 and 390px.
- `before/inspect-*/` and `after/inspect-*/`: the inspector's own captures and `report.json` per page.
- [`critique.md`](critique.md): the critique rounds in the skill's format.
- [`direction.json`](direction.json): the direction sheet the skill dealt.

## Direction

- **Layout:** the skill dealt "The well-kept board": flat tiles on 8px gaps, no outlines or shadows.
- **Idea:** the collection is the chimera, with 34 mismatched mods fitted into one board and each mod
  filled with its own pixel textures.
- **Interactive piece:** a "Find a mod" box on the first screen. It shows matches with icons as you
  type; Enter opens the first and `/` jumps to the box.
- **Redraws:** both dealt font pairs were set aside because neither suited the fantasy brief. The dealt
  list layout was swapped for one with an icon beside each mod, which fits 30 mods better.
- **Type:** Grenze for headings (a typeface that is part blackletter, part roman, a chimera of type) and
  Source Serif 4 for body text, both from Google Fonts.
- **Colour:** dark mode is black, snow and crimson, with gold as the second colour. Light mode uses a
  moon-grey ground with near-black ink. Violet appears only in the logo; gold sits opposite violet on
  the colour wheel.

## What each page does now

- **Home:**
  - the full-width name, with the dragon mark and the one-line pitch
  - a scrolling band of every mod name
  - the four featured mods at deliberately uneven sizes, labelled "The big one", "The useful one",
    "The small one" and "The weird one", each with its README tagline
  - the other 30 in a dense A–Z list, grouped as Released and In testing
  - a "Before you install" section listing the libraries and how many mods need each, counted from
    every `fabric.mod.json`
- **Mod pages:** the name, an icon plate, a wide "Read the guide" tile, the Minecraft version and
  dependencies, a strip of the mod's own textures, then the README. The sidebar also lists all mods.
- **Guides:** a 70-character reading column with an "On this page" list beside it. Callouts are filled
  panels, and recipes are drawn as crafting slots using the mod's own textures.
- **Changelog and roadmap:** restyled. The roadmap's star ratings now read High, Medium and Low, and the
  filter toggles work from the keyboard.
- **Theme switch:** cycles Auto → Light → Dark and is remembered across pages.

Checked in a real browser: the search box, the theme switch, the phone contents menu and the roadmap
filters. All links are relative and nothing scrolls sideways at 390px. `THEME_CSS` keeps its token
names, and the `/local/` dashboard still renders in both themes.

## Before launch

- The "Find it on Modrinth" and "Find it on CurseForge" buttons link to search results, because the
  repo doesn't record the project URLs. Direct links would be better.
- Sample copy to confirm: the home pitch, the "Four from the workshop" intro, the four labels, the
  one-line library descriptions, and the footer text.
- The logo is still the placeholder trace. The build copies `docs/design/logo-placeholder.svg` to
  `brand/logo.svg`, so replacing that file updates every page.
- Vanilla items in recipe grids show their legend letter instead of a texture, because vanilla textures
  aren't in the repo.
- No outside images were used: no stock photos and nothing from open collections. Every picture comes
  from the mods' own icons, textures and one Hopper X-Treme guide image.

## Inspector rules overridden

- **Picture count and video:** the skill wants 4+ photos and a video per page. The brief rules out
  stock imagery and assumes few screenshots, so mod pages show only the icon and textures.
- **Reading pages:** the rules written for landing pages (one dominant element, a minimum amount of
  motion, centred margins) were waived there. Text held to a reading width can't fill the screen.
- **Headings:** the "label above heading" rule was waived on Markdown content. It fires whenever a short
  README line ends just above a heading, which the design can't control. The designed pages pass it.

## Problems hit

- The dev server's file watcher rebuilt the site with stale generator code mid-session. Restart
  `bun run site --serve` after pulling generator changes.
- `tsc` couldn't run because there was no `node_modules`, but `bun run site` builds all 205 guide pages
  and 421 docs cleanly.

## Remaining weak spots

Mod pages will look much better once screenshots exist (there's room for them), and the scrolling name
band is the only bold motion on the site.
