# Player-usable give command

> Source: user request, 2026-10-07 · Effort **M** · Value ★★
> Status: **Building** (implemented on `main`, uncommitted; ops-only test and dedicated-server check still to do)

## Description

A command that hands the running player a book, usable by anyone, with no op level required. Today the only way to get an Opus book is vanilla `/give @s opus:book[opus:book_id={book_id:"ns:id"}]`, which needs permission level 2 and a hand-typed component. On a server (or a pack with cheats off) players have no way to get the guide unless an admin gives it to them.

**Shape:** `/opus give [<book>]` gives the executing player one `opus:book` with `opus:book_id` set to `<book>`.

- `<book>` is a resource id (`namespace:path`), parsed with vanilla's identifier argument type, so a malformed id gets vanilla's own "Invalid ID" error. Omitting it gives the default guide, `opus:guide` unless the config changes it.
- The server does not know which books are installed (books are client-only resource-pack assets), so it checks only that the id is well formed. An id that matches no book gives a book that opens the reader's existing "not found" overlay.
- There is no tab-completion of book ids in this version.
- A new config file (a small JSON file, no Mod Menu screen) is named `opus-guides.json`, is read and enforced on the server only, and has two options: restrict the command to ops, and the default guide id (`opus:guide`). When set, the command stays registered but requires permission level 2, so admins keep a shortcut. By default the command is available to everyone (permission level 0).

**Out of scope:** giving books to other players (`/give` covers that for ops), any rule limiting how many copies a player can take, validating ids against installed books, tab-completion of book ids, and other `/opus` subcommands (the root is chosen so they have a home later).

**Dependencies:** A server-side command registration (Architectury `CommandRegistrationEvent`), and a config file (Opus has none yet, so this adds the loading code for both loaders).
**Verification:** A GameTest that runs the command as a non-op player and checks the stack in their inventory, and a second one with the ops-only option on checking a non-op player is refused. Manual step for `TEST_PLAN.md`: running the command on a dedicated server.

## Decisions

- 2026-10-07 — The command is available to all players (permission level 0), with a config option to restrict it.
- 2026-10-07 — The server accepts any well-formed book id and does no validation or tab-completion; a missing book falls through to the reader's "not found" overlay. A client-to-server id sync can be added later as its own idea.
- 2026-10-07 — The command is `/opus give [<book>]`, rooted at `/opus` so later subcommands have a home.
- 2026-10-07 — The config option makes the command ops-only (permission level 2); it does not unregister it.
- 2026-10-07 — The config is a plain JSON file named `opus-guides`, not YACL.
- 2026-10-07 — The config is read and enforced server-side only: the ops-only check is the command's permission requirement, so a client's own config or modified client can't bypass it.
- 2026-10-07 — Omitting `<book>` gives the default guide. Its id is configurable and defaults to `opus:guide`.
- 2026-10-07 — A malformed id gets vanilla's identifier-argument parse error, with no custom chat message.
- 2026-10-07 — The config lives at `config/opus-guides.json` (the server's `config/` folder on a dedicated server, the integrated server's in single player).
- 2026-10-07 — If the configured default id is malformed, log a warning and fall back to `opus:guide`.
- 2026-10-07 — The config is read on every command run, so edits apply without a restart. A missing or invalid file falls back to the defaults with a warning, never an error.

## Brainstorm variants

_None yet._

## Related

- [Open-in-guide from items](open-in-guide-hooks.md) — also a way into the books, from the other direction.
- [Book item texture](../authoring/book-item-texture.md) — the book a player receives can look different per book.
- [Look recipes up by id](../authoring/server-recipe-lookup.md) — a server-to-client sync that could be reused if book ids ever need to be known server-side.

## Open questions

_None._
