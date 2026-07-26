package com.chimericdream.lib.blocks;

import com.chimericdream.lib.util.Tool;
import dev.architectury.registry.registries.RegistrySupplier;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockConfig {
    protected BlockBehaviour.Properties baseSettings = null;
    protected String blockName = null;
    protected Item item = null;
    protected RegistrySupplier<Item> itemSupplier = null;
    protected String itemName = null;
    protected String material = null;
    protected String materialName = null;
    protected Tool tool = null;
    protected RegistrySupplier<Block> ingredientSupplier = null;
    protected final Map<String, Block> ingredients = new LinkedHashMap<>();
    protected final Map<String, TagKey<Item>> tagIngredients = new LinkedHashMap<>();
    protected boolean isFlammable = false;
    protected boolean isTranslucent = false;
    protected final Map<String, Identifier> textures = new LinkedHashMap<>();
    protected RenderType renderType = RenderType.SOLID;

    public BlockConfig settings(BlockBehaviour.Properties baseSettings) {
        this.baseSettings = baseSettings;
        return this;
    }

    public BlockBehaviour.Properties getBaseSettings() {
        return Objects.requireNonNullElseGet(this.baseSettings, () -> BlockBehaviour.Properties.ofFullCopy(this.getIngredient()));
    }

    public BlockConfig item(RegistrySupplier<Item> itemSupplier) {
        this.itemSupplier = itemSupplier;
        return this;
    }

    public BlockConfig item(Item item) {
        this.item = item;
        return this;
    }

    public @Nullable Item getItem() {
        if (itemSupplier != null) {
            return itemSupplier.get();
        }

        return item;
    }

    public BlockConfig itemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public @Nullable String getItemName() {
        return itemName;
    }

    public BlockConfig material(String material) {
        this.material = material;
        return this;
    }

    public @Nullable String getMaterial() {
        return material;
    }

    public BlockConfig materialName(String materialName) {
        this.materialName = materialName;
        return this;
    }

    public @Nullable String getName() {
        return blockName;
    }

    public BlockConfig name(String name) {
        this.blockName = name;
        return this;
    }

    public @Nullable String getMaterialName() {
        return materialName;
    }

    public BlockConfig ingredient(RegistrySupplier<Block> blockSupplier) {
        this.ingredientSupplier = blockSupplier;
        return this;
    }

    public BlockConfig ingredient(Block block) {
        this.ingredients.put("default", block);
        return this;
    }

    public BlockConfig ingredient(String key, Block block) {
        this.ingredients.put(key, block);
        return this;
    }

    public @Nullable Block getIngredient() {
        if (ingredientSupplier != null) {
            return ingredientSupplier.get();
        }

        if (!ingredients.containsKey("default")) {
            throw new IllegalStateException("No default ingredient set");
        }

        return ingredients.get("default");
    }

    public @Nullable Block getIngredient(String key) {
        return ingredients.get(key);
    }

    public BlockConfig tagIngredient(TagKey<Item> tag) {
        this.tagIngredients.put("default", tag);
        return this;
    }

    public BlockConfig tagIngredient(String key, TagKey<Item> tag) {
        this.tagIngredients.put(key, tag);
        return this;
    }

    public @Nullable TagKey<Item> getTagIngredient() {
        return tagIngredients.get("default");
    }

    public @Nullable TagKey<Item> getTagIngredient(String key) {
        return tagIngredients.get(key);
    }

    public BlockConfig flammable(boolean isFlammable) {
        this.isFlammable = isFlammable;
        return this;
    }

    public BlockConfig flammable() {
        return flammable(true);
    }

    public boolean isFlammable() {
        return isFlammable;
    }

    public BlockConfig tool(Tool tool) {
        this.tool = tool;
        return this;
    }

    public @Nullable Tool getTool() {
        return tool;
    }

    public BlockConfig translucent(boolean isTranslucent) {
        this.isTranslucent = isTranslucent;
        return this;
    }

    public BlockConfig translucent() {
        return translucent(true);
    }

    public boolean isTranslucent() {
        return isTranslucent;
    }

    public BlockConfig texture(Identifier texture) {
        return texture("default", texture);
    }

    public BlockConfig texture(String name, Identifier texture) {
        this.textures.put(name, texture);
        return this;
    }

    public @Nullable Identifier getTexture() {
        Identifier texture = textures.get("default");

        // Not getOrDefault: it evaluates the fallback even when a default texture is set, and
        // getIngredient() throws when the config has no ingredient — which is exactly the case where
        // an explicit texture is most likely to be the only thing configured.
        if (texture != null) {
            return texture;
        }

        return TextureMapping.getBlockTexture(this.getIngredient()).sprite();
    }

    public @Nullable Identifier getTexture(String name) {
        return textures.get(name);
    }

    public BlockConfig renderType(RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public enum RenderType {
        SOLID("minecraft:solid"),
        CUTOUT("minecraft:cutout"),
        CUTOUT_MIPPED("minecraft:cutout_mipped"),
        CUTOUT_MIPPED_ALL("minecraft:cutout_mipped_all"),
        TRANSLUCENT("minecraft:translucent"),
        TRIPWIRE("minecraft:tripwire");

        public final String name;

        RenderType(String name) {
            this.name = name;
        }
    }
}
