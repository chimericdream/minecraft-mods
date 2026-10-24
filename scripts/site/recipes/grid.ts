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
        case 'grid': return r.shaped ? 'Crafting table' : 'Crafting table, any arrangement';
        case 'cooking': return [r.station, r.seconds === null ? '' : `${r.seconds} seconds`, r.xp === null ? '' : `${r.xp} xp`].filter(Boolean).join(' · ');
        case 'smithing': return 'Smithing table: template, base, addition';
        case 'stonecutting': return 'Stonecutter';
    }
};

const resultText = (r: RecipeResult) => displayName(r.id) + (r.count > 1 ? ` ×${r.count}` : '');

/** Recipes as inventory slots: the item's own texture when the site has it, otherwise its legend letter. */
export const gridFormatter = (texture: (id: string) => string | null): RecipeFormatter => recipe => {
    const slot = (s: Slot | null) => {
        if (!s) return '<span class="slot empty"></span>';
        const tex = !s.ingredient.isTag && s.ingredient.options.length === 1 ? texture(s.ingredient.options[0]!) : null;
        const label = legendText(s.ingredient);
        return tex
            ? `<span class="slot" title="${esc(label)}"><img src="${esc(tex)}" alt="${esc(label)}" width="16" height="16"></span>`
            : `<span class="slot" title="${esc(label)}">${esc(s.letter)}</span>`;
    };
    const grid = (w: number, slots: (Slot | null)[]) => `<span class="slots" style="--w:${w}">${slots.map(slot).join('')}</span>`;
    const inputs = recipe.kind === 'grid' ? grid(recipe.width, recipe.slots)
        : recipe.kind === 'smithing' ? grid(3, [recipe.template, recipe.base, recipe.addition])
            : grid(1, [recipe.input]);
    const out = texture(recipe.result.id);
    const count = recipe.result.count > 1 ? `<span class="n">${recipe.result.count}</span>` : '';
    const result = `<span class="slot out">${out ? `<img src="${esc(out)}" alt="" width="16" height="16">` : ''}${count}</span>`;

    const legend = new Map<string, string>();
    for (const s of slotsOf(recipe)) legend.set(s.letter, legendText(s.ingredient));
    return `<figure class="recipe" data-ui><div class="craft" aria-hidden="true">${inputs}<span class="arrow">→</span>${result}<span class="out-name">${esc(resultText(recipe.result))}</span></div>
<ul class="recipe-legend">${[...legend].map(([k, v]) => `<li><b>${esc(k)}</b> ${esc(v)}</li>`).join('')}</ul>
<figcaption>${esc(caption(recipe))}</figcaption>
<p class="sr-only">Makes ${esc(resultText(recipe.result))}.</p></figure>`;
};
