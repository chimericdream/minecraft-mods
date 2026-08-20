package com.chimericdream.camelnostrils.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jspecify.annotations.NonNull;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class UpsideDownChestItemRenderer implements NoDataSpecialModelRenderer {
    private final ChestSpecialRenderer delegate;

    public UpsideDownChestItemRenderer(ChestSpecialRenderer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();

        // Same flip as UpsideDownChestRenderer, kept in sync so the held/inventory icon matches the
        // in-world look.
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(-0.5, -0.5, -0.5);

        this.delegate.submit(poseStack, submitNodeCollector, lightCoords, overlayCoords, hasFoil, outlineColor);

        poseStack.popPose();
    }

    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> output) {
        this.delegate.getExtents(output);
    }

    public record Unbaked(Identifier texture, float openness, ChestType chestType) implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
                    Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness),
                    ChestType.CODEC.optionalFieldOf("chest_type", ChestType.SINGLE).forGetter(Unbaked::chestType)
                )
                .apply(i, Unbaked::new)
        );

        public Unbaked(Identifier texture) {
            this(texture, 0.0F, ChestType.SINGLE);
        }

        @Override
        public @NonNull MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public UpsideDownChestItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new UpsideDownChestItemRenderer(new ChestSpecialRenderer.Unbaked(texture, openness, chestType).bake(context));
        }
    }
}
