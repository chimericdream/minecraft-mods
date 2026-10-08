---
title: Troubleshooting
icon: item:minecraft:barrier
summary: When a book won't open or the config is ignored.
---
Something not working? Find the symptom, check the likely causes in order, and apply the fix.

* [A book says "not found"](01-book-not-found.md) - the pack isn't on, or the id doesn't match
* [Problems with opus-guides.json](02-opus-guides-json.md) - the `/opus give` settings are ignored or reset

The first thing to try for any book problem is the [validator](../03-for-pack-authors/index.md#checking-your-book). It
loads your book the way the game does and points at the file and line that is wrong.

Still stuck? The game log (`logs/latest.log`) holds a line for every problem Opus finds. Search it for `opus`.
