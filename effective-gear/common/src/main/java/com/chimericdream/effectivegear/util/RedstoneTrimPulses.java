package com.chimericdream.effectivegear.util;

import com.chimericdream.effectivegear.block.EGBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * A redstone trim pulse (see EG$ServerPlayerGameModeMixin) works by momentarily placing a real,
 * powered invisible button and letting it notify its neighbors exactly like a button press does.
 * Vanilla's own button behavior only ever un-powers itself on its scheduled tick, leaving the block
 * behind, so this tracks pulses that need to be fully removed one tick after they were placed instead.
 */
public final class RedstoneTrimPulses {
    private record Pulse(ServerLevel level, BlockPos pos, long removeAtTick) {
    }

    private static final List<Pulse> PENDING = new ArrayList<>();

    private RedstoneTrimPulses() {
    }

    public static void schedule(ServerLevel level, BlockPos pos) {
        PENDING.add(new Pulse(level, pos.immutable(), level.getGameTime() + 1));
    }

    public static void tick(ServerLevel level) {
        if (PENDING.isEmpty()) {
            return;
        }

        long now = level.getGameTime();
        PENDING.removeIf(pulse -> {
            if (pulse.level() != level || now < pulse.removeAtTick()) {
                return false;
            }

            if (level.getBlockState(pulse.pos()).is(EGBlocks.REDSTONE_TRIM_PULSE_BUTTON.get())) {
                level.removeBlock(pulse.pos(), false);
            }

            return true;
        });
    }
}
