package com.chimericdream.effectivegear.ability;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;

// Checked in order on each "use ability" press; a player can only match one, since each armor piece carries a single trim pattern.
public final class TrimAbilities {
    public static final List<TrimAbility> ABILITIES = List.of(
        new BoltDashAbility(),
        new FlowDoubleJumpAbility()
    );

    private TrimAbilities() {
    }

    public static boolean tryUseAbility(ServerPlayer player) {
        for (TrimAbility ability : ABILITIES) {
            if (TrimSetUtils.isWearingFullPattern(player, ability.pattern()) && ability.tryActivate(player)) {
                return true;
            }
        }

        return false;
    }
}
