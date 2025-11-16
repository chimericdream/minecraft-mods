package com.chimericdream.minekea.entity.block.containers;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.containers.crates.CrateBlock;
import com.chimericdream.minekea.block.containers.crates.Crates;
import com.chimericdream.minekea.client.screen.crate.CrateScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.ContainerUser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CrateBlockEntity extends LootableContainerBlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    public static final Identifier ENTITY_ID = Identifier.of(ModInfo.MOD_ID, "entities/blocks/containers/crate");

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(CrateBlock.ROW_COUNT * 9, ItemStack.EMPTY);
    private final ViewerCountManager stateManager;
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
        this.stateManager = new ViewerCountManager() {
            protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
                self.playSound(state, SoundEvents.BLOCK_BARREL_OPEN);
                self.setOpen(state, true);
            }

            protected void onContainerClose(World world, BlockPos pos, BlockState state) {
                self.playSound(state, SoundEvents.BLOCK_BARREL_CLOSE);
                self.setOpen(state, false);
            }

            protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                self.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
            }

            public boolean isPlayerViewing(PlayerEntity player) {
                if (player.currentScreenHandler instanceof CrateScreenHandler) {
                    Inventory inventory = ((CrateScreenHandler) player.currentScreenHandler).getInventory();

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

    public static int getPlayersLookingInCrateCount(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CrateBlockEntity) {
                return ((CrateBlockEntity) blockEntity).stateManager.getViewerCount();
            }
        }

        return 0;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CrateScreenHandler(Crates.CRATE_SCREEN_HANDLER.get(), syncId, playerInventory, this);
    }

    @Override
    public Text getContainerName() {
        if (this.isTrapped) {
            return Text.translatable(CrateScreenHandler.TRAPPED_SCREEN_ID.toTranslationKey());
        }

        return Text.translatable(CrateScreenHandler.SCREEN_ID.toTranslationKey());
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.items;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.items = inventory;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, this.items);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        Inventories.readData(view, this.items);
    }

    @Override
    public void onOpen(ContainerUser user) {
        if (!this.removed && !user.asLivingEntity().isSpectator()) {
            this.stateManager.openContainer(user.asLivingEntity(), this.getWorld(), this.getPos(), this.getCachedState(), user.getContainerInteractionRange());
        }
    }

    @Override
    public void onClose(ContainerUser user) {
        if (!this.removed && !user.asLivingEntity().isSpectator()) {
            this.stateManager.closeContainer(user.asLivingEntity(), this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, CrateBlockEntity entity) {
        if (!entity.removed) {
            entity.stateManager.updateViewerCount(world, pos, state);
        }
    }

    void setOpen(BlockState state, boolean open) {
        if (this.world == null) {
            return;
        }

        this.world.setBlockState(this.getPos(), state.with(CrateBlock.OPEN, open), 3);
    }

    void playSound(BlockState state, SoundEvent soundEvent) {
        if (this.world == null) {
            return;
        }

        Direction.Axis axis = state.get(CrateBlock.AXIS);

        Vec3i vec3i;
        if (axis.isVertical()) {
            vec3i = Direction.UP.getVector();
        } else if (axis.test(Direction.NORTH)) {
            vec3i = Direction.NORTH.getVector();
        } else {
            vec3i = Direction.EAST.getVector();
        }

        double d = (double) this.pos.getX() + 0.5 + (double) vec3i.getX() / 2.0;
        double e = (double) this.pos.getY() + 0.5 + (double) vec3i.getY() / 2.0;
        double f = (double) this.pos.getZ() + 0.5 + (double) vec3i.getZ() / 2.0;

        this.world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.5F);
    }

    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.addSyncedBlockEvent(pos, block, 1, newViewerCount);

        if (isTrapped && oldViewerCount != newViewerCount) {
            world.updateNeighborsAlways(pos, block, null);
            world.updateNeighborsAlways(pos.down(), block, null);
        }
    }
}
