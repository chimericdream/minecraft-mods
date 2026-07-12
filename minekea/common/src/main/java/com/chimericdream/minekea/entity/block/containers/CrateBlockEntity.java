package com.chimericdream.minekea.entity.block.containers;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.containers.crates.CrateBlock;
import com.chimericdream.minekea.block.containers.crates.Crates;
import com.chimericdream.minekea.client.screen.crate.CrateScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class CrateBlockEntity extends RandomizableContainerBlockEntity implements MenuProvider, ImplementedInventory {
    public static final Identifier ENTITY_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "entities/blocks/containers/crate");

    private NonNullList<ItemStack> items = NonNullList.withSize(CrateBlock.ROW_COUNT * 9, ItemStack.EMPTY);
    private final ContainerOpenersCounter stateManager;
    private final boolean isTrapped;

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        this(Crates.CRATE_BLOCK_ENTITY.get(), pos, state, false);
    }

    public CrateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, false);
    }

    public CrateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean isTrapped) {
        super(type, pos, state);

        this.isTrapped = isTrapped;

        CrateBlockEntity self = this;
        this.stateManager = new ContainerOpenersCounter() {
            protected void onOpen(Level world, BlockPos pos, BlockState state) {
                self.playSound(state, SoundEvents.BARREL_OPEN);
                self.setOpen(state, true);
            }

            protected void onClose(Level world, BlockPos pos, BlockState state) {
                self.playSound(state, SoundEvents.BARREL_CLOSE);
                self.setOpen(state, false);
            }

            protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                self.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
            }

            public boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof CrateScreenHandler) {
                    Container inventory = ((CrateScreenHandler) player.containerMenu).getInventory();

                    return inventory == self;
                } else {
                    return false;
                }
            }
        };
    }

    public boolean isTrapped() {
        return isTrapped;
    }

    public static int getPlayersLookingInCrateCount(BlockGetter world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CrateBlockEntity) {
                return ((CrateBlockEntity) blockEntity).stateManager.getOpenerCount();
            }
        }

        return 0;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new CrateScreenHandler(Crates.CRATE_SCREEN_HANDLER.get(), syncId, playerInventory, this);
    }

    @Override
    public @NotNull Component getDefaultName() {
        if (this.isTrapped) {
            return Component.translatable(CrateScreenHandler.TRAPPED_SCREEN_ID.toLanguageKey());
        }

        return Component.translatable(CrateScreenHandler.SCREEN_ID.toLanguageKey());
    }

    @Override
    protected void setItems(NonNullList<ItemStack> inventory) {
        this.items = inventory;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        ContainerHelper.saveAllItems(view, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        ContainerHelper.loadAllItems(view, this.items);
    }

    @Override
    public void startOpen(ContainerUser user) {
        if (!this.remove && !user.getLivingEntity().isSpectator()) {
            this.stateManager.incrementOpeners(user.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState(), user.getContainerInteractionRange());
        }
    }

    @Override
    public void stopOpen(ContainerUser user) {
        if (!this.remove && !user.getLivingEntity().isSpectator()) {
            this.stateManager.decrementOpeners(user.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, CrateBlockEntity entity) {
        if (!entity.remove) {
            entity.stateManager.recheckOpeners(world, pos, state);
        }
    }

    void setOpen(BlockState state, boolean open) {
        if (this.level == null) {
            return;
        }

        this.level.setBlock(this.getBlockPos(), state.setValue(CrateBlock.OPEN, open), 3);
    }

    void playSound(BlockState state, SoundEvent soundEvent) {
        if (this.level == null) {
            return;
        }

        Direction.Axis axis = state.getValue(CrateBlock.AXIS);

        Vec3i vec3i;
        if (axis.isVertical()) {
            vec3i = Direction.UP.getUnitVec3i();
        } else if (axis.test(Direction.NORTH)) {
            vec3i = Direction.NORTH.getUnitVec3i();
        } else {
            vec3i = Direction.EAST.getUnitVec3i();
        }

        double d = (double) this.worldPosition.getX() + 0.5 + (double) vec3i.getX() / 2.0;
        double e = (double) this.worldPosition.getY() + 0.5 + (double) vec3i.getY() / 2.0;
        double f = (double) this.worldPosition.getZ() + 0.5 + (double) vec3i.getZ() / 2.0;

        this.level.playSound(null, d, e, f, soundEvent, SoundSource.BLOCKS, 0.5F, this.level.getRandom().nextFloat() * 0.1F + 0.5F);
    }

    protected void onViewerCountUpdate(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.blockEvent(pos, block, 1, newViewerCount);

        if (isTrapped && oldViewerCount != newViewerCount) {
            world.updateNeighborsAt(pos, block, null);
            world.updateNeighborsAt(pos.below(), block, null);
        }
    }
}
