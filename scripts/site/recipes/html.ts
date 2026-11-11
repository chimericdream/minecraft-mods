import {esc} from '../util.ts';
import {displayName} from './names.ts';
import type {Ingredient, Recipe, RecipeFormatter} from './types.ts';

const ingredients = (ingredient: Ingredient): string => {
    const names = ingredient.options.map(displayName);
    if (ingredient.isTag) return `Any ${names[0]}`;
    return names.length > 1 ? `Any of: ${names.join(', ')}` : names[0]!;
};

const station = (recipe: Recipe): string => {
    switch (recipe.kind) {
        case 'grid': return recipe.shaped ? 'Crafting table' : 'Crafting table (shapeless)';
        case 'cooking': return [recipe.station, recipe.seconds === null ? '' : `${recipe.seconds}s`, recipe.xp === null ? '' : `${recipe.xp} xp`].filter(Boolean).join(' · ');
        case 'smithing': return 'Smithing table (template, base, addition)';
        case 'stonecutting': return 'Stonecutter';
    }
};

export const htmlFormatter: RecipeFormatter = recipe => {
    const slots = recipe.kind === 'grid' ? recipe.slots
        : recipe.kind === 'smithing' ? [recipe.template, recipe.base, recipe.addition] : [recipe.input];
    const columns = recipe.kind === 'grid' ? recipe.width : slots.length;
    const legend = new Map<string, string>();
    const cells = slots.map((slot, index) => {
        const position = recipe.kind === 'grid' && recipe.shaped
            ? `Row ${Math.floor(index / columns) + 1}, column ${index % columns + 1}: `
            : recipe.kind === 'smithing' ? ['Template: ', 'Base: ', 'Addition: '][index] : '';
        if (slot) legend.set(slot.letter, ingredients(slot.ingredient));
        return `<li${slot ? '' : ' class="recipe-slot-empty"'}><span class="sr-only">${position}${esc(slot ? ingredients(slot.ingredient) : 'Empty')}</span><span aria-hidden="true">${esc(slot?.letter ?? '')}</span></li>`;
    }).join('');
    const result = displayName(recipe.result.id) + (recipe.result.count > 1 ? ` ×${recipe.result.count}` : '');
    return `<figure class="recipe"><figcaption>${esc(station(recipe))}</figcaption>
<div class="recipe-flow"><ol class="recipe-slots" role="list" aria-label="Ingredient slots" style="--recipe-columns:${columns}">${cells}</ol>
<span class="recipe-arrow" aria-hidden="true">&rarr;</span><div class="recipe-result"><span>Produces</span><strong>${esc(result)}</strong></div></div>
<ul class="recipe-legend" aria-label="Ingredient key">${[...legend].map(([letter, name]) => `<li><b>${esc(letter)}</b> ${esc(name)}</li>`).join('')}</ul></figure>`;
};
