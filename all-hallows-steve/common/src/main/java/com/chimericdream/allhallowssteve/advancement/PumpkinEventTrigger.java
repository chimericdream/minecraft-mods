package com.chimericdream.allhallowssteve.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * One trigger for every pumpkin-related advancement vanilla can't express: a criterion names which
 * {@link PumpkinEvent} it waits for, and the code fires the event for the player who did it.
 */
public class PumpkinEventTrigger extends SimpleCriterionTrigger<PumpkinEventTrigger.TriggerInstance> {
    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, PumpkinEvent event) {
        this.trigger(player, instance -> instance.event() == event);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, PumpkinEvent event) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            PumpkinEvent.CODEC.fieldOf("event").forGetter(TriggerInstance::event)
        ).apply(instance, TriggerInstance::new));
    }
}
