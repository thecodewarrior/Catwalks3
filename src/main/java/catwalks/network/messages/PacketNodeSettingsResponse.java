package catwalks.network.messages;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;

import catwalks.network.NetworkHandler;
import catwalks.node.EntityNodeBase;
import io.netty.buffer.ByteBuf;

public class PacketNodeSettingsResponse implements IMessage {
	
	protected int id;
	protected NBTTagCompound tag;
	
    public PacketNodeSettingsResponse() { }

    public PacketNodeSettingsResponse(int id, NBTTagCompound tag) {
    	this.id = id;
    	this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	id = buf.readInt();
    	tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(id);
    	ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler implements IMessageHandler<PacketNodeSettingsResponse, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketNodeSettingsResponse message, MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("NodeSettingsResponse");
            	Entity plainentity = Minecraft.getMinecraft().theWorld.getEntityByID(message.id);
            	if(!( plainentity instanceof EntityNodeBase ))
            		return;
            	EntityNodeBase entity = (EntityNodeBase) plainentity;
            	entity.getNode().openGUI(message.tag);
            });
            return null;
        }
    }
}
