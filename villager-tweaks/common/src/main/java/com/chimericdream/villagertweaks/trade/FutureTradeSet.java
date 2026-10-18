package com.chimericdream.villagertweaks.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.List;

/** The offers a villager has already rolled for a level it hasn't reached yet, as saved in its NBT. */
public record FutureTradeSet(int level, MerchantOffers offers) {
    public static final Codec<FutureTradeSet> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.fieldOf("level").forGetter(FutureTradeSet::level),
        MerchantOffers.CODEC.fieldOf("offers").forGetter(FutureTradeSet::offers)
    ).apply(i, FutureTradeSet::new));

    public static final Codec<List<FutureTradeSet>> LIST_CODEC = CODEC.listOf();
}
