### Unreleased changes

#### New Features

* Added `/enchfix fixbooks`. It removes the repair cost from the enchanted books in your inventory, so books with the same enchantments stack again.
* Added a config for the maximum level of each enchantment, and an optional default maximum for the others. By default, each vanilla enchantment with more than one level has a maximum of X, or its hard cap if that is lower. Enchantments that stop giving a benefit at a level, such as Lure V, have a hard cap.
* Added the Random Enchantment Levels setting. It sets the maximum level of random enchantments from loot, villager trades, piglin bartering, the enchanting table, and mob equipment. Off uses the vanilla maximum. Balanced (the default) allows 2 levels above the vanilla maximum. Insane uses the maximum level from the config.
* Removed the anvil limit of 40 levels ("Too Expensive!"). You must still have the levels to pay the cost. A setting restores the limit.
* Added the Linear Upgrades setting for the anvil. The repair cost stops at 31 levels. Above the vanilla maximum level, each item adds one level. For example, Sharpness 40 and a Sharpness V book give Sharpness 41. The setting is off by default.
* Added the Remove Incompatibilities setting. When it is on, you can combine enchantments that work correctly together, for example all the protection enchantments on one piece of armor. Silk Touch and Fortune stay incompatible. Riptide stays incompatible with Loyalty and Channeling. The setting is off by default.
* Added the optional Advanced Silk Touch data pack. It adds the Advanced Silk Touch enchantment, with a maximum level of XXX. Its levels collect dirt paths and farmland (I), suspicious blocks with their loot (III), budding amethyst (V), infested blocks (VII), spawners with their mob (X), reinforced deepslate (XV), and bedrock (XXX). A master librarian has a 5% chance to sell a book at a level from X to XV. The pack is off by default.
* Added advancements for very high enchantment levels.
* Added the optional Scaling Enchantments data pack. With it, high levels of Protection, Lure, Quick Charge, Breach, Thorns, and Unbreaking continue to give more benefit, up to a limit.
* Added the Enchanting Fixes Guide, an in-game book that explains each feature and its settings. Craft one from a book and any enchanted book, or use `/opus give enchantingfixes:guide`. Install the optional Opus mod (1.2.0 or newer) to get it.
