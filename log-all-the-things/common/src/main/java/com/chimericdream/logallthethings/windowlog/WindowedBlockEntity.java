package com.chimericdream.logallthethings.windowlog;

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
 * Stores the two {@link BlockState}s a window-logged slab/stair combines: the original block
 * ({@code hostState}) and the pane filling the rest of the space ({@code windowState}).
 * {@link WindowedBlock} delegates shape/collision to both; {@code windowlog.client.WindowedBlockEntityRenderer}
 * renders both via {@code SubmitNodeCollector#submitMovingBlock}, the same mechanism vanilla uses to
 * render the block a piston is currently pushing.
 */
public class WindowedBlockEntity extends BlockEntity {
    private BlockState hostState = Blocks.AIR.defaultBlockState();
    private BlockState windowState = Blocks.AIR.defaultBlockState();

    public WindowedBlockEntity(BlockPos pos, BlockState state) {
        super(WindowLogBlocks.WINDOWED_BLOCK_ENTITY.get(), pos, state);
    }

    public BlockState getHostState() {
        return hostState;
    }

    public void setHostState(BlockState hostState) {
        this.hostState = hostState;
    }

    public BlockState getWindowState() {
        return windowState;
    }

    public void setWindowState(BlockState windowState) {
        this.windowState = windowState;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("HostState", BlockState.CODEC, hostState);
        output.store("WindowState", BlockState.CODEC, windowState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        hostState = input.read("HostState", BlockState.CODEC).orElse(Blocks.AIR.defaultBlockState());
        windowState = input.read("WindowState", BlockState.CODEC).orElse(Blocks.AIR.defaultBlockState());
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
