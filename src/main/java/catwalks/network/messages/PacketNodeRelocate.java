package catwalks.network.messages;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

import catwalks.network.NetworkHandler;
import catwalks.node.EntityNodeBase;
import io.netty.buffer.ByteBuf;

public class PacketNodeRelocate implements IMessage {
	
	int id;
	Vec3d pos;
	
    public PacketNodeRelocate() { }

    public PacketNodeRelocate(int id, Vec3d pos) {
    	this.id = id;
    	this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	id = buf.readInt();
    	pos = new Vec3d(buf.readFloat(),buf.readFloat(),buf.readFloat());
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(id);
    	buf.writeFloat((float) pos.xCoord);
    	buf.writeFloat((float) pos.yCoord);
    	buf.writeFloat((float) pos.zCoord);
    }

    public static class Handler implements IMessageHandler<PacketNodeRelocate, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketNodeRelocate message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("NodeRelocate");
            	Entity plainentity = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);
            	if(!( plainentity instanceof EntityNodeBase ))
            		return;
            	EntityNodeBase entity = (EntityNodeBase) plainentity;
            	
            	entity.setPosition(message.pos.xCoord, message.pos.yCoord, message.pos.zCoord);
            	entity.sendNodeUpdate();
            });
            return null;
        }
    }
}
