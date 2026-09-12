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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Draws a decorated pumpkin item's carved stencil overlays. The item's tinted cube is a separate,
 * plain {@code minecraft:model} layer in {@code items/decorated_pumpkin.json}'s composite — this
 * renderer only adds the translucent stencil decals on top, via {@link DecoratedPumpkinStencilRenderer},
 * using the component's own default ({@code NORTH}) orientation and flat {@link CardinalLighting#DEFAULT}
 * shading since an item in hand/inventory has no real placement or level to read either from.
 */
public class DecoratedPumpkinItemRenderer implements SpecialModelRenderer<PumpkinStencilsComponent> {
    private final String overlaySuffix;

    public DecoratedPumpkinItemRenderer(String overlaySuffix) {
        this.overlaySuffix = overlaySuffix;
    }

    @Override
    public void submit(@Nullable PumpkinStencilsComponent stencils, PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, boolean glint, int outlineColor) {
        if (stencils == null) {
            return;
        }

        DecoratedPumpkinStencilRenderer.submit(matrices, queue, Direction.NORTH, stencils, CardinalLighting.DEFAULT, light, overlaySuffix);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> vertices) {
    }

    @Override
    public @Nullable PumpkinStencilsComponent extractArgument(ItemStack stack) {
        PumpkinStencilsComponent stencils = stack.getOrDefault(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), PumpkinStencilsComponent.EMPTY);
        return stencils.isEmpty() ? null : stencils;
    }

    /**
     * @param overlaySuffix {@code ""} for the plain unlit decorated pumpkin, or a
     *                      {@code LitDecoratedPumpkinBlock}'s overlay suffix for a lit variant's item
     *                      model — see {@code DecoratedPumpkinStencilRenderer}.
     *                      <p>
     *                      {@code SpecialModelRenderers.ID_MAPPER} is a bidirectional map: each
     *                      distinct type identifier it's registered under (one per decorated pumpkin
     *                      block, in {@code AllHallowsSteveClient}) needs its own distinct
     *                      {@link MapCodec} instance — reusing one shared codec object across multiple
     *                      identifiers throws {@code IllegalArgumentException: value already present}.
     *                      Rather than require every call site to reach for the exact same instance,
     *                      {@link #type()} resolves through this fixed, one-per-known-suffix table, so
     *                      any two {@code Unbaked} instances built from the same suffix — whether from
     *                      block datagen or client registration — always agree on which codec identifies
     *                      that suffix.
     */
    public record Unbaked(String overlaySuffix) implements SpecialModelRenderer.Unbaked<PumpkinStencilsComponent> {
        private static final List<String> KNOWN_OVERLAY_SUFFIXES = List.of("", "_lit", "_lit_blue", "_lit_green", "_lit_red");
        private static final Map<String, MapCodec<Unbaked>> CODECS_BY_SUFFIX = KNOWN_OVERLAY_SUFFIXES.stream()
            .collect(Collectors.toMap(suffix -> suffix, suffix -> MapCodec.unit(new Unbaked(suffix))));

        @Override
        public SpecialModelRenderer<PumpkinStencilsComponent> bake(BakingContext context) {
            return new DecoratedPumpkinItemRenderer(overlaySuffix);
        }

        @Override
        public MapCodec<Unbaked> type() {
            MapCodec<Unbaked> codec = CODECS_BY_SUFFIX.get(overlaySuffix);
            if (codec == null) {
                throw new IllegalStateException("No DecoratedPumpkinItemRenderer.Unbaked codec registered for overlay suffix '" + overlaySuffix + "'");
            }

            return codec;
        }
    }
}
