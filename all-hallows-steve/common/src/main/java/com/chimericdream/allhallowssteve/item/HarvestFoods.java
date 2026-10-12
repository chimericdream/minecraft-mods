package com.chimericdream.allhallowssteve.item;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.List;

/**
 * Food values for the harvest foods. Only the spiced soul cakes have effects: cloves ward (absorption),
 * cinnamon brings luck, nutmeg lets you see in the dark, and the mulling spices sachet gives all three.
 * Everything else is plain hunger and saturation.
 */
public final class HarvestFoods {
    /** Like suspicious stew, the effects are short: 30 seconds. */
    public static final int EFFECT_TICKS = 30 * 20;

    public static final MobEffectInstance CLOVES_EFFECT = effect(MobEffects.ABSORPTION);
    public static final MobEffectInstance CINNAMON_EFFECT = effect(MobEffects.LUCK);
    public static final MobEffectInstance NUTMEG_EFFECT = effect(MobEffects.NIGHT_VISION);

    private HarvestFoods() {
    }

    private static MobEffectInstance effect(Holder<MobEffect> effect) {
        return new MobEffectInstance(effect, EFFECT_TICKS, 0);
    }

    public static FoodProperties soulCake() {
        return new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).build();
    }

    public static FoodProperties pumpkinSoup() {
        return new FoodProperties.Builder().nutrition(7).saturationModifier(0.6F).build();
    }

    public static FoodProperties roastedPumpkinSeeds() {
        return new FoodProperties.Builder().nutrition(2).saturationModifier(0.4F).build();
    }

    public static FoodProperties applePie() {
        return new FoodProperties.Builder().nutrition(8).saturationModifier(0.3F).build();
    }

    public static FoodProperties spicedPumpkinPie() {
        return new FoodProperties.Builder().nutrition(10).saturationModifier(0.5F).build();
    }

    /** What eating a soul cake with these effects does; an empty list is a plain cake. */
    public static Consumable soulCakeConsumable(List<MobEffectInstance> effects) {
        Consumable.Builder builder = Consumable.builder();

        if (!effects.isEmpty()) {
            builder.onConsume(new ApplyStatusEffectsConsumeEffect(effects));
        }

        return builder.build();
    }
}
