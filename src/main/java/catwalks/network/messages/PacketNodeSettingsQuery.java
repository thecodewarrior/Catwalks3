package catwalks.network.messages;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;

import catwalks.network.NetworkHandler;
import catwalks.node.EntityNodeBase;
import io.netty.buffer.ByteBuf;

public class PacketNodeSettingsQuery implements IMessage {
	
	protected int id;
	
    public PacketNodeSettingsQuery() { }

    public PacketNodeSettingsQuery(int id) {
    	this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(id);
    }

    public static class Handler implements IMessageHandler<PacketNodeSettingsQuery, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketNodeSettingsQuery message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("NodeSettingsQuery");
            	Entity plainentity = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);
            	if(!( plainentity instanceof EntityNodeBase ))
            		return;
            	EntityNodeBase entity = (EntityNodeBase) plainentity;
            	NetworkHandler.network.sendTo(new PacketNodeSettingsResponse(message.id, entity.getNode().getSettings()), ctx.getServerHandler().playerEntity);
            });
            return null;
        }
    }
}
