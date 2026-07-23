package com.chimericdream.villagertweaks.fabric.test;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.villager.Villager;

/**
 * Coverage for {@code VTVillagerEntityMixin}'s reputation overrides.
 *
 * <p>The mod files every reputation <em>event</em> under a shared GLOBAL_UUID when global reputation
 * is enabled, but the <em>read</em> side used to bail out early whenever bad reputation was also
 * enabled (its default). With both options on, writes went to GLOBAL_UUID and reads went to the
 * player's own UUID, so global reputation quietly did nothing.
 */
@SuppressWarnings("unused")
public class GlobalReputationGameTest {
    private static final BlockPos VILLAGER_POS = new BlockPos(2, 2, 2);

    /**
     * Runs {@code body} with the two reputation options forced on/off, then restores the config —
     * GameTests share one process, so leaking config state would bleed into other tests.
     */
    private static void withConfig(boolean globalReputation, boolean badReputation, Runnable body) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();
        boolean previousGlobal = config.enableGlobalReputation;
        boolean previousBad = config.enableBadReputation;

        config.enableGlobalReputation = globalReputation;
        config.enableBadReputation = badReputation;

        try {
            body.run();
        } finally {
            config.enableGlobalReputation = previousGlobal;
            config.enableBadReputation = previousBad;
        }
    }

    /**
     * The 2.3 regression, with both options at their "on" setting: one player cures the villager and
     * a completely different player must see that reputation. Before the fix the read returned 0
     * because it looked up the second player's own UUID.
     */
    @GameTest
    public void globalReputationIsSharedWhenBadReputationIsAlsoEnabled(GameTestHelper context) {
        withConfig(true, true, () -> {
            Villager villager = context.spawnWithNoFreeWill(EntityTypes.VILLAGER, VILLAGER_POS);
            ServerPlayer curer = context.makeMockServerPlayerInLevel();
            ServerPlayer bystander = context.makeMockServerPlayerInLevel();

            villager.onReputationEventFrom(ReputationEventType.ZOMBIE_VILLAGER_CURED, curer);

            int curerReputation = villager.getPlayerReputation(curer);
            int bystanderReputation = villager.getPlayerReputation(bystander);

            if (curerReputation <= 0) {
                context.fail("curing the villager should give the curer positive reputation, got " + curerReputation);
            }

            if (bystanderReputation != curerReputation) {
                context.fail(
                    "global reputation should be shared across players, but the curer saw "
                        + curerReputation + " and a bystander saw " + bystanderReputation
                );
            }
        });

        context.succeed();
    }

    /**
     * With bad reputation enabled, negative gossip must still count globally — the read predicate
     * has to match vanilla's "every gossip type", not the filtered one.
     */
    @GameTest
    public void badReputationStillCountsWhenGlobalReputationIsEnabled(GameTestHelper context) {
        withConfig(true, true, () -> {
            Villager villager = context.spawnWithNoFreeWill(EntityTypes.VILLAGER, VILLAGER_POS);
            ServerPlayer attacker = context.makeMockServerPlayerInLevel();

            villager.onReputationEventFrom(ReputationEventType.VILLAGER_HURT, attacker);

            int reputation = villager.getPlayerReputation(attacker);
            if (reputation >= 0) {
                context.fail("hurting the villager should give negative reputation, got " + reputation);
            }
        });

        context.succeed();
    }

    /**
     * Turning bad reputation off must keep filtering the negative gossip types — the fix must not
     * regress the option it shares a code path with.
     */
    @GameTest
    public void disablingBadReputationFiltersNegativeGossip(GameTestHelper context) {
        withConfig(true, false, () -> {
            Villager villager = context.spawnWithNoFreeWill(EntityTypes.VILLAGER, VILLAGER_POS);
            ServerPlayer attacker = context.makeMockServerPlayerInLevel();

            villager.onReputationEventFrom(ReputationEventType.VILLAGER_HURT, attacker);

            int reputation = villager.getPlayerReputation(attacker);
            if (reputation != 0) {
                context.fail("negative gossip should be ignored with bad reputation off, got " + reputation);
            }
        });

        context.succeed();
    }
}
