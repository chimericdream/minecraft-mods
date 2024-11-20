package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.client.util.ShulkerBoxSprites;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDataComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockDustParticle.class)
abstract public class ShulkerStuff$BlockDustParticleMixin extends ShulkerStuff$SpriteBillboardParticleAccessor {
    @Unique
    private boolean ss$hasProcessed = false;

    @Inject(method = "<init>*", at = @At(value = "TAIL"))
    public void ss$BlockDustParticleMixin(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state, BlockPos blockPos, CallbackInfo ci) {
        if (ss$hasProcessed) {
            return;
        }

        if (state.isIn(BlockTags.SHULKER_BOXES)) {
            ss$checkAndUpdateRGB(world, blockPos);
        }

        ss$hasProcessed = true;
    }

    @Unique
    private void ss$checkAndUpdateRGB(ClientWorld world, BlockPos pos) {
        ShulkerBoxBlockEntity entity = (ShulkerBoxBlockEntity) world.getBlockEntity(pos);
        if (entity == null) {
            return;
        }

        ShulkerStuffDataComponent ssData = entity.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DATA.get());
        if (ssData != null && ssData.lidColor() != -1) {
            Sprite sprite1 = ShulkerBoxSprites.GRAYSCALE_SHULKER_BOX.getSprite();
            this.setSprite(sprite1);

            int rgb = ssData.lidColor();

            this.red = (float) (rgb >> 16 & 255) / 255.0F;
            this.green = (float) (rgb >> 8 & 255) / 255.0F;
            this.blue = (float) (rgb & 255) / 255.0F;
        }
    }
}
