package com.chimericdream.minekea.block.containers;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.fluid.ModFluids;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;
import static net.minecraft.core.cauldron.CauldronInteraction.*;

public class MilkCauldronBlock extends AbstractCauldronBlock {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "containers/cauldrons/milk");
    public static CauldronInteraction.InteractionMap BEHAVIORS = newInteractionMap("milk");
    public static final MapCodec<MilkCauldronBlock> CODEC = simpleCodec(MilkCauldronBlock::new);

    public static final CauldronInteraction FILL_WITH_MILK;
    public static final CauldronInteraction EMPTY_CAULDRON;

    static {
        FILL_WITH_MILK = (state, world, pos, player, hand, stack) -> emptyBucket(
            world,
            pos,
            player,
            hand,
            stack,
            ModFluids.MILK_CAULDRON.get().defaultBlockState(),
            SoundEvents.BUCKET_EMPTY
        );
        EMPTY_CAULDRON = (state, world, pos, player, hand, stack) -> fillBucket(
            state,
            world,
            pos,
            player,
            hand,
            stack,
            new ItemStack(Items.MILK_BUCKET),
            statex -> true,
            SoundEvents.BUCKET_FILL
        );

        BEHAVIORS.map().put(Items.MILK_BUCKET, FILL_WITH_MILK);
        BEHAVIORS.map().put(Items.BUCKET, EMPTY_CAULDRON);

        CauldronInteraction.EMPTY.map().put(Items.MILK_BUCKET, FILL_WITH_MILK);
    }

    public MilkCauldronBlock(BlockBehaviour.Properties settings) {
        this();
    }

    public MilkCauldronBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)), BEHAVIORS);
    }

    @Override
    protected MapCodec<MilkCauldronBlock> codec() {
        return CODEC;
    }

    protected double getContentHeight(BlockState state) {
        return 0.9375;
    }

    public boolean isFull(BlockState state) {
        return true;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl) {
        if (world instanceof ServerLevel serverWorld) {
            BlockPos blockPos = pos.immutable();
            handler.runBefore(InsideBlockEffectType.EXTINGUISH, collidedEntity -> {
                if (collidedEntity instanceof LivingEntity livingEntity && collidedEntity.mayInteract(serverWorld, blockPos)) {
                    livingEntity.removeAllEffects();
                }
            });
        }

        handler.apply(InsideBlockEffectType.EXTINGUISH);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return 3;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return Items.CAULDRON.getDefaultInstance();
    }
}
