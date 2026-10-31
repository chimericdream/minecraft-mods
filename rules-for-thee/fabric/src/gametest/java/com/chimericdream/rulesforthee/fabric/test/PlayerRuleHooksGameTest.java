package com.chimericdream.rulesforthee.fabric.test;

import com.chimericdream.rulesforthee.data.RulesData;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Checks that the vanilla reads of a player's own game rules go through the per-player lookup: the same
 * world rule gives different answers for a player with an override and one without.
 *
 * <p>These drive the real vanilla methods (the mixin targets), not the lookup directly. Death itself isn't
 * driven end to end because a mock player has no network connection and {@code die} sends packets.
 */
@SuppressWarnings("unused")
public class PlayerRuleHooksGameTest {
    private static ServerPlayer mockPlayer(GameTestHelper context) {
        return (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
    }

    /**
     * {@code Player#isInvulnerableTo} itself, called non-virtually. {@code ServerPlayer} overrides it to
     * also read its network connection, which a mock player doesn't have, but the rule reads we wrap are in
     * the {@code Player} version.
     */
    private static final MethodHandle PLAYER_INVULNERABLE = playerInvulnerable();

    private static MethodHandle playerInvulnerable() {
        try {
            return MethodHandles.privateLookupIn(Player.class, MethodHandles.lookup()).findSpecial(
                Player.class,
                "isInvulnerableTo",
                MethodType.methodType(boolean.class, ServerLevel.class, DamageSource.class),
                Player.class
            );
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static boolean invulnerable(ServerPlayer player, DamageSource source) {
        try {
            return (boolean) PLAYER_INVULNERABLE.invoke(player, player.level(), source);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    /** Runs {@code body} with the world's {@code rule} set to {@code value}, restoring it afterwards. */
    private static <T> void withWorld(MinecraftServer server, GameRule<T> rule, T value, Runnable body) {
        GameRules rules = server.overworld().getGameRules();
        T previous = rules.get(rule);
        rules.set(rule, value, server);
        try {
            body.run();
        } finally {
            rules.set(rule, previous, server);
        }
    }

    /** One damage rule: a source of that damage and the rule that gates it. */
    private record DamageCase(String name, GameRule<Boolean> rule, Function<ServerLevel, DamageSource> source) {
    }

    @GameTest
    public void damageRulesFollowThePlayersOverride(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        MinecraftServer server = level.getServer();
        ServerPlayer player = mockPlayer(context);
        RulesData data = RulesData.get(server);

        DamageCase[] cases = {
            new DamageCase("fall", GameRules.FALL_DAMAGE, l -> l.damageSources().fall()),
            new DamageCase("drowning", GameRules.DROWNING_DAMAGE, l -> l.damageSources().drown()),
            new DamageCase("fire", GameRules.FIRE_DAMAGE, l -> l.damageSources().onFire()),
            new DamageCase("freeze", GameRules.FREEZE_DAMAGE, l -> l.damageSources().freeze()),
        };

        try {
            for (DamageCase test : cases) {
                DamageSource source = test.source().apply(level);

                // World default is that the damage applies, and nothing overrides it.
                context.assertTrue(!invulnerable(player, source), test.name() + ": damage applies by default");

                // A stricter override: the player is immune while the world rule says damage applies.
                data.entry(player.getUUID()).overrides().set(test.rule(), false);
                context.assertTrue(invulnerable(player, source), test.name() + ": override=false makes the player immune");

                // A more lenient override: with the world rule off, the player still takes damage.
                data.entry(player.getUUID()).overrides().set(test.rule(), true);
                withWorld(server, test.rule(), false, () -> context.assertTrue(
                    !invulnerable(player, source),
                    test.name() + ": override=true keeps damage on when the world has it off"
                ));

                // And someone without the override still follows the world rule.
                data.removePlayer(player.getUUID());
                withWorld(server, test.rule(), false, () -> context.assertTrue(
                    invulnerable(player, source),
                    test.name() + ": no override follows the world rule"
                ));
            }
            context.succeed();
        } finally {
            data.removePlayer(player.getUUID());
        }
    }

    private static Object callProtected(Player player, String name, ServerLevel level) throws ReflectiveOperationException {
        Method method = Player.class.getDeclaredMethod(name, ServerLevel.class);
        method.setAccessible(true);

        return method.invoke(player, level);
    }

    @GameTest
    public void keepInventoryOverrideKeepsTheInventoryOnDeath(GameTestHelper context) throws ReflectiveOperationException {
        ServerLevel level = context.getLevel();
        MinecraftServer server = level.getServer();
        ServerPlayer player = mockPlayer(context);
        RulesData data = RulesData.get(server);

        try {
            // The world has keepInventory off (the default), so a player with no override drops everything.
            withWorld(server, GameRules.KEEP_INVENTORY, false, () -> {
                player.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 3));
                try {
                    callProtected(player, "dropEquipment", level);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
                context.assertTrue(player.getInventory().getItem(0).isEmpty(), "no override: the inventory drops");
            });

            data.entry(player.getUUID()).overrides().set(GameRules.KEEP_INVENTORY, true);
            withWorld(server, GameRules.KEEP_INVENTORY, false, () -> {
                player.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 3));
                try {
                    callProtected(player, "dropEquipment", level);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
                context.assertTrue(
                    player.getInventory().getItem(0).is(Items.DIAMOND),
                    "keepInventory override: the inventory is kept while the world drops it"
                );
            });

            // The other direction: the world keeps inventories, this player does not.
            data.entry(player.getUUID()).overrides().set(GameRules.KEEP_INVENTORY, false);
            withWorld(server, GameRules.KEEP_INVENTORY, true, () -> {
                player.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 3));
                try {
                    callProtected(player, "dropEquipment", level);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
                context.assertTrue(
                    player.getInventory().getItem(0).isEmpty(),
                    "keepInventory=false override: the inventory drops while the world keeps it"
                );
            });
            context.succeed();
        } finally {
            data.removePlayer(player.getUUID());
        }
    }

    @GameTest
    public void keepInventoryOverrideKeepsTheExperience(GameTestHelper context) throws ReflectiveOperationException {
        ServerLevel level = context.getLevel();
        MinecraftServer server = level.getServer();
        ServerPlayer player = mockPlayer(context);
        RulesData data = RulesData.get(server);
        player.experienceLevel = 10;

        try {
            withWorld(server, GameRules.KEEP_INVENTORY, false, () -> {
                try {
                    context.assertTrue((int) callProtected(player, "getBaseExperienceReward", level) > 0, "no override: XP drops");
                    data.entry(player.getUUID()).overrides().set(GameRules.KEEP_INVENTORY, true);
                    context.assertTrue((int) callProtected(player, "getBaseExperienceReward", level) == 0, "override: XP is kept");
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            });
            context.succeed();
        } finally {
            data.removePlayer(player.getUUID());
        }
    }

    @GameTest
    public void respawnCopiesTheOldInventoryOnlyWithTheOverride(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        MinecraftServer server = level.getServer();
        RulesData data = RulesData.get(server);
        ServerPlayer oldPlayer = mockPlayer(context);
        ServerPlayer newPlayer = mockPlayer(context);

        try {
            // `restoreFrom` runs on the new player, so the override is looked up by the new player's UUID.
            withWorld(server, GameRules.KEEP_INVENTORY, false, () -> {
                oldPlayer.getInventory().setItem(0, new ItemStack(Items.EMERALD, 2));
                newPlayer.restoreFrom(oldPlayer, false);
                context.assertTrue(newPlayer.getInventory().getItem(0).isEmpty(), "no override: the new player starts empty");

                data.entry(newPlayer.getUUID()).overrides().set(GameRules.KEEP_INVENTORY, true);
                newPlayer.restoreFrom(oldPlayer, false);
                context.assertTrue(newPlayer.getInventory().getItem(0).is(Items.EMERALD), "override: the new player gets the inventory");
            });
            context.succeed();
        } finally {
            data.removePlayer(newPlayer.getUUID());
        }
    }

    /**
     * Whether a hit gets past the PVP checks. The victim's half is {@code ServerPlayer#isPvpAllowed}, called
     * directly: in {@code hurtServer} it comes after {@code isInvulnerableTo}, which reads the network
     * connection a mock player doesn't have. The attacker's half is checked at the top of {@code hurtServer},
     * which returns false when it blocks. A hit that isn't blocked carries on into vanilla damage code that
     * also needs the connection and throws, which counts as "not blocked".
     */
    private static boolean hitIsNotBlocked(ServerPlayer victim, ServerLevel level, DamageSource source) {
        try {
            Method method = ServerPlayer.class.getDeclaredMethod("isPvpAllowed");
            method.setAccessible(true);
            if (!(boolean) method.invoke(victim)) {
                return false;
            }

            return victim.hurtServer(level, source, 1.0F);
        } catch (NullPointerException e) {
            return true;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    @GameTest
    public void pvpNeedsBothPlayersToHaveItOn(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        MinecraftServer server = level.getServer();
        RulesData data = RulesData.get(server);
        ServerPlayer victim = mockPlayer(context);
        ServerPlayer attacker = mockPlayer(context);
        DamageSource hit = level.damageSources().playerAttack(attacker);

        try {
            withWorld(server, GameRules.PVP, true, () -> {
                context.assertTrue(hitIsNotBlocked(victim, level, hit), "no overrides: the hit lands");

                data.entry(victim.getUUID()).overrides().set(GameRules.PVP, false);
                context.assertTrue(!hitIsNotBlocked(victim, level, hit), "victim has PVP off: the hit is blocked");

                data.removePlayer(victim.getUUID());
                data.entry(attacker.getUUID()).overrides().set(GameRules.PVP, false);
                context.assertTrue(!hitIsNotBlocked(victim, level, hit), "attacker has PVP off: the hit is blocked");
            });

            data.removePlayer(attacker.getUUID());
            withWorld(server, GameRules.PVP, false, () -> {
                context.assertTrue(!hitIsNotBlocked(victim, level, hit), "world PVP off: the hit is blocked");

                data.entry(victim.getUUID()).overrides().set(GameRules.PVP, true);
                context.assertTrue(!hitIsNotBlocked(victim, level, hit), "only the victim has PVP on: still blocked");

                data.entry(attacker.getUUID()).overrides().set(GameRules.PVP, true);
                context.assertTrue(hitIsNotBlocked(victim, level, hit), "both have PVP on in a PVP-off world: the hit lands");
            });
            context.succeed();
        } finally {
            data.removePlayer(victim.getUUID());
            data.removePlayer(attacker.getUUID());
        }
    }

    @GameTest
    public void everyMixinTargetLoads(GameTestHelper context) throws ClassNotFoundException {
        // A mixin that fails to apply fails when its target class loads. Some targets aren't loaded until
        // first use, so load them all explicitly.
        for (String name : new String[]{
            "net.minecraft.world.entity.player.Player",
            "net.minecraft.server.level.ServerPlayer",
            "net.minecraft.server.PlayerAdvancements",
            "net.minecraft.world.entity.TamableAnimal",
        }) {
            Class.forName(name, true, getClass().getClassLoader());
        }
        context.succeed();
    }
}
