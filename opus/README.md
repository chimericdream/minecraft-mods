# Opus (Fabric/NeoForge)

![Version: 1.0.0-beta.0](https://img.shields.io/badge/version-1.0.0--beta.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: optional](https://img.shields.io/badge/server-optional-4caf50?style=flat-square)

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
* Translations that fall back to the book's default language
* A validator that finds broken links and bad widgets before your players do

## Notes for Documentation

Books live in `assets/<namespace>/opus-books/<book>/`. The bundled **Opus Guide** (press F7, or use the Opus Book
item) explains every feature and is itself written as an Opus book; its source is in
`common/src/main/resources/assets/opus/opus-books/guide`.

Check your books from the command line with `java -jar opus-<version>.jar <path>`. The path can be a book folder, a resource pack, or a mod's resources folder.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)
