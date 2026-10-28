package com.chimericdream.forwardingaddress.stub;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

/**
 * A deliberately plain stand-in for a block a removed mod used to register. It has the declared
 * properties so saved states load, full-cube collision, breaks by hand, and drops its own item. It
 * has no models or resources, so it shows the missing-model look until the migration converts it.
 */
public final class StubBlock extends Block {
    // A block builds its state definition inside Block's constructor, before any field of this class
    // exists, so the property list is handed over through a thread-local instead.
    private static final ThreadLocal<List<Property<?>>> PENDING = new ThreadLocal<>();

    private StubBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static StubBlock create(Identifier id, List<Property<?>> properties) {
        PENDING.set(properties);
        try {
            return new StubBlock(BlockBehaviour.Properties.of()
                .strength(0.5F)
                .setId(ResourceKey.create(Registries.BLOCK, id)));
        } finally {
            PENDING.remove();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        List<Property<?>> properties = PENDING.get();
        if (properties != null && !properties.isEmpty()) {
            builder.add(properties.toArray(new Property<?>[0]));
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}
