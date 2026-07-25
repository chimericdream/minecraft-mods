package com.chimericdream.hopperxtreme.entity;

import com.chimericdream.hopperxtreme.block.AbstractMultiHopperBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Shared connection wiring for the down-pulling multi variants (the Xtreme multi-hopper and the
 * glazed multi-hopper), both of which back onto {@link AbstractMultiHopperBlock} and connect on
 * their four horizontal sides plus {@code DOWN}.
 */
public abstract class AbstractDownMultiXtremeHopperBlockEntity extends AbstractMultiXtremeHopperBlockEntity {
    protected AbstractDownMultiXtremeHopperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int cooldownInTicks, boolean withFilter) {
        super(type, pos, state, cooldownInTicks, withFilter);
    }

    @Override
    protected BooleanProperty northConnectedProperty() {
        return AbstractMultiHopperBlock.NORTH_CONNECTED;
    }

    @Override
    protected BooleanProperty southConnectedProperty() {
        return AbstractMultiHopperBlock.SOUTH_CONNECTED;
    }

    @Override
    protected BooleanProperty eastConnectedProperty() {
        return AbstractMultiHopperBlock.EAST_CONNECTED;
    }

    @Override
    protected BooleanProperty westConnectedProperty() {
        return AbstractMultiHopperBlock.WEST_CONNECTED;
    }

    @Override
    protected BooleanProperty verticalConnectedProperty() {
        return AbstractMultiHopperBlock.DOWN_CONNECTED;
    }

    @Override
    protected Direction verticalDirection() {
        return Direction.DOWN;
    }

    @Override
    protected Direction extractSide() {
        return Direction.DOWN;
    }

    @Override
    protected double inputBlockYOffset() {
        return 1.0;
    }

    @Override
    protected double levelYOffset() {
        return 0.5;
    }
}
