package com.chimericdream.minekea.block.containers;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.fluid.ModFluids;
import com.chimericdream.minekea.mixin.CauldronDispatcherAccessor;
import com.chimericdream.minekea.mixin.CauldronInteractionsAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class HoneyCauldronBlock extends LayeredCauldronBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "containers/cauldrons/honey");
    public static CauldronInteraction.Dispatcher BEHAVIORS = new CauldronInteraction.Dispatcher();

    public static final CauldronInteraction FILL_WITH_HONEY;
    public static final CauldronInteraction EMPTY_CAULDRON;
    public static final CauldronInteraction FILL_FROM_BOTTLE;

    static {
        FILL_WITH_HONEY = (state, world, pos, player, hand, stack) -> CauldronInteractionsAccessor.minekea$invokeEmptyBucket(
            world,
            pos,
            player,
            hand,
            stack,
            ModFluids.HONEY_CAULDRON.get().defaultBlockState().setValue(LEVEL, 3),
            SoundEvents.BUCKET_EMPTY_LAVA
        );
        EMPTY_CAULDRON = (state, world, pos, player, hand, stack) -> CauldronInteractionsAccessor.minekea$invokeFillBucket(
            state,
            world,
            pos,
            player,
            hand,
            stack,
            new ItemStack(ModFluids.HONEY_BUCKET.get()),
            statex -> true,
            SoundEvents.BUCKET_FILL_LAVA
        );
        FILL_FROM_BOTTLE = (state, world, pos, player, hand, stack) -> {
            if (!world.isClientSide()) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                world.setBlockAndUpdate(pos, ModFluids.HONEY_CAULDRON.get().defaultBlockState());
                world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }

            return InteractionResult.SUCCESS;
        };

        ((CauldronDispatcherAccessor) (Object) BEHAVIORS).minekea$invokePut(Items.BUCKET, EMPTY_CAULDRON);
        ((CauldronDispatcherAccessor) (Object) BEHAVIORS).minekea$invokePut(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClientSide()) {
                Item item = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, Items.HONEY_BOTTLE.getDefaultInstance()));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(item));
                LayeredCauldronBlock.lowerFillLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return InteractionResult.SUCCESS;
        });
        ((CauldronDispatcherAccessor) (Object) BEHAVIORS).minekea$invokePut(Items.HONEY_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (state.getValue(LayeredCauldronBlock.LEVEL) != 3) {
                if (!world.isClientSide()) {
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.awardStat(Stats.USE_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    world.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
                    world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
        });

        ((CauldronDispatcherAccessor) (Object) CauldronInteractions.EMPTY).minekea$invokePut(Items.HONEY_BOTTLE, FILL_FROM_BOTTLE);

        ModFluids.HONEY_BUCKET.listen((bucket) -> {
            ((CauldronDispatcherAccessor) (Object) BEHAVIORS).minekea$invokePut(bucket, FILL_WITH_HONEY);
            ((CauldronDispatcherAccessor) (Object) CauldronInteractions.EMPTY).minekea$invokePut(bucket, FILL_WITH_HONEY);
        });
    }

    public HoneyCauldronBlock(BlockBehaviour.Properties settings) {
        this();
    }

    public HoneyCauldronBlock() {
        super(Biome.Precipitation.NONE, BEHAVIORS, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).setId(ResourceKey.create(Registries.BLOCK, BLOCK_ID)));
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl) {
        if (world instanceof ServerLevel serverWorld) {
            BlockPos blockPos = pos.immutable();
            handler.runBefore(InsideBlockEffectType.EXTINGUISH, collidedEntity -> {
                if (collidedEntity instanceof LivingEntity livingEntity && collidedEntity.mayInteract(serverWorld, blockPos)) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 15));
                }
            });
        }

        handler.apply(InsideBlockEffectType.EXTINGUISH);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return Items.CAULDRON.getDefaultInstance();
    }
}
