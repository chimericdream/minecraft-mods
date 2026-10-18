package com.chimericdream.villagertweaks.fabric.test;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Coverage for {@code VTVillagerTradePreviewMixin}: future levels are rolled when a player starts trading,
 * handed out unchanged at level-up, and kept across a save/load.
 */
@SuppressWarnings({"unused", "unchecked"})
public class TradePreviewGameTest {
    private static final BlockPos VILLAGER_POS = new BlockPos(2, 2, 2);

    private static Villager spawnVillager(GameTestHelper context, int level) {
        Villager villager = context.spawnWithNoFreeWill(EntityTypes.VILLAGER, VILLAGER_POS);
        villager.setVillagerData(
            villager.getVillagerData()
                .withProfession(context.getLevel().registryAccess(), VillagerProfession.FARMER)
                .withLevel(level)
        );

        return villager;
    }

    private static Map<Integer, MerchantOffers> futureOffers(Villager villager) throws ReflectiveOperationException {
        Field field = Villager.class.getDeclaredField("vt$futureOffers");
        field.setAccessible(true);

        return (Map<Integer, MerchantOffers>) field.get(villager);
    }

    /** What a player does to start trading: right-click with an empty hand. */
    private static void startTrading(Villager villager, ServerPlayer player) {
        villager.mobInteract(player, InteractionHand.MAIN_HAND);
        villager.setTradingPlayer(null);
    }

    private static void levelUp(Villager villager, ServerLevel level, int newLevel) throws ReflectiveOperationException {
        villager.setVillagerData(villager.getVillagerData().withLevel(newLevel));

        Method updateTrades = Villager.class.getDeclaredMethod("updateTrades", ServerLevel.class);
        updateTrades.setAccessible(true);
        updateTrades.invoke(villager, level);
    }

    private static void withPreview(boolean enabled, Runnable body) {
        VillagerTweaksConfig config = VillagerTweaksConfig.CONFIG.instance();
        boolean previous = config.enableTradePreview;
        config.enableTradePreview = enabled;

        try {
            body.run();
        } finally {
            config.enableTradePreview = previous;
        }
    }

    private static List<ItemStack> results(MerchantOffers offers) {
        List<ItemStack> results = new ArrayList<>();
        for (MerchantOffer offer : offers) {
            results.add(offer.getResult().copy());
        }

        return results;
    }

    /** A novice villager gets every level above it rolled, and level-up then hands out exactly those offers. */
    @GameTest
    public void futureLevelsAreRolledAndUsedAtLevelUp(GameTestHelper context) {
        withPreview(true, () -> {
            try {
                Villager villager = spawnVillager(context, 1);
                ServerPlayer player = context.makeMockServerPlayerInLevel();

                startTrading(villager, player);

                Map<Integer, MerchantOffers> future = futureOffers(villager);
                if (!future.keySet().equals(java.util.Set.of(2, 3, 4, 5))) {
                    context.fail("a novice villager should have levels 2-5 rolled, got " + future.keySet());
                    return;
                }

                int levelOneCount = villager.getOffers().size();
                List<ItemStack> expected = results(future.get(2));
                if (expected.isEmpty()) {
                    context.fail("level 2 should have rolled at least one offer");
                    return;
                }

                levelUp(villager, context.getLevel(), 2);

                List<ItemStack> actual = results(villager.getOffers()).subList(levelOneCount, villager.getOffers().size());
                if (actual.size() != expected.size()) {
                    context.fail("level-up should add the " + expected.size() + " rolled offers, added " + actual.size());
                    return;
                }

                for (int i = 0; i < expected.size(); i++) {
                    if (!ItemStack.matches(expected.get(i), actual.get(i))) {
                        context.fail("level-up offer " + i + " was " + actual.get(i) + ", rolled " + expected.get(i));
                        return;
                    }
                }

                if (future.containsKey(2)) {
                    context.fail("level 2 should be consumed once the villager reaches it");
                    return;
                }
            } catch (ReflectiveOperationException e) {
                context.fail("reflection failed: " + e);
                return;
            }
        });

        context.succeed();
    }

