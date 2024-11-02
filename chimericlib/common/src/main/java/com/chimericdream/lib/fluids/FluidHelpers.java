package com.chimericdream.lib.fluids;

import net.minecraft.fluid.Fluid;
import net.minecraft.text.Text;

public class FluidHelpers {
    public static Text getFluidName(Fluid fluid) {
        return fluid.getDefaultState().getBlockState().getBlock().getName();
    }
}
