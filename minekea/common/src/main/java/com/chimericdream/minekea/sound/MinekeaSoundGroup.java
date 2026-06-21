package com.chimericdream.minekea.sound;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public class MinekeaSoundGroup {
    public static final BlockSoundGroup EGG_CRATE_SOUND_GROUP;
    public static final BlockSoundGroup GLASS_JAR_SOUND_GROUP;

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
        EGG_CRATE_SOUND_GROUP = new BlockSoundGroup(
            1.0f,
            1.0f,
            SoundEvents.ENTITY_TURTLE_EGG_BREAK,
            SoundEvents.ENTITY_TURTLE_EGG_CRACK,
            SoundEvents.BLOCK_ANCIENT_DEBRIS_PLACE,
            SoundEvents.ENTITY_TURTLE_EGG_CRACK,
            SoundEvents.ENTITY_TURTLE_EGG_CRACK
        );

        GLASS_JAR_SOUND_GROUP = new BlockSoundGroup(
            0.9f,
            1.15f,
            SoundEvents.BLOCK_DECORATED_POT_BREAK,
            SoundEvents.BLOCK_GLASS_STEP,
            SoundEvents.BLOCK_DECORATED_POT_PLACE,
            SoundEvents.BLOCK_GLASS_HIT,
            SoundEvents.BLOCK_GLASS_FALL
        );
    }
}
