package com.chimericdream.lib.registries;

import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
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
    public final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS;
    public final DeferredRegister<Identifier> CUSTOM_STATS;
    public final DeferredRegister<ComponentType<?>> CUSTOM_COMPONENTS;

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
        VILLAGER_PROFESSIONS = DeferredRegister.create(modId, (RegistryKey<Registry<VillagerProfession>>) Registries.VILLAGER_PROFESSION.getKey());
        CUSTOM_STATS = DeferredRegister.create(modId, (RegistryKey<Registry<Identifier>>) Registries.CUSTOM_STAT.getKey());
        CUSTOM_COMPONENTS = DeferredRegister.create(modId, (RegistryKey<Registry<ComponentType<?>>>) Registries.DATA_COMPONENT_TYPE.getKey());
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

    public <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final String name, final Supplier<T> supplier) {
        return registerBlockEntity(Identifier.of(this.modId, name), supplier);
    }

    public <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final Identifier id, final Supplier<T> supplier) {
        return BLOCK_ENTITY_TYPES.register(id, supplier);
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

    public <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(Identifier.of(this.modId, name), block);
    }

    public <T extends Block> RegistrySupplier<T> registerBlock(Identifier id, Supplier<T> block) {
        if (Platform.isNeoForge()) {
            return BLOCKS.register(id.getPath(), block);
        }

        return BLOCKS.register(id, block);
    }

    public <T extends Fluid> RegistrySupplier<T> registerFluid(String name, Supplier<T> fluidSupplier) {
        return registerFluid(Identifier.of(this.modId, name), fluidSupplier);
    }

    public <T extends Fluid> RegistrySupplier<T> registerFluid(Identifier id, Supplier<T> fluidSupplier) {
        return FLUIDS.register(id, fluidSupplier);
    }

    public <T extends Item> RegistrySupplier<T> registerItem(String name, Supplier<T> itemSupplier) {
        return registerItem(Identifier.of(this.modId, name), itemSupplier);
    }

    public <T extends Item> RegistrySupplier<T> registerItem(Identifier id, Supplier<T> itemSupplier) {
        if (Platform.isNeoForge()) {
            return ITEMS.register(id.getPath(), itemSupplier);
        }

        return ITEMS.register(id, itemSupplier);
    }

    public RegistrySupplier<ItemGroup> registerItemGroup(String id, Supplier<Block> iconBlock) {
        return ITEM_GROUPS.register(
            id,
            () -> CreativeTabRegistry.create(
                Text.translatable(id),
                () -> new ItemStack(iconBlock.get())
            )
        );
    }

    public <T extends EntityType<?>> RegistrySupplier<T> registerEntityType(String name, Supplier<T> entitySupplier) {
        return registerEntityType(Identifier.of(this.modId, name), entitySupplier);
    }

    public <T extends EntityType<?>> RegistrySupplier<T> registerEntityType(Identifier id, Supplier<T> entitySupplier) {
        if (Platform.isNeoForge()) {
            return ENTITY_TYPES.register(id.getPath(), entitySupplier);
        }

        return ENTITY_TYPES.register(id, entitySupplier);
    }

    public <T extends ScreenHandlerType<?>> RegistrySupplier<T> registerScreenHandler(String name, Supplier<T> screenHandlerSupplier) {
        return registerScreenHandler(Identifier.of(this.modId, name), screenHandlerSupplier);
    }

    public <T extends ScreenHandlerType<?>> RegistrySupplier<T> registerScreenHandler(Identifier id, Supplier<T> screenHandlerSupplier) {
        if (Platform.isNeoForge()) {
            return SCREEN_HANDLERS.register(id.getPath(), screenHandlerSupplier);
        }

        return SCREEN_HANDLERS.register(id, screenHandlerSupplier);
    }

    public <T extends VillagerProfession> RegistrySupplier<T> registerVillagerProfession(String name, Supplier<T> professionSupplier) {
        return registerVillagerProfession(Identifier.of(this.modId, name), professionSupplier);
    }

    public <T extends VillagerProfession> RegistrySupplier<T> registerVillagerProfession(Identifier id, Supplier<T> professionSupplier) {
        if (Platform.isNeoForge()) {
            return VILLAGER_PROFESSIONS.register(id.getPath(), professionSupplier);
        }

        return VILLAGER_PROFESSIONS.register(id, professionSupplier);
    }

    public void registerCustomStat(String name) {
        registerCustomStat(Identifier.of(this.modId, name));
    }

    public void registerCustomStat(Identifier id) {
        if (Platform.isNeoForge()) {
            CUSTOM_STATS.register(id.getPath(), () -> id);
            return;
        }

        CUSTOM_STATS.register(id, () -> id);
    }
}
