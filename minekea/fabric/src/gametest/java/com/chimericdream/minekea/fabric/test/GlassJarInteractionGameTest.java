package com.chimericdream.minekea.fabric.test;

import com.chimericdream.minekea.block.containers.ContainerBlocks;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import com.chimericdream.minekea.fluid.ModFluids;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Coverage for the glass jar's right-click handling (CODE-REVIEW-PLAN 2.2, 2.9, 2.10).
 *
 * <p>Bottling honey never worked: {@code getBottle()} compared the stored {@link net.minecraft.world.level.material.Fluid}
 * against {@code ModFluids.HONEY_FLUID} — the {@code RegistrySupplier} holding it, not the fluid — which
 * compiles but is never true, so the method fell through and returned {@code null}.
 *
 * <p>And {@code useItemOn} wrote its results into the main hand no matter which hand it fired for.
 */
@SuppressWarnings("unused")
public class GlassJarInteractionGameTest {
    private static final BlockPos JAR = new BlockPos(2, 2, 2);

    private static GlassJarBlockEntity placeJar(GameTestHelper context) {
        context.setBlock(JAR, ContainerBlocks.GLASS_JAR.get());
        return context.getBlockEntity(JAR, GlassJarBlockEntity.class);
    }

    private static ServerPlayer player(GameTestHelper context) {
        return (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
    }

    /** Right-clicks the jar with the given hand, the way the vanilla dispatcher would. */
    private static void useJar(GameTestHelper context, ServerPlayer player, InteractionHand hand) {
        BlockPos absolute = context.absolutePos(JAR);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false);

        context.getLevel().getBlockState(absolute).useItemOn(
            player.getItemInHand(hand), context.getLevel(), player, hand, hit
        );
    }

    /**
     * The 2.2 regression. A jar of honey plus a glass bottle must give back a honey bottle.
     */
    @GameTest
    public void bottlingHoneyGivesAHoneyBottle(GameTestHelper context) {
        GlassJarBlockEntity jar = placeJar(context);
        jar.tryInsert(ModFluids.HONEY_FLUID.get(), 1.0);

        ServerPlayer player = player(context);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE));

        useJar(context, player, InteractionHand.MAIN_HAND);

        if (!player.getMainHandItem().is(Items.HONEY_BOTTLE)) {
            context.fail("Expected a honey bottle in hand, got " + player.getMainHandItem());
        }

        if (jar.getStoredBuckets() >= 1.0) {
            context.fail("Bottling should have drawn honey out of the jar, still holding " + jar.getStoredBuckets());
        }

        context.succeed();
    }

    /** Water already worked; this guards it against the rewrite. */
    @GameTest
    public void bottlingWaterGivesAWaterBottle(GameTestHelper context) {
        GlassJarBlockEntity jar = placeJar(context);
        jar.tryInsert(Fluids.WATER, 1.0);

        ServerPlayer player = player(context);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE));

        useJar(context, player, InteractionHand.MAIN_HAND);

        if (!player.getMainHandItem().is(Items.POTION)) {
            context.fail("Expected a water bottle in hand, got " + player.getMainHandItem());
        }

        context.succeed();
    }

    /** Lava has no bottled form, so the jar must be left alone rather than silently drained. */
    @GameTest
    public void bottlingLavaLeavesTheJarUntouched(GameTestHelper context) {
        GlassJarBlockEntity jar = placeJar(context);
        jar.tryInsert(Fluids.LAVA, 1.0);

        if (jar.getBottle() != null) {
            context.fail("Lava should not produce a bottle");
        }

        if (jar.getStoredBuckets() != 1.0) {
            context.fail("A failed bottling should not drain the jar, holding " + jar.getStoredBuckets());
        }

        context.succeed();
    }

    /**
     * The 2.9 regression. Used with the off hand, the jar used to write the emptied bucket into the
     * MAIN hand — destroying whatever was there and leaving the off-hand bucket full.
     */
    @GameTest
    public void usingTheJarOffHandLeavesTheMainHandAlone(GameTestHelper context) {
        placeJar(context);

        ServerPlayer player = player(context);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND, 5));
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.WATER_BUCKET));

        useJar(context, player, InteractionHand.OFF_HAND);

        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.is(Items.DIAMOND) || mainHand.getCount() != 5) {
            context.fail("The main hand should still hold 5 diamonds, got " + mainHand);
        }

        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (!offHand.is(Items.BUCKET)) {
            context.fail("The off hand should hold the emptied bucket, got " + offHand);
        }

        context.succeed();
    }
}
