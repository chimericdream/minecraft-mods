package com.chimericdream.shulkerstuff.neoforge.client.render.item;

import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.client.render.item.ShulkerBoxItemRendererLogic;
import net.minecraft.resources.Identifier;

/**
 * NeoForge dispatches "special" item model rendering through a real, moddable registry
 * ({@code RegisterSpecialModelRendererEvent}), keyed by id. This is the id this mod's dyed shulker box
 * special model is registered under. The plain shulker box item's model json must reference it; this
 * mod ships that override (NeoForge-only) at {@code assets/minecraft/items/shulker_box.json}:
 * <pre>
 * {
 *   "model": {
 *     "type": "minecraft:special",
 *     "base": "minecraft:item/shulker_box",
 *     "model": {
 *       "type": "shulkerstuff:dyed_shulker_box"
 *     }
 *   }
 * }
 * </pre>
 * The base is {@code item/shulker_box}, not {@code block/shulker_box}: only the former ships the
 * GUI/hand/ground display transforms (see {@code ShulkerBoxItemRendererLogic}).
 */
public class ShulkerBoxSpecialModel {
    public static final Identifier TYPE_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "dyed_shulker_box");

    public static final ShulkerBoxItemRendererLogic.Unbaked UNBAKED = new ShulkerBoxItemRendererLogic.Unbaked();
}
