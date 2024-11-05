package com.chimericdream.miniblockmerchants.registry;

import com.chimericdream.miniblockmerchants.MiniblockMerchantsMod;
import com.chimericdream.miniblockmerchants.ModInfo;
import com.chimericdream.miniblockmerchants.item.ModItems;
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
import net.minecraft.village.VillagerProfession;

import java.util.function.Supplier;

public class ModRegistries {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<Fluid>>) Registries.FLUID.getKey());
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<Block>>) Registries.BLOCK.getKey());
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<BlockEntityType<?>>>) Registries.BLOCK_ENTITY_TYPE.getKey());
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<Item>>) Registries.ITEM.getKey());
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<ItemGroup> ITEM_GROUPS = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<ItemGroup>>) Registries.ITEM_GROUP.getKey());
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<EntityType<?>>>) Registries.ENTITY_TYPE.getKey());
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<ScreenHandlerType<?>> SCREEN_HANDLERS = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<ScreenHandlerType<?>>>) Registries.SCREEN_HANDLER.getKey());
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<VillagerProfession>>) Registries.VILLAGER_PROFESSION.getKey());
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Identifier> CUSTOM_STATS = DeferredRegister.create(ModInfo.MOD_ID, (RegistryKey<Registry<Identifier>>) Registries.CUSTOM_STAT.getKey());

//    public static final RegistrySupplier<ItemGroup> CONVERSION_ITEM_GROUP = ITEM_GROUPS.register(
//        "item_group.minekea.blocks.furniture",
//        () -> CreativeTabRegistry.create(
//            Text.translatable("item_group.minekea.blocks.furniture"),
//            () -> new ItemStack(Tables.BLOCKS.getFirst().get())
//        )
//    );

    public static void init() {
        ModItems.init();

        MiniblockMerchantsMod.LOGGER.debug("Registering fluids");
        FLUIDS.register();

        MiniblockMerchantsMod.LOGGER.debug("Registering blocks");
        BLOCKS.register();

        MiniblockMerchantsMod.LOGGER.debug("Registering block entities");
        BLOCK_ENTITY_TYPES.register();

        MiniblockMerchantsMod.LOGGER.debug("Registering items");
        ITEMS.register();

        MiniblockMerchantsMod.LOGGER.debug("Registering item groups");
        ITEM_GROUPS.register();

        MiniblockMerchantsMod.LOGGER.debug("Registering entities");
        ENTITY_TYPES.register();

        MiniblockMerchantsMod.LOGGER.debug("Registering screen handlers");
        SCREEN_HANDLERS.register();

        MiniblockMerchantsMod.LOGGER.debug("Registering villager professions");
        VILLAGER_PROFESSIONS.register();

        MiniblockMerchantsMod.LOGGER.debug("Registering custom stats");
        CUSTOM_STATS.register();
    }

    public static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final String name, final Supplier<T> supplier) {
        return registerBlockEntity(Identifier.of(ModInfo.MOD_ID, name), supplier);
    }

    public static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final Identifier id, final Supplier<T> supplier) {
        Registrar<BlockEntityType<?>> registrar = BLOCK_ENTITY_TYPES.getRegistrar();

        return registrar.register(id, supplier);
    }

    public static RegistrySupplier<Block> registerWithItem(String name, Supplier<Block> supplier) {
        return registerWithItem(name, supplier, new Item.Settings());
    }

    public static RegistrySupplier<Block> registerWithItem(Identifier id, Supplier<Block> supplier) {
        return registerWithItem(id, supplier, new Item.Settings());
    }

    public static RegistrySupplier<Block> registerWithItem(String name, Supplier<Block> supplier, Item.Settings itemSettings) {
        return registerWithItem(Identifier.of(ModInfo.MOD_ID, name), supplier, itemSettings);
    }

    public static RegistrySupplier<Block> registerWithItem(Identifier id, Supplier<Block> supplier, Item.Settings itemSettings) {
        RegistrySupplier<Block> block = registerBlock(id, supplier);

        registerItem(id, () -> new BlockItem(block.get(), itemSettings));

        return block;
    }

    public static <T extends Block> RegistrySupplier<T> registerBlock(Identifier path, Supplier<T> block) {
        Registrar<Block> registrar = BLOCKS.getRegistrar();

        if (Platform.isNeoForge()) {
            return BLOCKS.register(path.getPath(), block);
        }

        return registrar.register(path, block);
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(Identifier path, Supplier<T> itemSupplier) {
        Registrar<Item> registrar = ITEMS.getRegistrar();

        if (Platform.isNeoForge()) {
            return ITEMS.register(path.getPath(), itemSupplier);
        }

        return registrar.register(path, itemSupplier);
    }

    public static <T extends EntityType<?>> RegistrySupplier<T> registerEntityType(Identifier path, Supplier<T> entitySupplier) {
        Registrar<EntityType<?>> registrar = ENTITY_TYPES.getRegistrar();

        if (Platform.isNeoForge()) {
            return ENTITY_TYPES.register(path.getPath(), entitySupplier);
        }

        return registrar.register(path, entitySupplier);
    }

    public static <T extends ScreenHandlerType<?>> RegistrySupplier<T> registerScreenHandler(Identifier path, Supplier<T> screenHandlerSupplier) {
        Registrar<ScreenHandlerType<?>> registrar = SCREEN_HANDLERS.getRegistrar();

        if (Platform.isNeoForge()) {
            return SCREEN_HANDLERS.register(path.getPath(), screenHandlerSupplier);
        }

        return registrar.register(path, screenHandlerSupplier);
    }

    public static <T extends VillagerProfession> RegistrySupplier<T> registerVillagerProfession(Identifier path, Supplier<T> professionSupplier) {
        Registrar<VillagerProfession> registrar = VILLAGER_PROFESSIONS.getRegistrar();

        if (Platform.isNeoForge()) {
            return VILLAGER_PROFESSIONS.register(path.getPath(), professionSupplier);
        }

        return registrar.register(path, professionSupplier);
    }

    public static void registerCustomStat(Identifier id) {
        Registrar<Identifier> registrar = CUSTOM_STATS.getRegistrar();

        if (Platform.isNeoForge()) {
            CUSTOM_STATS.register(id.getPath(), () -> id);
            return;
        }

        registrar.register(id, () -> id);
    }
}
