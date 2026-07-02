package com.chimericdream.houdiniblock.items;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class HoudiniBlockItem extends BlockItem {
    public static final CustomData DEFAULT_NBT;
    public static final Map<PlacementMode, String> TOOLTIP_KEYS = Map.of(
        PlacementMode.PREVENT_ON_BREAK, "item.houdiniblock.tooltip.prevent_on_break",
        PlacementMode.PREVENT_ON_PLACE, "item.houdiniblock.tooltip.prevent_on_place",
        PlacementMode.PREVENT_ALL, "item.houdiniblock.tooltip.prevent_all",
        PlacementMode.REPLACE_BLOCK, "item.houdiniblock.tooltip.replace_block"
    );

    static {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("houdini_placement_mode", PlacementMode.PREVENT_ON_BREAK.toString());

        DEFAULT_NBT = CustomData.of(nbt);
    }

    public HoudiniBlockItem(Block block, Item.Properties settings) {
        super(block, settings);
    }

    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        try {
            ItemStack itemStack = player.getItemInHand(hand);
            CompoundTag nbt = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, DEFAULT_NBT).copyTag();

            PlacementMode currentMode = PlacementMode.getFromNbt(nbt);
            PlacementMode newMode = switch (currentMode) {
                case PREVENT_ON_BREAK -> PlacementMode.PREVENT_ON_PLACE;
                case PREVENT_ON_PLACE -> PlacementMode.PREVENT_ALL;
                case PREVENT_ALL -> PlacementMode.REPLACE_BLOCK;
                case REPLACE_BLOCK -> PlacementMode.PREVENT_ON_BREAK;
            };

            if (newMode == PlacementMode.PREVENT_ON_BREAK) {
                itemStack.remove(DataComponents.CUSTOM_DATA);
            } else {
                nbt.putString("houdini_placement_mode", newMode.toString());
                itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            }

            if (!world.isClientSide()) {
                player.displayClientMessage(Component.translatable(TOOLTIP_KEYS.get(newMode)), true);
            }

            return InteractionResult.SUCCESS;
        } catch (IllegalArgumentException e) {
            return InteractionResult.FAIL;
        }
    }

    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, DEFAULT_NBT).copyTag();
        PlacementMode mode = PlacementMode.getFromNbt(nbt);

        BlockPlaceContext placementContext = new BlockPlaceContext(context);

        if (mode == PlacementMode.REPLACE_BLOCK) {
            BlockPos pos = context.getClickedPos();
            BlockState target = context.getLevel().getBlockState(pos);

            // This doesn't let you break unbreakable blocks
            // @TODO: make this configurable?
            if (target.getBlock().defaultDestroyTime() == -1.0F) {
                return InteractionResult.FAIL;
            }

            if (context.getLevel() instanceof ServerLevel world) {
                Block.dropResources(target, world, pos, null);
            }

            placementContext.relativePos = pos;
            placementContext.replaceClicked = true;
        }

        this.place(placementContext);

        Level world = context.getLevel();
        if (mode != PlacementMode.PREVENT_ON_BREAK) {
            Player player = context.getPlayer();
            if (player != null && !player.isCreative()) {
                stack.shrink(1);
            }

            if (world.isClientSide()) {
                world.playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }

        return InteractionResult.SUCCESS;
    }

    public enum PlacementMode {
        PREVENT_ON_BREAK,
        PREVENT_ON_PLACE,
        PREVENT_ALL,
        REPLACE_BLOCK;

        public static PlacementMode getFromNbt(CompoundTag nbt) {
            return PlacementMode.valueOf(nbt.getString("houdini_placement_mode").orElse(PlacementMode.PREVENT_ON_BREAK.toString()));
        }
    }
}
