package com.chimericdream.opus.client.item;

import com.google.common.base.Suppliers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/** Pre-baked quads drawn as one layer; the vanilla {@code CuboidItemModelWrapper} constructor is private, so this mirrors its {@code update}. */
final class FlatItemModel implements ItemModel {
    private final QuadCollection quads;
    private final ModelRenderProperties properties;
    private final Matrix4fc transformation;
    private final Supplier<Vector3fc[]> extents;

    FlatItemModel(QuadCollection quads, ModelRenderProperties properties, Matrix4fc transformation) {
        this.quads = quads;
        this.properties = properties;
        this.transformation = transformation;
        this.extents = Suppliers.memoize(() -> CuboidItemModelWrapper.computeExtents(quads.getAll()));
    }

    @Override
    public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver, ItemDisplayContext displayContext,
                       @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        output.appendModelIdentityElement(this);
        ItemStackRenderState.LayerRenderState layer = output.newLayer();
        if (item.hasFoil()) {
            layer.setFoilType(ItemStackRenderState.FoilType.STANDARD);
            output.setAnimated();
            output.appendModelIdentityElement(ItemStackRenderState.FoilType.STANDARD);
        }

        layer.setExtents(extents);
        layer.setLocalTransform(transformation);
        properties.applyToLayer(layer, displayContext);
        layer.prepareQuadList().addAll(quads.getAll());
    }
}
