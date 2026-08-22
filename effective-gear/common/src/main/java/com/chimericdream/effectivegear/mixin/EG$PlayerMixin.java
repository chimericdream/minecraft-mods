package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.ModInfo;
import com.chimericdream.effectivegear.item.armor.Trims;
import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import com.chimericdream.effectivegear.tags.EffectiveGearItemTags;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class EG$PlayerMixin {
    @Unique
    private static final Identifier EG$KNOCKBACK_RESISTANCE_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "slimeball_trim_knockback_resistance");

    @Unique
    private static final int EG$EFFECT_DURATION = 200;

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void eg$applyArmorSetBonuses(CallbackInfo ci) {
        Player self = (Player) (Object) this;

        if (TrimSetUtils.isWearingFullSet(
            self,
            EffectiveGearItemTags.NETHERITE_HELMETS,
            EffectiveGearItemTags.NETHERITE_CHESTPLATES,
            EffectiveGearItemTags.NETHERITE_LEGGINGS,
            EffectiveGearItemTags.NETHERITE_BOOTS
        )) {
            self.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, EG$EFFECT_DURATION, 0, false, false, true));
        }

        if (TrimSetUtils.isWearingFullTrim(self, Trims.TURTLE_SCUTE_TRIM_ID)) {
            self.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, EG$EFFECT_DURATION, 0, false, false, true));
        }

        if (TrimSetUtils.isWearingFullTrim(self, Trims.NETHER_STAR_TRIM_ID)) {
            self.addEffect(new MobEffectInstance(MobEffects.REGENERATION, EG$EFFECT_DURATION, 0, false, false, true));
        }

        if (TrimSetUtils.isWearingFullTrim(self, Trims.ENCHANTED_GOLDEN_APPLE_TRIM_ID)) {
            self.addEffect(new MobEffectInstance(MobEffects.REGENERATION, EG$EFFECT_DURATION, 1, false, false, true));
            self.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, EG$EFFECT_DURATION, 0, false, false, true));
        }

        eg$updateKnockbackResistance(self, TrimSetUtils.isWearingFullTrim(self, Trims.SLIMEBALL_TRIM_ID));
    }

    @Unique
    private static void eg$updateKnockbackResistance(Player player, boolean shouldHaveBonus) {
        AttributeInstance attribute = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (attribute == null) {
            return;
        }

        boolean hasBonus = attribute.getModifier(EG$KNOCKBACK_RESISTANCE_ID) != null;
        if (shouldHaveBonus && !hasBonus) {
            attribute.addTransientModifier(new AttributeModifier(EG$KNOCKBACK_RESISTANCE_ID, 0.5, AttributeModifier.Operation.ADD_VALUE));
        } else if (!shouldHaveBonus && hasBonus) {
            attribute.removeModifier(EG$KNOCKBACK_RESISTANCE_ID);
        }
    }

    @Redirect(
        method = "attack(Lnet/minecraft/world/entity/Entity;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setLastHurtMob(Lnet/minecraft/world/entity/Entity;)V")
    )
    private void eg$igniteTargetWithFullBlazePowderTrim(Player instance, Entity target) {
        instance.setLastHurtMob(target);

        if (TrimSetUtils.isWearingFullTrim(instance, Trims.BLAZE_POWDER_TRIM_ID)) {
            target.igniteForSeconds(4.0F);
        }
    }
}
