package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
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

        if (state.isOf(Blocks.SHULKER_BOX)) {
            ss$checkAndUpdateRGB(world, blockPos);
        }

        ss$hasProcessed = true;
    }

    @Unique
    private void ss$checkAndUpdateRGB(ClientWorld world, BlockPos pos) {
        ShulkerBoxBlockEntity entity = (ShulkerBoxBlockEntity) world.getBlockEntity(pos);
        if (entity == null && world.getBlockState(pos).isAir()) {
            entity = (ShulkerBoxBlockEntity) world.getBlockEntity(pos.down());
        }

        if (entity == null) {
            return;
        }

        ShulkerStuffDyedColorComponent ssDyedColorComponent = entity.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get());
        if (ssDyedColorComponent != null) {
            Sprite sprite1 = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(Blocks.WHITE_SHULKER_BOX.getDefaultState());
            this.setSprite(sprite1);

            int rgb = ssDyedColorComponent.lidColor();

            this.red = (float) (rgb >> 16 & 255) / 255.0F;
            this.green = (float) (rgb >> 8 & 255) / 255.0F;
            this.blue = (float) (rgb & 255) / 255.0F;
        }
    }
}
