package com.chimericdream.miniblockmerchants.item;

import com.chimericdream.miniblockmerchants.util.TextHelpers;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class VillagerConversionItem extends Item {
    public final String id;
    public final String profession;

    public VillagerConversionItem(String id, String profession) {
        super(new Item.Settings().arch$tab(ItemGroups.FUNCTIONAL));

        this.id = id;
        this.profession = profession;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(TextHelpers.getTooltip("tooltip.conversion_item.lore"));
    }
}
