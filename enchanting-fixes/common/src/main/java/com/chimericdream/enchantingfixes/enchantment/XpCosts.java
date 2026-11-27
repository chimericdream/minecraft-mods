package com.chimericdream.enchantingfixes.enchantment;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Level costs in XP points. In vanilla, a cost of N levels removes the top N levels of the player. Each level
 * needs more XP than the level before it, so the same cost takes much more XP at level 100 than at level 30.
 * With this setting, a cost of N levels takes the XP that a player needs to go from level 0 to level N.
 *
 * <p>The player must still have N levels. A player with N levels always has that XP, so the check before the
 * cost does not change.
 */
public final class XpCosts {
    /** Above this level, the total XP does not fit in a {@code long}. */
    private static final int MAX_LEVEL = 1_000_000_000;

    /** A level and the XP points that the player has in that level. */
    public record Experience(int level, int points) {
    }

    private XpCosts() {
    }

    /** The XP from {@code level} to the next level. The same as {@code Player.getXpNeededForNextLevel}. */
    public static long pointsForNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9L;
        }

        if (level >= 15) {
            return 37 + (level - 15) * 5L;
        }

        return 7 + level * 2L;
    }

    /** The total XP from level 0 to {@code level}. */
    public static long pointsToReach(int level) {
        long l = Math.clamp(level, 0, MAX_LEVEL);
        if (l <= 16) {
            return l * l + 6 * l;
        }

        if (l <= 31) {
            return (5 * l * l - 81 * l + 720) / 2;
        }

        return (9 * l * l - 325 * l + 4440) / 2;
    }

    /**
     * The experience after a cost of {@code levels} levels. If the player does not have enough XP, the result
     * is level 0.
     *
     * @param level  the level of the player
     * @param points the XP points that the player has in that level
     * @param levels the cost in levels
     */
    public static Experience afterCost(int level, int points, int levels) {
        long cost = pointsToReach(levels);
        long available = points;
        while (cost > available) {
            if (level <= 0) {
                return new Experience(0, 0);
            }

            cost -= available;
            level--;
            available = pointsForNextLevel(level);
        }

        return new Experience(level, (int) (available - cost));
    }

    /** Takes the XP for a cost of {@code levels} levels from the player. */
    public static void pay(Player player, int levels) {
        int points = Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
        Experience after = afterCost(player.experienceLevel, points, levels);
        if (player instanceof ServerPlayer serverPlayer) {
            // These methods also send the new values to the client. Set the level first: the progress uses it.
            serverPlayer.setExperienceLevels(after.level());
            serverPlayer.setExperiencePoints(after.points());
        } else {
            player.experienceLevel = after.level();
            player.experienceProgress = (float) after.points() / player.getXpNeededForNextLevel();
        }
    }
}