    /**
     * A villager from before the mod: already traded up to level 3, so it only gets levels 4 and 5 filled
     * in and the offers it already has are left alone.
     */
    @GameTest
    public void existingVillagerOnlyGetsLevelsAboveItsOwn(GameTestHelper context) {
        withPreview(true, () -> {
            try {
                Villager villager = spawnVillager(context, 3);
                ServerPlayer player = context.makeMockServerPlayerInLevel();

                // Give it offers up front, the way a saved villager from before the mod would have them.
                // (A villager with no offers won't start trading at all.)
                MerchantOffers existing = new MerchantOffers();
                existing.add(new MerchantOffer(new ItemCost(Items.WHEAT, 20), new ItemStack(Items.EMERALD), 16, 2, 0.05f));
                villager.setOffers(existing);
                int existingCount = villager.getOffers().size();

                startTrading(villager, player);

                Map<Integer, MerchantOffers> future = futureOffers(villager);
                if (!future.keySet().equals(java.util.Set.of(4, 5))) {
                    context.fail("a level 3 villager should have levels 4-5 rolled, got " + future.keySet());
                    return;
                }

                if (villager.getOffers().size() != existingCount) {
                    context.fail("the villager's current offers should be untouched, " + existingCount + " became " + villager.getOffers().size());
                    return;
                }
            } catch (ReflectiveOperationException e) {
                context.fail("reflection failed: " + e);
                return;
            }
        });

        context.succeed();
    }

    /** A master villager has nothing above it to preview. */
    @GameTest
    public void masterVillagerRollsNothing(GameTestHelper context) {
        withPreview(true, () -> {
            try {
                Villager villager = spawnVillager(context, VillagerData.MAX_VILLAGER_LEVEL);
                ServerPlayer player = context.makeMockServerPlayerInLevel();

                startTrading(villager, player);

                if (!futureOffers(villager).isEmpty()) {
                    context.fail("a master villager should have no future levels, got " + futureOffers(villager).keySet());
                    return;
                }
            } catch (ReflectiveOperationException e) {
                context.fail("reflection failed: " + e);
                return;
            }
        });

        context.succeed();
    }

    /** With the option off nothing is rolled ahead of time, so levelling up behaves like vanilla. */
    @GameTest
    public void nothingIsRolledWhenDisabled(GameTestHelper context) {
        withPreview(false, () -> {
            try {
                Villager villager = spawnVillager(context, 1);
                ServerPlayer player = context.makeMockServerPlayerInLevel();

                startTrading(villager, player);

                if (!futureOffers(villager).isEmpty()) {
                    context.fail("nothing should be rolled with trade preview off, got " + futureOffers(villager).keySet());
                    return;
                }
            } catch (ReflectiveOperationException e) {
                context.fail("reflection failed: " + e);
                return;
            }
        });

        context.succeed();
    }

    /** Switching profession throws away the old profession's rolled trades. */
    @GameTest
    public void changingProfessionDropsRolledTrades(GameTestHelper context) {
        withPreview(true, () -> {
            try {
                Villager villager = spawnVillager(context, 1);
                ServerPlayer player = context.makeMockServerPlayerInLevel();

                startTrading(villager, player);
                if (futureOffers(villager).isEmpty()) {
                    context.fail("expected trades to be rolled before changing profession");
                    return;
                }

                villager.setVillagerData(
                    villager.getVillagerData().withProfession(context.getLevel().registryAccess(), VillagerProfession.LIBRARIAN)
                );

                if (!futureOffers(villager).isEmpty()) {
                    context.fail("rolled trades should be dropped on a profession change, got " + futureOffers(villager).keySet());
                    return;
                }
            } catch (ReflectiveOperationException e) {
                context.fail("reflection failed: " + e);
                return;
            }
        });

        context.succeed();
    }

