package com.chimericdream.polycraft.mixin;

import com.chimericdream.polycraft.core.PinMap;
import com.chimericdream.polycraft.recipe.FurnaceResolver;
import com.chimericdream.polycraft.recipe.FurnaceState;
import com.chimericdream.polycraft.recipe.StationBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Furnaces, blast furnaces and smokers. PolyCraft keeps its pins and the recipe in use on the block, and wraps the two
 * places vanilla looks up the recipe: each tick, and when the input item changes.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class PCAbstractFurnaceBlockEntityMixin implements StationBlock {
    private static final String CACHED_CHECK_LOOKUP =
        "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/server/level/ServerLevel;)Ljava/util/Optional;";

    @Shadow
    private int cookingTimer;

    @Shadow
    private int cookingTotalTime;

    @Unique
    private final FurnaceState polycraft$state = new FurnaceState();

    @Unique
    private RecipeType<?> polycraft$recipeType;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void polycraft$rememberRecipeType(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType, CallbackInfo ci) {
        this.polycraft$recipeType = recipeType;
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void polycraft$save(ValueOutput output, CallbackInfo ci) {
        polycraft$state.save(output);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void polycraft$load(ValueInput input, CallbackInfo ci) {
        polycraft$state.load(input);
    }

    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = CACHED_CHECK_LOOKUP))
    private static Optional<RecipeHolder<? extends AbstractCookingRecipe>> polycraft$resolveEachTick(
        RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> check,
        RecipeInput input,
        ServerLevel level,
        Operation<Optional<RecipeHolder<? extends AbstractCookingRecipe>>> original,
        @Local(argsOnly = true) AbstractFurnaceBlockEntity entity
    ) {
        Optional<RecipeHolder<? extends AbstractCookingRecipe>> vanilla = original.call(check, input, level);
        return resolve(level, entity, (SingleRecipeInput) input, vanilla, false);
    }

    @WrapOperation(method = "getTotalCookTime", at = @At(value = "INVOKE", target = CACHED_CHECK_LOOKUP))
    private static Optional<RecipeHolder<? extends AbstractCookingRecipe>> polycraft$resolveOnInputChange(
        RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> check,
        RecipeInput input,
        ServerLevel level,
        Operation<Optional<RecipeHolder<? extends AbstractCookingRecipe>>> original,
        @Local(argsOnly = true) AbstractFurnaceBlockEntity entity
    ) {
        Optional<RecipeHolder<? extends AbstractCookingRecipe>> vanilla = original.call(check, input, level);
        return resolve(level, entity, (SingleRecipeInput) input, vanilla, true);
    }

    @Unique
    private static Optional<RecipeHolder<? extends AbstractCookingRecipe>> resolve(
        ServerLevel level,
        AbstractFurnaceBlockEntity entity,
        SingleRecipeInput input,
        Optional<RecipeHolder<? extends AbstractCookingRecipe>> vanilla,
        boolean inputChanged
    ) {
        try {
            return FurnaceResolver.resolve(level, entity, input, vanilla, inputChanged);
        } catch (RuntimeException e) {
            com.chimericdream.polycraft.PolyCraftMod.LOGGER.error("PolyCraft couldn't pick a furnace recipe; using vanilla's", e);
            return vanilla;
        }
    }

    @Override
    public PinMap polycraft$pins() {
        return polycraft$state.pins();
    }

    @Override
    public void polycraft$pinsChanged() {
        polycraft$state.invalidate();
        ((AbstractFurnaceBlockEntity) (Object) this).setChanged();
    }

    @Override
    public FurnaceState polycraft$state() {
        return polycraft$state;
    }

    @Override
    public RecipeType<?> polycraft$recipeType() {
        return polycraft$recipeType;
    }

    @Override
    public int polycraft$cookingTimer() {
        return cookingTimer;
    }

    @Override
    public int polycraft$cookingTotalTime() {
        return cookingTotalTime;
    }

    @Override
    public void polycraft$setCooking(int timer, int totalTime) {
        this.cookingTimer = timer;
        this.cookingTotalTime = totalTime;
    }
}
