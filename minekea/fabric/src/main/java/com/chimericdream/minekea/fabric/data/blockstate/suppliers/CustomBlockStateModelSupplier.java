package com.chimericdream.minekea.fabric.data.blockstate.suppliers;

import com.chimericdream.lib.blocks.BlockConfig;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/*
 * @TODO: abstract this out to chimericlib where I can add more custom model registration methods
 */
public class CustomBlockStateModelSupplier {
    public static final Model CUSTOM_CROP = new CustomCropModel();

    public static void registerCrop(BlockStateModelGenerator generator, Block crop, Property<Integer> ageProperty, int... ageTextureIndices) {
        if (ageProperty.getValues().size() != ageTextureIndices.length) {
            throw new IllegalArgumentException();
        }

        Int2ObjectMap<Identifier> int2ObjectMap = new Int2ObjectOpenHashMap<>();
        BlockStateVariantMap<WeightedVariant> blockStateVariantMap = BlockStateVariantMap.models(ageProperty).generate((integer) -> {
            int i = ageTextureIndices[integer];

            Identifier identifier = int2ObjectMap.computeIfAbsent(i, (j) -> generator.createSubModel(crop, "_stage" + i, CUSTOM_CROP, TextureMap::crop));

            return new WeightedVariant(Pool.of(new ModelVariant(identifier)));
        });

        generator.registerItemModel(crop.asItem());
        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(crop).with(blockStateVariantMap));
    }

    public static class CustomBlockModel extends Model {
        private final BlockConfig.RenderType renderType;

        public CustomBlockModel(Optional<Identifier> parent, Optional<String> variant, TextureKey... requiredTextureKeys) {
            this(BlockConfig.RenderType.SOLID, parent, variant, requiredTextureKeys);
        }

        public CustomBlockModel(BlockConfig.RenderType renderType, Optional<Identifier> parent, Optional<String> variant, TextureKey... requiredTextureKeys) {
            super(parent, variant, requiredTextureKeys);

            this.renderType = renderType;
        }

        @Override
        public Identifier upload(Identifier id, TextureMap textures, BiConsumer<Identifier, ModelSupplier> modelCollector) {
            Map<TextureKey, Identifier> map = this.createTextureMap(textures);
            modelCollector.accept(id, (ModelSupplier) () -> {
                JsonObject jsonObject = new JsonObject();
                this.parent.ifPresent((identifier) -> jsonObject.addProperty("parent", identifier.toString()));
                if (!map.isEmpty()) {
                    JsonObject jsonObject2 = new JsonObject();
                    map.forEach((textureKey, identifier) -> jsonObject2.addProperty(textureKey.getName(), identifier.toString()));
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
            super(BlockConfig.RenderType.CUTOUT, Optional.of(Identifier.ofVanilla("block/crop")), Optional.empty(), TextureKey.CROP);
        }
    }
}
