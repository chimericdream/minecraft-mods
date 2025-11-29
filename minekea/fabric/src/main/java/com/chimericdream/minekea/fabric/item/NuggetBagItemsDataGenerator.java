package com.chimericdream.minekea.fabric.item;

import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import com.chimericdream.minekea.fabric.item.currency.NuggetBagItemDataGenerator;
import com.chimericdream.minekea.fabric.util.ItemDataGeneratorGroup;
import com.chimericdream.minekea.item.NuggetBags;

import java.util.ArrayList;
import java.util.List;

public class NuggetBagItemsDataGenerator implements ItemDataGeneratorGroup {
    protected static final List<ChimericLibItemDataGenerator> ITEM_GENERATORS = new ArrayList<>();

    static {
        NuggetBags.ITEMS.forEach(item -> ITEM_GENERATORS.add(new NuggetBagItemDataGenerator(item.get())));
    }

    @Override
    public List<ChimericLibItemDataGenerator> getItemGenerators() {
        return ITEM_GENERATORS;
    }
}
