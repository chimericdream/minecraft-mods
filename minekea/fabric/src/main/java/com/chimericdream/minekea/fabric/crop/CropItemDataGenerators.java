package com.chimericdream.minekea.fabric.crop;

import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import com.chimericdream.minekea.fabric.util.ItemDataGeneratorGroup;

import java.util.ArrayList;
import java.util.List;

public class CropItemDataGenerators implements ItemDataGeneratorGroup {
    protected static final List<ChimericLibItemDataGenerator> ITEM_GENERATORS = new ArrayList<>();

    static {
        ITEM_GENERATORS.add(new WarpedWartItemDataGenerator());
    }

    @Override
    public List<ChimericLibItemDataGenerator> getItemGenerators() {
        return ITEM_GENERATORS;
    }
}
