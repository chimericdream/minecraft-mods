package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.enchantment.AdvancedSilkTouch;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;
import java.util.Optional;

import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.enchantment;
import static com.chimericdream.enchantingfixes.fabric.test.TestStacks.player;

/**
 * The Advanced Silk Touch enchantment, with real blocks, block entities, and loot tables. The test mod ships the
 * data pack data (see {@code fabric/build.gradle}), so the enchantment is always registered here.
 */
@SuppressWarnings("unused")
public class AdvancedSilkTouchGameTest {
    private static final BlockPos POS = new BlockPos(1, 1, 1);
    private static final BlockPos OTHER_POS = new BlockPos(2, 1, 1);

    /** A pickaxe with Advanced Silk Touch at {@code level}, and Silk Touch at {@code silkTouch} (0 for none). */
    private static ItemStack pickaxe(GameTestHelper context, int level, int silkTouch) {
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        if (level > 0) {
            enchantments.set(enchantment(context, AdvancedSilkTouch.KEY), level);
        }
        if (silkTouch > 0) {
            enchantments.set(enchantment(context, Enchantments.SILK_TOUCH), silkTouch);
        }

        ItemStack stack = new ItemStack(Items.NETHERITE_PICKAXE);
        stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
        return stack;
    }

    /** The drops when a player breaks the block at {@link #POS} with this tool. */
    private static List<ItemStack> drops(GameTestHelper context, ItemStack tool) {
        ServerLevel level = context.getLevel();
        BlockPos pos = context.absolutePos(POS);
        return Block.getDrops(level.getBlockState(pos), level, pos, level.getBlockEntity(pos), player(context), tool);
    }

    private static void assertDrop(GameTestHelper context, Block block, ItemStack tool, Item expected) {
        context.setBlock(POS, block);
        List<ItemStack> drops = drops(context, tool);
        String label = block + " with " + tool.getEnchantments();
        context.assertValueEqual(drops.size(), 1, label + ": number of drops");
        context.assertValueEqual(drops.getFirst().getItem(), expected, label + ": drop");
    }

    private static void assertNoDrop(GameTestHelper context, Block block, ItemStack tool) {
        context.setBlock(POS, block);
        context.assertTrue(drops(context, tool).isEmpty(), block + " with " + tool.getEnchantments() + " should drop nothing");
    }

    @GameTest
    public void eachLevelCollectsItsBlocks(GameTestHelper context) {
        assertDrop(context, Blocks.DIRT_PATH, pickaxe(context, 0, 1), Items.DIRT);
        assertDrop(context, Blocks.DIRT_PATH, pickaxe(context, 1, 0), Items.DIRT_PATH);
        assertDrop(context, Blocks.FARMLAND, pickaxe(context, 1, 0), Items.FARMLAND);
        assertNoDrop(context, Blocks.BUDDING_AMETHYST, pickaxe(context, 4, 0));
        assertDrop(context, Blocks.BUDDING_AMETHYST, pickaxe(context, 5, 0), Items.BUDDING_AMETHYST);
        assertNoDrop(context, Blocks.INFESTED_STONE, pickaxe(context, 6, 0));
        assertDrop(context, Blocks.INFESTED_STONE, pickaxe(context, 7, 0), Items.INFESTED_STONE);
        assertNoDrop(context, Blocks.REINFORCED_DEEPSLATE, pickaxe(context, 14, 0));
        assertDrop(context, Blocks.REINFORCED_DEEPSLATE, pickaxe(context, 15, 0), Items.REINFORCED_DEEPSLATE);
        assertNoDrop(context, Blocks.BEDROCK, pickaxe(context, 29, 0));
        assertDrop(context, Blocks.BEDROCK, pickaxe(context, 30, 0), Items.BEDROCK);
        context.succeed();
    }

    @GameTest
    public void silkTouchKeepsItsVanillaDrops(GameTestHelper context) {
        assertDrop(context, Blocks.INFESTED_STONE, pickaxe(context, 0, 1), Items.STONE);
        assertNoDrop(context, Blocks.BUDDING_AMETHYST, pickaxe(context, 0, 1));
        // Advanced Silk Touch does not include Silk Touch.
        assertDrop(context, Blocks.GRASS_BLOCK, pickaxe(context, 30, 0), Items.DIRT);
        // With both, each enchantment does its part.
        assertDrop(context, Blocks.GRASS_BLOCK, pickaxe(context, 7, 1), Items.GRASS_BLOCK);
        assertDrop(context, Blocks.INFESTED_STONE, pickaxe(context, 6, 1), Items.STONE);
        assertDrop(context, Blocks.INFESTED_STONE, pickaxe(context, 7, 1), Items.INFESTED_STONE);
        context.succeed();
    }

    @GameTest
    public void compatibilityAndExperience(GameTestHelper context) {
        Holder<Enchantment> advanced = enchantment(context, AdvancedSilkTouch.KEY);
        context.assertTrue(Enchantment.areCompatible(advanced, enchantment(context, Enchantments.SILK_TOUCH)), "Silk Touch should be compatible");
        context.assertFalse(Enchantment.areCompatible(advanced, enchantment(context, Enchantments.FORTUNE)), "Fortune should be incompatible");
        context.assertFalse(Enchantment.areCompatible(enchantment(context, Enchantments.FORTUNE), advanced), "Fortune should be incompatible (reversed)");
        // A spawner drops experience. Without this, a player could break and place one spawner for unlimited experience.
        int experience = EnchantmentHelper.processBlockExperience(context.getLevel(), pickaxe(context, 10, 0), 20);
        context.assertValueEqual(experience, 0, "block experience");
        context.succeed();
    }

