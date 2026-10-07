# Player-usable give command

> Source: user request, 2026-10-07 · Effort **M** · Value ★★
> Status: **Exploring**

## Description

A command that hands the running player a book, usable by anyone, with no op level required. Today the only way to get an Opus book is vanilla `/give @s opus:book[opus:book_id={book_id:"ns:id"}]`, which needs permission level 2 and a hand-typed component. On a server (or a pack with cheats off) players have no way to get the guide unless an admin gives it to them.

Proposed shape: `/opus give <book>` gives the executing player one `opus:book` with `opus:book_id` set, and tab-completes the ids of the books that are installed. A config option turns the command off for packs that want to control how books are obtained (loot, recipes, quests).

**Out of scope:** giving books to other players (`/give` covers that for ops), and any rule limiting how many copies a player can take.

**Dependencies:** A server-side command registration (Architectury `CommandRegistrationEvent`), a config file (Opus has none yet), and a way for the server to know which book ids exist (see open questions).
**Verification:** A GameTest that runs the command as a non-op player and checks the stack in their inventory; a second one with the config option off checking the command is absent. Manual step for `TEST_PLAN.md`: tab-completion on a dedicated server.

## Decisions

- 2026-10-07 — The command is available to all players (permission level 0), with a config option to disable it.

## Brainstorm variants

_None yet._

## Related

- [Open-in-guide from items](open-in-guide-hooks.md) — also a way into the books, from the other direction.
- [Book item texture](../authoring/book-item-texture.md) — the book a player receives can look different per book.

## Open questions

- Books live in resource-pack assets, which are client-only. How does the server know the ids for validation and tab-completion? Options: a small server-to-client-and-back sync, a server-side scan of the same assets, or accept any id and let the reader show its "not found" overlay. Needs a decision before this is Ready.
- Command name and shape: `/opus give <book>`, or a bare `/opusbook <book>`? Recommend the `/opus` root so later subcommands (reload, validate) have a home.
- Config format and location: a plain JSON/TOML file, or YACL with a Mod Menu screen like the other mods? Does the option disable the command entirely (not registered) or leave it for ops only? Recommend ops-only, so admins keep a shortcut.
- Does omitting `<book>` give the default Opus Guide? (Assumed yes.)
