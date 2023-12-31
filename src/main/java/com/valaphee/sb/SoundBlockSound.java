package com.valaphee.sb;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundBlockSound extends MovingSound {
    private final SoundBlockData blockData;
    private final SoundBlockData.Sound sound;

    protected SoundBlockSound(SoundBlockData blockData, SoundBlockData.Sound sound, boolean looping) {
        super(new SoundEvent(new ResourceLocation(sound.getId())), SoundCategory.BLOCKS);
        this.blockData = blockData;
        this.sound = sound;
        this.repeat = looping;

        // Since we're achieving distance via initially high volumes, we need to manually
        // prevent "accidental jump scares" on the first sound tick. Your heart (and ears)
        // will thank me later!
        this.xPosF = 0.0f;
        this.yPosF = Float.POSITIVE_INFINITY;
        this.zPosF = 0.0f;

        this.volume = blockData.getDistance() * sound.getDistance();
        this.pitch = blockData.getPitch() * sound.getPitch();
    }

    @Override
    public void update() {
        if (repeat) {
            donePlaying = !blockData.isPowered() || blockData.isUnloaded() || blockData.isInvalid();
        }

        this.xPosF = (float) ((double) blockData.getPos().getX() + blockData.getOffsetX() + sound.getOffsetX());
        this.yPosF = (float) ((double) blockData.getPos().getY() + blockData.getOffsetY() + sound.getOffsetY());
        this.zPosF = (float) ((double) blockData.getPos().getZ() + blockData.getOffsetZ() + sound.getOffsetZ());
        this.volume = blockData.getVolume() * sound.getVolume();
        this.pitch = blockData.getPitch() * sound.getPitch();
    }
}
