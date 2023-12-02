package com.valaphee.sb;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SoundBlockData extends TileEntity {
    @Setter
    private double offsetX = 0.0;
    @Setter
    private double offsetY = 0.0;
    @Setter
    private double offsetZ = 0.0;
    @Setter
    private int loopDelay = 0;
    private final List<Sound> intro = new ArrayList<>();
    private final List<Sound> loop = new ArrayList<>();
    private final List<Sound> outro = new ArrayList<>();

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        offsetX = compound.getDouble("ofx");
        offsetY = compound.getDouble("ofy");
        offsetZ = compound.getDouble("ofz");
        loopDelay = compound.getInteger("lopDelay");

        intro.clear();
        NBTTagList introListTag = compound.getTagList("itr", 10);
        for (NBTBase introTag : introListTag) {
            NBTTagCompound introCompoundTag = (NBTTagCompound) introTag;
            Sound sound = new Sound();
            sound.readFromNbt(introCompoundTag);
            intro.add(sound);
        }


        loop.clear();
        NBTTagList loopListTag = compound.getTagList("lop", 10);
        for (NBTBase loopTag : loopListTag) {
            NBTTagCompound loopCompoundTag = (NBTTagCompound) loopTag;
            Sound sound = new Sound();
            sound.readFromNbt(loopCompoundTag);
            loop.add(sound);
        }

        outro.clear();
        NBTTagList outroListTag = compound.getTagList("otr", 10);
        for (NBTBase outroTag : outroListTag) {
            NBTTagCompound outroCompoundTag = (NBTTagCompound) outroTag;
            Sound sound = new Sound();
            sound.readFromNbt(outroCompoundTag);
            outro.add(sound);
        }

        Main.logger.info(this);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setDouble("ofx", offsetX);
        compound.setDouble("ofy", offsetY);
        compound.setDouble("ofz", offsetZ);
        compound.setInteger("lopDelay", loopDelay);

        NBTTagList introListTag = new NBTTagList();
        for (Sound sound : intro) {
            NBTTagCompound introCompoundTag = new NBTTagCompound();
            sound.writeToNbt(introCompoundTag);
            introListTag.appendTag(introCompoundTag);
        }
        compound.setTag("itr", introListTag);


        NBTTagList loopListTag = new NBTTagList();
        for (Sound sound : loop) {
            NBTTagCompound loopCompoundTag = new NBTTagCompound();
            sound.writeToNbt(loopCompoundTag);
            loopListTag.appendTag(loopCompoundTag);
        }
        compound.setTag("lop", introListTag);


        NBTTagList outroListTag = new NBTTagList();
        for (Sound sound : intro) {
            NBTTagCompound outroCompoundTag = new NBTTagCompound();
            sound.writeToNbt(outroCompoundTag);
            outroListTag.appendTag(outroCompoundTag);
        }
        compound.setTag("otr", outroListTag);

        return compound;
    }

    @Override
    public String toString() {
        return "SoundBlockData{" +
                "offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", offsetZ=" + offsetZ +
                ", loopDelay=" + loopDelay +
                ", intro=" + intro +
                ", loop=" + loop +
                ", outro=" + outro +
                '}';
    }

    @Getter
    @Setter
    public static class Sound {
        private String id = "";
        private double offsetX = 0.0;
        private double offsetY = 0.0;
        private double offsetZ = 0.0;
        private float volume = 1.0f;
        private float pitch = 1.0f;

        public void readFromNbt(NBTTagCompound compound) {
            id = compound.getString("id");
            offsetX = compound.getDouble("ofx");
            offsetY = compound.getDouble("ofy");
            offsetZ = compound.getDouble("ofz");
            volume = compound.getFloat("vol");
            pitch = compound.getFloat("pit");
        }

        public void writeToNbt(NBTTagCompound compound) {
            compound.setDouble("ofx", offsetX);
            compound.setDouble("ofx", offsetX);
            compound.setDouble("ofx", offsetX);
            compound.setFloat("vol", volume);
            compound.setFloat("pit", pitch);
        }

        @Override
        public String toString() {
            return "Sound{" +
                    "id='" + id + '\'' +
                    ", offsetX=" + offsetX +
                    ", offsetY=" + offsetY +
                    ", offsetZ=" + offsetZ +
                    ", volume=" + volume +
                    ", pitch=" + pitch +
                    '}';
        }
    }
}
