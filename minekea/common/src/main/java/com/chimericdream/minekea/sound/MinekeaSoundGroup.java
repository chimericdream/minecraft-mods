package com.chimericdream.minekea.sound;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class MinekeaSoundGroup {
    public static final SoundType EGG_CRATE_SOUND_GROUP;
    public static final SoundType GLASS_JAR_SOUND_GROUP;

    // BlockSoundGroup(
    //     float volume,
    //     float pitch,
    //     SoundEvent breakSound,
    //     SoundEvent stepSound,
    //     SoundEvent placeSound,
    //     SoundEvent hitSound,
    //     SoundEvent fallSound
    // )

    static {
        EGG_CRATE_SOUND_GROUP = new SoundType(
            1.0f,
            1.0f,
            SoundEvents.TURTLE_EGG_BREAK,
            SoundEvents.TURTLE_EGG_CRACK,
            SoundEvents.ANCIENT_DEBRIS_PLACE,
            SoundEvents.TURTLE_EGG_CRACK,
            SoundEvents.TURTLE_EGG_CRACK
        );

        GLASS_JAR_SOUND_GROUP = new SoundType(
            0.9f,
            1.15f,
            SoundEvents.DECORATED_POT_BREAK,
            SoundEvents.GLASS_STEP,
            SoundEvents.DECORATED_POT_PLACE,
            SoundEvents.GLASS_HIT,
            SoundEvents.GLASS_FALL
        );
    }
}
