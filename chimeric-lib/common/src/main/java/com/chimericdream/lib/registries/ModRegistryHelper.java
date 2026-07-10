package com.chimericdream.lib.registries;

import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

public class ModRegistryHelper {
    public final String modId;
    public final Logger LOGGER;

    public final DeferredRegister<Fluid> FLUIDS;
    public final DeferredRegister<Block> BLOCKS;
    public final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES;
    public final DeferredRegister<Item> ITEMS;
    public final DeferredRegister<CreativeModeTab> ITEM_GROUPS;
    public final DeferredRegister<EntityType<?>> ENTITY_TYPES;
    public final DeferredRegister<MenuType<?>> SCREEN_HANDLERS;
    public final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS;
    public final DeferredRegister<Identifier> CUSTOM_STATS;
    public final DeferredRegister<DataComponentType<?>> CUSTOM_COMPONENTS;

    @SuppressWarnings("unchecked")
    public ModRegistryHelper(String modId, Logger logger) {
        this.modId = modId;
        this.LOGGER = logger;

        FLUIDS = DeferredRegister.create(modId, (ResourceKey<Registry<Fluid>>) BuiltInRegistries.FLUID.key());
        BLOCKS = DeferredRegister.create(modId, (ResourceKey<Registry<Block>>) BuiltInRegistries.BLOCK.key());
        BLOCK_ENTITY_TYPES = DeferredRegister.create(modId, (ResourceKey<Registry<BlockEntityType<?>>>) BuiltInRegistries.BLOCK_ENTITY_TYPE.key());
        ITEMS = DeferredRegister.create(modId, (ResourceKey<Registry<Item>>) BuiltInRegistries.ITEM.key());
        ITEM_GROUPS = DeferredRegister.create(modId, (ResourceKey<Registry<CreativeModeTab>>) BuiltInRegistries.CREATIVE_MODE_TAB.key());
        ENTITY_TYPES = DeferredRegister.create(modId, (ResourceKey<Registry<EntityType<?>>>) BuiltInRegistries.ENTITY_TYPE.key());
        SCREEN_HANDLERS = DeferredRegister.create(modId, (ResourceKey<Registry<MenuType<?>>>) BuiltInRegistries.MENU.key());
        VILLAGER_PROFESSIONS = DeferredRegister.create(modId, (ResourceKey<Registry<VillagerProfession>>) BuiltInRegistries.VILLAGER_PROFESSION.key());
        CUSTOM_STATS = DeferredRegister.create(modId, (ResourceKey<Registry<Identifier>>) BuiltInRegistries.CUSTOM_STAT.key());
        CUSTOM_COMPONENTS = DeferredRegister.create(modId, (ResourceKey<Registry<DataComponentType<?>>>) BuiltInRegistries.DATA_COMPONENT_TYPE.key());
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

        LOGGER.debug("Registering villager professions");
        VILLAGER_PROFESSIONS.register();

        LOGGER.debug("Registering custom stats");
        CUSTOM_STATS.register();

        LOGGER.debug("Registering custom component types");
        CUSTOM_COMPONENTS.register();
    }

    public Identifier makeId(String key) {
        return Identifier.fromNamespaceAndPath(this.modId, key);
    }

    public ResourceKey<Block> makeBlockRegistryKey(String key) {
        return makeBlockRegistryKey(makeId(key));
    }

    public ResourceKey<Block> makeBlockRegistryKey(Identifier id) {
        return ResourceKey.create(Registries.BLOCK, id);
    }

    public ResourceKey<Item> makeItemRegistryKey(String key) {
        return makeItemRegistryKey(makeId(key));
    }

    public ResourceKey<Item> makeItemRegistryKey(Identifier id) {
        return ResourceKey.create(Registries.ITEM, id);
    }

