package com.chimericdream.minekea.fluid;

import com.chimericdream.minekea.ModInfo;
import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.core.item.ArchitecturyBucketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
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

public class HoneyFluid extends ArchitecturyFlowingFluid.Source {
    public static final ArchitecturyFluidAttributes ATTRIBUTES = SimpleArchitecturyFluidAttributes.of(
            ModFluids.FLOWING_HONEY,
            ModFluids.HONEY_FLUID
        )
        .blockSupplier(() -> ModFluids.HONEY_SOURCE_BLOCK)
        .bucketItemSupplier(() -> ModFluids.HONEY_BUCKET)
        .slopeFindDistance(2)
        .dropOff(2)
        .tickDelay(40)
        .sourceTexture(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/fluids/honey"))
        .flowingTexture(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/fluids/honey/flowing"))
        .fillSound(SoundEvents.BUCKET_FILL_LAVA)
        .color(0xFFFFFF);

    public HoneyFluid() {
        super(HoneyFluid.ATTRIBUTES);
    }

    @Override
    protected @NotNull BlockState createLegacyBlock(FluidState state) {
        return ModFluids.HONEY_SOURCE_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    public static class Flowing extends ArchitecturyFlowingFluid.Flowing {
        public Flowing() {
            super(HoneyFluid.ATTRIBUTES);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
        }
    }

    public static class Block extends ArchitecturyLiquidBlock {
        public Block() {
            super(ModFluids.HONEY_FLUID, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).setId(REGISTRY_HELPER.makeBlockRegistryKey(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "fluids/honey/source"))));
        }

        @Override
        public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl) {
            super.entityInside(state, world, pos, entity, handler, bl);

            if (!world.isClientSide() && entity instanceof LivingEntity) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 300, 2));
            }
        }
    }

    public static class Bucket extends ArchitecturyBucketItem {
        public Bucket() {
            super(ModFluids.HONEY_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).arch$tab(CreativeModeTabs.INGREDIENTS).setId(REGISTRY_HELPER.makeItemRegistryKey(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "containers/honey_bucket"))));
        }
    }
}

