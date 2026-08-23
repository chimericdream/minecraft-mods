package com.chimericdream.lib.fabric.trims;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

/**
 * Wraps every item's loaded model with {@link TrimmedArmorItemModel.Unbaked}. This has to be
 * unconditional — filtering by the item's default {@code minecraft:equippable} component at this
 * point throws {@code NullPointerException: Components not bound yet} (see "Components not bound yet"
 * in {@code docs/MC-26.2-NOTES.md}: default data components aren't bound until a
 * {@code ReloadableServerResources} reload, which hasn't happened yet during the client's first
 * resource/model reload). {@link TrimmedArmorItemModel} itself no-ops for anything that isn't
 * trimmed armor, so the unconditional wrap is harmless — it just checks the live stack's own
 * components at render time instead, once they're actually bound.
 */
public class TrimmedArmorModelLoadingPlugin implements ModelLoadingPlugin {
    public static void register() {
        ModelLoadingPlugin.register(new TrimmedArmorModelLoadingPlugin());
    }

    @Override
    public void initialize(ModelLoadingPlugin.Context context) {
        context.modifyItemModelBeforeBake().register((original, ctx) -> new TrimmedArmorItemModel.Unbaked(original));
    }
}
