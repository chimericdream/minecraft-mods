### Unreleased changes

#### New Features

- New `/opus give [<book>]` command that gives you a book, with no operator permission needed. Leave out the book to get the Opus Guide. Server owners can restrict it to operators (and change the default book) in `config/opus-guides.json`.
- Books can have their own item look. Add `texture: yourpack:item/my_book` (a PNG under `textures/item/`) or `model: yourpack:item/my_book` (a model file) to `book.yml`, and that book's item uses it everywhere, including in the book's own contents list.

#### Documentation

- The "For pack authors" page of the Opus Guide now names the book item (`opus:book`) and shows how to give a player a specific book with `/give`.

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
