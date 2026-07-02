package com.chimericdream.minekea.item.tools;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class HammerItem extends Item {
    public final ResourceLocation ITEM_ID;

    public final ToolMaterial material;
    public final int maxSlots;
    public final String materialName;
    public final Item itemIngredient;
    public final TagKey<Item> itemIngredientTag;

    public HammerItem(ToolMaterial material, int maxSlots, String materialName, Item itemIngredient, TagKey<Item> itemIngredientTag) {
        this(material, maxSlots, materialName, itemIngredient, itemIngredientTag, new Item.Properties());
    }

    public HammerItem(ToolMaterial material, int maxSlots, String materialName, Item itemIngredient, TagKey<Item> itemIngredientTag, Item.Properties settings) {
        super(settings.stacksTo(1).arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES).pickaxe(material, 1.0F, -2.8F).setId(REGISTRY_HELPER.makeItemRegistryKey(makeId(materialName))));

        this.material = material;
        this.maxSlots = maxSlots;
        this.materialName = materialName;
        this.itemIngredient = itemIngredient;
        this.itemIngredientTag = itemIngredientTag;

        this.ITEM_ID = makeId(materialName);
    }

    public static ResourceLocation makeId(String materialName) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("tools/hammers/%s", materialName.toLowerCase()));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClientSide() && state.getDestroySpeed(world, pos) != 0.0F) {
            stack.hurtAndBreak(1, miner, EquipmentSlot.MAINHAND);
        }

        return true;
    }

    public List<Component> getTooltip() {
        return List.of(Component.literal(String.format("Uses up to %d slots", this.maxSlots)));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }

        List<Integer> slots = new ArrayList<>();

        Inventory inventory = player.getInventory();

        int hammerSlot = inventory.getSelectedSlot();
        for (int i = hammerSlot; i < 9 && slots.size() < this.maxSlots; i++) {
            ItemStack item = inventory.getItem(i);
            if (!item.isEmpty() && item.getItem() instanceof BlockItem) {
                slots.add(i);
            }
        }

        ItemStack hammer = inventory.getItem(hammerSlot);
        CustomData nbtComponent = hammer.getComponents().get(DataComponents.CUSTOM_DATA);

        RandomSource rand;
        if (nbtComponent == null) {
            rand = RandomSource.create();
        } else {
            rand = RandomSource.create(nbtComponent.copyTag().getLong("placement_seed").get());
        }

        if (slots.isEmpty()) {
            return InteractionResult.FAIL;
        }

        int totalBlocks = 0;
        for (int slot : slots) {
            totalBlocks += inventory.getItem(slot).getCount();
        }

        int randomBlock = rand.nextIntBetweenInclusive(1, totalBlocks);

        int slotToUse = -1;
        for (int slot : slots) {
            if (randomBlock <= inventory.getItem(slot).getCount()) {
                slotToUse = slot;
                break;
            }

            randomBlock -= inventory.getItem(slot).getCount();
        }

        if (slotToUse == -1) {
            return InteractionResult.FAIL;
        }

        ItemStack toPlace = inventory.getItem(slotToUse);
        BlockPlaceContext placementContext = new BlockPlaceContext(
            player,
            ctx.getHand(),
            toPlace,
            new BlockHitResult(ctx.getClickLocation(), ctx.getClickedFace(), ctx.getClickedPos(), ctx.isInside())
        );

        if (!placementContext.canPlace()) {
            return InteractionResult.FAIL;
        }

        Level world = ctx.getLevel();
        if (!world.isClientSide()) {
            long nextSeed = rand.nextLong();

            CompoundTag nbt = new CompoundTag();
            nbt.putLong("placement_seed", nextSeed);

            CustomData hammerNbt = CustomData.of(nbt);

            hammer.set(DataComponents.CUSTOM_DATA, hammerNbt);
        }

        InteractionResult result = ((BlockItem) toPlace.getItem()).place(placementContext);
        if (result == InteractionResult.CONSUME) {
            if (!player.isCreative()) {
                inventory.getItem(hammerSlot).hurtAndBreak(1, player, ctx.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
