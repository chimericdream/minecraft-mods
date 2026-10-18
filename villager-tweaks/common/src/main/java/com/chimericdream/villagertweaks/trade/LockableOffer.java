package com.chimericdream.villagertweaks.trade;

/**
 * Implemented onto {@code MerchantOffer} by {@code VTTradeOfferMixin}. A locked offer is a preview of a
 * trade the villager will only sell once it reaches {@link #vt$getUnlockLevel()}; it only ever exists on
 * the client, appended after the villager's real offers.
 */
public interface LockableOffer {
    /** The villager level that unlocks this offer, or 0 for a normal, tradeable offer. */
    int vt$getUnlockLevel();

    void vt$setUnlockLevel(int level);

    default boolean vt$isLocked() {
        return vt$getUnlockLevel() > 0;
    }
}
