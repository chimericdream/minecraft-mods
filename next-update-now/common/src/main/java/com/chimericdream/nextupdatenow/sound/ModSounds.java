package com.chimericdream.nextupdatenow.sound;

import com.chimericdream.nextupdatenow.ModInfo;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ModInfo.MOD_ID, Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> ICE_BALL_BREAK = register("entity.ice_ball.break");
    public static final RegistrySupplier<SoundEvent> ICE_BALL_THROW = register("entity.ice_ball.throw");

    public static void init() {
        SOUND_EVENTS.register();
    }

    private static RegistrySupplier<SoundEvent> register(String path) {
        Identifier id = Identifier.withDefaultNamespace(path);
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
