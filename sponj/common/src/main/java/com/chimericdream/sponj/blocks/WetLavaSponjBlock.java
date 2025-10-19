package com.chimericdream.sponj.blocks;

import com.chimericdream.sponj.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class WetLavaSponjBlock extends Block {
    public static final Identifier BLOCK_ID = Identifier.of(ModInfo.MOD_ID, "wet_lava_sponj");
    public static final RegistryKey<Block> BLOCK_REGISTRY_KEY = RegistryKey.of(RegistryKeys.BLOCK, BLOCK_ID);
    public static final RegistryKey<Item> ITEM_REGISTRY_KEY = RegistryKey.of(RegistryKeys.ITEM, BLOCK_ID);

    public WetLavaSponjBlock() {
        super(Settings.copy(Blocks.SPONGE).registryKey(BLOCK_REGISTRY_KEY));
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.getDimension().bedWorks() && !world.getDimension().ultrawarm()) {
            world.setBlockState(pos, ModBlocks.LAVA_SPONJ_BLOCK.get().getDefaultState(), 3);
            world.syncWorldEvent(2009, pos, 0);
            world.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
        }

    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Direction direction = Direction.random(random);
        if (direction != Direction.UP) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!state.isOpaque() || !blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
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

                world.addImportantParticleClient(ParticleTypes.DRIPPING_LAVA, d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }
}
