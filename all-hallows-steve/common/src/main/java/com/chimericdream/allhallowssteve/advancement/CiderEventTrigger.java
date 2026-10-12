package com.chimericdream.allhallowssteve.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** Like {@link PumpkinEventTrigger}, but for the cider and juice advancements: a criterion names which {@link CiderEvent} it waits for. */
public class CiderEventTrigger extends SimpleCriterionTrigger<CiderEventTrigger.TriggerInstance> {
    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, CiderEvent event) {
        this.trigger(player, instance -> instance.event() == event);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, CiderEvent event) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            CiderEvent.CODEC.fieldOf("event").forGetter(TriggerInstance::event)
        ).apply(instance, TriggerInstance::new));
    }
}
