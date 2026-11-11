import type {Ingredient, Recipe, RecipeResult, Slot} from './types.ts';

type Raw = Record<string, unknown>;

const COOKING_STATIONS: Record<string, string> = {
    smelting: 'Furnace',
    blasting: 'Blast Furnace',
    smoking: 'Smoker',
    campfire_cooking: 'Campfire',
};

const toIngredient = (value: unknown): Ingredient => {
    const list = (Array.isArray(value) ? value : [value]).map(String);
    return {options: list.map(s => s.replace(/^#/, '')), isTag: list.every(s => s.startsWith('#'))};
};

const toResult = (raw: Raw): RecipeResult => ({id: String(raw.result), count: Number(raw.count ?? 1)});

const ingredientKey = (i: Ingredient) => `${i.isTag}:${i.options.join('|')}`;

/** Hands out A, B, C... and reuses a letter when the same ingredient comes up again. */
const letterAssigner = () => {
    const seen = new Map<string, string>();
    return (ingredient: Ingredient): Slot => {
        const key = ingredientKey(ingredient);
        if (!seen.has(key)) {
            seen.set(key, String.fromCharCode(65 + seen.size));
        }
        return {letter: seen.get(key)!, ingredient};
    };
};

export const parseRecipe = (raw: Raw): Recipe => {
    const type = String(raw.type ?? (raw.pattern ? 'crafting_shaped' : 'crafting_shapeless')).replace(/^minecraft:/, '');
    const result = toResult(raw);
    const letter = letterAssigner();

    if (type === 'crafting_shaped') {
        const pattern = (raw.pattern as string[]).map(String);
        const key = raw.key as Record<string, unknown>;
        const width = Math.max(...pattern.map(r => r.length));
        const slots = pattern.flatMap(row => [...row.padEnd(width)].map((ch): Slot | null =>
            ch === ' ' ? null : {letter: ch, ingredient: toIngredient(key[ch])}));
        return {kind: 'grid', shaped: true, width, height: pattern.length, slots, result};
    }
    if (type === 'crafting_shapeless') {
        const slots = (raw.ingredients as unknown[]).map(i => letter(toIngredient(i)));
        const width = Math.min(3, slots.length);
        return {kind: 'grid', shaped: false, width, height: Math.ceil(slots.length / 3), slots, result};
    }
    if (type in COOKING_STATIONS) {
        const ticks = raw.cooking_time === undefined ? null : Number(raw.cooking_time);
        return {
            kind: 'cooking',
            station: COOKING_STATIONS[type]!,
            input: letter(toIngredient(raw.ingredient)),
            result,
            seconds: ticks === null ? null : ticks / 20,
            xp: raw.experience === undefined ? null : Number(raw.experience),
        };
    }
    if (type === 'smithing') {
        return {
            kind: 'smithing',
            template: letter(toIngredient(raw.template)),
            base: letter(toIngredient(raw.base)),
            addition: letter(toIngredient(raw.addition)),
            result,
        };
    }
    if (type === 'stonecutting') {
        return {kind: 'stonecutting', input: letter(toIngredient(raw.ingredient)), result};
    }
    throw new Error(`unsupported recipe type "${type}"`);
};
