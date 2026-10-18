package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import com.chimericdream.villagertweaks.trade.FutureTradeSet;
import com.chimericdream.villagertweaks.trade.LockedTradesPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Trade previews. Vanilla rolls a villager's offers one level at a time, from a random sequence shared by
 * every villager in the world, so what a villager will sell at level 4 isn't knowable until it gets there.
 * This rolls the levels above the villager's current one ahead of time, keeps them on the villager, and
 * hands them out at level-up instead of rolling again; the same offers are what the trading screen shows
 * as locked.
 */
@Mixin(Villager.class)
public abstract class VTVillagerTradePreviewMixin extends AbstractVillager {
    /** Offers already rolled for levels the villager hasn't reached, keyed by level. */
    @Unique
    private final Map<Integer, MerchantOffers> vt$futureOffers = new TreeMap<>();

    @Shadow
    private int updateMerchantTimer;

    @Shadow
    private boolean increaseProfessionLevelOnUpdate;

    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    protected abstract void increaseMerchantCareer(ServerLevel level);

    @Shadow
    protected abstract void resetSpecialPrices();

    @Shadow
    protected abstract void updateSpecialPrices(Player player);

    @Shadow
    protected abstract void resendOffersToTradingPlayer();

    public VTVillagerTradePreviewMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    /**
     * Fills in whatever future levels aren't rolled yet. Doing this whenever a player starts trading, rather
     * than once when the villager first rolls its offers, also covers villagers from before the mod was
     * installed: they keep what they have and only get the levels above their current one.
     */
    @Inject(method = "startTrading", at = @At("HEAD"))
    private void vt$rollFutureTrades(Player player, CallbackInfo ci) {
        if (!(this.level() instanceof ServerLevel serverLevel) || !VillagerTweaksConfig.CONFIG.instance().enableTradePreview) {
            return;
        }

        VillagerData data = this.getVillagerData();
        VillagerProfession profession = data.profession().value();

        // No trade set at the current level means no job (or a nitwit), so nothing to preview.
        if (profession.getTrades(data.level()) == null) {
            return;
        }

        this.vt$futureOffers.keySet().removeIf(level -> level <= data.level());

        for (int level = data.level() + 1; level <= VillagerData.MAX_VILLAGER_LEVEL; level++) {
            if (this.vt$futureOffers.containsKey(level)) {
                continue;
            }

            ResourceKey<TradeSet> tradeSet = profession.getTrades(level);
            if (tradeSet == null) {
                continue;
            }

            MerchantOffers rolled = new MerchantOffers();
            this.addOffersFromTradeSet(serverLevel, rolled, tradeSet);
            this.vt$futureOffers.put(level, rolled);
        }
    }

    /** Once the trading screen is open, tells the player's client what the villager has locked. */
    @Inject(method = "startTrading", at = @At("TAIL"))
    private void vt$sendLockedTradesOnOpen(Player player, CallbackInfo ci) {
        this.vt$sendLockedTrades(player);
    }

    /**
     * Vanilla resends the offers to the trading player when it restocks, and the client replaces its whole
     * trade list with them, locked rows included, so they have to be sent again.
     */
    @Inject(method = "resendOffersToTradingPlayer", at = @At("TAIL"))
    private void vt$sendLockedTradesOnResend(CallbackInfo ci) {
        this.vt$sendLockedTrades(this.getTradingPlayer());
    }

    /**
     * Vanilla only counts down to a level-up while nobody is trading, so a villager that has earned enough
     * experience stays at its old level until the player closes the screen. With previews on, the same
     * countdown runs during a trade and the screen is refreshed when it finishes, so the newly unlocked
     * trades appear without reopening it.
     */
    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void vt$levelUpWhileTrading(ServerLevel level, CallbackInfo ci) {
        Player tradingPlayer = this.getTradingPlayer();

        if (
            tradingPlayer == null
                || this.updateMerchantTimer <= 0
                || !this.increaseProfessionLevelOnUpdate
                || !VillagerTweaksConfig.CONFIG.instance().enableTradePreview
        ) {
            return;
        }

        this.updateMerchantTimer--;
        if (this.updateMerchantTimer > 0) {
            return;
        }

        this.increaseMerchantCareer(level);
        this.increaseProfessionLevelOnUpdate = false;
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));

        // The new level's offers have no reputation or hero discounts yet. Resetting first keeps the
        // existing offers from being discounted twice.
        this.resetSpecialPrices();
        this.updateSpecialPrices(tradingPlayer);
        this.resendOffersToTradingPlayer();
    }

    @Unique
    private void vt$sendLockedTrades(Player player) {
        if (
            this.vt$futureOffers.isEmpty()
                || !VillagerTweaksConfig.CONFIG.instance().enableTradePreview
                || !(player instanceof ServerPlayer serverPlayer)
                || !(serverPlayer.containerMenu instanceof MerchantMenu menu)
                || !NetworkManager.canPlayerReceive(serverPlayer, LockedTradesPayload.ID)
        ) {
            return;
        }

        int currentLevel = this.getVillagerData().level();
        List<LockedTradesPayload.LockedTrade> locked = new ArrayList<>();

        for (Map.Entry<Integer, MerchantOffers> entry : this.vt$futureOffers.entrySet()) {
            if (entry.getKey() <= currentLevel) {
                continue;
            }

            for (MerchantOffer offer : entry.getValue()) {
                locked.add(new LockedTradesPayload.LockedTrade(entry.getKey(), offer.copy()));
            }
        }

        if (!locked.isEmpty()) {
            NetworkManager.sendToPlayer(serverPlayer, new LockedTradesPayload(menu.containerId, locked));
        }
    }

    /** At level-up, hands out the offers rolled ahead of time instead of rolling the level's trade set again. */
    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void vt$useRolledTrades(ServerLevel level, CallbackInfo ci) {
        MerchantOffers rolled = this.vt$futureOffers.remove(this.getVillagerData().level());

        if (rolled != null) {
            this.getOffers().addAll(rolled);
            ci.cancel();
        }
    }

    /** A new profession is a new set of trades, so anything rolled for the old one no longer applies. */
    @Inject(method = "setVillagerData", at = @At("HEAD"))
    private void vt$dropRolledTradesOnProfessionChange(VillagerData data, CallbackInfo ci) {
        Holder<VillagerProfession> current = this.getVillagerData().profession();

        if (!current.equals(data.profession())) {
            this.vt$futureOffers.clear();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void vt$writeRolledTrades(ValueOutput view, CallbackInfo ci) {
        if (this.vt$futureOffers.isEmpty()) {
            return;
        }

        List<FutureTradeSet> sets = new ArrayList<>();
        this.vt$futureOffers.forEach((level, offers) -> sets.add(new FutureTradeSet(level, offers)));

        view.store("VTFutureTrades", FutureTradeSet.LIST_CODEC, sets);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void vt$readRolledTrades(ValueInput view, CallbackInfo ci) {
        this.vt$futureOffers.clear();

        view.read("VTFutureTrades", FutureTradeSet.LIST_CODEC)
            .ifPresent(sets -> sets.forEach(set -> this.vt$futureOffers.put(set.level(), set.offers())));
    }
}
