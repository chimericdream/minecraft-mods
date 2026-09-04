package com.chimericdream.effectivegear.ability;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.equipment.trim.TrimPattern;

// An active-use ability granted by wearing a full set of one trim pattern, triggered by the "use ability" keybind.
public interface TrimAbility {
    ResourceKey<TrimPattern> pattern();

    // Player is already confirmed to be wearing this ability's full pattern; returns whether it actually activated (e.g. false if on cooldown).
    boolean tryActivate(ServerPlayer player);
}