    /**
     * Vanilla holds a level-up back until nobody is trading. With previews on it happens during the trade
     * (after vanilla's 40 tick delay), using the offers that were previewed.
     */
    @GameTest
    public void levelUpHappensWhileTrading(GameTestHelper context) {
        withPreview(true, () -> {
            try {
                Villager villager = spawnVillager(context, 1);
                ServerPlayer player = context.makeMockServerPlayerInLevel();

                villager.mobInteract(player, InteractionHand.MAIN_HAND);
                if (!villager.isTrading()) {
                    context.fail("the villager should be trading with the player");
                    return;
                }

                int levelOneCount = villager.getOffers().size();
                List<ItemStack> expected = results(futureOffers(villager).get(2));

                // Enough experience to level up, then a trade to trigger it.
                villager.setVillagerXp(VillagerData.getMaxXpPerLevel(1));
                villager.notifyTrade(villager.getOffers().get(0));

                Method aiStep = Villager.class.getDeclaredMethod("customServerAiStep", ServerLevel.class);
                aiStep.setAccessible(true);

                for (int tick = 0; tick < 39; tick++) {
                    aiStep.invoke(villager, context.getLevel());
                }

                if (villager.getVillagerData().level() != 1) {
                    context.fail("the level-up should wait out its 40 tick delay, but the villager is already level "
                        + villager.getVillagerData().level());
                    return;
                }

                aiStep.invoke(villager, context.getLevel());

                if (villager.getVillagerData().level() != 2) {
                    context.fail("the villager should have levelled up mid-trade, but is level " + villager.getVillagerData().level());
                    return;
                }

                if (!villager.isTrading()) {
                    context.fail("levelling up should not end the trade");
                    return;
                }

                List<ItemStack> added = results(villager.getOffers()).subList(levelOneCount, villager.getOffers().size());
                if (added.size() != expected.size()) {
                    context.fail("expected the " + expected.size() + " previewed offers, got " + added.size());
                    return;
                }

                villager.setTradingPlayer(null);
            } catch (ReflectiveOperationException e) {
                context.fail("reflection failed: " + e);
                return;
            }
        });

        context.succeed();
    }

    /** With the option off the level-up waits for the screen to close, as in vanilla. */
    @GameTest
    public void levelUpWaitsForTradeToEndWhenDisabled(GameTestHelper context) {
        withPreview(false, () -> {
            try {
                Villager villager = spawnVillager(context, 1);
                ServerPlayer player = context.makeMockServerPlayerInLevel();

                villager.mobInteract(player, InteractionHand.MAIN_HAND);
                villager.setVillagerXp(VillagerData.getMaxXpPerLevel(1));
                villager.notifyTrade(villager.getOffers().get(0));

                Method aiStep = Villager.class.getDeclaredMethod("customServerAiStep", ServerLevel.class);
                aiStep.setAccessible(true);

                for (int tick = 0; tick < 60; tick++) {
                    aiStep.invoke(villager, context.getLevel());
                }

                if (villager.getVillagerData().level() != 1) {
                    context.fail("with trade preview off the villager should wait for the trade to end, but is level "
                        + villager.getVillagerData().level());
                    return;
                }

                villager.setTradingPlayer(null);
            } catch (ReflectiveOperationException e) {
                context.fail("reflection failed: " + e);
                return;
            }
        });

        context.succeed();
    }

    /** Rolled trades survive being saved and loaded, which is also what bagging a villager does. */
    @GameTest
    public void rolledTradesSurviveSaveAndLoad(GameTestHelper context) {
        withPreview(true, () -> {
            try {
                Villager villager = spawnVillager(context, 1);
                ServerPlayer player = context.makeMockServerPlayerInLevel();

                startTrading(villager, player);
                Map<Integer, MerchantOffers> before = futureOffers(villager);

                TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, context.getLevel().registryAccess());
                villager.saveWithoutId(output);
                CompoundTag saved = output.buildResult();

                if (!saved.contains("VTFutureTrades")) {
                    context.fail("the saved villager should include VTFutureTrades");
                    return;
                }

                Villager loaded = context.spawnWithNoFreeWill(EntityTypes.VILLAGER, VILLAGER_POS.east());
                loaded.load(TagValueInput.create(ProblemReporter.DISCARDING, context.getLevel().registryAccess(), saved));
                Map<Integer, MerchantOffers> after = futureOffers(loaded);

                if (!after.keySet().equals(before.keySet())) {
                    context.fail("loaded levels " + after.keySet() + " should match saved " + before.keySet());
                    return;
                }

                for (Map.Entry<Integer, MerchantOffers> entry : before.entrySet()) {
                    List<ItemStack> expected = results(entry.getValue());
                    List<ItemStack> actual = results(after.get(entry.getKey()));

                    if (expected.size() != actual.size()) {
                        context.fail("level " + entry.getKey() + " had " + expected.size() + " offers, loaded " + actual.size());
                        return;
                    }

                    for (int i = 0; i < expected.size(); i++) {
                        if (!ItemStack.matches(expected.get(i), actual.get(i))) {
                            context.fail("level " + entry.getKey() + " offer " + i + " changed across save/load");
                            return;
                        }
                    }
                }
            } catch (ReflectiveOperationException e) {
                context.fail("reflection failed: " + e);
                return;
            }
        });

        context.succeed();
    }
}
