package com.chimericdream.forwardingaddress.fabric.test.fixture;

import com.chimericdream.forwardingaddress.api.Migrations;
import com.chimericdream.lib.registries.ModRegistryHelper;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.TagValueInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Throwaway blocks, items, and migrations the GameTests convert between. Only the test mod's
 * {@code main} entrypoint loads this, so none of it reaches the production jar.
 */
public final class TestFixtures {
    public static final String MOD_ID = "forwardingaddress_test";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(MOD_ID, LOGGER);

    // Blocks without a block entity.
    public static final RegistrySupplier<Block> OLD_BLOCK = plain("old_block");
    public static final RegistrySupplier<Block> NEW_BLOCK = plain("new_block");
    public static final RegistrySupplier<Block> ORPHAN_BLOCK = plain("orphan_block");

    // Same block entity type on both sides.
    public static final RegistrySupplier<Block> OLD_CHEST = container("old_chest", () -> TestFixtures.CHEST_TYPE.get(), 0);
    public static final RegistrySupplier<Block> NEW_CHEST = container("new_chest", () -> TestFixtures.CHEST_TYPE.get(), 0);
    public static final RegistrySupplier<Block> PLAIN_CHEST = container("plain_chest", () -> TestFixtures.CHEST_TYPE.get(), 0);
    // Migrates to a block with no block entity, so its data would be lost.
    public static final RegistrySupplier<Block> OLD_EMPTYABLE = container("old_emptyable", () -> TestFixtures.CHEST_TYPE.get(), 0);
    public static final RegistrySupplier<BlockEntityType<?>> CHEST_TYPE = REGISTRY_HELPER.registerBlockEntity(
        "chest",
        () -> new BlockEntityType<>(
            (pos, state) -> new FixtureContainerEntity(() -> TestFixtures.CHEST_TYPE.get(), pos, state, 0),
            Set.of(OLD_CHEST.get(), NEW_CHEST.get(), PLAIN_CHEST.get(), OLD_EMPTYABLE.get())
        )
    );

    // Same block entity type, with a hidden trailing slot.
    public static final RegistrySupplier<Block> OLD_HOPPER = container("old_hopper", () -> TestFixtures.HOPPER_TYPE.get(), 1);
    public static final RegistrySupplier<Block> NEW_HOPPER = container("new_hopper", () -> TestFixtures.HOPPER_TYPE.get(), 1);
    public static final RegistrySupplier<BlockEntityType<?>> HOPPER_TYPE = REGISTRY_HELPER.registerBlockEntity(
        "hopper",
        () -> new BlockEntityType<>(
            (pos, state) -> new FixtureContainerEntity(() -> TestFixtures.HOPPER_TYPE.get(), pos, state, 1),
            Set.of(OLD_HOPPER.get(), NEW_HOPPER.get())
        )
    );

    // Different block entity types on each side.
    public static final RegistrySupplier<Block> OLD_BOX = container("old_box", () -> TestFixtures.BOX_TYPE.get(), 0);
    public static final RegistrySupplier<BlockEntityType<?>> BOX_TYPE = REGISTRY_HELPER.registerBlockEntity(
        "box",
        () -> new BlockEntityType<>(
            (pos, state) -> new FixtureContainerEntity(() -> TestFixtures.BOX_TYPE.get(), pos, state, 0),
            Set.of(OLD_BOX.get())
        )
    );
    public static final RegistrySupplier<Block> NEW_CRATE = container("new_crate", () -> TestFixtures.CRATE_TYPE.get(), 0);
    public static final RegistrySupplier<BlockEntityType<?>> CRATE_TYPE = REGISTRY_HELPER.registerBlockEntity(
        "crate",
        () -> new BlockEntityType<>(
            (pos, state) -> new FixtureContainerEntity(() -> TestFixtures.CRATE_TYPE.get(), pos, state, 0),
            Set.of(NEW_CRATE.get())
        )
    );

    public static final RegistrySupplier<Item> OLD_ITEM = item("old_item");
    public static final RegistrySupplier<Item> NEW_ITEM = item("new_item");
    public static final RegistrySupplier<Item> ORPHAN_ITEM = item("orphan_item");

    private TestFixtures() {
    }

    private static RegistrySupplier<Block> plain(String name) {
        return REGISTRY_HELPER.registerWithItem(
            name,
            () -> new FixtureBlock(BlockBehaviour.Properties.of().setId(REGISTRY_HELPER.makeBlockRegistryKey(name)))
        );
    }

    private static RegistrySupplier<Block> container(String name, Supplier<BlockEntityType<?>> type, int hidden) {
        return REGISTRY_HELPER.registerWithItem(
            name,
            () -> new FixtureContainerBlock(BlockBehaviour.Properties.of().setId(REGISTRY_HELPER.makeBlockRegistryKey(name)), type, hidden)
        );
    }

    private static RegistrySupplier<Item> item(String name) {
        return REGISTRY_HELPER.registerItem(
            name,
            () -> new Item(new Item.Properties().setId(REGISTRY_HELPER.makeItemRegistryKey(name)))
        );
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Registers the fixtures, then declares the migrations between them.
     */
    public static void register() {
        REGISTRY_HELPER.init();

        Migrations.block(id("old_block")).to(id("new_block")).register();
        // The target of this one is not installed, so it must be skipped without harm.
        Migrations.block(id("orphan_block")).to(Identifier.fromNamespaceAndPath("not_installed", "nowhere")).register();

        Migrations.block(id("old_chest")).to(id("new_chest")).register();
        Migrations.block(id("old_hopper")).to(id("new_hopper")).hiddenSlots(entity -> ((FixtureContainerEntity) entity).hiddenSlots()).register();

        // Different block entity types: converts only with a mapper, which here reloads the old data.
        Migrations.block(id("old_box")).to(id("new_crate"))
            .mapBlockEntity((oldData, target, registries) -> target.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, registries, oldData)))
            .register();

        // A block entity block whose target has none: the data would be lost, so it must be skipped.
        Migrations.block(id("old_emptyable")).to(id("old_block")).register();

        Migrations.item(id("old_item")).to(id("new_item")).register();
        Migrations.item(id("orphan_item")).to(Identifier.fromNamespaceAndPath("not_installed", "nowhere")).register();

        // Retired and never registered, as a mod that deleted its old classes would leave them. Only the
        // saved-data rewrite can reach these; "unmigrated_ghost" has no migration at all, as a control.
        Migrations.block(id("ghost_block")).to(id("new_block")).register();
        Migrations.item(id("ghost_item")).to(id("new_item")).register();
    }
}
