package com.chimericdream.polycraft.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Lets PolyCraft find who is using a smithing table. */
@Mixin(ItemCombinerMenu.class)
public interface PCItemCombinerMenuAccessor {
    @Accessor("player")
    Player polycraft$player();
}
