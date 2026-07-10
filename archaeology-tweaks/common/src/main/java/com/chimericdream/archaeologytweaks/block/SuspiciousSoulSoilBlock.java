package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class SuspiciousSoulSoilBlock extends BrushableFloatingBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "suspicious_soul_soil");

    public SuspiciousSoulSoilBlock() {
        super(
            Blocks.SOUL_SOIL,
            SoundEvents.BRUSH_GRAVEL,
            SoundEvents.BRUSH_GRAVEL_COMPLETED,
            BlockBehaviour.Properties
                .of()
                .mapColor(MapColor.COLOR_BROWN)
                .strength(0.5F)
                .sound(SoundType.SOUL_SOIL)
                .pushReaction(PushReaction.DESTROY)
                .setId(ResourceKey.create(Registries.BLOCK, BLOCK_ID))
        );
    }
}
