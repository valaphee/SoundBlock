package com.valaphee.sb;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoundBlockMessage implements IMessage {
    private SoundBlockData data;

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        data = new SoundBlockData();
        data.setPos(packetBuffer.readBlockPos());
        data.fromBytes(packetBuffer);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeBlockPos(data.getPos());
        data.toBytes(packetBuffer);
    }

    public static class Handler implements IMessageHandler<SoundBlockMessage, IMessage> {
        @Override
        public IMessage onMessage(SoundBlockMessage message, MessageContext ctx) {
            WorldServer world = ctx.getServerHandler().player.getServerWorld();
            world.addScheduledTask(() -> {
                if (!world.isBlockLoaded(message.data.getPos())) {
                    return;
                }

                TileEntity tileEntity = world.getTileEntity(message.data.getPos());
                if (!(tileEntity instanceof SoundBlockData)) {
                    return;
                }

                world.setTileEntity(message.data.getPos(), message.data);
                message.data.markDirty();
            });

            return null;
        }
    }
}
