package com.chimericdream.lib.neoforge.loot;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Removes the boilerplate around NeoForge global-loot-modifier registration. Every mod that ships a
 * loot modifier otherwise repeats the same verbose
 * {@code DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, modId)} line
 * (plus its imports). Create the register with this helper, register each codec on the returned
 * register, and bind it to the mod event bus in the NeoForge entrypoint as usual.
 *
 * <pre>{@code
 * public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
 *     LootModifierHelper.createRegister(ModInfo.MOD_ID);
 * public static final DeferredHolder<...> MY_MODIFIER =
 *     LOOT_MODIFIERS.register("my_modifier", MyLootModifier.CODEC);
 * // in the @Mod entrypoint: LOOT_MODIFIERS.register(modEventBus);
 * }</pre>
 */
public final class LootModifierHelper {
    private LootModifierHelper() {
    }

    /**
     * Creates a {@link DeferredRegister} for this mod's global loot modifier serializers. The caller
     * still registers each codec on it and binds it to the mod event bus.
     */
    public static DeferredRegister<MapCodec<? extends IGlobalLootModifier>> createRegister(String modId) {
        return DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, modId);
    }
}
