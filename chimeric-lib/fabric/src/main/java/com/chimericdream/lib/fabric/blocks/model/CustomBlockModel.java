package com.chimericdream.lib.fabric.blocks.model;

import com.chimericdream.lib.blocks.BlockConfig;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ModelTemplate} that also emits a {@code render_type} field — vanilla's own
 * {@code ModelTemplate} has no render-type support, so mods that need e.g. cutout rendering on a
 * generated model (lanterns, crops, glass-like blocks) need this to say so in the generated JSON.
 */
public class CustomBlockModel extends ModelTemplate {
    private final BlockConfig.RenderType renderType;

    public CustomBlockModel(Optional<Identifier> parent, Optional<String> variant, TextureSlot... requiredTextureSlots) {
        this(BlockConfig.RenderType.SOLID, parent, variant, requiredTextureSlots);
    }

    public CustomBlockModel(BlockConfig.RenderType renderType, Optional<Identifier> parent, Optional<String> variant, TextureSlot... requiredTextureSlots) {
        super(parent, variant, requiredTextureSlots);

        this.renderType = renderType;
    }

    @Override
    public @NotNull Identifier create(Identifier id, TextureMapping textures, BiConsumer<Identifier, ModelInstance> modelCollector) {
        Map<TextureSlot, Material> map = this.createMap(textures);
        modelCollector.accept(id, (ModelInstance) () -> {
            JsonObject jsonObject = new JsonObject();
            this.model.ifPresent((identifier) -> jsonObject.addProperty("parent", identifier.toString()));
            if (!map.isEmpty()) {
                JsonObject jsonObject2 = new JsonObject();
                map.forEach((textureKey, material) -> jsonObject2.addProperty(textureKey.getId(), material.sprite().toString()));
                jsonObject.add("textures", jsonObject2);
            }

            jsonObject.addProperty("render_type", renderType.name);

            return jsonObject;
        });
        return id;
    }
}
