package com.chimericdream.logallthethings.lavalog;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public final class LavaLogProperties {
    public static final BooleanProperty LAVALOGGED = BooleanProperty.create("lavalogged");

    private LavaLogProperties() {
    }

    /**
     * Reads {@link #LAVALOGGED} without throwing when the state lacks it. Our mixins can run on a
     * subclass of the target class that builds its own state definition without calling
     * {@code super.createBlockStateDefinition} (for example, a mod block that extends
     * {@code CrossCollisionBlock} directly), so that state never receives the property. Such a state
     * counts as not lava-logged.
     */
    public static boolean isLavaLogged(BlockState state) {
        return state.hasProperty(LAVALOGGED) && state.getValue(LAVALOGGED);
    }

    /** Sets {@link #LAVALOGGED} when the state has it. Otherwise returns the state unchanged. */
    public static BlockState withLavaLogged(BlockState state, boolean lavalogged) {
        return state.hasProperty(LAVALOGGED) ? state.setValue(LAVALOGGED, lavalogged) : state;
    }
}
