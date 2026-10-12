package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

/**
 * A cauldron of cider. A full one with mulling spices added, above a lit campfire, simmers for about 30 seconds and turns into a
 * {@link MulledAppleCiderCauldronBlock}, keeping whatever spices were added. If the fire goes out, the
 * simmer progress resets but the spices stay.
 */
public class AppleCiderCauldronBlock extends LayeredCauldronBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "apple_cider_cauldron");

    /** Simmer steps done so far; one more heated step after this value finishes the batch. */
    public static final int MAX_SIMMER = 4;
    public static final IntegerProperty SIMMER = IntegerProperty.create("simmer", 0, MAX_SIMMER);

    /** Ticks per simmer step: 5 steps of 6 seconds is about 30 seconds. */
    public static final int TICK_INTERVAL = 120;

    public AppleCiderCauldronBlock(BlockBehaviour.Properties settings) {
        this();
    }

    public AppleCiderCauldronBlock() {
        super(
            Biome.Precipitation.NONE,
            AppleCiderCauldrons.CIDER_BEHAVIORS,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID))
        );

        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(LEVEL, 1)
                .setValue(AppleCiderCauldrons.SPICED, false)
                .setValue(SIMMER, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AppleCiderCauldrons.SPICED, SIMMER);
    }

    /** Whether a lit campfire (or soul campfire) sits directly under the cauldron. */
    public static boolean isHeated(LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());

        return below.is(BlockTags.CAMPFIRES) && below.hasProperty(CampfireBlock.LIT) && below.getValue(CampfireBlock.LIT);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide() && isFull(state)) {
            level.scheduleTick(pos, this, TICK_INTERVAL);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        // No simmering until the mulling spices are in, whatever is under the cauldron.
        if (!isFull(state) || !state.getValue(AppleCiderCauldrons.SPICED)) {
            return;
        }

        int simmer = state.getValue(SIMMER);

        if (!isHeated(level, pos)) {
            if (simmer != 0) {
                level.setBlockAndUpdate(pos, state.setValue(SIMMER, 0));
            }
        } else if (simmer >= MAX_SIMMER) {
            level.setBlockAndUpdate(
                pos,
                ModBlocks.MULLED_APPLE_CIDER_CAULDRON.get().defaultBlockState()
                    .setValue(LEVEL, state.getValue(LEVEL))
            );
            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 0.8F);

            return;
        } else {
            level.setBlockAndUpdate(pos, state.setValue(SIMMER, simmer + 1));
        }

        level.scheduleTick(pos, this, TICK_INTERVAL);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(SIMMER) > 0 && random.nextInt(3) == 0) {
            level.addParticle(
                ParticleTypes.BUBBLE_POP,
                pos.getX() + 0.25 + random.nextDouble() * 0.5,
                pos.getY() + getContentHeight(state),
                pos.getZ() + 0.25 + random.nextDouble() * 0.5,
                0.0, 0.0, 0.0
            );
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean isPrecise) {
        handler.apply(InsideBlockEffectType.EXTINGUISH);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return Items.CAULDRON.getDefaultInstance();
    }
}
