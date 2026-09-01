### Unreleased changes

#### New Features

* Added lava-logging for slabs, stairs, walls, fences, iron bars/glass panes, trapdoors, ladders, and
  chains made of a non-flammable material, mirroring how water-logging already works:
  * Right-click one with a lava bucket to fill it with lava; empty the bucket on it again to pick the
    lava back up. Sneak while using the bucket to place lava normally instead of logging it.
  * Placing one directly into a lava source logs it automatically too, instead of requiring a separate
    bucket right-click afterward.
* Added window-logging: right-click a slab or plain stairs (not the corner-shaped ones) with a glass
  pane (any color) or bars (iron or copper) to fit it into the empty part of the block, turning it into
  a window. Aim at the glass/bars while mining to pop just it back out; mining the solid part breaks
  the whole thing and drops both items. A real pane or bars placed next to one connects to it like a
  normal window, a wooden window burns like the wood it's made of, and a plain stairs block placed next
  to a window-logged one still forms the usual corner shape. A windowed slab's pane faces the direction
  you were looking when you placed it. Sneak while placing the pane/bars to place it normally instead
  of window-logging. Lava-logging and window-logging can be combined on the same block.
* Added carpet-logging: right-click a slab, plain stairs (not the corner-shaped ones), wall, fence,
  chain (iron or copper), bars (iron or copper), or glass pane with a carpet to lay it into the block
  instead of placing a separate carpet on top. Aim at the carpet while mining to pop just it back out;
  mining the solid part breaks the whole thing and drops both items. Sneak while placing the carpet to
  place it normally instead of carpet-logging. A carpet-logged wall, fence, or bars/pane still connects
  to real neighbors (and to other carpet-logged ones) exactly like the un-logged block would, in both
  directions - a wall placed next to a carpet-logged fence connects to it immediately, and a
  carpet-logged fence updates to connect toward a wall placed next to it afterward.
* Added snow-logging: right-click a slab, plain stairs (not the corner-shaped ones), wall, fence,
  chain, bars, or glass pane with snow to build it up into the block one layer at a time, instead of
  placing snow separately on top. Each right-click adds another layer, capped at whatever headroom the
  block actually has - up to one shy of a full snow block on an open host like a fence, or up to half
  that on a slab or stairs, since only half the block is open there. Aim at the snow while mining to
  pop it all back out at once (a shovel is required to get anything back, same as normal snow); mining
  the solid part breaks the whole thing. Sneak while placing snow to place it normally instead of
  snow-logging. Doesn't currently combine with carpet-logging on the same block. A snow-logged wall,
  fence, or bars/pane still connects to real neighbors (and to other snow-logged or carpet-logged ones)
  exactly like the un-logged block would, in both directions.

#### Bug Fixes

* Fixed window-logged and carpet-logged blocks rendering noticeably brighter/flatter than the same
  block placed normally - they were missing the soft darkening real blocks get near solid neighbors
  (most visible under overhangs like leaves), and their shape-fitted glass/carpet overlays on stairs
  and slabs were missing the darkening real blocks get on their side and bottom faces, and lighting
  those faces from the block's own light level instead of each face's actual neighbor.
* Fixed the mod failing to load entirely on NeoForge with a mixin error, caused by NeoForge changing
  a vanilla fire-spreading method's signature in a way the window-logging fire-flammability fix didn't
  account for.
* Fixed a window-logged slab's glass always rendering running east-west, even when it was actually
  facing north-south - the pane's hitbox was correct, but the visible glass ran the wrong way.
* Fixed a snow-logged wall, fence, bars, or pane flickering right where the snow met its host - the
  snow's own texture was drawn exactly flush with the host's, so the two fought over which one showed
  through.
