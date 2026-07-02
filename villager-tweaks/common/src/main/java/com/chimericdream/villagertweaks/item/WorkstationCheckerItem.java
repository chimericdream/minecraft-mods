package com.chimericdream.villagertweaks.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WorkstationCheckerItem extends Item {
    public WorkstationCheckerItem(Properties settings) {
        super(settings);
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        return InteractionResult.PASS;
    }
}