    public <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final String name, final Supplier<T> supplier) {
        return registerBlockEntity(Identifier.fromNamespaceAndPath(this.modId, name), supplier);
    }

    public <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final Identifier id, final Supplier<T> supplier) {
        return BLOCK_ENTITY_TYPES.register(id, supplier);
    }

    public RegistrySupplier<Block> registerWithItem(String name, Supplier<Block> supplier) {
        return registerWithItem(name, supplier, new Item.Properties());
    }

    public RegistrySupplier<Block> registerWithItem(Identifier id, Supplier<Block> supplier) {
        return registerWithItem(id, supplier, new Item.Properties());
    }

    public RegistrySupplier<Block> registerWithItem(String name, Supplier<Block> supplier, Item.Properties itemSettings) {
        return registerWithItem(Identifier.fromNamespaceAndPath(this.modId, name), supplier, itemSettings);
    }

    public RegistrySupplier<Block> registerWithItem(Identifier id, Supplier<Block> supplier, Item.Properties itemSettings) {
        RegistrySupplier<Block> block = registerBlock(id, supplier);

        registerItem(
            id,
            () -> new BlockItem(
                block.get(),
                itemSettings.setId(ResourceKey.create(Registries.ITEM, id))
            )
        );

        return block;
    }

    public <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(Identifier.fromNamespaceAndPath(this.modId, name), block);
    }

    public <T extends Block> RegistrySupplier<T> registerBlock(Identifier id, Supplier<T> block) {
        if (Platform.isNeoForge()) {
            return BLOCKS.register(id.getPath(), block);
        }

        return BLOCKS.register(id, block);
    }

    public <T extends Fluid> RegistrySupplier<T> registerFluid(String name, Supplier<T> fluidSupplier) {
        return registerFluid(Identifier.fromNamespaceAndPath(this.modId, name), fluidSupplier);
    }

    public <T extends Fluid> RegistrySupplier<T> registerFluid(Identifier id, Supplier<T> fluidSupplier) {
        return FLUIDS.register(id, fluidSupplier);
    }

    public <T extends Item> RegistrySupplier<T> registerItem(String name, Supplier<T> itemSupplier) {
        return registerItem(Identifier.fromNamespaceAndPath(this.modId, name), itemSupplier);
    }

    public <T extends Item> RegistrySupplier<T> registerItem(Identifier id, Supplier<T> itemSupplier) {
        if (Platform.isNeoForge()) {
            return ITEMS.register(id.getPath(), itemSupplier);
        }

        return ITEMS.register(id, itemSupplier);
    }

    public RegistrySupplier<CreativeModeTab> registerItemGroup(String id, Supplier<Block> iconBlock) {
        return ITEM_GROUPS.register(
            id,
            () -> CreativeTabRegistry.create(
                Component.translatable(id),
                () -> new ItemStack(iconBlock.get())
            )
        );
    }

    public <T extends EntityType<?>> RegistrySupplier<T> registerEntityType(String name, Supplier<T> entitySupplier) {
        return registerEntityType(Identifier.fromNamespaceAndPath(this.modId, name), entitySupplier);
    }

    public <T extends EntityType<?>> RegistrySupplier<T> registerEntityType(Identifier id, Supplier<T> entitySupplier) {
        if (Platform.isNeoForge()) {
            return ENTITY_TYPES.register(id.getPath(), entitySupplier);
        }

        return ENTITY_TYPES.register(id, entitySupplier);
    }

    public <T extends MenuType<?>> RegistrySupplier<T> registerScreenHandler(String name, Supplier<T> screenHandlerSupplier) {
        return registerScreenHandler(Identifier.fromNamespaceAndPath(this.modId, name), screenHandlerSupplier);
    }

    public <T extends MenuType<?>> RegistrySupplier<T> registerScreenHandler(Identifier id, Supplier<T> screenHandlerSupplier) {
        if (Platform.isNeoForge()) {
            return SCREEN_HANDLERS.register(id.getPath(), screenHandlerSupplier);
        }

        return SCREEN_HANDLERS.register(id, screenHandlerSupplier);
    }

    public <T extends VillagerProfession> RegistrySupplier<T> registerVillagerProfession(String name, Supplier<T> professionSupplier) {
        return registerVillagerProfession(Identifier.fromNamespaceAndPath(this.modId, name), professionSupplier);
    }

    public <T extends VillagerProfession> RegistrySupplier<T> registerVillagerProfession(Identifier id, Supplier<T> professionSupplier) {
        if (Platform.isNeoForge()) {
            return VILLAGER_PROFESSIONS.register(id.getPath(), professionSupplier);
        }

        return VILLAGER_PROFESSIONS.register(id, professionSupplier);
    }

    public void registerCustomStat(String name) {
        registerCustomStat(Identifier.fromNamespaceAndPath(this.modId, name));
    }

    public void registerCustomStat(Identifier id) {
        if (Platform.isNeoForge()) {
            CUSTOM_STATS.register(id.getPath(), () -> id);
            return;
        }

        CUSTOM_STATS.register(id, () -> id);
    }
}
