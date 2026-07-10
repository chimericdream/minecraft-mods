package com.chimericdream.minekea.block.building.dyed;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class DyedPillarBlock extends RotatedPillarBlock {
    public static final EnumProperty<Direction.Axis> AXIS;

    static {
        AXIS = BlockStateProperties.AXIS;
    }

    public final Identifier BLOCK_ID;
    public final BlockConfig config;
    public final DyeColor color;

    public DyedPillarBlock(
        BlockConfig config,
        DyeColor color
    ) {
        super(config.getBaseSettings().mapColor(color).setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial(), color))));

        this.color = color;

        BLOCK_ID = makeId(config.getMaterial(), color);
        this.config = config;

        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    public static Identifier makeId(String material, DyeColor color) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/dyed/%s/%s", material, color));
    }

    public BlockConfig getConfig() {
        return config;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(AXIS, ctx.getClickedFace().getAxis());
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        PotionContents potionContentsComponent = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        BlockState blockState = world.getBlockState(pos);
        if (hit.getDirection() != Direction.DOWN && potionContentsComponent.is(Potions.WATER)) {
            world.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

            if (!world.isClientSide()) {
                ServerLevel serverWorld = (ServerLevel) world;

                for (int i = 0; i < 5; ++i) {
                    serverWorld.sendParticles(ParticleTypes.SPLASH, (double) pos.getX() + world.random.nextDouble(), pos.getY() + 1, (double) pos.getZ() + world.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                }
            }

            world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            world.setBlockAndUpdate(pos, config.getIngredient().defaultBlockState().setValue(AXIS, blockState.getValue(AXIS)));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}
