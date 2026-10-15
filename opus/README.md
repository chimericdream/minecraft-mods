# Opus (Fabric/NeoForge)

![Version: 1.1.0](https://img.shields.io/badge/version-1.1.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: optional](https://img.shields.io/badge/server-optional-4caf50?style=flat-square)

_Opus is a library mod intended for mod and pack developers to create highly customizable in-game guidebooks using nothing but Markdown._

## Introduction

### Minecraft Versions

* 26.2: supported

Opus is a documentation mod. A book is just a folder of Markdown files in a resource pack, so there is no
Java and no JSON to write. Folders become chapters, files become pages, and an `index.md` introduces its
chapter.

### Current Features

* Table of contents sidebar with search, back history, and previous/next buttons
* CommonMark with tables, strikethrough, task lists, and callouts
* YAML frontmatter for titles, icons, ordering, tags, hidden pages, and advancement-gated pages
* Links between pages that are checked when the book loads
* Recipe, item, and mob widgets written as fenced blocks
* Pictures from resource-pack textures, as centred blocks, side-by-side rows, or inline icons, with captions and click-to-enlarge, plus texture chapter icons
* Translations that fall back to the book's default language
* Custom item looks, names, and tooltips for each book, plus a `/opus give` command to hand out books
* A validator that finds broken links and bad widgets before your players do
* A Book of Books that holds every Opus guide in a pack in a single inventory slot

### Book of Books

In a big pack every mod can ship its own guide. The Book of Books lets players carry them all in one slot.

* **Craft it:** a book and a chest, shapeless. The first time you craft one you get the hidden "Bookception" advancement, and holding one with 10 or more guides earns the hidden "Magnum Opus" challenge.
* **Add guides:** combine the Book of Books with Opus books in a crafting grid. Add as many as you like, duplicates included. Pack authors can allow other mods' guidebooks too by adding them to the `opus:book_of_books_storable` item tag.
* **Read one:** right-click the Book of Books and pick a guide from the menu. It becomes that real guide.
* **Switch back:** punch the air to turn the guide back into the Book of Books.
* **Remove a guide:** turn the Book of Books into the guide you want to remove, then sneak and press your drop key.

## Notes for Documentation

Books live in `assets/<namespace>/opus-books/<book>/`. The bundled **Opus Guide** (use the Opus Book item, `/opus give`, or bind
the "Open the Opus Guide" key in Controls) explains every feature and is itself written as an Opus book; its source is in
`common/src/main/resources/assets/opus/opus-books/guide`.

Check your books from the command line with `java -jar opus-<version>.jar <path>`. The path can be a book folder, a resource pack, or a mod's resources folder.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

The Book of Books is heavily inspired by the [Akashic Tome](https://www.curseforge.com/minecraft/mc-mods/akashic-tome) mod by Vazkii and the Violet Moon team, and its controls are modelled on theirs so that players who know the Akashic Tome feel at home. Akashic Tome's readme describes it like this: "The Akashic Tome is meant to be used in large modpacks where every mod adds its own documentation book. Instead of cluttering your inventory, you can hold them all in one slot and swap as needed." No code or assets from Akashic Tome are used.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)
