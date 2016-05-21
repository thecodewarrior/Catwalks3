package catwalks.network.messages;

import catwalks.network.NetworkHandler;
import catwalks.node.EntityNodeBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNodeConnect implements IMessage {
	
	protected int fromEID, toEID;
	protected int fromIndex, toIndex;
	
    public PacketNodeConnect() {}

    public PacketNodeConnect(int fromEID, int fromIndex, int toEID, int toIndex) {
    	this.fromEID = fromEID;
    	this.fromIndex = fromIndex;
    	this.toEID = toEID;
    	this.toIndex = toIndex;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	fromEID   = buf.readInt();
    	fromIndex = buf.readInt();
    	toEID     = buf.readInt();
    	toIndex   = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(fromEID);
    	buf.writeInt(fromIndex);
    	buf.writeInt(toEID);
    	buf.writeInt(toIndex);
    }

    public static class Handler implements IMessageHandler<PacketNodeConnect, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketNodeConnect message, MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("NodeConnect");
            	Entity plainFrom = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.fromEID);
            	Entity plainTo = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.toEID);
            	if(!( plainFrom instanceof EntityNodeBase && plainTo instanceof EntityNodeBase))
            		return;
            	EntityNodeBase from = (EntityNodeBase) plainFrom;
            	EntityNodeBase to = (EntityNodeBase) plainTo;
            	
            	from.getNode().outputs().get(message.fromIndex).connectTo(to, message.toIndex);
            	from.firePacket(new PacketClientPortConnection(message.fromEID, message.fromIndex, from.getNode().outputs().get(message.fromIndex).connectedPoints()));
            });
            return null;
        }
    }
}
