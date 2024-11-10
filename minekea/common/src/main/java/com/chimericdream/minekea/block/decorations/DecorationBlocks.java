package com.chimericdream.minekea.block.decorations;

import com.chimericdream.minekea.block.decorations.candles.VotiveCandles;
import com.chimericdream.minekea.block.decorations.lighting.EndlessRodBlock;
import com.chimericdream.minekea.block.decorations.lighting.Lanterns;
import com.chimericdream.minekea.util.ModThingGroup;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class DecorationBlocks implements ModThingGroup {
    public static final List<RegistrySupplier<Block>> BLOCKS = new ArrayList<>();

    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> ENDLESS_ROD = REGISTRY_HELPER.registerWithItem(EndlessRodBlock.BLOCK_ID, EndlessRodBlock::new, new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL));
    @SuppressWarnings("UnstableApiUsage")
    public static final RegistrySupplier<Block> FAKE_CAKE = REGISTRY_HELPER.registerWithItem(FakeCakeBlock.BLOCK_ID, FakeCakeBlock::new, new Item.Settings().arch$tab(ItemGroups.BUILDING_BLOCKS));

    static {
        BLOCKS.add(ENDLESS_ROD);
        BLOCKS.add(FAKE_CAKE);
        BLOCKS.addAll(Lanterns.BLOCKS);
        BLOCKS.addAll(VotiveCandles.BLOCKS);
    }
}
