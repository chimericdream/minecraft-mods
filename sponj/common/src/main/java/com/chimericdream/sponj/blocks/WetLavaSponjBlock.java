package com.chimericdream.sponj.blocks;

import com.chimericdream.sponj.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WetLavaSponjBlock extends Block {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "wet_lava_sponj");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public WetLavaSponjBlock() {
        super(Properties.ofFullCopy(Blocks.SPONGE).setId(BLOCK_REGISTRY_KEY));
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.dimensionType().bedWorks() && !world.dimensionType().ultraWarm()) {
            world.setBlock(pos, ModBlocks.LAVA_SPONJ_BLOCK.get().defaultBlockState(), 3);
            world.levelEvent(2009, pos, 0);
            world.playSound((Player) null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
        }
    }

    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        Direction direction = Direction.getRandom(random);
        if (direction != Direction.UP) {
            BlockPos blockPos = pos.relative(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!state.canOcclude() || !blockState.isFaceSturdy(world, blockPos, direction.getOpposite())) {
                double d = (double) pos.getX();
                double e = (double) pos.getY();
                double f = (double) pos.getZ();
                if (direction == Direction.DOWN) {
                    e -= 0.05;
                    d += random.nextDouble();
                    f += random.nextDouble();
                } else {
                    e += random.nextDouble() * 0.8;
                    if (direction.getAxis() == Direction.Axis.X) {
                        f += random.nextDouble();
                        if (direction == Direction.EAST) {
                            ++d;
                        } else {
                            d += 0.05;
                        }
                    } else {
                        d += random.nextDouble();
                        if (direction == Direction.SOUTH) {
                            ++f;
                        } else {
                            f += 0.05;
                        }
                    }
                }

                world.addAlwaysVisibleParticle(ParticleTypes.DRIPPING_LAVA, d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }
}
