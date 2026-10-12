package com.chimericdream.allhallowssteve.advancement;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import com.chimericdream.allhallowssteve.wearable.PumpkinFaces;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class ModTriggers {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(ModInfo.MOD_ID, Registries.TRIGGER_TYPE);

    public static final RegistrySupplier<PumpkinEventTrigger> PUMPKIN_EVENT = TRIGGERS.register(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pumpkin_event"),
        PumpkinEventTrigger::new
    );

    public static final RegistrySupplier<CiderEventTrigger> CIDER_EVENT = TRIGGERS.register(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "cider_event"),
        CiderEventTrigger::new
    );

    public static final RegistrySupplier<PomaceHealedTrigger> POMACE_HEALED = TRIGGERS.register(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pomace_healed"),
        PomaceHealedTrigger::new
    );

    /** How often, in ticks, the worn pumpkin is checked. Cheap, and quick enough that nobody notices the delay. */
    private static final int WORN_CHECK_INTERVAL = 10;

    private ModTriggers() {
    }

    public static void init() {
        TRIGGERS.register();

        TickEvent.PLAYER_POST.register(ModTriggers::checkWornPumpkin);
    }

    /** Fires {@code event} for {@code player}. Does nothing for a non-server player (client-side calls, or no player at all). */
    public static void fire(Player player, PumpkinEvent event) {
        if (player instanceof ServerPlayer serverPlayer) {
            PUMPKIN_EVENT.get().trigger(serverPlayer, event);
        }
    }

    /** Fires the cider {@code event} for {@code player}, with the same rules as the pumpkin one. */
    public static void fire(Player player, CiderEvent event) {
        if (player instanceof ServerPlayer serverPlayer) {
            CIDER_EVENT.get().trigger(serverPlayer, event);
        }
    }

    /** Fires the "healed an animal with pomace" trigger for {@code player}. */
    public static void firePomaceHealed(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            POMACE_HEALED.get().trigger(serverPlayer);
        }
    }

    /** A criterion that completes when the cider {@code event} fires. Used by the advancement datagen. */
    public static Criterion<CiderEventTrigger.TriggerInstance> criterion(CiderEvent event) {
        return CIDER_EVENT.get().createCriterion(new CiderEventTrigger.TriggerInstance(Optional.empty(), event));
    }

    /** A criterion that completes when an animal is healed with pomace. */
    public static Criterion<PomaceHealedTrigger.TriggerInstance> pomaceHealedCriterion() {
        return POMACE_HEALED.get().createCriterion(new PomaceHealedTrigger.TriggerInstance(Optional.empty()));
    }

    /** A criterion that completes when {@code event} fires. Used by the advancement datagen. */
    public static Criterion<PumpkinEventTrigger.TriggerInstance> criterion(PumpkinEvent event) {
        return PUMPKIN_EVENT.get().createCriterion(new PumpkinEventTrigger.TriggerInstance(Optional.empty(), event));
    }

    /** Fires the "wearing a pumpkin" event for {@code player} if they are wearing one; called regularly from the player tick. */
    public static void checkWornPumpkin(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || player.tickCount % WORN_CHECK_INTERVAL != 0) {
            return;
        }

        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!PumpkinFaces.isWearable(head)) {
            return;
        }

        PumpkinStencilsComponent stencils = head.getOrDefault(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), PumpkinStencilsComponent.EMPTY);

        PUMPKIN_EVENT.get().trigger(serverPlayer, stencils.isEmpty() ? PumpkinEvent.WORN_UNCARVED : PumpkinEvent.WORN_CARVED);
    }
}
