package com.chimericdream.minekea.fabric.crop;

import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.util.BlockDataGeneratorGroup;

import java.util.ArrayList;
import java.util.List;

public class CropBlockDataGenerators implements BlockDataGeneratorGroup {
    protected static final List<ChimericLibBlockDataGenerator> BLOCK_GENERATORS = new ArrayList<>();

    static {
        BLOCK_GENERATORS.add(new WarpedWartCropDataGenerator());
    }

    @Override
    public List<ChimericLibBlockDataGenerator> getBlockGenerators() {
        return BLOCK_GENERATORS;
    }
}
