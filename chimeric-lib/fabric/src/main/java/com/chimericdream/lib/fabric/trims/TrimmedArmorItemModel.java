package com.chimericdream.lib.fabric.trims;

import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.Constructor;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBaker.SharedOperationKey;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.Material.Baked;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Wraps every item's ordinary flat-icon model with a trim overlay layer that's resolved from
 * whatever {@link TrimMaterial} is actually on the stack, rather than a fixed list of cases baked
 * into a model JSON. This is a Fabric port of NeoForge's own {@code neoforge:trimmed_armor} item
 * model type ({@code net.neoforged.neoforge.client.model.item.TrimmedArmorModel}) — NeoForge ships
 * that type in its universal jar, reaching the same private vanilla model-baking classes vanilla
 * itself uses to bake ordinary layer0/layer1/... "item/generated" textures via its own access
 * transformer. Fabric has no equivalent transformer, and a Loom access widener turned out not to
 * reliably widen a private constructor on a private <em>nested</em> class ({@code
 * ItemModelGenerator.ItemLayerKey}) at actual game runtime (it works at dev-compile time, but the
 * game still threw {@code IllegalAccessError} baking the first real trim layer) — so this reaches
 * both required members ({@code CuboidItemModelWrapper}'s constructor and
 * {@code ItemModelGenerator.ItemLayerKey}'s) via reflection instead, which needs no bytecode
 * transformation and works the same in dev and in a packaged jar.
 *
 * <p>Everything that decides whether a stack even qualifies — its {@code EquipmentSlot} (from the
 * {@code minecraft:equippable} component) and its {@code ArmorTrim}/{@code TrimMaterial} — is read at
 * <b>render time</b> off the live stack, not at bake time off the item's default components: MC 26.2
 * only binds default data components during a {@code ReloadableServerResources} reload, which hasn't
 * happened yet during the client's very first resource/model reload (see "Components not bound yet" in
 * {@code docs/MC-26.2-NOTES.md}), so {@link TrimmedArmorModelLoadingPlugin} wraps every item
 * unconditionally and this class simply no-ops for anything that isn't trimmed armor. Because it reads
 * straight off the stack, it works for <em>any</em> mod's armor and <em>any</em> mod's trim materials,
 * not just ones registered through {@code chimeric-lib}, with no per-item resource pack override to
 * generate or maintain.
 */
public class TrimmedArmorItemModel implements ItemModel {
    private static final Transformation TRIM_TRANSFORM = new Transformation(
        new Vector3f(), new Quaternionf(), new Vector3f(1.002F, 1.002F, 1.002F), new Quaternionf()
    );
    private static final ModelDebugName DEBUG_NAME = () -> "TrimmedArmorItemModel";
    private static final ModelState TRIM_STATE = new ComposedModelState(BlockModelRotation.IDENTITY, TRIM_TRANSFORM);
    private static final Constructor<?> ITEM_LAYER_KEY_CONSTRUCTOR = findItemLayerKeyConstructor();
    private static final Constructor<CuboidItemModelWrapper> CUBOID_ITEM_MODEL_WRAPPER_CONSTRUCTOR = findCuboidItemModelWrapperConstructor();

    private final Object2ObjectMap<String, ItemModel> itemsWithTrims = new Object2ObjectOpenHashMap<>();
    private final ItemModel baseModel;
    private final BakingContext bakingContext;
    private final Matrix4fc transformation;
    private final ItemTransforms itemTransforms;

    private TrimmedArmorItemModel(ItemModel baseModel, BakingContext bakingContext, Matrix4fc transformation) {
        this.baseModel = baseModel;
        this.bakingContext = bakingContext;
        this.transformation = transformation;

        ResolvedModel baseItemModel = bakingContext.blockModelBaker().getModel(Identifier.withDefaultNamespace("item/generated"));
        this.itemTransforms = baseItemModel.getTopTransforms();
    }

