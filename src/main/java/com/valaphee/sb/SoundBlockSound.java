package com.valaphee.sb;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class SoundBlockSound extends MovingSound {
    private final IBlockAccess blockAccess;
    private final BlockPos blockPos;

    protected SoundBlockSound(IBlockAccess blockAccess, BlockPos blockPos, double offsetX, double offsetY, double offsetZ, SoundBlockData.Sound sound, boolean repeat) {
        super(new SoundEvent(new ResourceLocation(sound.getId())), SoundCategory.AMBIENT);
        this.blockAccess = blockAccess;
        this.blockPos = blockPos;
        this.xPosF = (float) ((double) blockPos.getX() + offsetX + sound.getOffsetX());
        this.yPosF = (float) ((double) blockPos.getY() + offsetY + sound.getOffsetY());
        this.zPosF = (float) ((double) blockPos.getZ() + offsetZ + sound.getOffsetZ());
        this.volume = sound.getVolume();
        this.pitch = sound.getPitch();
        this.repeat = repeat;
    }

    @Override
    public void update() {
        this.donePlaying = repeat && !blockAccess.getBlockState(blockPos).getValue(SoundBlock.POWERED_PROPERTY);
    }
}
