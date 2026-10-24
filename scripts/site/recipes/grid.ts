import {esc} from '../util.ts';
import {caption, legendText, resultText, slotsOf} from './ascii.ts';
import type {Recipe, RecipeFormatter, Slot} from './types.ts';

const cell = (slot: Slot | null) => slot
    ? `<span class="rc" title="${esc(legendText(slot.ingredient))}">${esc(slot.letter)}</span>`
    : '<span class="rc empty"></span>';

const inputs = (r: Recipe): {cols: number; cells: (Slot | null)[]} => {
    switch (r.kind) {
        case 'grid': return {cols: r.width, cells: Array.from({length: r.width * r.height}, (_, i) => r.slots[i] ?? null)};
        case 'cooking': case 'stonecutting': return {cols: 1, cells: [r.input]};
        case 'smithing': return {cols: 3, cells: [r.template, r.base, r.addition]};
    }
};

/** Draws the recipe as a crafting grid of letter cells, an arrow and the result, with the letters explained below. */
export const gridFormatter: RecipeFormatter = recipe => {
    const legend = new Map<string, string>();
    for (const s of slotsOf(recipe)) {
        legend.set(s.letter, legendText(s.ingredient));
    }
    const {cols, cells} = inputs(recipe);
    return `<figure class="recipe"><figcaption>${esc(caption(recipe))}</figcaption>
<div class="recipe-row" aria-hidden="true"><div class="rgrid" style="--cols:${cols}">${cells.map(cell).join('')}</div><span class="rarrow"></span><span class="rresult">${esc(resultText(recipe.result))}</span></div>
<ul class="recipe-legend">${[...legend].map(([k, v]) => `<li><b>${esc(k)}</b> ${esc(v)}</li>`).join('')}</ul>
<p class="sr-only">Makes ${esc(resultText(recipe.result))}.</p></figure>`;
};
