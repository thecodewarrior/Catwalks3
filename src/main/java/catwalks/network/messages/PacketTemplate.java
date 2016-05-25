package catwalks.network.messages;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;

import catwalks.network.NetworkHandler;
import io.netty.buffer.ByteBuf;

public class PacketTemplate implements IMessage {
	
    public PacketTemplate() { }

    public PacketTemplate(int id, int hit) {}

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketTemplate, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketTemplate message, MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("---INSERT PACKET NAME---");
//            	Entity plainentity = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.id);
//            	if(!( plainentity instanceof EntityNodeBase ))
//            		return;
//            	EntityNodeBase entity = (EntityNodeBase) plainentity;
            });
            return null;
        }
    }
}
