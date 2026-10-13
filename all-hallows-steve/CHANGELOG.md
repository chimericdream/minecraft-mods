### Unreleased changes

### 26.2 - 1.1.0

#### New Features

* Added the All Hallows Steve Guide, an in-game book with screenshots covering pumpkins, cider, spices, and harvest foods. Craft one from a book and a pumpkin, or use `/opus give allhallowssteve:guide`.
* Opus 1.2.0 or newer is now required (it provides the guide book).
* Added harvest foods:
  * Soul cakes: craft wheat, sugar, and an egg for 2 cakes. Adding one spice makes a cake with a short effect, 30 seconds: cloves give Absorption, cinnamon gives Luck, and nutmeg gives Night Vision. Adding mulling spices makes a spiced soul cake with all three.
  * Pumpkin soup: a pumpkin, a bowl, and a carrot or potato.
  * Roasted pumpkin seeds: cook pumpkin seeds in a furnace, smoker, or campfire.
  * Apple pie: apple, sugar, and apple over three wheat.
  * Spiced pumpkin pie: a pumpkin pie and mulling spices.
* Added eleven advancements for the cider press, spices, juices, and mulled drinks, in the same tab as the pumpkin ones. Drinking every kind of mulled drink earns the secret *Season's Drinkings* and some experience.
* Added the Cider Press. Craft one from an iron ingot, two sticks, a barrel, and three planks.
  * Put in 3 apples and a glass bottle, plus charcoal, coal, or blaze powder for fuel, and after a short press you get a bottle of apple cider and some pomace. Hoppers can feed it and empty it.
  * Apple cider is a drink that restores hunger and gives back the empty bottle.
  * The press also takes 3 sweet berries or 3 glow berries and turns them into sweet berry juice or glow berry juice, with pomace as a byproduct too. The juices drink just like apple cider.
  * Pomace can be composted, used as fertilizer, and fed to pigs to breed them. Feeding it to a hurt pig, cow, sheep, or horse heals them a little, with green sparkles.
* Added three spices: cinnamon, cloves, and nutmeg.
  * Cinnamon sometimes drops when you strip a cherry, mangrove, or jungle log with an axe.
  * Smelt a small flower (dandelion, poppy, azure bluet, cornflower, oxeye daisy, and more) in a furnace or smoker to get cloves.
  * Smelt cocoa beans in a furnace or smoker to get nutmeg. Journeyman farmers also sell it: 1 emerald for 2 nutmeg.
* Added mulling spices. Craft one cinnamon, one cloves, and one nutmeg together.
* Added mulled sweet berry juice and mulled glow berry juice. Sweet berry juice and glow berry juice go into a cauldron and mull the same way as apple cider (3 bottles, mulling spices, a lit campfire, about 30 seconds). Different juices can't be mixed in one cauldron.
* Added mulled apple cider. Pour 3 bottles of apple cider into a cauldron, right-click it with mulling spices, and put it over a lit campfire or soul campfire. It only starts simmering once the spices are in. After about 30 seconds it becomes mulled apple cider. Use empty glass bottles to ladle it out.
  * Each serving restores 8 hunger. If the fire goes out, the simmer starts over but the spices stay.

### 26.2 - 1.0.1

#### Bug Fixes

* Fixed the overlay textures for candlelit pumpkins


### 26.2 - 1.0.0

#### New Features

* Added the Pumpkin Carving Station. Craft one from an iron ingot, shears, a crafting table, and some planks.
* The station can dye pumpkins: place a pumpkin and up to 3 dyes in it to get a colored pumpkin back.
* Already-dyed pumpkins can go back in the station to be re-dyed — new dyes mix with the existing color instead of replacing it.
* Added Carving Stencils:
  * Craft a Blank Stencil from paper and a pumpkin seed.
  * Craft a themed stencil from a Blank Stencil plus its matching item: Crafter (crafting table), Creaking (resin clump), Creaking Heart (creaking heart), Creeper (creeper head), Dispenser (bow), Dropper (cobblestone), Jack o'Lantern (jack o'lantern), Lodestone (iron ingot), Observer (redstone), or Wither Rose (wither rose).
  * Heart, Jigsaw, Spawner, and Structure Block stencils can't be crafted — find them as rare loot in village houses, strongholds, woodland mansions, dungeons, ancient cities, and end cities.
* The station can now carve pumpkins: place up to four non-blank stencils around the pumpkin to carve them into its four sides. Stencils aren't used up, so the same one can be reused on the next pumpkin.
* A carved pumpkin in the station's output slot slowly spins in place so you can see every carved side before taking it out.
* Added lit decorated pumpkins: craft a decorated pumpkin with a torch to light it up, keeping its color and carved stencils.
  * Torch — light level 15
  * Soul Torch — light level 10, Blue
  * Copper Torch — light level 14, Green
  * Redstone Torch — light level 7, Red
  * Lit pumpkins can still be re-dyed and re-carved in the Pumpkin Carving Station.
  * Shear a lit pumpkin to remove the torch and turn it back into a regular decorated pumpkin.
  * Dispensers can do both: a dispenser with a torch lights the decorated pumpkin in front of it, and one with shears removes the torch. A dispenser won't put a torch into a pumpkin that's already lit.
* Added candle-lit decorated pumpkins: sneak and use a candle on a decorated pumpkin to light it, then add up to four.
  * Each candle adds light, the same as vanilla candles: level 3, 6, 9, and 12. Candle color doesn't change the glow, and any candle works.
  * Use an empty hand to snuff the flame and flint and steel to light it again. The candles stay inside while it's snuffed.
  * Shear the pumpkin to take all the candles back out, each in its original color. Breaking the pumpkin returns them too.
  * Dispensers can add candles, relight with flint and steel, and shear the candles out. A dispenser won't add a fifth candle.
  * A pumpkin holds either candles or a torch, not both.
* Sneak with an empty hand on a decorated pumpkin to see what's inside it: nothing, a torch, or candles (and whether they're lit). The same message appears whenever you add, light, snuff, or take out its contents.
* Decorated pumpkins can be worn on your head, like a carved pumpkin (put one in the helmet slot; right-clicking still places it). Lit pumpkins can't be worn.
  * Other players and armor stands see the dyed, carved pumpkin on your head, and endermen won't be angered when you look at them.
  * In first person you see through the carved openings of your pumpkin's stencil. A pumpkin with no carving on the face you're looking through blocks your view completely.
  * Pick which of the four faces points forward with the new "Turn Worn Pumpkin" key (unbound by default): north, east, south, then west. Your choice is saved, and other players see the pumpkin turn.
  * A new option in the config screen (also reachable from Mod Menu) lowers how much the worn pumpkin blocks your view.
* Added advancements for the Pumpkin Carving Station, dyeing and carving, wearing decorated pumpkins, lighting them with something other than a regular torch, and collecting stencils. Two are secret, and collecting every stencil awards experience.
* Vanilla pumpkins work the same way: sneak and use a torch on a carved pumpkin to turn it into a jack o'lantern, or use shears on a jack o'lantern to take the torch back out. Dispensers can do both too. Only regular torches work here, since vanilla only has the one jack o'lantern.
