import {esc} from '../util.ts';
import {displayName} from './names.ts';
import type {Ingredient, Recipe, RecipeFormatter, Slot} from './types.ts';

const ingredientName = (ingredient: Ingredient): string => {
    const names = ingredient.options.map(displayName);
    if (ingredient.isTag) return `Any ${names[0]}`;
    return names.length > 1 ? `Any of: ${names.join(', ')}` : names[0]!;
};

/** Preserve the recipe's grid and legend, using readable cells instead of ASCII art. */
export const htmlFormatter: RecipeFormatter = (recipe: Recipe): string => {
    const slots: (Slot | null)[] = recipe.kind === 'grid' ? recipe.slots
        : recipe.kind === 'smithing' ? [recipe.template, recipe.base, recipe.addition] : [recipe.input];
    const columns = recipe.kind === 'grid' ? recipe.width : recipe.kind === 'smithing' ? 3 : 1;
    const caption = recipe.kind === 'grid' ? `Crafting table${recipe.shaped ? '' : ' (shapeless)'}`
        : recipe.kind === 'smithing' ? 'Smithing table: template, base, addition'
            : recipe.kind === 'stonecutting' ? 'Stonecutter'
                : [recipe.station, recipe.seconds === null ? '' : `${recipe.seconds}s`, recipe.xp === null ? '' : `${recipe.xp} xp`].filter(Boolean).join(' · ');
    const result = `${displayName(recipe.result.id)}${recipe.result.count > 1 ? ` ×${recipe.result.count}` : ''}`;
    const legend = new Map(slots.filter((slot): slot is Slot => slot !== null).map(slot => [slot.letter, ingredientName(slot.ingredient)]));
    return `<figure class="recipe"><figcaption>${esc(caption)}</figcaption>
<div class="recipe-flow"><div class="recipe-grid" style="--columns:${columns}" role="img" aria-label="${esc(slots.map((slot, i) => `Slot ${i + 1}: ${slot ? ingredientName(slot.ingredient) : 'empty'}`).join('; '))}">
${slots.map(slot => `<span class="recipe-slot" aria-hidden="true">${slot ? esc(slot.letter) : ''}</span>`).join('')}</div>
<span class="recipe-arrow" aria-hidden="true">→</span><div class="recipe-result"><span>Makes</span><strong>${esc(result)}</strong></div></div>
<ul class="recipe-legend">${[...legend].map(([letter, name]) => `<li><b>${esc(letter)}</b> ${esc(name)}</li>`).join('')}</ul></figure>`;
};
