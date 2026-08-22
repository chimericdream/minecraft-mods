package com.chimericdream.camelnostrils.entity;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.entity.fish.ZombieCod;
import com.chimericdream.camelnostrils.entity.fish.ZombieSalmon;
import com.chimericdream.camelnostrils.entity.fish.ZombieTropicalFish;
import com.chimericdream.lib.entities.FallingUpwardBlockEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
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

    /**
     * A leashed salmon/cod/tropical fish that dies from flopping out of water comes back as one of
     * these instead — see {@link com.chimericdream.camelnostrils.entity.fish.ZombieFishConverter}.
     * Hostile, 1 heart of health, immune to sunlight, and reuses the vanilla fish's own renderer/model
     * (registered in {@link com.chimericdream.camelnostrils.client.CamelNostrilsClient}).
     */
    public static final RegistrySupplier<EntityType<ZombieSalmon>> ZOMBIE_SALMON = REGISTRY_HELPER.registerEntityType(
        "zombie_salmon",
        () -> EntityType.Builder.of(ZombieSalmon::new, MobCategory.MONSTER)
            .sized(0.7F, 0.4F)
            .clientTrackingRange(4)
            .updateInterval(3)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "zombie_salmon")))
    );

    public static final RegistrySupplier<EntityType<ZombieCod>> ZOMBIE_COD = REGISTRY_HELPER.registerEntityType(
        "zombie_cod",
        () -> EntityType.Builder.of(ZombieCod::new, MobCategory.MONSTER)
            .sized(0.5F, 0.3F)
            .clientTrackingRange(4)
            .updateInterval(3)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "zombie_cod")))
    );

    public static final RegistrySupplier<EntityType<ZombieTropicalFish>> ZOMBIE_TROPICAL_FISH = REGISTRY_HELPER.registerEntityType(
        "zombie_tropical_fish",
        () -> EntityType.Builder.of(ZombieTropicalFish::new, MobCategory.MONSTER)
            .sized(0.5F, 0.4F)
            .clientTrackingRange(4)
            .updateInterval(3)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "zombie_tropical_fish")))
    );

    public static void init() {
    }

    /**
     * Must run after {@code REGISTRY_HELPER.init()} has actually registered the entity types above —
     * architectury's Fabric {@code EntityAttributeRegistry} resolves the {@link RegistrySupplier}
     * eagerly, so calling this any earlier throws "Registry Object not present".
     */
    public static void registerAttributes() {
        EntityAttributeRegistry.register(ZOMBIE_SALMON, ZombieSalmon::createAttributes);
        EntityAttributeRegistry.register(ZOMBIE_COD, ZombieCod::createAttributes);
        EntityAttributeRegistry.register(ZOMBIE_TROPICAL_FISH, ZombieTropicalFish::createAttributes);
    }
}
