package com.chimericdream.nextupdatenow.entity;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import static com.chimericdream.nextupdatenow.NextUpdateNowMod.REGISTRY_HELPER;

public class ModEntities {
    private static final Identifier ICE_BALL_ID = Identifier.withDefaultNamespace("ice_ball");

    public static final RegistrySupplier<EntityType<IceBall>> ICE_BALL = REGISTRY_HELPER.registerEntityType(
        ICE_BALL_ID,
        () -> EntityType.Builder.<IceBall>of(IceBall::new, MobCategory.MISC)
            .noLootTable()
            .sized(0.25F, 0.25F)
            .eyeHeight(0.0F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, ICE_BALL_ID))
    );

    public static void init() {
        // Loading this class registers the entity types.
    }
}
