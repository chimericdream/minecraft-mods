---
title: Frontmatter
tags: [basics, reference]
---
Every page may start with a block of [YAML](https://yaml.org) between two `---` lines.

`````markdown
---
title: Hoppers
icon: item:minecraft:hopper
order: 1
tags: [machines, logistics]
summary: Moves items between containers.
---
Your Markdown starts here.
`````

| Key | Meaning |
|:----|:--------|
| `title` | Page title. Defaults to a leading `# Heading`, then to the file name. |
| `icon` | `item:namespace:id`, or `texture:namespace:path`. Shown in the contents. Item icons work today; texture icons are planned. |
| `order` | Sort position among siblings; lower comes first. |
| `tags` | Lists the page on the book's tag pages and in search. |
| `summary` | One line shown under the title in listings and in search. |
| `hidden` | `true` keeps the page out of the contents; it is still linkable and searchable. |
| `requires` | Advancement ids that must all be completed before the page appears. **Not enforced yet:** the pages currently always show. |
| `aliases` | Extra names the page can be linked by. |
| `since` | The version that introduced the feature. Recorded for future use; not shown yet. |

Unknown keys produce a warning so typos are caught. Prefix your own keys with `x-` to silence it.

## Examples

A hidden draft that is still linkable:

`````markdown
---
title: Work in progress
hidden: true
---
`````

A renamed page that keeps its old links working:

`````markdown
---
title: Item sorters
aliases: [sorters, old-sorter-page]
---
`````

A page unlocked by an advancement (once enforcement lands):

`````markdown
---
title: The End
requires: [minecraft:story/enter_the_end]
---
`````

> [!NOTE]
> Quote values that look like numbers or dates, for example `since: "1.20"`, or YAML will rewrite them.
