package com.chimericdream.bannertweaks.client.render.block.entity.state;

import net.minecraft.network.chat.Component;

public interface BannerBlockEntityRenderStateAccessor {
    Component bt$getCustomName();

    void bt$setCustomName(Component customName);
}
