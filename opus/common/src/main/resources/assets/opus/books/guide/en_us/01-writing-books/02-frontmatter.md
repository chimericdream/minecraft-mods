---
title: Frontmatter
tags: [basics, reference]
---

Every page may start with a YAML block between two `---` lines.

````markdown
---
title: Hoppers
icon: item:minecraft:hopper
order: 1
tags: [machines, logistics]
summary: Moves items between containers.
---
````

| Key | Meaning |
|:----|:--------|
| `title` | Page title. Defaults to a leading `# Heading`, then to the file name. |
| `icon` | `item:namespace:id`, or `texture:namespace:path`. Shown in the table of contents. |
| `order` | Sort position among siblings; lower comes first. |
| `tags` | Lists the page on the book's tag pages and in search. |
| `summary` | One line shown under the title in listings and in search. |
| `hidden` | `true` keeps the page out of the table of contents; it is still linkable and searchable. |
| `requires` | Advancement ids that must be completed before the page appears. |
| `aliases` | Extra names the page can be linked by. |
| `since` | The version that introduced the feature, shown as a badge. |

Unknown keys produce a warning so typos are caught. Prefix your own keys with `x-` to silence it.

> [!NOTE]
> Quote values that look like numbers or dates, for example `since: "1.20"`, or YAML will rewrite them.
