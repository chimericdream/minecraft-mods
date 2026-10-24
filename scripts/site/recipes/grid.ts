import {esc} from '../util.ts';
import {displayName} from './names.ts';
import type {Ingredient, Recipe, RecipeFormatter, RecipeResult, Slot} from './types.ts';

const slotsOf = (r: Recipe): Slot[] => {
    switch (r.kind) {
        case 'grid': return r.slots.filter((s): s is Slot => s !== null);
        case 'cooking': case 'stonecutting': return [r.input];
        case 'smithing': return [r.template, r.base, r.addition];
    }
};

const legendText = (i: Ingredient) => {
    const names = i.options.map(displayName);
    if (i.isTag) return `Any ${names[0]}`;
    return names.length > 1 ? `Any of: ${names.join(', ')}` : names[0]!;
};

const caption = (r: Recipe): string => {
    switch (r.kind) {
        case 'grid': return r.shaped ? 'Crafting table' : 'Crafting table (shapeless)';
        case 'cooking': return [r.station, r.seconds === null ? '' : `${r.seconds}s`, r.xp === null ? '' : `${r.xp} xp`].filter(Boolean).join(' · ');
        case 'smithing': return 'Smithing table: template, base, addition';
        case 'stonecutting': return 'Stonecutter';
    }
};

/** The input slots as a CSS grid, each holding its legend letter; empty slots stay empty. */
const inputs = (r: Recipe): {cols: number; letters: string[]} => {
    switch (r.kind) {
        case 'grid': return {cols: r.width, letters: Array.from({length: r.width * r.height}, (_, i) => r.slots[i]?.letter ?? '')};
        case 'cooking': case 'stonecutting': return {cols: 1, letters: [r.input.letter]};
        case 'smithing': return {cols: 3, letters: [r.template.letter, r.base.letter, r.addition.letter]};
    }
};

const output = (r: RecipeResult) =>
    `<span class="craft-out">${esc(displayName(r.id))}${r.count > 1 ? ` <small>×${r.count}</small>` : ''}</span>`;

/** Draws a recipe as a crafting grid: lettered slots, an arrow, the result, and a legend naming each letter. */
export const gridFormatter: RecipeFormatter = recipe => {
    const legend = new Map<string, string>();
    for (const s of slotsOf(recipe)) {
        legend.set(s.letter, legendText(s.ingredient));
    }
    const {cols, letters} = inputs(recipe);
    const result = displayName(recipe.result.id) + (recipe.result.count > 1 ? ` ×${recipe.result.count}` : '');
    return `<figure class="recipe"><figcaption>${esc(caption(recipe))}</figcaption>
<div class="craft" aria-hidden="true"><div class="slots" style="--cols:${cols}">${letters.map(l => `<span>${esc(l)}</span>`).join('')}</div><span class="craft-arrow">→</span>${output(recipe.result)}</div>
<ul class="recipe-legend">${[...legend].map(([k, v]) => `<li><b>${esc(k)}</b> ${esc(v)}</li>`).join('')}</ul>
<p class="sr-only">Makes ${esc(result)}.</p></figure>`;
};
