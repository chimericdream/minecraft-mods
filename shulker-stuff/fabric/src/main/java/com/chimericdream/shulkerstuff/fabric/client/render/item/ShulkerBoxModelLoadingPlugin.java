package com.chimericdream.shulkerstuff.fabric.client.render.item;

import com.chimericdream.shulkerstuff.client.render.item.ShulkerBoxItemRendererLogic;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.resources.Identifier;

/**
 * Fabric API doesn't (yet) expose the {@code ExtraCodecs.LateBoundIdMapper} that backs vanilla's
 * "special" item model dispatch (NeoForge exposes it directly via {@code RegisterSpecialModelRendererEvent}),
 * so instead of registering a new special-model "type" id, this swaps the plain shulker box item's baked
 * model out for ours directly after it's baked.
 */
public class ShulkerBoxModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void initialize(Context pluginContext) {
        pluginContext.modifyItemModelAfterBake().register(ModelModifier.OVERRIDE_PHASE, (model, context) -> {
            if (isShulkerBoxItem(context.itemId())) {
                return ShulkerBoxItemRendererLogic.createItemModel(context.bakingContext());
            }

            return model;
        });
    }

    private static boolean isShulkerBoxItem(Identifier itemId) {
        return itemId.equals(Identifier.fromNamespaceAndPath("minecraft", "shulker_box"));
    }
}
