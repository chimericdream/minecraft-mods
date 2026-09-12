package com.chimericdream.allhallowssteve.client.render;

import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.CardinalLighting;

public class DecoratedPumpkinRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public PumpkinStencilsComponent stencils = PumpkinStencilsComponent.EMPTY;
    public CardinalLighting cardinalLighting = CardinalLighting.DEFAULT;
    /** {@code ""} unless this is a {@code LitDecoratedPumpkinBlock}, in which case its overlay suffix. */
    public String overlaySuffix = "";
}
