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

        this.xPosF = (float) ((double) blockData.getPos().getX() + blockData.getOffsetX() + sound.getOffsetX());
        this.yPosF = (float) ((double) blockData.getPos().getY() + blockData.getOffsetY() + sound.getOffsetY());
        this.zPosF = (float) ((double) blockData.getPos().getZ() + blockData.getOffsetZ() + sound.getOffsetZ());
        this.volume = blockData.getDistance();
        this.pitch = sound.getPitch();
    }

    @Override
    public void update() {
        if (repeat) {
            donePlaying = !blockData.isPowered() || blockData.isUnloaded() || blockData.isInvalid();
        }

        this.xPosF = (float) ((double) blockData.getPos().getX() + blockData.getOffsetX() + sound.getOffsetX());
        this.yPosF = (float) ((double) blockData.getPos().getY() + blockData.getOffsetY() + sound.getOffsetY());
        this.zPosF = (float) ((double) blockData.getPos().getZ() + blockData.getOffsetZ() + sound.getOffsetZ());
        this.volume = sound.getVolume();
        this.pitch = sound.getPitch();
    }
}
