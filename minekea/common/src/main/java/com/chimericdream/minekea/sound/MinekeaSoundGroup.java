package com.chimericdream.minekea.sound;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public class MinekeaSoundGroup {
    public static final BlockSoundGroup EGG_CRATE_SOUND_GROUP;

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
            1.0F,
            1.0F,
            SoundEvents.ENTITY_TURTLE_EGG_BREAK,
            SoundEvents.ENTITY_TURTLE_EGG_CRACK,
            SoundEvents.BLOCK_ANCIENT_DEBRIS_PLACE,
            SoundEvents.ENTITY_TURTLE_EGG_CRACK,
            SoundEvents.ENTITY_TURTLE_EGG_CRACK
        );
    }
}
