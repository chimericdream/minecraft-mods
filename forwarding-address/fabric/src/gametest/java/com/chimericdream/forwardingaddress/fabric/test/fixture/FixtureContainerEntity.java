package com.chimericdream.forwardingaddress.fabric.test.fixture;

import com.chimericdream.lib.inventories.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Supplier;

/**
 * A container block entity with {@link #VISIBLE} slots, plus optional hidden slots past
 * {@code getContainerSize()} the way Hopper X-Treme keeps its filter. Only the test mod uses it.
 */
public class FixtureContainerEntity extends BlockEntity implements ImplementedInventory {
    public static final int VISIBLE = 3;

    private final NonNullList<ItemStack> items;

    public FixtureContainerEntity(Supplier<BlockEntityType<?>> type, BlockPos pos, BlockState state, int hidden) {
        super(type.get(), pos, state);
        this.items = NonNullList.withSize(VISIBLE + hidden, ItemStack.EMPTY);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return VISIBLE;
    }

    public int hiddenSlots() {
        return items.size() - VISIBLE;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        ContainerHelper.saveAllItems(view, items);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        ContainerHelper.loadAllItems(view, items);
    }
}
