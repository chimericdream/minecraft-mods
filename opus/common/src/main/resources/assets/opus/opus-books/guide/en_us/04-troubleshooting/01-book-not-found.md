---
title: A book says "not found"
tags: [troubleshooting, books]
---
**What you see:** you right-click a book, press the guide key, or run `/opus give some:book`, and the message
**Book 'some:book' was not found** appears instead of the book.

The book id in the message is the one Opus looked for. Check it against each cause below, in this order.

## 1. The pack is not enabled

Books are part of a resource pack, and the pack has to be active on the player's game.

* Open **Options > Resource Packs** and move the pack to the **Selected** side.
* Mods that ship a book enable it automatically. If the book comes from a pack, each player enables it themselves,
  even on a server.
* After enabling a pack, wait for the reload to finish before opening the book.

## 2. The id doesn't match the folders

A book's id is `<namespace>:<book>`, and both parts are folder names:

```
assets/pannotia/opus-books/field-guide/book.yml   ->   pannotia:field-guide
```

* The folder must be named `opus-books`, with a hyphen, directly under `assets/<namespace>/`.
* Ids are lowercase. `Pannotia:Field-Guide` is not valid.
* The folder needs a `book.yml`. A folder without one is not a book.

## 3. The id was typed wrong

The server accepts any well-formed id, even one that matches no book, so `/opus give pannotia:filed-guide` hands
over a book that can't open.

* Compare the id in the message with the folder names letter by letter.
* In a `/give` command, the component is an object, not a bare string:
  `opus:book[opus:book_id={book_id:"pannotia:field-guide"}]`.

## 4. The book fails to load

If the pack is on and the id is right, the book may have a problem that stops it loading. Run the
[validator](../03-for-pack-authors/index.md#checking-your-book) on the pack and fix every error it lists, then reload with
<kbd>F3</kbd>+<kbd>T</kbd>.