    @GameTest
    public void suspiciousSandKeepsItsLoot(GameTestHelper context) {
        context.setBlock(POS, Blocks.SUSPICIOUS_SAND);
        BrushableBlockEntity sand = (BrushableBlockEntity) context.getLevel().getBlockEntity(context.absolutePos(POS));
        sand.setLootTable(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY, 42L);

        context.assertTrue(drops(context, pickaxe(context, 2, 1)).isEmpty(), "level II should not collect suspicious sand");

        List<ItemStack> drops = drops(context, pickaxe(context, 3, 0));
        context.assertValueEqual(drops.size(), 1, "number of drops");
        ItemStack stack = drops.getFirst();
        context.assertValueEqual(stack.getItem(), Items.SUSPICIOUS_SAND, "drop");
        TypedEntityData<BlockEntityType<?>> data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        context.assertTrue(data != null && data.contains("LootTable"), "the drop should keep the loot table");
        context.succeed();
    }

    @GameTest
    public void aPlayerCanMoveASpawner(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        context.setBlock(POS, Blocks.SPAWNER);
        SpawnerBlockEntity spawner = (SpawnerBlockEntity) level.getBlockEntity(context.absolutePos(POS));
        spawner.setEntityId(EntityTypes.ZOMBIE, level.getRandom());

        context.assertTrue(drops(context, pickaxe(context, 9, 1)).isEmpty(), "level IX should not collect a spawner");

        List<ItemStack> drops = drops(context, pickaxe(context, 10, 0));
        context.assertValueEqual(drops.size(), 1, "number of drops");
        ItemStack stack = drops.getFirst();
        context.assertValueEqual(stack.getItem(), Items.SPAWNER, "drop");

        // A survival player is not an operator, so vanilla does not load the data.
        ServerPlayer player = player(context);
        context.setBlock(OTHER_POS, Blocks.SPAWNER);
        BlockPos placed = context.absolutePos(OTHER_POS);
        context.assertTrue(BlockItem.updateCustomBlockEntityTag(level, player, placed, stack), "the placed spawner should get the data");

        SpawnerBlockEntity placedSpawner = (SpawnerBlockEntity) level.getBlockEntity(placed);
        Optional<EntityType<?>> entity = AdvancedSilkTouch.spawnerEntity(placedSpawner.saveCustomOnly(level.registryAccess()));
        context.assertValueEqual(entity, Optional.of(EntityTypes.ZOMBIE), "spawner entity");
        context.succeed();
    }

    @GameTest
    public void levelXXXBreaksBedrock(GameTestHelper context) {
        context.setBlock(POS, Blocks.BEDROCK);
        BlockPos pos = context.absolutePos(POS);
        ServerPlayer player = player(context);

        player.getInventory().setSelectedItem(pickaxe(context, 29, 1));
        float progress = context.getLevel().getBlockState(pos).getDestroyProgress(player, context.getLevel(), pos);
        context.assertValueEqual(progress, 0.0F, "progress with level XXIX");

        player.getInventory().setSelectedItem(pickaxe(context, 30, 0));
        progress = context.getLevel().getBlockState(pos).getDestroyProgress(player, context.getLevel(), pos);
        context.assertTrue(progress > 0.0F, "level XXX should break bedrock");
        context.succeed();
    }

    @GameTest
    public void someMasterLibrariansSellLevelXToXV(GameTestHelper context) {
        ResourceKey<VillagerTrade> key = ResourceKey.create(
            Registries.VILLAGER_TRADE,
            Identifier.parse("enchantingfixes:librarian/5/emerald_and_book_advanced_silk_touch")
        );
        VillagerTrade trade = context.getLevel().registryAccess().lookupOrThrow(Registries.VILLAGER_TRADE).getOrThrow(key).value();
        Villager villager = context.spawn(EntityTypes.VILLAGER, POS);
        LootContext lootContext = new LootContext.Builder(
            new LootParams.Builder(context.getLevel())
                .withParameter(LootContextParams.ORIGIN, villager.position())
                .withParameter(LootContextParams.THIS_ENTITY, villager)
                .withParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED, Unit.INSTANCE)
                .create(LootContextParamSets.VILLAGER_TRADE)
        ).create(Optional.empty());

        Holder<Enchantment> advanced = enchantment(context, AdvancedSilkTouch.KEY);
        int offers = 0;
        for (int i = 0; i < 1000; i++) {
            MerchantOffer offer = trade.getOffer(lootContext);
            if (offer == null) {
                continue;
            }

            offers++;
            int level = offer.getResult().getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(advanced);
            context.assertTrue(level >= 10 && level <= 15, "the book level " + level + " should be from X to XV");
        }

        // The chance is 5%. 1000 tries give about 50 offers.
        context.assertTrue(offers > 20 && offers < 100, "offers: " + offers);
        villager.discard();
        context.succeed();
    }
}
