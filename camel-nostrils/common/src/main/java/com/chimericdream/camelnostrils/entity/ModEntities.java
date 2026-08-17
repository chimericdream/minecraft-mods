package com.chimericdream.camelnostrils.entity;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.lib.entities.FallingUpwardBlockEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import static com.chimericdream.camelnostrils.CamelNostrilsMod.REGISTRY_HELPER;

public class ModEntities {
    public static final Identifier FALLING_UPWARD_BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "falling_upward_block");

    /**
     * Shared by every {@link com.chimericdream.lib.blocks.FallingUpwardBlock} registered by this mod,
     * the same way vanilla shares a single {@code EntityTypes.FALLING_BLOCK} across sand, gravel,
     * anvils, etc. — the specific block being carried lives on the entity, not the type.
     */
    public static final RegistrySupplier<EntityType<FallingUpwardBlockEntity>> FALLING_UPWARD_BLOCK_ENTITY = REGISTRY_HELPER.registerEntityType(
        FALLING_UPWARD_BLOCK_ID,
        () -> EntityType.Builder.of(FallingUpwardBlockEntity::new, MobCategory.MISC)
            .noLootTable()
            .sized(0.98F, 0.98F)
            .clientTrackingRange(10)
            .updateInterval(20)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, FALLING_UPWARD_BLOCK_ID))
    );

    public static void init() {
    }
}
