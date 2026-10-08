### Unreleased changes

#### New Features

- New `/opus give [<book>]` command that gives you a book, with no operator permission needed. Leave out the book to get the Opus Guide. Server owners can restrict it to operators (and change the default book) in `config/opus-guides.json`.
- Books can have their own item look. Add `texture: yourpack:item/my_book` (a PNG under `textures/item/`) or `model: yourpack:item/my_book` (a model file) to `book.yml`, and that book's item uses it everywhere, including in the book's own contents list.
- A book's item now shows the book's `title` as its name and its `description` in the tooltip, instead of just "Opus Book".
- Opening a book that can't be found now shows a small screen with an "Open troubleshooting" button, instead of a brief message on the action bar.
- A book item whose book can't be found now has its own icon (a grey book with a yellow !) instead of looking like a plain book.
- Books that don't set their own `texture` or `model` now use a maroon Opus book icon instead of the vanilla book.

#### Documentation

- The Opus Guide has a new Troubleshooting chapter. It explains what to check when a book says "not found" and what to do when `opus-guides.json` seems to be ignored.
- The "For pack authors" page of the Opus Guide now names the book item (`opus:book`) and shows how to give a player a specific book with `/give`.
- The Links page of the Opus Guide now explains that the number at the start of a file name is optional in links, and that other Markdown viewers need it.
- Fixed a few links in the Opus Guide that stopped working when the guide was read outside the game, such as on GitHub.

### 26.2 - 1.0.1

#### Bug Fixes

- Fixed the Opus Guide (and any other book) failing to open on NeoForge with "Book 'opus:guide' was not found".

### 26.2 - 1.0.0

Initial release.

#### New Features

- Opus lets mod and pack authors write in-game guidebooks as plain folders of Markdown files. There is no Java and no JSON: folders become chapters and files become pages.
- Readers get a table of contents sidebar, search, back history, and previous/next buttons.
- Pages support headings, lists, tables, task lists, callouts, and links between pages (links are checked when the book loads).
- Pages can have a title, icon, order, tags, and a hidden or advancement-locked setting, set with a short header at the top of the file.
- Recipe, item, and mob widgets can be dropped into any page. Mobs are shown live and slowly turn, all at the same scale so their sizes can be compared, and follow your GUI scale setting.
- Books can be translated, with missing pages falling back to the book's default language.
- Includes the Opus Book item and an "Open the Opus Guide" key (default F7). The Opus Guide is a book that explains how to write books.
- Includes a book checker: run `java -jar opus-<version>.jar <folder>` to list broken links, bad page headers, and invalid widgets in every book under a folder.
