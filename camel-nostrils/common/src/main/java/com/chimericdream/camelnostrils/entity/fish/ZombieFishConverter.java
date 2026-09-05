package com.chimericdream.camelnostrils.entity.fish;

import com.chimericdream.camelnostrils.entity.ModEntities;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.fish.Cod;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.animal.fish.TropicalFish;

/**
 * Converts a leashed salmon/cod/tropical fish that just died of drowning-out-of-water into its hostile
 * zombie counterpart, instead of letting it actually die. Triggered from
 * {@link com.chimericdream.camelnostrils.mixin.CN$LivingEntityMixin}.
 * <p>
 * Reuses vanilla's own {@link Mob#convertTo} (the same machinery Zombie uses to become a Drowned), which
 * copies position/rotation/velocity and — importantly — re-leashes the new mob to whatever was holding
 * the old one, so the player ends up holding a leash attached to a hostile fish.
 */
public final class ZombieFishConverter {
    private ZombieFishConverter() {
    }

    /**
     * @return {@code true} if {@code dyingFish} was one of the three convertible species and got
     * replaced with its zombie counterpart; {@code false} if it wasn't (e.g. a pufferfish, or already a
     * zombie fish) and should just die normally.
     */
    public static boolean convert(LivingEntity dyingFish) {
        if (!(dyingFish instanceof Mob fish)) {
            return false;
        }

        // Exact class match (not instanceof) so an already-converted zombie fish doesn't re-trigger this.
        if (dyingFish.getClass() == Salmon.class) {
            return fish.convertTo(ModEntities.ZOMBIE_SALMON.get(), ConversionParams.single(fish, false, false), zombie -> {
            }) != null;
        } else if (dyingFish.getClass() == Cod.class) {
            return fish.convertTo(ModEntities.ZOMBIE_COD.get(), ConversionParams.single(fish, false, false), zombie -> {
            }) != null;
        } else if (dyingFish.getClass() == TropicalFish.class) {
            return fish.convertTo(ModEntities.ZOMBIE_TROPICAL_FISH.get(), ConversionParams.single(fish, false, false), zombie -> {
            }) != null;
        }

        return false;
    }
}
