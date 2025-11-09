package com.chimericdream.villagertweaks.item;

import com.chimericdream.villagertweaks.ModInfo;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BaggedVillagerItem extends Item {
    public static final Identifier ITEM_ID = Identifier.of(ModInfo.MOD_ID, "bagged_villager");

    public BaggedVillagerItem(Settings settings) {
        super(settings);
    }

    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        VillagerEntity villager = EntityType.VILLAGER.create(world, SpawnReason.BUCKET);
        assert villager != null;

        try {
            NbtComponent component = user.getStackInHand(hand).get(DataComponentTypes.CUSTOM_DATA);
            assert component != null;
            ReadView view = NbtReadView.create(ErrorReporter.EMPTY, user.getRegistryManager(), component.copyNbt());

            villager.readCustomData(view);
            villager.refreshPositionAndAngles(user.getBlockPos(), 0, 0);

            world.spawnEntity(villager);

            user.getStackInHand(hand).decrement(1);
            user.setStackInHand(hand, new ItemStack(Items.BUNDLE));
        } catch (Exception e) {
            return ActionResult.FAIL;
        }

        return ActionResult.SUCCESS;
    }
}
