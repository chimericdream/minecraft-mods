/** "allhallowssteve:pumpkin_stencils/blank" -> "Blank"; "oak_planks" -> "Oak Planks". */
export const displayName = (id: string): string =>
    (id.split(':').pop()!.split('/').pop()!)
        .split('_')
        .map(w => w.charAt(0).toUpperCase() + w.slice(1))
        .join(' ');
