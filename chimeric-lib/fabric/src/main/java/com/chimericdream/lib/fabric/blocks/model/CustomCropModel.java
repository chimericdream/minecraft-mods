package com.chimericdream.lib.fabric.blocks.model;

import com.chimericdream.lib.blocks.BlockConfig;
import java.util.Optional;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;

/**
 * The render-type-aware model vanilla's own crop template needs (cutout), for use with
 * {@link ModelUtils#registerCrop}.
 */
public class CustomCropModel extends CustomBlockModel {
    public CustomCropModel() {
        super(BlockConfig.RenderType.CUTOUT, Optional.of(Identifier.withDefaultNamespace("block/crop")), Optional.empty(), TextureSlot.CROP);
    }
}
