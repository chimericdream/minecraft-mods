# Opus — Ideas

Feature ideas for this mod, from first thought to ready to build. The process is described in
[`docs/FEATURE-WORK.md`](../../docs/FEATURE-WORK.md).

## Identity

> **Draft, needs approval.** Markdown-driven in-game documentation: a book is a folder of Markdown files, with
> no Java and no JSON, in the spirit of Patchouli. The tone is a plain, readable reference that feels at home in
> Minecraft but stays out of the way of the pack author's own voice. Not a lore or story mod, and not a general
> wiki or recipe viewer.

## Active ideas

### Authoring

- [Look recipes up by id](authoring/server-recipe-lookup.md) — `recipe: minecraft:hopper` resolves to the real recipe through a small server sync; inline recipes stay the fallback.
- [Patchouli to Opus converter](authoring/patchouli-migration-tool.md) — Turn a Patchouli book into Markdown so hopper-xtreme and minekea can move over without rewriting their docs.
- [Validate books in the build](authoring/build-time-validation.md) — Run the book validator from Gradle and CI so broken links and bad widgets fail the build.
- [Tabs, accordions and other containers](authoring/container-directives-and-tabs.md) — `:::tabs` / `:::details` containers for pages that outgrow callouts.
- [Book skins and themes](authoring/book-skins.md) — Colours and textures chosen per book from `book.yml`, restylable by resource packs.
- [Book item texture](authoring/book-item-texture.md) — An optional `texture` key in `book.yml` gives each book its own item icon.

### Reader

- [Multiblock previews](reader/multiblock-previews.md) — A rotatable `multiblock` widget that shows a small structure from a list of layers.
- [Reader accessibility](reader/accessibility.md) — Text scale, high-contrast theme, keyboard navigation and narrator support (rule A3).
- [Tag pages and bookmarks](reader/tag-pages-and-bookmarks.md) — A generated Tags page, tag chips under titles, and per-book bookmarks.
- [Pages unlocked by advancements](reader/advancement-gating.md) — Wire `requires:` to the client's advancement progress (a stub today).
- [Images and texture icons](reader/images-and-textures.md) — Draw texture images in pages and `texture:` icons in the table of contents.
- [Search that finds recipes, items and mobs](reader/search-widgets-and-code.md) — Index widget contents (by display name, not just id) so "hopper" finds the page that crafts one.
- [Expand and collapse all chapters](reader/expand-collapse-all.md) — Sidebar controls to open or close every chapter at once, and maybe an accordion mode.
- [Deep nesting and long titles in the sidebar](reader/deep-nesting-and-long-titles.md) — What the sidebar does when a book nests deeper or has longer names than it fits: indent caps, wrapping, drill-down, validator warning.

### Integration

- [Recipe viewer integration (EMI/JEI/REI)](integration/recipe-viewer-integration.md) — Open recipe widgets in whichever recipe viewer is installed.
- [Open-in-guide from items](integration/open-in-guide-hooks.md) — A tooltip line and key that open the book page documenting the hovered item.
- [Player-usable give command](integration/player-give-command.md) — `/opus give [<book>]` for players without op, with a config option to make it ops-only.

## Inbox

- Troubleshooting section in the built-in Opus Guide (e.g. a malformed default id in `opus-guides.json`, books that show "not found"). Raised while refining [the give command](integration/player-give-command.md).

## Archive

- [Live mob previews](archive/live-mob-previews.md) — Shipped in 1.0.0-beta.0: the `entity` widget draws a live, rotating mob, all at one shared scale.
