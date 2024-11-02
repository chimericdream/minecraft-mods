package com.chimericdream.houdiniblock.items;

import com.chimericdream.lib.text.TextHelpers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class HoudiniBlockItem extends BlockItem {
    public static final NbtComponent DEFAULT_NBT;
    public static final Map<PlacementMode, String> TOOLTIP_KEYS = Map.of(
        PlacementMode.PREVENT_ON_BREAK, "item.houdiniblock.tooltip.prevent_on_break",
        PlacementMode.PREVENT_ON_PLACE, "item.houdiniblock.tooltip.prevent_on_place",
        PlacementMode.PREVENT_ALL, "item.houdiniblock.tooltip.prevent_all",
        PlacementMode.REPLACE_BLOCK, "item.houdiniblock.tooltip.replace_block"
    );

    static {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("houdini_placement_mode", PlacementMode.PREVENT_ON_BREAK.toString());

        DEFAULT_NBT = NbtComponent.of(nbt);
    }

    public HoudiniBlockItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, DEFAULT_NBT).copyNbt();
        PlacementMode currentMode = PlacementMode.valueOf(nbt.getString("houdini_placement_mode"));

        tooltip.add(TextHelpers.getTooltip(TOOLTIP_KEYS.get(currentMode)));
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!player.isSneaking()) {
            return TypedActionResult.pass(player.getStackInHand(hand));
        }

        try {
            ItemStack itemStack = player.getStackInHand(hand);
            NbtCompound nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, DEFAULT_NBT).copyNbt();

            PlacementMode currentMode = PlacementMode.valueOf(nbt.getString("houdini_placement_mode"));
            PlacementMode newMode = switch (currentMode) {
                case PREVENT_ON_BREAK -> PlacementMode.PREVENT_ON_PLACE;
                case PREVENT_ON_PLACE -> PlacementMode.PREVENT_ALL;
                case PREVENT_ALL -> PlacementMode.REPLACE_BLOCK;
                case REPLACE_BLOCK -> PlacementMode.PREVENT_ON_BREAK;
            };

            if (newMode == PlacementMode.PREVENT_ON_BREAK) {
                itemStack.remove(DataComponentTypes.CUSTOM_DATA);
            } else {
                nbt.putString("houdini_placement_mode", newMode.toString());
                itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            }

            if (!world.isClient()) {
                player.sendMessage(Text.translatable(TOOLTIP_KEYS.get(newMode)), true);
            }

            return TypedActionResult.success(player.getStackInHand(hand));
        } catch (IllegalArgumentException e) {
            return TypedActionResult.fail(player.getStackInHand(hand));
        }
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, DEFAULT_NBT).copyNbt();
        PlacementMode mode = PlacementMode.valueOf(nbt.getString("houdini_placement_mode"));

        ItemPlacementContext placementContext = new ItemPlacementContext(context);

        if (mode == PlacementMode.REPLACE_BLOCK) {
            BlockPos pos = context.getBlockPos();
            BlockState target = context.getWorld().getBlockState(pos);

            // This doesn't let you break unbreakable blocks
            // @TODO: make this configurable?
            if (target.getBlock().getHardness() == -1.0F) {
                return ActionResult.FAIL;
            }

            if (context.getWorld() instanceof ServerWorld world) {
                Block.dropStacks(target, world, pos, null);
            }

            placementContext.placementPos = pos;
            placementContext.canReplaceExisting = true;
        }

        this.place(placementContext);

        World world = context.getWorld();
        if (mode != PlacementMode.PREVENT_ON_BREAK) {
            PlayerEntity player = context.getPlayer();
            if (player != null && !player.isCreative()) {
                stack.decrement(1);
            }

            if (world.isClient()) {
                world.playSound(context.getPlayer(), context.getBlockPos(), SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }

        return ActionResult.SUCCESS;
    }

    public enum PlacementMode {
        PREVENT_ON_BREAK,
        PREVENT_ON_PLACE,
        PREVENT_ALL,
        REPLACE_BLOCK;
    }
}
