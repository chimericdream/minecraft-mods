package com.chimericdream.artificialheart.block;

import com.chimericdream.artificialheart.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public class ClippedEyeblossomBlock extends BaseClippedEyeblossomBlock {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "clipped_eyeblossom");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public static final ResourceLocation POTTED_BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "potted_clipped_eyeblossom");
    public static final ResourceKey<Block> POTTED_BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, POTTED_BLOCK_ID);
    public static final ResourceKey<Item> POTTED_ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, POTTED_BLOCK_ID);

    public ClippedEyeblossomBlock() {
        super(MobEffects.NAUSEA, 7.0F, Properties.ofFullCopy(Blocks.CLOSED_EYEBLOSSOM).setId(BLOCK_REGISTRY_KEY));
    }

    public static FlowerPotBlock getPottedBlock() {
        return new FlowerPotBlock(
            ModBlocks.CLIPPED_EYEBLOSSOM_BLOCK.get(),
            BlockBehaviour.Properties.of()
                .instabreak()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY)
                .setId(POTTED_BLOCK_REGISTRY_KEY)
        );
    }
}
