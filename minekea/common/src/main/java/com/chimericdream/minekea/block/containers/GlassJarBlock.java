package com.chimericdream.minekea.block.containers;

import com.chimericdream.lib.items.ItemHelpers;
import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.general.WaxBlock;
import com.chimericdream.minekea.block.building.storage.DyeBlock;
import com.chimericdream.minekea.block.building.storage.SetOfEggsBlock;
import com.chimericdream.minekea.block.building.storage.StorageBlocks;
import com.chimericdream.minekea.crop.WarpedWartItem;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import com.chimericdream.minekea.item.containers.ContainerItems;
import com.chimericdream.minekea.item.ingredients.WaxItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class GlassJarBlock extends BlockWithEntity implements Waterloggable {
    public static final MapCodec<GlassJarBlock> CODEC = createCodec(GlassJarBlock::new);

    public static final Map<String, String> ALLOWED_ITEM_IDS = new LinkedHashMap<>();
    public static final List<Item> ALLOWED_ITEMS = new ArrayList<>();

    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty WATERLOGGED;

    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "containers/glass_jar");

    private static final VoxelShape MAIN_SHAPE;
    private static final VoxelShape LID_SHAPE;

    static {
        FACING = Properties.HORIZONTAL_FACING;
        WATERLOGGED = Properties.WATERLOGGED;

        ALLOWED_ITEMS.add(Items.AMETHYST_SHARD);
        ALLOWED_ITEMS.add(Items.APPLE);
        ALLOWED_ITEMS.add(Items.BAMBOO);
        ALLOWED_ITEMS.add(Items.BEETROOT);
        ALLOWED_ITEMS.add(Items.BEETROOT_SEEDS);
        ALLOWED_ITEMS.add(Items.BLAZE_POWDER);
        ALLOWED_ITEMS.add(Items.BLAZE_ROD);
        ALLOWED_ITEMS.add(Items.BREEZE_ROD);
        ALLOWED_ITEMS.add(Items.BROWN_MUSHROOM);
        ALLOWED_ITEMS.add(Items.CARROT);
        ALLOWED_ITEMS.add(Items.CHARCOAL);
        ALLOWED_ITEMS.add(Items.CHORUS_FRUIT);
        ALLOWED_ITEMS.add(Items.COAL);
        ALLOWED_ITEMS.add(Items.DRIED_KELP);
        ALLOWED_ITEMS.add(Items.EGG);
        ALLOWED_ITEMS.add(Items.ENDER_PEARL);
        ALLOWED_ITEMS.add(Items.FLINT);
        ALLOWED_ITEMS.add(Items.GLOWSTONE_DUST);
        ALLOWED_ITEMS.add(Items.GOLDEN_APPLE);
        ALLOWED_ITEMS.add(Items.GRAVEL);
        ALLOWED_ITEMS.add(Items.HONEYCOMB);
        ALLOWED_ITEMS.add(Items.LEATHER);
        ALLOWED_ITEMS.add(Items.MELON_SEEDS);
        ALLOWED_ITEMS.add(Items.MELON_SLICE);
        ALLOWED_ITEMS.add(Items.NETHER_STAR);
        ALLOWED_ITEMS.add(Items.NETHER_WART);
        ALLOWED_ITEMS.add(Items.PAPER);
        ALLOWED_ITEMS.add(Items.PHANTOM_MEMBRANE);
        ALLOWED_ITEMS.add(Items.POTATO);
        ALLOWED_ITEMS.add(Items.PUMPKIN_SEEDS);
        ALLOWED_ITEMS.add(Items.RED_MUSHROOM);
        ALLOWED_ITEMS.add(Items.RED_SAND);
        ALLOWED_ITEMS.add(Items.REDSTONE);
        ALLOWED_ITEMS.add(Items.RESIN_CLUMP);
        ALLOWED_ITEMS.add(Items.SAND);
        ALLOWED_ITEMS.add(Items.SCULK_VEIN);
        ALLOWED_ITEMS.add(Items.SLIME_BALL);
        ALLOWED_ITEMS.add(Items.STICK);
        ALLOWED_ITEMS.add(Items.SUGAR);
        ALLOWED_ITEMS.add(Items.SUGAR_CANE);
        ALLOWED_ITEMS.add(Items.TOTEM_OF_UNDYING);
        // Done in the ModCrops file
        // ALLOWED_ITEMS.add(ModCrops.WARPED_WART_ITEM.get());
        ALLOWED_ITEMS.add(Items.WHEAT);
        ALLOWED_ITEMS.add(Items.WHEAT_SEEDS);

        ALLOWED_ITEMS.add(Items.WHITE_DYE);
        ALLOWED_ITEMS.add(Items.LIGHT_GRAY_DYE);
        ALLOWED_ITEMS.add(Items.GRAY_DYE);
        ALLOWED_ITEMS.add(Items.BLACK_DYE);
        ALLOWED_ITEMS.add(Items.BROWN_DYE);
        ALLOWED_ITEMS.add(Items.RED_DYE);
        ALLOWED_ITEMS.add(Items.ORANGE_DYE);
        ALLOWED_ITEMS.add(Items.YELLOW_DYE);
        ALLOWED_ITEMS.add(Items.LIME_DYE);
        ALLOWED_ITEMS.add(Items.GREEN_DYE);
        ALLOWED_ITEMS.add(Items.CYAN_DYE);
        ALLOWED_ITEMS.add(Items.LIGHT_BLUE_DYE);
        ALLOWED_ITEMS.add(Items.BLUE_DYE);
        ALLOWED_ITEMS.add(Items.PURPLE_DYE);
        ALLOWED_ITEMS.add(Items.MAGENTA_DYE);
        ALLOWED_ITEMS.add(Items.PINK_DYE);

        ALLOWED_ITEMS.add(Items.WHITE_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.LIGHT_GRAY_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.GRAY_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.BLACK_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.BROWN_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.RED_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.ORANGE_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.YELLOW_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.LIME_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.GREEN_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.CYAN_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.LIGHT_BLUE_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.BLUE_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.PURPLE_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.MAGENTA_CONCRETE_POWDER);
        ALLOWED_ITEMS.add(Items.PINK_CONCRETE_POWDER);

        /*
         * This would probably be cleaner looking if did something like
         *
         * ```
         * ALLOWED_ITEMS.putAll(Map.<String, String>ofEntries(...));
         * ```
         *
         * I had it like that, but the item tag generation was totally random, meaning the tag JSON file was different
         * every time I ran datagen. By doing it this way, I ensure the JSON file only updates when the values change.
         */
        ALLOWED_ITEM_IDS.put("minecraft:amethyst_shard", "minecraft:amethyst_block");
        ALLOWED_ITEM_IDS.put("minecraft:apple", StorageBlocks.APPLE_STORAGE_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:bamboo", "minecraft:bamboo_block");
        ALLOWED_ITEM_IDS.put("minecraft:beetroot", StorageBlocks.BEETROOT_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:beetroot_seeds", StorageBlocks.BEETROOT_SEEDS_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:blaze_powder", StorageBlocks.BLAZE_POWDER_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:blaze_rod", StorageBlocks.BLAZE_ROD_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:breeze_rod", StorageBlocks.BREEZE_ROD_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:brown_mushroom", "minecraft:brown_mushroom_block");
        ALLOWED_ITEM_IDS.put("minecraft:carrot", StorageBlocks.CARROT_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:charcoal", StorageBlocks.CHARCOAL_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:chorus_fruit", StorageBlocks.CHORUS_FRUIT_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:coal", "minecraft:coal_block");
        ALLOWED_ITEM_IDS.put("minecraft:dried_kelp", "minecraft:dried_kelp_block");
        ALLOWED_ITEM_IDS.put("minecraft:egg", SetOfEggsBlock.BLOCK_ID.toString());
        ALLOWED_ITEM_IDS.put("minecraft:ender_pearl", StorageBlocks.ENDER_PEARL_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:flint", StorageBlocks.FLINT_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:glowstone_dust", "minecraft:glowstone");
        ALLOWED_ITEM_IDS.put("minecraft:golden_apple", StorageBlocks.GOLDEN_APPLE_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:gravel", "minecraft:gravel");
        ALLOWED_ITEM_IDS.put("minecraft:honeycomb", "minecraft:honeycomb_block");
        ALLOWED_ITEM_IDS.put("minecraft:leather", StorageBlocks.LEATHER_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:melon_seeds", StorageBlocks.MELON_SEEDS_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:melon_slice", "minecraft:melon");
        ALLOWED_ITEM_IDS.put("minecraft:nether_star", StorageBlocks.NETHER_STAR_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:nether_wart", "minecraft:nether_wart_block");
        ALLOWED_ITEM_IDS.put("minecraft:paper", StorageBlocks.WALLPAPER_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:phantom_membrane", StorageBlocks.PHANTOM_MEMBRANE_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:potato", StorageBlocks.POTATO_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:pumpkin_seeds", StorageBlocks.PUMPKIN_SEEDS_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:red_mushroom", "minecraft:red_mushroom_block");
        ALLOWED_ITEM_IDS.put("minecraft:red_sand", "minecraft:red_sand");
        ALLOWED_ITEM_IDS.put("minecraft:redstone", "minecraft:redstone_block");
        ALLOWED_ITEM_IDS.put("minecraft:resin_clump", "minecraft:resin_block");
        ALLOWED_ITEM_IDS.put("minecraft:sand", "minecraft:sand");
        ALLOWED_ITEM_IDS.put("minecraft:sculk_vein", "minecraft:sculk");
        ALLOWED_ITEM_IDS.put("minecraft:slime_ball", "minecraft:slime_block");
        ALLOWED_ITEM_IDS.put("minecraft:stick", StorageBlocks.STICK_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:sugar", StorageBlocks.SUGAR_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:sugar_cane", StorageBlocks.SUGAR_CANE_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put("minecraft:totem_of_undying", StorageBlocks.TOTEM_BLOCK.getIdAsString());
        ALLOWED_ITEM_IDS.put(WarpedWartItem.ITEM_ID.toString(), "minecraft:warped_wart_block");
        ALLOWED_ITEM_IDS.put("minecraft:wheat", "minecraft:hay_block");
        ALLOWED_ITEM_IDS.put("minecraft:wheat_seeds", StorageBlocks.WHEAT_SEEDS_BLOCK.getIdAsString());

        ALLOWED_ITEM_IDS.put("minecraft:white_dye", DyeBlock.makeId("white").toString());
        ALLOWED_ITEM_IDS.put("minecraft:light_gray_dye", DyeBlock.makeId("light_gray").toString());
        ALLOWED_ITEM_IDS.put("minecraft:gray_dye", DyeBlock.makeId("gray").toString());
        ALLOWED_ITEM_IDS.put("minecraft:black_dye", DyeBlock.makeId("black").toString());
        ALLOWED_ITEM_IDS.put("minecraft:brown_dye", DyeBlock.makeId("brown").toString());
        ALLOWED_ITEM_IDS.put("minecraft:red_dye", DyeBlock.makeId("red").toString());
        ALLOWED_ITEM_IDS.put("minecraft:orange_dye", DyeBlock.makeId("orange").toString());
        ALLOWED_ITEM_IDS.put("minecraft:yellow_dye", DyeBlock.makeId("yellow").toString());
        ALLOWED_ITEM_IDS.put("minecraft:lime_dye", DyeBlock.makeId("lime").toString());
        ALLOWED_ITEM_IDS.put("minecraft:green_dye", DyeBlock.makeId("green").toString());
        ALLOWED_ITEM_IDS.put("minecraft:cyan_dye", DyeBlock.makeId("cyan").toString());
        ALLOWED_ITEM_IDS.put("minecraft:light_blue_dye", DyeBlock.makeId("light_blue").toString());
        ALLOWED_ITEM_IDS.put("minecraft:blue_dye", DyeBlock.makeId("blue").toString());
        ALLOWED_ITEM_IDS.put("minecraft:purple_dye", DyeBlock.makeId("purple").toString());
        ALLOWED_ITEM_IDS.put("minecraft:magenta_dye", DyeBlock.makeId("magenta").toString());
        ALLOWED_ITEM_IDS.put("minecraft:pink_dye", DyeBlock.makeId("pink").toString());

        ALLOWED_ITEM_IDS.put("minecraft:white_concrete_powder", "minecraft:white_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:light_gray_concrete_powder", "minecraft:light_gray_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:gray_concrete_powder", "minecraft:gray_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:black_concrete_powder", "minecraft:black_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:brown_concrete_powder", "minecraft:brown_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:red_concrete_powder", "minecraft:red_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:orange_concrete_powder", "minecraft:orange_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:yellow_concrete_powder", "minecraft:yellow_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:lime_concrete_powder", "minecraft:lime_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:green_concrete_powder", "minecraft:green_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:cyan_concrete_powder", "minecraft:cyan_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:light_blue_concrete_powder", "minecraft:light_blue_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:blue_concrete_powder", "minecraft:blue_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:purple_concrete_powder", "minecraft:purple_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:magenta_concrete_powder", "minecraft:magenta_concrete_powder");
        ALLOWED_ITEM_IDS.put("minecraft:pink_concrete_powder", "minecraft:pink_concrete_powder");

        ALLOWED_ITEM_IDS.put(WaxItem.makeId("plain").toString(), WaxBlock.makeId("plain").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("white").toString(), WaxBlock.makeId("white").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("light_gray").toString(), WaxBlock.makeId("light_gray").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("gray").toString(), WaxBlock.makeId("gray").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("black").toString(), WaxBlock.makeId("black").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("brown").toString(), WaxBlock.makeId("brown").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("red").toString(), WaxBlock.makeId("red").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("orange").toString(), WaxBlock.makeId("orange").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("yellow").toString(), WaxBlock.makeId("yellow").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("lime").toString(), WaxBlock.makeId("lime").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("green").toString(), WaxBlock.makeId("green").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("cyan").toString(), WaxBlock.makeId("cyan").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("light_blue").toString(), WaxBlock.makeId("light_blue").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("blue").toString(), WaxBlock.makeId("blue").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("purple").toString(), WaxBlock.makeId("purple").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("magenta").toString(), WaxBlock.makeId("magenta").toString());
        ALLOWED_ITEM_IDS.put(WaxItem.makeId("pink").toString(), WaxBlock.makeId("pink").toString());

        MAIN_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 9.0, 11.0);
        LID_SHAPE = Block.createCuboidShape(6.0, 9.0, 6.0, 10.0, 10.0, 10.0);
    }

    public GlassJarBlock(AbstractBlock.Settings settings) {
        this();
    }

    public GlassJarBlock() {
        super(Settings.copy(Blocks.GLASS).nonOpaque().registryKey(REGISTRY_HELPER.makeBlockRegistryKey(GlassJarBlock.BLOCK_ID)));

        this.setDefaultState(
            this.stateManager
                .getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, false)
        );
    }

    protected MapCodec<GlassJarBlock> getCodec() {
        return CODEC;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        Direction facing = Direction.NORTH;

        if (player != null) {
            facing = player.getHorizontalFacing().getOpposite();
        }

        return this.getDefaultState()
            .with(FACING, facing)
            .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
//        GlassJarBlockEntity entity;
//        try {
//            entity = (GlassJarBlockEntity) world.getBlockEntity(pos);
//            assert entity != null;
//        } catch (Exception e) {
//            MinekeaMod.LOGGER.error("The glass jar at {} had an invalid block entity.\nBlock Entity: {}", pos, world.getBlockEntity(pos));
//
//            return;
//        }
//
//        String mobId = entity.getMobId();
//        if (mobId == null) {
//            return;
//        }
//
//        SoundEvent sound = getMobSound(mobId);
//        if (sound != null && random.nextInt(100) == 0) {
//            world.playSound(null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, sound, SoundCategory.BLOCKS, 0.5F, 0.5F);
//        }
    }

//    @Nullable
//    private SoundEvent getMobSound(String mobId) {
//        return switch (mobId) {
//            case "minecraft:allay" -> SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM;
//            case "minecraft:bat" -> SoundEvents.ENTITY_BAT_AMBIENT;
//            case "minecraft:bee" -> SoundEvents.ENTITY_BEE_POLLINATE;
//            case "minecraft:endermite" -> SoundEvents.ENTITY_ENDERMITE_AMBIENT;
//            case "minecraft:silverfish" -> SoundEvents.ENTITY_SILVERFISH_AMBIENT;
//            case "minecraft:slime" -> SoundEvents.ENTITY_SLIME_SQUISH;
//            case "minecraft:vex" -> SoundEvents.ENTITY_VEX_AMBIENT;
//            default -> null;
//        };
//    }
//
//    @Override
//    public FluidState getFluidState(BlockState state) {
//        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
//    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(WATERLOGGED)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GlassJarBlockEntity(ContainerBlocks.GLASS_JAR_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        GlassJarBlockEntity entity;

        try {
            entity = (GlassJarBlockEntity) world.getBlockEntity(pos);
            assert entity != null;
        } catch (Exception e) {
            MinekeaMod.LOGGER.error("The glass jar at {} had an invalid block entity.\nBlock Entity: {}", pos, world.getBlockEntity(pos));

            return ActionResult.FAIL;
        }

//        if (isFilledBucket(stack)) {
//            Identifier stackId = Registries.ITEM.getId(stack.getItem());
//            Fluid bucketFluid = getFluidType(stackId);
//
//            if (!bucketFluid.matchesType(Fluids.EMPTY) && entity.tryInsert(bucketFluid)) {
//                replaceHeldItemOrDont(world, player, stack, Items.BUCKET.getDefaultStack());
//                if (world.isClient()) {
//                    entity.playEmptyBucketSound(bucketFluid);
//                }
//                entity.markDirty();
//            }
//        } else if (isFilledBottle(stack)) {
//            if (
//                stack.isOf(Items.HONEY_BOTTLE)
//                    && entity.tryInsert(ModFluids.HONEY_FLUID.get(), GlassJarBlockEntity.BOTTLE_SIZE)
//            ) {
//                replaceHeldItemOrDont(world, player, stack, Items.GLASS_BOTTLE.getDefaultStack());
//                if (world.isClient()) {
//                    entity.playEmptyBottleSound();
//                }
//                entity.markDirty();
//            } else if (
//                stack.isOf(Items.POTION)
//                    && stack.getComponents().get(DataComponentTypes.POTION_CONTENTS) != null
//                    && stack.getComponents().get(DataComponentTypes.POTION_CONTENTS).matches(Potions.WATER)
//                    && entity.tryInsert(Fluids.WATER, GlassJarBlockEntity.BOTTLE_SIZE)
//            ) {
//                replaceHeldItemOrDont(world, player, stack, Items.GLASS_BOTTLE.getDefaultStack());
//                if (world.isClient()) {
//                    entity.playEmptyBottleSound();
//                }
//                entity.markDirty();
//            }
//        } else if (
//            stack.isOf(Items.GLASS_BOTTLE)
//                && entity.hasFluid()
//                && (entity.getStoredFluid() == Fluids.WATER || entity.getStoredFluid() == ModFluids.HONEY_FLUID.get())
//        ) {
//            ItemStack bottle = entity.getBottle();
//
//            if (bottle != null && !bottle.isOf(Items.GLASS_BOTTLE)) {
//                replaceHeldItemOrDont(world, player, stack, bottle);
//                if (world.isClient()) {
//                    entity.playFillBottleSound();
//                }
//            }
//        } else if (isEmptyBucket(stack) && entity.hasFluid()) {
//            Fluid fluid = entity.getBucket();
//
//            if (!fluid.matchesType(Fluids.EMPTY)) {
//                if (fluid.matchesType(Fluids.WATER)) {
//                    replaceHeldItemOrDont(world, player, stack, Items.WATER_BUCKET.getDefaultStack());
//                } else if (fluid.matchesType(Fluids.LAVA)) {
//                    replaceHeldItemOrDont(world, player, stack, Items.LAVA_BUCKET.getDefaultStack());
//                } else if (fluid.matchesType(ModFluids.MILK_FLUID.get())) {
//                    replaceHeldItemOrDont(world, player, stack, Items.MILK_BUCKET.getDefaultStack());
//                } else if (fluid.matchesType(ModFluids.HONEY_FLUID.get())) {
//                    replaceHeldItemOrDont(world, player, stack, ModFluids.HONEY_BUCKET.get().getDefaultStack());
//                }
//
//                if (world.isClient()) {
//                    entity.playFillBucketSound(fluid);
//                }
//                entity.markDirty();
//            }
//        } else if (!stack.isEmpty() && entity.canAcceptItem(stack)) {
        if (!stack.isEmpty() && entity.canAcceptItem(stack)) {
            ItemStack originalStack = stack.copy();

            // Try to insert the item in the player's hand into the jar
            ItemStack remainingStack = entity.tryInsert(stack);

            if (remainingStack.isEmpty() || originalStack.getCount() > remainingStack.getCount()) {
                player.setStackInHand(Hand.MAIN_HAND, remainingStack);
                if (world.isClient()) {
                    entity.playAddItemSound();
                }
                entity.markDirty();
            }
        } else if (player.isSneaking() && stack.isEmpty()) {
            if (entity.hasItem()) {
                ItemScatterer.spawn(
                    world,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    entity.removeStack()
                );
                if (world.isClient()) {
                    entity.playRemoveItemSound();
                }
                entity.markDirty();
            }
        }

        world.markDirty(pos);

        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient()) {
            return;
        }

        GlassJarBlockEntity entity;

        try {
            entity = (GlassJarBlockEntity) world.getBlockEntity(pos);
            assert entity != null;
        } catch (Exception e) {
            MinekeaMod.LOGGER.error(String.format("The glass jar at %s had an invalid block entity.\nBlock Entity: %s", pos, world.getBlockEntity(pos)));
            return;
        }

        entity.readDataFromItemStack(itemStack);

//        TypedEntityData<EntityType<?>> entityData = itemStack.get(DataComponentTypes.ENTITY_DATA);
//        if (entityData != null) {
//            entity.readMobNbt(entityData.copyNbtWithoutId(), world.getRegistryManager());
//        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof GlassJarBlockEntity entity) {
            if (!world.isClient()) {
                if (entity.isEmpty() && !player.isCreative()) {
                    ItemEntity itemEntity = new ItemEntity(
                        world,
                        (double) pos.getX() + 0.5D,
                        (double) pos.getY() + 0.5D,
                        (double) pos.getZ() + 0.5D,
                        ContainerItems.GLASS_JAR_ITEM.get().getDefaultStack()
                    );

                    itemEntity.setToDefaultPickupDelay();

                    world.spawnEntity(itemEntity);
                } else if (!entity.isEmpty()) {
                    ItemStack itemStack = entity.toItemStack();

                    ItemEntity itemEntity = new ItemEntity(
                        world,
                        (double) pos.getX() + 0.5D,
                        (double) pos.getY() + 0.5D,
                        (double) pos.getZ() + 0.5D,
                        itemStack
                    );

                    itemEntity.setToDefaultPickupDelay();

                    entity.clear();

                    world.spawnEntity(itemEntity);
                }
            }
        }

        return state;
    }

    private void replaceHeldItemOrDont(World world, PlayerEntity player, ItemStack heldItem, ItemStack item) {
        ItemStack remaining = heldItem.copy();
        remaining.decrement(1);

        if (remaining.getCount() == 0) {
            player.setStackInHand(Hand.MAIN_HAND, item);
        } else {
            player.setStackInHand(Hand.MAIN_HAND, remaining);
            ItemScatterer.spawn(
                world,
                player.getX(),
                player.getY(),
                player.getZ(),
                item
            );
        }
    }

//    @Override
//    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
//        super.appendTooltip(stack, context, tooltip, options);
//
//        NbtComponent customDataComponent = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA);
//        NbtComponent entityDataComponent = stack.getComponents().get(DataComponentTypes.ENTITY_DATA);
//
//        if (customDataComponent != null) {
//            NbtCompound nbt = customDataComponent.copyNbt();
//
//            String storedFluid = nbt.getString(GlassJarBlockEntity.FLUID_KEY);
//            if (!storedFluid.isEmpty() && !storedFluid.equals("NONE")) {
//                Fluid fluid = Registries.FLUID.get(Identifier.of(storedFluid));
//
//                if (fluid != Fluids.EMPTY) {
//                    MutableText text = FluidHelpers.getFluidName(fluid).copy().formatted(Formatting.GREEN);
//
//                    double fluidAmount = nbt.getDouble(GlassJarBlockEntity.FLUID_AMT_KEY);
//
//                    String format = Math.round(fluidAmount) == fluidAmount ? " (%.0f buckets)" : " (%.1f buckets)";
//                    text.append(
//                        fluidAmount != 1.0
//                            ? String.format(format, fluidAmount)
//                            : " (1 bucket)"
//                    );
//
//                    tooltip.add(text);
//                }
//            } else {
//                DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
//                Inventories.readNbt(nbt, items, context.getRegistryLookup());
//
//                ItemStack storedItem = items.get(0);
//                if (!storedItem.isEmpty()) {
//                    int fullStacks = nbt.getInt(GlassJarBlockEntity.ITEM_AMT_KEY);
//                    int total = storedItem.getCount() + (fullStacks * storedItem.getMaxCount());
//
//                    MutableText text = storedItem.getName().copy().formatted(Formatting.GREEN);
//                    text.append(String.format(" (%d)", total));
//
//                    tooltip.add(text);
//                }
//            }
//        } else if (entityDataComponent != null) {
//            NbtCompound nbt = entityDataComponent.copyNbt();
//
//            if (!nbt.isEmpty()) {
//                EntityType.get(nbt.getString("id")).ifPresent(entityType -> {
//                    MutableText text = MutableText.of(Text.of("Captured mob: ").getContent()).formatted(Formatting.GREEN);
//                    text.append(entityType.getName().copy());
//
//                    tooltip.add(text);
//                });
//            }
//        }
//    }

    @Override
    public void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof GlassJarBlockEntity) {
            world.updateComparators(pos, this);
        }

        super.onStateReplaced(state, world, pos, moved);
    }

