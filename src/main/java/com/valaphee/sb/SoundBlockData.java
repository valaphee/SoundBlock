package com.valaphee.sb;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SoundBlockData extends TileEntity {
    @Setter
    private boolean powered = false;
    @Setter
    private boolean alwaysPowered = false;
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

    private float pitch = 1.0f;
    private float volume = 1.0f;
    private float distance = 1.0f;

    // Reference only
    private boolean loading = false;
    private boolean unloaded = false;

    public void setPitch(float pitch) {
        this.pitch = Math.max(pitch, 0.0f);
    }

    public void setVolume(float volume) {
        this.volume = Math.max(volume, 0.0f);
    }

    public void setDistance(float distance) {
        this.distance = Math.max(distance, 0.0f);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        powered = compound.getBoolean("powered");
        alwaysPowered = compound.getBoolean("alwaysPowered");
        offsetX = compound.getDouble("offsetX");
        offsetY = compound.getDouble("offsetY");
        offsetZ = compound.getDouble("offsetZ");
        loopDelay = compound.getInteger("loopDelay");
        pitch = compound.getFloat("pitch");
        volume = compound.getFloat("volume");
        distance = compound.getFloat("distance");

        intro.clear();
        NBTTagList introListTag = compound.getTagList("intro", 10);
        for (NBTBase introTag : introListTag) {
            NBTTagCompound introCompoundTag = (NBTTagCompound) introTag;
            Sound sound = new Sound();
            sound.readFromNbt(introCompoundTag);
            intro.add(sound);
        }

        loop.clear();
        NBTTagList loopListTag = compound.getTagList("loop", 10);
        for (NBTBase loopTag : loopListTag) {
            NBTTagCompound loopCompoundTag = (NBTTagCompound) loopTag;
            Sound sound = new Sound();
            sound.readFromNbt(loopCompoundTag);
            loop.add(sound);
        }

        outro.clear();
        NBTTagList outroListTag = compound.getTagList("outro", 10);
        for (NBTBase outroTag : outroListTag) {
            NBTTagCompound outroCompoundTag = (NBTTagCompound) outroTag;
            Sound sound = new Sound();
            sound.readFromNbt(outroCompoundTag);
            outro.add(sound);
        }

        if (loading) {
            loading = false;
            Main.instance.continueLoop(this);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setBoolean("powered", powered);
        compound.setBoolean("alwaysPowered", alwaysPowered);
        compound.setDouble("offsetX", offsetX);
        compound.setDouble("offsetY", offsetY);
        compound.setDouble("offsetZ", offsetZ);
        compound.setInteger("loopDelay", loopDelay);
        compound.setFloat("pitch", pitch);
        compound.setFloat("volume", volume);
        compound.setFloat("distance", distance);

        NBTTagList introListTag = new NBTTagList();
        for (Sound sound : intro) {
            NBTTagCompound introCompoundTag = new NBTTagCompound();
            sound.writeToNbt(introCompoundTag);
            introListTag.appendTag(introCompoundTag);
        }
        compound.setTag("intro", introListTag);

        NBTTagList loopListTag = new NBTTagList();
        for (Sound sound : loop) {
            NBTTagCompound loopCompoundTag = new NBTTagCompound();
            sound.writeToNbt(loopCompoundTag);
            loopListTag.appendTag(loopCompoundTag);
        }
        compound.setTag("loop", loopListTag);

        NBTTagList outroListTag = new NBTTagList();
        for (Sound sound : outro) {
            NBTTagCompound outroCompoundTag = new NBTTagCompound();
            sound.writeToNbt(outroCompoundTag);
            outroListTag.appendTag(outroCompoundTag);
        }
        compound.setTag("outro", outroListTag);

        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, SoundBlock.BLOCK.getRegistryName().hashCode(), getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            return;
        }

        loading = true;
    }

    @Override
    public void onChunkUnload() {
        unloaded = true;
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 0) {
            switch (type) {
                case 0: // Play intro and schedule loop
                    powered = true;
                    Main.instance.playIntro(this);
                    break;
                case 1: // Play outro and stop loop
                    powered = false;
                    Main.instance.playOutro(this);
                    break;
            }
        }

        return true;
    }

    public void fromBytes(PacketBuffer packetBuffer) {
        powered = packetBuffer.readBoolean();
        alwaysPowered = packetBuffer.readBoolean();
        offsetX = packetBuffer.readDouble();
        offsetY = packetBuffer.readDouble();
        offsetZ = packetBuffer.readDouble();
        loopDelay = packetBuffer.readInt();
        pitch = packetBuffer.readFloat();
        volume = packetBuffer.readFloat();
        distance = packetBuffer.readFloat();

        intro.clear();
        int introCount = packetBuffer.readVarInt();
        for (int i = 0; i < introCount; i++) {
            Sound sound = new Sound();
            sound.fromBytes(packetBuffer);
            intro.add(sound);
        }

        loop.clear();
        int loopCount = packetBuffer.readVarInt();
        for (int i = 0; i < loopCount; i++) {
            Sound sound = new Sound();
            sound.fromBytes(packetBuffer);
            loop.add(sound);
        }

        outro.clear();
        int outroCount = packetBuffer.readVarInt();
        for (int i = 0; i < outroCount; i++) {
            Sound sound = new Sound();
            sound.fromBytes(packetBuffer);
            outro.add(sound);
        }
    }

    public void toBytes(PacketBuffer packetBuffer) {
        packetBuffer.writeBoolean(powered);
        packetBuffer.writeBoolean(alwaysPowered);
        packetBuffer.writeDouble(offsetX);
        packetBuffer.writeDouble(offsetY);
        packetBuffer.writeDouble(offsetZ);
        packetBuffer.writeInt(loopDelay);
        packetBuffer.writeFloat(pitch);
        packetBuffer.writeFloat(volume);
        packetBuffer.writeFloat(distance);

        packetBuffer.writeVarInt(intro.size());
        for (Sound sound : intro) {
            sound.toBytes(packetBuffer);
        }

        packetBuffer.writeVarInt(loop.size());
        for (Sound sound : loop) {
            sound.toBytes(packetBuffer);
        }

        packetBuffer.writeVarInt(outro.size());
        for (Sound sound : outro) {
            sound.toBytes(packetBuffer);
        }
    }

    @Getter
    @Setter
    public static class Sound {
        private String id = "";
        private double offsetX = 0.0;
        private double offsetY = 0.0;
        private double offsetZ = 0.0;
        private float pitch = 1.0f;
        private float volume = 1.0f;
        private float distance = 1.0f;
        private boolean stopOnEnter = true;
        private boolean stopOnExit = true;

        public void setPitch(float pitch) {
            this.pitch = Math.max(pitch, 0.0f);
        }

        public void setVolume(float volume) {
            this.volume = Math.max(volume, 0.0f);
        }

        public void setDistance(float distance) {
            this.distance = Math.max(distance, 0.0f);
        }

        public void readFromNbt(NBTTagCompound compound) {
            id = compound.getString("id");
            offsetX = compound.getDouble("offsetX");
            offsetY = compound.getDouble("offsetY");
            offsetZ = compound.getDouble("offsetZ");
            pitch = compound.getFloat("pitch");
            volume = compound.getFloat("volume");
            distance = compound.getFloat("distance");
            stopOnEnter = compound.getBoolean("stopOnEnter");
            stopOnExit = compound.getBoolean("stopOnExit");
        }

        public void writeToNbt(NBTTagCompound compound) {
            compound.setString("id", id);
            compound.setDouble("offsetX", offsetX);
            compound.setDouble("offsetY", offsetY);
            compound.setDouble("offsetZ", offsetZ);
            compound.setFloat("pitch", pitch);
            compound.setFloat("volume", volume);
            compound.setFloat("distance", distance);
            compound.setBoolean("stopOnEnter", stopOnEnter);
            compound.setBoolean("stopOnExit", stopOnExit);
        }

        public void fromBytes(PacketBuffer packetBuffer) {
            id = packetBuffer.readString(256);
            offsetX = packetBuffer.readDouble();
            offsetY = packetBuffer.readDouble();
            offsetZ = packetBuffer.readDouble();
            pitch = packetBuffer.readFloat();
            volume = packetBuffer.readFloat();
            distance = packetBuffer.readFloat();
            stopOnEnter = packetBuffer.readBoolean();
            stopOnExit = packetBuffer.readBoolean();
        }

        public void toBytes(PacketBuffer packetBuffer) {
            packetBuffer.writeString(id);
            packetBuffer.writeDouble(offsetX);
            packetBuffer.writeDouble(offsetY);
            packetBuffer.writeDouble(offsetZ);
            packetBuffer.writeFloat(pitch);
            packetBuffer.writeFloat(volume);
            packetBuffer.writeFloat(distance);
            packetBuffer.writeBoolean(stopOnEnter);
            packetBuffer.writeBoolean(stopOnExit);
        }
    }
}
