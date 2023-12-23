package com.valaphee.sb;

import lombok.Getter;
import lombok.Setter;
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

    // Unserialized state
    private boolean unloaded = false;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        powered = compound.getBoolean("powered");
        offsetX = compound.getDouble("offsetX");
        offsetY = compound.getDouble("offsetY");
        offsetZ = compound.getDouble("offsetZ");
        loopDelay = compound.getInteger("loopDelay");

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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setBoolean("powered", powered);
        compound.setDouble("offsetX", offsetX);
        compound.setDouble("offsetY", offsetY);
        compound.setDouble("offsetZ", offsetZ);
        compound.setInteger("loopDelay", loopDelay);

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
        return new SPacketUpdateTileEntity(pos, 1293, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        boolean wasPowered = powered;

        super.onDataPacket(net, pkt);
        readFromNBT(pkt.getNbtCompound());

        if (powered) {
            if (!wasPowered) {
                Main.instance.playIntro(this);
            }
        } else if (wasPowered) {
            Main.instance.playOutro(this);
        }
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            return;
        }

        Main.instance.continueLoop(this);
    }

    @Override
    public void onChunkUnload() {
        unloaded = true;
    }

    public void fromBytes(PacketBuffer packetBuffer) {
        powered = packetBuffer.readBoolean();
        offsetX = packetBuffer.readDouble();
        offsetY = packetBuffer.readDouble();
        offsetZ = packetBuffer.readDouble();
        loopDelay = packetBuffer.readInt();
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
        packetBuffer.writeDouble(offsetX);
        packetBuffer.writeDouble(offsetY);
        packetBuffer.writeDouble(offsetZ);
        packetBuffer.writeInt(loopDelay);
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
        private float volume = 1.0f;
        private float pitch = 1.0f;
        private boolean stopOnEnter = true;
        private boolean stopOnExit = true;

        public void readFromNbt(NBTTagCompound compound) {
            id = compound.getString("id");
            offsetX = compound.getDouble("offsetX");
            offsetY = compound.getDouble("offsetY");
            offsetZ = compound.getDouble("offsetZ");
            volume = compound.getFloat("volume");
            pitch = compound.getFloat("pitch");
            stopOnEnter = compound.getBoolean("stopOnEnter");
            stopOnExit = compound.getBoolean("stopOnExit");
        }

        public void writeToNbt(NBTTagCompound compound) {
            compound.setString("id", id);
            compound.setDouble("offsetZ", offsetX);
            compound.setDouble("offsetY", offsetX);
            compound.setDouble("offsetX", offsetX);
            compound.setFloat("volume", volume);
            compound.setFloat("pitch", pitch);
            compound.setBoolean("stopOnEnter", stopOnEnter);
            compound.setBoolean("stopOnExit", stopOnExit);
        }

        public void fromBytes(PacketBuffer packetBuffer) {
            id = packetBuffer.readString(256);
            offsetX = packetBuffer.readDouble();
            offsetY = packetBuffer.readDouble();
            offsetZ = packetBuffer.readDouble();
            volume = packetBuffer.readFloat();
            pitch = packetBuffer.readFloat();
            stopOnEnter = packetBuffer.readBoolean();
            stopOnExit = packetBuffer.readBoolean();
        }

        public void toBytes(PacketBuffer packetBuffer) {
            packetBuffer.writeString(id);
            packetBuffer.writeDouble(offsetX);
            packetBuffer.writeDouble(offsetY);
            packetBuffer.writeDouble(offsetZ);
            packetBuffer.writeFloat(volume);
            packetBuffer.writeFloat(pitch);
            packetBuffer.writeBoolean(stopOnEnter);
            packetBuffer.writeBoolean(stopOnExit);
        }
    }
}