//    private Fluid getFluidType(Identifier heldItemId) {
//        Optional<Fluid> foundFluid = Registries.FLUID.stream()
//            .filter(fluid -> {
//                Item bucket = fluid.getBucketItem();
//                return Registries.ITEM.getId(bucket).compareTo(heldItemId) == 0;
//            })
//            .findFirst();
//
//        return foundFluid.orElse(Fluids.EMPTY);
//    }
//
//    private boolean isEmptyBucket(ItemStack item) {
//        if (item.isEmpty()) {
//            return false;
//        }
//
//        return item.isOf(Items.BUCKET);
//    }
//
//    private boolean isFilledBottle(ItemStack item) {
//        if (item.isEmpty()) {
//            return false;
//        }
//
//        if (
//            item.isOf(Items.POTION)
//                && item.getComponents().get(DataComponentTypes.POTION_CONTENTS) != null
//                && item.getComponents().get(DataComponentTypes.POTION_CONTENTS).matches(Potions.WATER)
//        ) {
//            return true;
//        }
//
//        return item.isOf(Items.HONEY_BOTTLE);
//    }
//
//    private boolean isFilledBucket(ItemStack item) {
//        if (item.isEmpty()) {
//            return false;
//        }
//
//        if (
//            !(item.getItem() instanceof BucketItem)
//                && !item.getItem().getTranslationKey().equals(Items.MILK_BUCKET.asItem().getTranslationKey())
//        ) {
//            return false;
//        }
//
//        Identifier itemId = Registries.ITEM.getId(item.getItem());
//
//        return itemId.compareTo(Registries.ITEM.getId(Items.BUCKET.asItem())) != 0;
//    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(MAIN_SHAPE, LID_SHAPE);
    }

    public static ItemStack getStackToRender(ItemStack stack) {
        if (stack.isEmpty()) {
            return stack;
        }

        Identifier stackId = ItemHelpers.getIdentifier(stack);

        if (!ALLOWED_ITEM_IDS.containsKey(stackId.toString())) {
            return stack;
        }

        return Registries.ITEM.get(Identifier.of(ALLOWED_ITEM_IDS.get(stackId.toString()))).getDefaultStack();
    }
}
