package com.chimericdream.shulkerstuff.neoforge.client.render.item;

import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.client.render.item.ShulkerBoxItemRendererLogic;
import net.minecraft.resources.Identifier;

/**
 * NeoForge dispatches "special" item model rendering through a real, moddable registry
 * ({@code RegisterSpecialModelRendererEvent}), keyed by id. This is the id this mod's dyed shulker box
 * special model is registered under; the item model json for the plain shulker box item needs to
 * reference it, e.g.:
 * <pre>
 * {
 *   "model": {
 *     "type": "minecraft:special",
 *     "base": "minecraft:block/shulker_box",
 *     "model": {
 *       "type": "shulkerstuff:dyed_shulker_box"
 *     }
 *   }
 * }
 * </pre>
 */
public class ShulkerBoxSpecialModel {
    public static final Identifier TYPE_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "dyed_shulker_box");

    public static final ShulkerBoxItemRendererLogic.Unbaked UNBAKED = new ShulkerBoxItemRendererLogic.Unbaked();
}
