package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public class BrushableFloatingBlock extends BrushableBlock {
    private final ResourceKey<LootTable> lootTable;

    public BrushableFloatingBlock(Block baseBlock, SoundEvent brushingSound, SoundEvent brushingCompleteSound, Identifier blockId, BlockBehaviour.Properties settings) {
        super(baseBlock, brushingSound, brushingCompleteSound, settings);
        this.lootTable = ResourceKey.create(Registries.LOOT_TABLE, blockId.withPrefix("blocks/"));
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        ATBrushableBlockEntity blockEntity = new ATBrushableBlockEntity(pos, state);
        blockEntity.setLootTable(this.lootTable, RandomSource.create().nextLong());
        return blockEntity;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockEntity var6 = world.getBlockEntity(pos);
        if (var6 instanceof ATBrushableBlockEntity brushableBlockEntity) {
            brushableBlockEntity.scheduledTick(world);
        }
    }

    @Override
    public void onBrokenAfterFall(Level world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
    }
}
