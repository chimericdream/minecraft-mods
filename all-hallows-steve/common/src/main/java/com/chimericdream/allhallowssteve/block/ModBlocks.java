package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.entity.CarvingStationBlockEntity;
import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
import com.chimericdream.allhallowssteve.client.screen.CarvingStationScreenHandler;
import com.chimericdream.allhallowssteve.item.DecoratedPumpkinBlockItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

public class ModBlocks {
    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Properties FUNCTIONAL_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS);

    @SuppressWarnings("UnstableApiUsage")
    private static final Item.Properties NATURAL_SETTINGS = new Item.Properties().arch$tab(CreativeModeTabs.NATURAL_BLOCKS);

    public static final RegistrySupplier<Block> CARVING_STATION = REGISTRY_HELPER.registerWithItem(CarvingStationBlock.BLOCK_ID, CarvingStationBlock::new, FUNCTIONAL_SETTINGS);

    public static final RegistrySupplier<BlockEntityType<CarvingStationBlockEntity>> CARVING_STATION_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        CarvingStationBlockEntity.ENTITY_ID,
        () -> new BlockEntityType<>(
            CarvingStationBlockEntity::new,
            Set.of(CARVING_STATION.get())
        )
    );

    public static final RegistrySupplier<MenuType<CarvingStationScreenHandler>> CARVING_STATION_SCREEN_HANDLER = REGISTRY_HELPER.registerScreenHandler(
        CarvingStationScreenHandler.SCREEN_ID,
        () -> new MenuType<>(CarvingStationScreenHandler::new, FeatureFlagSet.of())
    );

    public static final RegistrySupplier<Block> DECORATED_PUMPKIN = registerDecoratedPumpkinVariant(DecoratedPumpkinBlock.BLOCK_ID, DecoratedPumpkinBlock::new, NATURAL_SETTINGS);

    /** The four lit variants (see {@link LitDecoratedPumpkinBlock}), one per torch color. */
    public static final List<RegistrySupplier<Block>> LIT_DECORATED_PUMPKINS = new ArrayList<>();

    public static final RegistrySupplier<Block> LIT_DECORATED_PUMPKIN = registerLitDecoratedPumpkin(15, "_lit", "Lit Decorated Pumpkin", Items.TORCH);
    public static final RegistrySupplier<Block> LIT_DECORATED_PUMPKIN_BLUE = registerLitDecoratedPumpkin(10, "_lit_blue", "Blue Lit Decorated Pumpkin", Items.SOUL_TORCH);
    public static final RegistrySupplier<Block> LIT_DECORATED_PUMPKIN_GREEN = registerLitDecoratedPumpkin(14, "_lit_green", "Green Lit Decorated Pumpkin", Items.COPPER_TORCH);
    public static final RegistrySupplier<Block> LIT_DECORATED_PUMPKIN_RED = registerLitDecoratedPumpkin(7, "_lit_red", "Red Lit Decorated Pumpkin", Items.REDSTONE_TORCH);

    private static RegistrySupplier<Block> registerLitDecoratedPumpkin(int lightLevel, String overlaySuffix, String displayName, Item torchItem) {
        Identifier blockId = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "decorated_pumpkin" + overlaySuffix);

        RegistrySupplier<Block> block = registerDecoratedPumpkinVariant(
            blockId,
            () -> new LitDecoratedPumpkinBlock(blockId, lightLevel, overlaySuffix, displayName, torchItem),
            NATURAL_SETTINGS
        );

        LIT_DECORATED_PUMPKINS.add(block);

        return block;
    }

    /**
     * Like {@code REGISTRY_HELPER.registerWithItem}, but with a {@link DecoratedPumpkinBlockItem}
     * instead of a plain {@code BlockItem} so every decorated-pumpkin variant gets the dye
     * color/stencil tooltip.
     */
    private static RegistrySupplier<Block> registerDecoratedPumpkinVariant(Identifier id, Supplier<Block> supplier, Item.Properties itemSettings) {
        RegistrySupplier<Block> block = REGISTRY_HELPER.registerBlock(id, supplier);

        REGISTRY_HELPER.registerItem(
            id,
            () -> new DecoratedPumpkinBlockItem(block.get(), itemSettings.setId(ResourceKey.create(Registries.ITEM, id)))
        );

        return block;
    }

    public static final RegistrySupplier<BlockEntityType<DecoratedPumpkinBlockEntity>> DECORATED_PUMPKIN_BLOCK_ENTITY = REGISTRY_HELPER.registerBlockEntity(
        DecoratedPumpkinBlockEntity.ENTITY_ID,
        () -> new BlockEntityType<>(
            DecoratedPumpkinBlockEntity::new,
            Set.of(
                DECORATED_PUMPKIN.get(),
                LIT_DECORATED_PUMPKIN.get(),
                LIT_DECORATED_PUMPKIN_BLUE.get(),
                LIT_DECORATED_PUMPKIN_GREEN.get(),
                LIT_DECORATED_PUMPKIN_RED.get()
            )
        )
    );

    /** Whether {@code stack} is any decorated pumpkin (lit or not) — the pumpkin slot in the carving station accepts all of them. */
    public static boolean isDecoratedPumpkinItem(ItemStack stack) {
        if (stack.is(DECORATED_PUMPKIN.get().asItem())) {
            return true;
        }

        for (RegistrySupplier<Block> lit : LIT_DECORATED_PUMPKINS) {
            if (stack.is(lit.get().asItem())) {
                return true;
            }
        }

        return false;
    }

    public static void init() {
    }
}
