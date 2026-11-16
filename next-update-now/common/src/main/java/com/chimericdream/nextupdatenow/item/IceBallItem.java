package com.chimericdream.nextupdatenow.item;

import com.chimericdream.nextupdatenow.entity.IceBall;
import com.chimericdream.nextupdatenow.sound.ModSounds;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public class IceBallItem extends Item implements ProjectileItem {
    public static final float SHOOT_POWER = 1.5F;

    public IceBallItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            ModSounds.ICE_BALL_THROW.get(),
            SoundSource.NEUTRAL,
            0.5F,
            0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (level instanceof ServerLevel serverLevel) {
            Projectile.spawnProjectileFromRotation(IceBall::new, serverLevel, stack, player, 0.0F, SHOOT_POWER, 1.0F);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        return new IceBall(level, pos.x(), pos.y(), pos.z(), stack);
    }
}
