package com.chimericdream.allhallowssteve.item;

import com.chimericdream.allhallowssteve.tag.ModTags;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Feeding pomace to injured livestock (the {@code pomace_feedable} entity tag: cows, sheep, pigs, and
 * horse-likes) heals them a little. It only applies below max health, so a healthy pig still goes
 * through vanilla breeding with pomace, which is in the {@code pig_food} item tag.
 */
public class PomaceFeeding {
    /** Half-hearts restored per piece of pomace. */
    public static final float HEAL_AMOUNT = 2.0F;

    public static void init() {
        InteractionEvent.INTERACT_ENTITY.register(PomaceFeeding::onInteract);
    }

    public static EventResult onInteract(Player player, Entity entity, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.is(ModItems.POMACE.get()) || !entity.typeHolder().is(ModTags.POMACE_FEEDABLE) || !(entity instanceof LivingEntity animal)) {
            return EventResult.pass();
        }

        if (!animal.isAlive() || animal.getHealth() >= animal.getMaxHealth()) {
            return EventResult.pass();
        }

        if (player.level().isClientSide()) {
            return EventResult.interruptTrue();
        }

        animal.heal(HEAL_AMOUNT);
        animal.level().playSound(null, animal.blockPosition(), SoundEvents.GENERIC_EAT.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return EventResult.interruptTrue();
    }
}
