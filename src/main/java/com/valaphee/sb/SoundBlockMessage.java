package com.valaphee.sb;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
                BlockPos pos = message.data.getPos();
                if (!world.isBlockLoaded(pos)) {
                    return;
                }

                IBlockState state = world.getBlockState(pos);
                TileEntity oldTileEntity = world.getTileEntity(pos);
                if (!(oldTileEntity instanceof SoundBlockData)) {
                    return;
                }

                // The alwaysPowered field should override the powered field when it is set.
                // To achieve this, broadcast powered with respect to the alwaysPowered state.
                SoundBlockData oldData = (SoundBlockData) oldTileEntity;
                boolean wasAlwaysPowered = oldData.isAlwaysPowered();
                boolean alwaysPowered = message.data.isAlwaysPowered();
                if (alwaysPowered != wasAlwaysPowered) {
                    if (alwaysPowered) {
                        message.data.setPowered(true);
                        world.addBlockEvent(pos, state.getBlock(), 0, 0);
                    } else {
                        message.data.setPowered(false);
                        world.addBlockEvent(pos, state.getBlock(), 0, 1);
                    }
                }

                // Update tile entity and broadcast to all clients.
                world.setTileEntity(pos, message.data);
                message.data.markDirty();
                world.notifyBlockUpdate(pos, state, state, 3);
            });

            return null;
        }
    }
}
