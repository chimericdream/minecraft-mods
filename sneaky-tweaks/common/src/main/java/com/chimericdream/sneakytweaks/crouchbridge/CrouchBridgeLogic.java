package com.chimericdream.sneakytweaks.crouchbridge;

import com.chimericdream.sneakytweaks.advancement.SneakyTweaksAdvancements;
import com.chimericdream.sneakytweaks.config.SneakyTweaksConfig;
import com.google.common.collect.MapMaker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

/**
 * "Wile E. Coyote" sneaking: while crouching, a player can walk out over a gap of up to
 * {@code crouchBridgeMaxGapBlocks} as though it were solid ground, as long as they keep crouching and
 * don't look down.
 * <p>
 * Two hooks make this work, both driven from a {@link Player} mixin:
 * <ul>
 *     <li>{@link #shouldAllowSteppingOffEdge} runs from {@code Player#maybeBackOffFromEdge} — vanilla's
 *     sneak-edge stop, which otherwise never lets a crouching player leave solid ground in the first
 *     place — and lets them step off when eligible.</li>
 *     <li>{@link #tick} runs at the tail of {@code LivingEntity#travel} every tick and, while airborne
 *     and still within the gap limit, pins the player's Y position back to the height they left solid
 *     ground at and cancels the fall.</li>
 * </ul>
 * State isn't persisted or synced — it's transient per-tick physics bookkeeping recomputed
 * independently by whichever side (client, for local-player prediction; server, authoritatively) is
 * currently simulating that player's movement, and is safe to lose on logout/relog (it just resets to
 * "no anchor" until the player is next standing on solid ground).
 */
public final class CrouchBridgeLogic {
    // Anchor Y snaps up to hold the player level; this tolerance keeps a stale anchor (e.g. from
    // before an uncrouched free-fall) from snapping them a long way back up once they crouch again.
    private static final double MAX_REENGAGE_DROP = 1.0;

    // Entity#equals()/hashCode() compare by entity id, and in singleplayer the client's LocalPlayer
    // and the integrated server's ServerPlayer share an id — a plain equals()-based map would treat
    // them as the same key and let the two sides silently share (and race on) one CrouchBridgeState.
    // weakKeys() forces identity comparison instead, per MapMaker's contract, keeping each side's
    // state genuinely separate.
    private static final Map<Player, CrouchBridgeState> STATES = new MapMaker().weakKeys().makeMap();

    private CrouchBridgeLogic() {
    }

    public static boolean shouldAllowSteppingOffEdge(Player player) {
        SneakyTweaksConfig config = SneakyTweaksConfig.HANDLER.instance();
        return config.enableCrouchBridging && player.isCrouching() && !isLookingDown(player, config);
    }

    public static void tick(Player player) {
        SneakyTweaksConfig config = SneakyTweaksConfig.HANDLER.instance();
        CrouchBridgeState state = STATES.computeIfAbsent(player, p -> new CrouchBridgeState());

        if (!config.enableCrouchBridging || isExemptFromBridging(player)) {
            resetState(state);
            return;
        }

        if (player.onGround()) {
            state.hasAnchor = true;
            state.fallCommitted = false;
            state.usedBridge = false;
            state.lookedDown = false;
            state.anchorX = player.getX();
            state.anchorY = player.getY();
            state.anchorZ = player.getZ();
            return;
        }

        if (!player.isCrouching()) {
            // Releasing crouch mid-air forfeits the bridge until the next real landing, even if they
            // crouch again before hitting the ground.
            state.fallCommitted = true;
            return;
        }

        if (!state.hasAnchor || state.fallCommitted) {
            return;
        }

        if (state.anchorY - player.getY() > MAX_REENGAGE_DROP) {
            state.fallCommitted = true;
            return;
        }

        double dx = player.getX() - state.anchorX;
        double dz = player.getZ() - state.anchorZ;
        double gapDistance = Math.sqrt(dx * dx + dz * dz);

        if (isLookingDown(player, config)) {
            state.fallCommitted = true;
            state.lookedDown = true;

            if (state.usedBridge && player instanceof ServerPlayer serverPlayer) {
                SneakyTweaksAdvancements.award(serverPlayer, SneakyTweaksAdvancements.LOOKED_DOWN);
            }

            return;
        }

        if (gapDistance > config.crouchBridgeMaxGapBlocks) {
            state.fallCommitted = true;
            return;
        }

        if (player.getY() < state.anchorY) {
            player.setPos(player.getX(), state.anchorY, player.getZ());
        }

        Vec3 velocity = player.getDeltaMovement();
        if (velocity.y < 0.0) {
            player.setDeltaMovement(velocity.x, 0.0, velocity.z);
        }

        player.resetFallDistance();
        state.usedBridge = true;

        if (player instanceof ServerPlayer serverPlayer) {
            SneakyTweaksAdvancements.award(serverPlayer, SneakyTweaksAdvancements.CROUCH_BRIDGE);
        }
    }

    private static void resetState(CrouchBridgeState state) {
        state.hasAnchor = false;
        state.fallCommitted = false;
        state.usedBridge = false;
        state.lookedDown = false;
    }

    private static boolean isLookingDown(Player player, SneakyTweaksConfig config) {
        return player.getXRot() >= config.crouchBridgeLookDownThreshold;
    }

    private static boolean isExemptFromBridging(Player player) {
        return player.isSpectator()
            || player.getAbilities().flying
            || player.isSwimming()
            || player.isPassenger()
            || player.isFallFlying();
    }
}
