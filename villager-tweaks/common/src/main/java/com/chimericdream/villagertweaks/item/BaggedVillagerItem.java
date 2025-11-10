package com.chimericdream.villagertweaks.item;

import com.chimericdream.villagertweaks.ModInfo;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static net.minecraft.block.Block.sideCoversSmallSquare;

public class BaggedVillagerItem extends Item {
    public static final Identifier ITEM_ID = Identifier.of(ModInfo.MOD_ID, "bagged_villager");

    public BaggedVillagerItem(Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();

        if (context.getSide() != Direction.UP || !sideCoversSmallSquare(world, context.getBlockPos(), Direction.UP)) {
            return ActionResult.FAIL;
        }

        if (!(world.getBlockState(context.getBlockPos().up(1)).isAir() && world.getBlockState(context.getBlockPos().up(2)).isAir())) {
            return ActionResult.FAIL;
        }

        return spawnVillager(context.getWorld(), context.getBlockPos().up(), context.getPlayer(), context.getHand());
    }

    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!user.isOnGround()) {
            return ActionResult.PASS;
        }

        return spawnVillager(world, user.getBlockPos(), user, hand);
    }

    private ActionResult spawnVillager(World world, BlockPos pos, PlayerEntity user, Hand hand) {
        VillagerEntity villager = EntityType.VILLAGER.create(world, SpawnReason.BUCKET);
        assert villager != null;

        try {
            NbtComponent component = user.getStackInHand(hand).get(DataComponentTypes.CUSTOM_DATA);
            assert component != null;
            ReadView view = NbtReadView.create(ErrorReporter.EMPTY, user.getRegistryManager(), component.copyNbt());

            villager.setCustomName(user.getStackInHand(hand).get(DataComponentTypes.CUSTOM_NAME));
            villager.readCustomData(view);
            villager.refreshPositionAndAngles(pos, 0, 0);

            world.spawnEntity(villager);

            user.setStackInHand(hand, Items.BUNDLE.getDefaultStack());
        } catch (Exception e) {
            return ActionResult.FAIL;
        }

        return ActionResult.SUCCESS;
    }
}
