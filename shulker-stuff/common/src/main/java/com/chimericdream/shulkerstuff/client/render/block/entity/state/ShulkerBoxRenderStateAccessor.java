package com.chimericdream.shulkerstuff.client.render.block.entity.state;

public interface ShulkerBoxRenderStateAccessor {
    boolean ss$useVanillaColor();

    void ss$setUseVanillaColor(boolean useVanillaColor);

    int ss$getLidColor();

    void ss$setLidColor(int lidColor);

    int ss$getBaseColor();

    void ss$setBaseColor(int baseColor);
}
