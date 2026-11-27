package com.chimericdream.enchantingfixes.enchantment;

import com.chimericdream.enchantingfixes.ModInfo;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * The Advanced Silk Touch enchantment, from the optional Advanced Silk Touch data pack. Its levels collect blocks
 * that drop nothing, or drop a different item, with Silk Touch. It does not include Silk Touch: a tool can have
 * both.
 *
 * <p>The enchantment definition has a maximum level of 3. Random enchantments use that level as the base (see
 * {@link RandomLevels}). The default config list raises the maximum level to {@value #MAX_LEVEL}.
 */
public final class AdvancedSilkTouch {
    /** The path of the pack in the mod jar. */
    public static final String PACK_PATH = "resourcepacks/advanced_silk_touch";
    public static final String ID = ModInfo.MOD_ID + ":advanced_silk_touch";
    public static final ResourceKey<Enchantment> KEY = ResourceKey.create(Registries.ENCHANTMENT, Identifier.parse(ID));
    /** Above this level, the enchantment collects no more blocks. */
    public static final int MAX_LEVEL = 30;
    /** Bedrock has no hardness. With the necessary level, it breaks like reinforced deepslate. */
    public static final float BEDROCK_HARDNESS = 55.0F;

    private AdvancedSilkTouch() {
    }

    /** True if the data pack is on, so the enchantment is in the registry. */
    public static boolean isRegistered(Level level) {
        return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(KEY).isPresent();
    }

    /** The Advanced Silk Touch level that collects the block, or 0 if the enchantment does not change the block. */
    public static int requiredLevel(BlockState state) {
        if (state.is(Blocks.DIRT_PATH) || state.is(Blocks.FARMLAND)) {
            return 1;
        }
        if (state.getBlock() instanceof BrushableBlock) {
            return 3;
        }
        if (state.is(Blocks.BUDDING_AMETHYST)) {
            return 5;
        }
        if (state.getBlock() instanceof InfestedBlock) {
            return 7;
        }
        if (state.is(Blocks.SPAWNER)) {
            return 10;
        }
        if (state.is(Blocks.REINFORCED_DEEPSLATE)) {
            return 15;
        }
        if (state.is(Blocks.BEDROCK)) {
            return MAX_LEVEL;
        }

        return 0;
    }

    public static int level(ItemInstance tool) {
        ItemEnchantments enchantments = tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(KEY)) {
                return entry.getIntValue();
            }
        }

        return 0;
    }

    private static boolean collects(BlockState state, ItemInstance tool) {
        int required = requiredLevel(state);
        return required > 0 && level(tool) >= required;
    }

    /**
     * The drops of a block that a tool breaks. A collected block drops itself. A spawner or a suspicious block
     * keeps its block entity data, so a suspicious block keeps its loot.
     */
    public static List<ItemStack> drops(List<ItemStack> original, BlockState state, ServerLevel level, @Nullable BlockEntity blockEntity, ItemInstance tool) {
        if (!collects(state, tool)) {
            return original;
        }

        ItemStack stack = new ItemStack(state.getBlock());
        if (stack.isEmpty()) {
            return original;
        }

        if (blockEntity instanceof SpawnerBlockEntity || blockEntity instanceof BrushableBlockEntity) {
            TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
            blockEntity.saveCustomOnly(output);
            BlockItem.setBlockEntityData(stack, blockEntity.getType(), output);
        }

        return List.of(stack);
    }

    /** Bedrock has a hardness of -1, which makes it unbreakable. With the necessary level, it gets a hardness. */
    public static float destroySpeed(float original, BlockState state, Player player) {
        if (original != -1.0F || !state.is(Blocks.BEDROCK) || !collects(state, player.getMainHandItem())) {
            return original;
        }

        return BEDROCK_HARDNESS;
    }

    /**
     * Vanilla lets only operators place a spawner with block entity data. For other players, this sets only the
     * entity type from the data, while the data pack is on. A spawn egg can do the same in vanilla, so a player cannot get more from an
     * item with changed data.
     *
     * @return true if the spawner got an entity type
     */
    public static boolean placeSpawner(Level level, BlockPos pos, ItemStack stack) {
        if (level.isClientSide() || !isRegistered(level)) {
            return false;
        }

        TypedEntityData<BlockEntityType<?>> data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null || !(level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) || data.type() != spawner.getType()) {
            return false;
        }

        Optional<EntityType<?>> type = spawnerEntity(data.getUnsafe());
        if (type.isEmpty() || !type.get().canSpawn(level)) {
            return false;
        }

        spawner.setEntityId(type.get(), level.getRandom());
        BlockState state = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        return true;
    }

    /** The entity type in the {@code SpawnData} of saved spawner data. */
    public static Optional<EntityType<?>> spawnerEntity(CompoundTag tag) {
        return tag.getCompoundOrEmpty("SpawnData").getCompoundOrEmpty("entity").getString("id")
            .map(Identifier::tryParse)
            .flatMap(BuiltInRegistries.ENTITY_TYPE::getOptional);
    }
}
