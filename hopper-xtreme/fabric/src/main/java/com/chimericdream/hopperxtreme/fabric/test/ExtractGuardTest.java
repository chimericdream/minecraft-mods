package com.chimericdream.hopperxtreme.fabric.test;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.client.screen.FilteredHopperScreenHandler;
import com.chimericdream.hopperxtreme.client.screen.GlazedHopperScreenHandler;
import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/**
 * Regression tests for CODE-REVIEW-PLAN 2.11 and 2.6.
 *
 * <p>2.11 — {@code canExtract} used to cast its {@code hopper} argument to the mod's own block entity
 * unconditionally, even though the public entry point {@code extract(Level, Hopper)} accepts any
 * vanilla {@link net.minecraft.world.level.block.entity.Hopper}. A hopper minecart is the obvious
 * such caller, and it used to blow up with a {@code ClassCastException}.
 *
 * <p>2.6 — the mod's menus called {@code startOpen} in their constructors and never {@code stopOpen}.
 */
@SuppressWarnings("unused")
public class ExtractGuardTest {
    /**
     * Drives the public extract entry point with a vanilla hopper minecart. Pre-fix this threw a
     * {@code ClassCastException} out of {@code canExtract}; now the minecart pulls from the chest.
     */
    @GameTest
    public void extractAcceptsAForeignHopper(GameTestHelper context) {
        BlockPos chestPos = new BlockPos(2, 3, 2);
        BlockPos minecartPos = new BlockPos(2, 2, 2);

        context.setBlock(chestPos, Blocks.CHEST.defaultBlockState());
        ChestBlockEntity chest = context.getBlockEntity(chestPos, ChestBlockEntity.class);
        chest.setItem(0, new ItemStack(Items.DIAMOND, 4));

        MinecartHopper minecart = context.spawn(EntityTypes.HOPPER_MINECART, minecartPos);

        boolean extracted = XtremeHopperBlockEntity.extract(context.getLevel(), minecart);

        if (!extracted) {
            context.fail(Component.literal("Expected the hopper minecart to extract from the chest above it"), minecartPos);
            return;
        }

        if (!minecart.getItem(0).is(Items.DIAMOND)) {
            context.fail(Component.literal("Expected a diamond in the minecart, got " + minecart.getItem(0)), minecartPos);
            return;
        }

        context.succeed();
    }

    /** A filtered hopper's menu must release its viewer when the screen closes. */
    @GameTest
    public void filteredHopperMenuBalancesOpenAndClose(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        OpenCountingContainer container = new OpenCountingContainer(6);

        FilteredHopperScreenHandler menu =
            new FilteredHopperScreenHandler(null, 1, player.getInventory(), container);

        container.assertOpenCount(context, 1, "opening the menu");
        menu.removed(player);
        container.assertOpenCount(context, 0, "closing the menu");

        context.succeed();
    }

    /** Same for the glazed hopper's menu. */
    @GameTest
    public void glazedHopperMenuBalancesOpenAndClose(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        OpenCountingContainer container = new OpenCountingContainer(1);

        GlazedHopperScreenHandler menu =
            new GlazedHopperScreenHandler(null, 1, player.getInventory(), container);

        container.assertOpenCount(context, 1, "opening the menu");
        menu.removed(player);
        container.assertOpenCount(context, 0, "closing the menu");

        context.succeed();
    }

    /**
     * {@code SimpleContainer}'s own {@code startOpen}/{@code stopOpen} are no-ops, so this stands in
     * for a block entity with a real opener counter and just tallies the calls.
     */
    private static class OpenCountingContainer extends SimpleContainer {
        private int openCount = 0;

        OpenCountingContainer(int size) {
            super(size);
        }

        @Override
        public void startOpen(ContainerUser user) {
            openCount++;
        }

        @Override
        public void stopOpen(ContainerUser user) {
            openCount--;
        }

        void assertOpenCount(GameTestHelper context, int expected, String what) {
            if (openCount != expected) {
                context.fail(Component.literal(
                    "Expected an open count of " + expected + " after " + what + ", got " + openCount
                ));
            }
        }
    }
}
