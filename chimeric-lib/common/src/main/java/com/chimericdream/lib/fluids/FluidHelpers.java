package com.chimericdream.lib.fluids;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;

public class FluidHelpers {
    public static Component getFluidName(Fluid fluid) {
        return fluid.defaultFluidState().createLegacyBlock().getBlock().getName();
    }
}
