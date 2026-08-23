package com.chimericdream.effectivegear.block;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;

import static com.chimericdream.effectivegear.EffectiveGearMod.REGISTRY_HELPER;

public class EGBlocks {
    public static final String REDSTONE_TRIM_PULSE_BUTTON_ID = "redstone_trim_pulse_button";

    public static final RegistrySupplier<Block> REDSTONE_TRIM_PULSE_BUTTON = REGISTRY_HELPER.registerBlock(
        REDSTONE_TRIM_PULSE_BUTTON_ID,
        () -> new EGInvisibleButtonBlock(
            BlockSetType.STONE,
            20,
            Properties.of()
                .noCollision()
                .strength(0.5F)
                .pushReaction(PushReaction.DESTROY)
                .setId(REGISTRY_HELPER.makeBlockRegistryKey(REDSTONE_TRIM_PULSE_BUTTON_ID))
        )
    );

    public static void init() {
    }
}