    @Override
    public void update(
        @NonNull ItemStackRenderState state,
        @NonNull ItemStack stack,
        @NonNull ItemModelResolver resolver,
        @NonNull ItemDisplayContext context,
        @Nullable ClientLevel level,
        @Nullable ItemOwner owner,
        int seed
    ) {
        this.baseModel.update(state, stack, resolver, context, level, owner, seed);

        ArmorTrim trim = stack.get(DataComponents.TRIM);
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (trim == null || equippable == null || equippable.assetId().isEmpty()) {
            return;
        }

        String slotTexture = trimTextureBaseFor(equippable.slot());
        if (slotTexture == null) {
            return;
        }

        Holder<TrimMaterial> material = trim.material();
        String suffix = material.value().assets().assetId(equippable.assetId().get()).suffix();
        String cacheKey = slotTexture + "/" + suffix;
        Identifier baseTrimTexture = Identifier.withDefaultNamespace("trims/items/" + slotTexture);

        this.itemsWithTrims.computeIfAbsent(cacheKey, key -> createTrimLayer(baseTrimTexture, suffix))
            .update(state, stack, resolver, context, level, owner, seed);
    }

    @Nullable
    private static String trimTextureBaseFor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> "helmet_trim";
            case CHEST -> "chestplate_trim";
            case LEGS -> "leggings_trim";
            case FEET -> "boots_trim";
            default -> null;
        };
    }

    private ItemModel createTrimLayer(Identifier baseTrimTexture, String suffix) {
        ModelBaker baker = this.bakingContext.blockModelBaker();
        MaterialBaker materials = baker.materials();
        Baked overlayMaterial = materials.get(new Material(baseTrimTexture.withSuffix("_" + suffix)), DEBUG_NAME);
        ModelRenderProperties overlayRenderProperties = new ModelRenderProperties(false, overlayMaterial, this.itemTransforms);
        QuadCollection overlayQuads = baker.compute(newItemLayerKey(overlayMaterial, TRIM_STATE, 0));
        return newCuboidItemModelWrapper(List.of(), overlayQuads, overlayRenderProperties, this.transformation);
    }

    @SuppressWarnings("unchecked")
    private static SharedOperationKey<QuadCollection> newItemLayerKey(Material.Baked material, ModelState modelState, int layerIndex) {
        try {
            return (SharedOperationKey<QuadCollection>) ITEM_LAYER_KEY_CONSTRUCTOR.newInstance(material, modelState, layerIndex);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to construct ItemModelGenerator.ItemLayerKey via reflection", e);
        }
    }

    private static CuboidItemModelWrapper newCuboidItemModelWrapper(
        List<?> tints,
        QuadCollection quads,
        ModelRenderProperties properties,
        Matrix4fc transformation
    ) {
        try {
            return CUBOID_ITEM_MODEL_WRAPPER_CONSTRUCTOR.newInstance(tints, quads, properties, transformation);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to construct CuboidItemModelWrapper via reflection", e);
        }
    }

    private static Constructor<?> findItemLayerKeyConstructor() {
        try {
            Class<?> itemLayerKeyClass = Class.forName("net.minecraft.client.resources.model.cuboid.ItemModelGenerator$ItemLayerKey");
            Constructor<?> constructor = itemLayerKeyClass.getDeclaredConstructor(Material.Baked.class, ModelState.class, int.class);
            constructor.setAccessible(true);
            return constructor;
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Constructor<CuboidItemModelWrapper> findCuboidItemModelWrapperConstructor() {
        try {
            Constructor<?> constructor = CuboidItemModelWrapper.class.getDeclaredConstructor(
                List.class, QuadCollection.class, ModelRenderProperties.class, Matrix4fc.class
            );
            constructor.setAccessible(true);
            return (Constructor<CuboidItemModelWrapper>) constructor;
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private record ComposedModelState(ModelState parent, Transformation transformation) implements ModelState {
        private ComposedModelState {
            transformation = parent.transformation().compose(transformation);
        }

        @Override
        public @NonNull Matrix4fc faceTransformation(net.minecraft.core.@NonNull Direction face) {
            return this.parent.faceTransformation(face);
        }

        @Override
        public @NonNull Matrix4fc inverseFaceTransformation(net.minecraft.core.@NonNull Direction face) {
            return this.parent.inverseFaceTransformation(face);
        }
    }

    public record Unbaked(ItemModel.Unbaked baseModel) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(ItemModels.CODEC.fieldOf("base_model").forGetter(Unbaked::baseModel)).apply(instance, Unbaked::new)
        );

        @Override
        public @NonNull ItemModel bake(@NonNull BakingContext context, @NonNull Matrix4fc transformation) {
            return new TrimmedArmorItemModel(this.baseModel.bake(context, transformation), context, transformation);
        }

        @Override
        public void resolveDependencies(@NonNull Resolver resolver) {
            this.baseModel.resolveDependencies(resolver);
        }

        @Override
        public @NonNull MapCodec<? extends ItemModel.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
