package com.chimericdream.logallthethings.mixin;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chimericdream.logallthethings.lavalog.LavaLogProperties;

/**
 * {@code StainedGlassPaneBlock}'s own constructor calls {@code registerDefaultState} a second time,
 * <em>after</em> {@link LATT$IronBarsBlockMixin}'s {@code <init>} TAIL inject already ran during the
 * {@code super(properties)} call - and it builds that second default from
 * {@code this.stateDefinition.any()} rather than {@code this.defaultBlockState()}, so it doesn't carry
 * forward our {@code LAVALOGGED = false} fix-up. Every property {@code any()} doesn't already know to
 * set explicitly (ours included) falls back to the first entry in that property's value list -
 * {@code BooleanProperty}'s is {@code [true, false]} - so every stained-glass-pane color's registered
 * default silently comes back {@code lavalogged=true}, even though plain {@code glass_pane}/
 * {@code iron_bars} (a bare {@code IronBarsBlock}, with only the one {@code registerDefaultState} call)
 * are unaffected. Re-applying the same fix-up here, after this constructor's own re-registration,
 * restores the correct default for every color.
 */
@Mixin(StainedGlassPaneBlock.class)
public abstract class LATT$StainedGlassPaneBlockMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void latt$restoreDefaultLavaLogged(DyeColor color, Properties properties, CallbackInfo ci) {
        Block self = (Block) (Object) this;
        self.registerDefaultState(self.defaultBlockState().setValue(LavaLogProperties.LAVALOGGED, false));
    }
}
