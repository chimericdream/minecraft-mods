package com.chimericdream.lib.fabric.test.fixture;

import com.chimericdream.lib.inventories.ImplementedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * A throwaway container block entity backed by {@link ImplementedInventory}, used only by the
 * GameTests to prove the library's {@code Container} contract and NBT persistence work end to end.
 * Registered only from the test mod's entrypoint, so it never ships in the production jar.
 */
public class TestContainerBlockEntity extends BlockEntity implements ImplementedInventory {
    public static final int SIZE = 27;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public TestContainerBlockEntity(BlockPos pos, BlockState state) {
        super(TestFixtures.TEST_CONTAINER_BE.get(), pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
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
