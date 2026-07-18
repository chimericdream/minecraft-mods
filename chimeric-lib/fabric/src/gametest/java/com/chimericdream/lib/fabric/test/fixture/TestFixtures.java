package com.chimericdream.lib.fabric.test.fixture;

import com.chimericdream.lib.entities.SimpleSeatEntity;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.lib.screen.DoubleWideInventoryScreenHandler;
import com.chimericdream.lib.screen.SimpleInventoryScreenHandler;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registers the throwaway fixture content the GameTests need — all through the library's own
 * {@link ModRegistryHelper}, so registering them <em>is</em> part of what
 * {@code RegisterableBlockGameTest} verifies. Registration is driven from the test mod's
 * {@code main} entrypoint ({@link ChimericLibTestEntrypoint}), which only loads in the {@code gametest}
 * run, so none of this reaches the production jar.
 */
public final class TestFixtures {
    public static final String MOD_ID = "chimericlib_test";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(MOD_ID, LOGGER);

    public static final int SIMPLE_ROWS = 3;
    public static final int DOUBLE_ROWS = 3;

    /** A plain block+item, to prove {@code registerWithItem} resolves both. */
    public static final RegistrySupplier<Block> TEST_BLOCK = REGISTRY_HELPER.registerWithItem(
        "test_block",
        () -> new Block(BlockBehaviour.Properties.of().setId(REGISTRY_HELPER.makeBlockRegistryKey("test_block")))
    );

    /** A block+item that also owns a block entity, to prove the whole trio resolves. */
    public static final RegistrySupplier<Block> TEST_CONTAINER_BLOCK = REGISTRY_HELPER.registerWithItem(
        "test_container",
        () -> new TestContainerBlock(BlockBehaviour.Properties.of().setId(REGISTRY_HELPER.makeBlockRegistryKey("test_container")))
    );

    public static final RegistrySupplier<BlockEntityType<TestContainerBlockEntity>> TEST_CONTAINER_BE = REGISTRY_HELPER.registerBlockEntity(
        "test_container",
        () -> new BlockEntityType<>(TestContainerBlockEntity::new, Set.of(TEST_CONTAINER_BLOCK.get()))
    );

    public static final RegistrySupplier<EntityType<SimpleSeatEntity>> SEAT_ENTITY = REGISTRY_HELPER.registerEntityType(
        "test_seat_entity",
        () -> EntityType.Builder.of(SimpleSeatEntity::new, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, REGISTRY_HELPER.makeId("test_seat_entity")))
    );

    // The MenuSupplier passes a null type (as consumer handlers like Minekea's CrateScreenHandler do)
    // to sidestep a self-reference in this field's initializer; the tests construct these handlers
    // directly with the real registered type via SIMPLE_MENU.get()/DOUBLE_MENU.get().
    public static final RegistrySupplier<MenuType<SimpleInventoryScreenHandler>> SIMPLE_MENU = REGISTRY_HELPER.registerScreenHandler(
        "simple_menu",
        () -> new MenuType<>(
            (syncId, inv) -> new SimpleInventoryScreenHandler(null, syncId, inv, SIMPLE_ROWS),
            FeatureFlagSet.of()
        )
    );

    public static final RegistrySupplier<MenuType<DoubleWideInventoryScreenHandler>> DOUBLE_MENU = REGISTRY_HELPER.registerScreenHandler(
        "double_menu",
        () -> new MenuType<>(
            (syncId, inv) -> new DoubleWideInventoryScreenHandler(null, syncId, inv, DOUBLE_ROWS),
            FeatureFlagSet.of()
        )
    );

    private TestFixtures() {
    }

    /** Flushes every deferred registration above into the game registries. */
    public static void register() {
        REGISTRY_HELPER.init();
    }
}
