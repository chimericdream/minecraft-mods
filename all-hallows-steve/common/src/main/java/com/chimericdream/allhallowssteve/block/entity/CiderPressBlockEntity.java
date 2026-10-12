package com.chimericdream.allhallowssteve.block.entity;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.client.screen.CiderPressScreenHandler;
import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.tag.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

/**
 * Works like a brewing stand: put fruit and glass bottles in, add fuel (which is stored as charges),
 * and a timed press turns {@value #FRUIT_PER_PRESS} apples, sweet berries, or glow berries and one bottle
 * into a bottle of cider or juice plus a piece of pomace. Hoppers can feed the inputs from any side but the bottom and empty the outputs
 * from the sides and the bottom.
 */
public class CiderPressBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final Identifier ENTITY_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/entity/cider_press");

    public static final int SLOT_FRUIT = 0;
    public static final int SLOT_BOTTLE = 1;
    public static final int SLOT_FUEL = 2;
    public static final int SLOT_CIDER = 3;
    public static final int SLOT_POMACE = 4;
    public static final int INVENTORY_SIZE = 5;

    public static final int FRUIT_PER_PRESS = 3;
    /** Ticks for one pressing. */
    public static final int PRESS_TIME = 200;

    public static final int CHARCOAL_CHARGES = 8;
    public static final int COAL_CHARGES = 8;
    public static final int BLAZE_POWDER_CHARGES = 20;
    /** The most charges the press can hold: one blaze powder's worth, since fuel is only added when it runs out. */
    public static final int MAX_CHARGES = BLAZE_POWDER_CHARGES;

    public static final int DATA_PRESS_TIME = 0;
    public static final int DATA_FUEL = 1;
    public static final int DATA_COUNT = 2;

    private static final int[] SLOTS_FOR_UP = {SLOT_FRUIT, SLOT_BOTTLE, SLOT_FUEL};
    private static final int[] SLOTS_FOR_DOWN = {SLOT_CIDER, SLOT_POMACE};
    private static final int[] SLOTS_FOR_SIDES = {SLOT_FRUIT, SLOT_BOTTLE, SLOT_FUEL, SLOT_CIDER, SLOT_POMACE};

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private int pressTime;
    private int fuel;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_PRESS_TIME -> CiderPressBlockEntity.this.pressTime;
                case DATA_FUEL -> CiderPressBlockEntity.this.fuel;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_PRESS_TIME -> CiderPressBlockEntity.this.pressTime = value;
                case DATA_FUEL -> CiderPressBlockEntity.this.fuel = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public CiderPressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CIDER_PRESS_BLOCK_ENTITY.get(), pos, state);
    }

    /** The bottle a pressing of {@code fruit} makes, or null if the press can't use it. */
    public static @Nullable Item juiceFor(ItemStack fruit) {
        if (fruit.is(Items.APPLE)) {
            return ModItems.APPLE_CIDER.get();
        }

        if (fruit.is(Items.SWEET_BERRIES)) {
            return ModItems.SWEET_BERRY_JUICE.get();
        }

        return fruit.is(Items.GLOW_BERRIES) ? ModItems.GLOW_BERRY_JUICE.get() : null;
    }

    /** How many pressings one of {@code stack} adds, or 0 if it isn't fuel. */
    public static int fuelCharges(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(ModTags.CIDER_PRESS_FUEL)) {
            return 0;
        }

        if (stack.is(Items.BLAZE_POWDER)) {
            return BLAZE_POWDER_CHARGES;
        }

        return stack.is(Items.COAL) ? COAL_CHARGES : CHARCOAL_CHARGES;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public int getPressTime() {
        return pressTime;
    }

    public int getFuel() {
        return fuel;
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.set(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable(CiderPressScreenHandler.SCREEN_ID.toLanguageKey());
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new CiderPressScreenHandler(ModBlocks.CIDER_PRESS_SCREEN_HANDLER.get(), syncId, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.inventory.replaceAll(ignored -> ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.inventory);
        this.pressTime = input.getShortOr("PressTime", (short) 0);
        this.fuel = input.getByteOr("Fuel", (byte) 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        ContainerHelper.saveAllItems(output, this.inventory);
        output.putShort("PressTime", (short) this.pressTime);
        output.putByte("Fuel", (byte) this.fuel);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case SLOT_FRUIT -> stack.is(ModTags.CIDER_PRESS_PRESSABLE);
            case SLOT_BOTTLE -> stack.is(Items.GLASS_BOTTLE);
            case SLOT_FUEL -> fuelCharges(stack) > 0;
            default -> false;
        };
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return switch (side) {
            case UP -> SLOTS_FOR_UP;
            case DOWN -> SLOTS_FOR_DOWN;
            default -> SLOTS_FOR_SIDES;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_CIDER || slot == SLOT_POMACE;
    }

    /** Whether one pressing could start now: enough fruit and a bottle in, and room for its juice and pomace. */
    private boolean canPress() {
        ItemStack fruit = this.inventory.get(SLOT_FRUIT);
        ItemStack bottle = this.inventory.get(SLOT_BOTTLE);
        Item juice = juiceFor(fruit);
        if (juice == null || fruit.getCount() < FRUIT_PER_PRESS || !fruit.is(ModTags.CIDER_PRESS_PRESSABLE) || bottle.isEmpty()) {
            return false;
        }

        return hasRoomFor(this.inventory.get(SLOT_CIDER), juice)
            && hasRoomFor(this.inventory.get(SLOT_POMACE), ModItems.POMACE.get());
    }

    private static boolean hasRoomFor(ItemStack output, Item item) {
        return output.isEmpty() || (output.is(item) && output.getCount() < output.getMaxStackSize());
    }

    private void addOutput(int slot, Item item) {
        ItemStack existing = this.inventory.get(slot);
        if (existing.isEmpty()) {
            this.inventory.set(slot, new ItemStack(item));
        } else {
            existing.grow(1);
        }
    }

    private void finishPress(Level level, BlockPos pos) {
        ItemStack fruit = this.inventory.get(SLOT_FRUIT);
        Item juice = juiceFor(fruit);
        fruit.shrink(FRUIT_PER_PRESS);
        this.inventory.get(SLOT_BOTTLE).shrink(1);
        this.addOutput(SLOT_CIDER, juice);
        this.addOutput(SLOT_POMACE, ModItems.POMACE.get());

        level.playSound(null, pos, SoundEvents.COMPOSTER_FILL_SUCCESS, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CiderPressBlockEntity press) {
        boolean canPress = press.canPress();

        // Fuel is only drawn when a pressing is waiting on it, so hopper-fed charcoal isn't burned for nothing.
        if (press.fuel <= 0 && canPress) {
            ItemStack fuelStack = press.inventory.get(SLOT_FUEL);
            int charges = fuelCharges(fuelStack);
            if (charges > 0) {
                press.fuel = charges;
                fuelStack.shrink(1);
                setChanged(level, pos, state);
            }
        }

        if (press.pressTime > 0) {
            if (!canPress) {
                press.pressTime = 0;
            } else {
                press.pressTime--;
                if (press.pressTime == 0) {
                    press.finishPress(level, pos);
                }
            }

            setChanged(level, pos, state);
        } else if (canPress && press.fuel > 0) {
            press.fuel--;
            press.pressTime = PRESS_TIME;
            setChanged(level, pos, state);
        }
    }
}
