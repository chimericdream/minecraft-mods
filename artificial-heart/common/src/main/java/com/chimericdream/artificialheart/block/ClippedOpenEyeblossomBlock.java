package com.chimericdream.artificialheart.block;

import com.chimericdream.artificialheart.ModInfo;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ClippedOpenEyeblossomBlock extends BaseClippedEyeblossomBlock {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "clipped_open_eyeblossom");
    public static final RegistryKey<Block> BLOCK_REGISTRY_KEY = RegistryKey.of(RegistryKeys.BLOCK, BLOCK_ID);
    public static final RegistryKey<Item> ITEM_REGISTRY_KEY = RegistryKey.of(RegistryKeys.ITEM, BLOCK_ID);

    public static final Identifier POTTED_BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "potted_clipped_open_eyeblossom");
    public static final RegistryKey<Block> POTTED_BLOCK_REGISTRY_KEY = RegistryKey.of(RegistryKeys.BLOCK, POTTED_BLOCK_ID);
    public static final RegistryKey<Item> POTTED_ITEM_REGISTRY_KEY = RegistryKey.of(RegistryKeys.ITEM, POTTED_BLOCK_ID);

    public ClippedOpenEyeblossomBlock() {
        super(StatusEffects.BLINDNESS, 11.0F, Settings.copy(Blocks.OPEN_EYEBLOSSOM).registryKey(BLOCK_REGISTRY_KEY));
    }

    public static FlowerPotBlock getPottedBlock() {
        return new FlowerPotBlock(
            ModBlocks.CLIPPED_OPEN_EYEBLOSSOM_BLOCK.get(),
            AbstractBlock.Settings.create()
                .breakInstantly()
                .nonOpaque()
                .pistonBehavior(PistonBehavior.DESTROY)
                .registryKey(POTTED_BLOCK_REGISTRY_KEY)
        );
    }
}
