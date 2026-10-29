package com.chimericdream.forwardingaddress.fabric.test;

import com.chimericdream.forwardingaddress.fabric.test.fixture.FixtureContainerEntity;
import com.chimericdream.forwardingaddress.fabric.test.fixture.TestFixtures;
import com.chimericdream.forwardingaddress.migration.MigrationEvents;
import com.chimericdream.forwardingaddress.migration.RetiredItems;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Item conversion: stacks directly, then through the places items live (containers in the world, the
 * player's inventory and ender chest, and dropped items).
 */
@SuppressWarnings("unused")
public class ItemConversionGameTest {
    private static final BlockPos POS = new BlockPos(2, 2, 2);
    private static final double SEARCH_RANGE = 3.0;

    @GameTest
    public void aStackConvertsKeepingItsCountAndComponents(GameTestHelper context) {
        ItemStack old = new ItemStack(TestFixtures.OLD_ITEM.get(), 7);
        old.set(DataComponents.CUSTOM_NAME, Component.literal("Treasured"));

        ItemStack converted = MigrationEvents.items().convert(old);

        if (!converted.is(TestFixtures.NEW_ITEM.get())) {
            context.fail("expected the new item but found " + converted);
        }
        if (converted.getCount() != 7) {
            context.fail("the count should carry over, but it is " + converted.getCount());
        }
        if (!Component.literal("Treasured").equals(converted.get(DataComponents.CUSTOM_NAME))) {
            context.fail("the custom name should carry over, but it is " + converted.get(DataComponents.CUSTOM_NAME));
        }

        context.succeed();
    }

    @GameTest
    public void aStackWithNoMigrationIsReturnedAsIs(GameTestHelper context) {
        ItemStack stack = new ItemStack(TestFixtures.NEW_ITEM.get(), 3);

        if (MigrationEvents.items().convert(stack) != stack) {
            context.fail("a stack with no migration should be returned unchanged");
        }

        context.succeed();
    }

    @GameTest
    public void aMigrationWhoseTargetIsNotInstalledLeavesTheStack(GameTestHelper context) {
        ItemStack stack = new ItemStack(TestFixtures.ORPHAN_ITEM.get(), 3);

        if (MigrationEvents.items().convert(stack) != stack) {
            context.fail("a stack whose target is missing should be returned unchanged");
        }

        context.succeed();
    }

    @GameTest
    public void aBlocksItemConvertsWithTheBlock(GameTestHelper context) {
        ItemStack stack = new ItemStack(TestFixtures.OLD_BLOCK.get().asItem(), 4);

        ItemStack converted = MigrationEvents.items().convert(stack);

        if (!converted.is(TestFixtures.NEW_BLOCK.get().asItem()) || converted.getCount() != 4) {
            context.fail("the block's item should convert with the block, but found " + converted);
        }

        context.succeed();
    }

    @GameTest
    public void itemsInAWorldContainerConvert(GameTestHelper context) {
        context.setBlock(POS, TestFixtures.PLAIN_CHEST.get().defaultBlockState());
        FixtureContainerEntity chest = (FixtureContainerEntity) context.getLevel().getBlockEntity(context.absolutePos(POS));
        chest.setItem(0, new ItemStack(TestFixtures.OLD_ITEM.get(), 2));
        chest.setItem(2, new ItemStack(TestFixtures.NEW_ITEM.get(), 5));

        MigrationEvents.convertAt(context.getLevel(), context.absolutePos(POS));

        if (!chest.getItem(0).is(TestFixtures.NEW_ITEM.get()) || chest.getItem(0).getCount() != 2) {
            context.fail("the old item should convert in place, but slots are " + chest.getItems());
        }
        if (!chest.getItem(2).is(TestFixtures.NEW_ITEM.get()) || chest.getItem(2).getCount() != 5) {
            context.fail("items that need no conversion should be untouched, but slots are " + chest.getItems());
        }

        context.assertBlockPresent(TestFixtures.PLAIN_CHEST.get(), POS);
        context.succeed();
    }

    @GameTest
    public void aPlayersInventoryAndEnderChestConvert(GameTestHelper context) {
        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
        player.getInventory().setItem(4, new ItemStack(TestFixtures.OLD_ITEM.get(), 9));
        player.getEnderChestInventory().setItem(10, new ItemStack(TestFixtures.OLD_ITEM.get(), 3));

        MigrationEvents.convertPlayerItems(player);

        if (!player.getInventory().getItem(4).is(TestFixtures.NEW_ITEM.get()) || player.getInventory().getItem(4).getCount() != 9) {
            context.fail("the inventory stack should convert, but it is " + player.getInventory().getItem(4));
        }
        if (!player.getEnderChestInventory().getItem(10).is(TestFixtures.NEW_ITEM.get())) {
            context.fail("the ender chest stack should convert, but it is " + player.getEnderChestInventory().getItem(10));
        }

        context.succeed();
    }

    @GameTest
    public void aDroppedItemConvertsWhenItIsAdded(GameTestHelper context) {
        ItemEntity dropped = context.spawnItem(TestFixtures.OLD_ITEM.get(), POS);

        context.assertItemEntityCountIs(TestFixtures.OLD_ITEM.get(), POS, SEARCH_RANGE, 0);
        context.assertItemEntityCountIs(TestFixtures.NEW_ITEM.get(), POS, SEARCH_RANGE, 1);
        context.succeed();
    }

    /**
     * Entities read from chunk storage reach the level through {@code addLegacyChunkEntities}, not the
     * spawn path, and some loader events (Architectury's Fabric {@code EntityEvent.ADD}) miss it.
     */
    @GameTest
    public void aDroppedItemConvertsWhenLoadedFromDisk(GameTestHelper context) {
        BlockPos pos = context.absolutePos(POS);
        ItemEntity dropped = new ItemEntity(context.getLevel(), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, new ItemStack(TestFixtures.OLD_ITEM.get(), 3));

        context.getLevel().addLegacyChunkEntities(Stream.of(dropped));

        if (!dropped.getItem().is(TestFixtures.NEW_ITEM.get()) || dropped.getItem().getCount() != 3) {
            context.fail("a dropped item loaded from disk should convert, but it holds " + dropped.getItem());
        }

        context.succeed();
    }

    @GameTest
    public void anItemFrameConvertsWhenLoadedFromDisk(GameTestHelper context) {
        BlockPos pos = context.absolutePos(POS);
        ItemFrame frame = new ItemFrame(context.getLevel(), pos, Direction.NORTH);
        frame.setItem(new ItemStack(TestFixtures.OLD_ITEM.get()), false);

        context.getLevel().addLegacyChunkEntities(Stream.of(frame));

        if (!frame.getItem().is(TestFixtures.NEW_ITEM.get())) {
            context.fail("an item frame loaded from disk should convert, but it holds " + frame.getItem());
        }

        context.succeed();
    }

    @GameTest
    public void aRetiredItemGetsATooltipAndIsHiddenFromCreativeTabs(GameTestHelper context) {
        ItemStack retired = new ItemStack(TestFixtures.OLD_ITEM.get());
        List<Component> lines = new ArrayList<>();
        RetiredItems.appendTooltip(retired, lines);

        if (lines.size() != 1) {
            context.fail("a retired item should get one tooltip line, but got " + lines);
        }
        if (!RetiredItems.isRetired(retired)) {
            context.fail("the old item should count as retired");
        }

        ItemStack current = new ItemStack(TestFixtures.NEW_ITEM.get());
        List<Component> none = new ArrayList<>();
        RetiredItems.appendTooltip(current, none);
        if (!none.isEmpty() || RetiredItems.isRetired(current)) {
            context.fail("the replacement item is not retired and should get no tooltip");
        }

        ItemStack orphan = new ItemStack(TestFixtures.ORPHAN_ITEM.get());
        if (RetiredItems.isRetired(orphan)) {
            context.fail("an item whose target is not installed is not retired, so it stays visible");
        }

        context.succeed();
    }

    @GameTest
    public void armorAndOffhandConvertOnLogin(GameTestHelper context) {
        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TestFixtures.OLD_ITEM.get()));
        player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(TestFixtures.OLD_ITEM.get(), 2));

        MigrationEvents.convertPlayerItems(player);

        if (!player.getItemBySlot(EquipmentSlot.HEAD).is(TestFixtures.NEW_ITEM.get())) {
            context.fail("the helmet slot should convert, but it holds " + player.getItemBySlot(EquipmentSlot.HEAD));
        }
        if (!player.getItemBySlot(EquipmentSlot.OFFHAND).is(TestFixtures.NEW_ITEM.get())) {
            context.fail("the offhand should convert, but it holds " + player.getItemBySlot(EquipmentSlot.OFFHAND));
        }

        context.succeed();
    }
}
