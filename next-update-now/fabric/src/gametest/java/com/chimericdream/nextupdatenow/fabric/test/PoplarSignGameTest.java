package com.chimericdream.nextupdatenow.fabric.test;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

/**
 * Regression test for the crash fixed in this mod's main-branch 1.0.1: poplar's sign and
 * hanging-sign blocks reuse vanilla's {@code BlockEntityType.SIGN}/{@code .HANGING_SIGN} (see
 * {@code ModBlocks#registerPoplarWoodSet}), whose immutable {@code validBlocks} set is baked from
 * the vanilla block list at class-init time and can never contain these new blocks.
 * {@code ModBlockEntityValidBlocks} + {@code BlockEntityTypeMixin} extend {@code isValid} to also
 * consult a side-table these blocks register themselves into. If that wiring regresses,
 * {@code BlockEntityType#isValid} goes back to returning false for these blocks and placing one
 * either fails to create its block entity or crashes outright — exactly what shipped in 1.0.0 and
 * was fixed in 1.0.1.
 */
@SuppressWarnings("unused")
public class PoplarSignGameTest {
    private static final BlockPos SIGN_POS = new BlockPos(2, 2, 2);
    private static final BlockPos HANGING_SIGN_POS = new BlockPos(2, 3, 2);

    @GameTest
    public void poplarSignGetsAValidBlockEntity(GameTestHelper context) {
        context.setBlock(SIGN_POS.below(), Blocks.STONE);

        Block sign = BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace("poplar_sign"));
        context.setBlock(SIGN_POS, sign);

        BlockEntity be = context.getLevel().getBlockEntity(context.absolutePos(SIGN_POS));
        context.assertTrue(be instanceof SignBlockEntity, "poplar_sign should carry a valid SignBlockEntity, not null/wrong-typed");

        context.succeed();
    }

    @GameTest
    public void poplarHangingSignGetsAValidBlockEntity(GameTestHelper context) {
        context.setBlock(HANGING_SIGN_POS.above(), Blocks.STONE);

        Block hangingSign = BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace("poplar_hanging_sign"));
        context.setBlock(HANGING_SIGN_POS, hangingSign);

        BlockEntity be = context.getLevel().getBlockEntity(context.absolutePos(HANGING_SIGN_POS));
        context.assertTrue(be instanceof HangingSignBlockEntity, "poplar_hanging_sign should carry a valid HangingSignBlockEntity, not null/wrong-typed");

        context.succeed();
    }
}
