package com.chimericdream.lib.registries;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class ModRegistryHelper {
    public final String modId;
    public final Logger LOGGER;

    public final DeferredRegister<Fluid> FLUIDS;
    public final DeferredRegister<Block> BLOCKS;
    public final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES;
    public final DeferredRegister<Item> ITEMS;
    public final DeferredRegister<ItemGroup> ITEM_GROUPS;
    public final DeferredRegister<EntityType<?>> ENTITY_TYPES;
    public final DeferredRegister<ScreenHandlerType<?>> SCREEN_HANDLERS;

    @SuppressWarnings("unchecked")
    public ModRegistryHelper(String modId, Logger logger) {
        this.modId = modId;
        this.LOGGER = logger;

        FLUIDS = DeferredRegister.create(modId, (RegistryKey<Registry<Fluid>>) Registries.FLUID.getKey());
        BLOCKS = DeferredRegister.create(modId, (RegistryKey<Registry<Block>>) Registries.BLOCK.getKey());
        BLOCK_ENTITY_TYPES = DeferredRegister.create(modId, (RegistryKey<Registry<BlockEntityType<?>>>) Registries.BLOCK_ENTITY_TYPE.getKey());
        ITEMS = DeferredRegister.create(modId, (RegistryKey<Registry<Item>>) Registries.ITEM.getKey());
        ITEM_GROUPS = DeferredRegister.create(modId, (RegistryKey<Registry<ItemGroup>>) Registries.ITEM_GROUP.getKey());
        ENTITY_TYPES = DeferredRegister.create(modId, (RegistryKey<Registry<EntityType<?>>>) Registries.ENTITY_TYPE.getKey());
        SCREEN_HANDLERS = DeferredRegister.create(modId, (RegistryKey<Registry<ScreenHandlerType<?>>>) Registries.SCREEN_HANDLER.getKey());
    }

    public void init() {
        LOGGER.debug("Registering fluids");
        FLUIDS.register();

        LOGGER.debug("Registering blocks");
        BLOCKS.register();

        LOGGER.debug("Registering block entities");
        BLOCK_ENTITY_TYPES.register();

        LOGGER.debug("Registering items");
        ITEMS.register();

        LOGGER.debug("Registering item groups");
        ITEM_GROUPS.register();

        LOGGER.debug("Registering entities");
        ENTITY_TYPES.register();

        LOGGER.debug("Registering screen handlers");
        SCREEN_HANDLERS.register();
    }

    public <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final String name, final Supplier<T> supplier) {
        return registerBlockEntity(Identifier.of(this.modId, name), supplier);
    }

    public <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final Identifier id, final Supplier<T> supplier) {
        Registrar<BlockEntityType<?>> registrar = BLOCK_ENTITY_TYPES.getRegistrar();

        return registrar.register(id, supplier);
    }

    public RegistrySupplier<Block> registerWithItem(String name, Supplier<Block> supplier) {
        return registerWithItem(name, supplier, new Item.Settings());
    }

    public RegistrySupplier<Block> registerWithItem(Identifier id, Supplier<Block> supplier) {
        return registerWithItem(id, supplier, new Item.Settings());
    }

    public RegistrySupplier<Block> registerWithItem(String name, Supplier<Block> supplier, Item.Settings itemSettings) {
        return registerWithItem(Identifier.of(this.modId, name), supplier, itemSettings);
    }

    public RegistrySupplier<Block> registerWithItem(Identifier id, Supplier<Block> supplier, Item.Settings itemSettings) {
        RegistrySupplier<Block> block = registerBlock(id, supplier);

        registerItem(id, () -> new BlockItem(block.get(), itemSettings));

        return block;
    }

    public <T extends Block> RegistrySupplier<T> registerBlock(Identifier path, Supplier<T> block) {
        Registrar<Block> registrar = BLOCKS.getRegistrar();

        if (Platform.isNeoForge()) {
            return BLOCKS.register(path.getPath(), block);
        }

        return registrar.register(path, block);
    }

    public <T extends Item> RegistrySupplier<T> registerItem(Identifier path, Supplier<T> itemSupplier) {
        Registrar<Item> registrar = ITEMS.getRegistrar();

        if (Platform.isNeoForge()) {
            return ITEMS.register(path.getPath(), itemSupplier);
        }

        return registrar.register(path, itemSupplier);
    }

    public <T extends EntityType<?>> RegistrySupplier<T> registerEntityType(Identifier path, Supplier<T> entitySupplier) {
        Registrar<EntityType<?>> registrar = ENTITY_TYPES.getRegistrar();

        if (Platform.isNeoForge()) {
            return ENTITY_TYPES.register(path.getPath(), entitySupplier);
        }

        return registrar.register(path, entitySupplier);
    }

    public <T extends ScreenHandlerType<?>> RegistrySupplier<T> registerScreenHandler(Identifier path, Supplier<T> screenHandlerSupplier) {
        Registrar<ScreenHandlerType<?>> registrar = SCREEN_HANDLERS.getRegistrar();

        if (Platform.isNeoForge()) {
            return SCREEN_HANDLERS.register(path.getPath(), screenHandlerSupplier);
        }

        return registrar.register(path, screenHandlerSupplier);
    }
}
