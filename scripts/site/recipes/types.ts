/** One recipe slot: the item ids that satisfy it (more than one means "any of"). */
export type Ingredient = {options: string[]; isTag: boolean};

export type RecipeResult = {id: string; count: number};

/** A slot in a crafting grid or flow, tagged with the letter shown in the diagram and its legend. */
export type Slot = {letter: string; ingredient: Ingredient};

export type Recipe =
    | {kind: 'grid'; shaped: boolean; width: number; height: number; slots: (Slot | null)[]; result: RecipeResult}
    | {kind: 'cooking'; station: string; input: Slot; result: RecipeResult; seconds: number | null; xp: number | null}
    | {kind: 'smithing'; template: Slot; base: Slot; addition: Slot; result: RecipeResult}
    | {kind: 'stonecutting'; input: Slot; result: RecipeResult};

/**
 * Turns a parsed recipe into an HTML fragment. The ASCII formatter is the first implementation; a
 * formatter that renders real item textures can replace it without touching the guide renderer.
 */
export type RecipeFormatter = (recipe: Recipe) => string;
