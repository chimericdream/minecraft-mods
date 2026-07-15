package com.chimericdream.minekea.block.containers;

import com.chimericdream.lib.items.ItemHelpers;
import com.chimericdream.minekea.MinekeaMod;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.general.WaxBlock;
import com.chimericdream.minekea.block.building.storage.BlueEggCrateBlock;
import com.chimericdream.minekea.block.building.storage.BrownEggCrateBlock;
import com.chimericdream.minekea.block.building.storage.DyeBlock;
import com.chimericdream.minekea.block.building.storage.EggCrateBlock;
import com.chimericdream.minekea.block.building.storage.StorageBlocks;
import com.chimericdream.minekea.crop.WarpedWartItem;
import com.chimericdream.minekea.entity.block.containers.GlassJarBlockEntity;
import com.chimericdream.minekea.fluid.ModFluids;
import com.chimericdream.minekea.item.containers.ContainerItems;
import com.chimericdream.minekea.item.ingredients.WaxItem;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;
import static com.chimericdream.minekea.sound.MinekeaSoundGroup.GLASS_JAR_SOUND_GROUP;

public class GlassJarBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<GlassJarBlock> CODEC = simpleCodec(GlassJarBlock::new);

    public static final Map<String, String> ALLOWED_ITEM_IDS = new LinkedHashMap<>();
    public static final List<Item> ALLOWED_ITEMS = new ArrayList<>();

    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty WATERLOGGED;

    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "containers/glass_jar");

    private static final VoxelShape MAIN_SHAPE;
    private static final VoxelShape LID_SHAPE;

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;

        ALLOWED_ITEMS.add(Items.AMETHYST_SHARD);
        ALLOWED_ITEMS.add(Items.APPLE);
        ALLOWED_ITEMS.add(Items.BAMBOO);
        ALLOWED_ITEMS.add(Items.BEETROOT);
        ALLOWED_ITEMS.add(Items.BEETROOT_SEEDS);
        ALLOWED_ITEMS.add(Items.BLAZE_POWDER);
        ALLOWED_ITEMS.add(Items.BLAZE_ROD);
        ALLOWED_ITEMS.add(Items.BLUE_EGG);
        ALLOWED_ITEMS.add(Items.BREEZE_ROD);
        ALLOWED_ITEMS.add(Items.BROWN_MUSHROOM);
        ALLOWED_ITEMS.add(Items.BROWN_EGG);
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

        ALLOWED_ITEMS.add(Items.DYE.white());
        ALLOWED_ITEMS.add(Items.DYE.lightGray());
        ALLOWED_ITEMS.add(Items.DYE.gray());
        ALLOWED_ITEMS.add(Items.DYE.black());
        ALLOWED_ITEMS.add(Items.DYE.brown());
        ALLOWED_ITEMS.add(Items.DYE.red());
        ALLOWED_ITEMS.add(Items.DYE.orange());
        ALLOWED_ITEMS.add(Items.DYE.yellow());
        ALLOWED_ITEMS.add(Items.DYE.lime());
        ALLOWED_ITEMS.add(Items.DYE.green());
        ALLOWED_ITEMS.add(Items.DYE.cyan());
        ALLOWED_ITEMS.add(Items.DYE.lightBlue());
        ALLOWED_ITEMS.add(Items.DYE.blue());
        ALLOWED_ITEMS.add(Items.DYE.purple());
        ALLOWED_ITEMS.add(Items.DYE.magenta());
        ALLOWED_ITEMS.add(Items.DYE.pink());

        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.white());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.lightGray());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.gray());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.black());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.brown());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.red());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.orange());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.yellow());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.lime());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.green());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.cyan());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.lightBlue());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.blue());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.purple());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.magenta());
        ALLOWED_ITEMS.add(Items.CONCRETE_POWDER.pink());

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
        ALLOWED_ITEM_IDS.put("minecraft:apple", StorageBlocks.APPLE_STORAGE_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:bamboo", "minecraft:bamboo_block");
        ALLOWED_ITEM_IDS.put("minecraft:beetroot", StorageBlocks.BEETROOT_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:beetroot_seeds", StorageBlocks.BEETROOT_SEEDS_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:blaze_powder", StorageBlocks.BLAZE_POWDER_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:blaze_rod", StorageBlocks.BLAZE_ROD_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:blue_egg", BlueEggCrateBlock.BLOCK_ID.toString());
        ALLOWED_ITEM_IDS.put("minecraft:breeze_rod", StorageBlocks.BREEZE_ROD_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:brown_egg", BrownEggCrateBlock.BLOCK_ID.toString());
        ALLOWED_ITEM_IDS.put("minecraft:brown_mushroom", "minecraft:brown_mushroom_block");
        ALLOWED_ITEM_IDS.put("minecraft:carrot", StorageBlocks.CARROT_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:charcoal", StorageBlocks.CHARCOAL_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:chorus_fruit", StorageBlocks.CHORUS_FRUIT_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:coal", "minecraft:coal_block");
        ALLOWED_ITEM_IDS.put("minecraft:dried_kelp", "minecraft:dried_kelp_block");
        ALLOWED_ITEM_IDS.put("minecraft:egg", EggCrateBlock.BLOCK_ID.toString());
        ALLOWED_ITEM_IDS.put("minecraft:ender_pearl", StorageBlocks.ENDER_PEARL_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:flint", StorageBlocks.FLINT_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:glowstone_dust", "minecraft:glowstone");
        ALLOWED_ITEM_IDS.put("minecraft:golden_apple", StorageBlocks.GOLDEN_APPLE_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:gravel", "minecraft:gravel");
        ALLOWED_ITEM_IDS.put("minecraft:honeycomb", "minecraft:honeycomb_block");
        ALLOWED_ITEM_IDS.put("minecraft:leather", StorageBlocks.LEATHER_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:melon_seeds", StorageBlocks.MELON_SEEDS_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:melon_slice", "minecraft:melon");
        ALLOWED_ITEM_IDS.put("minecraft:nether_star", StorageBlocks.NETHER_STAR_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:nether_wart", "minecraft:nether_wart_block");
        ALLOWED_ITEM_IDS.put("minecraft:paper", StorageBlocks.WALLPAPER_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:phantom_membrane", StorageBlocks.PHANTOM_MEMBRANE_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:potato", StorageBlocks.POTATO_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:pumpkin_seeds", StorageBlocks.PUMPKIN_SEEDS_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:red_mushroom", "minecraft:red_mushroom_block");
        ALLOWED_ITEM_IDS.put("minecraft:red_sand", "minecraft:red_sand");
        ALLOWED_ITEM_IDS.put("minecraft:redstone", "minecraft:redstone_block");
        ALLOWED_ITEM_IDS.put("minecraft:resin_clump", "minecraft:resin_block");
        ALLOWED_ITEM_IDS.put("minecraft:sand", "minecraft:sand");
        ALLOWED_ITEM_IDS.put("minecraft:sculk_vein", "minecraft:sculk");
        ALLOWED_ITEM_IDS.put("minecraft:slime_ball", "minecraft:slime_block");
        ALLOWED_ITEM_IDS.put("minecraft:stick", StorageBlocks.STICK_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:sugar", StorageBlocks.SUGAR_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:sugar_cane", StorageBlocks.SUGAR_CANE_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put("minecraft:totem_of_undying", StorageBlocks.TOTEM_BLOCK.getId().toString());
        ALLOWED_ITEM_IDS.put(WarpedWartItem.ITEM_ID.toString(), "minecraft:warped_wart_block");
        ALLOWED_ITEM_IDS.put("minecraft:wheat", "minecraft:hay_block");
        ALLOWED_ITEM_IDS.put("minecraft:wheat_seeds", StorageBlocks.WHEAT_SEEDS_BLOCK.getId().toString());

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

        MAIN_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 9.0, 11.0);
        LID_SHAPE = Block.box(6.0, 9.0, 6.0, 10.0, 10.0, 10.0);
    }

    public GlassJarBlock(BlockBehaviour.Properties settings) {
        this();
    }

    public GlassJarBlock() {
        super(Properties.ofFullCopy(Blocks.GLASS).sound(GLASS_JAR_SOUND_GROUP).noOcclusion().setId(REGISTRY_HELPER.makeBlockRegistryKey(GlassJarBlock.BLOCK_ID)));

        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
        );
    }

    protected @NotNull MapCodec<GlassJarBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        Direction facing = Direction.NORTH;

        if (player != null) {
            facing = player.getDirection().getOpposite();
        }

        return this.defaultBlockState()
            .setValue(FACING, facing)
            .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        GlassJarBlockEntity entity;
        try {
            entity = (GlassJarBlockEntity) world.getBlockEntity(pos);
            assert entity != null;
        } catch (Exception e) {
            MinekeaMod.LOGGER.error("The glass jar at {} had an invalid block entity.\nBlock Entity: {}", pos, world.getBlockEntity(pos));

            return;
        }

        String mobId = entity.getMobId();
        if (mobId == null) {
            return;
        }

        SoundEvent sound = getMobSound(mobId);
        if (world instanceof ClientLevel && sound != null && random.nextInt(100) == 0) {
            world.playSound(null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, sound, SoundSource.BLOCKS, 0.5F, 0.5F);
        }
    }

    @Nullable
    private SoundEvent getMobSound(String mobId) {
        return switch (mobId) {
            case "minecraft:allay" -> SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
            case "minecraft:bat" -> SoundEvents.BAT_AMBIENT;
            case "minecraft:bee" -> SoundEvents.BEE_POLLINATE;
            case "minecraft:endermite" -> SoundEvents.ENDERMITE_AMBIENT;
            case "minecraft:silverfish" -> SoundEvents.SILVERFISH_AMBIENT;
            case "minecraft:slime" -> SoundEvents.SLIME_SQUISH;
            case "minecraft:vex" -> SoundEvents.VEX_AMBIENT;
            default -> null;
        };
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickView.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlassJarBlockEntity(ContainerBlocks.GLASS_JAR_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        GlassJarBlockEntity entity;

        try {
            entity = (GlassJarBlockEntity) world.getBlockEntity(pos);
            assert entity != null;
        } catch (Exception e) {
            MinekeaMod.LOGGER.error("The glass jar at {} had an invalid block entity.\nBlock Entity: {}", pos, world.getBlockEntity(pos));

            return InteractionResult.FAIL;
        }

        if (isFilledBucket(stack)) {
            Identifier stackId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            Fluid bucketFluid = getFluidType(stackId);

            if (!bucketFluid.isSame(Fluids.EMPTY) && entity.tryInsert(bucketFluid)) {
                replaceHeldItemOrDont(world, player, stack, Items.BUCKET.getDefaultInstance());
                if (world.isClientSide()) {
                    entity.playEmptyBucketSound(bucketFluid);
                }
                entity.setChanged();
            }
        } else if (isFilledBottle(stack)) {
            if (
                stack.is(Items.HONEY_BOTTLE)
                    && entity.tryInsert(ModFluids.HONEY_FLUID.get(), GlassJarBlockEntity.BOTTLE_SIZE)
            ) {
                replaceHeldItemOrDont(world, player, stack, Items.GLASS_BOTTLE.getDefaultInstance());
                if (world.isClientSide()) {
                    entity.playEmptyBottleSound();
                }
                entity.setChanged();
            } else if (
                stack.is(Items.POTION)
                    && stack.getComponents().get(DataComponents.POTION_CONTENTS) != null
                    && stack.getComponents().get(DataComponents.POTION_CONTENTS).is(Potions.WATER)
                    && entity.tryInsert(Fluids.WATER, GlassJarBlockEntity.BOTTLE_SIZE)
            ) {
                replaceHeldItemOrDont(world, player, stack, Items.GLASS_BOTTLE.getDefaultInstance());
                if (world.isClientSide()) {
                    entity.playEmptyBottleSound();
                }
                entity.setChanged();
            }
        } else if (
            stack.is(Items.GLASS_BOTTLE)
                && entity.hasFluid()
                && (entity.getStoredFluid() == Fluids.WATER || entity.getStoredFluid() == ModFluids.HONEY_FLUID.get())
        ) {
            ItemStack bottle = entity.getBottle();

            if (bottle != null && !bottle.is(Items.GLASS_BOTTLE)) {
                replaceHeldItemOrDont(world, player, stack, bottle);
                if (world.isClientSide()) {
                    entity.playFillBottleSound();
                }
            }
        } else if (isEmptyBucket(stack) && entity.hasFluid()) {
            Fluid fluid = entity.getBucket();

            if (!fluid.isSame(Fluids.EMPTY)) {
                if (fluid.isSame(Fluids.WATER)) {
                    replaceHeldItemOrDont(world, player, stack, Items.WATER_BUCKET.getDefaultInstance());
                } else if (fluid.isSame(Fluids.LAVA)) {
                    replaceHeldItemOrDont(world, player, stack, Items.LAVA_BUCKET.getDefaultInstance());
                } else if (fluid.isSame(ModFluids.MILK_FLUID.get())) {
                    replaceHeldItemOrDont(world, player, stack, Items.MILK_BUCKET.getDefaultInstance());
                } else if (fluid.isSame(ModFluids.HONEY_FLUID.get())) {
                    replaceHeldItemOrDont(world, player, stack, ModFluids.HONEY_BUCKET.get().getDefaultInstance());
                }

                if (world.isClientSide()) {
                    entity.playFillBucketSound(fluid);
                }
                entity.setChanged();
            }
        } else if (!stack.isEmpty() && entity.canAcceptItem(stack)) {
            if (!stack.isEmpty() && entity.canAcceptItem(stack)) {
                ItemStack originalStack = stack.copy();

                // Try to insert the item in the player's hand into the jar
                ItemStack remainingStack = entity.tryInsert(stack);

                if (remainingStack.isEmpty() || originalStack.getCount() > remainingStack.getCount()) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, remainingStack);
                    if (world.isClientSide()) {
                        entity.playAddItemSound();
                    }
                    entity.setChanged();
                }
            }
        } else if (player.isShiftKeyDown() && stack.isEmpty()) {
            if (entity.hasItem()) {
                Containers.dropItemStack(
                    world,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    entity.removeStack()
                );
                if (world.isClientSide()) {
                    entity.playRemoveItemSound();
                }
                entity.setChanged();
            }
        }

        world.blockEntityChanged(pos);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide()) {
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
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof GlassJarBlockEntity entity) {
            if (!world.isClientSide()) {
                if (entity.isEmpty() && !player.isCreative()) {
                    ItemEntity itemEntity = new ItemEntity(
                        world,
                        (double) pos.getX() + 0.5D,
                        (double) pos.getY() + 0.5D,
                        (double) pos.getZ() + 0.5D,
                        ContainerItems.GLASS_JAR_ITEM.get().getDefaultInstance()
                    );

                    itemEntity.setDefaultPickUpDelay();

                    world.addFreshEntity(itemEntity);
                } else if (!entity.isEmpty()) {
                    ItemStack itemStack = entity.toItemStack();

                    ItemEntity itemEntity = new ItemEntity(
                        world,
                        (double) pos.getX() + 0.5D,
                        (double) pos.getY() + 0.5D,
                        (double) pos.getZ() + 0.5D,
                        itemStack
                    );

                    itemEntity.setDefaultPickUpDelay();

                    world.addFreshEntity(itemEntity);
                }
            }
        }

        world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);

        return state;
    }

    private void replaceHeldItemOrDont(Level world, Player player, ItemStack heldItem, ItemStack item) {
        ItemStack remaining = heldItem.copy();
        remaining.shrink(1);

        if (remaining.getCount() == 0) {
            player.setItemInHand(InteractionHand.MAIN_HAND, item);
        } else {
            player.setItemInHand(InteractionHand.MAIN_HAND, remaining);
            Containers.dropItemStack(
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
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof GlassJarBlockEntity) {
            world.updateNeighbourForOutputSignal(pos, this);
        }

        super.affectNeighborsAfterRemoval(state, world, pos, moved);
    }

    private Fluid getFluidType(Identifier heldItemId) {
        Optional<Fluid> foundFluid = BuiltInRegistries.FLUID.stream()
            .filter(fluid -> {
                Item bucket = fluid.getBucket();
                return BuiltInRegistries.ITEM.getKey(bucket).compareTo(heldItemId) == 0;
            })
            .findFirst();

        return foundFluid.orElse(Fluids.EMPTY);
    }

    private boolean isEmptyBucket(ItemStack item) {
        if (item.isEmpty()) {
            return false;
        }

        return item.is(Items.BUCKET);
    }

    private boolean isFilledBottle(ItemStack item) {
        if (item.isEmpty()) {
            return false;
        }

        if (
            item.is(Items.POTION)
                && item.getComponents().get(DataComponents.POTION_CONTENTS) != null
                && item.getComponents().get(DataComponents.POTION_CONTENTS).is(Potions.WATER)
        ) {
            return true;
        }

        return item.is(Items.HONEY_BOTTLE);
    }

    private boolean isFilledBucket(ItemStack item) {
        if (item.isEmpty()) {
            return false;
        }

        if (
            !(item.getItem() instanceof BucketItem)
                && !item.getItem().getDescriptionId().equals(Items.MILK_BUCKET.asItem().getDescriptionId())
        ) {
            return false;
        }

        Identifier itemId = BuiltInRegistries.ITEM.getKey(item.getItem());

        return itemId.compareTo(BuiltInRegistries.ITEM.getKey(Items.BUCKET.asItem())) != 0;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.or(MAIN_SHAPE, LID_SHAPE);
    }

    public static ItemStack getStackToRender(ItemStack stack) {
        if (stack.isEmpty()) {
            return stack;
        }

        Identifier stackId = ItemHelpers.getIdentifier(stack);

        if (!ALLOWED_ITEM_IDS.containsKey(stackId.toString())) {
            return stack;
        }

        return BuiltInRegistries.ITEM.getValue(Identifier.parse(ALLOWED_ITEM_IDS.get(stackId.toString()))).getDefaultInstance();
    }
}
