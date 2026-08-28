package com.chimericdream.lib.testkit.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Reusable mock-player helpers for GameTests that need to simulate a real interaction rather than
 * calling a block/item method directly. {@link GameTestHelper#useBlock} only drives
 * {@code BlockState#useItemOn} / {@code ItemStack#useOn} — items that instead override the general
 * {@code Item#use(Level, Player, InteractionHand)} dispatch (buckets doing their own reach-limited
 * raycast are the common case, but not the only one) need a differently-positioned mock player and a
 * manual apply-the-result step, which is what this class provides.
 */
public final class GameTestPlayers {
    private GameTestPlayers() {
    }

    /**
     * Creates a mock server player positioned at {@code playerRelativePos} and oriented to look
     * directly at {@code targetRelativePos}, both relative to the test structure. Useful before a
     * reach-limited raycast (e.g. {@code Item#use}) that needs the player actually facing the block
     * under test.
     */
    public static Player makeFacingPlayer(
        GameTestHelper context, GameType gameType, BlockPos playerRelativePos, BlockPos targetRelativePos
    ) {
        Player player = context.makeMockServerPlayer(gameType);
        Vec3 playerPos = Vec3.atBottomCenterOf(context.absolutePos(playerRelativePos));
        player.setPos(playerPos.x, playerPos.y, playerPos.z);
        lookAt(player, Vec3.atCenterOf(context.absolutePos(targetRelativePos)));

        return player;
    }

    /**
     * Orients {@code player} to look at {@code target} (an absolute position). {@code Entity#lookAt}
     * would normally do this, but {@code ServerPlayer} overrides it to also send a look-rotation packet
     * to the client connection — which throws a {@code NullPointerException} on a mock player, since it
     * has no real connection. This is the same underlying math, just without the network send.
     */
    public static void lookAt(Player player, Vec3 target) {
        Vec3 from = player.getEyePosition();
        double xd = target.x - from.x;
        double yd = target.y - from.y;
        double zd = target.z - from.z;
        double horizontalDistance = Math.sqrt(xd * xd + zd * zd);
        player.setXRot(Mth.wrapDegrees((float) (-(Mth.atan2(yd, horizontalDistance) * 180.0F / (float) Math.PI))));
        player.setYRot(Mth.wrapDegrees((float) (Mth.atan2(zd, xd) * 180.0F / (float) Math.PI) - 90.0F));
    }

    /**
     * Calls {@code Item#use} for {@code player}'s held item in {@code hand} and, if it succeeds,
     * applies the resulting {@link InteractionResult.Success#heldItemTransformedTo()} to that hand —
     * the real interaction manager's job on an actual server, which nothing in a GameTest does for you.
     */
    public static InteractionResult useItem(Level level, Player player, InteractionHand hand) {
        InteractionResult result = player.getItemInHand(hand).getItem().use(level, player, hand);
        if (result instanceof InteractionResult.Success success) {
            player.setItemInHand(hand, success.heldItemTransformedTo());
        }

        return result;
    }
}
