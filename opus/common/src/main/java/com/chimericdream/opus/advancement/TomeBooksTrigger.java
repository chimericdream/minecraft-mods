package com.chimericdream.opus.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** Fires with how many guides the fullest Book of Books in a player's inventory holds; completes once that reaches {@code min_books}. */
public class TomeBooksTrigger extends SimpleCriterionTrigger<TomeBooksTrigger.TriggerInstance> {
    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, int books) {
        this.trigger(player, instance -> books >= instance.minBooks());
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, int minBooks) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            Codec.INT.fieldOf("min_books").forGetter(TriggerInstance::minBooks)
        ).apply(instance, TriggerInstance::new));
    }
}
