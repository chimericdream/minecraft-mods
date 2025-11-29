package com.chimericdream.minekea.item.tools;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class HammerItem extends Item {
    public final Identifier ITEM_ID;

    public final ToolMaterial material;
    public final int maxSlots;
    public final String materialName;
    public final Item itemIngredient;
    public final TagKey<Item> itemIngredientTag;

    public HammerItem(ToolMaterial material, int maxSlots, String materialName, Item itemIngredient, TagKey<Item> itemIngredientTag) {
        this(material, maxSlots, materialName, itemIngredient, itemIngredientTag, new Item.Settings());
    }

    public HammerItem(ToolMaterial material, int maxSlots, String materialName, Item itemIngredient, TagKey<Item> itemIngredientTag, Item.Settings settings) {
        super(settings.maxCount(1).arch$tab(ItemGroups.TOOLS).pickaxe(material, 1.0F, -2.8F).registryKey(REGISTRY_HELPER.makeItemRegistryKey(makeId(materialName))));

        this.material = material;
        this.maxSlots = maxSlots;
        this.materialName = materialName;
        this.itemIngredient = itemIngredient;
        this.itemIngredientTag = itemIngredientTag;

        this.ITEM_ID = makeId(materialName);
    }

    public static Identifier makeId(String materialName) {
        return Identifier.of(ModInfo.MOD_ID, String.format("tools/hammers/%s", materialName.toLowerCase()));
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient() && state.getHardness(world, pos) != 0.0F) {
            stack.damage(1, miner, EquipmentSlot.MAINHAND);
        }

        return true;
    }

    public List<Text> getTooltip() {
        return List.of(Text.literal(String.format("Uses up to %d slots", this.maxSlots)));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if (player == null) {
            return ActionResult.FAIL;
        }

        List<Integer> slots = new ArrayList<>();

        PlayerInventory inventory = player.getInventory();

        int hammerSlot = inventory.getSelectedSlot();
        for (int i = hammerSlot; i < 9 && slots.size() < this.maxSlots; i++) {
            ItemStack item = inventory.getStack(i);
            if (!item.isEmpty() && item.getItem() instanceof BlockItem) {
                slots.add(i);
            }
        }

        ItemStack hammer = inventory.getStack(hammerSlot);
        NbtComponent nbtComponent = hammer.getComponents().get(DataComponentTypes.CUSTOM_DATA);

        Random rand;
        if (nbtComponent == null) {
            rand = Random.create();
        } else {
            rand = Random.create(nbtComponent.copyNbt().getLong("placement_seed").get());
        }

        if (slots.isEmpty()) {
            return ActionResult.FAIL;
        }

        int totalBlocks = 0;
        for (int slot : slots) {
            totalBlocks += inventory.getStack(slot).getCount();
        }

        int randomBlock = rand.nextBetween(1, totalBlocks);

        int slotToUse = -1;
        for (int slot : slots) {
            if (randomBlock <= inventory.getStack(slot).getCount()) {
                slotToUse = slot;
                break;
            }

            randomBlock -= inventory.getStack(slot).getCount();
        }

        if (slotToUse == -1) {
            return ActionResult.FAIL;
        }

        ItemStack toPlace = inventory.getStack(slotToUse);
        ItemPlacementContext placementContext = new ItemPlacementContext(
            player,
            ctx.getHand(),
            toPlace,
            new BlockHitResult(ctx.getHitPos(), ctx.getSide(), ctx.getBlockPos(), ctx.hitsInsideBlock())
        );

        if (!placementContext.canPlace()) {
            return ActionResult.FAIL;
        }

        World world = ctx.getWorld();
        if (!world.isClient()) {
            long nextSeed = rand.nextLong();

            NbtCompound nbt = new NbtCompound();
            nbt.putLong("placement_seed", nextSeed);

            NbtComponent hammerNbt = NbtComponent.of(nbt);

            hammer.set(DataComponentTypes.CUSTOM_DATA, hammerNbt);
        }

        ActionResult result = ((BlockItem) toPlace.getItem()).place(placementContext);
        if (result == ActionResult.CONSUME) {
            if (!player.isCreative()) {
                inventory.getStack(hammerSlot).damage(1, player, ctx.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
        }

        return ActionResult.SUCCESS;
    }
}
