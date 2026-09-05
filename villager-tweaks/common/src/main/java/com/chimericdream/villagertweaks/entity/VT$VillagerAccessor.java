package com.chimericdream.villagertweaks.entity;

import net.minecraft.world.entity.npc.villager.VillagerData;

public interface VT$VillagerAccessor {
    default VillagerData getVillagerData() {
        return null;
    }
}
