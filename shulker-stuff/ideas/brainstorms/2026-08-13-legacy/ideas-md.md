# Legacy notes from the root `ideas.md`

Copied verbatim from this mod's section of the repo-root `ideas.md` when it was retired (see
[`docs/FEATURE-WORK.md`](../../../../docs/FEATURE-WORK.md)).

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
