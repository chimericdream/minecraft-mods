package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class SuspiciousSoulSandBlock extends BrushableFloatingNonFullBlock {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "suspicious_soul_sand");

    public SuspiciousSoulSandBlock() {
        super(
            Blocks.SOUL_SAND,
            SoundEvents.BRUSH_SAND,
            SoundEvents.BRUSH_SAND_COMPLETED,
            BlockBehaviour.Properties
                .of()
                .mapColor(MapColor.COLOR_BROWN)
                .strength(0.5F)
                .speedFactor(0.4F)
                .sound(SoundType.SOUL_SAND)
                .isRedstoneConductor(Blocks::always)
                .isViewBlocking(Blocks::always)
                .isSuffocating(Blocks::always)
                .pushReaction(PushReaction.DESTROY)
                .setId(ResourceKey.create(Registries.BLOCK, BLOCK_ID))
        );
    }
}
