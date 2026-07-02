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

public class SuspiciousClayBlock extends BrushableFloatingBlock {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "suspicious_clay");

    public SuspiciousClayBlock() {
        super(
            Blocks.CLAY,
            SoundEvents.BRUSH_GRAVEL,
            SoundEvents.BRUSH_GRAVEL_COMPLETED,
            BlockBehaviour.Properties
                .of()
                .mapColor(MapColor.CLAY)
                .strength(0.6F)
                .sound(SoundType.GRAVEL)
                .pushReaction(PushReaction.DESTROY)
                .setId(ResourceKey.create(Registries.BLOCK, BLOCK_ID))
        );
    }
}
