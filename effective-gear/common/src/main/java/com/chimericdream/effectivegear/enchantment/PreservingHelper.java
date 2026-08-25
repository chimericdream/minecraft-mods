package com.chimericdream.effectivegear.enchantment;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Shared state for the Preserving enchantment: the blockstate property that marks a leaves block as
 * color-locked, and the set of leaf species where that's actually visible (every other leaves block
 * either already renders a fixed color or isn't biome-tinted at all, so preserving it would be a
 * no-op). Vanilla registers all five of these under the same shared {@code foliage()} block color
 * source, whose fixed (non-biome-sampled) color is uniformly {@link FoliageColor#FOLIAGE_DEFAULT} for
 * every one of them, mangrove included.
 *
 * <p>{@link #PRESERVED} is referenced by {@code EG$LeavesBlockMixin} from inside
 * {@code LeavesBlock.createBlockStateDefinition}, which runs while {@link Blocks}'s own static
 * initializer is still assigning its leaf fields — so the species set below is built lazily
 * ({@link Suppliers#memoize}) rather than eagerly, otherwise loading this class that early would try
 * to read not-yet-assigned {@code Blocks} fields as {@code null}.
 */
public final class PreservingHelper {
    public static final BooleanProperty PRESERVED = BooleanProperty.create("preserved");

    private static final Supplier<Set<Block>> PRESERVABLE_LEAVES = Suppliers.memoize(() -> Set.of(
        Blocks.OAK_LEAVES,
        Blocks.JUNGLE_LEAVES,
        Blocks.ACACIA_LEAVES,
        Blocks.DARK_OAK_LEAVES,
        Blocks.MANGROVE_LEAVES
    ));

    private PreservingHelper() {
    }

    public static boolean isPreservable(Block block) {
        return PRESERVABLE_LEAVES.get().contains(block);
    }

    public static Set<Block> getPreservableLeaves() {
        return PRESERVABLE_LEAVES.get();
    }

    public static int getLevel(ServerLevel world, ItemInstance tool) {
        Registry<Enchantment> registry = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> preserving = registry.getOrThrow(ModEnchantments.PRESERVING);
        return EnchantmentHelper.getItemEnchantmentLevel(preserving, tool);
    }
}
