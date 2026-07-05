package com.chimericdream.minekea.fabric.data.blockstate.suppliers;

import com.chimericdream.lib.blocks.BlockConfig;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/*
 * @TODO: abstract this out to chimericlib where I can add more custom model registration methods
 */
public class CustomBlockStateModelSupplier {
    public static final ModelTemplate CUSTOM_CROP = new CustomCropModel();

    public static void registerCrop(BlockModelGenerators generator, Block crop, Property<Integer> ageProperty, int... ageTextureIndices) {
        if (ageProperty.getAllValues().count() != ageTextureIndices.length) {
            throw new IllegalArgumentException();
        }

        Int2ObjectMap<ResourceLocation> int2ObjectMap = new Int2ObjectOpenHashMap<>();
        PropertyDispatch<MultiVariant> blockStateVariantMap = PropertyDispatch.initial(ageProperty).generate((integer) -> {
            int i = ageTextureIndices[integer];

            ResourceLocation identifier = int2ObjectMap.computeIfAbsent(i, (j) -> generator.createSuffixedVariant(crop, "_stage" + i, CUSTOM_CROP, TextureMapping::crop));

            return new MultiVariant(WeightedList.of(new Variant(identifier)));
        });

        generator.registerSimpleFlatItemModel(crop.asItem());
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(crop).with(blockStateVariantMap));
    }

    public static class CustomBlockModel extends ModelTemplate {
        private final BlockConfig.RenderType renderType;

        public CustomBlockModel(Optional<ResourceLocation> parent, Optional<String> variant, TextureSlot... requiredTextureSlots) {
            this(BlockConfig.RenderType.SOLID, parent, variant, requiredTextureSlots);
        }

        public CustomBlockModel(BlockConfig.RenderType renderType, Optional<ResourceLocation> parent, Optional<String> variant, TextureSlot... requiredTextureSlots) {
            super(parent, variant, requiredTextureSlots);

            this.renderType = renderType;
        }

        @Override
        public @NotNull ResourceLocation create(ResourceLocation id, TextureMapping textures, BiConsumer<ResourceLocation, ModelInstance> modelCollector) {
            Map<TextureSlot, ResourceLocation> map = this.createMap(textures);
            modelCollector.accept(id, (ModelInstance) () -> {
                JsonObject jsonObject = new JsonObject();
                this.model.ifPresent((identifier) -> jsonObject.addProperty("parent", identifier.toString()));
                if (!map.isEmpty()) {
                    JsonObject jsonObject2 = new JsonObject();
                    map.forEach((textureKey, identifier) -> jsonObject2.addProperty(textureKey.getId(), identifier.toString()));
                    jsonObject.add("textures", jsonObject2);
                }

                jsonObject.addProperty("render_type", renderType.name);

                return jsonObject;
            });
            return id;
        }
    }

    /*
     * @TODO: abstract this out to chimericlib and make it more generic; i.e. support the rest of the render types in NeoForge
     *   @see https://docs.neoforged.net/docs/resources/client/models/#render-types
     */
    public static class CustomCropModel extends CustomBlockModel {
        public CustomCropModel() {
            super(BlockConfig.RenderType.CUTOUT, Optional.of(ResourceLocation.withDefaultNamespace("block/crop")), Optional.empty(), TextureSlot.CROP);
        }
    }
}
