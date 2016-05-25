package catwalks.network.messages;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;

import catwalks.gui.CommandContainer;
import catwalks.network.NetworkHandler;
import io.netty.buffer.ByteBuf;
import mcjty.lib.network.AbstractServerCommand;
import mcjty.lib.network.Argument;
import mcjty.lib.network.NetworkTools;

public class PacketClientGuiCommand implements IMessage {
	
	protected String command;
    protected Map<String,Argument> args;
	
    public PacketClientGuiCommand() { }

    public PacketClientGuiCommand(String command, Argument... arguments) {
    	this.command = command;
        if (arguments == null) {
            this.args = null;
        } else {
            this.args = new HashMap<String, Argument>(arguments.length);
            for (Argument arg : arguments) {
                args.put(arg.getName(), arg);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        command = NetworkTools.readString(buf);
        args = AbstractServerCommand.readArguments(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	NetworkTools.writeString(buf, command);
    	AbstractServerCommand.writeArguments(buf, args);
    }

    public static class Handler implements IMessageHandler<PacketClientGuiCommand, IMessage> {
    	
        @Override
        public IMessage onMessage(PacketClientGuiCommand message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(() -> {
            	NetworkHandler.notifyPacketHandling("ClientGuiCommand");
            	if(Minecraft.getMinecraft().currentScreen instanceof CommandContainer) {
            		((CommandContainer)Minecraft.getMinecraft().currentScreen).execute(message.command, message.args);
            	}
            });
            return null;
        }
    }
}
