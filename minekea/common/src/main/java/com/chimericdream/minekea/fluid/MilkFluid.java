package com.chimericdream.minekea.fluid;

import com.chimericdream.minekea.ModInfo;
import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class MilkFluid extends ArchitecturyFlowingFluid.Source {
    public static final ArchitecturyFluidAttributes ATTRIBUTES = SimpleArchitecturyFluidAttributes.of(
            ModFluids.FLOWING_MILK,
            ModFluids.MILK_FLUID
        )
        .blockSupplier(() -> ModFluids.MILK_SOURCE_BLOCK)
        .slopeFindDistance(2)
        .dropOff(2)
        .tickDelay(10)
        .sourceTexture(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/fluids/milk"))
        .flowingTexture(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/fluids/milk/flowing"))
        .fillSound(SoundEvents.BUCKET_FILL)
        .color(0xFFFFFF);

    public MilkFluid() {
        super(MilkFluid.ATTRIBUTES);
    }

    @Override
    public @NotNull Item getBucket() {
        return Items.MILK_BUCKET;
    }

    @Override
    protected @NotNull BlockState createLegacyBlock(FluidState state) {
        return ModFluids.MILK_SOURCE_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    public static class Flowing extends ArchitecturyFlowingFluid.Flowing {
        public Flowing() {
            super(MilkFluid.ATTRIBUTES);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
        }
    }

    public static class Block extends ArchitecturyLiquidBlock {
        public Block() {
            super(ModFluids.MILK_FLUID, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).setId(REGISTRY_HELPER.makeBlockRegistryKey(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "fluids/milk/source"))));
        }

        @Override
        public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl) {
            super.entityInside(state, world, pos, entity, handler, bl);

            int level = state.getValue(LiquidBlock.LEVEL);

            if (!world.isClientSide() && entity instanceof LivingEntity && level == 0) {
                ((LivingEntity) entity).removeAllEffects();
            }
        }
    }
}
