package com.chimericdream.minekea.fabric.item;

import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import com.chimericdream.minekea.fabric.item.ingredients.HoneyBucketItemDataGenerator;
import com.chimericdream.minekea.fabric.util.ItemDataGeneratorGroup;

import java.util.ArrayList;
import java.util.List;

public class HoneyBucketItemsDataGenerator implements ItemDataGeneratorGroup {
    protected static final List<ChimericLibItemDataGenerator> ITEM_GENERATORS = new ArrayList<>();

    static {
        ITEM_GENERATORS.add(new HoneyBucketItemDataGenerator());
    }

    @Override
    public List<ChimericLibItemDataGenerator> getItemGenerators() {
        return ITEM_GENERATORS;
    }
}
