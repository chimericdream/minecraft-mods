---
title: Problems with opus-guides.json
tags: [troubleshooting, config]
---
`config/opus-guides.json` holds the settings for `/opus give`. It lives on the **server**: in the server's `config`
folder on a dedicated server, and in your own `config` folder in single player. Clients never read it.

```
{
  "opsOnly": false,
  "defaultGuide": "opus:guide"
}
```

* `opsOnly` - `true` means only operators can run `/opus give`.
* `defaultGuide` - the book given when no book id is typed. Defaults to `opus:guide`.

If the file doesn't exist, Opus creates it with these defaults the first time the command is used.

## My changes don't do anything

* **You edited the wrong file.** On a dedicated server, the file on your own computer is ignored.
* **You haven't run the command again.** The file is checked every time `/opus give` runs, so there is no need to
  restart, but nothing happens until the next run.
* **You're an operator.** `opsOnly` stops other players. Operators can always run the command.

## `/opus give` gives me the wrong book

If `defaultGuide` is not a valid id, Opus ignores it and falls back to `opus:guide`. Valid ids are lowercase and look
like `namespace:path`. The log says what it did:

```
opus-guides.json: defaultGuide 'My Guide' is not a valid id; using opus:guide
```

Only the bad setting is ignored. `opsOnly` still applies.

An id that is valid but names no book is accepted as is. The book it gives can't open, so see
[A book says "not found"](01-book-not-found.md).

## The whole file is ignored

If the file can't be read as JSON, or a setting has the wrong type (for example `"opsOnly": "yes"` instead of
`true`), Opus uses the defaults for **both** settings and logs a warning:

```
Could not read opus-guides.json; using defaults (...)
```

Common causes are a missing comma or quote, a trailing comma, or a comment, which JSON doesn't allow. Paste the file
into any JSON checker, fix it, and save. The next `/opus give` picks it up.

To start over, delete the file. A fresh one with the defaults is written on the next use.
