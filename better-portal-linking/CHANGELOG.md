### Unreleased changes

#### New Features

* Added the Better Portal Linking Guide, an in-game book covering portal addresses and how links are chosen. Craft one from a book and a block of obsidian, or use `/opus give betterportallinking:guide`.
* Opus 1.2.0 or newer is now required (it provides the guide book).

#### Bug Fixes

* The config screen is now available on NeoForge too (Mods → select Better Portal Linking → Config); previously it only worked on Fabric via Mod Menu.

### 26.2 - 1.0.0

#### New Features

* Added portal addressing: place matching blocks (concrete, terracotta, or glazed terracotta by
  default) on a portal's four diagonal corners to link it to another portal with the same corner
  blocks, instead of always linking to the nearest one. Portals without corner blocks work exactly
  like vanilla.
* Added a config screen (via Mod Menu / YACL) with a master on/off switch and a debug logging toggle
  for portal linking decisions.
