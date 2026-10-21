package com.chimericdream.polycraft.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Lets PolyCraft find the block entity behind a furnace menu. */
@Mixin(AbstractFurnaceMenu.class)
public interface PCAbstractFurnaceMenuAccessor {
    @Accessor("container")
    Container polycraft$container();
}
