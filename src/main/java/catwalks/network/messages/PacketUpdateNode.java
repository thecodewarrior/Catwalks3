package catwalks.network.messages;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.util.IThreadListener;

import catwalks.network.NetworkHandler;
import catwalks.node.EntityNodeBase;
import io.netty.buffer.ByteBuf;

public class PacketUpdateNode implements IMessage {
    
    protected int entityID, destroyTimer;
    protected float rotationPitch, rotationYaw;
    protected long posX, posY, posZ;
    
    public PacketUpdateNode() { }

    public PacketUpdateNode(EntityNodeBase entity) {
    	entityID = entity.getEntityId();
        posX = EntityTracker.getPositionLong(entity.posX);
        posY = EntityTracker.getPositionLong(entity.posY);
        posZ = EntityTracker.getPositionLong(entity.posZ);
        rotationPitch = entity.rotationPitch;
        rotationYaw = entity.rotationYaw;
        destroyTimer = entity.destroyTimer;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        posX = buf.readLong();
        posY = buf.readLong();
        posZ = buf.readLong();
        rotationPitch = buf.readFloat();
        rotationYaw = buf.readFloat();
        destroyTimer = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(entityID);
    	buf.writeLong(posX);
    	buf.writeLong(posY);
    	buf.writeLong(posZ);
    	buf.writeFloat(rotationPitch);
    	buf.writeFloat(rotationYaw);
    	buf.writeInt(destroyTimer);
    }

    public static class Handler implements IMessageHandler<PacketUpdateNode, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketUpdateNode message, MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("UpdateNode");
            	Entity plainentity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
            	if(!( plainentity instanceof EntityNodeBase ))
            		return;
            	EntityNodeBase entity = (EntityNodeBase) plainentity;
            	entity.setPosition(message.posX/4096.0, message.posY/4096.0, message.posZ/4096.0);
            	entity.rotationPitch = message.rotationPitch;
            	entity.rotationYaw = message.rotationYaw;
            	entity.destroyTimer = message.destroyTimer;
            });
            return null;
        }
    }
}
