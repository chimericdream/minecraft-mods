package com.chimericdream.shulkerstuff.client.render.item;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3fc;

import java.util.Set;
import java.util.function.Consumer;

/**
 * As of 1.21.10, item-form rendering of block entities (shulker boxes, chests, etc.) no longer goes
 * through a {@code BuiltinItemRendererRegistry}/{@code BlockEntityWithoutLevelRenderer}-style hook.
 * Instead, an item's baked {@link ItemModel} can be a {@link SpecialModelWrapper} wrapping a
 * {@link SpecialModelRenderer}, which is submitted to a {@link SubmitNodeCollector} instead of being
 * drawn immediately with a {@code VertexConsumer}.
 * <p>
 * Vanilla's own {@code ShulkerBoxSpecialRenderer} can't be reused directly here: it always renders with
 * a fixed, un-tinted material baked per (vanilla) dye color, whereas Shulker Stuff needs to tint the lid
 * and base independently with arbitrary custom RGB colors read from {@link ShulkerStuffDyedColorComponent}.
 * It DOES construct and wrap an actual {@link ShulkerBoxRenderer} though (via its
 * {@link SpecialModelRenderer.BakingContext} constructor, exactly like {@code ShulkerBoxSpecialRenderer}
 * does), reaching its "model"/"materials" fields (widened accessible in shulkerstuff.accesswidener) to
 * submit "base"/"lid" separately with their own tint via {@link SubmitNodeCollector#submitModelPart}.
 */
public class ShulkerBoxItemRendererLogic {
    // block/shulker_box has no "display" block at all (no parent, no per-context transforms), which is
    // why using it left the item rendering at full block scale everywhere, including invisible/wrongly
    // placed in the 2D GUI viewport. item/shulker_box (via item/template_shulker_box) is the one vanilla
    // actually ships GUI/hand/ground/fixed display transforms on.
    private static final Identifier BASE_MODEL_ID = Identifier.withDefaultNamespace("item/shulker_box");

    /**
     * Builds the baked {@link ItemModel} to use for the plain shulker box item, wiring our custom
     * {@link Renderer} up through {@link SpecialModelWrapper} exactly like vanilla does for its own
     * special-rendered items.
     */
    public static ItemModel createItemModel(ItemModel.BakingContext context) {
        Renderer renderer = new Unbaked().bake(context);

        ResolvedModel resolvedModel = context.blockModelBaker().getModel(BASE_MODEL_ID);
        ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(
            context.blockModelBaker(),
            resolvedModel,
            resolvedModel.getTopTextureSlots()
        );

        return new SpecialModelWrapper<>(renderer, properties, new Matrix4f());
    }

    public record RenderData(int lidColor, int baseColor) {
//        private static final RenderData DEFAULT = new RenderData(9922455, 9922455);
        private static final RenderData DEFAULT = new RenderData(-1, -1);
    }

    public static class Renderer implements SpecialModelRenderer<RenderData> {
        private final ShulkerBoxRenderer vanillaRenderer;
        private final SpriteId defaultMaterial;
        private final SpriteId dyedMaterial;

        public Renderer(ShulkerBoxRenderer vanillaRenderer, SpriteId defaultMaterial, SpriteId dyedMaterial) {
            this.vanillaRenderer = vanillaRenderer;
            this.defaultMaterial = defaultMaterial;
            this.dyedMaterial = dyedMaterial;
        }

        @Override
        public void submit(
            RenderData data,
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

            // The atlas isn't stitched yet when this Renderer is baked, so the sprites can only be
            // resolved lazily here, at submit time. -1 means that part was never dyed by this mod, so it
            // keeps the plain undyed (purple-hued) texture at full brightness; only a genuinely dyed part
            // switches to the near-grayscale white texture so its tint comes out as the intended hue.
            TextureAtlasSprite defaultSprite = vanillaRenderer.sprites.get(defaultMaterial);
            TextureAtlasSprite dyedSprite = vanillaRenderer.sprites.get(dyedMaterial);
            RenderType renderType = defaultMaterial.renderType(vanillaRenderer.model::renderType);

            // Item display always shows the box closed, and nothing else ever calls setupAnim on this
            // Renderer's own ShulkerBoxRenderer instance, so "lid" stays at its baked (closed) rest pose
            // without needing to touch it here.
            ModelPart root = vanillaRenderer.model.root();
            ModelPart base = root.getChild("base");
            ModelPart lid = root.getChild("lid");

            boolean baseDyed = data.baseColor() != -1;
            boolean lidDyed = data.lidColor() != -1;

            // ShulkerStuffDyedColorComponent stores plain 24-bit RGB (alpha byte 0x00). World/hand
            // rendering ignores the tint's alpha channel, but the GUI item-icon path bakes into an
            // offscreen atlas texture that's later alpha-composited onto the screen - there, alpha=0x00
            // baked in as fully transparent, which is why the icon was invisible even though the world
            // and in-hand renders (same colors) looked correct. Force full alpha for the tint only.
            int baseColor = baseDyed ? (data.baseColor() | 0xFF000000) : 0xFFFFFFFF;
            int lidColor = lidDyed ? (data.lidColor() | 0xFF000000) : 0xFFFFFFFF;

            collector.submitModelPart(base, poseStack, renderType, light, overlay, baseDyed ? dyedSprite : defaultSprite, false, false, baseColor, null, outlineColor);
            collector.submitModelPart(lid, poseStack, renderType, light, overlay, lidDyed ? dyedSprite : defaultSprite, false, false, lidColor, null, outlineColor);

            poseStack.popPose();
        }

        @Override
        public RenderData extractArgument(ItemStack stack) {
            ShulkerStuffDyedColorComponent component = stack.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get());
            return component == null ? RenderData.DEFAULT : new RenderData(component.lidColor(), component.baseColor());
        }

        @Override
        public void getExtents(Consumer<Vector3fc> extents) {
            PoseStack poseStack = new PoseStack();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.scale(0.9995F, 0.9995F, 0.9995F);
            poseStack.scale(1.0F, -1.0F, -1.0F);
            poseStack.translate(0.0F, -1.0F, 0.0F);
            vanillaRenderer.model.root().getExtentsForGui(poseStack, extents);
        }
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked<RenderData> {
        // Not deserialized from JSON in this mod's own wiring (Fabric replaces the baked ItemModel directly),
        // but NeoForge dispatches special models through this codec, so it needs to be a real one.
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public @NotNull MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public Renderer bake(SpecialModelRenderer.BakingContext context) {
            ShulkerBoxRenderer vanillaRenderer = new ShulkerBoxRenderer(context);
            return new Renderer(vanillaRenderer, Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION, Sheets.getShulkerBoxSprite(DyeColor.WHITE));
        }
    }
}
