package com.chimericdream.logallthethings.snowlog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the two {@link BlockState}s a snow-logged slab/stair/wall/fence/chain/bars/pane combines:
 * the original block ({@code hostState}) and the snow layered onto it ({@code snowState}, a real
 * {@code minecraft:snow} state carrying its own {@code SnowLayerBlock.LAYERS} value). Mirrors
 * {@code carpetlog.CarpetedBlockEntity} exactly - see that class for the rendering/shape rationale.
 */
public class SnowedBlockEntity extends BlockEntity {
    private BlockState hostState = Blocks.AIR.defaultBlockState();
    private BlockState snowState = Blocks.AIR.defaultBlockState();

    public SnowedBlockEntity(BlockPos pos, BlockState state) {
        super(SnowLogBlocks.SNOWED_BLOCK_ENTITY.get(), pos, state);
    }

    public BlockState getHostState() {
        return hostState;
    }

    public void setHostState(BlockState hostState) {
        this.hostState = hostState;
    }

    public BlockState getSnowState() {
        return snowState;
    }

    public void setSnowState(BlockState snowState) {
        this.snowState = snowState;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("HostState", BlockState.CODEC, hostState);
        output.store("SnowState", BlockState.CODEC, snowState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        hostState = input.read("HostState", BlockState.CODEC).orElse(Blocks.AIR.defaultBlockState());
        snowState = input.read("SnowState", BlockState.CODEC).orElse(Blocks.AIR.defaultBlockState());
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }
}
