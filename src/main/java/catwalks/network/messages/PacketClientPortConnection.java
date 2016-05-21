package catwalks.network.messages;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.Vec3d;

import catwalks.network.NetworkHandler;
import catwalks.node.EntityNodeBase;
import io.netty.buffer.ByteBuf;

public class PacketClientPortConnection implements IMessage {
	
	protected int id, index;
	protected List<Vec3d> points;
	
    public PacketClientPortConnection() { }

    public PacketClientPortConnection(int id, int index, List<Vec3d> otherPoints) {
    	this.id = id;
    	this.index = index;
    	this.points = otherPoints;
    	
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	id = buf.readInt();
    	index = buf.readInt();
    	int length = buf.readInt();
    	points = new ArrayList<>();
    	for (int i = 0; i < length; i++) {
			points.add(new Vec3d(buf.readFloat(),buf.readFloat(),buf.readFloat()));
		}
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(id);
    	buf.writeInt(index);
    	buf.writeInt(points.size());
    	for (Vec3d point : points) {
    		buf.writeFloat((float) point.xCoord);
        	buf.writeFloat((float) point.yCoord);
        	buf.writeFloat((float) point.zCoord);
		}
    }

    public static class Handler implements IMessageHandler<PacketClientPortConnection, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketClientPortConnection message, MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("ClientPortConnection");
            	Entity plainentity = Minecraft.getMinecraft().theWorld.getEntityByID(message.id);
            	if(!( plainentity instanceof EntityNodeBase ))
            		return;
            	EntityNodeBase entity = (EntityNodeBase) plainentity;
            	entity.getNode().outputs().get(message.index).connectedLocs = message.points;
            });
            return null;
        }
    }
}
