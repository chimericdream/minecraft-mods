package com.chimericdream.shulkerstuff.client.render.item;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Set;

/**
 * As of 1.21.10, item-form rendering of block entities (shulker boxes, chests, etc.) no longer goes
 * through a {@code BuiltinItemRendererRegistry}/{@code BlockEntityWithoutLevelRenderer}-style hook.
 * Instead, an item's baked {@link ItemModel} can be a {@link SpecialModelWrapper} wrapping a
 * {@link SpecialModelRenderer}, which is submitted to a {@link SubmitNodeCollector} instead of being
 * drawn immediately with a {@code VertexConsumer}.
 * <p>
 * Vanilla's own {@code ShulkerBoxSpecialRenderer} can't be reused here: it always renders with a fixed,
 * un-tinted material baked per (vanilla) dye color, whereas Shulker Stuff needs to tint the lid and base
 * independently with arbitrary custom RGB colors read from {@link ShulkerStuffDyedColorComponent}. So this
 * renders the vanilla shulker box model part-by-part (mirroring what {@code ShulkerBoxRenderer} does
 * internally) so each part can get its own tint via {@link SubmitNodeCollector#submitModelPart}.
 */
public class ShulkerBoxItemRendererLogic {
    private static final ResourceLocation BASE_MODEL_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "block/shulker_box");

    /**
     * Builds the baked {@link ItemModel} to use for the plain shulker box item, wiring our custom
     * {@link Renderer} up through {@link SpecialModelWrapper} exactly like vanilla does for its own
     * special-rendered items.
     */
    public static ItemModel createItemModel(ItemModel.BakingContext context) {
        SpecialModelRenderer.BakingContext specialContext = new SpecialModelRenderer.BakingContext.Simple(
            context.entityModelSet(),
            context.materials(),
            context.playerSkinRenderCache()
        );
        Renderer renderer = new Unbaked().bake(specialContext);

        ResolvedModel resolvedModel = context.blockModelBaker().getModel(BASE_MODEL_ID);
        ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(
            context.blockModelBaker(),
            resolvedModel,
            resolvedModel.getTopTextureSlots()
        );

        return new SpecialModelWrapper<>(renderer, properties);
    }

    public record RenderData(int lidColor, int baseColor) {
        private static final RenderData DEFAULT = new RenderData(9922455, 9922455);
    }

    public static class Renderer implements SpecialModelRenderer<RenderData> {
        private final ModelPart root;
        private final ModelPart lid;
        private final RenderType renderType;
        private final TextureAtlasSprite sprite;

        public Renderer(ModelPart root, RenderType renderType, TextureAtlasSprite sprite) {
            this.root = root;
            this.lid = root.getChild("lid");
            this.renderType = renderType;
            this.sprite = sprite;
        }

        @Override
        public void submit(
            RenderData data,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            int overlay,
            boolean hasFoil,
            int outlineColor
        ) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(0.9995F, 0.9995F, 0.9995F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            poseStack.translate(0.0F, -1.0F, 0.0F);

            // Item display always shows the box closed.
            lid.setPos(0.0F, 24.0F, 0.0F);
            lid.yRot = 0.0F;

            lid.visible = false;
            collector.submitModelPart(root, poseStack, renderType, light, overlay, sprite, false, false, data.baseColor(), null, outlineColor);
            lid.visible = true;
            collector.submitModelPart(lid, poseStack, renderType, light, overlay, sprite, false, false, data.lidColor(), null, outlineColor);

            poseStack.popPose();
        }

        @Override
        public RenderData extractArgument(ItemStack stack) {
            ShulkerStuffDyedColorComponent component = stack.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get());
            return component == null ? RenderData.DEFAULT : new RenderData(component.lidColor(), component.baseColor());
        }

        @Override
        public void getExtents(Set<Vector3f> extents) {
            PoseStack poseStack = new PoseStack();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(0.9995F, 0.9995F, 0.9995F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            poseStack.translate(0.0F, -1.0F, 0.0F);
            root.getExtentsForGui(poseStack, extents);
        }
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked {
        // Not deserialized from JSON in this mod's own wiring (Fabric replaces the baked ItemModel directly),
        // but NeoForge dispatches special models through this codec, so it needs to be a real one.
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public @NotNull MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public Renderer bake(SpecialModelRenderer.BakingContext context) {
            ModelPart root = context.entityModelSet().bakeLayer(ModelLayers.SHULKER_BOX);
            TextureAtlasSprite sprite = context.materials().get(Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION);
            return new Renderer(root, Sheets.shulkerBoxSheet(), sprite);
        }
    }
}
