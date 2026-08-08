package com.chimericdream.lib.blocks.family;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.registries.ModRegistryHelper;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;

/**
 * Declares a base block's {@link BlockConfig} once and registers whichever of its
 * stairs/slab/wall variants are requested, each with its own derived {@link BlockConfig}
 * (ingredient = the base block, materialName/texture/tool/flammable inherited from the base
 * unless overridden per variant).
 */
public class BlockFamily {
    private final Map<BlockFamilyVariant, RegistrySupplier<Block>> blocks;
    private final Map<BlockFamilyVariant, BlockConfig> configs;

    private BlockFamily(Map<BlockFamilyVariant, RegistrySupplier<Block>> blocks, Map<BlockFamilyVariant, BlockConfig> configs) {
        this.blocks = blocks;
        this.configs = configs;
    }

    public Optional<RegistrySupplier<Block>> getBlock(BlockFamilyVariant variant) {
        return Optional.ofNullable(blocks.get(variant));
    }

    public Optional<BlockConfig> getConfig(BlockFamilyVariant variant) {
        return Optional.ofNullable(configs.get(variant));
    }

    public Optional<RegistrySupplier<Block>> getStairs() {
        return getBlock(BlockFamilyVariant.STAIRS);
    }

    public Optional<RegistrySupplier<Block>> getSlab() {
        return getBlock(BlockFamilyVariant.SLAB);
    }

    public Optional<RegistrySupplier<Block>> getWall() {
        return getBlock(BlockFamilyVariant.WALL);
    }

    public Set<BlockFamilyVariant> getVariants() {
        return blocks.keySet();
    }

    public static Builder builder(ModRegistryHelper helper, String materialKey, BlockConfig template) {
        return new Builder(helper, materialKey, template);
    }

    public static class Builder {
        private final ModRegistryHelper helper;
        private final String materialKey;
        private final BlockConfig template;

        private final Set<BlockFamilyVariant> variants = EnumSet.noneOf(BlockFamilyVariant.class);
        private final Map<BlockFamilyVariant, Identifier> idOverrides = new EnumMap<>(BlockFamilyVariant.class);
        private Item.Properties itemSettings = new Item.Properties();

        private Function<BlockConfig, ? extends StairBlock> stairsFactory = config ->
            new StairBlock(config.getIngredient().defaultBlockState(), config.getBaseSettings());
        private Function<BlockConfig, ? extends SlabBlock> slabFactory = config ->
            new SlabBlock(config.getBaseSettings());
        private Function<BlockConfig, ? extends WallBlock> wallFactory = config ->
            new WallBlock(config.getBaseSettings());

        private Builder(ModRegistryHelper helper, String materialKey, BlockConfig template) {
            this.helper = helper;
            this.materialKey = materialKey;
            this.template = template;
        }

        public Builder variants(BlockFamilyVariant... variants) {
            this.variants.addAll(List.of(variants));
            return this;
        }

        public Builder itemSettings(Item.Properties itemSettings) {
            this.itemSettings = itemSettings;
            return this;
        }

        public Builder stairsId(Identifier id) {
            idOverrides.put(BlockFamilyVariant.STAIRS, id);
            return this;
        }

        public Builder slabId(Identifier id) {
            idOverrides.put(BlockFamilyVariant.SLAB, id);
            return this;
        }

        public Builder wallId(Identifier id) {
            idOverrides.put(BlockFamilyVariant.WALL, id);
            return this;
        }

        public Builder stairsFactory(Function<BlockConfig, ? extends StairBlock> factory) {
            this.stairsFactory = factory;
            return this;
        }

        public Builder slabFactory(Function<BlockConfig, ? extends SlabBlock> factory) {
            this.slabFactory = factory;
            return this;
        }

        public Builder wallFactory(Function<BlockConfig, ? extends WallBlock> factory) {
            this.wallFactory = factory;
            return this;
        }

        public BlockFamily build() {
            Map<BlockFamilyVariant, RegistrySupplier<Block>> blocks = new EnumMap<>(BlockFamilyVariant.class);
            Map<BlockFamilyVariant, BlockConfig> configs = new EnumMap<>(BlockFamilyVariant.class);

            if (variants.contains(BlockFamilyVariant.STAIRS)) {
                Identifier id = idFor(BlockFamilyVariant.STAIRS, "_stairs");
                BlockConfig config = deriveVariantConfig(id);
                configs.put(BlockFamilyVariant.STAIRS, config);
                blocks.put(BlockFamilyVariant.STAIRS, helper.registerWithItem(id, () -> stairsFactory.apply(config), itemSettings));
            }

            if (variants.contains(BlockFamilyVariant.SLAB)) {
                Identifier id = idFor(BlockFamilyVariant.SLAB, "_slab");
                BlockConfig config = deriveVariantConfig(id);
                configs.put(BlockFamilyVariant.SLAB, config);
                blocks.put(BlockFamilyVariant.SLAB, helper.registerWithItem(id, () -> slabFactory.apply(config), itemSettings));
            }

            if (variants.contains(BlockFamilyVariant.WALL)) {
                Identifier id = idFor(BlockFamilyVariant.WALL, "_wall");
                BlockConfig config = deriveVariantConfig(id);
                configs.put(BlockFamilyVariant.WALL, config);
                blocks.put(BlockFamilyVariant.WALL, helper.registerWithItem(id, () -> wallFactory.apply(config), itemSettings));
            }

            return new BlockFamily(blocks, configs);
        }

        private Identifier idFor(BlockFamilyVariant variant, String suffix) {
            return idOverrides.getOrDefault(variant, helper.makeId(materialKey + suffix));
        }

        /**
         * The base settings are stamped with {@code id} up front so the default factories (plain
         * vanilla {@code StairBlock}/{@code SlabBlock}/{@code WallBlock}) construct a block whose
         * registry key is already set — {@code DeferredRegister.register} does not inject it, and
         * vanilla block construction requires it. A custom factory that builds its own subclass with
         * its own id convention (e.g. a mod's pre-existing block class) simply ignores this.
         */
        private BlockConfig deriveVariantConfig(Identifier id) {
            BlockConfig config = new BlockConfig()
                .material(materialKey)
                .materialName(template.getMaterialName())
                .ingredient(template.getIngredient())
                .texture(template.getTexture())
                .tool(template.getTool())
                .flammable(template.isFlammable())
                .translucent(template.isTranslucent())
                .renderType(template.getRenderType());

            config.settings(config.getBaseSettings().setId(helper.makeBlockRegistryKey(id)));

            return config;
        }
    }
}
