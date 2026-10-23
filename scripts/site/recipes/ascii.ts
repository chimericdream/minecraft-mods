import {esc} from '../util.ts';
import {displayName} from './names.ts';
import type {Ingredient, Recipe, RecipeFormatter, RecipeResult, Slot} from './types.ts';

type Block = string[];

const width = (b: Block) => Math.max(...b.map(l => [...l].length));
const pad = (s: string, n: number) => s + ' '.repeat(n - [...s].length);

/** Place blocks side by side, vertically centred, with `gap` text between them on the middle row. */
const hjoin = (blocks: Block[], gap: string): Block => {
    const height = Math.max(...blocks.map(b => b.length));
    const mid = Math.floor(height / 2);
    const gapW = [...gap].length;
    return Array.from({length: height}, (_, row) => blocks.map((b, i) => {
        const top = Math.floor((height - b.length) / 2);
        const line = pad(b[row - top] ?? '', width(b));
        return i === 0 ? line : (row === mid ? gap : ' '.repeat(gapW)) + line;
    }).join(''));
};

const grid = (cols: number, rows: number, cell: (i: number) => string): Block => {
    const bar = (l: string, m: string, r: string) => l + Array(cols).fill('───').join(m) + r;
    const out = [bar('┌', '┬', '┐')];
    for (let y = 0; y < rows; y++) {
        out.push('│' + Array.from({length: cols}, (_, x) => ` ${cell(y * cols + x)} `).join('│') + '│');
        out.push(y < rows - 1 ? bar('├', '┼', '┤') : bar('└', '┴', '┘'));
    }
    return out;
};

const box = (text: string): Block => [
    '┌' + '─'.repeat(text.length + 2) + '┐',
    `│ ${text} │`,
    '└' + '─'.repeat(text.length + 2) + '┘',
];

const resultText = (r: RecipeResult) => displayName(r.id) + (r.count > 1 ? ` ×${r.count}` : '');

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

const ARROW = '  ──▶  ';

const diagram = (r: Recipe): Block => {
    const out = box(resultText(r.result));
    switch (r.kind) {
        case 'grid': return hjoin([grid(r.width, r.height, i => r.slots[i]?.letter ?? ' '), out], ARROW);
        case 'cooking': case 'stonecutting': return hjoin([grid(1, 1, () => r.input.letter), out], ARROW);
        case 'smithing': return hjoin([grid(3, 1, i => [r.template, r.base, r.addition][i]!.letter), out], ARROW);
    }
};

export const asciiFormatter: RecipeFormatter = recipe => {
    const legend = new Map<string, string>();
    for (const s of slotsOf(recipe)) {
        legend.set(s.letter, legendText(s.ingredient));
    }
    return `<figure class="recipe"><figcaption>${esc(caption(recipe))}</figcaption>
<pre class="recipe-diagram" aria-hidden="true">${esc(diagram(recipe).join('\n'))}</pre>
<ul class="recipe-legend">${[...legend].map(([k, v]) => `<li><b>${esc(k)}</b> ${esc(v)}</li>`).join('')}</ul>
<p class="sr-only">Makes ${esc(resultText(recipe.result))}.</p></figure>`;
};
