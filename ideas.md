#### Mod Template

* Add commented-out code to make it easier to implement data generation

#### Archaeology Tweaks

* Craftable pottery sherds
    * Mechanic similar to how smithing templates are copied
    * Special workstation?
* Pottery sherds for each special banner pattern

#### Artificial Heart (planned)

* Use shears on a creaking heart block to lock its visual state (active/inactive)
* Locked creaking hearts will not spawn creaking mobs, even if placed between pale oak logs
* Purely decorative, keeping the texture they had when they were sheared
* Same thing, but for the eyeblossoms
* "Creaking eye"
    * New placeable block/item/thing that emits a redstone signal when you look at it
    * Maybe strength depends on your distance

#### Athenaeum

* More books!
* Add books to loot tables for other structures, including modded structures
* Additional metadata for books, such as genre
* Custom blocks/items
* New villager type
* Book catalog
* Advancements
* Additional configuration options to control which books can be generated
* GUI improvements for creating books
    * Inspiration: https://www.curseforge.com/minecraft/mc-mods/stendhal
    * I like the premise of this mod, but think it could be improved in a few ways
    * Move writeable book GUI to center of screen

#### Banner Tweaks

* Generate banners for different structures
    * Can't be totally random, or they'll look like garbage
* Banner shape variants
    * Jagged bottoms
    * Sharp
* Allow placing on the underside of blocks
* Allow placing horizontally

#### Beacon & Conduit Tweaks

* Variable beacon bonuses depending on payment
* Additional beacon effects
* "Inverted" beacons (negative effects)
* Redirect beacon beams
* Hide beacon beams

#### Blacklight (planned)

* An alternative glowstone block which emits ultraviolet light, blocking mob spawns without actually lighting up an area
* The ability to imbue blocks with UV dust (or similar item) so they emit UV light like the block above
* A wearable item to allow the player to view blocks which are imbued with UV light

#### ChimericLib

* Add more helpers for Fabric datagen
* Add carpets to `c:shears_mineable` tag

#### Cobblicious (planned)

* More block variants
    * Mossy
    * Cracked
    * Chiseled
    * Pillar
    * Smooth

#### Enchantment Numbers Fix

* Rename
* Add some new enchantments
* Override which items can be enchanted with which enchantments
* Allow previously incompatible enchantments to be combined
    * Some combinations may be still be disabled by default
        * Silk Touch and Fortune
        * Riptide and Loyalty
        * Riptide and Channeling
* Allow non-solid blocks to be placed between bookshelves and the enchanting table
    * Inspiration: https://www.curseforge.com/minecraft/mc-mods/enchanter-fix
* Configurable sorting of enchantments
    * Alphabetical
    * Vanilla order
    * Custom order
    * Grouped by type
* Configurable colors/icons for enchantment tooltips

#### Flat Bedrock

*

#### Hang from Slabs (planned)

* Allow lanterns to be hung from under a top slab
* Allow hanging signs under top slabs

#### Hopper X-Treme

* Filtered hoppers
* Pipes/ducts

#### Houdini Block

*

#### JD Crafte (planned)

* Feeding trough
    * Feeds animals in a given radius
* Irrigation minecart
    * Speeds up crop growth
* Fancy farmland
    * Faster growing
    * Self-hydrating
    * Can't be trampled
* Rustic furniture
* Farm-themed decorations
* Something you can hook up to a horse to transport stuff (cart of some kind)
* Specialized minecarts for farming (e.g. "combine" for harvesting)
* More crops?
* World gen?
* More animals?
* Villager profession(s)?

#### Minekea

* Iron shelves
* Hammer rework
    * Remove trowel functionality
    * Right-clicking a block with the hammer should instead convert it to the cracked/cobbled variant, if one exists
    * Break 3x3 area instead of a single block
* New item to replace the hammer's trowel functionality

#### Miniblock Merchants

* Workstations?
* Profession-specific structures for each merchant type
* Built-in support for a variety of mods which add new structures (e.g. YUNG's Better...)
* Custom advancements
* More miniblocks
* More professions

#### Pannotia Companion (planned)

*

#### Playgrounds (planned)

*

#### Shulker Stuff

* [x] Make shulkers behave more like bundles
    * [x] Insert items by right-clicking them with the shulker in hand
    * [x] Extract items by right-clicking an empty inventory slot with the shulker in hand
    * [x] Extract items by right-clicking the shulker with an empty hand
    * [x] Insert items by right-clicking the shulker with an ItemStack
    * [x] Throw individual stacks by shift-right-clicking the air when not in the inventory
* [x] Arbitrarily dyed shulkers
    * [x] Render the shulker in the inventory with the correct color
    * [x] Render the shulker in the world with the correct color
    * [x] Use correct particle colors
    * [x] Separate colors for top and bottom
    * [x] Add new workstation to facilitate dyeing
* [x] Upgrades (smithing templates)
    * [x] Hardened: can't be blown up by creepers or other explosions
    * [x] Hardened smithing template
        * [x] Found in end cities
        * [x] Found in ancient cities
    * [x] Plated: item entity form won't be destroyed by fire, lava, or explosions
    * [x] Plated smithing template
        * [x] Found in treasure bastions
* [x] Enchantments
    * [x] Vacuum (2 levels)
        * [x] Vacuum I: suck up items that match a non-full stack in the shulker inventory
        * [x] Vacuum II: suck up all items as long as there is space
    * [x] Void: behaves like Vacuum I, but will continue picking up matching items after the stack is full. Any
      items picked up after this point are deleted
    * [x] Refill: when you use the last block/item in a stack, if a matching stack is in the shulker, it will refill
      your hand
    * [ ] Deep Storage (3 levels): extra rows of storage
        * 1 row per level
* [ ] Apply banners to a shulker
* [ ] Display nameplates above named shulker boxes

#### Sponj

*

#### Villager Tweaks

* [ ] Item (shackles?) that can be dispensed onto a villager to prevent them from moving
* [ ] Re-add stacked curing discounts

