package com.chimericdream.minekea.item.tools;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.client.screen.BlockPainterScreenHandler;
import com.chimericdream.minekea.data.nbt.NbtHelpers;
import com.chimericdream.minekea.registry.ColoredBlocksRegistry;
import com.chimericdream.minekea.registry.ColoredBlocksRegistry.BlockColor;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class BlockPainterItem extends Item {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tools/painter");

    public BlockPainterItem() {
        super(new Item.Properties().stacksTo(1).arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES).setId(REGISTRY_HELPER.makeItemRegistryKey(ITEM_ID)));
    }

    public static BlockColor getColor(ItemStack stack) {
        CompoundTag nbt = NbtHelpers.getOrCreateNbt(stack);
        String stackColor = nbt.getStringOr("current_color", "none");

        return BlockColor.get(stackColor);
    }

    public static BlockColor getNextColor(ItemStack stack) {
        CompoundTag nbt = NbtHelpers.getOrCreateNbt(stack);
        String stackColor = nbt.getStringOr("current_color", "none");

        return BlockColor.get(stackColor).getNext();
    }

    public static CompoundTag makeNbt(CompoundTag nbt, BlockColor color) {
        nbt.putString("current_color", color.toString());
        nbt.putString("CustomModelData", color.getModelNumber());

        return nbt;
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);

        NbtHelpers.setCustomDataFromNbt(stack, makeNbt(new CompoundTag(), BlockColor.WHITE));

        return stack;
    }

    public List<Component> getTooltip(ItemStack stack) {
        CompoundTag nbt = NbtHelpers.getOrCreateNbt(stack);
        String stackColor = nbt.getStringOr("current_color", "NONE");
        BlockColor color = BlockColor.get(stackColor);

        return List.of(Component.literal(String.format("Current color: %s", color)));
    }

    @Override
    public boolean isBarVisible(ItemStack painter) {
        ItemStack dye = getSelectedDye(painter);

        return !dye.isEmpty();
    }

    @Override
    public int getBarWidth(ItemStack painter) {
        ItemStack dye = getSelectedDye(painter);

        if (dye.isEmpty()) {
            return 0;
        }

        return Math.round(13.0F - (float) (dye.getMaxStackSize() - dye.getCount()) * 13.0F / (float) dye.getMaxStackSize());
    }

    @Override
    public int getBarColor(ItemStack painter) {
        ItemStack dye = getSelectedDye(painter);

        float f = Math.max(0.0F, (float) dye.getCount() / (float) dye.getMaxStackSize());
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public @NotNull InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (player.level() != null && !player.level().isClientSide()) {
            if (player.isShiftKeyDown()) {
                openScreen(player, player.getItemInHand(hand));
            }
        }

        return InteractionResult.SUCCESS;
    }

    public static void openScreen(Player player, ItemStack painter) {
        if (player.level() != null && !player.level().isClientSide()) {
            player.openMenu(new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable(painter.getItem().getDescriptionId());
                }

                @Override
                public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                    return new BlockPainterScreenHandler(syncId, inv, painter);
                }
            });
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide() || (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())) {
            return super.useOn(context);
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);

        String colorGroup = ColoredBlocksRegistry.getBlockGroup(state.getBlock());

        if (colorGroup == null) {
            return super.useOn(context);
        }

        ItemStack stack = context.getItemInHand();
        BlockColor color = getColor(stack);

        Block newBlock = ColoredBlocksRegistry.findBlock(colorGroup, color);
        if (newBlock == null || state.is(newBlock)) {
            return super.useOn(context);
        }

        Player player = context.getPlayer();
        ItemStack dye = getSelectedDye(stack);

        if (dye.isEmpty() && (player == null || !player.isCreative())) {
            return InteractionResult.FAIL;
        }

        if (player != null && !player.isCreative()) {
            PainterInventory painter = new PainterInventory(stack);
            painter.consumeDye(color);
        }

        context.getLevel().setBlockAndUpdate(pos, newBlock.defaultBlockState());
        context.getLevel().blockEntityChanged(pos);

        return InteractionResult.SUCCESS;
    }

    private ItemStack getSelectedDye(ItemStack painter) {
        BlockColor color = getColor(painter);
        Item dye = color.getDye();
        ItemContainerContents inventory = painter.get(DataComponents.CONTAINER);

        if (dye == null || inventory == null) {
            return ItemStack.EMPTY;
        }

        return inventory.nonEmptyItemCopyStream()
            .filter(stack -> stack.getItem() == dye)
            .findFirst()
            .orElse(ItemStack.EMPTY);
    }

    public static class PainterInventory implements ImplementedInventory {
        public static final int INVENTORY_SIZE = 16;

        private final ItemStack painterStack;
        private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

        public PainterInventory(ItemStack stack) {
            painterStack = stack;

            ItemContainerContents inventory = painterStack.get(DataComponents.CONTAINER);

            if (inventory != null) {
                inventory.copyInto(items);
            }
        }

        @Override
        public NonNullList<ItemStack> getItems() {
            return items;
        }

        @Override
        public ItemStack tryInsert(ItemStack stack) {
            return stack;
        }

        @Override
        public ItemStack tryInsert(int slot, ItemStack stack) {
            return ImplementedInventory.super.tryInsert(slot, stack);
        }

        public void consumeDye(BlockColor color) {
            ItemStack dye = this.getItem(color.getIndex()).copy();
            if (dye.isEmpty()) {
                return;
            }

            dye.shrink(1);

            if (dye.isEmpty()) {
                this.setItem(color.getIndex(), ItemStack.EMPTY);
            } else {
                this.setItem(color.getIndex(), dye);
            }

            this.setChanged();
        }

        @Override
        public void setChanged() {
            painterStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        }
    }
}
