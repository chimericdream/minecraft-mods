---
title: Netherite Plating
icon: item:shulkerstuff:plated_shulker_upgrade_smithing_template
tags: [plating]
summary: Protect a shulker box from explosions and fire.
---
A plated shulker box can't be destroyed by explosions or fire. This protects it both when you carry it as an item and
when it is placed in the world. A plated box shows *Netherite Plated* in its tooltip.

## The template

The **Plated Shulker Upgrade** smithing template is found in **bastion treasure chests**.

```item
shulkerstuff:plated_shulker_upgrade_smithing_template
```

Once you have one, you can make more. Surround a template and a netherite ingot with diamonds to craft 2 templates.

```recipe
pattern: ["#S#", "#N#", "###"]
key:
  "#": diamond
  S: shulkerstuff:plated_shulker_upgrade_smithing_template
  N: netherite_ingot
result: shulkerstuff:plated_shulker_upgrade_smithing_template
count: 2
```

## Plating a box

Use a smithing table with the template, a shulker box, and a netherite ingot.

```recipe
type: smithing
template: shulkerstuff:plated_shulker_upgrade_smithing_template
base: shulker_box
addition: netherite_ingot
result: shulker_box
```

The template is used up each time.

## Config

The template's rarity in bastion chests can be changed on the Shulker Stuff config screen. The option is
**Plated Shulker Upgrade Chance (1 in x)**. The default is 12, and a bigger number makes it rarer.
