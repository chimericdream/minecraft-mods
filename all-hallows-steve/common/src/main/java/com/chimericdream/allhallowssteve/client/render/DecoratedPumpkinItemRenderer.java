package com.chimericdream.allhallowssteve.client.render;

import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CardinalLighting;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Draws a decorated pumpkin item's carved stencil overlays. The item's tinted cube is a separate,
 * plain {@code minecraft:model} layer in {@code items/decorated_pumpkin.json}'s composite — this
 * renderer only adds the translucent stencil decals on top, via {@link DecoratedPumpkinStencilRenderer},
 * using the component's own default ({@code NORTH}) orientation and flat {@link CardinalLighting#DEFAULT}
 * shading since an item in hand/inventory has no real placement or level to read either from.
 */
public class DecoratedPumpkinItemRenderer implements SpecialModelRenderer<PumpkinStencilsComponent> {
    @Override
    public void submit(@Nullable PumpkinStencilsComponent stencils, PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, boolean glint, int outlineColor) {
        if (stencils == null) {
            return;
        }

        DecoratedPumpkinStencilRenderer.submit(matrices, queue, Direction.NORTH, stencils, CardinalLighting.DEFAULT, light);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> vertices) {
    }

    @Override
    public @Nullable PumpkinStencilsComponent extractArgument(ItemStack stack) {
        PumpkinStencilsComponent stencils = stack.getOrDefault(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), PumpkinStencilsComponent.EMPTY);
        return stencils.isEmpty() ? null : stencils;
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<PumpkinStencilsComponent> {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public SpecialModelRenderer<PumpkinStencilsComponent> bake(BakingContext context) {
            return new DecoratedPumpkinItemRenderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }
    }
}
