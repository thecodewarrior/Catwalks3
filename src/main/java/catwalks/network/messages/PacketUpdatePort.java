package catwalks.network.messages;

import catwalks.CatwalksMod;
import catwalks.network.NetworkHandler;
import catwalks.node.EntityNodeBase;
import catwalks.util.Logs;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdatePort implements IMessage {
    
    protected int entityID, index;
    protected boolean isoutput;
    protected ByteBuf buf;
    
    public PacketUpdatePort() { }

    public PacketUpdatePort(EntityNodeBase entity, boolean isoutput, int index, ByteBuf buf) {
    	entityID = entity.getEntityId();
        this.index = index;
        this.buf = buf;
        this.isoutput = isoutput;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        index = buf.readInt();
        isoutput = buf.readBoolean();
        
        this.buf = NetworkHandler.createBuffer();
        
        int length = buf.readInt();
        buf.readBytes(this.buf, 0, length);
        this.buf.writerIndex(length);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(entityID);
    	buf.writeInt(index);
    	buf.writeBoolean(isoutput);
    	buf.writeInt(this.buf.readableBytes());
    	buf.writeBytes(this.buf);
    }

    public static class Handler implements IMessageHandler<PacketUpdatePort, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketUpdatePort message, MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling(false, "UpdatePort (%s)", message.isoutput ? "out" : "in");
            	Entity plainentity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
            	if(!( plainentity instanceof EntityNodeBase ))
            		return;
            	EntityNodeBase entity = (EntityNodeBase) plainentity;
            	if(message.isoutput)
            		entity.getNode().outputs().get(message.index).readFromBuf(message.buf);
            	if(!message.isoutput)
            		entity.getNode().inputs().get(message.index).readFromBuf(message.buf);
            });
            return null;
        }
    }
}
