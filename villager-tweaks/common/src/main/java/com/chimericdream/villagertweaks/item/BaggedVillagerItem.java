package com.chimericdream.villagertweaks.item;

import com.chimericdream.villagertweaks.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.Block.canSupportCenter;

public class BaggedVillagerItem extends Item {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "bagged_villager");

    public BaggedVillagerItem(Properties settings) {
        super(settings);
    }

    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();

        if (context.getClickedFace() != Direction.UP || !canSupportCenter(world, context.getClickedPos(), Direction.UP)) {
            return InteractionResult.FAIL;
        }

        if (!(world.getBlockState(context.getClickedPos().above(1)).isAir() && world.getBlockState(context.getClickedPos().above(2)).isAir())) {
            return InteractionResult.FAIL;
        }

        return spawnVillager(context.getLevel(), context.getClickedPos().above(), context.getPlayer(), context.getHand());
    }

    public @NotNull InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!user.onGround()) {
            return InteractionResult.PASS;
        }

        return spawnVillager(world, user.blockPosition(), user, hand);
    }

    private InteractionResult spawnVillager(Level world, BlockPos pos, Player user, InteractionHand hand) {
        Villager villager = EntityType.VILLAGER.create(world, EntitySpawnReason.BUCKET);
        assert villager != null;

        try {
            CustomData component = user.getItemInHand(hand).get(DataComponents.CUSTOM_DATA);
            assert component != null;
            ValueInput view = TagValueInput.create(ProblemReporter.DISCARDING, user.registryAccess(), component.copyTag());

            villager.setCustomName(user.getItemInHand(hand).get(DataComponents.CUSTOM_NAME));
            villager.readAdditionalSaveData(view);
            villager.snapTo(pos, 0, 0);

            world.addFreshEntity(villager);

            user.setItemInHand(hand, Items.BUNDLE.getDefaultInstance());
        } catch (Exception e) {
            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }
}
